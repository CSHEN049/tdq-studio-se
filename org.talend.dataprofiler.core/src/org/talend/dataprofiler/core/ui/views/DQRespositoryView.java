// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.model.nodes.foldernode.AbstractFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ColumnFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.TableFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ViewFolderNode;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IService;
import org.talend.dataprofiler.core.service.IViewerFilterService;
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;
import org.talend.dataprofiler.core.ui.views.filters.AbstractViewerFilter;
import org.talend.dataprofiler.core.ui.views.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.views.filters.ReportingFilter;

/**
 * @author rli
 * 
 */
public class DQRespositoryView extends CommonNavigator {

    public static final String ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView";

    private Map<String, AbstractViewerFilter> filterMap = new HashMap<String, AbstractViewerFilter>();

    private static final String VIEW_CONTEXT_ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView.viewScope"; //$NON-NLS-1$

    public DQRespositoryView() {
        super();
        CorePlugin.getDefault().checkDQStructure();
    }

    public void init(IViewSite aSite, IMemento aMemento) throws PartInitException {
        super.init(aSite, aMemento);
        if (aMemento == null) {
            setLinkingEnabled(true);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        this.addViewerFilter(EMFObjFilter.FILTER_ID);
        this.addViewerFilter(ReportingFilter.FILTER_ID);
        adjustFilter();
        activateContext();
        this.getCommonViewer().setSorter(null);
        this.getCommonViewer().getTree().addTreeListener(new TreeAdapter() {

            @Override
            public void treeExpanded(TreeEvent e) {
                TreeItem item = (TreeItem) e.item;
                if (!item.getText().endsWith(")")) {
                    Object obj = item.getData();

                    if (obj instanceof TableFolderNode || obj instanceof ViewFolderNode || obj instanceof ColumnFolderNode) {
                        item.setText(item.getText() + "(" + item.getItemCount() + ")");
                    }
                }
                super.treeExpanded(e);
            }

        });

        this.getCommonViewer().getTree().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Tree tree = (Tree) e.getSource();
                Point point = new Point(e.x, e.y);
                TreeItem item = tree.getItem(point);
                if (item != null) {
                    Object obj = item.getData();

                    if (obj instanceof AbstractFolderNode) {
                        AbstractFolderNode node = (AbstractFolderNode) obj;
                        node.loadChildren();
                        Object[] children = node.getChildren();
                        if (children != null) {
                            if (item.getText().indexOf("(") > 0) {
                                item.setText(item.getText().substring(0, item.getText().indexOf("(")) + "(" + children.length
                                        + ")");
                            } else {
                                item.setText(item.getText() + "(" + children.length + ")");
                            }

                        }

                    }
                }
                super.mouseDoubleClick(e);
            }

        });
    }

    /**
     * Activate a context that this view uses. It will be tied to this view activation events and will be removed when
     * the view is disposed.
     */
    private void activateContext() {
        IContextService contextService = (IContextService) getSite().getService(IContextService.class);
        contextService.activateContext(VIEW_CONTEXT_ID);

        DeleteObjectsAction deleteObjectsAction = new DeleteObjectsAction();
        IHandlerService service = (IHandlerService) getViewSite().getService(IHandlerService.class);
        service.activateHandler(deleteObjectsAction.getActionDefinitionId(), new ActionHandler(deleteObjectsAction));
    }

    private void adjustFilter() {
        List<IService> filterList = GlobalServiceRegister.getDefault().getServiceGroup(IViewerFilterService.class);
        for (IService service : filterList) {
            if (service instanceof IViewerFilterService) {
                IViewerFilterService filterService = (IViewerFilterService) service;
                if (filterService.isAddOrDel()) {
                    this.addViewerFilter(filterService.getViwerFilterId());
                } else {
                    this.removeViewerFilter(filterService.getViwerFilterId());
                }
            }
        }
    }

    public void addViewerFilter(int viewerFilterId) {
        if (filterMap.containsKey(String.valueOf(viewerFilterId))) {
            return;
        }
        AbstractViewerFilter filter = null;
        switch (viewerFilterId) {
        case EMFObjFilter.FILTER_ID:
            filter = new EMFObjFilter();
            filterMap.put(String.valueOf(EMFObjFilter.FILTER_ID), filter);
            break;
        case ReportingFilter.FILTER_ID:
            filter = new ReportingFilter();
            filterMap.put(String.valueOf(ReportingFilter.FILTER_ID), filter);
            break;
        default:
            filter = new ReportingFilter();
            filterMap.put(String.valueOf(EMFObjFilter.FILTER_ID), filter);
        }
        this.getCommonViewer().addFilter(filter);
    }

    public void removeViewerFilter(int viewerFilterId) {
        String filterKey = String.valueOf(viewerFilterId);
        if (filterMap.containsKey(filterKey)) {
            this.getCommonViewer().removeFilter(filterMap.get(filterKey));
            this.filterMap.remove(filterKey);
        }
    }

}

// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.cwm.compare.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.internal.ChangePropertyAction;
import org.eclipse.compare.internal.CompareUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffGroup;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.ui.AbstractCompareAction;
import org.eclipse.emf.compare.ui.ModelCompareInput;
import org.eclipse.emf.compare.ui.util.EMFCompareConstants;
import org.eclipse.emf.compare.ui.viewer.content.ModelContentMergeViewer;
import org.eclipse.emf.compare.ui.viewer.content.part.diff.ModelContentMergeDiffTab;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.actions.ReloadDatabaseAction;
import org.talend.cwm.compare.ui.actions.RenameComparedElementAction;
import org.talend.cwm.compare.ui.actions.SubelementCompareAction;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
@SuppressWarnings("restriction")
public class CompareModelContentMergeViewer extends ModelContentMergeViewer {

    private static final int KEY_CODE_C = 99;

    private static final int KEY_CODE_T = 116;

    private static final int KEY_CODE_V = 118;

    private static Logger log = Logger.getLogger(CompareModelContentMergeViewer.class);

    private Object selectedOjbect = null;

    private ModelContentMergeDiffTab diffTabLeft = null;

    private ModelContentMergeDiffTab diffTabRight = null;

    public CompareModelContentMergeViewer(Composite parent, CompareConfiguration config, Object selObj) {
        super(parent, config);
        selectedOjbect = selObj;
        // MOD mzhao feature 8227
        diffTabLeft = (ModelContentMergeDiffTab) leftPart.getTreePart();
        diffTabRight = (ModelContentMergeDiffTab) rightPart.getTreePart();
        diffTabLeft.setComparator(new ViewerComparator());
        diffTabRight.setComparator(new ViewerComparator());
    }

    public void hookContextMenu(boolean compareEachOther) {

        if (!compareEachOther) {
            MenuManager menuMgr = new MenuManager("#PopupMenu", "contextMenu");//$NON-NLS-1$ $NON-NLS-2$
            menuMgr.setRemoveAllWhenShown(true);
            menuMgr.addMenuListener(new IMenuListener() {

                public void menuAboutToShow(IMenuManager manager) {
                    manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                    IStructuredSelection selection = (IStructuredSelection) diffTabLeft.getSelection();
                    EObject selectedElement = null;
                    if (selection.toList().size() == 1) {
                        selectedElement = (EObject) selection.getFirstElement();
                        if (selectedElement instanceof Package) {
                            if (isMuitLevelStructor((Package) selectedElement)) {
                                return;
                            }
                            SubelementCompareAction subEleCompTableAction = new SubelementCompareAction(Messages
                                    .getString("CompareModelContentMergeViewer.CompareListOfTables"), //$NON-NLS-1$
                                    diffTabLeft, selectedOjbect, SubelementCompareAction.TABLE_COMPARE);
                            SubelementCompareAction subEleCompViewAction = new SubelementCompareAction(Messages
                                    .getString("CompareModelContentMergeViewer.CompareListOfViews"), //$NON-NLS-1$
                                    diffTabLeft, selectedOjbect, SubelementCompareAction.VIEW_COMPARE);
                            manager.add(subEleCompTableAction);
                            manager.add(subEleCompViewAction);
                        } else if (selectedElement instanceof ColumnSet) {
                            addRenameMenuAction(manager, selectedElement);
                            SubelementCompareAction subEleCompColumnAction = new SubelementCompareAction(
                                    Messages.getString("CompareModelContentMergeViewer.CompareListOfColumns"), diffTabLeft, selectedOjbect, //$NON-NLS-1$
                                    SubelementCompareAction.COLUMN_COMPARE);
                            manager.add(subEleCompColumnAction);
                        } else if (selectedElement instanceof TdColumn) {
                            addRenameMenuAction(manager, selectedElement);
                        }

                    }

                }
            });

            Menu menu = menuMgr.createContextMenu(diffTabLeft.getControl());
            diffTabLeft.getControl().setMenu(menu);
            CompareUIPlugin.getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite()
                    .registerContextMenu(menuMgr, diffTabLeft);
            // Add key shortcut
            diffTabLeft.getTree().addKeyListener(new CompareKeyListener());
            diffTabLeft.getTree().addMouseListener(new CompareMouseListener());
        }
    }

    /**
     * DOC bZhou Comment method "hookToolBar".
     * 
     * @param compareEachOther
     */
    public void hookToolBar(boolean compareEachOther) {
        IToolBarManager tbm = getToolBarManager(getControl().getParent());
        IContributionItem[] icItems = tbm.getItems();
        for (IContributionItem conbItem : icItems) {
            if (conbItem instanceof ActionContributionItem) {
                IAction action = ((ActionContributionItem) conbItem).getAction();

                if (action != null) {
                    String actionId = action.getActionDefinitionId();
                    if (compareEachOther && COPY_RIGHT_TO_LEFT_ID.equals(actionId)) {
                        tbm.remove(conbItem);
                        conbItem.dispose();
                    }
                }
            }
        }

        tbm.update(true);
    }

    private List<DiffElement> getDiffElementList() {
        List<DiffElement> diffElementList = new ArrayList<DiffElement>();
        // recursive to add diff element.
        DiffModel diffModel = (DiffModel) ((ModelCompareInput) getInput()).getDiff();
        EList<DiffElement> diffElements = diffModel.getOwnedElements();
        for (DiffElement diffEle : diffElements) {
            getDiffElements(diffEle, diffElementList);
        }
        return diffElementList;
    }

    private List<ModelElement> getChangedList() {
        List<ModelElement> changedLists = new ArrayList<ModelElement>();
        List<DiffElement> diffElementList = getDiffElementList();
        for (DiffElement diffEle : diffElementList) {
            if (diffEle instanceof ModelElementChangeRightTarget) {
                changedLists.add((ModelElement) ((ModelElementChangeRightTarget) diffEle).getRightElement());
            } else if (diffEle instanceof UpdateAttribute) {
                changedLists.add((ModelElement) ((UpdateAttribute) diffEle).getRightElement());
            }
        }
        return changedLists;
    }

    private void addRenameMenuAction(IMenuManager manager, EObject selectedElement) {

        List<ModelElement> changedList = getChangedList();
        List<DiffElement> diffElementList = getDiffElementList();
        // Add rename element action.
        for (DiffElement diffEle : diffElementList) {
            if (diffEle instanceof UpdateAttribute) {
                if (((UpdateAttribute) diffEle).getLeftElement() == selectedElement) {
                    // Add action menu
                    RenameComparedElementAction renameComparedElementAction = new RenameComparedElementAction(
                            (IFolderNode) selectedOjbect, (ModelElement) selectedElement, changedList);
                    manager.add(renameComparedElementAction);
                    return;
                }
            } else if (diffEle instanceof ModelElementChangeLeftTarget) {
                if (((ModelElementChangeLeftTarget) diffEle).getLeftElement() == selectedElement) {
                    // Add action menu
                    RenameComparedElementAction renameComparedElementAction = new RenameComparedElementAction(
                            (IFolderNode) selectedOjbect, (ModelElement) selectedElement, changedList);
                    manager.add(renameComparedElementAction);
                    return;
                }
            }
        }
    }

    /**
     * 
     * DOC mzhao CompareModelContentMergeViewer class global comment. Detailled comment
     */
    private class CompareKeyListener implements KeyListener {

        public void keyPressed(KeyEvent e) {
            IStructuredSelection selection = (IStructuredSelection) diffTabLeft.getSelection();
            EObject selectedElement = null;
            if (selection.toList().size() == 1) {
                selectedElement = (EObject) selection.getFirstElement();
            }
            switch (e.keyCode) {
            case KEY_CODE_T:
                if (selectedElement != null && selectedElement instanceof Package) {
                    performCompareAction(selectedElement, SubelementCompareAction.TABLE_COMPARE);
                }

                break;
            case KEY_CODE_V:
                if (selectedElement != null && selectedElement instanceof Package) {
                    performCompareAction(selectedElement, SubelementCompareAction.VIEW_COMPARE);
                }

                break;
            case KEY_CODE_C:
                if (selectedElement != null && selectedElement instanceof ColumnSet) {
                    performCompareAction(selectedElement, SubelementCompareAction.COLUMN_COMPARE);
                }
                break;
            default:
                break;
            }

        }

        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * 
     * DOC mzhao CompareModelContentMergeViewer class global comment. Detailled comment
     */
    private class CompareMouseListener implements MouseListener {

        public void mouseDoubleClick(MouseEvent e) {
            IStructuredSelection selection = (IStructuredSelection) diffTabLeft.getSelection();
            EObject selectedElement = null;
            if (selection.toList().size() == 1) {
                selectedElement = (EObject) selection.getFirstElement();
                if (selectedElement != null) {
                    performCompareAction(selectedElement, SubelementCompareAction.TABLE_COMPARE);
                }
            }
        }

        public void mouseDown(MouseEvent e) {

        }

        public void mouseUp(MouseEvent e) {

        }

    }

    private void performCompareAction(EObject selectedElement, int tableOrViewCompare) {
        List<DiffElement> diffElementList = getDiffElementList();
        for (DiffElement diffEle : diffElementList) {
            if (diffEle instanceof UpdateAttribute) {
                if (((UpdateAttribute) diffEle).getLeftElement() == selectedElement) {
                    return;
                }

            } else if (diffEle instanceof ModelElementChangeLeftTarget) {
                if (((ModelElementChangeLeftTarget) diffEle).getLeftElement() == selectedElement) {
                    return;
                }

            }
        }

        SubelementCompareAction subEleCompColumnAction = null;
        if (selectedElement instanceof Package) {
            if (isMuitLevelStructor((Package) selectedElement)) {
                return;
            }
            subEleCompColumnAction = new SubelementCompareAction(
                    tableOrViewCompare == SubelementCompareAction.TABLE_COMPARE ? Messages
                            .getString("CompareModelContentMergeViewer.CompareListOfTable") //$NON-NLS-1$
                            : Messages.getString("CompareModelContentMergeViewer.CompareListOfVeiw"), diffTabLeft, selectedOjbect, tableOrViewCompare); //$NON-NLS-1$
        } else if (selectedElement instanceof ColumnSet) {
            subEleCompColumnAction = new SubelementCompareAction(
                    Messages.getString("CompareModelContentMergeViewer.CompareListOfColumn"), diffTabLeft, selectedOjbect, //$NON-NLS-1$
                    SubelementCompareAction.COLUMN_COMPARE);
        }
        if (subEleCompColumnAction != null) {
            subEleCompColumnAction.run();
        }

    }

    private void getDiffElements(DiffElement diffEle, List<DiffElement> diffElementList) {
        if (diffEle instanceof DiffGroup) {
            for (DiffElement subDiffEle : ((DiffGroup) diffEle).getSubDiffElements()) {
                getDiffElements(subDiffEle, diffElementList);
            }
        } else {
            diffElementList.add(diffEle);
        }
    }

    @Override
    protected void createToolItems(ToolBarManager tbm) {

        // NEXT DIFF
        final Action nextDiff = new AbstractCompareAction(ResourceBundle.getBundle(BUNDLE_NAME), "action.NextDiff.") { //$NON-NLS-1$

            @Override
            public void run() {
                navigate(true);
            }
        };
        final ActionContributionItem nextDiffContribution = new ActionContributionItem(nextDiff);
        nextDiffContribution.setVisible(true);
        tbm.appendToGroup("navigation", nextDiffContribution); //$NON-NLS-1$
        // PREVIOUS DIFF
        final Action previousDiff = new AbstractCompareAction(ResourceBundle.getBundle(BUNDLE_NAME), "action.PrevDiff.") { //$NON-NLS-1$

            @Override
            public void run() {
                navigate(false);
            }
        };
        final ActionContributionItem previousDiffContribution = new ActionContributionItem(previousDiff);
        previousDiffContribution.setVisible(true);
        tbm.appendToGroup("navigation", previousDiffContribution); //$NON-NLS-1$

        IContributionItem[] icItems = tbm.getItems();
        for (IContributionItem conbItem : icItems) {

            if (conbItem instanceof ActionContributionItem) {

                IAction action = ((ActionContributionItem) conbItem).getAction();
                if (action != null) {
                    String actionId = action.getActionDefinitionId();

                    if (action instanceof ChangePropertyAction) {
                        tbm.remove(conbItem);
                        conbItem.dispose();
                    }

                    if (COPY_LEFT_TO_RIGHT_ID.equals(actionId)) {
                        tbm.remove(conbItem);
                        conbItem.dispose();
                    }
                }
            }

        }
        tbm.update(true);
        // ~

    }

    @Override
    protected void copy(boolean leftToRight) {
        // First check dependencies.
        ModelElement modelElement = null;
        IFile resourceFile = null;
        // File
        if (selectedOjbect instanceof IFile) {
            modelElement = PrvResourceFileHelper.getInstance().findProvider((IFile) selectedOjbect);
        } else if (selectedOjbect instanceof IRepositoryViewObject) {
            // MOD klliu 2010-10-08 bug 16173: get changes in "database compare" editor
            modelElement = PropertyHelper.getModelElement(((IRepositoryViewObject) selectedOjbect).getProperty());

        } else {
            // Folder
            // MOD msjian 2011-5-20 20875:Change to model element
            IRepositoryNode parentNode = DBTableRepNode.getParentPackageNode((IRepositoryNode) selectedOjbect);
            Package ctatlogSwtich = parentNode instanceof DBCatalogRepNode ? ((DBCatalogRepNode) parentNode).getCatalog() : null;
            // ~
            if (ctatlogSwtich != null) {
                resourceFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(
                        ConnectionHelper.getTdDataProvider(ctatlogSwtich));
                modelElement = ConnectionHelper.getTdDataProvider(ctatlogSwtich);
            }
            ColumnSet columnSet = parentNode instanceof DBTableRepNode ? ((DBTableRepNode) parentNode).getTdTable() : null;
            if (columnSet != null) {
                resourceFile = PrvResourceFileHelper.getInstance().findCorrespondingFile(
                        ConnectionHelper.getDataProvider(columnSet));
                modelElement = ConnectionHelper.getDataProvider(columnSet);
            }
        }
        if (modelElement != null && resourceFile != null) {
            String titleMessage = DefaultMessagesImpl.getString("CompareModelContentMergeViewer.ImpactAnalyses"); //$NON-NLS-1$

            int showDialog = DeleteModelElementConfirmDialog.showElementImpactDialog(null, new ModelElement[] { modelElement },
                    titleMessage, titleMessage);
            if (showDialog == Window.OK) {
                EObjectHelper.removeDependencys(new IResource[] { resourceFile });
            } else {
                return;
            }
        }

        int diffItemsCount = ((ModelCompareInput) getInput()).getDiffAsList().size();
        try {
            super.copy(leftToRight);
            // MOD klliu 2011-03-01 bug 17506
            ModelCompareInput modelCompareInput = (ModelCompareInput) getInput();
            if (!leftToRight && modelCompareInput.getDiffAsList().size() == 0) {
                Resource leftResource = modelCompareInput.getLeftResource();
                XmiResourceManager resourceManager = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
                        .getResourceManager();
                resourceManager.saveResource(leftResource);
                // refactor the input
                final MatchModel match = (MatchModel) modelCompareInput.getMatch();
                match.getUnmatchedElements().clear();
                final ComparisonResourceSnapshot snap = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
                snap.setDiff((DiffModel) ((ModelCompareInput) getInput()).getDiff());
                snap.setMatch((MatchModel) ((ModelCompareInput) getInput()).getMatch());
                configuration.setProperty(EMFCompareConstants.PROPERTY_CONTENT_INPUT_CHANGED, snap);
            }
            // MOD mzhao 2009-03-11 copy from right to left.need reload the
            // currently selected element.
            if (!leftToRight && diffItemsCount > 0) {
                // MOD yyi 2011-05-16 21512:need to reload the db connection element
                if (selectedOjbect instanceof RepositoryViewObject) {
                    new ReloadDatabaseAction(((RepositoryViewObject) selectedOjbect).getRepositoryNode(), null).run();
                } else {
                    // MOD msjian 2011-5-20 20875:do copy for table list
                    new ReloadDatabaseAction(selectedOjbect, null).run();
                }
            }

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }

    }

    private boolean isMuitLevelStructor(Package current) {
        if (current instanceof Catalog) {
            List<Schema> schemas = CatalogHelper.getSchemas((Catalog) current);
            if (schemas.size() > 0) {
                return true;
            }
        }
        return false;
    }

    private static final String COPY_LEFT_TO_RIGHT_ID = "org.eclipse.compare.copyAllLeftToRight"; //$NON-NLS-1$

    private static final String COPY_RIGHT_TO_LEFT_ID = "org.eclipse.compare.copyAllRightToLeft"; //$NON-NLS-1$

}

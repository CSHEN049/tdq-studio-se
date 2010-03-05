// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.editor.composite.AbstractTableDropTree;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisTableTreeViewer;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public abstract class TableViewerDND {

    ISelectionReceiver receiver = null;

    private static int lastValidOperation;

    /**
     * DOC xqliu Comment method "installDND".
     * 
     * @param targetControl
     */
    public static void installDND(final Tree targetControl) {

        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        DQRespositoryView findView = (DQRespositoryView) activePage.findView(DQRespositoryView.ID);
        final CommonViewer commonViewer = findView.getCommonViewer();
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] { transfer };
        DropTarget dropTarget = new DropTarget(targetControl, operations);
        dropTarget.setTransfer(transfers);

        DropTargetListener dndListener = new TreeDropTargetEffect(targetControl) {

            ISelectionReceiver receiver = null;

            @Override
            public void dragEnter(DropTargetEvent event) {
                super.dragEnter(event);
                IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
                Object object = selection.getFirstElement();
                if (object instanceof IFile) {
                    receiver = new DQRuleReceiver();
                }

                if (object instanceof NamedColumnSet) {
                    receiver = new TableReceiver();
                }

                if (receiver == null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.feedback = DND.FEEDBACK_EXPAND;
                    receiver.doDropValidation(event, commonViewer);
                }
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                super.dragOver(event);
                if (receiver != null) {
                    receiver.doDropValidation(event, commonViewer);
                }
            }

            @Override
            public void drop(DropTargetEvent event) {
                int index = targetControl.getItemCount();
                super.drop(event);
                if (event.item == null) {
                    // TreeItem item = new TreeItem(targetControl, SWT.NONE);
                    // item.setText(texts);
                    // item.setText(text);
                } else {
                    TreeItem item = (TreeItem) event.item;
                    TreeItem[] items = targetControl.getItems();
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] == item) {
                            index = i;
                            break;
                        }
                    }

                }
                if (receiver != null) {
                    receiver.drop(event, commonViewer, index);
                }
            }
        };

        dropTarget.addDropListener(dndListener);
    }

    /**
     * DOC xqliu TableViewerDND class global comment. Detailled comment
     */
    interface ISelectionReceiver {

        void doDropValidation(DropTargetEvent event, CommonViewer commonViewer);

        void drop(DropTargetEvent event, CommonViewer commonViewer, int index);
    }

    /**
     * DOC xqliu DQRuleReceiver class global comment. Detailled comment
     */
    static class DQRuleReceiver implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {
            if (event.detail != DND.DROP_NONE) {
                lastValidOperation = event.detail;
            }

            boolean is = true;
            Object firstElement = ((StructuredSelection) commonViewer.getSelection()).getFirstElement();
            if (firstElement instanceof IFile) {
                IFile fe = (IFile) firstElement;
                if (FactoriesUtil.DQRULE.equals(fe.getFileExtension())) {
                    WhereRule whereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(fe);
                    if (whereRule != null && TaggedValueHelper.getValidStatus(whereRule)) {
                        is = false;
                    }
                }
            }

            if (event.item == null || is) {
                event.detail = DND.DROP_NONE;
            } else {
                Object data = event.item.getData(AnalysisTableTreeViewer.INDICATOR_UNIT_KEY);
                if (data != null) {
                    event.detail = DND.DROP_NONE;
                } else {
                    event.detail = lastValidOperation;
                }
            }
        }

        // @Override
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            List list = ((StructuredSelection) commonViewer.getSelection()).toList();
            List<IFile> files = new ArrayList<IFile>();
            for (Object obj : list) {
                if (obj instanceof IFile) {
                    files.add((IFile) obj);
                }
            }

            TreeItem item = (TreeItem) event.item;
            if (item != null) {
                Object obj = item.getData(AnalysisTableTreeViewer.TABLE_INDICATOR_KEY);
                if (obj != null && obj instanceof TableIndicator) {
                    TableIndicator data = (TableIndicator) obj;
                    AnalysisTableTreeViewer viewer = (AnalysisTableTreeViewer) item.getParent().getData(
                            AnalysisTableTreeViewer.VIEWER_KEY);

                    viewer.dropWhereRules(data, files, index, item);
                }
            }
        }

        public void dragOver(DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
        }
    }

    /**
     * DOC xqliu TableViewerDND class global comment. Detailled comment
     */
    static class TableReceiver implements ISelectionReceiver {

        // @Override
        public void doDropValidation(DropTargetEvent event, CommonViewer commonViewer) {
            event.detail = DND.DROP_NONE;
            Object firstElement = ((StructuredSelection) LocalSelectionTransfer.getTransfer().getSelection()).getFirstElement();
            if (firstElement instanceof NamedColumnSet) {
                NamedColumnSet set = (NamedColumnSet) firstElement;
                Tree tree = (Tree) ((DropTarget) event.widget).getControl();
                AbstractTableDropTree viewer = (AbstractTableDropTree) tree.getData();
                if (viewer != null && viewer.canDrop(set)) {
                    event.detail = DND.DROP_MOVE;
                }
            }
        }

        @SuppressWarnings("unchecked")
        // @Override
        public void drop(DropTargetEvent event, CommonViewer commonViewer, int index) {
            LocalSelectionTransfer localSelection = LocalSelectionTransfer.getTransfer();
            Tree control = (Tree) ((DropTarget) event.widget).getControl();
            AbstractTableDropTree viewer = (AbstractTableDropTree) control.getData();
            StructuredSelection selection = (StructuredSelection) localSelection.getSelection();
            Iterator it = selection.iterator();
            List<NamedColumnSet> selectedTableList = new ArrayList<NamedColumnSet>();
            while (it.hasNext()) {
                NamedColumnSet set = (NamedColumnSet) it.next();
                selectedTableList.add(set);
            }
            int size1 = selection.size();
            int size2 = selectedTableList.size();
            if (size1 == size2) {
                viewer.dropTables(selectedTableList, index);
            }
            localSelection = null;
        }
    }
}

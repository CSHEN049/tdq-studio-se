// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.provider;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.dataprofiler.core.ui.action.actions.ColumnFilterAction;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnFilterProvider extends AbstractCommonActionProvider {

    protected static Logger log = Logger.getLogger(ColumnFilterProvider.class);

    public ColumnFilterProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        Object firstElement = currentSelection.getFirstElement();
        if (firstElement instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) firstElement;
            if (node instanceof DBTableRepNode || node instanceof DBViewRepNode) {
                ColumnFilterAction ecfAction = new ColumnFilterAction(node);
                menu.add(ecfAction);
            }
        }
    }

}

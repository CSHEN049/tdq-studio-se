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
package org.talend.dataprofiler.core.ui.editor;

import java.util.Hashtable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;

/**
 * DOC rli class global comment. Detailled comment
 */
public class AnalysisFormEditorContributor extends MultiPageEditorActionBarContributor {

    protected AnalysisEditor fEditor;

    protected IFormPage fPage;

    private GlobalAction runAnalysisAction = null;
    private Hashtable<String, Action> fGlobalActions = new Hashtable<String, Action>();

    public AnalysisFormEditorContributor() {
        super();
    }

    /**
     * DOC rli PDEFormEditorContributor class global comment. Detailled comment
     */
    class GlobalAction extends Action {

        private String id;

        public GlobalAction(String id) {
            this.id = id;
        }
        public void run() {
            fEditor.performGlobalAction(id);
        }
    }

    private void addGlobalAction(String id) {
        GlobalAction action = new GlobalAction(id);
        action.setActionDefinitionId(id);
        addGlobalAction(id, action);
    }

    private void addGlobalAction(String id, Action action) {
        fGlobalActions.put(id, action);
        getActionBars().setGlobalActionHandler(id, action);
    }

    public void contributeToMenu(IMenuManager mm) {
    }

    public void contributeToStatusLine(IStatusLineManager slm) {
    }

    public void contributeToToolBar(IToolBarManager tbm) {
    }

    public void contributeToCoolBar(ICoolBarManager cbm) {
    }

    public AnalysisEditor getEditor() {
        return fEditor;
    }

    public IAction getGlobalAction(String id) {
        return (IAction) fGlobalActions.get(id);
    }

    public IStatusLineManager getStatusLineManager() {
        return getActionBars().getStatusLineManager();
    }

    protected void makeActions() {
        addGlobalAction(ActionFactory.DELETE.getId());
        // MOD mzhao bug 8710
        addGlobalAction(RunAnalysisAction.ID);
    }

    public void setActiveEditor(IEditorPart targetEditor) {
        if (!(targetEditor instanceof AnalysisEditor)) {
            return;
        }

        fEditor = (AnalysisEditor) targetEditor;
        setActivePage(fEditor.getActiveEditor());

    }

    public void setActivePage(IEditorPart newEditor) {
        if (fEditor == null) {
            return;
        }
        IFormPage oldPage = fPage;
        fPage = fEditor.getActivePageInstance();
        if (fPage != null) {
            if (oldPage != null && !oldPage.isEditor() && !fPage.isEditor()) {
                getActionBars().updateActionBars();
            }
        }
        // MOD mzhao bug 8710
        addGlobalAction(RunAnalysisAction.ID, fGlobalActions.get(RunAnalysisAction.ID));
    }

    public void init(IActionBars bars) {
        super.init(bars);
        makeActions();
    }

}

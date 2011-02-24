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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension2;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.talend.dataprofiler.core.CorePlugin;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class CommonFormEditor extends FormEditor {

    private TdEditorToolBar toolBar = null;

    private TdEditorBarWrapper editorBarWrap = null;

    private Object editorObject;

    public Object getEditorObject() {
        return this.editorObject;
    }

    public void setEditorObject(Object editorObject) {
        this.editorObject = editorObject;
    }

    @Override
    protected void addPages() {
        // TODO Auto-generated method stub

    }

    private static Logger log = Logger.getLogger(CommonFormEditor.class);

    protected boolean isDirty = false;

    private IDocumentProvider provider;

    /** The editor's element state listener. */
    private IElementStateListener fElementStateListener = new ElementListener();

    /**
     * DOC rli AnalysisEditor class global comment. Detailled comment
     */
    class ElementListener implements IElementStateListener {

        public void elementContentAboutToBeReplaced(Object element) {
        }

        public void elementContentReplaced(Object element) {
        }

        public void elementDeleted(Object element) {
            close(true);
        }

        public void elementDirtyStateChanged(Object element, boolean isDirty) {
        }

        public void elementMoved(Object originalElement, Object movedElement) {
            close(true);
        }
    }

    @Override
    protected Composite createPageContainer(Composite parent) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 0;
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        parent.setLayout(gridLayout);

        Composite barComp = new Composite(parent, SWT.NONE);
        GridData gdData = new GridData(GridData.FILL_HORIZONTAL);
        barComp.setLayoutData(gdData);
        barComp.setLayout(new FormLayout());

        createToolbar(barComp);

        Composite mainParent = new Composite(parent, SWT.NONE);
        GridData gdData1 = new GridData(GridData.FILL_BOTH);
        gdData1.grabExcessVerticalSpace = true;
        mainParent.setLayoutData(gdData1);
        return super.createPageContainer(mainParent);
    }

    protected void setInput(IEditorInput input) {
        super.setInput(input);
        translateInput(input);
        try {
            updateDocumentProvider(input);
        } catch (CoreException e) {
            log.error("Install the document provider is failture when created the editor of " + input.getName());
        }
    }

    protected void translateInput(IEditorInput input) {
    }

    /**
     * Provide a new DocumentProvider based on the given editor input.
     * 
     * @param input the editor input.
     * @throws CoreException
     */
    protected void updateDocumentProvider(IEditorInput input) throws CoreException {
        IProgressMonitor rememberedProgressMonitor = null;
        provider = new TextFileDocumentProvider();
        provider.addElementStateListener(fElementStateListener);
        if (provider instanceof IDocumentProviderExtension2) {
            IDocumentProviderExtension2 extension = (IDocumentProviderExtension2) provider;
            extension.setProgressMonitor(rememberedProgressMonitor);
        }

        provider.connect(input);
    }

    /**
     * Disposes of the connection with the document provider. Subclasses may extend.
     * 
     * @since 3.0
     */
    protected void disposeDocumentProvider() {
        if (provider != null) {

            IEditorInput input = getEditorInput();
            if (input != null) {
                provider.disconnect(input);
            }

            if (fElementStateListener != null) {
                provider.removeElementStateListener(fElementStateListener);
                fElementStateListener = null;
            }

        }
        provider = null;
    }

    @Override
    public void dispose() {
        this.disposeDocumentProvider();
        super.dispose();
    }

    public void close(final boolean save) {
        Display display = getSite().getShell().getDisplay();
        display.asyncExec(new Runnable() {

            public void run() {
                getSite().getPage().closeEditor(CommonFormEditor.this, save);
            }
        });
    }

    public void doSaveAs() {
        doSave(null);
    }

    public void doSave(IProgressMonitor monitor) {
        this.isDirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);
        if (this.getEditorObject() != null) {
            CorePlugin.getDefault().refreshDQView(this.getEditorObject());
        } else {
            CorePlugin.getDefault().refreshDQView();
        }
        CorePlugin.getDefault().refreshWorkSpace();
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#pageChange(int)
     */
    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (getActivePageInstance() instanceof AbstractFormPage) {
            AbstractFormPage page = (AbstractFormPage) getActivePageInstance();
            editorBarWrap.clearRegisterComposite();
            editorBarWrap.setExpandableComposites(page.getExpandCompositeList());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
     */
    @Override
    public boolean isDirty() {
        return isDirty || super.isDirty();
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /**
     * DOC bzhou Comment method "createToolbar".
     * 
     * @param parent
     */
    protected void createToolbar(final Composite parent) {
        editorBarWrap = new TdEditorBarWrapper(this);
        toolBar = new TdEditorToolBar(parent, editorBarWrap);

        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        toolBar.getToolbarControl().setLayoutData(data);

        toolBar.addResizeListener(new ControlListener() {

            public void controlMoved(ControlEvent e) {
            }

            public void controlResized(ControlEvent e) {
                parent.getParent().layout(true);
                parent.layout(true);
            }
        });
    }

    /**
     * DOC bzhou Comment method "getToolBar".
     * 
     * @return
     */
    public TdEditorToolBar getToolBar() {
        return toolBar;
    }

    /**
     * DOC yyi Comment method "LockEditor".
     * 
     * @param lock
     */
    public void lockFormEditor(boolean lock) {
        if (null != editorBarWrap) {
            Iterator<ExpandableComposite> it = editorBarWrap.getExpandableComposites().iterator();
            while (it.hasNext()) {
                ExpandableComposite composite = it.next();
                if (composite == null || composite.isDisposed()) {
                    it.remove();
                    continue;
                }
                composite.getClient().setEnabled(!lock);
            }
        }
        if (null != toolBar) {
            toolBar.getToolbarControl().setEnabled(!lock);
        }
    }
}

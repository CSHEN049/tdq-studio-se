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
package org.talend.dataprofiler.core.ui.action.actions;

import java.text.DecimalFormat;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPoolMap;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 */
public class RunAnalysisAction extends Action implements ICheatSheetAction {

    private static Logger log = Logger.getLogger(RunAnalysisAction.class);

    public static final String ID = "org.talend.common.runTalendElement"; //$NON-NLS-1$

    private static final DecimalFormat FORMAT_SECONDS = new DecimalFormat("0.00"); //$NON-NLS-1$

    private Analysis analysis = null;

    private IRuningStatusListener listener;

    private IFile selectionFile;

    private AnalysisRepNode node;

    public IFile getSelectionFile() {
        return selectionFile;
    }

    public void setSelectionFile(IFile selectionFile) {
        this.selectionFile = selectionFile;
    }

    public void setSelectionNode(AnalysisRepNode node) {
        this.node = node;
    }

    public RunAnalysisAction() {
        super(DefaultMessagesImpl.getString("RunAnalysisAction.Run")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

        // MOD klliu bug 19244 2011-03-10
        if (node != null) {
            editor = CorePlugin.getDefault().openEditor(new AnalysisItemEditorInput(node.getObject().getProperty().getItem()),
                    AnalysisEditor.class.getName());
        }
        // MOD qiongli bug 13880,2010-7-6,avoid 'ClassCastException'
        if (selectionFile != null) {
            // editor = CorePlugin.getDefault().openEditor(selectionFile, AnalysisEditor.class.getName());
            analysis = AnaResourceFileHelper.getInstance().findAnalysis(selectionFile);
            RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
            if (recursiveFind != null) {
                editor = CorePlugin.getDefault().openEditor(
                        new AnalysisItemEditorInput(recursiveFind.getObject().getProperty().getItem()),
                        AnalysisEditor.class.getName());
            }
        }
        // ~
        if (editor == null) {
            analysis = this.node.getAnalysis();// AnaResourceFileHelper.getInstance().findAnalysis(selectionFile);
        } else {
            AnalysisEditor anaEditor = (AnalysisEditor) editor;
            if (editor.isDirty()) {
                // MOD qiongli 2011-6-20 bug 21533,can save should before method doSave()
                // MOD klliu bug 19991 3td 2011-03-29
                ReturnCode canSave = anaEditor.getMasterPage().canSave();
                if (!canSave.isOk()) {
                    MessageDialog.openError(null, DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"),
                            canSave.getMessage());
                    return;
                }
                // ~
                anaEditor.doSave(null);
            }
            ReturnCode canRun = anaEditor.canRun();
            if (!canRun.isOk()) {
                MessageDialogWithToggle.openError(null, DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), canRun//$NON-NLS-1$
                        .getMessage());
                return;
            }

            if (selectionFile != null) {
                analysis = AnaResourceFileHelper.getInstance().findAnalysis(selectionFile);

                IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .getEditorReferences();

                for (IEditorReference reference : editorReferences) {
                    AnalysisItemEditorInput analysisItemEditorInput;
                    try {
                        // MOD qiongli bug 16505.
                        IEditorInput editorInput = reference.getEditorInput();
                        // if (editorInput instanceof FileEditorInput) {
                        // finput = (FileEditorInput) reference.getEditorInput();
                        // if (finput.getFile().equals(selectionFile)) {
                        // IFormPage activePageInstance = ((AnalysisEditor) reference.getEditor(true))
                        // .getActivePageInstance();
                        // // MOD qiongli bug 13880
                        // // if (reference instanceof IRuningStatusListener) {
                        // if (activePageInstance instanceof IRuningStatusListener) {
                        // listener = (IRuningStatusListener) activePageInstance;
                        // }
                        // }
                        // }
                        if (editorInput instanceof AnalysisItemEditorInput) {
                            analysisItemEditorInput = (AnalysisItemEditorInput) editorInput;
                            Analysis ana = ((TDQAnalysisItem) analysisItemEditorInput.getItem()).getAnalysis();
                            if (analysis.equals(ana)) {
                                IFormPage activePageInstance = ((AnalysisEditor) reference.getEditor(true))
                                        .getActivePageInstance();
                                // MOD qiongli bug 13880
                                // if (reference instanceof IRuningStatusListener) {
                                if (activePageInstance instanceof IRuningStatusListener) {
                                    listener = (IRuningStatusListener) activePageInstance;
                                }
                            }
                        }
                    } catch (PartInitException e) {
                        log.error(e, e);
                    }
                }
            } else {
                IEditorInput editorInput = anaEditor.getEditorInput();
                if (editorInput instanceof FileEditorInput) {
                    IFile afile = ((FileEditorInput) editorInput).getFile();
                    analysis = AnaResourceFileHelper.getInstance().findAnalysis(afile);
                } else if (editorInput instanceof AnalysisItemEditorInput) {
                    analysis = ((TDQAnalysisItem) ((AnalysisItemEditorInput) editorInput).getItem()).getAnalysis();
                }
                IFormPage activePageInstance = anaEditor.getActivePageInstance();
                if (activePageInstance instanceof IRuningStatusListener) {
                    listener = (IRuningStatusListener) activePageInstance;
                }
            }
        }

        if (analysis == null) {
            return;
        }
        // MOD klliu bug 4546 check connectiong is connected well.
        Connection analysisDataProvider = this.getAnalysisDataProvider(analysis);
        String dialogTitle = DefaultMessagesImpl.getString("RunAnalysisAction.urlChanged");//$NON-NLS-1$
        String dialogMessage = DefaultMessagesImpl.getString("RunAnalysisAction.checkDBConnection");//$NON-NLS-1$
        // MOD klliu bug 4584 Filtering the file connection when checking connection is successful,before real running
        // analysis.
        if (!(analysisDataProvider instanceof DelimitedFileConnection) && !checkConnIsUseful(analysisDataProvider)) {
            MessageDialogWithToggle.openWarning(null, dialogTitle, dialogMessage);
            return;
        }
        // ~
        AnalysisType analysisType = analysis.getParameters().getAnalysisType();

        if (AnalysisType.COLUMNS_COMPARISON.equals(analysisType)) {
            if (!MessageDialogWithToggle.openConfirm(null, DefaultMessagesImpl.getString("RunAnalysisAction.confirmTitle"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("RunAnalysisAction.confirmMSG"))) { //$NON-NLS-1$
                return;
            }
        }

        if (AnalysisType.CONNECTION.equals(analysisType)) {
            if (AnalysisHelper.getReloadDatabases(analysis)) {
                Connection conntion = (Connection) analysis.getContext().getConnection();
                if (conntion != null) {
                    try {
                        RepositoryNode connectionNode = RepositoryNodeHelper.recursiveFind(conntion);
                        ComparisonLevelFactory.creatComparisonLevel(connectionNode).reloadCurrentLevelElement();
                    } catch (ReloadCompareException e) {
                        log.error(e, e);
                    }
                }
            }
        }

        final WorkspaceJob job = new WorkspaceJob("Run Analysis") { //$NON-NLS-1$

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

                monitor.beginTask(
                        DefaultMessagesImpl.getString("RunAnalysisAction.running", analysis.getName()), IProgressMonitor.UNKNOWN); //$NON-NLS-1$ //$NON-NLS-2$

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        if (listener != null) {
                            listener.fireRuningItemChanged(false);
                        }
                    }

                });

                ReturnCode executed = null;
                AnalysisExecutorThread aet = new AnalysisExecutorThread(analysis, monitor);

                Thread thread = new Thread(aet);
                thread.start();

                while (true) {
                    if (aet.getExecuted() != null) {
                        executed = aet.getExecuted();
                        break;
                    }
                    if (monitor.isCanceled()) {
                        thread.interrupt();
                        closeConnectionPool(analysis);
                        executed = new ReturnCode(DefaultMessagesImpl.getString("RunAnalysisAction.TaskCancel"), false); //$NON-NLS-1$
                        break;
                    }
                }
                aet = null;
                monitor.done();

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        if (listener != null) {
                            listener.fireRuningItemChanged(true);
                        }

                        CorePlugin.getDefault().refreshDQView();
                    }

                });

                displayResultStatus(executed);

                return Status.OK_STATUS;
            }

        };

        job.setUser(true);
        job.schedule();
    }

    private boolean checkConnIsUseful(Connection analysisDataProvider) {
        Properties props = new Properties();
        String userName = JavaSqlFactory.getUsername(analysisDataProvider);
        String password = JavaSqlFactory.getPassword(analysisDataProvider);
        String url = JavaSqlFactory.getURL(analysisDataProvider);
        if (analysisDataProvider.isContextMode()) {
            userName = ConnectionUtils.getOriginalConntextValue(analysisDataProvider, userName);
            password = ConnectionUtils.getOriginalConntextValue(analysisDataProvider, password);
            url = ConnectionUtils.getOriginalConntextValue(analysisDataProvider, url);
        }
        props.put(TaggedValueHelper.USER, userName);
        props.put(TaggedValueHelper.PASSWORD, password);
        if (analysisDataProvider instanceof MDMConnection) {
            props.put(TaggedValueHelper.UNIVERSE, ConnectionHelper.getUniverse((MDMConnection) analysisDataProvider));
            props.put(TaggedValueHelper.DATA_FILTER, ConnectionHelper.getDataFilter((MDMConnection) analysisDataProvider));
        }
        ReturnCode returnCode = ConnectionUtils.isMdmConnection(analysisDataProvider) ? new MdmWebserviceConnection(
                JavaSqlFactory.getURL(analysisDataProvider), props).checkDatabaseConnection() : ConnectionUtils.checkConnection(
                url, JavaSqlFactory.getDriverClass(analysisDataProvider), props);
        return returnCode.isOk();
    }

    /**
     * close the connection pool which belong to the analysis(whether use pooled connection to execute the analysis or
     * not, can call this method safely).
     * 
     * @param ana
     */
    protected void closeConnectionPool(Analysis ana) {
        TdqAnalysisConnectionPoolMap instance = TdqAnalysisConnectionPoolMap.getInstance(ana);
        Connection analysisDataProvider = this.getAnalysisDataProvider(ana);
        if (analysisDataProvider != null) {
            instance.closePool(analysisDataProvider);
        }
        instance.closePools();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        // ADD mzhao 2009-02-03 If there is no active editor opened, run
        // analysis action from cheat sheets will do nothing.
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor == null) {
            return;
        }
        AnalysisEditor anaEditor = (AnalysisEditor) editor;
        AbstractAnalysisMetadataPage masterPage = (AbstractAnalysisMetadataPage) anaEditor.getMasterPage();
        listener = masterPage;
        // ~
        run();
    }

    private void displayResultStatus(final ReturnCode executed) {
        if (log.isInfoEnabled()) {
            int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
            log.info(DefaultMessagesImpl
                    .getString(
                            "RunAnalysisAction.displayInformation", new Object[] { analysis.getName(), executed, FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000) })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        }

        if (executed.getMessage() != null) {
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    MessageDialogWithToggle.openError(
                            null,
                            DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), DefaultMessagesImpl.getString("RunAnalysisAction.failRunAnalysis", analysis.getName(), executed.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$
                }
            });
        }
    }

    protected Connection getAnalysisDataProvider(Analysis analysis) {
        DataManager datamanager = analysis.getContext().getConnection();
        if (datamanager != null && datamanager.eIsProxy()) {
            datamanager = (DataManager) EObjectHelper.resolveObject(datamanager);
        }
        return SwitchHelpers.CONNECTION_SWITCH.doSwitch(datamanager);
    }
}

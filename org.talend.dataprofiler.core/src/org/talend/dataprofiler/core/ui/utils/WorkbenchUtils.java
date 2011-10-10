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
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.OpenItemEditorAction;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.repository.ProjectManager;
import org.talend.repository.localprovider.model.LocalFolderHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class WorkbenchUtils {

    private static Logger log = Logger.getLogger(WorkbenchUtils.class);

    private static final int AUTO_CHANGE2DATA_PROFILER_TRUE = 1;

    private static final int AUTO_CHANGE2DATA_PROFILER_FALSE = 2;

    private static final boolean AUTO_CHANGE2DATA_PROFILER = true;

    private static final String ANALYSIS_EDITOR_ID = "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"; //$NON-NLS-1$

    private static final String CONNECTION_EDITOR_ID = "org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor"; //$NON-NLS-1$

    private WorkbenchUtils() {
    }

    /**
     * DOC bZhou Comment method "changePerspective".
     * 
     * @param perspectiveID
     */
    public static void changePerspective(final String perspectiveID) {
        Display.getCurrent().asyncExec(new Runnable() {

            public void run() {
                IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                try {
                    PlatformUI.getWorkbench().showPerspective(perspectiveID, activeWindow);
                } catch (WorkbenchException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    public static void autoChange2DataProfilerPerspective() {
        if (!AUTO_CHANGE2DATA_PROFILER) {
            return;
        }
        try {
            IPerspectiveDescriptor perspective = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getPerspective();
            if (!(PluginConstant.PERSPECTIVE_ID.equals(perspective.getId()) || PluginConstant.SQLEXPLORER_PERSPECTIVE_ID
                    .equals(perspective.getId()))) {

                int autoChange = ResourcesPlugin.getPlugin().getPluginPreferences()
                        .getInt(PluginConstant.AUTO_CHANGE2DATA_PROFILER);

                switch (autoChange) {
                case AUTO_CHANGE2DATA_PROFILER_TRUE:
                    // change perspective automatically
                    changePerspective(PluginConstant.PERSPECTIVE_ID);
                    break;
                case AUTO_CHANGE2DATA_PROFILER_FALSE:
                    // do nothing
                    break;
                default:
                    // ask user what to do, and rember user's decision
                    if (MessageUI.openYesNoQuestion(DefaultMessagesImpl
                            .getString("WorkbenchUtils.autoChange2DataProfilerPerspective"))) { //$NON-NLS-1$
                        ResourcesPlugin.getPlugin().getPluginPreferences()
                                .setValue(PluginConstant.AUTO_CHANGE2DATA_PROFILER, AUTO_CHANGE2DATA_PROFILER_TRUE);
                        // change perspective
                        changePerspective(PluginConstant.PERSPECTIVE_ID);
                    } else {
                        ResourcesPlugin.getPlugin().getPluginPreferences()
                                .setValue(PluginConstant.AUTO_CHANGE2DATA_PROFILER, AUTO_CHANGE2DATA_PROFILER_FALSE);
                    }
                }
            }
        } catch (Throwable t) {
            log.warn(t, t);
        }
    }

    public static IFolder folder2IFolder(Folder folder) {
        IProject rootProject = ResourceManager.getRootProject();
        return rootProject.getFolder(folder.getPath());
    }

    public static IPath getPath(IRepositoryNode node) {
        return RepositoryNodeHelper.getPath(node);
    }

    public static IPath getPath(RepositoryViewObject viewObject) {
        return getPath((RepositoryNode) viewObject.getRepositoryNode());
    }

    public static IFolder getFolder(RepositoryNode node) {
        // MOD qiongli 2011-1-18 if it is recyclebin,return the root folder
        IPath path = getPath(node);
        if (path.toString().equals(PluginConstant.EMPTY_STRING)) {
            return ResourceManager.getRootProject().getFolder(ResourceManager.getRootFolderLocation());
        }
        return ResourceManager.getRootProject().getFolder(getPath(node));
    }

    public static IFolder getFolder(RepositoryViewObject viewObject) {
        return getFolder((RepositoryNode) viewObject.getRepositoryNode());
    }

    public static String getItemExtendtion(int classID) {
        String fileExtension = FileConstants.ITEM_EXTENSION;
        switch (classID) {

        case org.talend.dataquality.properties.PropertiesPackage.TDQ_ANALYSIS_ITEM:
            fileExtension = FileConstants.ANA_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_REPORT_ITEM:
            fileExtension = FileConstants.REP_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_INDICATOR_DEFINITION_ITEM:
            fileExtension = FileConstants.DEF_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_PATTERN_ITEM:
            fileExtension = FileConstants.PAT_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_BUSINESS_RULE_ITEM:
            fileExtension = FileConstants.RULE_EXTENSION;

            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_JRXML_ITEM:
            fileExtension = FileConstants.JRXML_EXTENSION;
            break;
        case org.talend.dataquality.properties.PropertiesPackage.TDQ_SOURCE_FILE_ITEM:
            fileExtension = FileConstants.SQL_EXTENSION;
            break;
        }
        return fileExtension;

    }

    public static IPath getFilePath(IRepositoryNode node) {
        Item item = node.getObject().getProperty().getItem();
        // FIXME itemType never used.
        ERepositoryObjectType itemType = ERepositoryObjectType.getItemType(item);
        IPath folderPath = WorkbenchUtils.getPath(node);
        String name = node
                .getObject()
                .getLabel()
                .concat("_") //$NON-NLS-1$
                .concat(node.getObject().getProperty().getVersion())
                .concat(".") //$NON-NLS-1$
                .concat(WorkbenchUtils.getItemExtendtion(item != null ? item.eClass().getClassifierID() : node.getObject()
                        .getProperty().getItem().eClass().getClassifierID()));
        IPath append = folderPath.append(new Path(name));
        return append;
    }

    /**
     * 
     * if it is TDQ_Data Profiling,TDQ_Libraries or metadata.
     * 
     * @param folderItem
     * @return
     */
    public static boolean isTDQOrMetadataRootFolder(FolderItem folderItem) {
        Project newProject = ProjectManager.getInstance().getCurrentProject();

        FolderHelper folderHelper = LocalFolderHelper.createInstance(newProject.getEmfProject(), ProxyRepositoryFactory
                .getInstance().getRepositoryContext().getUser());
        String path = folderHelper.getFullFolderPath(folderItem);
        if (path != null && (path.startsWith("TDQ") || path.startsWith("metadata"))) { //$NON-NLS-1$ //$NON-NLS-2$
            return true;
        }
        return false;
    }

    /**
     * 
     * Add qiongli: get the detail ERepositoryObjectType of folderItem.
     * 
     * @param folderItem
     * @return
     */
    public static ERepositoryObjectType getFolderContentType(FolderItem folderItem) {
        if (!folderItem.getType().equals(FolderType.SYSTEM_FOLDER_LITERAL)) {
            if (!(folderItem.getParent() instanceof FolderItem)) {
                return null; // appears only for a folder for expression builder !
            }
            return getFolderContentType((FolderItem) folderItem.getParent());
        }
        for (ERepositoryObjectType objectType : (ERepositoryObjectType[]) ERepositoryObjectType.values()) {
            String folderName;
            try {
                folderName = ERepositoryObjectType.getFolderName(objectType);
            } catch (Exception e) {
                // just catch exception to avoid the types who don't have folders
                continue;
            }
            if (folderName.contains("/")) { //$NON-NLS-1$
                String[] folders = folderName.split("/"); //$NON-NLS-1$
                FolderItem currentFolderItem = folderItem;
                boolean found = true;
                for (int i = folders.length - 1; i >= 0; i--) {
                    if (!currentFolderItem.getProperty().getLabel().equals(folders[i])) {
                        found = false;
                        break;
                    }
                    if (i > 0) {
                        if (!(currentFolderItem.getParent() instanceof FolderItem)) {
                            found = false;
                            break;
                        }
                        currentFolderItem = (FolderItem) currentFolderItem.getParent();
                    }
                }
                if (found) {
                    return objectType;
                }
            } else {
                if (folderName.equals(folderItem.getProperty().getLabel())) {
                    return objectType;
                }
            }
        }
        if (folderItem.getParent() instanceof FolderItem) {
            return getFolderContentType((FolderItem) folderItem.getParent());
        }
        return null;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toUpperCase().indexOf("LINUX") > -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().indexOf("WIN") > -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean isWinXP() {
        return System.getProperty("os.name").toUpperCase().indexOf("WINDOWS XP") > -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean isWin7() {
        return System.getProperty("os.name").toUpperCase().indexOf("WINDOWS 7") > -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toUpperCase().indexOf("MAC") > -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * judgement one string equals another string dependency on the OS's case sensitive type.
     * 
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equalsOS(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        } else {
            if (isLinux() || isMac()) {
                return str1.equals(str2);
            } else if (isWindows()) {
                return str1.equalsIgnoreCase(str2);
            } else {
                // other os
                return str1.equals(str2);
            }
        }
    }

    public static void refreshCurrentAnalysisEditor() {
        // Refresh current opened editors.
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        // MOD qiongli 2011-9-8 TDQ-3317.when focucs on DI perspective,don't refresh the open editors
        DQRespositoryView findView = (DQRespositoryView) activePage.findView(DQRespositoryView.ID);
        if (findView == null) {
            return;
        }
        IEditorReference[] editors = activePage.getEditorReferences();
        if (editors != null) {
            for (IEditorReference editorRef : editors) {
                if (editorRef.getId().equals(ANALYSIS_EDITOR_ID) || editorRef.getId().equals(CONNECTION_EDITOR_ID)) {
                    boolean isConfirm = MessageDialog.openConfirm(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            DefaultMessagesImpl.getString("WorkbenchUtils.ElementChange"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("WorkbenchUtils.RefreshCurrentEditor")); //$NON-NLS-1$
                    if (!isConfirm) {
                        return;
                    } else {
                        break;
                    }
                }
            }

            try {
                for (IEditorReference editorRef : editors) {
                    IEditorInput editorInput = editorRef.getEditorInput();
                    if (editorRef.getId().equals(ANALYSIS_EDITOR_ID)) {
                        if (editorInput instanceof AnalysisItemEditorInput) {
                            AnalysisItemEditorInput anaItemEditorInput = (AnalysisItemEditorInput) editorInput;
                            Analysis analysis = anaItemEditorInput.getTDQAnalysisItem().getAnalysis();
                            if (analysis.eIsProxy()) {
                                analysis = (Analysis) EObjectHelper.resolveObject(analysis);
                            }
                            RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
                            // close the editor
                            activePage.closeEditor(editorRef.getEditor(false), false);
                            // reopen the analysis
                            if (recursiveFind != null) {
                                new OpenItemEditorAction(recursiveFind).run();
                            }
                        }
                    } else if (editorRef.getId().equals(CONNECTION_EDITOR_ID)) {
                        if (editorInput instanceof ConnectionItemEditorInput) {
                            ConnectionItemEditorInput connItemEditorInput = (ConnectionItemEditorInput) editorInput;
                            Item item = connItemEditorInput.getItem();
                            if (item instanceof ConnectionItem) {
                                ConnectionItem connItem = (ConnectionItem) item;
                                Connection connection = connItem.getConnection();
                                if (connection.eIsProxy()) {
                                    connection = (Connection) EObjectHelper.resolveObject(connection);
                                }
                                RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(connection);
                                // close the editor
                                activePage.closeEditor(editorRef.getEditor(false), false);
                                // reopen the connection
                                if (recursiveFind != null) {
                                    new OpenItemEditorAction(recursiveFind).run();
                                }
                            }
                        }
                    }
                }
            } catch (PartInitException e) {
                log.error(e);
            }
        }
    }

    public static void refreshAnalysesNode() {
        IRepositoryNode analysesNode = RepositoryNodeHelper.getDataProfilingFolderNode(EResourceConstant.ANALYSIS);
        if (analysesNode != null) {
            CorePlugin.getDefault().refreshDQView(analysesNode);
        }
    }

    public static void refreshMetadataNode() {
        // database connection node
        IRepositoryNode dbNode = RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.DB_CONNECTIONS);
        if (dbNode != null) {
            CorePlugin.getDefault().refreshDQView(dbNode);
        }
        // delimited file connection node
        IRepositoryNode dfNode = RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.FILEDELIMITED);
        if (dfNode != null) {
            CorePlugin.getDefault().refreshDQView(dfNode);
        }
        // mdm connection node
        IRepositoryNode mdmNode = RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.MDM_CONNECTIONS);
        if (mdmNode != null) {
            CorePlugin.getDefault().refreshDQView(mdmNode);
        }
    }

    /**
     * 
     * DOC qiongli TDQ-3317:move this method from ReloadDatabaseAction. to this class .
     * 
     * @param oldDataProvider
     * @throws PartInitException
     */
    public static void impactExistingAnalyses(DataProvider oldDataProvider) throws PartInitException {
        EList<Dependency> clientDependencies = oldDataProvider.getSupplierDependency();
        List<Analysis> unsynedAnalyses = new ArrayList<Analysis>();
        for (Dependency dep : clientDependencies) {
            StringBuffer impactedAnaStr = new StringBuffer();
            for (ModelElement mod : dep.getClient()) {
                // MOD mzhao 2009-08-24 The dependencies include "Property" and "Analysis"
                if (!(mod instanceof Analysis)) {
                    continue;
                }
                Analysis ana = (Analysis) mod;
                unsynedAnalyses.add(ana);
                impactedAnaStr.append(ana.getName());
            }

            for (Analysis analysis : unsynedAnalyses) {
                // Reload.
                Resource eResource = analysis.eResource();
                if (eResource == null) {
                    continue;
                }

                EMFSharedResources.getInstance().unloadResource(eResource.getURI().toString());

                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                // MOD by zshen for bug 12316 to avoid null argument.
                Path path = new Path(analysis.getFileName() == null ? eResource.getURI().toPlatformString(false)
                        : analysis.getFileName());
                IFile file = root.getFile(path);
                analysis = (Analysis) AnaResourceFileHelper.getInstance().getModelElement(file);
                // MOD qiongli 2010-8-17,bug 14977
                if (analysis != null) {
                    eResource = analysis.eResource();
                    Map<EObject, Collection<Setting>> referenceMaps = EcoreUtil.UnresolvedProxyCrossReferencer.find(eResource);
                    Iterator<EObject> it = referenceMaps.keySet().iterator();
                    ModelElement eobj = null;
                    while (it.hasNext()) {
                        eobj = (ModelElement) it.next();
                        Collection<Setting> settings = referenceMaps.get(eobj);
                        for (Setting setting : settings) {
                            if (setting.getEObject() instanceof AnalysisContext) {
                                analysis.getContext().getAnalysedElements().remove(eobj);
                            } else if (setting.getEObject() instanceof Indicator) {
                                analysis.getResults().getIndicators().remove(setting.getEObject());
                            }
                        }

                    }
                    AnaResourceFileHelper.getInstance().save(analysis);
                }
            }
        }

        // Refresh current opened editors.
        refreshCurrentAnalysisEditor();
    }

    /**
     * Get viewPart with special partId. If the active page doesn't exsit, the method will return null; Else, it will
     * get the viewPart and focus it. if the viewPart closed, it will be opened.
     * 
     * @param viewId the identifier of viewPart
     * @return
     */
    public static IViewPart getAndOpenView(String viewId) {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return null;
        }
        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
        if (page == null) {
            return null;
        }
        IViewPart part = page.findView(viewId);
        if (part == null) {
            try {
                part = page.showView(viewId);
            } catch (Exception e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
        } else {
            page.bringToTop(part);
        }
        return part;
    }
}

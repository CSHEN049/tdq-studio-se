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
package org.talend.dataprofiler.rcp;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.SystemException;
import org.talend.core.ICoreService;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.ColumnNameChanged;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.repository.model.IRepositoryNode;

/**
 * 
 * @author zshen
 * 
 * This class is needed by ProxyRepositoryFactory, so if simple remove it will effect startup for TOP. If you came to
 * ProxyRepositoryFactory class you will find coreService variable and it need a service (which implements ICoreService)
 * to initialize itself. We have a CoreService calss in the org.talend.core plugin but in TOP we can't get it from
 * org.talend.core(TOP don't contain it). So we need another one to instead of it. And ICoreService is main responsible
 * to check Job name and logon project, logon project we have be done by myself and we don't have job on the TOP. So
 * TopService only is empty class.
 */
public class TopService implements ICoreService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#addWorkspaceTaskDone(java.lang.String)
     */
    public void addWorkspaceTaskDone(String task) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#checkJob(java.lang.String)
     */
    public boolean checkJob(String name) throws BusinessException {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#componentsReset()
     */
    public void componentsReset() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#createStatsLogAndImplicitParamter(org.talend.core.model.general.Project)
     */
    public void createStatsLogAndImplicitParamter(Project project) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#deleteAllJobs(boolean)
     */
    public void deleteAllJobs(boolean fromPluginModel) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#deleteRoutinefile(org.talend.core.model.repository.IRepositoryViewObject)
     */
    public void deleteRoutinefile(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#filterSpecialChar(java.lang.String)
     */
    public String filterSpecialChar(String input) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getColumnNameChanged(org.talend.core.model.metadata.IMetadataTable,
     * org.talend.core.model.metadata.IMetadataTable)
     */
    public List<ColumnNameChanged> getColumnNameChanged(IMetadataTable oldTable, IMetadataTable newTable) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getContextFileNameForPerl(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    public String getContextFileNameForPerl(String projectName, String jobName, String version, String context) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getContextFlagFromQueryUtils()
     */
    public boolean getContextFlagFromQueryUtils() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getImageWithDocExt(java.lang.String)
     */
    public Image getImageWithDocExt(String extension) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getImageWithSpecial(org.eclipse.swt.graphics.Image)
     */
    public ImageDescriptor getImageWithSpecial(Image source) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getJavaJobFolderName(java.lang.String, java.lang.String)
     */
    public String getJavaJobFolderName(String jobName, String version) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getJavaProjectFolderName(org.talend.core.model.properties.Item)
     */
    public String getJavaProjectFolderName(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getLanTypeString()
     */
    public String getLanTypeString() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getLastUser()
     */
    public String getLastUser() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getNewMetadataColumns(org.talend.core.model.metadata.IMetadataTable,
     * org.talend.core.model.metadata.IMetadataTable)
     */
    public List<ColumnNameChanged> getNewMetadataColumns(IMetadataTable oldTable, IMetadataTable newTable) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.ICoreService#getParameterUNIQUENAME(org.talend.designer.core.model.utils.emf.talendfile.NodeType)
     */
    public String getParameterUNIQUENAME(NodeType node) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRemoveMetadataColumns(org.talend.core.model.metadata.IMetadataTable,
     * org.talend.core.model.metadata.IMetadataTable)
     */
    public List<ColumnNameChanged> getRemoveMetadataColumns(IMetadataTable oldTable, IMetadataTable newTable) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRoot()
     */
    public IRepositoryNode getRoot() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRootProjectNameForPerl(org.talend.core.model.properties.Item)
     */
    public String getRootProjectNameForPerl(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getRoutineAndJars()
     */
    public Map<String, List<URI>> getRoutineAndJars() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getSpecificResourceInJavaProject(org.eclipse.core.runtime.IPath)
     */
    public IResource getSpecificResourceInJavaProject(IPath path) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getSpecificResourceInPerlProject(org.eclipse.core.runtime.IPath)
     */
    public IResource getSpecificResourceInPerlProject(IPath path) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#getTemplateString()
     */
    public String getTemplateString() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#initializeComponents(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void initializeComponents(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#initializeForTalendStartupJob()
     */
    public void initializeForTalendStartupJob() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#initializeTemplates()
     */
    public Job initializeTemplates() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#isAlreadyBuilt(org.talend.core.model.general.Project)
     */
    public boolean isAlreadyBuilt(Project project) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#isContainContextParam(java.lang.String)
     */
    public boolean isContainContextParam(String code) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#isKeyword(java.lang.String)
     */
    public boolean isKeyword(String word) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#readWorkspaceTasksDone()
     */
    public List<String> readWorkspaceTasksDone() {
        // TODO Auto-generated method stub
        return new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#removeItemRelations(org.talend.core.model.properties.Item)
     */
    public void removeItemRelations(Item item) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#removeJobLaunch(org.talend.core.model.repository.IRepositoryViewObject)
     */
    public void removeJobLaunch(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#setFlagForQueryUtils(boolean)
     */
    public void setFlagForQueryUtils(boolean flag) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncAllRoutines()
     */
    public void syncAllRoutines() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncAllRules()
     */
    public void syncAllRules() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncLibraries(org.eclipse.core.runtime.IProgressMonitor[])
     */
    public void syncLibraries(IProgressMonitor... monitorWrap) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#validateValueForDBType(java.lang.String)
     */
    public String validateValueForDBType(String columnName) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#synchronizeMapptingXML()
     */
    public void synchronizeMapptingXML() {
        // I do not know what the method is supposed to be doing to I do nothing.
    }

    public void synchronizeSapLib() {
        // TODO Auto-generated method stub

    }

    public IPreferenceStore getPreferenceStore() {
        // MOD qiongli 2011-4-11.bug 20115.
        return CorePlugin.getDefault().getPreferenceStore();
    }

    public boolean isOpenedItemInEditor(IRepositoryViewObject object) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#updatePalette()
     */
    public void updatePalette() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.core.ICoreService#resetUniservLibraries()
     */
    public void resetUniservLibraries() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#deleteBeanfile(org.talend.core.model.repository.IRepositoryViewObject)
     */
    public void deleteBeanfile(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#syncAllBeans()
     */
    public void syncAllBeans() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.ICoreService#convert(org.talend.core.model.metadata.builder.connection.MetadataTable)
     */
    public IMetadataTable convert(MetadataTable originalTable) {
        // TODO Auto-generated method stub
        return null;
    }

    public MenuManager[] getRepositoryContextualsActionGroups() {
        return null;
    }

}

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
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemExportWriter implements IExportWriter {

    private static Logger log = Logger.getLogger(FileSystemExportWriter.class);

    private IPath basePath;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#populate(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], boolean)
     */
    public ItemRecord[] populate(ItemRecord[] elements, boolean checkExisted) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#mapping(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * )
     */
    public Map<IPath, IPath> mapping(ItemRecord record) {
        Map<IPath, IPath> toExportMap = new HashMap<IPath, IPath>();

        // item
        IPath itemResPath = new Path(record.getFile().getAbsolutePath());
        IPath itemDesPath = record.getFullPath();

        // property
        IPath propResPath = record.getPropertyPath();
        IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        toExportMap.put(itemResPath, itemDesPath);
        if (!propResPath.toFile().exists()) {
            return toExportMap;
        }
        toExportMap.put(propResPath, propDesPath);

        Property property = record.getProperty();
        EResourceConstant typedConstant = EResourceConstant.getTypedConstant(property.getItem());
        if (typedConstant != null && typedConstant == EResourceConstant.MDM_CONNECTIONS) {
            ConnectionItem item = (ConnectionItem) property.getItem();
            Connection connection = item.getConnection();
            List<TdXmlSchema> tdXmlDocumentList = ConnectionHelper.getTdXmlDocument(connection);
            for (TdXmlSchema schema : tdXmlDocumentList) {
                IPath srcPath = itemResPath.removeLastSegments(1).append(schema.getXsdFilePath());
                if (!srcPath.toFile().exists()) {
                    log.error("The file : " + srcPath.toFile() + " can't be found.This will make MDMConnection useless ");//$NON-NLS-1$ //$NON-NLS-2$ 
                    break;
                }
                IPath desPath = itemDesPath.removeLastSegments(1).append(new Path(schema.getXsdFilePath()));
                toExportMap.put(srcPath, desPath);
            }
        }
        return toExportMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], org.eclipse.core.runtime.IProgressMonitor)
     */
    public void write(ItemRecord[] records, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        try {
            for (ItemRecord record : records) {

                if (monitor.isCanceled()) {
                    break;
                }

                Map<IPath, IPath> toImportMap = mapping(record);

                monitor.subTask("Exporting " + record.getName());//$NON-NLS-1$ 

                if (record.isValid()) {
                    log.info("Exporting " + record.getFile().getAbsolutePath());//$NON-NLS-1$ 

                    for (IPath resPath : toImportMap.keySet()) {
                        IPath desPath = toImportMap.get(resPath);
                        synchronized (ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()
                                .getResourceManager().resourceSet) {
                            write(resPath, desPath);
                        }
                    }
                } else {
                    for (String error : record.getErrors()) {
                        log.error(error);
                    }
                }

                monitor.worked(1);
            }

            finish(records);

        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.eclipse.core.runtime.IPath,
     * org.eclipse.core.runtime.IPath)
     */
    public void write(IPath resPath, IPath desPath) throws IOException, CoreException {
        File resFile = resPath.toFile();
        File desFile = this.basePath.append(desPath).toFile();

        if (resFile.exists()) {
            FilesUtils.copyFile(resFile, desFile);
        } else {
            log.warn("Export failed! " + resFile.getAbsolutePath() + " is not existed");//$NON-NLS-1$ //$NON-NLS-2$ 
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#finish(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [])
     */
    public void finish(ItemRecord[] records) throws IOException, CoreException {
        IFile projFile = ResourceManager.getRootProject().getFile("talend.project");//$NON-NLS-1$ 
        writeSysFile(projFile);

        IFile definitonFile = DefinitionHandler.getTalendDefinitionFile();
        writeSysFile(definitonFile);

        IFile versionFile = WorkspaceVersionHelper.getVersionFile();
        writeSysFile(versionFile);

        ItemRecord.clear();
    }

    /**
     * DOC bZhou Comment method "writeSysFile".
     * 
     * @param file
     * @throws IOException
     * @throws CoreException
     */
    private void writeSysFile(IFile file) throws IOException, CoreException {
        if (file.exists()) {
            write(file.getLocation(), file.getFullPath().makeRelative());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#computeInput(org.eclipse.core.runtime.IPath)
     */
    public ItemRecord computeInput(IPath path) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        File file = path == null ? ResourceManager.getRootProject().getLocation().toFile() : workspace.getRoot().getFolder(path)
                .getLocation().toFile();

        return new ItemRecord(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#setBasePath(org.eclipse.core.runtime.IPath)
     */
    public void setBasePath(IPath path) {
        this.basePath = path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#getBasePath()
     */
    public IPath getBasePath() {
        return this.basePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#check()
     */
    public List<String> check() {
        List<String> errors = new ArrayList<String>();

        if (basePath == null || !basePath.toFile().exists()) {
            errors.add("The root directory does not exist");//$NON-NLS-1$ 
        }

        return errors;
    }
}

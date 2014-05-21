// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.dbproduct.DriverManager;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.DatabaseStructureView;
import net.sourceforge.sqlexplorer.util.AliasAndManaDriverHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.classloader.DynamicClassLoader;
import org.talend.core.database.conn.version.EDatabaseVersion4Drivers;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;
import org.talend.dq.helper.PropertyHelper;
import org.talend.librariesmanager.prefs.LibrariesManagerUtils;
import org.talend.metadata.managment.hive.HiveClassLoaderFactory;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CWMPlugin extends Plugin {

    private static Logger log = Logger.getLogger(CWMPlugin.class);

    private static CWMPlugin self;

    public CWMPlugin() {
        super();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        self = this;
        initPreferences(self);
    }

    /**
     * DOC xqliu Comment method "initPreferences".
     * 
     * @param cwm
     */
    private void initPreferences(CWMPlugin cwm) {
        IEclipsePreferences prefs = new DefaultScope().getNode(cwm.getBundle().getSymbolicName());
        prefs.putBoolean(PluginConstant.CONNECTION_TIMEOUT, false);
        prefs.putBoolean(PluginConstant.FILTER_TABLE_VIEW_COLUMN, true);
        PlatformUI.getPreferenceStore().setDefault(AnalysisThreadMemoryChangeNotifier.ANALYSIS_AUTOMATIC_MEMORY_CONTROL, false);
    }

    /**
     * DOC bZhou Comment method "getDefault".
     * 
     * @return
     */
    public static CWMPlugin getDefault() {
        return self;
    }

    /**
     * DOC bZhou Comment method "addConnetionAliasToSQLPlugin".
     * 
     * @param dataproviders
     */
    public void addConnetionAliasToSQLPlugin(ModelElement... dataproviders) {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();
        DriverManager driverManager = sqlPlugin.getDriverModel();

        List<String> tdqSupportDBType = MetadataConnectionUtils.getTDQSupportDBTemplate();
        // if all dataproviders are not supported on DQ side,don't save files SQLAliases.xml and
        // SQLDrivers.xml.Otherwise,save it.
        AliasAndManaDriverHelper aliasManaDriverHelper = AliasAndManaDriverHelper.getInstance();
        for (ModelElement dataProvider : dataproviders) {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataProvider);
                // MOD bug mzhao filter the other connections except database connection.
                if (connection != null && connection instanceof DatabaseConnection) {

                    // TDQ-8379 do nothing if the database type isn't supproted on DQ side.
                    DatabaseConnection dbConn = ((DatabaseConnection) connection);
                    String databaseType = dbConn.getDatabaseType();
                    if (!tdqSupportDBType.contains(databaseType)) {
                        continue;
                    }
                    // only new Alias when it is not in aliasManager
                    Alias alias = aliasManager.getAlias(dataProvider.getName());
                    if (alias == null) {
                        alias = new Alias(dataProvider.getName());

                        String user = JavaSqlFactory.getUsername(connection);
                        // MOD gdbu 2011-3-17 bug 19539
                        String password = JavaSqlFactory.getPassword(connection);
                        // ~19539

                        // user should not be null
                        user = user == null ? "" : user; //$NON-NLS-1$
                        // password should not be null
                        password = password == null ? "" : password; //$NON-NLS-1$

                        // MOD scorreia 2010-07-24 set empty string instead of null password so that database xml file
                        // is serialized correctly.
                        assert user != null;
                        assert password != null;

                        String url = JavaSqlFactory.getURL(connection);

                        User previousUser = new User(user, password);
                        previousUser.setDatabaseConnection(dbConn);
                        alias.setDefaultUser(previousUser);

                        alias.setAutoLogon(false);
                        alias.setConnectAtStartup(true);
                        alias.setUrl(url);

                        ManagedDriver manDr = aliasManaDriverHelper.getManaDriverByConnection(dbConn);
                        if (manDr == null) {
                            manDr = aliasManaDriverHelper.createNewManagerDriver(dbConn);
                            driverManager.addDriver(manDr);
                        } else if (!manDr.isDriverClassLoaded()) {
                            this.loadDriverByLibManageSystem(dbConn);
                        }

                        if (manDr != null) {
                            alias.setDriver(manDr);
                        }
                    }
                    if (!aliasManager.contains(alias) && alias.getName() != null) {
                        aliasManager.addAlias(alias);
                    }

                    // Add yyi 2010-09-15 14549: hide connections in SQL Explorer when a connection is moved to the
                    // trash bin.
                    // MOD Qiongli TDQ-6166 just put once for every Alias
                    if (sqlPlugin.getPropertyFile().get(alias) == null) {
                        sqlPlugin.getPropertyFile().put(alias, PropertyHelper.getPropertyFile(dataProvider));
                    }
                    aliasManager.modelChanged();

                }
            } catch (Exception e) { // MOD scorreia 2010-07-24 catch all exceptions
                log.error(e, e);
            }
        }

    }

    /**
     * 
     * DOC qiongli Comment method "updateConnetionAliasByName".
     * 
     * @param connection
     * @param aliasName
     */
    public void updateConnetionAliasByName(Connection connection, String aliasName) {
        if (connection == null || aliasName == null) {
            return;
        }
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        // if the ctabItem is open,close it.
        IWorkbenchPage page = sqlPlugin.getActivePage();
        if (page != null) {
            DatabaseStructureView view = (DatabaseStructureView) page.findView(DatabaseStructureView.class.getName());
            if (view != null) {
                view.closeCurrentCabItem(aliasName);
            }
        } else {
            // print the error log when page is null(command line environment or other cases).
            log.error("Workebench page is null!"); //$NON-NLS-1$
        }

        AliasManager aliasManager = sqlPlugin.getAliasManager();
        Alias alias = aliasManager.getAlias(aliasName);
        if (alias != null) {
            try {
                aliasManager.removeAlias(aliasName);
                // aliasManager.saveAliases();
                // aliasManager.modelChanged();
                addConnetionAliasToSQLPlugin(connection);

            } catch (Exception e) {
                log.error(e, e);
            }
        }

    }

    /**
     * 
     * update ManagedDriver driver jars.
     * 
     * @param connection
     */
    public void loadDriverByLibManageSystem(DatabaseConnection connection) {
        String dbType = connection.getDatabaseType();
        String dbVersion = connection.getDbVersionString();
        String driverClassName = JavaSqlFactory.getDriverClass(connection);
        if (ConnectionHelper.isHive(connection)) {
            loadDriverForHive(connection, driverClassName);
        } else {
            loadDriverByLibManageSystem(dbType, dbVersion, driverClassName);
        }
    }

    /**
     * 
     * Load the driver by lib management system , which will configure the SQL Explorer driver classpath from xml.
     * 
     * @param dbType
     * @param dbVersion
     * @param driverClassName
     */
    public void loadDriverByLibManageSystem(String dbType, String dbVersion, String driverClassName) {
        if (dbType == null || driverClassName == null) {
            return;
        }
        DriverManager driverManager = SQLExplorerPlugin.getDefault().getDriverModel();
        AliasAndManaDriverHelper aliasManaHelper = AliasAndManaDriverHelper.getInstance();
        String manaDriverId = aliasManaHelper.joinManagedDriverId(dbType, driverClassName, dbVersion);
        ManagedDriver manDr = driverManager.getDriver(manaDriverId);
        if (manDr != null && !manDr.isDriverClassLoaded()) {
            // find driver jars from 'temp\dbWizard', prefrence page or installation path 'lib\java',
            // "librariesIndex.xml".
            try {
                List<String> jarNames = EDatabaseVersion4Drivers.getDrivers(dbType, dbVersion);
                LinkedList<String> driverJarRealPaths = aliasManaHelper.getDriverJarRealPaths(jarNames);
                if (!driverJarRealPaths.isEmpty()) {
                    manDr.getJars().clear();
                    manDr.getJars().addAll(driverJarRealPaths);
                }

                manDr.registerSQLDriver(dbType, dbVersion);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * 
     * find all jar pathes by jar names.
     * 
     * @param root
     * @param jarNames
     * @return if return an empty Set,indicate that it find failed.
     * @throws MalformedURLException
     */
    public Set<String> findAllJarPath(File root, List<String> jarNames) {
        Set<String> jarPathes = new HashSet<String>();
        if (!root.exists() || jarNames == null || jarNames.isEmpty()) {
            return jarPathes;
        }
        boolean allIsOK = true;
        try {
            for (String jarName : jarNames) {
                List<File> jarFiles = FilesUtils.getJarFilesFromFolder(root, jarName);
                if (jarFiles.isEmpty()) {
                    allIsOK = false;
                    break;
                }
                for (File file : jarFiles) {
                    jarPathes.add(file.getPath());
                }
            }
        } catch (MalformedURLException e) {
            log.error(e);
        }
        if (!allIsOK) {
            jarPathes.clear();
        }
        return jarPathes;
    }

    /**
     * DOC bZhou Comment method "removeAliasInSQLExplorer".
     * 
     * @param dataproviders
     */
    public void removeAliasInSQLExplorer(DataProvider... dataproviders) {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();

        DatabaseStructureView dsView = sqlPlugin.getDatabaseStructureView();
        // MOD qiongli 2012-11-12 TDQ-6166,only load aliases from file when AliasManager'Aliases is empty.should remove
        // alias from propertyFile map at the same time.
        try {
            Collection<Alias> aliases = aliasManager.getAliases();
            if (aliases.isEmpty()) {
                return;
            }
            for (DataProvider dataProvider : dataproviders) {
                String aliasName = dataProvider.getName();
                if (null == aliasName) {
                    continue;
                }
                Alias alias = aliasManager.getAlias(aliasName);
                if (alias != null) {
                    sqlPlugin.getPropertyFile().remove(alias);
                    aliasManager.removeAlias(aliasName);
                }
                // if the ctabItem is open,close it.
                if (dsView != null) {
                    dsView.closeCurrentCabItem(aliasName);
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        aliasManager.modelChanged();
    }

    /**
     * 
     * when you start TOP ,the 'lib/java' dosen't exist,should create it.
     */
    public void createLibFolderIfNotExist() {
        String installLocation = LibrariesManagerUtils.getLibrariesPath(ECodeLanguage.JAVA);
        File libFile = new File(installLocation);
        if (!libFile.exists()) {
            org.talend.utils.io.FilesUtils.createFoldersIfNotExists(installLocation, false);
        }
    }

    /**
     * TDDQ-8113 load hive drive by DynamicClassLoader,then set the attribute for Hive ManagedDriver.
     * 
     * @param connection
     * @param driverClassName
     */
    private void loadDriverForHive(DatabaseConnection connection, String driverClassName) {
        DriverManager driverManager = SQLExplorerPlugin.getDefault().getDriverModel();
        String id = AliasAndManaDriverHelper.getInstance().joinManagedDriverId(connection);
        ManagedDriver manDr = driverManager.getDriver(id);
        IMetadataConnection metadataConnection = ConvertionHelper.convert(connection);
        ClassLoader classLoader = HiveClassLoaderFactory.getInstance().getClassLoader(metadataConnection);
        if (classLoader != null && classLoader instanceof DynamicClassLoader) {
            DynamicClassLoader dynClassLoader = (DynamicClassLoader) classLoader;
            String libStorePath = dynClassLoader.getLibStorePath();
            File libFolder = new File(libStorePath);
            if (libFolder.exists()) {
                List<String> relaPathLs = new ArrayList<String>();
                relaPathLs.addAll(dynClassLoader.getLibraries());
                Set<String> findAllJarPath = findAllJarPath(libFolder, relaPathLs);
                if (!findAllJarPath.isEmpty()) {
                    manDr.getJars().addAll(findAllJarPath);
                    try {
                        manDr.registerHiveSQLDriver(connection);
                    } catch (ClassNotFoundException e) {
                        log.error(e);
                    } catch (InstantiationException e) {
                        log.error(e);
                    } catch (IllegalAccessException e) {
                        log.error(e);
                    }

                }
            }
        }
    }

}

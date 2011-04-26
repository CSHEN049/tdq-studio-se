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
package org.talend.dataprofiler.core.migration.impl;

import java.util.LinkedList;

import net.sourceforge.sqlexplorer.EDriverName;
import net.sourceforge.sqlexplorer.dbproduct.DriverManager;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.AProjectTask;
import org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask.MigrationTaskType;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class UpdateSQLExplorerDriversTask extends AProjectTask {

    private static Logger log = Logger.getLogger(UpdateSQLExplorerDriversTask.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        DriverManager driverModel = sqlPlugin.getDriverModel();

        for (ManagedDriver mand : driverModel.getDrivers()) {
            for (EDriverName supportDBUrlType : EDriverName.values()) {

                if (mand.getId().equals(supportDBUrlType.getSqlEid())) {
                    LinkedList<String> jars = supportDBUrlType.getJars();
                    if (!jars.isEmpty()) {
                        mand.setJars(jars);
                        mand.setDriverClassName(supportDBUrlType.getDbDriver());
                        mand.registerSQLDriver();

                        break;
                    }
                    log.warn(DefaultMessagesImpl.getString("UpdateSQLExplorerDriversTask_NoJar", supportDBUrlType.getDBKey())); //$NON-NLS-1$
                }
            }
        }

        driverModel.saveDrivers();

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.SQLEXPLORER;
    }
}

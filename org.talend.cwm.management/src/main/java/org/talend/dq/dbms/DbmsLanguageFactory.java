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
package org.talend.dq.dbms;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlStore;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.model.metadata.builder.util.DatabaseConstant;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.SoftwareSystemManager;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.utils.ProductVersion;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.SoftwareSystem;

/**
 * @author scorreia
 * 
 * Factory for the creation of DbmsLanguage objects.
 */
public final class DbmsLanguageFactory {

    private static Logger log = Logger.getLogger(DbmsLanguageFactory.class);

    private DbmsLanguageFactory() {
        // avoid instantiation
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param dataManager a data manager used for initializing the correct language in the created DbmsLanguage
     * @return a new DbmsLanguage even if the data manager did not allow to get the correct language
     */
    public static DbmsLanguage createDbmsLanguage(DataManager dataManager) {
        DbmsLanguage dbmsLanguage = new DbmsLanguage();
        if (dataManager == null) {
            return dbmsLanguage;
        }
        Connection dataprovider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataManager);
        if (dataprovider == null) {
            return dbmsLanguage;
        }

        TdSoftwareSystem softwareSystem = SoftwareSystemManager.getInstance().getSoftwareSystem(dataprovider);
        boolean isMdm = ConnectionUtils.isMdmConnection(dataprovider);
        // MOD qiongli 2011-1-11 feature 16796.handle the delimited file
        boolean isDelimitedFile = ConnectionUtils.isDelimitedFileConnection(dataprovider);
        if (softwareSystem != null || isMdm) {
            final String dbmsSubtype = isMdm ? DbmsLanguage.MDM : softwareSystem.getSubtype();
            if (log.isDebugEnabled()) {
                log.debug("Software system subtype (Database type): " + dbmsSubtype); //$NON-NLS-1$
            }
            if (StringUtils.isNotBlank(dbmsSubtype)) {
                String version = isMdm ? DatabaseConstant.MDM_VERSION : softwareSystem.getVersion();
                dbmsLanguage = createDbmsLanguage(dbmsSubtype, version);
            }
        } else if (isDelimitedFile) {
            dbmsLanguage = createDbmsLanguage(DbmsLanguage.DELIMITEDFILE, PluginConstant.EMPTY_STRING);
        }
        String identifierQuoteString = ConnectionHelper.getIdentifierQuoteString(dataprovider);
        if (identifierQuoteString == null || identifierQuoteString.length() == 0) {
            // MOD scorreia 2009-11-24 check for null because in some cases (DB2 z/OS and TOP 3.2.2), the identifier
            // quote was stored as null.
            // given data provider has not stored the identifier quote (version 1.1.0 of TOP)
            // we must set it by hand
            identifierQuoteString = dbmsLanguage.getHardCodedQuoteIdentifier();
        }
        dbmsLanguage.setDbQuoteString(identifierQuoteString);
        return dbmsLanguage;
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param dbmsSubtype
     * @return the appropriate DbmsLanguage
     */
    private static DbmsLanguage createDbmsLanguage(String dbmsSubtype, String databaseVersion) {
        ProductVersion dbVersion = ProductVersion.fromString(databaseVersion, true);
        if (isMySQL(dbmsSubtype)) {
            return new MySQLDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isOracle(dbmsSubtype)) {
            return new OracleDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isDB2(dbmsSubtype)) {
            return new DB2DbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isAS400(dbmsSubtype)) {
            return new AS400DbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isMSSQL(dbmsSubtype)) {
            return new MSSqlDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isPostgresql(dbmsSubtype)) {
            return new PostgresqlDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isSybase(dbmsSubtype)) {
            return new SybaseASEDbmsLanguage(dbVersion);
        }
        if (isSQLite(dbmsSubtype)) {
            return new SQLiteDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isTeradata(dbmsSubtype)) {
            return new TeradataDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isIngres(dbmsSubtype)) {
            return new IngresDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isMdm(dbmsSubtype)) {
            return new MdmDbmsLanguage(dbmsSubtype, dbVersion);
        }
        if (isDelimitedFile(dbmsSubtype)) {
            return new DelimitedFileLanguage(dbmsSubtype, dbVersion);
        }
        // MOD zshen fixed bug 11005: SQL syntax error for all analysis on Informix databases in Talend Open Profiler
        if (isInfomix(dbmsSubtype)) {
            return new InfomixDbmsLanguage(dbmsSubtype, dbVersion);
        }// ~11005
        return new DbmsLanguage(dbmsSubtype, dbVersion);
    }

    public static DbmsLanguage createDbmsLanguage(String dataType) {
        SupportDBUrlType dbType = SupportDBUrlStore.getInstance().getDBUrlType(dataType);
        return createDbmsLanguage(dbType);
    }

    /**
     * DOC jet Comment method "getDbmsLanguage".
     * 
     * @param dbType
     * @return
     */
    private static DbmsLanguage createDbmsLanguage(SupportDBUrlType dbType) {

        DbmsLanguage result = null;

        if (dbType == null) {
            return new DbmsLanguage();
        }
        //MOD qiongli 2011-4-18 bug 16723,data cleansing
        result=createDbmsLanguage(dbType.getLanguage(),PluginConstant.EMPTY_STRING);
        return result;
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param connection a connection (must be open)
     * @return the appropriate DbmsLanguage or a default one if something failed with the connection.
     */
    public static DbmsLanguage createDbmsLanguage(java.sql.Connection connection) {
        assert connection != null;
        // MOD xqliu 2009-07-13 bug 7888
        String databaseProductName = null;
        try {
            databaseProductName = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection).getDatabaseProductName();
            databaseProductName = databaseProductName == null ? PluginConstant.EMPTY_STRING : databaseProductName;
            String databaseProductVersion = null;
            try {
                databaseProductVersion = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection)
                        .getDatabaseProductVersion();
                databaseProductVersion = databaseProductVersion == null ? "0" : databaseProductVersion; //$NON-NLS-1$
            } catch (Exception e) {
                log.warn(Messages.getString("DbmsLanguageFactory.RetrieveVerSionException", databaseProductName), e);//$NON-NLS-1$
            }
            DbmsLanguage dbmsLanguage = createDbmsLanguage(databaseProductName, databaseProductVersion);
            dbmsLanguage.setDbQuoteString(org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(connection)
                    .getIdentifierQuoteString());
            return dbmsLanguage;
        } catch (SQLException e) {
            log.warn(Messages.getString("DbmsLanguageFactory.RetrieveInfoException", e), e);//$NON-NLS-1$
            return new DbmsLanguage();
        }
    }

    private static boolean isMySQL(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MYSQL, dbms);
    }

    public static boolean isOracle(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.ORACLE, dbms);
    }

    private static boolean isPostgresql(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.POSTGRESQL, dbms);
    }

    private static boolean isMSSQL(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MSSQL, dbms);
    }

    private static boolean isDB2(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.DB2, dbms);
    }

    private static boolean isAS400(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.AS400, dbms);
    }

    private static boolean isSybase(String dbms) {
        return ConnectionUtils.isSybaseeDBProducts(dbms);
    }

    private static boolean isSQLite(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.SQLITE3, dbms);
    }

    private static boolean isTeradata(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.TERADATA, dbms);
    }

    private static boolean isIngres(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.INGRES, dbms);
    }

    private static boolean isMdm(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.MDM, dbms);
    }

    private static boolean isDelimitedFile(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.DELIMITEDFILE, dbms);
    }

    // MOD zshen 11005: SQL syntax error for all analysis on Informix databases in Talend Open Profiler
    public static boolean isInfomix(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.INFOMIX, dbms);
    }

    // ~11005

    public static boolean isAllDatabaseType(String dbms) {
        return compareDbmsLanguage(DbmsLanguage.SQL, dbms);
    }

    public static boolean compareDbmsLanguage(String lang1, String lang2) {
        if (lang1 == null || lang2 == null) {
            return false;
        }
        // MOD mzhao 2010-08-02 bug 14464, for AS400
        if (StringUtils.contains(lang1, DbmsLanguage.AS400) && StringUtils.contains(StringUtils.upperCase(lang2), lang1)) {
            return true;
        }
        // MOD 2008-08-04 scorreia: for DB2 database, dbName can be "DB2/NT" or "DB2/6000" or "DB2"...
        if (lang1.startsWith(DbmsLanguage.DB2)) {
            if (StringUtils.contains(lang2, DbmsLanguage.AS400)) {
                return false;
            }
            return StringUtils.upperCase(lang1).startsWith(StringUtils.upperCase(lang2))
                    || StringUtils.upperCase(lang2).startsWith(StringUtils.upperCase(lang1));
        } else
        // MOD 2010-01-26 zshen: for informix database, dbName can be "informix" or "informix Dynamic Server"
        if (lang1.startsWith(DbmsLanguage.INFOMIX)) {
            return StringUtils.upperCase(lang1).startsWith(StringUtils.upperCase(lang2))
                    || StringUtils.upperCase(lang2).startsWith(StringUtils.upperCase(lang1));
        }
        return StringUtils.equalsIgnoreCase(lang1, lang2);
    }

    /**
     * Method "createDbmsLanguage".
     * 
     * @param analysis
     * @return the dbms language associated to the connection of the given analysis or a default one.
     */
    public static DbmsLanguage createDbmsLanguage(Analysis analysis) {
        final AnalysisContext context = analysis.getContext();
        if (context != null) {
            final DataManager dm = context.getConnection();
            if (dm != null) {
                return createDbmsLanguage(dm);
            }
        }
        return new DbmsLanguage();
    }

    public static DbmsLanguage createDbmsLanguage(SoftwareSystem softwareSystem) {
        if (softwareSystem != null) {
            return createDbmsLanguage(softwareSystem.getName(), softwareSystem.getVersion());
        }

        return new DbmsLanguage();
    }
}

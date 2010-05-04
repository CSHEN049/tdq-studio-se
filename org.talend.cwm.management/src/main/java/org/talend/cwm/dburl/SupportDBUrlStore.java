// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.dburl;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.dq.PluginConstant;

/**
 * This class store the all the support database connection url.
 * 
 */
public final class SupportDBUrlStore {

    protected static Logger log = Logger.getLogger(SupportDBUrlStore.class);

    private static final Properties PROP = new Properties();

    private static Map<String, SupportDBUrlType> supportDBUrlMap = new HashMap<String, SupportDBUrlType>();

    // MOD mzhao bug 12313, 2010-04-02 There is not dbType in prv files before 4.0 release, here use driver class name
    // to get db type.
    private static Map<String, SupportDBUrlType> supportDiverNameDBUrlMap = new HashMap<String, SupportDBUrlType>();

    private static SupportDBUrlStore dbUrlStore = new SupportDBUrlStore();

    public static SupportDBUrlStore getInstance() {
        return dbUrlStore;
    }

    private SupportDBUrlStore() {
        loadProperties();
        fillDbUrlMap();
    }

    private void loadProperties() {
        InputStream in = SupportDBUrlStore.class.getResourceAsStream("dburl.properties"); //$NON-NLS-1$
        try {
            PROP.load(in);
            in.close();
        } catch (IOException e) {
            log.error(e, e);
        }
    }

    private void fillDbUrlMap() {
        // PTODO scorreia choose here which Database types to enable
        supportDBUrlMap.put(SupportDBUrlType.ODBCDEFAULTURL.getDBKey(), SupportDBUrlType.ODBCDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey(), SupportDBUrlType.MYSQLDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getDBKey(), SupportDBUrlType.ORACLEWITHSIDDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.ORACLEWITHSERVICENAMEDEFAULTURL.getDBKey(),
                SupportDBUrlType.ORACLEWITHSERVICENAMEDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.MSSQLDEFAULTURL.getDBKey(), SupportDBUrlType.MSSQLDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.MSSQL2008URL.getDBKey(), SupportDBUrlType.MSSQL2008URL);
        supportDBUrlMap.put(SupportDBUrlType.DB2DEFAULTURL.getDBKey(), SupportDBUrlType.DB2DEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.DB2ZOSDEFAULTURL.getDBKey(), SupportDBUrlType.DB2ZOSDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.POSTGRESQLEFAULTURL.getDBKey(), SupportDBUrlType.POSTGRESQLEFAULTURL);
        // supportDBUrlMap.put(SupportDBUrlType.INTERBASEDEFAULTURL.getDBKey(), SupportDBUrlType.INTERBASEDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.SYBASEDEFAULTURL.getDBKey(), SupportDBUrlType.SYBASEDEFAULTURL);
        // supportDBUrlMap.put(SupportDBUrlType.INFORMIXDEFAULTURL.getDBKey(), SupportDBUrlType.INFORMIXDEFAULTURL);
        // supportDBUrlMap.put(SupportDBUrlType.FIREBIRDDEFAULTURL.getDBKey(), SupportDBUrlType.FIREBIRDDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.INGRESDEFAULTURL.getDBKey(), SupportDBUrlType.INGRESDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.INFORMIXDEFAULTURL.getDBKey(), SupportDBUrlType.INFORMIXDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.SQLITE3DEFAULTURL.getDBKey(), SupportDBUrlType.SQLITE3DEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.GENERICJDBCDEFAULTURL.getDBKey(), SupportDBUrlType.GENERICJDBCDEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.TERADATADEFAULTURL.getDBKey(), SupportDBUrlType.TERADATADEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.AS400DEFAULTURL.getDBKey(), SupportDBUrlType.AS400DEFAULTURL);
        supportDBUrlMap.put(SupportDBUrlType.XML_eXist.getDBKey(), SupportDBUrlType.XML_eXist);
        supportDBUrlMap.put(SupportDBUrlType.MDM.getDBKey(), SupportDBUrlType.MDM);

        // MOD mzhao bug 12313, 2010-04-02 There is not dbType in prv files before 4.0 release, here use driver class
        // name
        // to get db type.
        supportDiverNameDBUrlMap.put(SupportDBUrlType.ODBCDEFAULTURL.getDbDriver(), SupportDBUrlType.ODBCDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.MYSQLDEFAULTURL.getDbDriver(), SupportDBUrlType.MYSQLDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getDbDriver(),
                SupportDBUrlType.ORACLEWITHSIDDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.ORACLEWITHSERVICENAMEDEFAULTURL.getDbDriver(),
                SupportDBUrlType.ORACLEWITHSERVICENAMEDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.MSSQLDEFAULTURL.getDbDriver(), SupportDBUrlType.MSSQLDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.MSSQL2008URL.getDbDriver(), SupportDBUrlType.MSSQL2008URL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.DB2DEFAULTURL.getDbDriver(), SupportDBUrlType.DB2DEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.DB2ZOSDEFAULTURL.getDbDriver(), SupportDBUrlType.DB2ZOSDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.POSTGRESQLEFAULTURL.getDbDriver(), SupportDBUrlType.POSTGRESQLEFAULTURL);
        // supportDiverNameDBUrlMap.put(SupportDBUrlType.INTERBASEDEFAULTURL.getDbDriver(),
        // SupportDBUrlType.INTERBASEDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.SYBASEDEFAULTURL.getDbDriver(), SupportDBUrlType.SYBASEDEFAULTURL);
        // supportDiverNameDBUrlMap.put(SupportDBUrlType.INFORMIXDEFAULTURL.getDbDriver(),
        // SupportDBUrlType.INFORMIXDEFAULTURL);
        // supportDiverNameDBUrlMap.put(SupportDBUrlType.FIREBIRDDEFAULTURL.getDbDriver(),
        // SupportDBUrlType.FIREBIRDDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.INGRESDEFAULTURL.getDbDriver(), SupportDBUrlType.INGRESDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.INFORMIXDEFAULTURL.getDbDriver(), SupportDBUrlType.INFORMIXDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.SQLITE3DEFAULTURL.getDbDriver(), SupportDBUrlType.SQLITE3DEFAULTURL);
        supportDiverNameDBUrlMap
                .put(SupportDBUrlType.GENERICJDBCDEFAULTURL.getDbDriver(), SupportDBUrlType.GENERICJDBCDEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.TERADATADEFAULTURL.getDbDriver(), SupportDBUrlType.TERADATADEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.AS400DEFAULTURL.getDbDriver(), SupportDBUrlType.AS400DEFAULTURL);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.XML_eXist.getDbDriver(), SupportDBUrlType.XML_eXist);
        supportDiverNameDBUrlMap.put(SupportDBUrlType.MDM.getDbDriver(), SupportDBUrlType.MDM);
        // ~12313
    }

    public void changeAllDBNmae(String dbName) {
        Iterator<String> it = supportDBUrlMap.keySet().iterator();
        while (it.hasNext()) {
            SupportDBUrlType dbType = supportDBUrlMap.get(it.next());
            if (dbType.getDBName() != null) {
                dbType.setDBName(dbName);
            }
        }
    }

    public String[] getDBTypes() {
        String[] dbTypeItems = new String[supportDBUrlMap.size()];
        supportDBUrlMap.keySet().toArray(dbTypeItems);
        Arrays.sort(dbTypeItems);
        return dbTypeItems;
    }

    public String[] getDBLanguages() {
        Set<String> tempList = new HashSet<String>();

        for (SupportDBUrlType type : SupportDBUrlType.values()) {
            String language = type.getLanguage();

            if (supportDBUrlMap.containsKey(type.getDBKey())) {
                tempList.add(language);
            }
        }

        String[] dbLanguages = tempList.toArray(new String[tempList.size()]);
        Arrays.sort(dbLanguages);

        return dbLanguages;
    }

    public String getDBUrl(SupportDBUrlType dbType) {
        return getDBUrl(dbType.getDBKey(), dbType.getHostName(), dbType.getPort(), dbType.getDBName(), dbType.getDataSource(),
                dbType.getParamSeprator() != null ? PluginConstant.DEFAULT_PARAMETERS : PluginConstant.EMPTY_STRING);
    }

    // }

    /**
     * Get dburl which content are replaced by parameter value.
     * 
     * @param dbType
     * @param host
     * @param port
     * @param dbName
     * @param dataSource
     * @param paramString TODO
     * @return
     */
    public String getDBUrl(String dbType, String host, String port, String dbName, String dataSource, String paramString) {
        String propUrlValue = PROP.getProperty(dbType);
        SupportDBUrlType defaultUrlType = supportDBUrlMap.get(dbType);
        defaultUrlType = defaultUrlType == null ? SupportDBUrlType.ODBCDEFAULTURL : defaultUrlType;
        if (propUrlValue == null) {
            return PluginConstant.EMPTY_STRING;
        }

        String argHost = (host == null) ? PluginConstant.EMPTY_STRING : host;
        String argPort = (port == null) ? PluginConstant.EMPTY_STRING : port;
        String argDBName = (dbName == null) ? PluginConstant.EMPTY_STRING : dbName;
        String argDataSource = (dataSource == null) ? PluginConstant.EMPTY_STRING : dataSource;
        Object[] argsUrl = { argHost, argPort, argDBName, argDataSource };
        if (paramString.equals(PluginConstant.EMPTY_STRING)) {
            return MessageFormat.format(propUrlValue, argsUrl);
        } else {
            return MessageFormat.format(propUrlValue, argsUrl) + defaultUrlType.getParamSeprator() + paramString;
        }
    }

    /**
     * Get the dburl via the dbType, and the dburl content come from the default value of SupportDBUrlType.
     * 
     * @param dbType
     * @return
     */
    public String getDefaultDBUrl(String dbType) {
        String propUrlValue = PROP.getProperty(dbType);
        SupportDBUrlType defaultUrlType = supportDBUrlMap.get(dbType);
        defaultUrlType = defaultUrlType == null ? SupportDBUrlType.ODBCDEFAULTURL : defaultUrlType;
        if (propUrlValue == null) {
            return PluginConstant.EMPTY_STRING;
        }
        Object[] args = { defaultUrlType.getHostName(), defaultUrlType.getPort(), defaultUrlType.getDBName(),
                defaultUrlType.getDataSource() };
        return MessageFormat.format(propUrlValue, args);
    }

    public SupportDBUrlType getDBUrlType(String dbType) {
        SupportDBUrlType dbUrlDefaultType = supportDBUrlMap.get(dbType);
        return dbUrlDefaultType == null ? SupportDBUrlType.ODBCDEFAULTURL : dbUrlDefaultType;
    }

    /**
     * MOD mzhao bug 12313, 2010-04-02 There is not dbType in prv files before 4.0 release, here use driver class name.
     * 
     * @param dbType
     * @return
     */
    public SupportDBUrlType getDBUrlTypeByDriverName(String dbDriver) {
        SupportDBUrlType dbUrlDefaultType = supportDiverNameDBUrlMap.get(dbDriver);
        return dbUrlDefaultType == null ? SupportDBUrlType.ODBCDEFAULTURL : dbUrlDefaultType;
    }

    public Properties getDBPameterProperties(String connectionStr) {
        Properties paramProperties = new Properties();
        String matchSubStr = connectionStr.substring(0, 8);
        Set<Object> s = PROP.keySet();
        Iterator<Object> it = s.iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            String value = PROP.getProperty(id);
            if (value.contains(matchSubStr)) {
                paramProperties.setProperty(PluginConstant.DBTYPE_PROPERTY, id);
                MessageFormat mf = new MessageFormat(value);
                Object[] parseResult = mf.parse(connectionStr, new ParsePosition(0));
                if (parseResult != null) {
                    if (parseResult[0] != null) {
                        paramProperties.setProperty(PluginConstant.HOSTNAME_PROPERTY, (String) parseResult[0]);
                    }

                    if (parseResult[1] != null) {
                        paramProperties.setProperty(PluginConstant.PORT_PROPERTY, (String) parseResult[1]);
                    }

                    break;
                }
            }
        }

        return paramProperties;
    }

    /**
     * DOC bZhou Comment method "findDBTypeByName".
     * 
     * @param dbName
     * @return
     */
    public SupportDBUrlType findDBTypeByName(String dbName) {
        if (supportDBUrlMap == null || supportDBUrlMap.isEmpty() || dbName == null) {
            return null;
        }

        for (String key : supportDBUrlMap.keySet()) {
            if (dbName.equalsIgnoreCase(key)) {
                return supportDBUrlMap.get(key);
            }
        }

        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            InputStream in = SupportDBUrlStore.class.getResourceAsStream("dburl.properties"); //$NON-NLS-1$
            PROP.load(in);
            in.close();
            Set<?> s = PROP.keySet();
            // System.out.println(s.toString());
            Iterator<?> it = s.iterator();
            while (it.hasNext()) {
                String id = (String) it.next();
                String value = PROP.getProperty(id);
                System.out.println("\n" + id + "-------" + value); //$NON-NLS-1$ //$NON-NLS-2$

                // Object[] arguments = { "10.78.23.23", "33456", "testDB" };
                Object[] arguments = { "{hostname}", "{port}", "{dbname}", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                System.out.println(MessageFormat.format(value, arguments));
            }
            // System.out.println(p.toString());
        } catch (Exception e) {
            log.error(e, e);
        }
    }

}

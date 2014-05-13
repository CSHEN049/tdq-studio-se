package net.sourceforge.sqlexplorer.dbproduct;

import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.sqlexplorer.ExplorerException;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SQLCannotConnectException;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.talend.utils.sql.ConnectionUtils;

/**
 * Manages a JDBC Driver
 * 
 * @author John Spackman
 */
public class ManagedDriver implements Comparable<ManagedDriver> {

    public class SQLDriver implements ISQLDriver {

        public void addPropertyChangeListener(PropertyChangeListener listener) {
        }

        public void assignFrom(ISQLDriver rhs) throws ValidationException {
            throw new ValidationException(Messages.getString("ManagedDriver.NotSupported")); //$NON-NLS-1$
        }

        public int compareTo(ISQLDriver rhs) {
            return ManagedDriver.this.getDriverClassName().compareTo(rhs.getDriverClassName());
        }

        public String getDriverClassName() {
            return ManagedDriver.this.getDriverClassName();
        }

        public IIdentifier getIdentifier() {
            return null;
        }

        public String getJarFileName() {
            return null;
        }

        public String[] getJarFileNames() {
            return (String[]) ManagedDriver.this.getJars().toArray();
        }

        public StringWrapper getJarFileNameWrapper(int idx) throws ArrayIndexOutOfBoundsException {
            return null;
        }

        public StringWrapper[] getJarFileNameWrappers() {
            return null;
        }

        public String getName() {
            return ManagedDriver.this.getDriverClassName();
        }

        public String getUrl() {
            return ManagedDriver.this.getUrl();
        }

        public String getWebSiteUrl() {
            return null;
        }

        public boolean isJDBCDriverClassLoaded() {
            return ManagedDriver.this.isDriverClassLoaded();
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
        }

        public void setDriverClassName(String driverClassName) throws ValidationException {
        }

        public void setJarFileName(String value) throws ValidationException {
        }

        public void setJarFileNames(String[] values) {
        }

        public void setJarFileNameWrapper(int idx, StringWrapper value) throws ArrayIndexOutOfBoundsException {
        }

        public void setJarFileNameWrappers(StringWrapper[] value) {
        }

        public void setJDBCDriverClassLoaded(boolean cl) {
        }

        public void setName(String name) throws ValidationException {
        }

        public void setUrl(String url) throws ValidationException {
        }

        public void setWebSiteUrl(String url) throws ValidationException {
        }
    }

    private String id;

    private String name;

    private String driverClassName;

    private String url;

    private LinkedList<String> jars = new LinkedList<String>();

    private Driver jdbcDriver;

    public ManagedDriver(String id) {
        this.id = id;
    }

    /**
     * Constructs a new ManagedDriver from a previously serialised version
     * 
     * @param root result of previous call to describeAsXml()
     */
    public ManagedDriver(Element root) {
        super();
        id = root.attributeValue(DriverManager.ID);
        name = root.elementText(DriverManager.NAME);
        driverClassName = root.elementText(DriverManager.DRIVER_CLASS);
        url = root.elementText(DriverManager.URL);
        Element jarsElem = root.element(DriverManager.JARS);
        List<Element> list = jarsElem.elements();
        if (list != null) {
            for (Element jarElem : list) {
                String jar = jarElem.getTextTrim();
                if (jar != null) {
                    jars.add(jar);
                }
            }
        }
    }

    /**
     * Describes this driver in XML; the result can be passed to the constructor to refabricate it late
     * 
     * @return
     */
    public Element describeAsXml() {
        Element root = new DefaultElement(DriverManager.DRIVER);
        root.addAttribute(DriverManager.ID, id);
        root.addElement(DriverManager.NAME).setText(name);
        if (driverClassName != null) {
            root.addElement(DriverManager.DRIVER_CLASS).setText(driverClassName);
        }
        root.addElement(DriverManager.URL).setText(url);
        Element jarsElem = root.addElement(DriverManager.JARS);
        for (String jar : jars) {
            jarsElem.addElement(DriverManager.JAR).setText(jar);
        }
        return root;
    }

    /**
     * Loads the Driver class
     * 
     * @throws ExplorerException
     * @throws SQLException
     */
    public synchronized void registerSQLDriver() throws ClassNotFoundException {
        if (driverClassName == null || driverClassName.length() == 0) {
            return;
        }
        unregisterSQLDriver();
        jdbcDriver = DatabaseProductFactory.loadDriver(this);
    }

    /**
     * Unloads the class
     * 
     */
    public synchronized void unregisterSQLDriver() {
        jdbcDriver = null;
    }

    /**
     * Establishes a JDBC connection
     * 
     * @param user
     * @return
     * @throws ExplorerException
     * @throws SQLException
     */
    public SQLConnection getConnection(User user) throws SQLException {
        Properties props = new Properties();
        // MOD msjian TDQ-8463: for the string "user" and "password", no need to do international.bacause some
        // jars(e.g:ojdbc14.jar) don't support international and cause to get connection error
        if (user.getUserName() != null) {
            props.put("user", user.getUserName()); //$NON-NLS-1$
        }
        if (user.getPassword() != null) {
            props.put("password", user.getPassword());//$NON-NLS-1$
        }
        if (!isDriverClassLoaded()) {
            try {
                registerSQLDriver();
            } catch (ClassNotFoundException e) {
                throw new SQLException(Messages.getString("ManagedDriver.CannotLoadDriver1", driverClassName) + " "//$NON-NLS-1$ //$NON-NLS-2$
                        + Messages.getString("ManagedDriver.CannotLoadDriver2"));//$NON-NLS-1$
            }
        }
        if (!isDriverClassLoaded()) {
            throw new SQLException(Messages.getString("ManagedDriver.CannotLoadDriver1", driverClassName));//$NON-NLS-1$
        }

        Connection jdbcConn = null;
        try {
            String dbUrl = user.getAlias().getUrl();
            if (ConnectionUtils.isHsql(dbUrl)) {
                dbUrl = ConnectionUtils.addShutDownForHSQLUrl(dbUrl, user.getMetadataConnection().getAdditionalParams());
            }
            jdbcConn = jdbcDriver.connect(dbUrl, props);
        } catch (SQLException e) {
            throw new SQLCannotConnectException(user, e);
        }
        if (jdbcConn == null) {
            throw new SQLCannotConnectException(user);
        }

        return new SQLConnection(user, jdbcConn, this, getDatabaseProduct().describeConnection(jdbcConn));
    }

    public boolean isDriverClassLoaded() {
        return jdbcDriver != null;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getId() {
        return id;
    }

    public LinkedList<String> getJars() {
        return jars;
    }

    public Driver getJdbcDriver() {
        return jdbcDriver;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setJars(LinkedList<String> jars) {
        this.jars = jars;
    }

    public void setJars(String[] jars) {
        this.jars.clear();
        for (String jar : jars) {
            this.jars.add(jar);
        }
    }

    public void setJdbcDriver(Driver jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public DatabaseProduct getDatabaseProduct() {
        return DatabaseProductFactory.getInstance(this);
    }

    public int compareTo(ManagedDriver that) {
        return name.compareTo(that.name);
    }

    public boolean isUsedByAliases() {
        Collection<Alias> aliases = SQLExplorerPlugin.getDefault().getAliasManager().getAliases();
        for (Alias alias : aliases) {
            if (alias.getDriverId().equals(this.getId())) {
                return true;
            }
        }
        return false;
    }
}

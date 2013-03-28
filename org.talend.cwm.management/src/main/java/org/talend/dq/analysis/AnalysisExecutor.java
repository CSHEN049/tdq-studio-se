// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.ValueIndicator;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPool;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.indicators.IndicatorCommonUtil;
import org.talend.dq.indicators.ext.PatternMatchingExt;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.metadata.managment.connection.manager.HiveConnectionManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public abstract class AnalysisExecutor implements IAnalysisExecutor {

    private static Logger log = Logger.getLogger(AnalysisExecutor.class);

    protected String errorMessage;

    protected Boolean parallelExeStatus = Boolean.TRUE;

    protected static final boolean POOLED_CONNECTION = Boolean.TRUE;

    /**
     * use {@link #dbms()} to access this attribute.
     */
    private DbmsLanguage dbmsLanguage;

    protected Analysis cachedAnalysis;

    private long usedMemory;

    /**
     * Getter for usedMemory.
     * 
     * @return the usedMemory
     */
    public long getUsedMemory() {
        return this.usedMemory;
    }

    private volatile boolean isLowMemory = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.IAnalysisExecutor#execute(org.talend.dataquality.analysis.Analysis)
     */
    public ReturnCode execute(final Analysis analysis) {
        // --- preconditions
        if (!check(analysis)) {
            return getReturnCode(false);
        }
        assert analysis != null;

        // --- creation time
        final ExecutionInformations resultMetadata = analysis.getResults().getResultMetadata();
        final long startime = System.currentTimeMillis();
        resultMetadata.setExecutionDate(new Date(startime));
        // MOD qiongli 2012-3-14 TDQ-4433,if import from low vesion and not import SystemIdicator,should initionlize
        // these indicator.
        initializeIndicators(analysis.getResults().getIndicators());
        // --- create SQL statement
        if (this.getMonitor() != null) {
            this.getMonitor().setTaskName(Messages.getString("AnalysisExecutor.CreateSQLStatements"));
        }
        String sql = createSqlStatement(analysis);
        if (sql == null) {
            return getReturnCode(false);
        }

        // ADD msjian 2011-5-30 17479: Excel Odbc connection can not run well on the correlation analysis
        // note: this feature is not supported now, if support, delete this
        if (errorMessage != null && !errorMessage.equals("")) { //$NON-NLS-1$
            if ("EXCEL".equals(dbms().getDbmsName())) { //$NON-NLS-1$
                return getReturnCode(true);
            }
        }
        // ~

        // --- run analysis
        boolean ok = false;
        try { // catch any exception
            if (this.continueRun()) {
                ok = runAnalysis(analysis, sql);
            }
        } catch (Exception e) {
            ok = false;
            log.error(e, e);
        } finally {
            // ADD msjian TDQ-5952: we should close connections always.
            // after run analysis, close connection at once when don't need it
            TdqAnalysisConnectionPool.closeConnectionPool(analysis);
            // TDQ-5952~
        }

        // --- set metadata information of analysis
        resultMetadata.setLastRunOk(ok);
        int executionNumber = resultMetadata.getExecutionNumber() + 1;
        resultMetadata.setExecutionNumber(executionNumber);
        if (this.isLowMemory) {
            errorMessage = Messages.getString("Evaluator.OutOfMomory", usedMemory);//$NON-NLS-1$
            resultMetadata.setMessage(errorMessage);
        } else if (ok) {
            resultMetadata.setLastExecutionNumberOk(executionNumber);
            resultMetadata.setMessage(null); // reset error message

        } else {
            resultMetadata.setMessage(errorMessage);
        }

        // --- compute execution duration
        if (this.continueRun()) {
            long endtime = System.currentTimeMillis();
            resultMetadata.setExecutionDuration((int) (endtime - startime));

            // MOD qiongli 2010-8-10, feature 14252
            EList<Indicator> indicatorLs = analysis.getResults().getIndicators();
            resultMetadata.setOutThreshold(false);
            for (Indicator indicator : indicatorLs) {
                if (hasOutThreshold(indicator)) {
                    resultMetadata.setOutThreshold(true);
                    break;
                }
            }// ~
        }
        return new ReturnCode(this.errorMessage, ok);
    }

    /**
     * 
     * DOC qiongli Comment method "hasOutThreshold".
     * 
     * @param indicator
     * @return
     */
    private boolean hasOutThreshold(Indicator indicator) {
        String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(indicator);
        String[] indiPercentThreshold = IndicatorHelper.getIndicatorThresholdInPercent(indicator);
        Object obj = IndicatorCommonUtil.getIndicatorValue(indicator);
        if (indicatorThreshold != null || indiPercentThreshold != null) {
            // MOD qiongli 2011-11-15 TDQ-3690 avoid String "null",and get the value for ValueIndicator to transfer.
            if (obj != null && !PluginConstant.EMPTY_STRING.equals(obj.toString()) && !"null".equalsIgnoreCase(obj.toString())) {
                String value = PluginConstant.EMPTY_STRING;
                if (indicator instanceof ValueIndicator) {
                    value = ((ValueIndicator) indicator).getValue();
                }
                ChartDataEntity chartDataEntity = new ChartDataEntity(indicator, PluginConstant.EMPTY_STRING, value);
                if (obj instanceof PatternMatchingExt) {
                    obj = (((PatternMatchingExt) obj).getMatchingValueCount());
                }
                if (chartDataEntity.isOutOfRange(obj.toString())) {
                    return true;
                }
            }
        }
        List<Indicator> leaves = IndicatorHelper.getIndicatorLeaves(indicator);
        if (leaves.size() > 0 && !leaves.get(0).equals(indicator)) {
            for (Indicator leaveIndicator : leaves) {
                if (hasOutThreshold(leaveIndicator)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method "createSqlStatement".
     * 
     * @param analysis the analysis from which the SQL will be generated
     * @return the generated SQL statement or null if problem
     */
    protected abstract String createSqlStatement(Analysis analysis);

    /**
     * Method "getReturnCode".
     * 
     * @param ok
     * @return a return code with the last error message
     */
    protected ReturnCode getReturnCode(boolean ok) {
        return ok ? new ReturnCode() : new ReturnCode(this.errorMessage, false);
    }

    /**
     * Method "check" checks that the analysis can be run.
     * 
     * @param analysis the analysis to prepare
     * @return true if ok.
     */
    protected boolean check(Analysis analysis) {
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            this.errorMessage = Messages.getString("AnalysisExecutor.ContextNull", analysis.getName()); //$NON-NLS-1$
            return false;
        }
        DataManager connection = context.getConnection();
        if (connection == null) {
            this.errorMessage = Messages.getString("AnalysisExecutor.NoConnectionFound", analysis.getName()); //$NON-NLS-1$
            return false;
        }
        if (log.isInfoEnabled()) {
            if (SoftwaredeploymentPackage.eINSTANCE.getDataProvider().isInstance(connection)) {
                // MOD 20130225 TDQ-6632 the name of the item should be given (not the pathname)
                log.info(Messages.getString("AnalysisExecutor.CONNECTIONTO", connection.getName()));//$NON-NLS-1$
            }
        }
        AnalysisResult results = analysis.getResults();
        if (results == null) {
            this.errorMessage = Messages.getString("AnalysisExecutor.AnalysisnotNotPrepareCorrect", analysis.getName()); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    /**
     * Method "runAnalysis". Mod 20130314 TDQ-5973 refactor to extract same part here
     * 
     * @param analysis the analysis to be run
     * @param sqlStatement the sql statement to execute on Database
     * @return true if ok
     */
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        // Open connection
        TypedReturnCode<java.sql.Connection> connection = this.getConnectionBeforeRun(analysis);
        if (!connection.isOk()) {
            return this.traceError(connection.getMessage());
        }

        // abstract method
        ReturnCode rc = evaluate(analysis, connection.getObject(), sqlStatement);

        // close connection
        ReturnCode rcon = closeConnection(analysis, connection.getObject());
        if (!rc.isOk()) {
            traceError(rc.getMessage());
        }

        return rc.isOk() && rcon.isOk();
    }

    protected abstract ReturnCode evaluate(Analysis analysis, java.sql.Connection connection, String sqlStatement);

    /**
     * get the connection before evaluating, same for all sub classes
     * 
     * @param analysis
     * @return the connection(pooled or not)
     */
    protected TypedReturnCode<java.sql.Connection> getConnectionBeforeRun(Analysis analysis) {
        // open a connection
        TypedReturnCode<java.sql.Connection> connection = null;
        if (POOLED_CONNECTION) {
            // reset the connection pool before run this analysis
            resetConnectionPool(analysis);
            connection = getPooledConnection(analysis);
        } else {
            connection = getConnection(analysis);
        }

        return connection;

    }

    /**
     * close the connection for the analysis after running.
     * 
     * @param analysis
     * @param connection
     * @return close success or not
     */
    protected ReturnCode closeConnection(Analysis analysis, java.sql.Connection connection) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        if (connection != null) {
            if (POOLED_CONNECTION) {
                resetConnectionPool(analysis);
            } else {
                rc = ConnectionUtils.closeConnection(connection);
                if (!rc.isOk()) {
                    this.errorMessage = rc.getMessage();
                }
            }
        } else {
            rc.setOk(Boolean.FALSE);
            rc.setMessage("Connection is null when running analysis: " + analysis.getName()); //$NON-NLS-1$
            log.error(rc.getMessage());
        }
        return rc;
    }

    /**
     * Method "traceError".
     * 
     * @param error the message to set in errorMessage
     * @return always false
     */
    protected boolean traceError(String error) {
        this.errorMessage = error;
        log.error(this.errorMessage);
        return false;
    }

    /**
     * DOC scorreia Comment method "getConnection".
     * 
     * @param analysis
     * @param schema
     * @return
     */
    protected TypedReturnCode<java.sql.Connection> getConnection(Analysis analysis) {
        // MODSCA 2008-03-25 scorreia schema is not used. removed (was used before to select the catalog of the db)
        // now it is done elsewhere
        TypedReturnCode<java.sql.Connection> rc = new TypedReturnCode<java.sql.Connection>();

        DataManager datamanager = analysis.getContext().getConnection();
        if (datamanager == null) {
            rc.setReturnCode(Messages.getString("AnalysisExecutor.DataManagerNull", analysis.getName()), false); //$NON-NLS-1$
            return rc;
        }
        if (datamanager != null && datamanager.eIsProxy()) {
            datamanager = (DataManager) EObjectHelper.resolveObject(datamanager);
        }
        Connection dataprovider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(datamanager);
        if (dataprovider == null) {
            rc.setReturnCode(Messages.getString("AnalysisExecutor.DataProviderNull", datamanager.getName(), //$NON-NLS-1$
                    analysis.getName()), false);
            return rc;
        }

        // create hive connection
        IMetadataConnection metadataConnection = ConvertionHelper.convert(dataprovider);
        if (EDatabaseTypeName.HIVE.getXmlName().equalsIgnoreCase(metadataConnection.getDbType())) {
            try {
                java.sql.Connection createConnection = HiveConnectionManager.getInstance().createConnection(metadataConnection);
                rc.setOk(true);
                rc.setObject(createConnection);
            } catch (ClassNotFoundException e) {
                log.error(e);
                rc.setOk(false);
            } catch (InstantiationException e) {
                log.error(e);
                rc.setOk(false);
            } catch (IllegalAccessException e) {
                log.error(e);
                rc.setOk(false);
            } catch (SQLException e) {
                log.error(e);
                rc.setOk(false);
            }
            return rc;
        } else {
            // create other type connection
            TypedReturnCode<java.sql.Connection> connection = JavaSqlFactory.createConnection(dataprovider);
            if (!connection.isOk()) {
                rc.setReturnCode(connection.getMessage(), false);
                return rc;
            }

            rc.setObject(connection.getObject());
            return rc;
        }
    }

    /**
     * let the connection pool go back to its original state.
     * 
     * @param analysis
     */
    protected void resetConnectionPool(Analysis analysis) {
        TdqAnalysisConnectionPool.closeConnectionPool(analysis);
    }

    /**
     * DOC xqliu Comment method "getPooledConnection".
     * 
     * @param analysis
     * @param dataProvider
     * @return
     */
    protected TypedReturnCode<java.sql.Connection> getPooledConnection(Analysis analysis) {
        TypedReturnCode<java.sql.Connection> rc = new TypedReturnCode<java.sql.Connection>();
        java.sql.Connection pooledConnection = null;

        try {
            pooledConnection = TdqAnalysisConnectionPool.getConnectionPool(analysis).getConnection();
        } catch (Exception e) {
            log.debug(e, e);
        }

        if (pooledConnection == null) {
            rc.setReturnCode("Can't get any useable connection!", false); //$NON-NLS-1$
            return rc;
        }

        // else ok
        rc.setObject(pooledConnection);
        return rc;
    }

    /**
     * DOC xqliu Comment method "getAnalysisDataProvider".
     * 
     * @param analysis
     * @return
     */
    protected Connection getAnalysisDataProvider(Analysis analysis) {
        DataManager datamanager = analysis.getContext().getConnection();
        if (datamanager != null && datamanager.eIsProxy()) {
            datamanager = (DataManager) EObjectHelper.resolveObject(datamanager);
        }
        return SwitchHelpers.CONNECTION_SWITCH.doSwitch(datamanager);
    }

    private IProgressMonitor monitor;

    public IProgressMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    protected boolean continueRun() {
        if (!Platform.isRunning()) { // reporting engine is working as library
            return true;
        }
        boolean ret = true;
        if (getMonitor() != null && getMonitor().isCanceled()) {
            ret = false;
        } else if (this.isLowMemory) {
            ret = false;
        } else if (AnalysisThreadMemoryChangeNotifier.getInstance().isUsageThresholdExceeded()) {
            this.usedMemory = AnalysisThreadMemoryChangeNotifier.convertToMB(ManagementFactory.getMemoryMXBean()
                    .getHeapMemoryUsage().getUsed());
            ret = false;
            this.isLowMemory = true;
        }
        return ret;
    }

    /**
     * Method "getCatalogName".
     * 
     * @param analyzedElement
     * @return the catalog or schema quoted name
     */
    // MOD yyi 2011-02-22 17871:delimitefile
    protected String getQuotedCatalogName(ModelElement analyzedElement) {
        final Catalog parentCatalog = CatalogHelper.getParentCatalog(analyzedElement);
        return parentCatalog == null ? null : quote(parentCatalog.getName());
    }

    /**
     * DOC scorreia Comment method "getSchemaName".
     * 
     * @param columnSetOwner
     * @return
     */
    protected String getQuotedSchemaName(ModelElement columnSetOwner) {
        final Schema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
        return (parentSchema == null) ? null : quote(parentSchema.getName());
    }

    /**
     * Method "dbms".
     * 
     * @return the DBMS language (not null)
     */
    protected DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            this.dbmsLanguage = createDbmsLanguage();
        }
        return this.dbmsLanguage;
    }

    DbmsLanguage createDbmsLanguage() {
        DataManager connection = this.cachedAnalysis.getContext().getConnection();
        return DbmsLanguageFactory.createDbmsLanguage(connection);
    }

    /**
     * Method "quote".
     * 
     * @param input
     * @return the given string between quotes (for SQL)
     */
    protected String quote(String input) {
        return dbms().quote(input);
    }

    /**
     * 2012-3-14 TDQ-4433,reset indicatorDefinition for indicator if needed(indicatorDefinition is null or proxy).
     */
    protected void initializeIndicators(List<Indicator> indicators) {
        ModelElementAnalysisHandler modHandler = new ModelElementAnalysisHandler();
        for (Indicator ind : indicators) {
            modHandler.initializeIndicator(ind);
        }
    }
}

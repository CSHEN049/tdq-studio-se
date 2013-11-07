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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.dbms.GenericSQLHandler;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Classifier;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * @author scorreia
 * 
 * Run analysis on columns.
 */
public class ColumnAnalysisExecutor extends AnalysisExecutor {

    private Connection dataprovider;

    private static Logger log = Logger.getLogger(ColumnAnalysisExecutor.class);

    protected Map<ModelElement, Package> schemata = new HashMap<ModelElement, Package>();

    protected boolean isAccessWith(Connection dp) {
        if (dataprovider == null) {
            dataprovider = dp;
            return true;
        }
        // else compare
        return ResourceHelper.areSame(dataprovider, dp);
    }

    @Override
    protected ReturnCode evaluate(Analysis analysis, java.sql.Connection connection, String sqlStatement) {
        // ADD sizhaoliu TDQ-7216 update JUDI indicators while running a tDqReportRun job.
        UDIHelper.updateJUDIsForAnalysis(analysis);
        IndicatorEvaluator eval = new IndicatorEvaluator(analysis);
        // MOD xqliu 2009-02-09 bug 6237
        eval.setMonitor(getMonitor());
        // set it into the evaluator
        eval.setConnection(connection);
        // use pooled connection
        eval.setPooledConnection(POOLED_CONNECTION);

        // --- add indicators
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            assert indicator != null;
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(indicator.getAnalyzedElement());
            if (tdColumn == null) {
                continue;
            }
            // --- get the schema owner
            if (!belongToSameSchemata(tdColumn)) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.GivenColumn", tdColumn.getName()); //$NON-NLS-1$
                return new ReturnCode(errorMessage, Boolean.FALSE);
            }
            String columnName = ColumnHelper.getFullName(tdColumn);
            eval.storeIndicator(columnName, indicator);
        }

        // when to close connection
        boolean closeAtTheEnd = true;
        Package catalog = schemata.values().iterator().next();
        if (!eval.selectCatalog(catalog.getName())) {
            log.warn(Messages.getString("ColumnAnalysisExecutor.FAILEDTOSELECTCATALOG", catalog.getName()));//$NON-NLS-1$
        }

        return eval.evaluateIndicators(sqlStatement, closeAtTheEnd);
    }

    /**
     * Method "belongToSameSchemata" fills in the map this{@link #schemata}.
     * 
     * @param tdColumn a column
     * @return false when the given column has an owner different from the one registered in the map.
     */
    protected boolean belongToSameSchemata(final TdColumn tdColumn) {
        assert tdColumn != null;
        if (schemata.get(tdColumn) != null) {
            return true;
        }
        // get table or view
        ColumnSet owner = ColumnHelper.getColumnOwnerAsColumnSet(tdColumn);
        if (owner == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NotFoundColumn", tdColumn.getName()); //$NON-NLS-1$
            return false;
        }
        // get catalog or schema
        Package schema = ColumnSetHelper.getParentCatalogOrSchema(owner);
        if (schema == null) {
            this.errorMessage = Messages.getString(
                    "ColumnAnalysisExecutor.NoSchemaOrCatalogFound", owner.getName(), tdColumn.getName()); //$NON-NLS-1$
            return false;
        }

        schemata.put(tdColumn, schema);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    public String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
        // CwmZQuery query = new CwmZQuery();
        StringBuilder sql = new StringBuilder("SELECT ");//$NON-NLS-1$
        EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();

        // From here to the end of the method is as same as the part in ColumnSetAnalysisExecutor(line 184),
        // so if you modify the code here, please also modify the same part.
        if (analysedElements.isEmpty()) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.CannotCreateSQLStatement",//$NON-NLS-1$
                    analysis.getName());
            return null;
        }
        Set<ColumnSet> fromPart = new HashSet<ColumnSet>();
        final Iterator<ModelElement> iterator = analysedElements.iterator();
        while (iterator.hasNext()) { // for (ModelElement modelElement : analysedElements) {
            ModelElement modelElement = iterator.next();
            // --- preconditions
            TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(modelElement);
            if (col == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.GivenElementIsNotColumn", modelElement); //$NON-NLS-1$
                return null;
            }
            Classifier owner = col.getOwner();
            if (owner == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoOwnerFound", col.getName()); //$NON-NLS-1$
            }
            ColumnSet colSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(owner);
            if (colSet == null) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContainerFound", col.getName()); //$NON-NLS-1$
                return null;
            }
            // else add into select
            // MOD zshen feature 12919 select all the column to be prepare for drill down when user need.
            if (!analysis.getParameters().isStoreData()) {
                sql.append(this.quote(col.getName()));
                // append comma if more columns exist
                if (iterator.hasNext()) {
                    sql.append(',');
                }
            }

            // add from
            fromPart.add(colSet);

        }
        if (fromPart.size() != 1) {
            log.error(Messages.getString("ColumnAnalysisExecutor.ANALYSISMUSTRUNONONETABLE") + fromPart.size() + PluginConstant.DOT_STRING);//$NON-NLS-1$
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.ANALYSISMUSTRUNONONETABLEERRORMESSAGE");//$NON-NLS-1$
            return null;
        }
        // MOD zshen feature 12919 select all the column to be prepare for drill down.
        if (analysis.getParameters().isStoreData()) {
            // MOD klliu 2011-06-30 bug 22523 whichever is Table or View,that finds columns should ues columnset
            EObject eContainer = analysedElements.get(0).eContainer();
            List<TdColumn> columnList = ColumnSetHelper.getColumns(SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eContainer));
            // ~
            Iterator<TdColumn> iter = columnList.iterator();
            while (iter.hasNext()) {
                TdColumn column = iter.next();
                sql.append(this.quote(column.getName()));
                // append comma if more columns exist
                if (iter.hasNext()) {
                    sql.append(',');
                }
            }
        }

        // add from clause
        sql.append(dbms().from());
        ModelElement element = fromPart.iterator().next();
        Package parentRelation = PackageHelper.getParentPackage((MetadataTable) fromPart.iterator().next());
        if (parentRelation instanceof Schema) {
            // MOD msjian TDQ-5503 2012-6-12: fix when the db is mssql, there should exist catalog
            String catalog = null;
            if (SupportDBUrlType.isMssql(dbms().getDbmsName())) {
                catalog = PackageHelper.getParentPackage(parentRelation).getName();
            }
            sql.append(dbms().toQualifiedName(catalog, parentRelation.getName(), element.getName()));
            // TDQ-5503~
        } else if (parentRelation instanceof Catalog) {
            String ownerUser = null;
            if (ConnectionUtils.isSybaseeDBProducts(dbms().getDbmsName())) {
                ownerUser = ColumnSetHelper.getTableOwner(element);
            }
            sql.append(dbms().toQualifiedName(parentRelation.getName(), ownerUser, element.getName()));
        }
        // add where clause
        // --- get data filter
        ModelElementAnalysisHandler handler = new ModelElementAnalysisHandler();
        handler.setAnalysis(analysis);
        String stringDataFilter = handler.getStringDataFilter();
        if (!(stringDataFilter == null || "".equals(stringDataFilter))) { //$NON-NLS-1$
            sql.append(GenericSQLHandler.WHERE_CLAUSE);
        }
        String sqlStatement = sql.toString();
        sqlStatement = dbms().addWhereToStatement(sqlStatement, stringDataFilter);
        return sqlStatement;
    }

    @Override
    protected boolean check(final Analysis analysis) {
        if (analysis == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AnalysisIsNull"); //$NON-NLS-1$
            return false;
        }
        if (!super.check(analysis)) {
            // error message already set in super method.
            return false;
        }

        // --- check existence of context
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoContextSet", analysis.getName()); //$NON-NLS-1$
            return false;
        }

        // --- check that there exists at least on element to analyze
        if (context.getAnalysedElements().size() == 0) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AnalysisHaveAtLeastOneColumn"); //$NON-NLS-1$
            return false;
        }

        // --- check that the connection has been set
        if (context.getConnection() == null) {
            this.errorMessage = Messages.getString("ColumnAnalysisExecutor.NoConnectionSet"); //$NON-NLS-1$
            return false;
        }

        return checkAnalyzedElements(analysis, context);
    }

    /**
     * DOC scorreia Comment method "checkAnalyzedElements".
     * 
     * @param analysis
     * @param context
     */
    protected boolean checkAnalyzedElements(final Analysis analysis, AnalysisContext context) {
        ModelElementAnalysisHandler analysisHandler = new ModelElementAnalysisHandler();
        analysisHandler.setAnalysis(analysis);

        for (ModelElement node : context.getAnalysedElements()) {
            TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(node);

            // --- Check that each analyzed element has at least one indicator
            if (analysisHandler.getIndicators(column).size() == 0) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.EachColumnHaveOneIndicator"); //$NON-NLS-1$
                return false;
            }

            // --- get the data provider
            Connection dp = ConnectionHelper.getTdDataProvider(column);
            if (!isAccessWith(dp)) {
                this.errorMessage = Messages.getString("ColumnAnalysisExecutor.AllColumnsBelongSameConnection", //$NON-NLS-1$
                        column.getName(), dataprovider.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * Method "getQuotedColumnName".
     * 
     * @param column a column
     * @return the quoted column name
     */
    protected String getQuotedColumnName(ModelElement column) {
        if (column != null && column.eIsProxy()) {
            column = (ModelElement) EObjectHelper.resolveObject(column);
        }
        assert column != null;
        String quotedColName = quote(column.getName());
        return quotedColName;
    }

    /**
     * Method "getQuotedTableName".
     * 
     * @param column
     * @return the quoted table name
     */
    protected String getQuotedTableName(TdColumn column) {
        if (column != null && column.eIsProxy()) {
            column = (TdColumn) EObjectHelper.resolveObject(column);
        }
        String table = quote(ColumnHelper.getTableFullName(column));
        return table;
    }

}

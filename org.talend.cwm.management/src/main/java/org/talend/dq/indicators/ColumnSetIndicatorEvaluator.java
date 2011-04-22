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
package org.talend.dq.indicators;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.db.connection.MdmStatement;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dq.helper.ParameterUtil;
import org.talend.utils.sql.TalendTypeConvert;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.csvreader.CsvReader;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class ColumnSetIndicatorEvaluator extends Evaluator<String> {

    private static Logger log = Logger.getLogger(ColumnSetIndicatorEvaluator.class);

    protected Analysis analysis = null;

    // MOD yyi 2011-02-22 17871:delimitefile
    protected boolean isDelimitedFile = false;

    protected boolean isMdm = false;

    protected TdXmlSchema tdXmlDocument;

    protected MdmWebserviceConnection mdmWebserviceConn;

    public ColumnSetIndicatorEvaluator(Analysis analysis) {
        this.analysis = analysis;
        this.isDelimitedFile = analysis.getContext().getConnection() instanceof DelimitedFileConnection;
        this.isMdm = analysis.getContext().getConnection() instanceof MDMConnection;
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode ok = new ReturnCode(true);
        AnalysisResult anaResult = analysis.getResults();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = anaResult.getIndicToRowMap();
        indicToRowMap.clear();
        if (isDelimitedFile) {
            ok = evaluateByDelimitedFile(sqlStatement, ok);
        } else if (isMdm) {
            ok = evaluateByMDM(sqlStatement, ok);
        } else {
            ok = evaluateBySql(sqlStatement, ok);
            Statement statement = null;
            // FIXME stat should be closed.
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setFetchSize(fetchSize);
            if (continueRun()) {
                if (log.isInfoEnabled()) {
                    log.info("Executing query: " + sqlStatement);
                }
                statement.execute(sqlStatement);
            }
        }

        return ok;
    }

    /**
     * 
     * DOC qiongli Comment method "getAnalyzedElementsName".
     * 
     * @return
     */
    private List<String> getAnalyzedElementsName() {
        List<String> columnsName = new ArrayList<String>();
        List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
        for (ModelElement me : analysisElementList) {
            String name = ModelElementHelper.getName(me);
            if (name != null) {
                columnsName.add(name);
            }

        }
        return columnsName;
    }

    /**
     * 
     * orgnize EList 'objectLs' by SQL.
     * 
     * @param sqlStatement
     * @param ok
     * @return
     * @throws SQLException
     */
    private ReturnCode evaluateBySql(String sqlStatement, ReturnCode ok) throws SQLException {
        Statement statement = null;
        // FIXME stat should be closed.
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        statement.setFetchSize(fetchSize);
        if (continueRun()) {
            if (log.isInfoEnabled()) {
                log.info("Executing query: " + sqlStatement);
            }
            statement.execute(sqlStatement);
        }
        // get the results
        ResultSet resultSet = statement.getResultSet();
        List<String> columnNames = getAnalyzedElementsName();

        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            ok.setReturnCode(mess, false);
            return ok;
        }
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        indicToRowMap.clear();
        while (resultSet.next()) {
            EList<Object> objectLs = new BasicEList<Object>();
            Iterator<String> it = columnNames.iterator();
            while (it.hasNext()) {
                Object obj = resultSet.getObject(it.next());
                if (obj != null && (PluginConstant.EMPTY_STRING.equals(obj.toString().trim()))) {
                    obj = obj.toString().trim();
                }
                objectLs.add(obj);
            }
            if (objectLs.size() == 0) {
                continue;
            }
            handleObjects(objectLs, resultSet);
        }

        return ok;
    }

    /**
     * 
     * orgnize EList 'objectLs' for DelimitedFile connection.
     * 
     * @param sqlStatement
     * @param returnCode
     * @return
     */
    private ReturnCode evaluateByDelimitedFile(String sqlStatement, ReturnCode returnCode) {
        DelimitedFileConnection con = (DelimitedFileConnection) analysis.getContext().getConnection();
        String path = con.getFilePath();
        IPath iPath = new Path(path);
        File file = iPath.toFile();
        String separator = con.getFieldSeparatorValue();
        String encoding = con.getEncoding();
        if (!file.exists()) {
            returnCode.setReturnCode(Messages.getString("System can not find the file specified"), false);
            return returnCode;
        }
        CsvReader csvReader = null;
        try {
            // FIXME encoding might be null.
            csvReader = new CsvReader(new BufferedReader(new InputStreamReader(new java.io.FileInputStream(file),
                    encoding == null ? encoding : encoding)), ParameterUtil.trimParameter(separator).charAt(0));

            String rowSep = con.getRowSeparatorValue();
            if (!rowSep.equals("\"\\n\"") && !rowSep.equals("\"\\r\"")) {
                csvReader.setRecordDelimiter(ParameterUtil.trimParameter(rowSep).charAt(0));
            }
            csvReader.setSkipEmptyRecords(true);
            String textEnclosure = con.getTextEnclosure();
            if (textEnclosure != null && textEnclosure.length() > 0) {
                csvReader.setTextQualifier(ParameterUtil.trimParameter(textEnclosure).charAt(0));
            } else {
                csvReader.setUseTextQualifier(false);
            }
            String escapeChar = con.getEscapeChar();
            if (escapeChar == null || escapeChar.equals("\"\\\\\"") || escapeChar.equals("\"\"")) {
                csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
            } else {
                csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
            }

            List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
            EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
            indicToRowMap.clear();
            boolean isBablyForm = false;
            while (csvReader.readRecord()) {
                long currentRow = csvReader.getCurrentRecord();
                if (con.isFirstLineCaption() && currentRow == Long.valueOf("0")) {
                    continue;
                }
                String[] rowValues = csvReader.getValues();
                Object object = null;
                EList<Object> objectLs = new BasicEList<Object>();
                MetadataColumn mColumn = null;
                for (int i = 0; i < analysisElementList.size(); i++) {
                    mColumn = (MetadataColumn) analysisElementList.get(i);
                    Integer position = ColumnHelper.getColumnIndex(mColumn);
                    // MOD qiongli 2011-4-2,bug 20033,warning with a badly form file
                    if (position == null || position >= rowValues.length) {
                        log.warn(Messages.getString("DelimitedFileIndicatorEvaluator.incorrectData",
                                StringUtils.join(rowValues, separator.charAt(1))));
                        if (!isBablyForm) {
                            isBablyForm = true;
                            Display.getDefault().asyncExec(new Runnable() {

                                public void run() {
                                    MessageDialog.openWarning(null,
                                            Messages.getString("DelimitedFileIndicatorEvaluator.badlyForm.Title"),
                                            Messages.getString("DelimitedFileIndicatorEvaluator.badlyForm.Message"));
                                }
                            });
                        }
                        continue;
                    }
                    object = TalendTypeConvert.convertToObject(mColumn.getTalendType(), rowValues[position]);
                    // if (object == null) {
                    // continue;
                    // }
                    objectLs.add(object);

                }
                handleObjects(objectLs, rowValues, mColumn);
            }
        } catch (Exception e) {
            log.error(e, e);
            returnCode.setReturnCode(e.getMessage(), false);
        }

        return returnCode;
    }

    /**
     * 
     * DOC qiongli Comment method "evaluateByMDM".
     * 
     * @param sqlStatement
     * @param returnCode
     * @return
     */
    private ReturnCode evaluateByMDM(String sqlStatement, ReturnCode returnCode) {
        if (mdmWebserviceConn == null || tdXmlDocument == null) {
            returnCode.setOk(false);
            return returnCode;
        }
        MdmStatement statement = mdmWebserviceConn.createStatement();
        String[] resultSet = null;
        // sqlStatement = "//" + tdXmlDocument.getName();
        this.getAnalyzedElements();
        if (continueRun()) {
            try {
                returnCode.setOk(true);
                returnCode.setOk(returnCode.isOk() && statement.execute(tdXmlDocument, sqlStatement));
                // resultSet = statement.getResultSet();
                List<String> strResultList = Arrays.asList(statement.getResultSet());
                resultSet = strResultList.toArray(new String[strResultList.size()]);
            } catch (RemoteException e) {
                returnCode.setMessage(e.getMessage());
            } catch (ServiceException e) {
                returnCode.setMessage(e.getMessage());
            }
        }
        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            returnCode.setReturnCode(mess, false);
            return returnCode;
        }
        List<Map<String, String>> resultSetList = new ArrayList<Map<String, String>>();
        List<ModelElement> analysisElementList = this.analysis.getContext().getAnalysedElements();
        TdXmlElementType parentElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(XmlElementHelper
                .getParentElement(SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(analysisElementList.get(0))));
        List<TdXmlElementType> columnList = org.talend.cwm.db.connection.ConnectionUtils.getXMLElements(parentElement);
        if (analysis.getParameters().isStoreData()) {
            resultSetList = statement.tidyResultSet(columnList.toArray(new ModelElement[columnList.size()]), resultSet);
        } else {
            resultSetList = statement.tidyResultSet(analysisElementList.toArray(new ModelElement[analysisElementList.size()]),
                    resultSet);
        }
        List<String> columnNames = getAnalyzedElementsName();
        for (int i = 0; i < resultSetList.size(); i++) {
            Map<String, String> rowMap = (Map<String, String>) resultSetList.get(i);
            EList<Object> objectLs = new BasicEList<Object>();
            Iterator<String> it = columnNames.iterator();
            while (it.hasNext()) {
                Object obj = rowMap.get(it.next());
                if (obj != null && (PluginConstant.EMPTY_STRING.equals(obj.toString().trim()))) {
                    obj = obj.toString().trim();
                }
                objectLs.add(obj);
            }
            if (objectLs.size() == 0) {
                continue;
            }
            handleObjects(rowMap, objectLs, columnList);

        }
        return returnCode;
    }

    /**
     * 
     * DOC qiongli Comment method "handleObjects".
     * 
     * @param objectLs
     * @throws SQLException
     */
    private void handleObjects(EList<Object> objectLs, ResultSet resultSet) throws SQLException {
        if (objectLs.size() == 0)
            return;
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        int recordIncrement = 0;
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                indicator.handle(objectLs);
                // feature 19192 ,save data for drill down RowCountIndicator.
                if (indicator instanceof SimpleStatIndicator) {
                    for (Indicator leafIndicator : ((SimpleStatIndicator) indicator).getLeafIndicators()) {
                        if (!(leafIndicator instanceof RowCountIndicator) || !indicator.isStoreData()) {
                            continue;
                        }

                        List<Object[]> valueObjectList = initDataSet(leafIndicator, indicToRowMap);
                        recordIncrement = valueObjectList.size();
                        if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                            for (int j = 0; j < resultSet.getMetaData().getColumnCount(); j++) {
                                List<TdColumn> columnList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH
                                        .doSwitch(((ColumnSetMultiValueIndicator) indicator).getAnalyzedColumns().get(0)
                                                .eContainer()));
                                String newcol = columnList.get(j).getName();
                                Object newobject = resultSet.getObject(newcol);
                                if (newobject != null && !(newobject instanceof String)
                                        && newobject.toString().indexOf("TIMESTAMP") > -1) {
                                    newobject = resultSet.getTimestamp(newcol);
                                }
                                // if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                if (recordIncrement < valueObjectList.size()) {
                                    valueObjectList.get(recordIncrement)[j] = newobject;
                                } else {
                                    Object[] valueObject = new Object[resultSet.getMetaData().getColumnCount()];
                                    valueObject[j] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                                // }
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 
     * handle Objects and store data for delimited file .
     * 
     * @param objectLs
     * @param rowValues
     * @param metadataColumn is one of analysedElements.it is used to get its Table then get the table's columns.
     */
    private void handleObjects(EList<Object> objectLs, String[] rowValues, MetadataColumn metadataColumn) {
        if (objectLs.size() == 0 || metadataColumn == null)
            return;

        EList<Indicator> indicators = analysis.getResults().getIndicators();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        int recordIncrement = 0;
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                indicator.handle(objectLs);
                // feature 19192,store all rows value for RowCountIndicator
                if (indicator instanceof SimpleStatIndicator) {
                    SimpleStatIndicator simpIndi = (SimpleStatIndicator) indicator;
                    for (Indicator leafIndicator : simpIndi.getLeafIndicators()) {
                        if (!(leafIndicator instanceof RowCountIndicator) || !analysis.getParameters().isStoreData()) {
                            continue;
                        }
                        List<Object[]> valueObjectList = initDataSet(leafIndicator, indicToRowMap);
                        List<MetadataColumn> columnList = ((MetadataTable) ColumnHelper
                                .getColumnOwnerAsMetadataTable(metadataColumn)).getColumns();
                        recordIncrement = valueObjectList.size();

                        Object[] valueObject = new Object[columnList.size()];
                        if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                            for (int j = 0; j < columnList.size(); j++) {
                                Object newobject = PluginConstant.EMPTY_STRING;
                                // if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                if (j < rowValues.length) {
                                    newobject = rowValues[j];
                                }
                                if (recordIncrement < valueObjectList.size()) {
                                    valueObjectList.get(recordIncrement)[j] = newobject;
                                } else {
                                    valueObject[j] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                                // }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * handle Objects and store data for MDM.
     * 
     * @param rowMap
     * @param objectLs
     * @param columnList
     */
    private void handleObjects(Map<String, String> rowMap, EList<Object> objectLs, List<TdXmlElementType> columnList) {
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        int recordIncrement = 0;
        for (Indicator indicator : indicators) {
            if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
                indicator.handle(objectLs);
                if (indicator instanceof SimpleStatIndicator) {
                    if (!indicator.isStoreData()) {
                        break;
                    }
                    SimpleStatIndicator simpIndi = (SimpleStatIndicator) indicator;
                    for (Indicator leafIndicator : simpIndi.getLeafIndicators()) {
                        if (!(leafIndicator instanceof RowCountIndicator)) {
                            continue;
                        }
                        List<Object[]> valueObjectList = initDataSet(leafIndicator, indicToRowMap);
                        recordIncrement = valueObjectList.size();

                        int offset = 0;
                        for (TdXmlElementType columnElement : columnList) {
                            Object newobject = rowMap.get(columnElement.getName());
                            if (recordIncrement < analysis.getParameters().getMaxNumberRows()) {
                                if (recordIncrement < valueObjectList.size()) {
                                    valueObjectList.get(recordIncrement)[offset] = newobject;
                                } else {
                                    Object[] valueObject = new Object[columnList.size()];
                                    valueObject[offset] = newobject;
                                    valueObjectList.add(valueObject);
                                }
                            }
                            offset++;
                        }
                    }
                }
            }
        }
    }

    /*
     * ADD yyi 2011-02-22 17871:delimitefile
     * 
     * @see org.talend.dq.indicators.Evaluator#checkConnection()
     */
    @Override
    protected ReturnCode checkConnection() {
        if (isDelimitedFile) {
            return new ReturnCode();
        } else if (isMdm) {
            if (mdmWebserviceConn.checkDatabaseConnection().isOk()) {
                return new ReturnCode(true);
            }
        }
        return super.checkConnection();
    }

    /*
     * ADD yyi 2011-02-24 17871:delimitefile
     * 
     * @see org.talend.dq.indicators.Evaluator#closeConnection()
     */
    @Override
    protected ReturnCode closeConnection() {
        if (isDelimitedFile || isMdm) {
            return new ReturnCode();
        }
        return super.closeConnection();
    }

    protected List<Object[]> initDataSet(Indicator indicator, EMap<Indicator, AnalyzedDataSet> indicToRowMap) {
        AnalyzedDataSet analyzedDataSet = indicToRowMap.get(indicator);
        List<Object[]> valueObjectList = null;
        if (analyzedDataSet == null) {
            analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
            indicToRowMap.put(indicator, analyzedDataSet);
            analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
            analyzedDataSet.setRecordSize(0);
        }
        valueObjectList = analyzedDataSet.getData();
        if (valueObjectList == null) {
            valueObjectList = new ArrayList<Object[]>();
            analyzedDataSet.setData(valueObjectList);
        }

        return valueObjectList;
    }

    /**
     * 
     * store data which from 'simpleIndicator.getListRows()' except RowCountIndicator.
     * 
     * @param indicToRowMap
     */
    private void storeDataSet() {
        EMap<Indicator, AnalyzedDataSet> indicToRowMap = analysis.getResults().getIndicToRowMap();
        for (Indicator indicator : analysis.getResults().getIndicators()) {
            if (indicator instanceof SimpleStatIndicator) {
                SimpleStatIndicator simpleIndicator = (SimpleStatIndicator) indicator;
                List<Object[]> listRows = simpleIndicator.getListRows();
                if (!indicator.isStoreData() || listRows == null || listRows.isEmpty()) {
                    break;
                }
                for (Indicator leafIndicator : simpleIndicator.getLeafIndicators()) {
                    if (leafIndicator instanceof RowCountIndicator) {
                        continue;
                    }
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    AnalyzedDataSet analyzedDataSet = indicToRowMap.get(leafIndicator);
                    if (analyzedDataSet == null) {
                        analyzedDataSet = AnalysisFactory.eINSTANCE.createAnalyzedDataSet();
                        indicToRowMap.put(leafIndicator, analyzedDataSet);
                        analyzedDataSet.setDataCount(analysis.getParameters().getMaxNumberRows());
                        analyzedDataSet.setRecordSize(0);
                    }

                    for (int i = 0; i < listRows.size(); i++) {
                        Object[] object = listRows.get(i);
                        // the last element store the count value.
                        Object count = object[object.length > 0 ? object.length - 1 : 0];
                        if (leafIndicator instanceof DistinctCountIndicator) {
                            dataList.add(object);
                        } else if (leafIndicator instanceof UniqueCountIndicator) {
                            if (count != null && NumberUtils.isNumber(count + PluginConstant.EMPTY_STRING)) {
                                if (Long.valueOf(count + PluginConstant.EMPTY_STRING).longValue() == 1) {
                                    dataList.add(object);
                                }
                            }
                        } else if (leafIndicator instanceof DuplicateCountIndicator) {
                            if (count != null && NumberUtils.isNumber(count + PluginConstant.EMPTY_STRING)) {
                                if (Long.valueOf(count + PluginConstant.EMPTY_STRING).longValue() > 1) {
                                    dataList.add(object);
                                }
                            }
                        }
                    }
                    analyzedDataSet.setData(dataList);
                }
            }
        }
    }

    public TdXmlSchema getTdXmlDocument() {
        return this.tdXmlDocument;
    }

    public void setTdXmlDocument(TdXmlSchema tdXmlDocument) {
        this.tdXmlDocument = tdXmlDocument;
    }

    public MdmWebserviceConnection getMdmWebserviceConn() {
        return this.mdmWebserviceConn;
    }

    public void setMdmWebserviceConn(MdmWebserviceConnection mdmWebserviceConn) {
        this.mdmWebserviceConn = mdmWebserviceConn;
    }

    @Override
    public ReturnCode evaluateIndicators(String sqlStatement, boolean closeConnection) {
        ReturnCode returnCode = super.evaluateIndicators(sqlStatement, closeConnection);
        storeDataSet();
        return returnCode;
    }
}

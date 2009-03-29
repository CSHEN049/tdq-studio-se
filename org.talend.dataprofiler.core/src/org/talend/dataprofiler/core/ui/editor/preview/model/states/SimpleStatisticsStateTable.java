// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview.model.states;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.jfree.chart.JFreeChart;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.preview.TableIndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.dataset.CustomerDefaultCategoryDataset;
import org.talend.dataprofiler.core.ui.editor.preview.model.entity.TableStructureEntity;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.BaseChartTableLabelProvider;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ChartTableProviderClassSet.CommonContenteProvider;
import org.talend.dataprofiler.core.ui.utils.ChartDecorator;
import org.talend.dq.analysis.explore.DataExplorer;
import org.talend.dq.analysis.explore.SimpleStatisticsExplorer;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class SimpleStatisticsStateTable extends AbstractChartTypeStatesTable {

    public SimpleStatisticsStateTable(List<TableIndicatorUnit> units) {
        super(units);
    }

    public JFreeChart getChart() {
        JFreeChart chart = TopChartFactory.create3DBarChart(DefaultMessagesImpl
                .getString("SimpleStatisticsStateTable.SimpleStatistics"), getDataset(), true);
        ChartDecorator.decorate(chart);
        return chart;
    }

    public ICustomerDataset getCustomerDataset() {
        CustomerDefaultCategoryDataset customerdataset = new CustomerDefaultCategoryDataset();
        for (TableIndicatorUnit unit : units) {
            double value = Double.parseDouble(unit.getValue().toString());
            String label = unit.getIndicatorName();

            customerdataset.addValue(value, label, ""); //$NON-NLS-1$

            ChartDataEntity entity = new ChartDataEntity();
            entity.setIndicator(unit.getIndicator());
            entity.setLabel(label);
            entity.setValue(String.valueOf(value));
            entity.setPercent(String.valueOf(value / unit.getIndicator().getCount()));

            customerdataset.addDataEntity(entity);
        }
        return customerdataset;
    }

    public DataExplorer getDataExplorer() {
        return new SimpleStatisticsExplorer();
    }

    public JFreeChart getExampleChart() {
        return null;
    }

    @Override
    protected TableStructureEntity getTableStructure() {
        TableStructureEntity entity = new TableStructureEntity();
        entity
                .setFieldNames(new String[] {
                        DefaultMessagesImpl.getString("SimpleStatisticsStateTable.Label"), DefaultMessagesImpl.getString("SimpleStatisticsStateTable.Count"), "%" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        entity.setFieldWidths(new Integer[] { 200, 150, 150 });
        return entity;
    }

    @Override
    protected ITableLabelProvider getLabelProvider() {
        return new BaseChartTableLabelProvider();
    }

    @Override
    protected IStructuredContentProvider getContentProvider() {
        return new CommonContenteProvider();
    }

    public String getReferenceLink() {
        return null;
    }
}

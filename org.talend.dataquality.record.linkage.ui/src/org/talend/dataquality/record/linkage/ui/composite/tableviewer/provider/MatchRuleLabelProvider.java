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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dq.helper.CustomAttributeMatcherHelper;

/**
 * created by zshen on Aug 1, 2013 Detailled comment
 * 
 */
public class MatchRuleLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof MatchKeyDefinition) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) element;
            switch (columnIndex) {
            case 0:
                return mkd.getName();
            case 1:
                return mkd.getColumn();
            case 2:
                return AttributeMatcherType.valueOf(mkd.getAlgorithm().getAlgorithmType()).getLabel();
            case 3:
                return CustomAttributeMatcherHelper.getClassName(mkd.getAlgorithm().getAlgorithmParameters());
            case 4:
                return String.valueOf(mkd.getConfidenceWeight());
            case 5:
                return HandleNullEnum.getTypeByValue(mkd.getHandleNull()).getLabel();
            }

        }
        return StringUtils.EMPTY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
     */
    @Override
    public Color getForeground(Object element, int columnIndex) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
     */
    @Override
    public Color getBackground(Object element, int columnIndex) {
        if (element instanceof MatchKeyDefinition) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) element;
            switch (columnIndex) {

            case 3:
                boolean takeParameter = AttributeMatcherType.valueOf(mkd.getAlgorithm().getAlgorithmType()).equals(
                        AttributeMatcherType.CUSTOM);
                return getCellColor(takeParameter);

            }

        }
        return null;
    }

    /**
     * DOC zshen Comment method "getCellColor".
     * 
     * @param takeParameter
     * @return
     */
    protected Color getCellColor(boolean takeParameter) {
        return Display.getDefault().getSystemColor(takeParameter ? SWT.COLOR_WHITE : SWT.COLOR_GRAY);
    }

}

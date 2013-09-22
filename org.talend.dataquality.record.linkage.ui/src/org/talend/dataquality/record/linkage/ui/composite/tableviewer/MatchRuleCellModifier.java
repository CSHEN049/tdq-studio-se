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
package org.talend.dataquality.record.linkage.ui.composite.tableviewer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.TableItem;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.rules.MatchKeyDefinition;

/**
 * created by zshen on Aug 1, 2013 Detailled comment
 * 
 */
public class MatchRuleCellModifier extends AbstractMatchCellModifier<MatchKeyDefinition> {

    /**
     * DOC zshen MatchRuleCellModifier constructor comment.
     */
    public MatchRuleCellModifier(AbstractMatchAnalysisTableViewer<MatchKeyDefinition> newTableViewer) {
        this.tableViewer = newTableViewer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean canModify(Object element, String property) {
        if (element != null && element instanceof MatchKeyDefinition) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) element;
            if (MatchAnalysisConstant.CUSTOM_MATCHER_CLASS.equalsIgnoreCase(property)) {
                return AttributeMatcherType.getTypeBySavedValue(mkd.getAlgorithm().getAlgorithmType()).isTakeParameter();
            } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
                return columnList.size() > 0;
            }
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getValue(Object element, String property) {
        MatchKeyDefinition mkd = (MatchKeyDefinition) element;
        if (MatchAnalysisConstant.HANDLE_NULL.equalsIgnoreCase(property)) {
            return HandleNullEnum.getTypeByValue(mkd.getHandleNull()).getIndex();
        } else if (MatchAnalysisConstant.MATCHING_TYPE.equalsIgnoreCase(property)) {
            return AttributeMatcherType.getTypeBySavedValue(mkd.getAlgorithm().getAlgorithmType()).getIndex();
        } else if (MatchAnalysisConstant.CUSTOM_MATCHER_CLASS.equalsIgnoreCase(property)) {
            return mkd.getAlgorithm().getAlgorithmParameters();
        } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
            return columnList.indexOf(mkd.getColumn());
        } else if (MatchAnalysisConstant.CONFIDENCE_WEIGHT.equalsIgnoreCase(property)) {
            return String.valueOf(mkd.getConfidenceWeight());
        } else if (MatchAnalysisConstant.MATCH_KEY_NAME.equalsIgnoreCase(property)) {
            return mkd.getName();
        } else if (MatchAnalysisConstant.THRESHOLD.equalsIgnoreCase(property)) {
            return String.valueOf(mkd.getThreshold());
        }
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public void modify(Object element, String property, Object value) {
        if (element instanceof TableItem) {
            MatchKeyDefinition mkd = (MatchKeyDefinition) ((TableItem) element).getData();
            String newValue = String.valueOf(value);
            if (MatchAnalysisConstant.HANDLE_NULL.equalsIgnoreCase(property)) {
                HandleNullEnum valueByIndex = HandleNullEnum.getTypeByIndex(Integer.valueOf(newValue).intValue());
                if (StringUtils.equals(mkd.getHandleNull(), valueByIndex.getValue())) {
                    return;
                }
                mkd.setHandleNull(valueByIndex.getValue());
            } else if (MatchAnalysisConstant.MATCHING_TYPE.equalsIgnoreCase(property)) {
                AttributeMatcherType valueByIndex = AttributeMatcherType.getTypeByIndex(Integer.valueOf(newValue).intValue());
                if (StringUtils.equals(mkd.getAlgorithm().getAlgorithmType(), valueByIndex.getComponentName())) {
                    return;
                }
                mkd.getAlgorithm().setAlgorithmType(valueByIndex.getComponentName());
                mkd.getAlgorithm().setAlgorithmParameters(StringUtils.EMPTY);
            } else if (MatchAnalysisConstant.CUSTOM_MATCHER_CLASS.equalsIgnoreCase(property)) {
                if (StringUtils.equals(mkd.getAlgorithm().getAlgorithmParameters(), newValue)) {
                    return;
                }
                mkd.getAlgorithm().setAlgorithmParameters(String.valueOf(value));
            } else if (MatchAnalysisConstant.COLUMN.equalsIgnoreCase(property)) {
                if (Integer.parseInt(newValue) == -1) {
                    return;
                }
                String columnName = columnList.get(Integer.parseInt(newValue));
                if (StringUtils.equals(mkd.getColumn(), columnName)) {
                    return;
                }
                mkd.setColumn(columnName);
                tableViewer.noticeColumnSelectChange();
            } else if (MatchAnalysisConstant.CONFIDENCE_WEIGHT.equalsIgnoreCase(property)) {
                if (mkd.getConfidenceWeight() == Integer.valueOf(newValue).intValue()) {
                    return;
                }
                mkd.setConfidenceWeight(Integer.valueOf(newValue).intValue());
            } else if (MatchAnalysisConstant.MATCH_KEY_NAME.equalsIgnoreCase(property)) {
                if (StringUtils.equals(mkd.getName(), newValue)) {
                    return;
                }
                mkd.setName(newValue);
            } else if (MatchAnalysisConstant.THRESHOLD.equalsIgnoreCase(property)) {
                if (mkd.getThreshold() == Double.parseDouble(newValue)) {
                    return;
                }
                try {
                    mkd.setThreshold(Double.parseDouble(newValue));
                } catch (NumberFormatException e) {
                    // revert user change at here so don't need do anything
                }
            } else {
                return;
            }
            tableViewer.update(mkd, null);
        }
    }

}

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
package org.talend.dataquality.record.linkage.attribute;

/**
 * Abstract matcher class for shared operations like blank string checking.
 */
public abstract class AbstractAttributeMatcher implements IAttributeMatcher {

    private NullOption nullOption = NullOption.nullMatchNull;

    private String attributeName = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String,
     * java.lang.String)
     */
    public double getMatchingWeight(String str1, String str2) {
        switch (nullOption) {
        case nullMatchAll:
            if (isNullOrEmpty(str1) || isNullOrEmpty(str2)) {
                return 1.0;
            }
            break;
        case nullMatchNone:
            if (isNullOrEmpty(str1) || isNullOrEmpty(str2)) {
                return 0.0;
            }
            break;
        case nullMatchNull:
            boolean str1IsNull = isNullOrEmpty(str1);
            boolean str2IsNull = isNullOrEmpty(str2);
            if (str1IsNull && str2IsNull) { // both null => match
                return 1.0;
            } else if (str1IsNull || str2IsNull) { // only one null => non-match
                return 0.0;
            }
            break;
        default:
            break;
        }

        assert !isNullOrEmpty(str1) : "string should not be null or empty here"; //$NON-NLS-1$
        assert !isNullOrEmpty(str2) : "string should not be null or empty here"; //$NON-NLS-1$
        return getWeight(str1, str2);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str); //$NON-NLS-1$ 
    }

    /**
     * Calculate matching weight using specified matcher.
     * 
     * @param record1 the first string
     * @param record2 the secord string
     * @return result between 0 and 1
     */
    protected abstract double getWeight(String record1, String record2);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setNullOption(org.talend.dataquality.record
     * .linkage.attribute.IAttributeMatcher.NullOption)
     */
    public void setNullOption(NullOption option) {
        this.nullOption = option;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getAttributeName()
     */
    public String getAttributeName() {
        return attributeName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#setAttributeName(java.lang.String)
     */
    public void setAttributeName(String name) {
        this.attributeName = name;
    }

}

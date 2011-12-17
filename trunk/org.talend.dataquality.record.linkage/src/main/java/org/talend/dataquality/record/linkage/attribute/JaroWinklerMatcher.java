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
package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.StringComparisonUtil;

/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class JaroWinklerMatcher extends JaroMatcher {

    /**
     * prefix adjustment scale.
     */
    private static final double PREFIXADUSTMENTSCALE = 0.1;


    /* (non-Javadoc)
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.jaroWinkler;
    }

    /* (non-Javadoc)
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String, java.lang.String)
     */
    public double getMatchingWeight(String str1, String str2) {
        double dist = super.getMatchingWeight(str1, str2);
        // This extension modifies the weights of poorly matching pairs string1, string2 which share a common prefix
        final int prefixLength = StringComparisonUtil.getPrefixLength(str1, str2);
        return dist + (prefixLength * PREFIXADUSTMENTSCALE * (1 - dist));
    }

}

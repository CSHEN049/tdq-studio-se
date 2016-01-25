// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.Functions;

import java.io.Serializable;

/**
 * created by jgonzalez on 22 juin 2015. This class is used when the requested function is BetweenIndexesKeep. It will
 * return a new String that only contains the input elements that are between the bounds given as parameter.
 *
 */
public class BetweenIndexesKeep extends BetweenIndexes<String> implements Serializable {

    private static final long serialVersionUID = 1913164034646800125L;

    @Override
    public String generateMaskedRow(String str) {
        if (super.check(str, 2)) {
            super.setBounds(str);
            return str.substring(begin - 1, end);
        } else {
            if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
                return str;
            } else {
                return EMPTY_STRING;
            }
        }
    }
}

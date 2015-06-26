// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

/**
 * created by jgonzalez on 24 juin 2015. See GgenerateFromFileHash.
 *
 */
public class GenerateFromFileHashString extends GenerateFromFileHash<String> {

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            super.init();
            if (StringTokens.size() > 0) {
                if (str == null || EMPTY_STRING.equals(str)) {
                    return StringTokens.get(rnd.nextInt(StringTokens.size()));
                } else {
                    return StringTokens.get(Math.abs(str.hashCode()) % StringTokens.size());
                }
            } else {
                return EMPTY_STRING;
            }
        }
    }
}

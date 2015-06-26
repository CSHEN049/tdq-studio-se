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
 * created by jgonzalez on 22 juin 2015. See KeepFirstAndGenerate.
 *
 */
public class KeepFirstAndGenerateString extends KeepFirstAndGenerate<String> {

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null) || EMPTY_STRING.equals(str) && keepNull) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder(EMPTY_STRING);
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                String s = str.trim();
                if (integerParam > s.length()) {
                    integerParam = s.length() - 2;
                }
                for (int i = 0; i < integerParam; ++i) {
                    sb.append(s.charAt(i));
                    if (!Character.isDigit(s.charAt(i))) {
                        integerParam++;
                    }
                }
                for (int i = integerParam; i < s.length(); ++i) {
                    if (Character.isDigit(s.charAt(i))) {
                        sb.append(rnd.nextInt(9));
                    } else {
                        sb.append(s.charAt(i));
                    }
                }
            }
            return sb.toString();
        }
    }
}
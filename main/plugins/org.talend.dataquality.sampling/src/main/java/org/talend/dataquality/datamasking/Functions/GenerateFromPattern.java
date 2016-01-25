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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 17 juil. 2015 Detailled comment
 *
 */
public class GenerateFromPattern extends Function<String> implements Serializable {

    private static final long serialVersionUID = 7920843158759995757L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return null;
        } else {
            StringBuilder result = new StringBuilder(EMPTY_STRING);
            int count = 0;
            for (int i = 0; i < parameters[0].length(); ++i, ++count) {
                if (count < parameters[0].length()) {
                    switch (parameters[0].charAt(count)) {
                    case 'A':
                        result.append(UPPER.charAt(rnd.nextInt(26)));
                        break;
                    case 'a':
                        result.append(LOWER.charAt(rnd.nextInt(26)));
                        break;
                    case '9':
                        result.append(rnd.nextInt(9));
                        break;
                    case '\\':
                        if (parameters[0].charAt(count + 1) - 48 <= parameters.length) {
                            result.append(parameters[parameters[0].charAt(count + 1) - 48].trim());
                        }
                        count++;
                        break;
                    default:
                        if (!Character.isLetterOrDigit(parameters[0].charAt(count))) {
                            result.append(parameters[0].charAt(count));
                        }
                        break;
                    }
                }
            }
            return result.toString();
        }
    }

}

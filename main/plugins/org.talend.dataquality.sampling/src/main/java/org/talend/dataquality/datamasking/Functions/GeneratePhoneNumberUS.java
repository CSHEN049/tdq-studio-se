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

import java.io.Serializable;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 16 juil. 2015 Detailled comment
 *
 */
public class GeneratePhoneNumberUS extends Function<String> implements Serializable {

    private static final long serialVersionUID = 1160032103743243299L;

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return null;
        } else {
            StringBuilder result = new StringBuilder(EMPTY_STRING);
            result.append(rnd.nextInt(8) + 2);
            result.append(rnd.nextInt(9));
            int tmp = 0;
            do {
                tmp = rnd.nextInt(9);
            } while (tmp == result.charAt(1) - 48);
            result.append(tmp);
            result.append("-"); //$NON-NLS-1$
            result.append(rnd.nextInt(8) + 2);
            result.append(rnd.nextInt(9));
            if (result.charAt(5) == 1) {
                do {
                    tmp = rnd.nextInt(9);
                } while (tmp == 1);
                result.append(tmp);
            } else {
                result.append(rnd.nextInt(9));
            }
            result.append("-"); //$NON-NLS-1$
            for (int i = 0; i < 4; ++i) {
                result.append(rnd.nextInt(9));
            }
            return result.toString();
        }
    }
}

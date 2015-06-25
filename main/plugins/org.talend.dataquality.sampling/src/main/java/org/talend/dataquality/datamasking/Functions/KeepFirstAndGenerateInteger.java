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
public class KeepFirstAndGenerateInteger extends KeepFirstAndGenerate<Integer> {

    @Override
    public Integer generateMaskedRow(Integer i) {
        if (i == null && keepNull) {
            return null;
        } else {
            if (i != null && integerParam > 0) {
                if ((int) Math.log10(i) + 1 < integerParam) {
                    integerParam = (int) Math.log10(i) + 1;
                }
                sb = new StringBuilder(i.toString().substring(0, integerParam));
                for (int j = integerParam; j < i.toString().length(); ++j) {
                    sb.append(rnd.nextInt(9));
                }
                return Integer.parseInt(sb.toString());
            } else {
                return 0;
            }
        }
    }
}

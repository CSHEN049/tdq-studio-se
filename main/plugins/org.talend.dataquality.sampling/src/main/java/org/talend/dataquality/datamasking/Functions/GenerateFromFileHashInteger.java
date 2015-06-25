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

import java.util.ArrayList;
import java.util.List;

/**
 * created by jgonzalez on 24 juin 2015. See GgenerateFromFileHash.
 *
 */
public class GenerateFromFileHashInteger extends GenerateFromFileHash<Integer> {

    private List<Integer> IntegerTokens = new ArrayList<>();

    @Override
    public Integer generateMaskedRow(Integer i) {
        if (i == null && keepNull) {
            return null;
        } else {
            super.init();
            for (int j = 0; j < StringTokens.size(); ++j) {
                int tmp = 0;
                try {
                    tmp = Integer.parseInt(StringTokens.get(j));
                } catch (NumberFormatException e) {
                    // Do Nothing
                }
                IntegerTokens.add(tmp);
            }
            if (IntegerTokens.size() > 0) {
                if (i == null) {
                    return IntegerTokens.get(rnd.nextInt(IntegerTokens.size()));
                } else {
                    return IntegerTokens.get(Math.abs(i.hashCode() % IntegerTokens.size()));
                }
            } else {
                return 0;
            }
        }
    }
}

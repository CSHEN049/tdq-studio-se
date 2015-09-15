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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 8 sept. 2015 Detailled comment
 *
 */
public abstract class GenerateSequence<T2> extends Function<T2> {

    protected int seq = 0;

    public int setSeq(String s) {
        int i = 0;
        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            // Do nothing
        }
        return i;
    }

    @Override
    public abstract T2 generateMaskedRow(T2 t);

}

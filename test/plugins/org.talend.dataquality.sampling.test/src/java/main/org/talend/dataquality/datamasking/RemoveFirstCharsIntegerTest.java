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
package org.talend.dataquality.datamasking;

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.RemoveFirstCharsInteger;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class RemoveFirstCharsIntegerTest {

    private int input = 666;

    private int output;

    private RemoveFirstCharsInteger rfci = new RemoveFirstCharsInteger();

    @Test
    public void test() {
        rfci.integerParam = 2;
        output = rfci.generateMaskedRow(input);
        assertEquals(output, 6);
    }

    @Test
    public void testDummyGood() {
        rfci.integerParam = 10;
        output = rfci.generateMaskedRow(input);
        assertEquals(output, 0);
    }

}

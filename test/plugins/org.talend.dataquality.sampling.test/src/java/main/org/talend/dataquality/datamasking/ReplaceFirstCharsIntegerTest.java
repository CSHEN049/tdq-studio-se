// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.ReplaceFirstCharsInteger;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class ReplaceFirstCharsIntegerTest {

    private String output;

    private Integer input = 123456;

    private ReplaceFirstCharsInteger rfci = new ReplaceFirstCharsInteger();

    @Before
    public void setUp() throws Exception {
        rfci.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        rfci.integerParam = 3;
        output = rfci.generateMaskedRow(input).toString();
        assertEquals(output, "830456"); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        rfci.integerParam = 7;
        output = rfci.generateMaskedRow(input).toString();
        assertEquals(output, "830807"); //$NON-NLS-1$
    }

}

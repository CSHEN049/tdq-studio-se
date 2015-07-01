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

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateCreditCardString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateCreditCardStringTest {

    private String output;

    private GenerateCreditCardString gccs = new GenerateCreditCardString();

    @Before
    public void setUp() throws Exception {
        gccs.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void test() {
        output = gccs.generateMaskedRow(null).toString();
        assertEquals(output, "4384055893226268"); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gccs.keepNull = true;
        output = String.valueOf(gccs.generateMaskedRow(null));
        assertEquals(output, "null"); //$NON-NLS-1$
    }

}

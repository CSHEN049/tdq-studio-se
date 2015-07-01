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
import org.talend.dataquality.datamasking.Functions.GenerateBetweenString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateBetweenStringTest {

    private String output;

    private GenerateBetweenString gbs = new GenerateBetweenString();

    @Before
    public void setUp() throws Exception {
        gbs.setRandomWrapper(new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        gbs.parameters = "10,20".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gbs.generateMaskedRow(gbs.EMPTY_STRING);
        assertEquals(output, "17"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        gbs.parameters = "jk,df".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = gbs.generateMaskedRow(gbs.EMPTY_STRING);
        assertEquals(output, "0"); //$NON-NLS-1$
    }

}

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
import org.talend.dataquality.datamasking.Functions.BetweenIndexesKeep;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class BetweenIndexesKeepTest {

    private BetweenIndexesKeep bik = new BetweenIndexesKeep();

    private String input = "Steve"; //$NON-NLS-1$

    private String output;

    @Test
    public void testGood() {
        bik.parameters = "2, 4".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bik.generateMaskedRow(input);
        assertEquals("tev", output); //$NON-NLS-1$
    }

    @Test
    public void testDummyGood() {
        bik.parameters = "-1, 8".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bik.generateMaskedRow(input);
        assertEquals("Steve", output); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        bik.parameters = "1".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }

    @Test
    public void testBad2() {
        bik.parameters = "lk, df".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        output = bik.generateMaskedRow(input);
        assertEquals("", output); //$NON-NLS-1$
    }
}

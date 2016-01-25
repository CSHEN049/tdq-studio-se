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
package org.talend.dataquality.datamasking;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.datamasking.Functions.GenerateFromFileString;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 29 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileStringTest {

    private String output;

    static final String path = GenerateFromFileStringTest.class.getResource("/data/name.txt").getFile(); //$NON-NLS-1$

    private GenerateFromFileString gffs = new GenerateFromFileString();

    @Before
    public void setUp() throws Exception {
        gffs.parse(path, false, new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        output = gffs.generateMaskedRow(null);
        assertEquals("Brad", output); //$NON-NLS-1$
    }

    @Test
    public void testNull() {
        gffs.keepNull = true;
        output = gffs.generateMaskedRow(null);
        assertEquals(null, output);
    }

}

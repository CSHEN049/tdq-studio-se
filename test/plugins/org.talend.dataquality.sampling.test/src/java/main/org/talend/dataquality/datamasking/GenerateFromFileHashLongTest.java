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
import org.talend.dataquality.datamasking.Functions.GenerateFromFileHashLong;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 30 juin 2015 Detailled comment
 *
 */
public class GenerateFromFileHashLongTest {

    private String output;

    private String path = "/home/jgonzalez/Bureau/data/numbers.txt"; //$NON-NLS-1$

    private GenerateFromFileHashLong gffhl = new GenerateFromFileHashLong();

    @Before
    public void setUp() throws Exception {
        gffhl.setRandomWrapper(new RandomWrapper(42));
        gffhl.parameters = path.split(","); //$NON-NLS-1$
    }

    @Test
    public void testGood() {
        output = gffhl.generateMaskedRow(null).toString();
        assertEquals(output, "10"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        gffhl.keepNull = true;
        output = gffhl.generateMaskedRow(0L).toString();
        assertEquals(output, "10"); //$NON-NLS-1$
    }
}

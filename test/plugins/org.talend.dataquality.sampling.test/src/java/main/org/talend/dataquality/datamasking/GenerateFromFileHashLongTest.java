// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

    private String path = GenerateFromFileHashIntegerTest.path;

    private GenerateFromFileHashLong gffhl = new GenerateFromFileHashLong();

    @Before
    public void setUp() throws Exception {
        gffhl.parse(path, false, new RandomWrapper(42));
    }

    @Test
    public void testGood() {
        assertEquals(18, gffhl.generateMaskedRow(101L).longValue());
        assertEquals(9, gffhl.generateMaskedRow(null).longValue());
    }

    @Test
    public void testBad() {
        gffhl.keepNull = true;
        assertEquals(null, gffhl.generateMaskedRow(null));
    }
}

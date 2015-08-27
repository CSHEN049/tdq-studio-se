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
import org.talend.dataquality.datamasking.Functions.MaskEmail;
import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 25 juin 2015 Detailled comment
 *
 */
public class MaskEmailTest {

    private String output;

    private MaskEmail me = new MaskEmail();

    @Test
    public void testGood() {
        String mail = "jugonzalez@talend.com"; //$NON-NLS-1$
        me.parameters = "test".split(","); //$NON-NLS-1$ //$NON-NLS-2$
        me.rnd = new RandomWrapper(42);
        output = me.generateMaskedRow(mail);
        assertEquals(output, "test@talend.com"); //$NON-NLS-1$
    }

    @Test
    public void testBad() {
        String mail = "not an email"; //$NON-NLS-1$
        me.parameters = me.EMPTY_STRING.split(","); //$NON-NLS-1$
        output = me.generateMaskedRow(mail);
        assertEquals(output, "XXXXXXXXXXXX"); //$NON-NLS-1$
    }

}

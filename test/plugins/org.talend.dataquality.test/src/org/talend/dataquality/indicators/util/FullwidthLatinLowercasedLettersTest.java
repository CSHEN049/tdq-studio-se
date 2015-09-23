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
package org.talend.dataquality.indicators.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * DOC talend class global comment. Detailled comment
 */
public class FullwidthLatinLowercasedLettersTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.indicators.util.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     * 
     * case1 normal case
     */
    @Test
    public void testHandleRequestCase1() {
        FullwidthLatinLowercasedLetters fullwidthLatinLowercasedLetters = new FullwidthLatinLowercasedLetters();
        String handleRequest = fullwidthLatinLowercasedLetters.handleRequest("ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ"); //$NON-NLS-1$
        Assert.assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaa", handleRequest); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.indicators.util.ChainResponsibilityHandler#handleRequest(java.lang.String)}.
     * 
     * case 2 special case
     */
    @Test
    public void testHandleRequestCase2() {
        FullwidthLatinLowercasedLetters fullwidthLatinLowercasedLetters = new FullwidthLatinLowercasedLetters();
        String handleRequest = fullwidthLatinLowercasedLetters.handleRequest("abcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        Assert.assertEquals("abcdefghijklmnopqrstuvwxyz", handleRequest); //$NON-NLS-1$
    }

}

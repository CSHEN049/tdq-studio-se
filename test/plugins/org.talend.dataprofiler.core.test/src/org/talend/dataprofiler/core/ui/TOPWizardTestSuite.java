// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * DOC yyi class global comment. Detailled comment
 */
@RunWith(Suite.class)
// @Suite.SuiteClasses( { CreateNewAnalysisWizardTest.class, DatabaseConnectionWizardTest.class })
public class TOPWizardTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.talend.dataprofiler.core.ui");
        // $JUnit-BEGIN$

        // $JUnit-END$
        return suite;
    }

}

// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dq.analysis.parameters.AnalysisParameter;

/**
 * @author zqin
 * 
 */
public class ColumnTimeWizard extends ColumnSetWizard {

    public ColumnTimeWizard(AnalysisParameter parameter) {
        super(parameter);
        setIndicator(ColumnsetFactory.eINSTANCE.createMinMaxDateIndicator());
    }
}

// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.CatalogEvaluator;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CatalogAnalysisExecutor extends AbstactSchemaAnalysisExecutor {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#runAnalysis(org.talend.dataquality.analysis.Analysis,
     * java.lang.String)
     */
    @Override
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        CatalogEvaluator eval = new CatalogEvaluator();
        // MOD xqliu 2009-02-09 bug 6237
        eval.setMonitor(getMonitor());
        // // --- add indicators
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            ModelElement analyzedElement = indicator.getAnalyzedElement();
            if (analyzedElement == null) {
                continue;
            }

            TdCatalog cat = SwitchHelpers.CATALOG_SWITCH.doSwitch(analyzedElement);
            if (cat == null) {
                continue;
            }
            eval.storeIndicator(cat, indicator);
            // ADDED rli 2008-07-10 fixed for the SchemaIndicator will increased after connection analysis running.
            indicator.reset();
        }

        return runAnalysisLow(analysis, sqlStatement, eval);
    }

}

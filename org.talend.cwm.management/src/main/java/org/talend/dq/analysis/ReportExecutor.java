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
package org.talend.dq.analysis;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionHelper;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author scorreia
 * 
 * this class executes the analyses contained in the report that need to be computed.
 */
public class ReportExecutor implements IReportExecutor {

    private static Logger log = Logger.getLogger(ReportExecutor.class);

    private boolean atLeastOneFailure;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.IReportExecutor#execute(org.talend.dataquality.reports.TdReport)
     */
    public ReturnCode execute(TdReport report) {
        atLeastOneFailure = false;
        long startTime = System.currentTimeMillis();
        EList<AnalysisMap> analysisMaps = report.getAnalysisMap();
        StringBuilder strBuilder = new StringBuilder();
        // loop on analysis maps is faster than loop on analyses
        for (AnalysisMap analysisMap : analysisMaps) {
            Analysis analysis = analysisMap.getAnalysis();
            if (analysisMap.isMustRefresh()) {
                if (analysis == null) {
                    return new ReturnCode(
                            Messages.getString("ReportExecutor.CannotEvaluateNullAnalysis", report.getName()), false); //$NON-NLS-1$
                }
                ReturnCode executeRc = AnalysisExecutorSelector.executeAnalysis(analysis);
              
                // ADD msjian TDQ-5952: we should close connections always
                TdqAnalysisConnectionHelper.closeConnectionPool(analysis);
                // TDQ-5952~
                
                if (!executeRc.isOk()) {
                    log.error("Failed to execute analysis " + analysis.getName() + ". Reason: " + executeRc.getMessage());
                    atLeastOneFailure = true;
                }
                if (log.isInfoEnabled()) {
                    strBuilder.append("Report " + report.getName() + ": Analysis " + analysis.getName() + " refreshed. State: "
                            + executeRc.isOk() + "\n");
                }
            } else { // skipped analysis
                if (log.isInfoEnabled()) {
                    strBuilder.append("Report " + report.getName() + ": Analysis " + analysis.getName() + " skipped.\n");
                }
            }
        }
        // log execution
        if (log.isInfoEnabled()) {
            if (strBuilder.length() == 0) {
                log.info("Generating reports for \"" + report.getName() + "\" without refreshing any analysis.");
            } else {
                log.info(strBuilder.toString());
            }
        }

        long endTime = System.currentTimeMillis();
        // fill in the execution informations
        ExecutionInformations execInformations = ReportHelper.getExecutionInformations(report);
        execInformations.setExecutionDate(new Date());
        int duration = (int) (endTime - startTime);
        execInformations.setExecutionDuration(duration);
        execInformations.setExecutionNumber(execInformations.getExecutionNumber() + 1);

        if (atLeastOneFailure) {
            execInformations.setLastRunOk(false);
            String err = Messages.getString("ReportExecutor.AnalysisExecutionFailed", report.getName()); //$NON-NLS-1$
            execInformations.setMessage(err);
            return new ReturnCode(err, false);
        }
        // else
        execInformations.setLastRunOk(true);
        execInformations.setLastExecutionNumberOk(execInformations.getExecutionNumber());
        execInformations.setMessage(null);

        return new ReturnCode();
    }

}

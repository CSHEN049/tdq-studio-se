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
package org.talend.dq.helper;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.softwaredeployment.util.SoftwaredeploymentSwitch;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.util.XmlSwitch;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.util.AnalysisSwitch;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.util.PatternSwitch;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dataquality.rules.DQRule;
import org.talend.dataquality.rules.util.RulesSwitch;
import orgomg.cwm.foundation.softwaredeployment.SoftwareSystem;
import orgomg.cwmx.analysis.informationreporting.Report;
import orgomg.cwmx.analysis.informationreporting.util.InformationreportingSwitch;

/**
 * 
 * This class is a utility class to decide what's the real type of a model element.
 * 
 * Designed by zqin, 2010-6-25
 */
public final class ModelElementIdentifier {

    private ModelElementIdentifier() {
        //
    }

    public static final AnalysisSwitch<Analysis> ANALYSIS = new AnalysisSwitch<Analysis>() {

        public Analysis caseAnalysis(Analysis object) {
            return object;
        };
    };

    public static final InformationreportingSwitch<Report> REPORT = new InformationreportingSwitch<Report>() {

        public Report caseReport(Report object) {
            return object;
        };
    };

    public static final PatternSwitch<Pattern> PATTERN = new PatternSwitch<Pattern>() {

        public Pattern casePattern(Pattern object) {
            return object;
        };
    };

    public static final DefinitionSwitch<IndicatorDefinition> INDICATOR_DEFINITION = new DefinitionSwitch<IndicatorDefinition>() {

        public IndicatorDefinition caseIndicatorDefinition(IndicatorDefinition object) {
            return object;
        };
    };

    public static final RulesSwitch<DQRule> DQ_RULE = new RulesSwitch<DQRule>() {

        public DQRule caseDQRule(DQRule object) {
            return object;
        };
    };

    public static final SoftwaredeploymentSwitch<SoftwareSystem> SOFTWARE = new SoftwaredeploymentSwitch<SoftwareSystem>() {

        public SoftwareSystem caseSoftwareSystem(SoftwareSystem object) {
            return object;
        };
    };

    public static final XmlSwitch<TdXMLDocument> XMLDOC = new XmlSwitch<TdXMLDocument>() {

        public TdXMLDocument caseTdXMLDocument(TdXMLDocument object) {
            return object;
        };
    };

    /**
     * DOC bZhou Comment method "isAnalysis".
     * 
     * Decide it's an analysis or not.
     * 
     * @param element
     * @return
     */
    public static boolean isAnalysis(EObject element) {
        return ANALYSIS.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isReport".
     * 
     * Decide it's a report or not.
     * 
     * @param element
     * @return
     */
    public static boolean isReport(EObject element) {
        return REPORT.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isPattern".
     * 
     * Decide it's a pattern or not.
     * 
     * @param element
     * @return
     */
    public static boolean isPattern(EObject element) {
        return PATTERN.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isID".
     * 
     * Decide it's an indicator defintion or not.
     * 
     * @param element
     * @return
     */
    public static boolean isID(EObject element) {
        return INDICATOR_DEFINITION.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isDQRule".
     * 
     * Decide it's a dq rule or not.
     * 
     * @param element
     * @return
     */
    public static boolean isDQRule(EObject element) {
        return DQ_RULE.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isDataProvider".
     * 
     * Decide it's a data provider or not.
     * 
     * @param element
     * @return
     */
    public static boolean isDataProvider(EObject element) {
        return SwitchHelpers.CONNECTION_SWITCH.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isXMLProvider".
     * 
     * Decide it's a XML provider or not.
     * 
     * @param element
     * @return
     */
    public static boolean isXMLProvider(EObject element) {
        return XMLDOC.doSwitch(element) != null;
    }

    /**
     * DOC bZhou Comment method "isSoftware".
     * 
     * Decide it's a software system or not.
     * 
     * @param element
     * @return
     */
    public static boolean isSoftware(EObject element) {
        return SOFTWARE.doSwitch(element) != null;
    }
}

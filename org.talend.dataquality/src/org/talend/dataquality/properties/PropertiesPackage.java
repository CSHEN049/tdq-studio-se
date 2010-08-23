/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.talend.dataquality.properties.PropertiesFactory
 * @model kind="package"
 * @generated
 */
public interface PropertiesPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "properties";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://dataquality.properties";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "dataquality.properties";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    PropertiesPackage eINSTANCE = org.talend.dataquality.properties.impl.PropertiesPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TDQAnalysisItemImpl <em>TDQ Analysis Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TDQAnalysisItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQAnalysisItem()
     * @generated
     */
    int TDQ_ANALYSIS_ITEM = 0;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ANALYSIS_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ANALYSIS_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ANALYSIS_ITEM__PARENT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ANALYSIS_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__FILENAME;

    /**
     * The feature id for the '<em><b>Analysis</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ANALYSIS_ITEM__ANALYSIS = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>TDQ Analysis Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ANALYSIS_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TDQReportItemImpl <em>TDQ Report Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TDQReportItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQReportItem()
     * @generated
     */
    int TDQ_REPORT_ITEM = 1;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_REPORT_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_REPORT_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_REPORT_ITEM__PARENT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_REPORT_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__FILENAME;

    /**
     * The feature id for the '<em><b>Report</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_REPORT_ITEM__REPORT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>TDQ Report Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_REPORT_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TDQIndicatorDefinitionItemImpl <em>TDQ Indicator Definition Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TDQIndicatorDefinitionItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQIndicatorDefinitionItem()
     * @generated
     */
    int TDQ_INDICATOR_DEFINITION_ITEM = 2;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_INDICATOR_DEFINITION_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_INDICATOR_DEFINITION_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_INDICATOR_DEFINITION_ITEM__PARENT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_INDICATOR_DEFINITION_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__FILENAME;

    /**
     * The feature id for the '<em><b>Indicator Definition</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_INDICATOR_DEFINITION_ITEM__INDICATOR_DEFINITION = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>TDQ Indicator Definition Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_INDICATOR_DEFINITION_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TDQBusinessRuleItemImpl <em>TDQ Business Rule Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TDQBusinessRuleItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQBusinessRuleItem()
     * @generated
     */
    int TDQ_BUSINESS_RULE_ITEM = 3;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_BUSINESS_RULE_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_BUSINESS_RULE_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_BUSINESS_RULE_ITEM__PARENT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_BUSINESS_RULE_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__FILENAME;

    /**
     * The feature id for the '<em><b>Dqrule</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_BUSINESS_RULE_ITEM__DQRULE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>TDQ Business Rule Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_BUSINESS_RULE_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TDQPatternItemImpl <em>TDQ Pattern Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TDQPatternItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQPatternItem()
     * @generated
     */
    int TDQ_PATTERN_ITEM = 4;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PATTERN_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PATTERN_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PATTERN_ITEM__PARENT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PATTERN_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__FILENAME;

    /**
     * The feature id for the '<em><b>Pattern</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PATTERN_ITEM__PATTERN = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>TDQ Pattern Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PATTERN_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TDQJrxmlItemImpl <em>TDQ Jrxml Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TDQJrxmlItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQJrxmlItem()
     * @generated
     */
    int TDQ_JRXML_ITEM = 5;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_JRXML_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_JRXML_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_JRXML_ITEM__PARENT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_JRXML_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM__FILENAME;

    /**
     * The number of structural features of the '<em>TDQ Jrxml Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_JRXML_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.TDQ_ITEM_FEATURE_COUNT + 0;


    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TDQAnalysisItem <em>TDQ Analysis Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TDQ Analysis Item</em>'.
     * @see org.talend.dataquality.properties.TDQAnalysisItem
     * @generated
     */
    EClass getTDQAnalysisItem();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.properties.TDQAnalysisItem#getAnalysis <em>Analysis</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Analysis</em>'.
     * @see org.talend.dataquality.properties.TDQAnalysisItem#getAnalysis()
     * @see #getTDQAnalysisItem()
     * @generated
     */
    EReference getTDQAnalysisItem_Analysis();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TDQReportItem <em>TDQ Report Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TDQ Report Item</em>'.
     * @see org.talend.dataquality.properties.TDQReportItem
     * @generated
     */
    EClass getTDQReportItem();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.properties.TDQReportItem#getReport <em>Report</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Report</em>'.
     * @see org.talend.dataquality.properties.TDQReportItem#getReport()
     * @see #getTDQReportItem()
     * @generated
     */
    EReference getTDQReportItem_Report();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TDQIndicatorDefinitionItem <em>TDQ Indicator Definition Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TDQ Indicator Definition Item</em>'.
     * @see org.talend.dataquality.properties.TDQIndicatorDefinitionItem
     * @generated
     */
    EClass getTDQIndicatorDefinitionItem();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.properties.TDQIndicatorDefinitionItem#getIndicatorDefinition <em>Indicator Definition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Indicator Definition</em>'.
     * @see org.talend.dataquality.properties.TDQIndicatorDefinitionItem#getIndicatorDefinition()
     * @see #getTDQIndicatorDefinitionItem()
     * @generated
     */
    EReference getTDQIndicatorDefinitionItem_IndicatorDefinition();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TDQBusinessRuleItem <em>TDQ Business Rule Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TDQ Business Rule Item</em>'.
     * @see org.talend.dataquality.properties.TDQBusinessRuleItem
     * @generated
     */
    EClass getTDQBusinessRuleItem();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.properties.TDQBusinessRuleItem#getDqrule <em>Dqrule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Dqrule</em>'.
     * @see org.talend.dataquality.properties.TDQBusinessRuleItem#getDqrule()
     * @see #getTDQBusinessRuleItem()
     * @generated
     */
    EReference getTDQBusinessRuleItem_Dqrule();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TDQPatternItem <em>TDQ Pattern Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TDQ Pattern Item</em>'.
     * @see org.talend.dataquality.properties.TDQPatternItem
     * @generated
     */
    EClass getTDQPatternItem();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.properties.TDQPatternItem#getPattern <em>Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Pattern</em>'.
     * @see org.talend.dataquality.properties.TDQPatternItem#getPattern()
     * @see #getTDQPatternItem()
     * @generated
     */
    EReference getTDQPatternItem_Pattern();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TDQJrxmlItem <em>TDQ Jrxml Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>TDQ Jrxml Item</em>'.
     * @see org.talend.dataquality.properties.TDQJrxmlItem
     * @generated
     */
    EClass getTDQJrxmlItem();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    PropertiesFactory getPropertiesFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TDQAnalysisItemImpl <em>TDQ Analysis Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TDQAnalysisItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQAnalysisItem()
         * @generated
         */
        EClass TDQ_ANALYSIS_ITEM = eINSTANCE.getTDQAnalysisItem();

        /**
         * The meta object literal for the '<em><b>Analysis</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TDQ_ANALYSIS_ITEM__ANALYSIS = eINSTANCE.getTDQAnalysisItem_Analysis();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TDQReportItemImpl <em>TDQ Report Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TDQReportItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQReportItem()
         * @generated
         */
        EClass TDQ_REPORT_ITEM = eINSTANCE.getTDQReportItem();

        /**
         * The meta object literal for the '<em><b>Report</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TDQ_REPORT_ITEM__REPORT = eINSTANCE.getTDQReportItem_Report();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TDQIndicatorDefinitionItemImpl <em>TDQ Indicator Definition Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TDQIndicatorDefinitionItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQIndicatorDefinitionItem()
         * @generated
         */
        EClass TDQ_INDICATOR_DEFINITION_ITEM = eINSTANCE.getTDQIndicatorDefinitionItem();

        /**
         * The meta object literal for the '<em><b>Indicator Definition</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TDQ_INDICATOR_DEFINITION_ITEM__INDICATOR_DEFINITION = eINSTANCE.getTDQIndicatorDefinitionItem_IndicatorDefinition();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TDQBusinessRuleItemImpl <em>TDQ Business Rule Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TDQBusinessRuleItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQBusinessRuleItem()
         * @generated
         */
        EClass TDQ_BUSINESS_RULE_ITEM = eINSTANCE.getTDQBusinessRuleItem();

        /**
         * The meta object literal for the '<em><b>Dqrule</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TDQ_BUSINESS_RULE_ITEM__DQRULE = eINSTANCE.getTDQBusinessRuleItem_Dqrule();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TDQPatternItemImpl <em>TDQ Pattern Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TDQPatternItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQPatternItem()
         * @generated
         */
        EClass TDQ_PATTERN_ITEM = eINSTANCE.getTDQPatternItem();

        /**
         * The meta object literal for the '<em><b>Pattern</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TDQ_PATTERN_ITEM__PATTERN = eINSTANCE.getTDQPatternItem_Pattern();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TDQJrxmlItemImpl <em>TDQ Jrxml Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TDQJrxmlItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTDQJrxmlItem()
         * @generated
         */
        EClass TDQ_JRXML_ITEM = eINSTANCE.getTDQJrxmlItem();

    }

} //PropertiesPackage

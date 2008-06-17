/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.reports;

import org.eclipse.emf.ecore.EObject;

import org.talend.dataquality.analysis.Analysis;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Analysis Map</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This class maps the analysis to the "refresh" boolean. 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.reports.AnalysisMap#getAnalysis <em>Analysis</em>}</li>
 *   <li>{@link org.talend.dataquality.reports.AnalysisMap#isMustRefresh <em>Must Refresh</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.reports.ReportsPackage#getAnalysisMap()
 * @model
 * @generated
 */
public interface AnalysisMap extends EObject {
    /**
     * Returns the value of the '<em><b>Analysis</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Analysis</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Analysis</em>' reference.
     * @see #setAnalysis(Analysis)
     * @see org.talend.dataquality.reports.ReportsPackage#getAnalysisMap_Analysis()
     * @model
     * @generated
     */
    Analysis getAnalysis();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.AnalysisMap#getAnalysis <em>Analysis</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Analysis</em>' reference.
     * @see #getAnalysis()
     * @generated
     */
    void setAnalysis(Analysis value);

    /**
     * Returns the value of the '<em><b>Must Refresh</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * True when the analysis must be recomputed each time the report is run. 
     * <!-- end-model-doc -->
     * @return the value of the '<em>Must Refresh</em>' attribute.
     * @see #setMustRefresh(boolean)
     * @see org.talend.dataquality.reports.ReportsPackage#getAnalysisMap_MustRefresh()
     * @model
     * @generated
     */
    boolean isMustRefresh();

    /**
     * Sets the value of the '{@link org.talend.dataquality.reports.AnalysisMap#isMustRefresh <em>Must Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Must Refresh</em>' attribute.
     * @see #isMustRefresh()
     * @generated
     */
    void setMustRefresh(boolean value);

} // AnalysisMap

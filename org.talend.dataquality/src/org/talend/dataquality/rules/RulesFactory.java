/**
 * // ============================================================================
 * //
 * // Copyright (C) 2006-2007 Talend Inc. - www.talend.com
 * //
 * // This source code is available under agreement available at
 * // %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * //
 * // You should have received a copy of the agreement
 * // along with this program; if not, write to Talend SA
 * // 9 rue Pages 92150 Suresnes, France
 * //
 * // ============================================================================
 * 
 *
 * $Id$
 */
package org.talend.dataquality.rules;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.talend.dataquality.rules.RulesPackage
 * @generated
 */
public interface RulesFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    RulesFactory eINSTANCE = org.talend.dataquality.rules.impl.RulesFactoryImpl.init();

    /**
     * Returns a new object of class '<em>DQ Rule</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>DQ Rule</em>'.
     * @generated
     */
    DQRule createDQRule();

    /**
     * Returns a new object of class '<em>Specified DQ Rule</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Specified DQ Rule</em>'.
     * @generated
     */
    SpecifiedDQRule createSpecifiedDQRule();

    /**
     * Returns a new object of class '<em>Inferred DQ Rule</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Inferred DQ Rule</em>'.
     * @generated
     */
    InferredDQRule createInferredDQRule();

    /**
     * Returns a new object of class '<em>Where Rule</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Where Rule</em>'.
     * @generated
     */
    WhereRule createWhereRule();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    RulesPackage getRulesPackage();

} //RulesFactory

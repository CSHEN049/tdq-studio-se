/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package orgomg.cwmx.resource.express;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Conjoint</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * This represents a physical Express conjoint. This is a type of physical dimension that may be used to provide more efficient storage for sparse cubes.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link orgomg.cwmx.resource.express.Conjoint#getSearchAlgorithm <em>Search Algorithm</em>}</li>
 *   <li>{@link orgomg.cwmx.resource.express.Conjoint#getPageSpace <em>Page Space</em>}</li>
 * </ul>
 * </p>
 *
 * @see orgomg.cwmx.resource.express.ExpressPackage#getConjoint()
 * @model
 * @generated
 */
public interface Conjoint extends Dimension {
    /**
     * Returns the value of the '<em><b>Search Algorithm</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Indicates the type of algorithm Express should use for loading and accessing the values of the Conjoint Dimension. Valid values are HASH, BTREE, NOHASH.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Search Algorithm</em>' attribute.
     * @see #setSearchAlgorithm(String)
     * @see orgomg.cwmx.resource.express.ExpressPackage#getConjoint_SearchAlgorithm()
     * @model dataType="orgomg.cwm.objectmodel.core.String"
     * @generated
     */
    String getSearchAlgorithm();

    /**
     * Sets the value of the '{@link orgomg.cwmx.resource.express.Conjoint#getSearchAlgorithm <em>Search Algorithm</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Search Algorithm</em>' attribute.
     * @see #getSearchAlgorithm()
     * @generated
     */
    void setSearchAlgorithm(String value);

    /**
     * Returns the value of the '<em><b>Page Space</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * If specified, this defines the type of page space to be allocated to data relating specific values of the Conjoint to values of its base Dimensions:
     * 
     *     OWNSPACE specifies that the data will be stored in private page space.
     *     SHAREDSPACE specifies that the data will be stored in the database�s global page space.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Page Space</em>' attribute.
     * @see #setPageSpace(String)
     * @see orgomg.cwmx.resource.express.ExpressPackage#getConjoint_PageSpace()
     * @model dataType="orgomg.cwm.objectmodel.core.String"
     * @generated
     */
    String getPageSpace();

    /**
     * Sets the value of the '{@link orgomg.cwmx.resource.express.Conjoint#getPageSpace <em>Page Space</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Page Space</em>' attribute.
     * @see #getPageSpace()
     * @generated
     */
    void setPageSpace(String value);

} // Conjoint

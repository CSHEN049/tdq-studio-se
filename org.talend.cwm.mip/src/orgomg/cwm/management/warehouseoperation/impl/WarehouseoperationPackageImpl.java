/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package orgomg.cwm.management.warehouseoperation.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import orgomg.cwm.analysis.businessnomenclature.BusinessnomenclaturePackage;

import orgomg.cwm.analysis.businessnomenclature.impl.BusinessnomenclaturePackageImpl;

import orgomg.cwm.analysis.datamining.DataminingPackage;

import orgomg.cwm.analysis.datamining.impl.DataminingPackageImpl;

import orgomg.cwm.analysis.informationvisualization.InformationvisualizationPackage;

import orgomg.cwm.analysis.informationvisualization.impl.InformationvisualizationPackageImpl;

import orgomg.cwm.analysis.olap.OlapPackage;

import orgomg.cwm.analysis.olap.impl.OlapPackageImpl;

import orgomg.cwm.analysis.transformation.TransformationPackage;

import orgomg.cwm.analysis.transformation.impl.TransformationPackageImpl;

import orgomg.cwm.foundation.businessinformation.BusinessinformationPackage;

import orgomg.cwm.foundation.businessinformation.impl.BusinessinformationPackageImpl;

import orgomg.cwm.foundation.datatypes.DatatypesPackage;

import orgomg.cwm.foundation.datatypes.impl.DatatypesPackageImpl;

import orgomg.cwm.foundation.expressions.ExpressionsPackage;

import orgomg.cwm.foundation.expressions.impl.ExpressionsPackageImpl;

import orgomg.cwm.foundation.keysindexes.KeysindexesPackage;

import orgomg.cwm.foundation.keysindexes.impl.KeysindexesPackageImpl;

import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;

import orgomg.cwm.foundation.softwaredeployment.impl.SoftwaredeploymentPackageImpl;

import orgomg.cwm.foundation.typemapping.TypemappingPackage;

import orgomg.cwm.foundation.typemapping.impl.TypemappingPackageImpl;

import orgomg.cwm.management.warehouseoperation.ActivityExecution;
import orgomg.cwm.management.warehouseoperation.ChangeRequest;
import orgomg.cwm.management.warehouseoperation.Measurement;
import orgomg.cwm.management.warehouseoperation.StepExecution;
import orgomg.cwm.management.warehouseoperation.TransformationExecution;
import orgomg.cwm.management.warehouseoperation.WarehouseoperationFactory;
import orgomg.cwm.management.warehouseoperation.WarehouseoperationPackage;

import orgomg.cwm.management.warehouseprocess.WarehouseprocessPackage;

import orgomg.cwm.management.warehouseprocess.datatype.DatatypePackage;

import orgomg.cwm.management.warehouseprocess.datatype.impl.DatatypePackageImpl;

import orgomg.cwm.management.warehouseprocess.events.EventsPackage;

import orgomg.cwm.management.warehouseprocess.events.impl.EventsPackageImpl;

import orgomg.cwm.management.warehouseprocess.impl.WarehouseprocessPackageImpl;

import orgomg.cwm.objectmodel.behavioral.BehavioralPackage;

import orgomg.cwm.objectmodel.behavioral.impl.BehavioralPackageImpl;

import orgomg.cwm.objectmodel.core.CorePackage;

import orgomg.cwm.objectmodel.core.impl.CorePackageImpl;

import orgomg.cwm.objectmodel.instance.InstancePackage;

import orgomg.cwm.objectmodel.instance.impl.InstancePackageImpl;

import orgomg.cwm.objectmodel.relationships.RelationshipsPackage;

import orgomg.cwm.objectmodel.relationships.impl.RelationshipsPackageImpl;

import orgomg.cwm.resource.multidimensional.MultidimensionalPackage;

import orgomg.cwm.resource.multidimensional.impl.MultidimensionalPackageImpl;

import orgomg.cwm.resource.record.RecordPackage;

import orgomg.cwm.resource.record.impl.RecordPackageImpl;

import orgomg.cwm.resource.relational.RelationalPackage;

import orgomg.cwm.resource.relational.enumerations.EnumerationsPackage;

import orgomg.cwm.resource.relational.enumerations.impl.EnumerationsPackageImpl;

import orgomg.cwm.resource.relational.impl.RelationalPackageImpl;

import orgomg.cwm.resource.xml.XmlPackage;

import orgomg.cwm.resource.xml.impl.XmlPackageImpl;

import orgomg.cwmmip.CwmmipPackage;

import orgomg.cwmmip.impl.CwmmipPackageImpl;

import orgomg.cwmx.analysis.informationreporting.InformationreportingPackage;

import orgomg.cwmx.analysis.informationreporting.impl.InformationreportingPackageImpl;

import orgomg.cwmx.analysis.informationset.InformationsetPackage;

import orgomg.cwmx.analysis.informationset.impl.InformationsetPackageImpl;

import orgomg.cwmx.foundation.er.ErPackage;

import orgomg.cwmx.foundation.er.impl.ErPackageImpl;

import orgomg.cwmx.resource.coboldata.CoboldataPackage;

import orgomg.cwmx.resource.coboldata.impl.CoboldataPackageImpl;

import orgomg.cwmx.resource.dmsii.DmsiiPackage;

import orgomg.cwmx.resource.dmsii.impl.DmsiiPackageImpl;

import orgomg.cwmx.resource.essbase.EssbasePackage;

import orgomg.cwmx.resource.essbase.impl.EssbasePackageImpl;

import orgomg.cwmx.resource.express.ExpressPackage;

import orgomg.cwmx.resource.express.impl.ExpressPackageImpl;

import orgomg.cwmx.resource.imsdatabase.ImsdatabasePackage;

import orgomg.cwmx.resource.imsdatabase.impl.ImsdatabasePackageImpl;

import orgomg.cwmx.resource.imsdatabase.imstypes.ImstypesPackage;

import orgomg.cwmx.resource.imsdatabase.imstypes.impl.ImstypesPackageImpl;

import orgomg.mof.model.ModelPackage;

import orgomg.mof.model.impl.ModelPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WarehouseoperationPackageImpl extends EPackageImpl implements WarehouseoperationPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass measurementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass changeRequestEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass transformationExecutionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass activityExecutionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass stepExecutionEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see orgomg.cwm.management.warehouseoperation.WarehouseoperationPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private WarehouseoperationPackageImpl() {
        super(eNS_URI, WarehouseoperationFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static WarehouseoperationPackage init() {
        if (isInited) return (WarehouseoperationPackage)EPackage.Registry.INSTANCE.getEPackage(WarehouseoperationPackage.eNS_URI);

        // Obtain or create and register package
        WarehouseoperationPackageImpl theWarehouseoperationPackage = (WarehouseoperationPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof WarehouseoperationPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new WarehouseoperationPackageImpl());

        isInited = true;

        // Obtain or create and register interdependencies
        CorePackageImpl theCorePackage = (CorePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI) instanceof CorePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI) : CorePackage.eINSTANCE);
        BehavioralPackageImpl theBehavioralPackage = (BehavioralPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(BehavioralPackage.eNS_URI) instanceof BehavioralPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(BehavioralPackage.eNS_URI) : BehavioralPackage.eINSTANCE);
        RelationshipsPackageImpl theRelationshipsPackage = (RelationshipsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RelationshipsPackage.eNS_URI) instanceof RelationshipsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RelationshipsPackage.eNS_URI) : RelationshipsPackage.eINSTANCE);
        InstancePackageImpl theInstancePackage = (InstancePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(InstancePackage.eNS_URI) instanceof InstancePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(InstancePackage.eNS_URI) : InstancePackage.eINSTANCE);
        BusinessinformationPackageImpl theBusinessinformationPackage = (BusinessinformationPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(BusinessinformationPackage.eNS_URI) instanceof BusinessinformationPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(BusinessinformationPackage.eNS_URI) : BusinessinformationPackage.eINSTANCE);
        DatatypesPackageImpl theDatatypesPackage = (DatatypesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DatatypesPackage.eNS_URI) instanceof DatatypesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DatatypesPackage.eNS_URI) : DatatypesPackage.eINSTANCE);
        ExpressionsPackageImpl theExpressionsPackage = (ExpressionsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ExpressionsPackage.eNS_URI) instanceof ExpressionsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ExpressionsPackage.eNS_URI) : ExpressionsPackage.eINSTANCE);
        KeysindexesPackageImpl theKeysindexesPackage = (KeysindexesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(KeysindexesPackage.eNS_URI) instanceof KeysindexesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(KeysindexesPackage.eNS_URI) : KeysindexesPackage.eINSTANCE);
        SoftwaredeploymentPackageImpl theSoftwaredeploymentPackage = (SoftwaredeploymentPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SoftwaredeploymentPackage.eNS_URI) instanceof SoftwaredeploymentPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SoftwaredeploymentPackage.eNS_URI) : SoftwaredeploymentPackage.eINSTANCE);
        TypemappingPackageImpl theTypemappingPackage = (TypemappingPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(TypemappingPackage.eNS_URI) instanceof TypemappingPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(TypemappingPackage.eNS_URI) : TypemappingPackage.eINSTANCE);
        RelationalPackageImpl theRelationalPackage = (RelationalPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RelationalPackage.eNS_URI) instanceof RelationalPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RelationalPackage.eNS_URI) : RelationalPackage.eINSTANCE);
        EnumerationsPackageImpl theEnumerationsPackage = (EnumerationsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(EnumerationsPackage.eNS_URI) instanceof EnumerationsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(EnumerationsPackage.eNS_URI) : EnumerationsPackage.eINSTANCE);
        RecordPackageImpl theRecordPackage = (RecordPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RecordPackage.eNS_URI) instanceof RecordPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RecordPackage.eNS_URI) : RecordPackage.eINSTANCE);
        MultidimensionalPackageImpl theMultidimensionalPackage = (MultidimensionalPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MultidimensionalPackage.eNS_URI) instanceof MultidimensionalPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MultidimensionalPackage.eNS_URI) : MultidimensionalPackage.eINSTANCE);
        XmlPackageImpl theXmlPackage = (XmlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(XmlPackage.eNS_URI) instanceof XmlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(XmlPackage.eNS_URI) : XmlPackage.eINSTANCE);
        TransformationPackageImpl theTransformationPackage = (TransformationPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(TransformationPackage.eNS_URI) instanceof TransformationPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(TransformationPackage.eNS_URI) : TransformationPackage.eINSTANCE);
        OlapPackageImpl theOlapPackage = (OlapPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(OlapPackage.eNS_URI) instanceof OlapPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(OlapPackage.eNS_URI) : OlapPackage.eINSTANCE);
        DataminingPackageImpl theDataminingPackage = (DataminingPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DataminingPackage.eNS_URI) instanceof DataminingPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DataminingPackage.eNS_URI) : DataminingPackage.eINSTANCE);
        InformationvisualizationPackageImpl theInformationvisualizationPackage = (InformationvisualizationPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(InformationvisualizationPackage.eNS_URI) instanceof InformationvisualizationPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(InformationvisualizationPackage.eNS_URI) : InformationvisualizationPackage.eINSTANCE);
        BusinessnomenclaturePackageImpl theBusinessnomenclaturePackage = (BusinessnomenclaturePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(BusinessnomenclaturePackage.eNS_URI) instanceof BusinessnomenclaturePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(BusinessnomenclaturePackage.eNS_URI) : BusinessnomenclaturePackage.eINSTANCE);
        WarehouseprocessPackageImpl theWarehouseprocessPackage = (WarehouseprocessPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(WarehouseprocessPackage.eNS_URI) instanceof WarehouseprocessPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(WarehouseprocessPackage.eNS_URI) : WarehouseprocessPackage.eINSTANCE);
        DatatypePackageImpl theDatatypePackage = (DatatypePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DatatypePackage.eNS_URI) instanceof DatatypePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DatatypePackage.eNS_URI) : DatatypePackage.eINSTANCE);
        EventsPackageImpl theEventsPackage = (EventsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(EventsPackage.eNS_URI) instanceof EventsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(EventsPackage.eNS_URI) : EventsPackage.eINSTANCE);
        ErPackageImpl theErPackage = (ErPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ErPackage.eNS_URI) instanceof ErPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ErPackage.eNS_URI) : ErPackage.eINSTANCE);
        CoboldataPackageImpl theCoboldataPackage = (CoboldataPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(CoboldataPackage.eNS_URI) instanceof CoboldataPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(CoboldataPackage.eNS_URI) : CoboldataPackage.eINSTANCE);
        DmsiiPackageImpl theDmsiiPackage = (DmsiiPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DmsiiPackage.eNS_URI) instanceof DmsiiPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DmsiiPackage.eNS_URI) : DmsiiPackage.eINSTANCE);
        ImsdatabasePackageImpl theImsdatabasePackage = (ImsdatabasePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ImsdatabasePackage.eNS_URI) instanceof ImsdatabasePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ImsdatabasePackage.eNS_URI) : ImsdatabasePackage.eINSTANCE);
        ImstypesPackageImpl theImstypesPackage = (ImstypesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ImstypesPackage.eNS_URI) instanceof ImstypesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ImstypesPackage.eNS_URI) : ImstypesPackage.eINSTANCE);
        EssbasePackageImpl theEssbasePackage = (EssbasePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(EssbasePackage.eNS_URI) instanceof EssbasePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(EssbasePackage.eNS_URI) : EssbasePackage.eINSTANCE);
        ExpressPackageImpl theExpressPackage = (ExpressPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ExpressPackage.eNS_URI) instanceof ExpressPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ExpressPackage.eNS_URI) : ExpressPackage.eINSTANCE);
        InformationsetPackageImpl theInformationsetPackage = (InformationsetPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(InformationsetPackage.eNS_URI) instanceof InformationsetPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(InformationsetPackage.eNS_URI) : InformationsetPackage.eINSTANCE);
        InformationreportingPackageImpl theInformationreportingPackage = (InformationreportingPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(InformationreportingPackage.eNS_URI) instanceof InformationreportingPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(InformationreportingPackage.eNS_URI) : InformationreportingPackage.eINSTANCE);
        CwmmipPackageImpl theCwmmipPackage = (CwmmipPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(CwmmipPackage.eNS_URI) instanceof CwmmipPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(CwmmipPackage.eNS_URI) : CwmmipPackage.eINSTANCE);
        ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) : ModelPackage.eINSTANCE);

        // Create package meta-data objects
        theWarehouseoperationPackage.createPackageContents();
        theCorePackage.createPackageContents();
        theBehavioralPackage.createPackageContents();
        theRelationshipsPackage.createPackageContents();
        theInstancePackage.createPackageContents();
        theBusinessinformationPackage.createPackageContents();
        theDatatypesPackage.createPackageContents();
        theExpressionsPackage.createPackageContents();
        theKeysindexesPackage.createPackageContents();
        theSoftwaredeploymentPackage.createPackageContents();
        theTypemappingPackage.createPackageContents();
        theRelationalPackage.createPackageContents();
        theEnumerationsPackage.createPackageContents();
        theRecordPackage.createPackageContents();
        theMultidimensionalPackage.createPackageContents();
        theXmlPackage.createPackageContents();
        theTransformationPackage.createPackageContents();
        theOlapPackage.createPackageContents();
        theDataminingPackage.createPackageContents();
        theInformationvisualizationPackage.createPackageContents();
        theBusinessnomenclaturePackage.createPackageContents();
        theWarehouseprocessPackage.createPackageContents();
        theDatatypePackage.createPackageContents();
        theEventsPackage.createPackageContents();
        theErPackage.createPackageContents();
        theCoboldataPackage.createPackageContents();
        theDmsiiPackage.createPackageContents();
        theImsdatabasePackage.createPackageContents();
        theImstypesPackage.createPackageContents();
        theEssbasePackage.createPackageContents();
        theExpressPackage.createPackageContents();
        theInformationsetPackage.createPackageContents();
        theInformationreportingPackage.createPackageContents();
        theCwmmipPackage.createPackageContents();
        theModelPackage.createPackageContents();

        // Initialize created meta-data
        theWarehouseoperationPackage.initializePackageContents();
        theCorePackage.initializePackageContents();
        theBehavioralPackage.initializePackageContents();
        theRelationshipsPackage.initializePackageContents();
        theInstancePackage.initializePackageContents();
        theBusinessinformationPackage.initializePackageContents();
        theDatatypesPackage.initializePackageContents();
        theExpressionsPackage.initializePackageContents();
        theKeysindexesPackage.initializePackageContents();
        theSoftwaredeploymentPackage.initializePackageContents();
        theTypemappingPackage.initializePackageContents();
        theRelationalPackage.initializePackageContents();
        theEnumerationsPackage.initializePackageContents();
        theRecordPackage.initializePackageContents();
        theMultidimensionalPackage.initializePackageContents();
        theXmlPackage.initializePackageContents();
        theTransformationPackage.initializePackageContents();
        theOlapPackage.initializePackageContents();
        theDataminingPackage.initializePackageContents();
        theInformationvisualizationPackage.initializePackageContents();
        theBusinessnomenclaturePackage.initializePackageContents();
        theWarehouseprocessPackage.initializePackageContents();
        theDatatypePackage.initializePackageContents();
        theEventsPackage.initializePackageContents();
        theErPackage.initializePackageContents();
        theCoboldataPackage.initializePackageContents();
        theDmsiiPackage.initializePackageContents();
        theImsdatabasePackage.initializePackageContents();
        theImstypesPackage.initializePackageContents();
        theEssbasePackage.initializePackageContents();
        theExpressPackage.initializePackageContents();
        theInformationsetPackage.initializePackageContents();
        theInformationreportingPackage.initializePackageContents();
        theCwmmipPackage.initializePackageContents();
        theModelPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theWarehouseoperationPackage.freeze();

        return theWarehouseoperationPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getMeasurement() {
        return measurementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMeasurement_Value() {
        return (EAttribute)measurementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMeasurement_Unit() {
        return (EAttribute)measurementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMeasurement_Type() {
        return (EAttribute)measurementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMeasurement_CreationDate() {
        return (EAttribute)measurementEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMeasurement_EffectiveDate() {
        return (EAttribute)measurementEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getMeasurement_ModelElement() {
        return (EReference)measurementEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getChangeRequest() {
        return changeRequestEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChangeRequest_ChangeDescription() {
        return (EAttribute)changeRequestEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChangeRequest_ChangeReason() {
        return (EAttribute)changeRequestEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChangeRequest_Status() {
        return (EAttribute)changeRequestEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChangeRequest_Completed() {
        return (EAttribute)changeRequestEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChangeRequest_RequestDate() {
        return (EAttribute)changeRequestEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChangeRequest_CompletionDate() {
        return (EAttribute)changeRequestEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getChangeRequest_ModelElement() {
        return (EReference)changeRequestEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTransformationExecution() {
        return transformationExecutionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransformationExecution_StartDate() {
        return (EAttribute)transformationExecutionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransformationExecution_EndDate() {
        return (EAttribute)transformationExecutionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransformationExecution_InProgress() {
        return (EAttribute)transformationExecutionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTransformationExecution_Successful() {
        return (EAttribute)transformationExecutionEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTransformationExecution_Status() {
        return (EReference)transformationExecutionEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getActivityExecution() {
        return activityExecutionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getActivityExecution_TransformationActivity() {
        return (EReference)activityExecutionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getActivityExecution_StepExecution() {
        return (EReference)activityExecutionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStepExecution() {
        return stepExecutionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStepExecution_TransformationStep() {
        return (EReference)stepExecutionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStepExecution_ActivityExecution() {
        return (EReference)stepExecutionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStepExecution_CallAction() {
        return (EReference)stepExecutionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public WarehouseoperationFactory getWarehouseoperationFactory() {
        return (WarehouseoperationFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        measurementEClass = createEClass(MEASUREMENT);
        createEAttribute(measurementEClass, MEASUREMENT__VALUE);
        createEAttribute(measurementEClass, MEASUREMENT__UNIT);
        createEAttribute(measurementEClass, MEASUREMENT__TYPE);
        createEAttribute(measurementEClass, MEASUREMENT__CREATION_DATE);
        createEAttribute(measurementEClass, MEASUREMENT__EFFECTIVE_DATE);
        createEReference(measurementEClass, MEASUREMENT__MODEL_ELEMENT);

        changeRequestEClass = createEClass(CHANGE_REQUEST);
        createEAttribute(changeRequestEClass, CHANGE_REQUEST__CHANGE_DESCRIPTION);
        createEAttribute(changeRequestEClass, CHANGE_REQUEST__CHANGE_REASON);
        createEAttribute(changeRequestEClass, CHANGE_REQUEST__STATUS);
        createEAttribute(changeRequestEClass, CHANGE_REQUEST__COMPLETED);
        createEAttribute(changeRequestEClass, CHANGE_REQUEST__REQUEST_DATE);
        createEAttribute(changeRequestEClass, CHANGE_REQUEST__COMPLETION_DATE);
        createEReference(changeRequestEClass, CHANGE_REQUEST__MODEL_ELEMENT);

        transformationExecutionEClass = createEClass(TRANSFORMATION_EXECUTION);
        createEAttribute(transformationExecutionEClass, TRANSFORMATION_EXECUTION__START_DATE);
        createEAttribute(transformationExecutionEClass, TRANSFORMATION_EXECUTION__END_DATE);
        createEAttribute(transformationExecutionEClass, TRANSFORMATION_EXECUTION__IN_PROGRESS);
        createEAttribute(transformationExecutionEClass, TRANSFORMATION_EXECUTION__SUCCESSFUL);
        createEReference(transformationExecutionEClass, TRANSFORMATION_EXECUTION__STATUS);

        activityExecutionEClass = createEClass(ACTIVITY_EXECUTION);
        createEReference(activityExecutionEClass, ACTIVITY_EXECUTION__TRANSFORMATION_ACTIVITY);
        createEReference(activityExecutionEClass, ACTIVITY_EXECUTION__STEP_EXECUTION);

        stepExecutionEClass = createEClass(STEP_EXECUTION);
        createEReference(stepExecutionEClass, STEP_EXECUTION__TRANSFORMATION_STEP);
        createEReference(stepExecutionEClass, STEP_EXECUTION__ACTIVITY_EXECUTION);
        createEReference(stepExecutionEClass, STEP_EXECUTION__CALL_ACTION);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        CorePackage theCorePackage = (CorePackage)EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI);
        TransformationPackage theTransformationPackage = (TransformationPackage)EPackage.Registry.INSTANCE.getEPackage(TransformationPackage.eNS_URI);
        BehavioralPackage theBehavioralPackage = (BehavioralPackage)EPackage.Registry.INSTANCE.getEPackage(BehavioralPackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        measurementEClass.getESuperTypes().add(theCorePackage.getModelElement());
        changeRequestEClass.getESuperTypes().add(theCorePackage.getModelElement());
        transformationExecutionEClass.getESuperTypes().add(theCorePackage.getModelElement());
        activityExecutionEClass.getESuperTypes().add(this.getTransformationExecution());
        stepExecutionEClass.getESuperTypes().add(this.getTransformationExecution());

        // Initialize classes and features; add operations and parameters
        initEClass(measurementEClass, Measurement.class, "Measurement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getMeasurement_Value(), theCorePackage.getFloat(), "value", null, 0, 1, Measurement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMeasurement_Unit(), theCorePackage.getString(), "unit", null, 0, 1, Measurement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMeasurement_Type(), theCorePackage.getString(), "type", null, 0, 1, Measurement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMeasurement_CreationDate(), theCorePackage.getTime(), "creationDate", null, 0, 1, Measurement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMeasurement_EffectiveDate(), theCorePackage.getTime(), "effectiveDate", null, 0, 1, Measurement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getMeasurement_ModelElement(), theCorePackage.getModelElement(), theCorePackage.getModelElement_Measurement(), "modelElement", null, 1, 1, Measurement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(changeRequestEClass, ChangeRequest.class, "ChangeRequest", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getChangeRequest_ChangeDescription(), theCorePackage.getString(), "changeDescription", null, 0, 1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getChangeRequest_ChangeReason(), theCorePackage.getString(), "changeReason", null, 0, 1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getChangeRequest_Status(), theCorePackage.getString(), "status", null, 0, 1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getChangeRequest_Completed(), theCorePackage.getBoolean(), "completed", null, 0, 1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getChangeRequest_RequestDate(), theCorePackage.getTime(), "requestDate", null, 0, 1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getChangeRequest_CompletionDate(), theCorePackage.getTime(), "completionDate", null, 0, 1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getChangeRequest_ModelElement(), theCorePackage.getModelElement(), theCorePackage.getModelElement_ChangeRequest(), "modelElement", null, 1, -1, ChangeRequest.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(transformationExecutionEClass, TransformationExecution.class, "TransformationExecution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTransformationExecution_StartDate(), theCorePackage.getTime(), "startDate", null, 0, 1, TransformationExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTransformationExecution_EndDate(), theCorePackage.getTime(), "endDate", null, 0, 1, TransformationExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTransformationExecution_InProgress(), theCorePackage.getBoolean(), "inProgress", null, 0, 1, TransformationExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTransformationExecution_Successful(), theCorePackage.getBoolean(), "successful", null, 0, 1, TransformationExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTransformationExecution_Status(), theCorePackage.getExpression(), null, "status", null, 0, 1, TransformationExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(activityExecutionEClass, ActivityExecution.class, "ActivityExecution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getActivityExecution_TransformationActivity(), theTransformationPackage.getTransformationActivity(), theTransformationPackage.getTransformationActivity_Execution(), "transformationActivity", null, 1, 1, ActivityExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getActivityExecution_StepExecution(), this.getStepExecution(), this.getStepExecution_ActivityExecution(), "stepExecution", null, 0, -1, ActivityExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(stepExecutionEClass, StepExecution.class, "StepExecution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getStepExecution_TransformationStep(), theTransformationPackage.getTransformationStep(), theTransformationPackage.getTransformationStep_Execution(), "transformationStep", null, 1, 1, StepExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStepExecution_ActivityExecution(), this.getActivityExecution(), this.getActivityExecution_StepExecution(), "activityExecution", null, 0, 1, StepExecution.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStepExecution_CallAction(), theBehavioralPackage.getCallAction(), theBehavioralPackage.getCallAction_StepExecution(), "callAction", null, 0, 1, StepExecution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //WarehouseoperationPackageImpl

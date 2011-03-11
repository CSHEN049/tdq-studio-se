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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.chart.jung.JungGraphGenerator;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnNominalIntervalTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.composite.IndicatorsComp;
import org.talend.dataprofiler.core.ui.editor.preview.HideSeriesChartComposite;
import org.talend.dataprofiler.core.ui.pref.EditorPreferencePage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.ColumnCorrelationAnalysisHandler;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.indicators.graph.GraphBuilder;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author xzhao
 * 
 */
public class ColumnCorrelationNominalAndIntervalMasterPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnCorrelationNominalAndIntervalMasterPage.class);

    AnalysisColumnNominalIntervalTreeViewer treeViewer;

    DataFilterComp dataFilterComp;

    ColumnCorrelationAnalysisHandler correlationAnalysisHandler;

    private ColumnSetMultiValueIndicator columnSetMultiIndicator;

    protected String execLang;

    private String stringDataFilter;

    private Composite chartComposite;

    private ScrolledForm form;

    private static final int TREE_MAX_LENGTH = 400;

    private static final int INDICATORS_SECTION_HEIGHT = 300;

    protected Composite[] previewChartCompsites;

    private EList<ModelElement> analyzedColumns;

    private Section analysisColSection;

    private Section dataFilterSection;

    private Section previewSection;

    private Section analysisParamSection;

    private Section indicatorsSection;

    private IndicatorsComp indicatorsViewer;

    public ColumnCorrelationNominalAndIntervalMasterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        recomputeIndicators();

    }

    public void recomputeIndicators() {
        correlationAnalysisHandler = new ColumnCorrelationAnalysisHandler();
        correlationAnalysisHandler.setAnalysis((Analysis) this.currentModelElement);
        stringDataFilter = correlationAnalysisHandler.getStringDataFilter();
        analyzedColumns = correlationAnalysisHandler.getAnalyzedColumns();
        if (correlationAnalysisHandler.getIndicator() == null) {
            ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
            // MOD qiongli 2010-6-18 bug 12766
            if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator.eClass()) {
                columnSetMultiIndicator = columnsetFactory.createCountAvgNullIndicator();
                // MOD xqliu 2010-04-06 bug 12161
                fillSimpleIndicators(columnSetMultiIndicator);
                // ~12161
            }
            if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator.eClass()) {
                columnSetMultiIndicator = columnsetFactory.createMinMaxDateIndicator();
            }
            if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator.eClass()) {
                columnSetMultiIndicator = columnsetFactory.createWeakCorrelationIndicator();
            }
        } else {
            columnSetMultiIndicator = (ColumnSetMultiValueIndicator) correlationAnalysisHandler.getIndicator();
        }
        initializeIndicator(columnSetMultiIndicator);
        columnSetMultiIndicator.setStoreData(true);
        for (ModelElement element : analyzedColumns) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            if (tdColumn == null) {
                continue;
            }
            // currentColumnIndicator = new ColumnIndicator(tdColumn);
            DataminingType dataminingType = correlationAnalysisHandler.getDatamingType(tdColumn);
            MetadataHelper.setDataminingType(dataminingType == null ? DataminingType.NOMINAL : dataminingType, tdColumn);
        }
    }

    /**
     * DOC xqliu Comment method "fillSimpleIndicators". ADD xqliu 2010-04-06 bug 12161
     * 
     * @param countAvgNullIndicator
     */
    private void fillSimpleIndicators(ColumnSetMultiValueIndicator countAvgNullIndicator) {
        countAvgNullIndicator.setRowCountIndicator(IndicatorsFactory.eINSTANCE.createRowCountIndicator());
        countAvgNullIndicator.setDistinctCountIndicator(IndicatorsFactory.eINSTANCE.createDistinctCountIndicator());
        countAvgNullIndicator.setDuplicateCountIndicator(IndicatorsFactory.eINSTANCE.createDuplicateCountIndicator());
        countAvgNullIndicator.setUniqueCountIndicator(IndicatorsFactory.eINSTANCE.createUniqueCountIndicator());
    }

    private void initializeIndicator(Indicator indicator) {
        if (indicator.getIndicatorDefinition() == null) {
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicator);
        }
        // MOD xqliu 2010-04-06 bug 12161
        if (indicator instanceof CountAvgNullIndicator) {
            if (((CountAvgNullIndicator) indicator).getChildIndicators().size() == 0) {
                CountAvgNullIndicator countAvgNullIndicator = (CountAvgNullIndicator) indicator;
                fillSimpleIndicators(countAvgNullIndicator);
            }
            for (Indicator child : ((CompositeIndicator) indicator).getChildIndicators()) {
                initializeIndicator(child); // recurse
            }
        }
        // ~12161
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        Composite body = form.getBody();

        body.setLayout(new GridLayout());
        final SashForm sForm = new SashForm(body, SWT.NULL);
        sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        topComp = toolkit.createComposite(sForm);
        topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
        metadataSection.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        // set title of form.
        if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator.eClass()) {
            form.setText(DefaultMessagesImpl
                    .getString("ColumnCorrelationNominalAndIntervalMasterPage.CorrelationAnalysisInterval")); //$NON-NLS-1$
        }

        if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator.eClass()) {
            form.setText(DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.CorrelationAnalysisDate")); //$NON-NLS-1$
        }

        if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator.eClass()) {
            form.setText(DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.NominalCorrelation")); //$NON-NLS-1$
        }

        createAnalysisColumnsSection(form, topComp);

        createIndicatorsSection(form, topComp);

        createDataFilterSection(form, topComp);

        // createAnalysisParamSection(form, topComp);
        if (!EditorPreferencePage.isHideGraphics()) {
            Composite previewComp = toolkit.createComposite(sForm);
            previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
            previewComp.setLayout(new GridLayout());
            // add by hcheng for 0007290: Chart cannot auto compute it's size in
            // DQRule analsyis Editor
            previewComp.addControlListener(new ControlAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse .swt.events.ControlEvent)
                 */
                @Override
                public void controlResized(ControlEvent e) {
                    super.controlResized(e);
                    sForm.redraw();
                    form.reflow(true);
                }
            });
            // ~
            createPreviewSection(form, previewComp);
        }

    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisColSection = createSection(form, anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analyzeColumn"), null); //$NON-NLS-1$

        Composite topComp = toolkit.createComposite(analysisColSection);
        topComp.setLayout(new GridLayout());
        // ~ MOD mzhao 2009-05-05,Bug 6587.
        createConnBindWidget(topComp);
        // ~
        Hyperlink clmnBtn = toolkit.createHyperlink(topComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.selectColumn"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog();
            }

        });

        Composite tree = toolkit.createComposite(topComp, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnalysisColumnNominalIntervalTreeViewer(tree, this);
        treeViewer.addPropertyChangeListener(this);
        treeViewer.setInput(analyzedColumns.toArray());
        treeViewer.setDirty(false);
        analysisColSection.setClient(topComp);

    }

    /**
     * 
     */
    public void openColumnsSelectionDialog() {
        List<RepositoryNode> columnList = treeViewer.getColumnSetMultiValueList();
        if (columnList == null) {
            columnList = new ArrayList<RepositoryNode>();
        }
        RepositoryNode connNode = (RepositoryNode) getConnCombo().getData(String.valueOf(getConnCombo().getSelectionIndex()));
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(
                this,
                /* getEditor().getActiveEditor().getSite().getShell() */null,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelection"), columnList, connNode, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {

        previewSection = createSection(
                form,
                parent,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
        previewSection.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(previewSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
        refreshBtn.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.refreshGraphics")); //$NON-NLS-1$
        refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        final Label message = toolkit.createLabel(sectionClient,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.spaceWhite")); //$NON-NLS-1$
        message.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        message.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        chartComposite = toolkit.createComposite(sectionClient);
        chartComposite.setLayout(new GridLayout());
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Analysis analysis = correlationAnalysisHandler.getAnalysis();

        refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {

                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                boolean analysisStatue = analysis.getResults().getResultMetadata() != null
                        && analysis.getResults().getResultMetadata().getExecutionDate() != null;

                if (!analysisStatue) {
                    boolean returnCode = MessageDialog.openConfirm(null,
                            DefaultMessagesImpl.getString("ColumnMasterDetailsPage.ViewResult"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("ColumnMasterDetailsPage.RunOrSeeSampleData")); //$NON-NLS-1$

                    if (returnCode) {
                        new RunAnalysisAction().run();
                        message.setVisible(false);
                    } else {
                        createPreviewCharts(form, chartComposite, false);
                        message.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.warning")); //$NON-NLS-1$
                        message.setVisible(true);
                    }
                } else {
                    createPreviewCharts(form, chartComposite, true);
                }

                chartComposite.layout();
                form.reflow(true);
            }

        });

        previewSection.setClient(sectionClient);
    }

    public void createPreviewCharts(final ScrolledForm form, final Composite composite, final boolean isCreate) {

        List<Composite> previewChartList = new ArrayList<Composite>();

        if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator.eClass()) {
            GraphBuilder gBuilder = new GraphBuilder();
            gBuilder.setTotalWeight(columnSetMultiIndicator.getCount());
            List<Object[]> listRows = columnSetMultiIndicator.getListRows();
            if (listRows != null) {
                JungGraphGenerator generator = new JungGraphGenerator(gBuilder, listRows);
                // MOD yyi 2009-09-09 feature 8834
                generator.generate(composite, false, true);
            }
        } else {

            List<MetadataColumn> numericOrDateList = new ArrayList<MetadataColumn>();
            if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator.eClass()) {
                numericOrDateList = columnSetMultiIndicator.getNumericColumns();
            }
            if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator.eClass()) {
                numericOrDateList = columnSetMultiIndicator.getDateColumns();
            }

            for (MetadataColumn column : numericOrDateList) {
                final MetadataColumn tdColumn = (MetadataColumn) column;

                ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                        | ExpandableComposite.CLIENT_INDENT);
                exComp.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.column", tdColumn.getName())); //$NON-NLS-1$
                exComp.setLayout(new GridLayout());
                exComp.setData(columnSetMultiIndicator);
                previewChartList.add(exComp);

                final Composite comp = toolkit.createComposite(exComp);
                comp.setLayout(new GridLayout());
                comp.setLayoutData(new GridData(GridData.FILL_BOTH));

                if (tdColumn != null) {
                    IRunnableWithProgress rwp = new IRunnableWithProgress() {

                        public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                            monitor.beginTask(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.createPreview", //$NON-NLS-1$
                                    tdColumn.getName()), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                            Display.getDefault().asyncExec(new Runnable() {

                                public void run() {
                                    new HideSeriesChartComposite(comp, analysis, columnSetMultiIndicator, tdColumn, false);
                                }

                            });

                            monitor.done();
                        }

                    };

                    try {
                        new ProgressMonitorDialog(getSite().getShell()).run(true, false, rwp);
                    } catch (Exception ex) {
                        log.error(ex, ex);
                    }
                }

                exComp.addExpansionListener(new ExpansionAdapter() {

                    @Override
                    public void expansionStateChanged(ExpansionEvent e) {
                        getChartComposite().layout();
                        form.reflow(true);
                    }

                });

                exComp.setExpanded(true);

                exComp.setClient(comp);
            }
        }

        if (!previewChartList.isEmpty()) {
            this.previewChartCompsites = previewChartList.toArray(new Composite[previewChartList.size()]);
        }
    }

    @Override
    public void refresh() {
        if (chartComposite != null) {
            try {
                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                createPreviewCharts(form, chartComposite, true);
                chartComposite.layout();
                getForm().reflow(true);
            } catch (Exception ex) {
                log.error(ex, ex);
            }

        }
    }

    /**
     * DOC yyi Comment method "createIndicatorsSection".
     * 
     * @param topComp
     * @param form
     */
    private void createIndicatorsSection(ScrolledForm form, Composite topComp) {
        indicatorsSection = createSection(form, topComp, "Indicators", null); //$NON-NLS-1$

        Composite indicatorsComp = toolkit.createComposite(indicatorsSection, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(indicatorsComp);
        indicatorsComp.setLayout(new GridLayout());
        ((GridData) indicatorsComp.getLayoutData()).heightHint = INDICATORS_SECTION_HEIGHT;

        indicatorsViewer = new IndicatorsComp(indicatorsComp, this);
        indicatorsViewer.setDirty(false);
        indicatorsViewer.addPropertyChangeListener(this);
        indicatorsViewer.setInput(columnSetMultiIndicator);
        indicatorsSection.setClient(indicatorsComp);
    }

    /**
     * @param form
     * @param toolkit
     * @param anasisDataComp
     */
    void createDataFilterSection(final ScrolledForm form, Composite anasisDataComp) {
        dataFilterSection = createSection(
                form,
                anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.dataFilter"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$

        Composite sectionClient = toolkit.createComposite(dataFilterSection);
        dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
        dataFilterComp.addPropertyChangeListener(this);
        dataFilterSection.setClient(sectionClient);
    }

    /**
     * DOC hcheng Comment method "createAnalysisParamSection".
     * 
     * @param form
     * @param anasisDataComp
     */
    void createAnalysisParamSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisParamSection = createSection(form, anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.AnalysisParameter"), null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(analysisParamSection);
        sectionClient.setLayout(new GridLayout(2, false));
        toolkit.createLabel(sectionClient, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.ExecutionEngine")); //$NON-NLS-1$
        final CCombo execCombo = new CCombo(sectionClient, SWT.BORDER);
        execCombo.setEditable(false);
        for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
            String temp = language.getLiteral();
            execCombo.add(temp);
        }
        // ExecutionLanguage executionLanguage =
        // analysis.getParameters().getExecutionLanguage();
        execCombo.setText(ExecutionLanguage.SQL.getLiteral());
        execCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                execLang = execCombo.getText();
            }

        });
        analysisParamSection.setClient(sectionClient);
    }

    /**
     * @param outputFolder
     * @throws DataprofilerCoreException
     */

    public void saveAnalysis() throws DataprofilerCoreException {
        // ADD gdbu 2011-3-3 bug 19179

        // remove the space from analysis name
        this.analysis.setName(this.analysis.getName().replace(" ", ""));
        // change 'ana' field's 'dataquality' tag content
        for (Domain domain : this.analysis.getParameters().getDataFilter()) {
            domain.setName(this.analysis.getName());
        }

        // ~

        IRepositoryViewObject reposObject = null;
        Connection tdProvider = null;
        correlationAnalysisHandler.clearAnalysis();
        columnSetMultiIndicator.getAnalyzedColumns().clear();

        // set execute engine
        Analysis analysis = correlationAnalysisHandler.getAnalysis();

        analysis.getParameters().setExecutionLanguage(ExecutionLanguage.get(execLang));

        // set data filter
        correlationAnalysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());

        // save analysis
        List<RepositoryNode> repositoryNodeList = treeViewer.getColumnSetMultiValueList();

        if (repositoryNodeList != null && repositoryNodeList.size() != 0) {
            reposObject = repositoryNodeList.get(0).getObject();
            tdProvider = ((ConnectionItem) reposObject.getProperty().getItem()).getConnection();
            // tdProvider = ConnectionHelper.getTdDataProvider(SwitchHelpers.COLUMN_SWITCH.doSwitch(columnList.get(0)));
            analysis.getContext().setConnection(tdProvider);

            List<TdColumn> columnLst = new ArrayList<TdColumn>();
            for (RepositoryNode repNode : repositoryNodeList) {
                columnLst.add((TdColumn) ((MetadataColumnRepositoryObject) repNode.getObject()).getTdColumn());
            }

            columnSetMultiIndicator.getAnalyzedColumns().addAll(columnLst);
            correlationAnalysisHandler.addIndicator(columnLst, columnSetMultiIndicator);
        } else {

            tdProvider = (Connection) analysis.getContext().getConnection();
            if (tdProvider != null) {
                tdProvider.getSupplierDependency().get(0).getClient().remove(analysis);
                analysis.getContext().setConnection(null);
                analysis.getResults().getIndicators().clear();
            }
            // MOD by zshen for bug 12042.
            ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
            ColumnSetMultiValueIndicator columnSetMultiValueIndicator = null;
            if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator.eClass()) {
                columnSetMultiValueIndicator = columnsetFactory.createCountAvgNullIndicator();
            }

            if (ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator.eClass()) {
                columnSetMultiValueIndicator = columnsetFactory.createMinMaxDateIndicator();
            }

            if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator.eClass()) {
                columnSetMultiValueIndicator = columnsetFactory.createWeakCorrelationIndicator();
            }

            fillSimpleIndicators(columnSetMultiValueIndicator);
            analysis.getResults().getIndicators().add(columnSetMultiValueIndicator);
            // ~12042
            analysis.getClientDependency().clear();
        }

        String urlString = analysis.eResource() != null ? analysis.eResource().getURI().toFileString()
                : PluginConstant.EMPTY_STRING;
        // ADD xqliu 2010-07-19 bug 14014
        this.updateAnalysisClientDependency();
        // ~ 14014
        // 2011.1.12 MOD by zhsne to unify anlysis and connection id when saving.
        ReturnCode saved = new ReturnCode(false);
        IEditorInput editorInput = this.getEditorInput();
        if (editorInput instanceof AnalysisItemEditorInput) {
            AnalysisItemEditorInput analysisInput = (AnalysisItemEditorInput) editorInput;
            TDQAnalysisItem tdqAnalysisItem = analysisInput.getTDQAnalysisItem();

            // ADD gdbu 2011-3-2 bug 19179
            tdqAnalysisItem.getProperty().setLabel(analysis.getName());
            this.nameText.setText(analysis.getName());
            // ~

            saved = ElementWriterFactory.getInstance().createAnalysisWrite().save(tdqAnalysisItem);
        }
        if (saved.isOk()) {
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(tdProvider);
            if (node != null) {
                // ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.TRUE, Boolean.TRUE);
                ElementWriterFactory.getInstance().createDataProviderWriter().save(node.getObject().getProperty().getItem());
            }
            // AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true
            // );
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + urlString + " successful");
            }
        } else {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString(
                    "ColumnMasterDetailsPage.problem", analysis.getName(), urlString, saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        treeViewer.setDirty(false);
        dataFilterComp.setDirty(false);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.correlationAnalysisHandler.setStringDataFilter((String) evt.getNewValue());
        }

    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || (treeViewer != null && treeViewer.isDirty())
                || (dataFilterComp != null && dataFilterComp.isDirty());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.treeViewer != null) {
            this.treeViewer.removePropertyChangeListener(this);
        }
        if (dataFilterComp != null) {
            this.dataFilterComp.removePropertyChangeListener(this);
        }
    }

    /**
     * Getter for treeViewer.
     * 
     * @return the treeViewer
     */
    public AnalysisColumnNominalIntervalTreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public ScrolledForm getForm() {
        return form;
    }

    public void setForm(ScrolledForm form) {
        this.form = form;
    }

    public ColumnCorrelationAnalysisHandler getColumnCorrelationAnalysisHandler() {
        return correlationAnalysisHandler;
    }

    public ColumnSetMultiValueIndicator getColumnSetMultiValueIndicator() {
        return columnSetMultiIndicator;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    @Override
    protected ReturnCode canSave() {

        // ADD gdbu 2011-3-3 bug 19179
        this.nameText.setText(this.nameText.getText().replace(" ", ""));
        if (this.nameText.getText().length() == 0) {
            // analysis can not without a name
            this.nameText.setText(this.analysis.getName());
            return new ReturnCode(DefaultMessagesImpl.getString("AbstractFilterMetadataPage.MSG_ANALYSIS_NONE_NAME"), false);
        }
        String elementName = this.nameText.getText();
        List<IRepositoryNode> childrensname = this.analysisRepNode.getParent().getChildren();
        for (IRepositoryNode children : childrensname) {
            if (elementName.equals(this.analysis.getName())) {
                // if new name equals itself's old name ,return true
                break;
            } else if (elementName.equals((children.getLabel() + "").replace(" ", ""))) {
                // if new name equals one of tree-list's name,return false
                this.nameText.setText(this.analysis.getName());
                return new ReturnCode(DefaultMessagesImpl.getString("AbstractFilterMetadataPage.MSG_ANALYSIS_SAME_NAME"), false);
            }
        }
        // ~

        String message = null;
        List<RepositoryNode> columnSetMultiValueList = getTreeViewer().getColumnSetMultiValueList();

        if (!columnSetMultiValueList.isEmpty()) {
            // MOD qiongli 2010-8-19.bug 14436.move some codes to the method of 'canDrop()', which judge to come
            // from same table
            List<RepositoryNode> nodes = treeViewer.getColumnSetMultiValueList();

            if (ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator() == columnSetMultiIndicator.eClass()
                    || ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator() == columnSetMultiIndicator.eClass()) {
                message = verifyColumn(nodes, columnSetMultiIndicator.eClass());

            } else if (ColumnsetPackage.eINSTANCE.getWeakCorrelationIndicator() == columnSetMultiIndicator.eClass()) {
                List<DBColumnRepNode> columnNodes = RepositoryNodeHelper.getColumnNodeList(nodes.toArray());
                for (DBColumnRepNode columNode : columnNodes) {
                    TdColumn tdColumn = columNode.getTdColumn();

                    if (correlationAnalysisHandler.getDatamingType(tdColumn) != DataminingType.NOMINAL) {
                        message = DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.NotAllNominal"); //$NON-NLS-1$
                        break;
                    }
                }
                // for (int i = 0; i < nodes.size(); i++) {
                // TdColumn tdColumn = (TdColumn) nodes.get(i);
                //
                // if (correlationAnalysisHandler.getDatamingType(tdColumn) != DataminingType.NOMINAL) {
                //                        message = DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.NotAllNominal"); //$NON-NLS-1$
                // break;
                // }
                // }
            }
        }
        if (message == null) {
            return new ReturnCode(true);
        }

        return new ReturnCode(message, false);
    }

    /**
     * 
     * DOC hcheng Comment method "verifyColumn". For 7513,Condition striction when save the correlation analysis.
     * 
     * @param columns
     * @param className
     * @return
     */
    private String verifyColumn(List<RepositoryNode> columns, EClass className) {
        List<TdColumn> invalidCols = new ArrayList<TdColumn>();
        List<TdColumn> nominalCols = new ArrayList<TdColumn>();
        List<TdColumn> intervalCols = new ArrayList<TdColumn>();
        String message = null;

        for (int i = 0; i < columns.size(); i++) {
            RepositoryNode tdColumnNode = (RepositoryNode) columns.get(i);
            TdColumn tdColumn = (TdColumn) ((MetadataColumnRepositoryObject) tdColumnNode.getObject()).getTdColumn();
            if (className == ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator()) {
                if (Java2SqlType.isDateInSQL(tdColumn.getSqlDataType().getJavaDataType())) {
                    invalidCols.add(tdColumn);
                    break;
                }
            } else if (className == ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator()) {
                if (Java2SqlType.isNumbericInSQL(tdColumn.getSqlDataType().getJavaDataType())) {
                    invalidCols.add(tdColumn);
                    break;
                }
            }
            if (correlationAnalysisHandler.getDatamingType(tdColumn) == DataminingType.NOMINAL) {
                nominalCols.add(tdColumn);
            }
            if (correlationAnalysisHandler.getDatamingType(tdColumn) == DataminingType.INTERVAL) {
                intervalCols.add(tdColumn);
            }

        }
        if (invalidCols.size() > 0) {
            if (className == ColumnsetPackage.eINSTANCE.getCountAvgNullIndicator()) {
                message = DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.ColumnNotUsed"); //$NON-NLS-1$
            } else if (className == ColumnsetPackage.eINSTANCE.getMinMaxDateIndicator()) {
                message = DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.NumbericColumn"); //$NON-NLS-1$
            }
        } else if (nominalCols.size() == 0 || intervalCols.size() == 0) {
            message = DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.HaveIntervalAndNominal"); //$NON-NLS-1$

        }
        return message;

    }

    @Override
    protected ReturnCode canRun() {
        List<RepositoryNode> columnSetMultiValueList = getTreeViewer().getColumnSetMultiValueList();
        if (columnSetMultiValueList.isEmpty()) {
            return new ReturnCode(
                    DefaultMessagesImpl.getString("ColumnCorrelationNominalAndIntervalMasterPage.NoColumnsAssigned"), false); //$NON-NLS-1$
        }

        return new ReturnCode(true);

    }
}

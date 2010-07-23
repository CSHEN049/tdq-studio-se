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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UIPagination;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dataquality.indicators.sql.util.IndicatorSqlSwitch;
import org.talend.dq.analysis.ModelElementAnalysisHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.informix.util.stringUtil;

/**
 * @author rli
 * 
 */
public class ColumnMasterDetailsPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnMasterDetailsPage.class);

    private String execLang;

    private Button drillDownCheck;

    private Text maxNumText;

    private CCombo execCombo;

    AnalysisColumnTreeViewer treeViewer;

    DataFilterComp dataFilterComp;

    ModelElementAnalysisHandler analysisHandler;

    private ModelElementIndicator[] currentModelElementIndicators;

    private String stringDataFilter;

    private Composite chartComposite;

    private ScrolledForm form;

    private static final int TREE_MAX_LENGTH = 400;

    private Composite[] previewChartCompsites;

    private AnalysisEditor currentEditor;

    private Section dataFilterSection = null;

    private Section analysisColumnSection = null;

    private Section analysisParamSection = null;

    private Section previewSection = null;

    private List<ExpandableComposite> previewChartList = null;

    private static IndicatorSqlSwitch<JavaUserDefIndicator> javaUserDefIndSwitch = new IndicatorSqlSwitch<JavaUserDefIndicator>() {

        @Override
        public JavaUserDefIndicator caseJavaUserDefIndicator(JavaUserDefIndicator object) {
            return object;
        }

    };

    public ColumnMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (AnalysisEditor) editor;
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        recomputeIndicators();
    }

    public void recomputeIndicators() {
        analysisHandler = new ModelElementAnalysisHandler();
        analysisHandler.setAnalysis((Analysis) this.currentModelElement);
        stringDataFilter = analysisHandler.getStringDataFilter();
        EList<ModelElement> analyzedColumns = analysisHandler.getAnalyzedColumns();
        List<ModelElementIndicator> meIndicatorList = new ArrayList<ModelElementIndicator>();
        ModelElementIndicator currentIndicator;
        for (ModelElement element : analyzedColumns) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            TdXMLElement xmlElement = SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(element);
            if (tdColumn == null && xmlElement == null) {
                continue;
            }
            currentIndicator = ModelElementIndicatorHelper.createModelElementIndicator(element);
            DataminingType dataminingType = DataminingType.get(analysisHandler.getDatamingType(element));
            MetadataHelper.setDataminingType(dataminingType == null ? DataminingType.NOMINAL : dataminingType, element);
            Collection<Indicator> indicatorList = analysisHandler.getIndicators(element);
            currentIndicator.setIndicators(indicatorList.toArray(new Indicator[indicatorList.size()]));
            meIndicatorList.add(currentIndicator);
        }
        currentModelElementIndicators = meIndicatorList.toArray(new ModelElementIndicator[meIndicatorList.size()]);
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
        form.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnAna")); //$NON-NLS-1$
        metadataSection.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        createAnalysisColumnsSection(form, topComp);

        createDataFilterSection(form, topComp);

        // MOD xqliu 2009-07-01 bug 7068
        createAnalysisParamSection(form, topComp);
        // ~

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

        createPreviewSection(form, previewComp);
    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisColumnSection = createSection(form, anasisDataComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.analyzeColumn"), null); //$NON-NLS-1$

        Composite topComp = toolkit.createComposite(analysisColumnSection);
        topComp.setLayout(new GridLayout());
        // ~ MOD mzhao 2009-05-05,Bug 6587.
        createConnBindWidget(topComp);
        // ~

        Hyperlink clmnBtn = toolkit.createHyperlink(topComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.selectColumn"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog();
            }

        });

        Hyperlink indcBtn = toolkit.createHyperlink(topComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.selectIndicator"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(indcBtn);
        indcBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                treeViewer.openIndicatorSelectDialog(null);
            }

        });

        Composite actionBarComp = toolkit.createComposite(topComp);
        GridLayout gdLayout = new GridLayout();
        gdLayout.numColumns = 2;
        actionBarComp.setLayout(gdLayout);

        ImageHyperlink collapseAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        collapseAllImageLink.setToolTipText(DefaultMessagesImpl.getString("CollapseAllColumns")); //$NON-NLS-1$
        collapseAllImageLink.setImage(ImageLib.getImage(ImageLib.COLLAPSE_ALL));
        collapseAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                TreeItem[] items = treeViewer.getTree().getItems();
                expandTreeItems(items, false);
                packOtherColumns();
            }
        });

        ImageHyperlink expandAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        expandAllImageLink.setToolTipText(DefaultMessagesImpl.getString("ExpandAllColumns")); //$NON-NLS-1$
        expandAllImageLink.setImage(ImageLib.getImage(ImageLib.EXPAND_ALL));
        expandAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                TreeItem[] items = treeViewer.getTree().getItems();
                expandTreeItems(items, true);
                packOtherColumns();
            }
        });

        Composite tree = toolkit.createComposite(topComp, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnalysisColumnTreeViewer(tree, this);
        treeViewer.setDirty(false);
        treeViewer.addPropertyChangeListener(this);
        analysisColumnSection.setClient(topComp);
    }

    /**
     * 
     * DOC mzhao Comment method "packOtherColumns".
     */
    private void packOtherColumns() {
        TreeColumn columns[] = treeViewer.getTree().getColumns();
        for (TreeColumn column : columns) {
            column.pack();
        }
        treeViewer.getTree().pack();
        treeViewer.getTree().getParent().layout();
    }

    /**
     * 
     * DOC mzhao Comment method "expandTreeItems".
     * 
     * @param items
     * @param expandOrCollapse
     */
    private void expandTreeItems(TreeItem[] items, boolean expandOrCollapse) {
        for (TreeItem item : items) {
            item.setExpanded(expandOrCollapse);
            TreeItem[] its = item.getItems();
            if (its != null && its.length > 0) {
                expandTreeItems(its, expandOrCollapse);
            }
        }
    }

    /**
     * 
     */
    public void openColumnsSelectionDialog() {
        ModelElementIndicator[] modelElementIndicators = treeViewer.getModelElementIndicator();
        List<ModelElement> modelElementList = new ArrayList<ModelElement>();
        for (ModelElementIndicator modelElementIndicator : modelElementIndicators) {
            modelElementList.add(modelElementIndicator.getModelElement());
        }
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(this, null, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.columnSelection"), modelElementList, DefaultMessagesImpl //$NON-NLS-1$
                .getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$
        if (dialog.open() == Window.OK) {
            Object[] modelElements = dialog.getResult();
            treeViewer.setInput(modelElements);
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

        Composite actionBarComp = toolkit.createComposite(sectionClient);
        GridLayout gdLayout = new GridLayout();
        gdLayout.numColumns = 2;
        actionBarComp.setLayout(gdLayout);

        ImageHyperlink collapseAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        collapseAllImageLink.setToolTipText(DefaultMessagesImpl.getString("CollapseAllColumns")); //$NON-NLS-1$
        collapseAllImageLink.setImage(ImageLib.getImage(ImageLib.COLLAPSE_ALL));
        collapseAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                if (previewChartList != null && !previewChartList.isEmpty()) {
                    for (ExpandableComposite comp : previewChartList) {
                        comp.setExpanded(false);
                        comp.getParent().pack();
                    }
                }
            }
        });

        ImageHyperlink expandAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        expandAllImageLink.setToolTipText(DefaultMessagesImpl.getString("ExpandAllColumns")); //$NON-NLS-1$
        expandAllImageLink.setImage(ImageLib.getImage(ImageLib.EXPAND_ALL));
        expandAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                if (previewChartList != null && !previewChartList.isEmpty()) {
                    for (ExpandableComposite comp : previewChartList) {
                        comp.setExpanded(true);
                        comp.getParent().pack();
                    }
                }

            }
        });

        ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
        refreshBtn.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.refreshGraphics")); //$NON-NLS-1$
        refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        final Label message = toolkit.createLabel(sectionClient, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.spaceWhite")); //$NON-NLS-1$
        message.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        message.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        chartComposite = toolkit.createComposite(sectionClient);
        chartComposite.setLayout(new GridLayout());
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Analysis analysis = analysisHandler.getAnalysis();

        refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {

                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                boolean analysisStatue = analysis.getResults().getResultMetadata() != null
                        && analysis.getResults().getResultMetadata().getExecutionDate() != null;

                if (!analysisStatue) {
                    boolean returnCode = MessageDialog.openConfirm(null, DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.ViewResult"), //$NON-NLS-1$
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
        previewChartList = new ArrayList<ExpandableComposite>();

        final ModelElementIndicator[] modelElementIndicatores = treeViewer.getModelElementIndicator();
        // ~ MOD mzhao 2009-04-20, Do pagination. Bug 6512.
        UIPagination uiPagination = new UIPagination(toolkit, composite);
        int pageSize = UIPagination.getPageSize();
        int totalPages = modelElementIndicatores.length / pageSize;
        List<ModelElementIndicator> modelElementIndicators = null;
        for (int index = 0; index < totalPages; index++) {
            modelElementIndicators = new ArrayList<ModelElementIndicator>();
            for (int idx = 0; idx < pageSize; idx++) {
                modelElementIndicators.add(modelElementIndicatores[index * pageSize + idx]);
            }
            PaginationInfo pginfo = new MasterPaginationInfo(form, previewChartList, modelElementIndicators, uiPagination);
            uiPagination.addPage(pginfo);

        }
        int left = modelElementIndicatores.length % pageSize;
        if (left != 0) {
            modelElementIndicators = new ArrayList<ModelElementIndicator>();
            for (int leftIdx = 0; leftIdx < left; leftIdx++) {
                modelElementIndicators.add(modelElementIndicatores[totalPages * pageSize + leftIdx]);
            }
            PaginationInfo pginfo = new MasterPaginationInfo(form, previewChartList, modelElementIndicators, uiPagination);
            uiPagination.addPage(pginfo);
            totalPages++;
        }
        uiPagination.init();
        // ~

        for (ExpandableComposite comp : previewChartList) {
            registerSection(comp);
        }

        composite.layout();
        composite.pack();
        form.reflow(true);
    }

    @Override
    public void refresh() {
        if (chartComposite != null) {
            try {
                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                createPreviewCharts(form, chartComposite, true);
                chartComposite.getParent().layout();
                chartComposite.layout();
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
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
        analysisParamSection = createSection(form, anasisDataComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.AnalysisParameter"), null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(analysisParamSection);

        sectionClient.setLayout(new GridLayout(2, false));
        toolkit.createLabel(sectionClient, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.ExecutionEngine")); //$NON-NLS-1$
        // MOD zshen:need to use the component with finish indicator Selection.
        execCombo = new CCombo(sectionClient, SWT.BORDER);
        // ~
        execCombo.setEditable(false);

        for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
            String temp = language.getLiteral();
            execCombo.add(temp);
        }
        ExecutionLanguage executionLanguage = analysis.getParameters().getExecutionLanguage();
        // MOD zshen feature 12919 : add allow drill down and max number row component for java engin.
        final Composite javaEnginSection = createjavaEnginSection(sectionClient);
        if (ExecutionLanguage.SQL.equals(executionLanguage)) {
            javaEnginSection.setVisible(false);
        }
        execCombo.setText(executionLanguage.getLiteral());
        execLang = executionLanguage.getLiteral();
        // ADD xqliu 2009-08-24 bug 8776
        treeViewer.setLanguage(ExecutionLanguage.get(executionLanguage.getLiteral()));
        // ~
        execCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                // MOD xqliu 2009-08-24 bug 8776
                execLang = execCombo.getText();
                // MOD mzhao feature 11128, 2010-01-29. Java Engine is applicable to Java UDI. Set the Java UDI the
                // right language.
                EList<Indicator> inds = analysis.getResults().getIndicators();
                for (Indicator ind : inds) {
                    if (javaUserDefIndSwitch.doSwitch(ind) != null) {
                        ((JavaUserDefIndicator) ind).setExecuteEngine(ExecutionLanguage.get(execLang));
                    }
                }

                // MOD zshen 11104 2010-01-27: when have a datePatternFreqIndicator in the
                // "analyzed Columns",ExecutionLanguage only is Java.
                if (ExecutionLanguage.SQL.equals(ExecutionLanguage.get(execLang)) && includeDatePatternFreqIndicator()) {
                    MessageUI.openWarning(DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.DatePatternFreqIndicatorWarning")); //$NON-NLS-1$
                    execCombo.setText(ExecutionLanguage.JAVA.getLiteral());
                    execLang = execCombo.getText();
                    return;
                }
                // ~11104
                // MOD zshen feature 12919 : hidden or display parameter of java engin.
                if (ExecutionLanguage.SQL.equals(ExecutionLanguage.get(execLang))) {
                    javaEnginSection.setVisible(false);
                } else {
                    javaEnginSection.setVisible(true);
                }
                // 12919~
                setDirty(true);
                treeViewer.setLanguage(ExecutionLanguage.get(execLang));
                // ~
            }

        });
        analysisParamSection.setClient(sectionClient);
    }

    /**
     * 
     * add zshen feature 12919.
     */
    protected Composite createjavaEnginSection(Composite sectionClient) {

        AnalysisParameters anaParameters = analysisHandler.getAnalysis().getParameters();
        Composite javaEnginSection = toolkit.createComposite(sectionClient);
        Composite checkSection = toolkit.createComposite(javaEnginSection);
        Composite numberSection = toolkit.createComposite(javaEnginSection);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;

        GridDataFactory.fillDefaults().grab(true, false).span(2, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(javaEnginSection);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(checkSection);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(numberSection);

        javaEnginSection.setLayout(gridLayout);
        checkSection.setLayout(gridLayout);
        numberSection.setLayout(gridLayout);
        Label drillDownLabel = toolkit.createLabel(checkSection, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.allowDrillDownLabel"));
        drillDownCheck = toolkit.createButton(checkSection, "", SWT.CHECK);
        drillDownCheck.setSelection(true);
        drillDownCheck.setSelection(anaParameters.isStoreData());
        drillDownCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirty(true);
            }

        });
        Label maxNumLabel = toolkit.createLabel(numberSection, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.maxNumberLabel"));
        maxNumText = toolkit.createText(numberSection, null, SWT.BORDER);
        maxNumText.setText(String.valueOf(anaParameters.getMaxNumberRows()));
        maxNumText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String textContent = maxNumText.getText();
                if (stringUtil.isANum(textContent)) {
                    setDirty(true);
                } else {
                    MessageDialog.openWarning(e.display.getActiveShell(), DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.warningMessageTitle"), DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.integerConvertWarning"));
                    maxNumText.setText(textContent.substring(0, textContent.length() - 1));
                }

            }

        });
        GridDataFactory.fillDefaults().grab(true, false).applyTo(maxNumText);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(maxNumLabel);
        // ((GridData) maxNumText.getLayoutData()).widthHint = 200;
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(drillDownCheck);
        // GridData data1 = new GridData();
        // data1.horizontalSpan = 2;
        // drillDownCheck.setLayoutData(data1);
        // maxNumLabel.setLayoutData(data1);
        return javaEnginSection;
    }

    /**
     * ADD xqliu 2009-08-24 bug 8776.
     * 
     * @return
     */
    protected boolean includeUDI() {
        ModelElementIndicator[] modelElementIndicators = this.getTreeViewer().getModelElementIndicator();
        for (ModelElementIndicator modelElementIndicator : modelElementIndicators) {
            Indicator[] indicators = modelElementIndicator.getIndicators();
            for (Indicator indicator : indicators) {
                if (indicator instanceof UserDefIndicator) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param outputFolder
     * @throws DataprofilerCoreException
     */
    public void saveAnalysis() throws DataprofilerCoreException {
        analysisHandler.clearAnalysis();
        ModelElementIndicator[] modelElementIndicators = treeViewer.getModelElementIndicator();
        // List<TdDataProvider> providerList = new ArrayList<TdDataProvider>();
        Connection tdProvider = null;
        Analysis analysis = analysisHandler.getAnalysis();
        analysis.getParameters().setExecutionLanguage(ExecutionLanguage.get(execLang));
        // MOD zshen feature 12919 to save analysisParameter.
        if (ExecutionLanguage.JAVA.equals(ExecutionLanguage.get(execLang))) {
            analysis.getParameters().setMaxNumberRows(Integer.parseInt(maxNumText.getText()));
            analysis.getParameters().setStoreData(drillDownCheck.getSelection());
        }
        // ~12919
        if (modelElementIndicators != null && modelElementIndicators.length != 0) {

            tdProvider = ModelElementIndicatorHelper.getTdDataProvider(modelElementIndicators[0]);
            analysis.getContext().setConnection(tdProvider);

            for (ModelElementIndicator modelElementIndicator : modelElementIndicators) {
                analysisHandler.addIndicator(modelElementIndicator.getModelElement(), modelElementIndicator.getIndicators());
                DataminingType type = MetadataHelper.getDataminingType(modelElementIndicator.getModelElement());
                if (type == null) {
                    type = MetadataHelper.getDefaultDataminingType(modelElementIndicator.getJavaType());
                }
                analysisHandler.setDatamingType(type.getLiteral(), modelElementIndicator.getModelElement());
            }
        } else {
            analysis.getContext().setConnection(null);
            analysis.getClientDependency().clear();
        }
        // if (providerList.size() != 0) {
        //           
        // }
        analysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());
        // boolean modifiedResourcesSaved =
        // analysisHandler.saveModifiedResources();
        // if (!modifiedResourcesSaved) {
        // log.error("Problem when saving modified resource.");
        // }
        // AnalysisWriter writer = new AnalysisWriter();

        String urlString = analysis.eResource() != null ? analysis.eResource().getURI().toFileString()
                : PluginConstant.EMPTY_STRING;
        // try {
        // urlString = editorInput.getFile();
        // analysisHandler.getAnalysis().setUrl(urlString);
        // } catch (MalformedURLException e) {
        // }

        // FIXME after i set the options of bins designer, and when saving the
        // file, it cause a exception.

        // ADD xqliu 2010-07-19 bug 14014
        this.updateAnalysisClientDependency();
        // ~ 14014

        // File file = new File(editorInput.getFile().getParent() +
        // File.separator + fileName);
        // ReturnCode saved = writer.save(analysisHandler.getAnalysis(), file);
        ReturnCode saved = AnaResourceFileHelper.getInstance().save(analysis);
        if (saved.isOk()) {
            if (tdProvider != null) {
                PrvResourceFileHelper.getInstance().save(tdProvider);
            }
            // AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true
            // );
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + urlString + " successful"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString(
                    "ColumnMasterDetailsPage.problem", analysis.getName(), urlString, saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        // TODO get the domain constraint, we will see later.
        // Domain dataFilter = getDataFilter(dataManager, (Column) column); //
        // CAST here for test
        // analysisBuilder.addFilterOnData(dataFilter);
        //
        // FolderProvider folderProvider = new FolderProvider();
        // folderProvider.setFolder(new File(outputFolder));
        // DqRepositoryViewService.saveDomain(dataFilter, folderProvider);
        treeViewer.setDirty(false);
        dataFilterComp.setDirty(false);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
            currentEditor.setRefreshResultPage(true);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.analysisHandler.setStringDataFilter((String) evt.getNewValue());
        }
    }

    // public void setDirty(boolean isDirty) {
    // if (this.isDirty != isDirty) {
    // this.isDirty = isDirty;
    // ((AnalysisEditor)
    // this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
    // }
    // }

    @Override
    public boolean isDirty() {
        return super.isDirty() || treeViewer.isDirty() || dataFilterComp.isDirty();
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
    public AnalysisColumnTreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public ScrolledForm getForm() {
        return form;
    }

    public void setForm(ScrolledForm form) {
        this.form = form;
    }

    public ModelElementAnalysisHandler getAnalysisHandler() {
        return analysisHandler;
    }

    public ModelElementIndicator[] getCurrentModelElementIndicators() {
        return this.currentModelElementIndicators;
    }

    public Composite[] getPreviewChartCompsites() {
        if (previewChartList != null && !previewChartList.isEmpty()) {
            this.previewChartCompsites = previewChartList.toArray(new Composite[previewChartList.size()]);
        }
        return previewChartCompsites;
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    @Override
    protected ReturnCode canRun() {
        // ADD xqliu 2010-01-22 bug 11200
        ReturnCode checkMdmExecutionEngine = checkMdmExecutionEngine();
        if (!checkMdmExecutionEngine.isOk()) {
            return checkMdmExecutionEngine;
        }
        // ~
        ModelElementIndicator[] modelElementIndicators = treeViewer.getModelElementIndicator();
        if (modelElementIndicators == null || modelElementIndicators.length == 0) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.NoColumnAssigned"), false); //$NON-NLS-1$
        }
        for (ModelElementIndicator modelElementIndicator : modelElementIndicators) {
            if (modelElementIndicator.getIndicators().length == 0) {
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.NoIndicatorAssigned"), false); //$NON-NLS-1$
            }
        }

        return new ReturnCode(true);
    }

    @Override
    protected ReturnCode canSave() {
        // ADD xqliu 2010-01-22 bug 11200
        ReturnCode checkMdmExecutionEngine = checkMdmExecutionEngine();
        if (!checkMdmExecutionEngine.isOk()) {
            return checkMdmExecutionEngine;
        }
        // ~
        List<ModelElement> analyzedElement = new ArrayList<ModelElement>();

        for (ModelElementIndicator modelElementIndicator : treeViewer.getModelElementIndicator()) {
            analyzedElement.add(modelElementIndicator.getModelElement());
        }

        if (!analyzedElement.isEmpty()) {
            if (!ModelElementHelper.isFromSameConnection(analyzedElement)) {
                return new ReturnCode(
                        DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.notSameElementMessage"), false); //$NON-NLS-1$
            }

            if (!ModelElementHelper.isFromSameTable(analyzedElement) && !"".equals(dataFilterComp.getDataFilterString())) { //$NON-NLS-1$
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.CannotCreatAnalysis"), false); //$NON-NLS-1$
            }
        }

        return new ReturnCode(true);
    }

    /**
     * DOC xqliu Comment method "checkMdmExecutionEngine".
     */
    private ReturnCode checkMdmExecutionEngine() {
        ModelElementIndicator[] modelElementIndicators = treeViewer.getModelElementIndicator();
        if (modelElementIndicators != null && modelElementIndicators.length != 0) {
            analysis.getContext().setConnection(ModelElementIndicatorHelper.getTdDataProvider(modelElementIndicators[0]));
        }
        // feature 13573 add the support to java engine
        // if (analysisHandler.isMdmConnection() && ExecutionLanguage.JAVA.getLiteral().equals(this.execLang)) {
        //            return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.mdmSQL"), false); //$NON-NLS-1$
        // }
        return new ReturnCode(true);
    }

    /**
     * 
     * DOC zshen Comment method "getExecCombo".
     * 
     * @return the Combo for executeLanguage
     */
    public CCombo getExecCombo() {
        return execCombo;
    }

    /**
     * 
     * DOC zshen Comment method "includeDatePatternFreqIndicator".
     * 
     * @return whether have a datePatternFreqIndicator in the "analyzed Columns"
     */
    public boolean includeDatePatternFreqIndicator() {
        for (ModelElementIndicator modelElementIndicator : this.treeViewer.getModelElementIndicator()) {
            if (modelElementIndicator.contains(IndicatorEnum.DatePatternFreqIndicatorEnum)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * DOC zshen Comment method "chageExecuteLanguageToJava". change ExecutionLanuage to Java.
     */
    public void chageExecuteLanguageToJava() {
        if (!(ExecutionLanguage.JAVA.getLiteral().equals(this.execLang))) {
            int i = 0;
            for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
                if (language.compareTo(ExecutionLanguage.JAVA) == 0) {
                    this.execCombo.select(i);
                } else {
                    i++;
                }
            }
        }
    }

}

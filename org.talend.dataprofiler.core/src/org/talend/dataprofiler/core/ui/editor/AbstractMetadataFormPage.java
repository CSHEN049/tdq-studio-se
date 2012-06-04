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
package org.talend.dataprofiler.core.ui.editor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EmfHelper;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.CorePackage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractMetadataFormPage extends AbstractFormPage {

    private static final int MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING = 200;

    public static final String ACTION_HANDLER = "ACTION_HANDLER"; //$NON-NLS-1$

    private static final int META_FIELD_WIDTH = 200;

    private static final String NAME_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.name"); //$NON-NLS-1$

    private static final String PURPOSE_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.purpose"); //$NON-NLS-1$

    private static final String DESCRIPTION_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.description"); //$NON-NLS-1$

    private static final String AUTHOR_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.author"); //$NON-NLS-1$

    // private static final String LOCKER_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.locker"); //$NON-NLS-1$

    // private static final String VERSION_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.version"); //$NON-NLS-1$

    private static final String STATUS_LABEL = DefaultMessagesImpl.getString("AbstractMetadataFormPage.status"); //$NON-NLS-1$

    private static final String WHITESPACE_CHECK_MSG = DefaultMessagesImpl.getString("AbstractMetadataFormPage.whitespace"); //$NON-NLS-1$

    private static final String NAMECONNOTBEEMPTY = DefaultMessagesImpl.getString("AbstractMetadataFormPage.nameCannotBeEmpty"); //$NON-NLS-1$

    protected Text nameText;

    protected Text purposeText;

    protected Text descriptionText;

    protected Text authorText;

    protected boolean modify;

    // protected Text lockerText;

    // protected Text versionText;

    protected CCombo statusCombo;

    protected Composite topComp;

    protected Section metadataSection;

    protected ModelElement currentModelElement;

    protected RepositoryNode repositoryNode;

    protected RepositoryViewObject repositoryViewObject;

    protected ScrolledForm form;

    private String formTitle;

    private String metadataTitle;

    public String oldDataproviderName;

    private Collection<Text> checkWhitespaceTextFields = new HashSet<Text>();

    public AbstractMetadataFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        this.currentModelElement = getCurrentModelElement(editor);
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(currentModelElement);
        if (recursiveFind != null) {
            this.repositoryNode = recursiveFind;
            if (this.repositoryNode.getObject() != null) {
                this.repositoryViewObject = (RepositoryViewObject) this.repositoryNode.getObject();
            }
        }
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        form = managedForm.getForm();
        form.setText(getFormTitle());
        Composite body = form.getBody();

        // TableWrapLayout layout = new TableWrapLayout();
        body.setLayout(new GridLayout());

        topComp = toolkit.createComposite(body);
        GridData anasisData = new GridData(GridData.FILL_BOTH);

        topComp.setLayoutData(anasisData);
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
    }

    /**
     * DOC bZhou Comment method "getIntactElemenetName".
     * 
     * @return
     */
    public String getIntactElemenetName() {
        if (currentModelElement == null) {
            currentModelElement = getCurrentModelElement(getEditor());
        }
        return DqRepositoryViewService.buildElementName(getProperty());
    }

    protected abstract ModelElement getCurrentModelElement(FormEditor editor);

    protected Section creatMetadataSection(final ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, getMetadataTitle(), ""); //$NON-NLS-1$ //$NON-NLS-2$
        Composite parent = toolkit.createComposite(section);
        parent.setLayout(new GridLayout(2, false));

        nameText = createMetadataTextFiled(NAME_LABEL, parent);
        // set the max number of characters to be entered in the text field
        // ADDED sgandon 16/03/2010 bug 11760
        nameText.setTextLimit(EmfHelper.getStringMaxSize(CorePackage.Literals.MODEL_ELEMENT__NAME,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        purposeText = createMetadataTextFiled(PURPOSE_LABEL, parent);
        // set the max number of characters to be entered in the text field
        // ADDED sgandon 16/03/2010 bug 11760
        purposeText.setTextLimit(TaggedValueHelper.getStringMaxSize(TaggedValueHelper.PURPOSE,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        // description fields
        // ADDED sgandon 16/03/2010 bug 11760
        toolkit.createLabel(parent, DESCRIPTION_LABEL);

        descriptionText = toolkit.createText(parent, null, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridDataFactory.fillDefaults().hint(META_FIELD_WIDTH, 60).applyTo(descriptionText);
        // set the max number of characters to be entered in the text field
        descriptionText.setTextLimit(TaggedValueHelper.getStringMaxSize(TaggedValueHelper.DESCRIPTION,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        authorText = createMetadataTextFiled(AUTHOR_LABEL, parent);
        // ADDED 2010-04-01 sgandon bug 11760 : author size limitation
        authorText.setTextLimit(TaggedValueHelper.getStringMaxSize(TaggedValueHelper.AUTHOR,
                MAX_TEXT_FIELD_STRING_SIZE_FOR_USUAL_STRING));

        // MOD 2009-09-08 yyi Feature: 8870.
        if (!ReponsitoryContextBridge.isDefautProject()) {
            authorText.setEnabled(false);
            authorText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        }
        // toolkit.createLabel(parent, VERSION_LABEL);
        // createVersionUI(parent);

        // lockerText = createMetadataTextFiled(LOCKER_LABEL, parent);

        // versionText = createMetadataVersionFiled(VERSION_LABEL, parent);

        toolkit.createLabel(parent, STATUS_LABEL); //$NON-NLS-1$
        statusCombo = new CCombo(parent, SWT.BORDER);
        statusCombo.setEditable(false);

        // MOD mzhao feature 7479 2009-10-16
        String statusValue = getProperty() != null ? getProperty().getStatusCode() : DevelopmentStatus.DRAFT.getLiteral();

        List<org.talend.core.model.properties.Status> statusList = MetadataHelper.getTechnicalStatus();
        if (statusList != null && statusList.size() > 0) {
            List<String> statusArray = MetadataHelper.toArray(statusList);
            String[] tempString = new String[statusList.size()];
            statusCombo.setItems(statusArray.toArray(tempString));
            if (statusArray.contains(statusValue)) {
                statusCombo.remove(statusValue);
                statusCombo.add(statusValue, 0);
            }
        } else {
            for (DevelopmentStatus status : DevelopmentStatus.values()) {
                statusCombo.add(status.getLiteral());
            }
            // statusCombo.remove(statusValue);
            statusCombo.add(statusValue, 0);
        }

        initMetaTextFied();

        nameText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                // boolean dirty = isDirty();
                modify = true;
                setDirty(true);

                // MOD msjian 2011-7-18 23216: when changed the name of a connection to null, write a warning
                if (PluginConstant.EMPTY_STRING.equals(nameText.getText())) {
                    getManagedForm().getMessageManager().addMessage(NAMECONNOTBEEMPTY, NAMECONNOTBEEMPTY, null,
                            IMessageProvider.ERROR, nameText);
                } else {
                    getManagedForm().getMessageManager().removeMessage(NAMECONNOTBEEMPTY, nameText);
                }

                // if ("".equals(nameText.getText().trim())) { //$NON-NLS-1$
                // setDirty(dirty);
                // MessageDialog.openError(null,
                //                            DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.error"), DefaultMessagesImpl.getString("AbstractMetadataFormPage.nameCannotBeEmpty"));//$NON-NLS-1$//$NON-NLS-2$
                // nameText.removeModifyListener(this);
                // Property property = getProperty();
                // nameText.setText(property.getLabel());
                // nameText.addModifyListener(this);
                // }

                // fireTextChange();
            }

        });
        purposeText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });
        descriptionText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });

        authorText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }
        });

        // versionText.addModifyListener(new ModifyListener() {
        //
        // /*
        // * (non-Javadoc)
        // *
        // * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
        // */
        // public void modifyText(ModifyEvent e) {
        // setDirty(true);
        // }
        // });

        statusCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // fireTextChange();
            }

        });

        addWhitespaceValidate(nameText, purposeText, descriptionText, authorText, purposeText);
        section.setClient(parent);
        return section;
    }

    // private Text createMetadataVersionFiled(String label, Composite parent) {
    // toolkit.createLabel(parent, label);
    // return createVersionUI(parent);
    // }

    // /**
    // * DOC bZhou Comment method "createVersionUI".
    // *
    // * @param parent
    // */
    // private Text createVersionUI(Composite parent) {
    // Composite versionContainer = new Composite(parent, SWT.NONE);
    // versionContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // GridLayout versionLayout = new GridLayout(3, false);
    // versionLayout.marginHeight = 0;
    // versionLayout.marginWidth = 0;
    // versionLayout.horizontalSpacing = 0;
    // versionContainer.setLayout(versionLayout);
    //
    // final Text text = new Text(versionContainer, SWT.BORDER);
    // text.setEnabled(false);
    // text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    //
    // Button versionMajorBtn = new Button(versionContainer, SWT.PUSH);
    //        versionMajorBtn.setText("M"); //$NON-NLS-1$
    //
    // Button versionMinorBtn = new Button(versionContainer, SWT.PUSH);
    //        versionMinorBtn.setText("m"); //$NON-NLS-1$
    //
    // versionMajorBtn.addSelectionListener(new SelectionAdapter() {
    //
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // String version = text.getText();
    // version = VersionUtils.upMajor(version);
    // text.setText(version);
    // Property property = getProperty();
    // if (property != null) {
    // property.setVersion(version);
    // property.setCreationDate(new Date());
    // }
    // setDirty(true);
    // }
    // });
    //
    // versionMinorBtn.addSelectionListener(new SelectionAdapter() {
    //
    // @Override
    // public void widgetSelected(SelectionEvent e) {
    // String version = text.getText();
    // version = VersionUtils.upMinor(version);
    // text.setText(version);
    // Property property = getProperty();
    // if (property != null) {
    // property.setVersion(version);
    // property.setCreationDate(new Date());
    // }
    // setDirty(true);
    // }
    // });
    //
    // return text;
    // }

    /**
     * DOC bZhou Comment method "createMetadataTextFiled".
     * 
     * @param text
     * @param parent
     * @return MOD sgandon 16/03/2010 bug 11760 : unecessary parameter removed
     */
    private Text createMetadataTextFiled(String label, Composite parent) {
        toolkit.createLabel(parent, label);

        Text text = toolkit.createText(parent, null, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(text);
        ((GridData) text.getLayoutData()).widthHint = META_FIELD_WIDTH;
        return text;
    }

    protected void initMetaTextFied() {

        Property property = getProperty();

        if (property != null) {
            // MDO qionlgi 2012-5-30 TDQ-5078 the ModelElement name could contain special chars.
            String name = currentModelElement.getName();
            if (name == null || PluginConstant.EMPTY_STRING.equals(name)) {
                name = property.getLabel();
            }
            String purpose = property.getPurpose();
            String description = property.getDescription();
            String author = property.getAuthor().getLogin();
            // String version = property.getVersion();
            String devStatus = property.getStatusCode();

            // String lockerStr = null;
            // ItemState state = property.getItem().getState();
            // if (state != null) {
            // User locker = state.getLocker();
            // if (locker != null) {
            // lockerStr = locker.getLogin();
            // }
            // }

            nameText.setText(name == null ? PluginConstant.EMPTY_STRING : name);
            // MOD klliu 2010-04-21 bug 20204 get the init value
            setOldDataproviderName(nameText.getText());
            purposeText.setText(purpose == null ? PluginConstant.EMPTY_STRING : purpose);
            descriptionText.setText(description == null ? PluginConstant.EMPTY_STRING : description);
            // ~ MOD klliu bug 3938 check the currentModelElement's AUTHOR whether is null,if not ,
            // the content of authorText is currentModelElement's AUTHOR
            TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.AUTHOR, currentModelElement.getTaggedValue());
            authorText.setText(author == null ? (tv == null ? PluginConstant.EMPTY_STRING
                    : (tv.getValue() == null ? PluginConstant.EMPTY_STRING : tv.getValue())) : author);
            // ~
            authorText.setEnabled(false);
            // lockerText.setText(lockerStr == null ? PluginConstant.EMPTY_STRING : lockerStr);
            // lockerText.setEnabled(false);
            // versionText.setText(version == null ? VersionUtils.DEFAULT_VERSION : version);
            statusCombo.setText(devStatus == null ? PluginConstant.EMPTY_STRING : devStatus);

        }
    }

    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        saveTextChange();
    }

    protected boolean saveTextChange() {
        // MOD msjian 2011-7-18 23216: when saved, and when the name of a connection is null, open an error
        if (PluginConstant.EMPTY_STRING.equals(nameText.getText().trim())) {
            MessageDialog
                    .openError(
                            null,
                            DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.error"), DefaultMessagesImpl.getString("AbstractMetadataFormPage.nameCannotBeEmpty"));//$NON-NLS-1$//$NON-NLS-2$
            nameText.setText(currentModelElement.getName());
            nameText.setFocus();
        } else {
            // MOD gdbu 2011-4-8 bug : 19976
            // nameText.setText(WorkspaceUtils.normalize(nameText.getText()));
            currentModelElement.setName(nameText.getText());
            // ~19976
        }

        MetadataHelper.setPurpose(purposeText.getText(), currentModelElement);
        MetadataHelper.setDescription(descriptionText.getText(), currentModelElement);
        MetadataHelper.setAuthor(currentModelElement, authorText.getText());
        // MetadataHelper.setVersion(versionText.getText(), currentModelElement);
        MetadataHelper.setDevStatus(currentModelElement, statusCombo.getText());

        // if (currentModelElement instanceof Connection) {
        // Property property = PropertyHelper.getProperty(currentModelElement);
        Property property = this.repositoryViewObject == null ? null : this.repositoryViewObject.getProperty();
        if (property != null) {
            property.setDisplayName(nameText.getText());
            property.setLabel(WorkspaceUtils.normalize(nameText.getText()));
            property.setPurpose(purposeText.getText());
            property.setDescription(descriptionText.getText());
            property.setStatusCode(statusCombo.getText());
            property.getAuthor().setLogin(authorText.getText());
            // property.setVersion(versionText.getText());
        }
        // }

        // ADD msjian 2011-7-18 23216: when there is no error for name, do set
        if (PluginConstant.EMPTY_STRING.equals(nameText.getText().trim())) {
            return false;
        }
        return true;
    }

    public boolean performGlobalAction(String actionId) {
        Control focusControl = getFocusControl();
        if (focusControl == null) {
            return false;
        }
        AbstractAnalysisActionHandler focusPart = getFocusSection();
        if (focusPart != null) {
            return focusPart.doGlobalAction(actionId);
        }
        return false;
    }

    protected Control getFocusControl() {
        IManagedForm form = getManagedForm();
        if (form == null) {
            return null;
        }
        Control control = form.getForm();
        if (control == null || control.isDisposed()) {
            return null;
        }
        Display display = control.getDisplay();
        Control focusControl = display.getFocusControl();
        if (focusControl == null || focusControl.isDisposed()) {
            return null;
        }
        return focusControl;
    }

    private AbstractAnalysisActionHandler getFocusSection() {
        Control focusControl = getFocusControl();
        if (focusControl == null) {
            return null;
        }
        Composite parent = focusControl.getParent();
        AbstractAnalysisActionHandler targetPart = null;
        while (parent != null) {
            Object data = parent.getData(ACTION_HANDLER);
            if (data != null && data instanceof AbstractAnalysisActionHandler) {
                targetPart = (AbstractAnalysisActionHandler) data;
                break;
            }
            parent = parent.getParent();
        }
        return targetPart;
    }

    /**
     * Sets the formTitle.
     * 
     * @param formTitle the formTitle to set
     */
    public void setFormTitle(String formTitle) {
        this.formTitle = formTitle;
    }

    /**
     * Getter for formTitle.
     * 
     * @return the formTitle
     */
    public String getFormTitle() {
        return formTitle == null ? "" : formTitle; //$NON-NLS-1$
    }

    /**
     * Sets the metadataTitle.
     * 
     * @param metadataTitle the metadataTitle to set
     */
    protected void setMetadataTitle(String metadataTitle) {
        this.metadataTitle = metadataTitle;
    }

    /**
     * Getter for metadataTitle.
     * 
     * @return the metadataTitle
     */
    protected String getMetadataTitle() {
        return metadataTitle == null ? "" : metadataTitle; //$NON-NLS-1$
    }

    public String getOldDataproviderName() {
        return this.oldDataproviderName;
    }

    public void setOldDataproviderName(String oldDataproviderName) {
        this.oldDataproviderName = oldDataproviderName;
    }

    public boolean isNameTextUpdate() {
        return modify;
    }

    /**
     * ADD yyi 2011-05-31 16158:add whitespace check for text fields.
     * 
     * @param fields
     */
    public void addWhitespaceValidate(Text... fields) {
        for (Text t : fields) {
            validateWhithspace(t);
            t.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    validateWhithspace((Text) e.widget);
                }
            });
        }
    }

    private void validateWhithspace(Text field) {
        if (field.getText().length() > 0 && PluginConstant.EMPTY_STRING.equals(field.getText().trim())) {
            getManagedForm().getMessageManager().addMessage(WHITESPACE_CHECK_MSG, WHITESPACE_CHECK_MSG, null,
                    IMessageProvider.ERROR, field);
            checkWhitespaceTextFields.add(field);
        } else {
            getManagedForm().getMessageManager().removeMessage(WHITESPACE_CHECK_MSG, field);
            checkWhitespaceTextFields.remove(field);
        }
    }

    /**
     * @return true if any text fields with validates contains whitespace.
     */
    public boolean checkWhithspace() {
        return 0 == getWhitespaceFields().size();
    }

    /**
     * @return whitespace contained fields
     */
    public Collection<Text> getWhitespaceFields() {
        return checkWhitespaceTextFields;
    }

    public abstract ReturnCode canSave();

    /**
     * 
     * check if the nameText is a dupilcate name.
     * 
     * @return
     */
    protected ReturnCode canModifyName(ERepositoryObjectType objectType) {

        String elementName = this.nameText.getText();
        Property oldProperty = null;
        if (repositoryViewObject != null) {
            oldProperty = this.repositoryViewObject.getProperty();
        }
        ReturnCode ret = new ReturnCode();
        if (oldProperty == null||objectType==null) {
            return ret;
        }
        if (PluginConstant.EMPTY_STRING.equals(elementName.trim())) {
            this.nameText.setText(oldProperty.getDisplayName());
            ret.setReturnCode(NAMECONNOTBEEMPTY, false);
            return ret;
        }
        // MOD qiongli 2012-2-14 TDQ-4539.compare the name with all items of the specified type.
        boolean exist = PropertyHelper.existDuplicateName(elementName, oldProperty.getDisplayName(), objectType);
        if (exist) {
            this.nameText.setText(oldProperty != null ? oldProperty.getDisplayName() : PluginConstant.SPACE_STRING);
            ret.setReturnCode(
                    DefaultMessagesImpl.getString("UIMessages.ItemExistsErrorWithParameter", repositoryViewObject.getLabel()),
                    false);
            return ret;
        }

        return ret;
    }
}

// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.pattern;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.model.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class PatternMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static final String SQL = "SQL";

    private Pattern pattern;

    public static final String ALL_DATABASE_TYPE = "ALL_DATABASE_TYPE";

    private Composite sectionComp;

    private Composite componentsComp;

    private List<PatternComponent> tempPatternComponents;

    private List<String> allDBTypeList;

    private List<String> remainDBTypeList;

    public PatternMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        String[] supportTypes = SupportDBUrlStore.getInstance().getDBTypes();
        String[] allDBTypes = new String[supportTypes.length + 1];
        System.arraycopy(supportTypes, 0, allDBTypes, 0, supportTypes.length);
        allDBTypes[supportTypes.length] = ALL_DATABASE_TYPE;
        allDBTypeList = new ArrayList<String>();
        allDBTypeList.addAll(Arrays.asList(allDBTypes));
        if (tempPatternComponents == null) {
            tempPatternComponents = new ArrayList<PatternComponent>();
        } else {
            tempPatternComponents.clear();
        }
        tempPatternComponents.addAll(pattern.getComponents());
        remainDBTypeList = new ArrayList<String>();
        remainDBTypeList.addAll(allDBTypeList);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        this.pattern = PatternResourceFileHelper.getInstance().findPattern(input.getFile());
        return pattern;
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();

        form.setText("Pattern Settings");
        metadataSection.setText("Pattern Metadata");
        metadataSection.setDescription("Set the properties of pattern.");
        creatPatternDefinitionSection(form, topComp);
    }

    private void creatPatternDefinitionSection(ScrolledForm form, Composite topComp) {
        Section section = createSection(form, topComp, "Pattern Definition", false, null);
        sectionComp = toolkit.createComposite(section);
        sectionComp.setLayout(new GridLayout());
        Label label = new Label(sectionComp, SWT.WRAP);
        label.setText("Add here the definition of your pattern specific to a database. "
                + " If the expression is simple enough to be used in all databases,"
                + " use the \"ALL_DATABASE_TYPE\" type enumerate.");
        componentsComp = new Composite(sectionComp, SWT.NONE);
        componentsComp.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(componentsComp);
        EList<PatternComponent> components = this.pattern.getComponents();
        for (int i = 0; i < components.size(); i++) {
            RegularExpressionImpl regularExpress = (RegularExpressionImpl) components.get(i);
            creatNewExpressLine(form, regularExpress);
        }
        createAddButton(form);

        section.setClient(sectionComp);

    }

    private void createAddButton(final ScrolledForm form) {
        final Button addButton = new Button(sectionComp, SWT.NONE);
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setToolTipText("Add");
        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.CENTER;
        labelGd.widthHint = 65;
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                remainDBTypeList.clear();
                remainDBTypeList.addAll(allDBTypeList);
                for (PatternComponent patternComponent : tempPatternComponents) {
                    String language = ((RegularExpressionImpl) patternComponent).getExpression().getLanguage();
                    language = language.equalsIgnoreCase(SQL) ? ALL_DATABASE_TYPE : language;
                    remainDBTypeList.remove(language);
                }
                if (remainDBTypeList.size() == 0) {
                    MessageDialog.openWarning(null, "Warning", "No more pattern expression type available!");
                    return;
                }
                Expression expression = CoreFactory.eINSTANCE.createExpression();
                if (remainDBTypeList.contains(ALL_DATABASE_TYPE)) {
                    expression.setLanguage(SQL);
                } else {
                    expression.setLanguage(remainDBTypeList.get(0));
                }
                RegularExpressionImpl newRegularExpress = (RegularExpressionImpl) PatternFactory.eINSTANCE
                        .createRegularExpression();
                newRegularExpress.setExpression(expression);
                creatNewExpressLine(form, newRegularExpress);
                tempPatternComponents.add(newRegularExpress);
                form.reflow(true);
                setDirty(true);
            }
        });
    }

    private void creatNewExpressLine(final ScrolledForm form, RegularExpressionImpl regularExpress) {
        final Composite expressComp = new Composite(componentsComp, SWT.NONE);
        expressComp.setLayout(new GridLayout(10, true));
        final CCombo combo = new CCombo(expressComp, SWT.BORDER);
        combo.setEditable(false);
        combo.setItems(remainDBTypeList.toArray(new String[remainDBTypeList.size()]));
        final RegularExpressionImpl finalRegExpress = regularExpress;
        String language = regularExpress.getExpression().getLanguage();
        if (language == null) {
            if (this.remainDBTypeList.contains(ALL_DATABASE_TYPE)) {
                combo.setText(ALL_DATABASE_TYPE);
            } else {
                combo.setText(remainDBTypeList.get(0));
            }
        } else {
            String supportLanguage = language.equalsIgnoreCase(SQL) ? ALL_DATABASE_TYPE : language;
            combo.setText(supportLanguage);
        }
        GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(combo);
        combo.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                String language = combo.getText().equalsIgnoreCase(ALL_DATABASE_TYPE) ? SQL : combo.getText();
                finalRegExpress.getExpression().setLanguage(language);
                setDirty(true);
            }
        });
        final Text patternText = new Text(expressComp, SWT.BORDER);
        patternText.setText(regularExpress.getExpression().getBody() == null ? PluginConstant.EMPTY_STRING : regularExpress
                .getExpression().getBody());
        GridDataFactory.fillDefaults().span(7, 1).grab(true, false).applyTo(patternText);
        patternText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                finalRegExpress.getExpression().setBody(patternText.getText());
                setDirty(true);
            }

        });
        Button delButton = new Button(expressComp, SWT.NONE);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setToolTipText("Delete");
        GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(delButton);
        delButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                tempPatternComponents.remove(finalRegExpress);
                expressComp.dispose();
                sectionComp.layout();
                form.reflow(true);
                setDirty(true);
            }
        });
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(expressComp);
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((PatternEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((PatternEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        if (savePattern()) {
            this.isDirty = false;
        }
    }

    private boolean savePattern() {
        this.pattern.getComponents().clear();
        this.pattern.getComponents().addAll(tempPatternComponents);

        // PTODO fixed bug 4296: set the Pattern is valid
        TaggedValueHelper.setValidStatus(true, pattern);

        EList<PatternComponent> components = this.pattern.getComponents();
        List<String> existLanguage = new ArrayList<String>();
        for (int i = 0; i < components.size(); i++) {
            RegularExpressionImpl regularExpress = (RegularExpressionImpl) components.get(i);
            String language = regularExpress.getExpression().getLanguage();
            if ((regularExpress.getExpression().getBody() == null) || (!regularExpress.getExpression().getBody().matches("'.*'"))) {
                MessageDialog.openWarning(null, "Warning",
                        "The pattern's expression starts and ends must has a single quote \"'\"");
                return false;
            }
            if (existLanguage.contains(language)) {
                MessageDialog.openError(null, "Error", "The language type is not unique:" + language);
                return false;
            } else {
                existLanguage.add(language);
            }
        }
        EMFUtil.saveSingleResource(pattern.eResource());
        return true;

    }
}

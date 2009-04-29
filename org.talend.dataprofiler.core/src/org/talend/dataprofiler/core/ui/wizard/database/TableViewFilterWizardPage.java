// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.database;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableViewFilterWizardPage extends AbstractWizardPage {

    private static final String MSG_FILTER_VALID = DefaultMessagesImpl.getString("TableViewColumnFilterWizardPage.filterValid");

    private static final String MSG_FILTER_INVALID = DefaultMessagesImpl
            .getString("TableViewColumnFilterWizardPage.filterInvalid");

    private TableViewFilterWizard parent;

    private Text tableFilterText;

    private Text viewFilterText;

    public Text getTableFilterText() {
        return tableFilterText;
    }

    public void setTableFilterText(Text tableFilterText) {
        this.tableFilterText = tableFilterText;
    }

    public Text getViewFilterText() {
        return viewFilterText;
    }

    public void setViewFilterText(Text viewFilterText) {
        this.viewFilterText = viewFilterText;
    }

    /**
     * DOC xqliu TableViewFilterWizardPage constructor comment.
     */
    public TableViewFilterWizardPage() {
        super();
    }

    public TableViewFilterWizardPage(TableViewFilterWizard parent) {
        this();
        this.parent = parent;

    }

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout grid = new GridLayout(2, false);
        comp.setLayout(grid);

        GridData gd = new GridData();
        gd.widthHint = 280;

        Label l1 = new Label(comp, SWT.NONE);
        l1.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.conn"));

        Label t11 = new Label(comp, SWT.BORDER);
        t11.setText(this.parent.getTdDataProvider().getName());
        t11.setLayoutData(gd);

        Label l2 = new Label(comp, SWT.NONE);
        l2.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.catalog"));

        Label t22 = new Label(comp, SWT.BORDER);
        t22.setText(this.parent.getPackageObj().getName());
        t22.setLayoutData(gd);

        Label label1 = new Label(comp, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.tableFilter"));

        tableFilterText = new Text(comp, SWT.BORDER);
        tableFilterText.setText(this.parent.getOldTableFilter());
        tableFilterText.setLayoutData(gd);

        Label label2 = new Label(comp, SWT.NONE);
        label2.setText(DefaultMessagesImpl.getString("TableViewFilterWizardPage.viewFilter"));

        viewFilterText = new Text(comp, SWT.BORDER);
        viewFilterText.setText(this.parent.getOldViewFilter());
        viewFilterText.setLayoutData(gd);

        addFieldsListeners();

        this.setControl(comp);
    }

    @Override
    public boolean checkFieldsValue() {
        String tableFilter = this.tableFilterText.getText();
        if (tableFilter.indexOf("\\") > -1 || tableFilter.indexOf("/") > -1) {
            return false;
        }
        String viewFilter = this.viewFilterText.getText();
        if (viewFilter.indexOf("\\") > -1 || viewFilter.indexOf("/") > -1) {
            return false;
        }
        return true;
    }

    private void addFieldsListeners() {
        ModifyListener modL = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (checkFieldsValue()) {
                    updateStatus(IStatus.OK, MSG_FILTER_VALID);
                } else {
                    updateStatus(IStatus.ERROR, MSG_FILTER_INVALID);
                }
            }
        };
        tableFilterText.addModifyListener(modL);
        viewFilterText.addModifyListener(modL);
    }
}

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
package org.talend.dataprofiler.core.ui.editor.composite;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.views.DataFilterDND;

/**
 * @author rli
 * 
 */
public class DataFilterComp extends AbstractPagePart {

    private final String stringDataFilter;

    private Text dataFilterText;

    public DataFilterComp(Composite parent, String stringDataFilter) {
        this.stringDataFilter = stringDataFilter;
        this.createContent(parent);
    }

    public void createContent(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        // MOD hcheng,2009-06-22,for 7803
        Label label = new Label(parent, SWT.NULL);
        label.setText("Where"); //$NON-NLS-1$
        Font boldFont = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD); //$NON-NLS-1$
        label.setFont(boldFont);
        GridData gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        label.setLayoutData(gd);
        dataFilterText = new Text(parent, SWT.BORDER | SWT.MULTI);
        DataFilterDND.installDND(dataFilterText);
        dataFilterText.setToolTipText(DefaultMessagesImpl.getString("DataFilterComp.here")); //$NON-NLS-1$
        dataFilterText.setText(stringDataFilter == null ? PluginConstant.EMPTY_STRING : stringDataFilter);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(dataFilterText);
        ((GridData) dataFilterText.getLayoutData()).heightHint = 150;
        dataFilterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                propertyChangeSupport.firePropertyChange(PluginConstant.DATAFILTER_PROPERTY, null, dataFilterText.getText());
            }

        });
    }

    public String getDataFilterString() {
        return dataFilterText.getText();
    }

    public void addModifyListener(ModifyListener listener) {
        dataFilterText.addModifyListener(listener);
    }

    @Override
    public void updateModelViewer() {
        // TODO Auto-generated method stub

    }
}

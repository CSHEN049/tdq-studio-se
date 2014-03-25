// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.table;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.RuleFolderFliter;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizardPage;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DQRuleSelectPage extends AbstractAnalysisWizardPage {

    private CheckboxTreeViewer cViewer;

    public CheckboxTreeViewer getCViewer() {
        return cViewer;
    }

    public void setCViewer(CheckboxTreeViewer viewer) {
        cViewer = viewer;
    }

    private final String pageTitle = DefaultMessagesImpl.getString("DQRuleSelectPage.newAnalysis"); //$NON-NLS-1$

    private final String pageMessage = DefaultMessagesImpl.getString("DQRuleSelectPage.selectDQRule"); //$NON-NLS-1$

    public DQRuleSelectPage() {
        setTitle(pageTitle);
        setDescription(pageMessage);
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        FillLayout layout = new FillLayout();
        container.setLayout(layout);

        cViewer = new ContainerCheckedTreeViewer(container, SWT.NONE);
        cViewer.setLabelProvider(new DQRuleLabelProvider());
        cViewer.setContentProvider(new WorkbenchContentProvider());
        cViewer.setInput(ResourceManager.getLibrariesFolder());
        // ADD mzhao bug TDQ-4188 hide the .svn folders.
        cViewer.addFilter(new DQFolderFliter(true));
        cViewer.addFilter(new RuleFolderFliter(true));
        cViewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                        return true;
                    }
                } else if (element instanceof IFolder) {
                    IFolder folder = (IFolder) element;
                    return ResourceService.isSubFolder(ResourceManager.getRulesFolder(), folder);
                }
                return false;
            }
        });

        setControl(container);
    }

    @Override
    public boolean canFlipToNextPage() {
        return false;
    }

    @Override
    public boolean isCanFinishEarly() {
        return true;
    }

    @Override
    public boolean isHasPages() {
        return false;
    }
}

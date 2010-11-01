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
package org.talend.dataprofiler.rcp.intro;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor;
import org.talend.repository.registeruser.RegisterManagement;

/**
 * DOC rli class global comment. Detailled comment <br/>
 * 
 */
@SuppressWarnings("restriction")
public class ApplicationWorkbenchAdvisor extends IDEWorkbenchAdvisor {

    private static Logger log = Logger.getLogger(ApplicationWorkbenchAdvisor.class);

    private static final String PERSPECTIVE_ID = "org.talend.dataprofiler.DataProfilingPerspective"; //$NON-NLS-1$

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        configurer.setSaveAndRestore(true);
        IPreferenceStore apiStore = PlatformUI.getPreferenceStore();
        apiStore.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);
        // PTODO qzhang close the Sql editor, when start up the RCP.
        apiStore.setValue(IWorkbenchPreferenceConstants.CLOSE_EDITORS_ON_EXIT, true);
    }

    public String getInitialWindowPerspectiveId() {
        return PERSPECTIVE_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.ide.application.IDEWorkbenchAdvisor#postStartup()
     */
    @Override
    public void postStartup() {
        super.postStartup();
        // Start Web Service Registration
        try {
            RegisterManagement.decrementTry();
        } catch (Exception e) {
            log.error("Problem during the registration wizard:" + e.getLocalizedMessage(), e); //$NON-NLS-1$
        }
    }
}

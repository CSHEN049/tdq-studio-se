package org.epic.debug.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * @author ruehl
 */
public class LaunchConfigurationDialogCGI extends
    AbstractLaunchConfigurationTabGroup
{
    public void createTabs(ILaunchConfigurationDialog dialog, String mode)
    {
        ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[]
        {
            new LaunchConfigurationMainTab(false),
            new LaunchConfigurationCGIWebServerTab(),
            new LaunchConfigurationCGIEnvTab(),
            new LaunchConfigurationCGIBrowserTab(),
            new org.eclipse.debug.ui.CommonTab()
        };
        setTabs(tabs);
    }
}

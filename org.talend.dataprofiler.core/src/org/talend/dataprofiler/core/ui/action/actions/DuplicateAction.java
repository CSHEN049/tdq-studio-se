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
package org.talend.dataprofiler.core.ui.action.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.handle.ActionHandleFactory;
import org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import common.Logger;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class DuplicateAction extends Action {

    private IRepositoryNode[] nodeArray = new IRepositoryNode[0];

    private static Logger log = Logger.getLogger(DuplicateAction.class);

    /**
     * DOC bZhou DuplicateAction constructor comment.
     */
    public DuplicateAction() {
        super(DefaultMessagesImpl.getString("DuplicateCWMResourceAction.Duplicate")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EDIT_COPY));
    }

    public DuplicateAction(IRepositoryNode[] nodeArray) {
        this();
        this.nodeArray = nodeArray;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        Object duplicateObject = null;
        for (final IRepositoryNode node : nodeArray) {
            if (node != null) {

                final IDuplicateHandle handle = ActionHandleFactory.createDuplicateHandle(node);

                if (handle != null) {
                    // MOD msjian TDQ-4672 2012-2-17: modified the check duplicate name method
                    String initLabel = generateInitialLabel(node);
                    InputDialog dialog = new InputDialog(
                            null,
                            DefaultMessagesImpl.getString("DuplicateAction.InputDialog"), DefaultMessagesImpl.getString("DuplicateAction.InpurtDesc"), initLabel, //$NON-NLS-1$ //$NON-NLS-2$
                            new IInputValidator() {

                                public String isValid(String newText) {
                                    if (PropertyHelper.existDuplicateName(newText, null, node.getContentType(), true)) {
                                        return DefaultMessagesImpl.getString("DuplicateAction.LabelExists"); //$NON-NLS-1$
                                    }

                                    return null;
                                }
                            });
                    // TDQ-4672~

                    if (dialog.open() == Window.OK) {
                        String newLabel = dialog.getValue();

                        // TDQ-4179 MOD yyin 20120313: when duplicate a user defined indicator,
                        // there are no rule for the user, only for sys, so no need to check the valid rule,just
                        // duplicate
                        if (ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS.equals(node
                                .getProperties(IRepositoryNode.EProperties.LABEL))) {
                            duplicateObject = handle.duplicate(newLabel);
                        } else {
                            // ~TDQ-4719
                            ReturnCode rc = handle.validDuplicated();
                            if (rc.isOk()) {
                                duplicateObject = handle.duplicate(newLabel);
                            } else {
                                MessageDialog.openError(null,
                                        DefaultMessagesImpl.getString("DuplicateAction.InvalidDialog"), rc.getMessage()); //$NON-NLS-1$
                            }
                        }
                    }
                }
            }
        }

        CorePlugin.getDefault().refreshWorkSpace();
        // CorePlugin.getDefault().refreshDQView();
        if (duplicateObject != null) {
            selectAndReveal(duplicateObject);
        }
    }

    /**
     * DOC bZhou Comment method "generateInitialLabel".
     * 
     * @param handle
     * @return
     */
    private String generateInitialLabel(IDuplicateHandle handle) {
        String initNameValue = "Copy_of_" + handle.getProperty().getLabel(); //$NON-NLS-1$

        if (!handle.isExistedLabel(initNameValue)) {
            return initNameValue;
        } else {
            char j = 'a';
            String temp = initNameValue;
            while (handle.isExistedLabel(temp)) {
                if (j <= 'z') {
                    temp = initNameValue + "_" + (j++) + ""; //$NON-NLS-1$ //$NON-NLS-2$
                }

            }
            return temp;
        }
    }

    /**
     * DOC msjian Comment method "generateInitialLabel".
     * 
     * check the duplicate name by node.
     * 
     * @param node
     * @return
     */
    private String generateInitialLabel(IRepositoryNode node) {
        String initNameValue = "Copy_of_" + node.getLabel(); //$NON-NLS-1$
        ERepositoryObjectType type = node.getContentType();
        if (!PropertyHelper.existDuplicateName(initNameValue, null, type, true)) {
            return initNameValue;
        } else {
            char j = 'a';
            String temp = initNameValue;

            while (PropertyHelper.existDuplicateName(temp, null, type, true)) {
                if (j <= 'z') {
                    temp = initNameValue + "_" + (j++) + ""; //$NON-NLS-1$ //$NON-NLS-2$
                }

            }
            return temp;
        }
    }

    /**
     * DOC bZhou Comment method "selectAndReveal".
     * 
     * Selects and reveals the newly added resource in all parts of the active workbench window's active page.
     * 
     * @param duplicateObject
     */
    private void selectAndReveal(Object duplicateObject) {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        IWorkbenchPart activePart = page.getActivePart();
        ModelElement modelElement = ModelElementFileFactory.getModelElement((IFile) duplicateObject);
        RepositoryNode recursiveFind = null;
        recursiveFind = getSelctionNode(duplicateObject, modelElement, recursiveFind);
        if (recursiveFind != null) {
            CorePlugin.getDefault().refreshDQView(recursiveFind.getParent());
        }
        if (activePart instanceof ISetSelectionTarget) {
            ISelection selection = new StructuredSelection(recursiveFind);
            ((ISetSelectionTarget) activePart).selectReveal(selection);
        }

    }

    private RepositoryNode getSelctionNode(Object duplicateObject, ModelElement modelElement, RepositoryNode recursiveFind) {
        if (modelElement != null) {
            recursiveFind = RepositoryNodeHelper.recursiveFind(modelElement);
            RepositoryNode parent = recursiveFind.getParent();
            CommonViewer dqCommonViewer = RepositoryNodeHelper.getDQCommonViewer();
            if (dqCommonViewer != null && !dqCommonViewer.getExpandedState(parent)) {
                dqCommonViewer.setExpandedState(parent, true);
            }

        } else {
            IPath filePath = new Path(((IFile) duplicateObject).getFullPath().removeFileExtension().toString()
                    .concat("_" + VersionUtils.DEFAULT_VERSION)) //$NON-NLS-1$
                    .addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
            Property itemProperty = PropertyHelper.getProperty(ResourceManager.getRootProject().getFile(filePath));
            Item item = itemProperty.getItem();
            String uuid = ResourceHelper.getUUID(item);
            IRepositoryNode librariesFolderNode = null;
            if (item instanceof TDQSourceFileItem) {
                librariesFolderNode = RepositoryNodeHelper.getLibrariesFolderNode(EResourceConstant.SOURCE_FILES);

            } else if (item instanceof TDQJrxmlItem) {
                librariesFolderNode = RepositoryNodeHelper.getLibrariesFolderNode(EResourceConstant.JRXML_TEMPLATE);
            }
            for (IRepositoryNode node : librariesFolderNode.getChildren()) {
                Item sourceIitem = node.getObject().getProperty().getItem();
                // MOD msjian TDQ-4830 2012-5-25: fixed NPE
                String uuid2 = ResourceHelper.getUUID(sourceIitem);
                if (uuid2 != null && uuid2.equals(uuid)) {
                    recursiveFind = (RepositoryNode) node;
                    break;
                }
                // TDQ-4830~
            }
        }
        return recursiveFind;
    }
}

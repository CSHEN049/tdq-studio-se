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
package org.talend.cwm.compare.ui.editor;

import java.io.IOException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ICompareInputLabelProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorLauncher;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class ModelElementCompareEditorLauncher implements IEditorLauncher {

    private String connectionName = ""; //$NON-NLS-1$

    private Object selectedObject = null;

    private boolean compareEachOther;

    public ModelElementCompareEditorLauncher(String connName, Object selObj, boolean ce) {
        connectionName = connName;
        selectedObject = selObj;
        compareEachOther = ce;
    }

    public void open(IPath file) {
        try {
            final EObject snapshot = ModelUtils.load(file.toFile(), new ResourceSetImpl());
            if (snapshot instanceof ComparisonResourceSnapshot) {
                CompareConfiguration comapreConfiguration = new CompareConfiguration();
                comapreConfiguration.setDefaultLabelProvider(new ICompareInputLabelProvider() {

                    public Image getAncestorImage(Object input) {
                        return null;
                    }

                    public String getAncestorLabel(Object input) {
                        return ""; //$NON-NLS-1$
                    }

                    public Image getLeftImage(Object input) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    public String getLeftLabel(Object input) {
                        String showLabel = Messages.getString("ModelElementCompareEditorLauncher.LocalStructure", connectionName); //$NON-NLS-1$
                        if (compareEachOther) {
                            showLabel = Messages.getString("ModelElementCompareEditorLauncher.FirstElement"); //$NON-NLS-1$
                        }
                        return showLabel;
                    }

                    public Image getRightImage(Object input) {
                        return null;
                    }

                    public String getRightLabel(Object input) {
                        String showLabel = Messages.getString("ModelElementCompareEditorLauncher.DistantStructure"); //$NON-NLS-1$
                        if (compareEachOther) {
                            showLabel = Messages.getString("ModelElementCompareEditorLauncher.SecondElement"); //$NON-NLS-1$
                        }
                        return showLabel;
                    }

                    public Image getImage(Object element) {
                        return null;
                    }

                    public String getText(Object element) {
                        return Messages.getString("ModelElementCompareEditorLauncher.TextLabel"); //$NON-NLS-1$
                    }

                    public void addListener(ILabelProviderListener listener) {

                    }

                    public void dispose() {

                    }

                    public boolean isLabelProperty(Object element, String property) {
                        return false;
                    }

                    public void removeListener(ILabelProviderListener listener) {
                        // TODO Auto-generated method stub

                    }

                });
                ModelElementCompareEditorInput compEditorInput = new ModelElementCompareEditorInput(
                        (ComparisonResourceSnapshot) snapshot, comapreConfiguration, selectedObject);
                // MOD mzhao bug 8581 Add the specific title for comparison
                // editor.
                String editorTitle = Messages.getString("ModelElementCompareEditorLauncher.Compare"); //$NON-NLS-1$
                if (selectedObject instanceof IFile) {
                    editorTitle = PrvResourceFileHelper.getInstance().findProvider((IFile) selectedObject).getObject().getName();
                } else if (selectedObject instanceof IFolderNode) {
                    editorTitle = ((ModelElement) ((IFolderNode) selectedObject).getParent()).getName();
                } else if (selectedObject instanceof Catalog) {
                    editorTitle = ((Catalog) selectedObject).getName();
                } else if (selectedObject instanceof IRepositoryViewObject) {
                    editorTitle = ((ConnectionItem) ((IRepositoryViewObject) selectedObject).getProperty().getItem())
                            .getConnection().getName();
                }

                compEditorInput.setTitle(editorTitle);
                CompareUI.openCompareEditor(compEditorInput);

                compEditorInput.hookLeftPanelContextMenu(compareEachOther);
                compEditorInput.hookToolBar(compareEachOther);
            }
        } catch (IOException e) {
            // Fichier non lisible
            assert false;
        }
    }

}

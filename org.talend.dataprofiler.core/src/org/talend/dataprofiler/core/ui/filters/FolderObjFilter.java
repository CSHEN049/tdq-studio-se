// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.filters;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.jface.viewers.Viewer;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC rli class global comment. Detailled comment
 */
public class FolderObjFilter extends AbstractViewerFilter {

    public static final int FILTER_ID = 3;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.filters.AbstractViewerFilter#getId()
     */
    @Override
    public int getId() {
        return FILTER_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IResource) {
            IResource res = (IResource) element;
            if (IResource.FOLDER == res.getType()) {
                IFolder folder = (IFolder) element;
                // MOD mzhao 2010-08-12 14891: use same repository API with TOS to persistent metadata
                if (((IFolder) folder).getProjectRelativePath().toString().startsWith(EResourceConstant.METADATA.getPath())) {
                    String folderName = ((IFolder) folder).getName();
                    if (folderName.equals("bin")) {
                        return false;
                    }
                    if (folderName.equals(EResourceConstant.METADATA.getPath())) {
                        return true;
                    } else if (ResourceManager.getConnectionFolder().getFullPath().isPrefixOf(folder.getFullPath())
                            || ResourceManager.getMDMConnectionFolder().getFullPath().isPrefixOf(folder.getFullPath())) {
                        return true;
                    }
                    return false;
                }

                ResourceAttributes resourceAttributes = folder.getResourceAttributes();
                if (resourceAttributes == null) {
                    return true;
                }
                if (resourceAttributes.isHidden()) {
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

}

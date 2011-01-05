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
package org.talend.dq.nodes;

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.Container;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class SourceFileFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(SourceFileFolderRepNode.class);

    /**
     * DOC klliu SourceFileFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public SourceFileFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        try {
            RootContainer<String, IRepositoryViewObject> sourceFiles = ProxyRepositoryFactory.getInstance()
                    .getTdqRepositoryViewObjects(getContentType(), RepositoryNodeHelper.getPath(this).toString());
            // sub folders
            for (Container<String, IRepositoryViewObject> container : sourceFiles.getSubContainer()) {
                Folder folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_SOURCE_FILES);
                super.getChildren().add(new SourceFileSubFolderNode(folder, this, ENodeType.SIMPLE_FOLDER));
            }
            // source files
            for (IRepositoryViewObject viewObject : sourceFiles.getMembers()) {
                if (!viewObject.isDeleted()) {
                    SourceFileRepNode repNode = new SourceFileRepNode(viewObject, this, ENodeType.REPOSITORY_ELEMENT);
                    viewObject.setRepositoryNode(repNode);
                    super.getChildren().add(repNode);
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return super.getChildren();
    }
}

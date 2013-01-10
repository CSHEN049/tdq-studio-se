// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DelimitedFileConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.record.RecordFile;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DFConnectionRepNode extends ConnectionRepNode {

    private DelimitedFileConnection dfConnection;

    public DelimitedFileConnection getDfConnection() {
        return this.dfConnection;
    }

    /**
     * DOC qiongli DelimitedFileConnectionRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DFConnectionRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        RepositoryNodeHelper.restoreCorruptedConn(object.getProperty());
        if (object != null && object.getProperty() != null) {
            Item item = object.getProperty().getItem();
            if (item != null && item instanceof DelimitedFileConnectionItem) {
                this.dfConnection = (DelimitedFileConnection) ((DelimitedFileConnectionItem) item).getConnection();
            }
        }
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        EList<Package> dataPackage = getDfConnection().getDataPackage();
        if (dataPackage != null && dataPackage.size() > 0) {
            Package pack = dataPackage.get(0);
            if (pack instanceof RecordFile) {
                return filterResultsIfAny(createRepositoryNodeSchema());
            }
        }

        return new ArrayList<IRepositoryNode>();
    }

    private List<IRepositoryNode> createRepositoryNodeSchema() {
        super.getChildren().clear();
        IRepositoryViewObject object = this.getObject();
        Set<MetadataTable> tables = ConnectionHelper.getTables(((ConnectionItem) getObject().getProperty().getItem())
                .getConnection());
        if (tables != null && tables.size() > 0) {
            for (MetadataTable mtable : tables) {
                MetadataTableRepositoryObject mtRepObject = new MetadataTableRepositoryObject(object, mtable);
                mtRepObject.setTableName(mtable.getLabel());
                mtRepObject.setLabel(mtable.getLabel());
                mtRepObject.setId(mtable.getLabel());
                DFTableRepNode tableNode = new DFTableRepNode(mtRepObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
                tableNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
                tableNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_TABLE);
                mtRepObject.setRepositoryNode(tableNode);
                super.getChildren().add(tableNode);
            }
        }
        return super.getChildren();
    }

    @Override
    public boolean canExpandForDoubleClick() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getLabel()
     */
    @Override
    public String getLabel() {
        if (getObject() == null) {
            return this.getProperties(EProperties.LABEL).toString();
        }
        return this.getObject().getLabel();
    }
}

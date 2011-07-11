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
 * DOC klliu class global comment. Detailled comment: system indicator folder repository node
 */
public class SysIndicatorFolderRepNode extends DQRepositoryNode {

    private static Logger log = Logger.getLogger(SysIndicatorFolderRepNode.class);

    /**
     * DOC klliu IndicatorFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public SysIndicatorFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        try {
            super.getChildren().clear();
            RootContainer<String, IRepositoryViewObject> tdqViewObjects = ProxyRepositoryFactory.getInstance()
                    .getTdqRepositoryViewObjects(getContentType(), RepositoryNodeHelper.getPath(this).toString());
            // sub folders
            for (Container<String, IRepositoryViewObject> container : tdqViewObjects.getSubContainer()) {
                Folder folder = new Folder((Property) container.getProperty(),
                        getSystemIndicatorFolderRepositoryType(container.getLabel()));
                if (folder.isDeleted()) {
                    continue;
                }
                SysIndicatorFolderRepNode childNodeFolder = new SysIndicatorFolderRepNode(folder, this, ENodeType.SYSTEM_FOLDER);
                childNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
                childNodeFolder.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
                super.getChildren().add(childNodeFolder);
            }
            // rule files
            for (IRepositoryViewObject viewObject : tdqViewObjects.getMembers()) {
                if (!viewObject.isDeleted()) {
                    SysIndicatorDefinitionRepNode repNode = new SysIndicatorDefinitionRepNode(viewObject, this,
                            ENodeType.REPOSITORY_ELEMENT);
                    repNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
                    repNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
                    viewObject.setRepositoryNode(repNode);
                    repNode.setSystemIndicator(true);
                    super.getChildren().add(repNode);
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        // MOD gdbu 2011-6-29 bug : 22204
        return filterResultsIfAny(super.getChildren());
        // ~22204
    }

    /**
     * DOC xqliu Comment method "getSystemIndicatorFolderRepositoryType".
     * 
     * @param label
     * @return
     */
    private ERepositoryObjectType getSystemIndicatorFolderRepositoryType(String label) {
        if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_BUSINESS_RULES).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_BUSINESS_RULES;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_CORRELATION).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_CORRELATION;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY).endsWith(
                label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_OVERVIEW).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_OVERVIEW;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_FINDER).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_FINDER;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_MATCHING).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_MATCHING;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_ROW_COMPARISON).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_ROW_COMPARISON;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_SIMPLE_STATISTICS).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SIMPLE_STATISTICS;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_SOUNDEX).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SOUNDEX;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_SUMMARY_STATISTICS)
                .endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SUMMARY_STATISTICS;
        } else if (ERepositoryObjectType.getFolderName(ERepositoryObjectType.SYSTEM_INDICATORS_TEXT_STATISTICS).endsWith(label)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_TEXT_STATISTICS;
        }
        return null;
    }

    @Override
    public String getLabel() {
        if (this.getObject() != null) {
            return this.getObject().getLabel();
        }
        return super.getLabel();
    }
}

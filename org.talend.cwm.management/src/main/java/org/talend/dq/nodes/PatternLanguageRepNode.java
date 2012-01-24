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
package org.talend.dq.nodes;

import org.talend.dataquality.PluginConstant;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC klliu class global comment. Detailled comment
 */
public class PatternLanguageRepNode extends DQRepositoryNode {

    private String label;

    public PatternLanguageRepNode(RepositoryNode parent, ENodeType type) {
        super(null, parent, type);
        this.type = type;

    }

    public void setLabel(String label) {
        this.label = label;
    }
    @Override
    public String getLabel() {
        return this.label == null ? PluginConstant.EMPTY_STRING : this.label;
    }
}

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

import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class SysIndicatorDefinitionRepNode extends RepositoryNode {

    boolean isSystemIndicator = false;

    private IndicatorDefinition indicatorDefinition;

    public IndicatorDefinition getIndicatorDefinition() {
        return this.indicatorDefinition;
    }

    /**
     * DOC klliu IndicatorDefinitionRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public SysIndicatorDefinitionRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        Property property = object.getProperty();
        if (property != null) {
            Item item = property.getItem();
            if (item != null && item instanceof TDQIndicatorDefinitionItem) {
                this.indicatorDefinition = ((TDQIndicatorDefinitionItem) item).getIndicatorDefinition();
            }
        }
    }

    public void setSystemIndicator(boolean isSystemIndicator) {
        this.isSystemIndicator = isSystemIndicator;
    }

    public boolean isSystemIndicator() {
        return this.isSystemIndicator;
    }

    @Override
    public String getLabel() {
        if (this.getIndicatorDefinition() != null) {
            return this.getIndicatorDefinition().getName();
        }
        return super.getLabel();
    }

    @Override
    public boolean canExpandForDoubleClick() {
        return false;
    }
}

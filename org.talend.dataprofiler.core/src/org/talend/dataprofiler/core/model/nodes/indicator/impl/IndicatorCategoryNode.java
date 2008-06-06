// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.model.nodes.indicator.impl;

import org.talend.dataprofiler.core.model.nodes.indicator.AbstractIndicatorNode;
import org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;

/**
 * @author rli
 * 
 */
public class IndicatorCategoryNode extends AbstractIndicatorNode {

    public IndicatorCategoryNode(IndicatorEnum indicatorEnum) {
        super(indicatorEnum);
         createChildren(indicatorEnum);
    }

    public IndicatorCategoryNode(String label, IndicatorEnum[] indicatorEnums) {
        super(null);
        this.label = label;
         this.creatChildren(indicatorEnums);
    }

    private void createChildren(IndicatorEnum indicatorEnum) {
        this.creatChildren(indicatorEnum.getChildren());

    }

    private void creatChildren(IndicatorEnum[] indicatorEnums) {
        IIndicatorNode[] childrenNodes = new IIndicatorNode[indicatorEnums.length];
        for (int i = 0; i < indicatorEnums.length; i++) {
            if (indicatorEnums[i].hasChildren()) {
                childrenNodes[i] = new IndicatorCategoryNode(indicatorEnums[i]);
//                ((IndicatorCategoryNode) childrenNodes[i]).createChildren(indicatorEnums[i]);
            } else {

                childrenNodes[i] = new IndicatorNode(indicatorEnums[i]);
            }
            childrenNodes[i].setParent(this);
        }
        this.setChildren(childrenNodes);
    }

    public String getLabel() {
        if (indicatorEnum != null) {
            return this.indicatorEnum.getLabel();
        } else {
            return label;
        }
    }

    public boolean isIndicatorEnumNode() {
        return indicatorEnum == null;
    }

    public void addChildren(IIndicatorNode node) {
        if (this.children != null) {
            IIndicatorNode[] nodes = new IIndicatorNode[this.children.length + 1];
            System.arraycopy(children, 0, nodes, 0, this.children.length);
            nodes[nodes.length - 1] = node;
            this.children = nodes;
        } else {
            this.children = new IIndicatorNode[] { node };
        }
    }

    /**
     * @param children the children to set
     */
    public void setChildren(IIndicatorNode[] children) {
        this.children = children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.indicator.IIndicatorNode#getChildren()
     */
    public IIndicatorNode[] getChildren() {
        return children;
    }

}

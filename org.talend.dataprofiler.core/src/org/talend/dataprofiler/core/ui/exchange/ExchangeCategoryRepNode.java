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
package org.talend.dataprofiler.core.ui.exchange;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ExchangeCategoryRepNode extends RepositoryNode {

    /**
     * flag for get categories from web site.
     */
    private boolean flag = false;

    /**
     * messages of expression.
     */
    private String msg = "Can't get any categories from website!";

    private final IEcosCategory ecosCategory;

    private ENodeType type;

    private String label;

    public IEcosCategory getEcosCategory() {
        return this.ecosCategory;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ENodeType getType() {
        return this.type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ExchangeCategoryRepNode(IEcosCategory ecosCategory, RepositoryNode parent, ENodeType type) {
        super(null, parent, type);
        this.type = type;
        this.ecosCategory = ecosCategory;
        if (ecosCategory != null) {
            this.setId(ecosCategory.getId());
            this.label = ecosCategory.getName();
        }
    }

    @Override
    public String getLabel() {
        return this.label == null ? this.getMsg() : this.label;
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        if (this.isFlag()) {
            List<IEcosComponent> component = this.getEcosCategory().getComponent();
            for (IEcosComponent eco : component) {
                list.add(new ExchangeComponentRepNode(eco, this, ENodeType.REPOSITORY_ELEMENT));
            }
        }
        return list;
    }
}

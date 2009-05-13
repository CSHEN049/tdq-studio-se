// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dq.CWMPlugin;
import org.talend.dq.PluginConstant;
import org.talend.dq.helper.NeedSaveDataProviderHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * @author scorreia
 * @param <COLSET> either TdTable or TdView
 */
public abstract class NamedColumnSetFolderNode<COLSET extends NamedColumnSet> extends AbstractDatabaseFolderNode {

    private static Logger log = Logger.getLogger(NamedColumnSetFolderNode.class);

    private static final boolean FILTER_FLAG = CWMPlugin.getDefault().getPluginPreferences().getBoolean(
            PluginConstant.FILTER_TABLE_VIEW_COLUMN);

    /**
     * @param name
     */
    public NamedColumnSetFolderNode(String name) {
        super(name);
    }

    protected <T extends List<COLSET>> void loadChildrenLow(orgomg.cwm.objectmodel.core.Package pack, TdCatalog catalog,
            TdSchema schema, final T columnSets) {
        assert pack != null;
        // MOD xqliu 2009-04-27 bug 6507
        if (FILTER_FLAG) {
            columnSets.addAll(getColumnSetsWithFilter(catalog, schema));
        } else {
            columnSets.addAll(getColumnSets(catalog, schema));
        }
        if (columnSets.size() > 0) {
            if (FILTER_FLAG && columnSets.size() > TaggedValueHelper.TABLE_VIEW_MAX) {
                columnSets.clear();
                this.setChildren(null);
                MessageUI.openWarning(DefaultMessagesImpl.getString("NamedColumnSetFolderNode.warnMsg",
                        TaggedValueHelper.TABLE_VIEW_MAX));
            } else {
                this.setChildren(columnSets.toArray());
            }
            return;
        } else {
            if (FILTER_FLAG) {
                this.setChildren(null);
                if (getColumnSets(catalog, schema).size() > 0) {
                    return;
                }
            }
        }
        // ~

        TdDataProvider provider = DataProviderHelper.getTdDataProvider(pack);
        if (provider == null) {
            log.warn(pack.getName());
            return;
        }
        // load from database
        loadColumnSets(catalog, schema, provider, columnSets);
        // store views in catalog or schema
        pack.getOwnedElement().addAll(columnSets);
        this.setChildren(columnSets.toArray());
        NeedSaveDataProviderHelper.register(provider.eResource().getURI().path(), provider);
    }

    /**
     * @param catalog
     * @param schema
     * @return the Tables or Views in the given catalog or schema.
     */
    protected abstract List<COLSET> getColumnSets(TdCatalog catalog, TdSchema schema);

    /**
     * @param catalog
     * @param schema
     * @return the Tables or Views in the given catalog or schema.
     */
    protected abstract List<COLSET> getColumnSetsWithFilter(TdCatalog catalog, TdSchema schema);

    /**
     * Loads columnsets (table or view) from database.
     * 
     * @param <T>
     * @param catalog
     * @param schema
     * @param provider
     * @param columnSets
     * @return
     */
    protected abstract <T extends List<COLSET>> boolean loadColumnSets(TdCatalog catalog, TdSchema schema,
            TdDataProvider provider, final T columnSets);

    /**
     * DOC xqliu Comment method "filterColumnSets". ADD xqliu 2009-05-07 bug 7234
     * 
     * @param <T>
     * @param columnSets
     * @param columnSetPattern
     */
    protected <T extends NamedColumnSet> List<T> filterColumnSets(List<T> columnSets, String columnSetPattern) {
        if (needFilter(columnSetPattern)) {
            String[] patterns = cleanPatterns(columnSetPattern.split(","));
            return filterMatchingColumnSets(columnSets, patterns);
        }
        return columnSets;
    }

    /**
     * DOC xqliu Comment method "filterMatchingColumnSets". ADD xqliu 2009-05-07 bug 7234
     * 
     * @param <T>
     * @param columnSets
     * @param patterns
     */
    private <T extends NamedColumnSet> List<T> filterMatchingColumnSets(List<T> columnSets, String[] patterns) {
        List<T> retColumnSets = new ArrayList<T>();
        int size = 0;
        for (T t : columnSets) {
            for (String pattern : patterns) {
                String regex = pattern.replaceAll("%", ".*");
                if (t.getName().matches(regex)) {
                    retColumnSets.add(t);
                    size++;
                    if (size > TaggedValueHelper.TABLE_VIEW_MAX) {
                        return retColumnSets;
                    }
                    break;
                }
            }
        }
        return retColumnSets;
    }

    /**
     * DOC xqliu Comment method "cleanPatterns". ADD xqliu 2009-05-07 bug 7234
     * 
     * @param split
     * @return
     */
    private String[] cleanPatterns(String[] split) {
        ArrayList<String> ret = new ArrayList<String>();
        for (String s : split) {
            if (s != null && !"".equals(s) && !ret.contains(s)) {
                ret.add(s);
            }
        }
        return ret.toArray(new String[ret.size()]);
    }

    /**
     * DOC xqliu Comment method "needFilter". ADD xqliu 2009-05-07 bug 7234
     * 
     * @param columnSetPattern
     * @return
     */
    private boolean needFilter(String columnSetPattern) {
        if (FILTER_FLAG) {
            if (columnSetPattern != null && !columnSetPattern.equals("")) {
                String[] patterns = cleanPatterns(columnSetPattern.split(","));
                if (patterns != null && patterns.length > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}

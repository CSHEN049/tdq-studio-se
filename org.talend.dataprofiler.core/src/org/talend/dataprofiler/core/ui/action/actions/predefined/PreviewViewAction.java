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
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import java.util.Collection;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewViewAction extends Action {

    private static Logger log = Logger.getLogger(PreviewViewAction.class);

    private TdView view;

    /**
     * DOC Zqin PreviewViewAction constructor comment.
     */
    public PreviewViewAction(TdView view) {
        super(DefaultMessagesImpl.getString("PreviewViewAction.previewView")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPLORE_IMAGE));
        this.view = view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // MOD scorreia 2008-12-12 do not switch perspective for preview
        // ChangePerspectiveAction perspectiveAction = new ChangePerspectiveAction(PluginConstant.SE_ID);
        // perspectiveAction.run();
        SQLExplorerPlugin default1 = SQLExplorerPlugin.getDefault();
        Collection<Alias> aliases = default1.getAliasManager().getAliases();
        TdDataProvider tdDataProvider = DataProviderHelper.getDataProvider(view);
        TypedReturnCode<TdProviderConnection> tdPc = DataProviderHelper.getTdProviderConnection(tdDataProvider);
        TdProviderConnection providerConnection = tdPc.getObject();
        String url = providerConnection.getConnectionString();
        for (Alias alias : aliases) {
            if (alias.getUrl().equals(url)) {
                String qualifiedName = getTableQualifiedName(tdDataProvider);
                String query = "select * from " + qualifiedName; //$NON-NLS-1$
                SQLEditorInput input = new SQLEditorInput("SQL Editor (" + SQLExplorerPlugin.getDefault().getEditorSerialNo() //$NON-NLS-1$
                        + ").sql"); //$NON-NLS-1$
                input.setUser(alias.getDefaultUser());
                try {
                    IWorkbenchPage page = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage();
                    SQLEditor editorPart = (SQLEditor) page.openEditor((IEditorInput) input, SQLEditor.class.getName());
                    editorPart.setText(query);
                    ExecSQLAction execSQLAction = new ExecSQLAction(editorPart);
                    execSQLAction.run();
                } catch (PartInitException e) {
                    log.error(e, e);
                }
            }
        }
    }

    /**
     * DOC scorreia Comment method "getQualifiedName".
     * 
     * @param tdDataProvider
     * @return
     */
    private String getTableQualifiedName(TdDataProvider tdDataProvider) {
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(tdDataProvider);
        Package catalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(view);
        if (catalogOrSchema == null) {
            return view.getName();
        }
        // else
        String catalogName = null;
        String schemaName = null;
        if (catalogOrSchema != null && RelationalPackage.eINSTANCE.getTdSchema().equals(catalogOrSchema.eClass())) {
            schemaName = catalogOrSchema.getName();
            TdCatalog parentCatalog = CatalogHelper.getParentCatalog(catalogOrSchema);
            if (parentCatalog != null) {
                catalogName = parentCatalog.getName();
            }
        } else {
            catalogName = catalogOrSchema.getName();
        }

        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (dbmsLanguage.getDbmsName().equals(SupportDBUrlType.SYBASEDEFAULTURL.getLanguage())) {
            schemaName = ColumnSetHelper.getTableOwner(view);
        }
        // ~11934

        return dbmsLanguage.toQualifiedName(catalogName, schemaName, view.getName());
    }
}

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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.i18n.DefaultMessagesImpl;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TableViewComparisonLevel extends AbstractComparisonLevel {

    protected static Logger log = Logger.getLogger(TableViewComparisonLevel.class);

    /**
     * 
     */
    private static final List<TdColumn> EMPTY_COLUMN_LIST = Collections.emptyList();

    public TableViewComparisonLevel(DBColumnFolderRepNode dbFolderNode) {
        super(dbFolderNode);
    }

    public TableViewComparisonLevel(ColumnSet columnSet) {
        super(null);
        selectedObj = columnSet;

    }

    @Override
    protected IFile createTempConnectionFile() throws ReloadCompareException {
        // MOD klliu bug 15822 201-09-30
        if (oldDataProvider != null && oldDataProvider.eIsProxy()) {
            oldDataProvider = (Connection) EObjectHelper.resolveObject(oldDataProvider);
        }
        // MOD klliu bug 16503 201-10-28 Attention,we will not use
        // PrvResourceFileHelper and instead of WorkspaceUtils
        // in the application,except migratory task
        IFile findCorrespondingFile = WorkspaceUtils.getModelElementResource(oldDataProvider);
        if (findCorrespondingFile == null) {

            throw new ReloadCompareException(DefaultMessagesImpl.getString(
                    "TableViewComparisonLevel.NotFindFileOfDataprovider", oldDataProvider.getName())); //$NON-NLS-1$
        }
        IFile tempConnectionFile = DQStructureComparer.copyedToDestinationFile(findCorrespondingFile,
                DQStructureComparer.getTempRefreshFile());

        URI uri = URI.createPlatformResourceURI(tempConnectionFile.getFullPath().toString(), false);
        Resource resource = EMFSharedResources.getInstance().getResource(uri, true);
        Collection<Connection> tdDataProviders = ConnectionHelper.getTdDataProviders(resource.getContents());

        if (tdDataProviders.isEmpty()) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("TableViewComparisonLevel.NoDataProviderFound", //$NON-NLS-1$
                    tempConnectionFile.getLocation().toFile().getAbsolutePath()));
        }
        if (tdDataProviders.size() > 1) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString(
                    "TableViewComparisonLevel.TooManyDataProviderInFile", tdDataProviders.size(), //$NON-NLS-1$
                    tempConnectionFile.getLocation().toFile().getAbsolutePath()));
        }
        tempReloadProvider = tdDataProviders.iterator().next();
        return tempConnectionFile;
    }

    @Override
    protected boolean compareWithReloadObject() throws ReloadCompareException {

        // add option for ignoring some elements
        MatchModel match = null;
        try {
            DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
            match = MatchService.doContentMatch(columnFolderRepNode.getColumnSet(), getSavedReloadObject(), options);
        } catch (InterruptedException e) {
            log.error(e, e);
            return false;
        }
        DiffModel diff = null;
        try {
            diff = DiffService.doDiff(match, false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            handleSubDiffElement(de);
        }
        return true;
    }

    private void handleSubDiffElement(DiffElement de) {
        if (de.getSubDiffElements().size() > 0) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleSubDiffElement(difElement);
            }

        } else {
            handleDiffPackageElement(de);
        }
    }

    @Override
    protected Connection findDataProvider() {
        Connection provider = null;
        if (selectedObj instanceof DBColumnFolderRepNode) {
            DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
            ColumnSet columnSet = columnFolderRepNode.getColumnSet();
            if (columnSet != null) {
                Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
                provider = ConnectionHelper.getTdDataProvider(parentCatalogOrSchema);
            }
        } else if (selectedObj instanceof RepositoryNode) {
            Item connItem = ((RepositoryNode) selectedObj).getObject().getProperty().getItem();
            provider = ((ConnectionItem) connItem).getConnection();
        } else {
            ColumnSet columnSet = (ColumnSet) selectedObj;
            Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
            provider = ConnectionHelper.getTdDataProvider(parentCatalogOrSchema);
        }
        return provider;
    }

    @Override
    public Connection reloadCurrentLevelElement() throws ReloadCompareException {
        if (selectedObj instanceof DBColumnFolderRepNode) {
            DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
            columnFolderRepNode.setReload(true);
        }
        return super.reloadCurrentLevelElement();
    }

    @Override
    protected EObject getSavedReloadObject() throws ReloadCompareException {
        // ColumnSet selectedColumnSet = (ColumnSet) selectedObj;
        ColumnSet selectedColumnSet = null;
        if (selectedObj instanceof DBColumnFolderRepNode) {
            DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
            selectedColumnSet = columnFolderRepNode.getColumnSet();
        } else if (selectedObj instanceof ColumnSet) {
            selectedColumnSet = (ColumnSet) selectedObj;
        }
        ColumnSet toReloadcolumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        // MOD scorreia 2009-01-29 clear content of findMatchedColumnSet
        ColumnSetHelper.setColumns(toReloadcolumnSet, EMPTY_COLUMN_LIST);
        toReloadcolumnSet.getOwnedElement().clear();
        try {
            DqRepositoryViewService.getColumns(tempReloadProvider, toReloadcolumnSet, null, true);
            // MOD mzhao 2009-11-12 save to resoure after reload.
            util.saveResource(toReloadcolumnSet.eResource());
        } catch (Exception e1) {
            throw new ReloadCompareException(e1);
        }
        // MOD scorreia 2009-01-29 columns are stored in the table
        // ColumnSetHelper.setColumns(toReloadcolumnSet, columns);
        return toReloadcolumnSet;
    }

    @Override
    protected Resource getLeftResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = null;
        if (selectedObj instanceof DBColumnFolderRepNode) {
            DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
            selectedColumnSet = columnFolderRepNode.getColumnSet();
        } else if (selectedObj instanceof RepositoryNode) {
            selectedColumnSet = (ColumnSet) RepositoryNodeHelper.getMetadataElement((RepositoryNode) selectedObj);
        } else {
            selectedColumnSet = (ColumnSet) selectedObj;
        }
        ColumnSet findMatchedColumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, copyedDataProvider);
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        columnList.addAll(ColumnSetHelper.getColumns(findMatchedColumnSet));
        Resource leftResource = copyedDataProvider.eResource();
        leftResource.getContents().clear();
        for (TdColumn column : columnList) {
            DQStructureComparer.clearSubNode(column);
            leftResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(leftResource);
        return upperCaseResource(leftResource);
    }

    @Override
    protected Resource getRightResource() throws ReloadCompareException {
        ColumnSet selectedColumnSet = null;
        if (selectedObj instanceof RepositoryNode) {
            selectedColumnSet = (ColumnSet) RepositoryNodeHelper.getMetadataElement((RepositoryNode) selectedObj);
        } else if (selectedObj instanceof DBColumnFolderRepNode) {
            DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
            selectedColumnSet = columnFolderRepNode.getColumnSet();
        } else {
            selectedColumnSet = (ColumnSet) selectedObj;
        }
        ColumnSet findMatchedColumnSet = DQStructureComparer.findMatchedColumnSet(selectedColumnSet, tempReloadProvider);
        List<TdColumn> columns = null;
        try {
            // MOD scorreia 2009-01-29 clear content of findMatchedColumnSet
            ColumnSetHelper.setColumns(findMatchedColumnSet, EMPTY_COLUMN_LIST);
            columns = DqRepositoryViewService.getColumns(tempReloadProvider, findMatchedColumnSet, null, true);
        } catch (Exception e1) {
            throw new ReloadCompareException(e1);
        }
        // MOD scorreia 2009-01-29 columns are stored in the table
        // ColumnSetHelper.addColumns(findMatchedColumnSet, columns);

        URI uri = tempReloadProvider.eResource().getURI();
        Resource rightResource = null;
        rightResource = EMFSharedResources.getInstance().getResource(uri, true);
        if (rightResource == null) {
            throw new ReloadCompareException(DefaultMessagesImpl.getString("TableViewComparisonLevel.NoFactoryFoundForURI", uri));
            //$NON-NLS-1$
        }
        rightResource.getContents().clear();
        for (TdColumn column : columns) {
            DQStructureComparer.clearSubNode(column);
            rightResource.getContents().add(column);
        }
        EMFSharedResources.getInstance().saveResource(rightResource);
        return upperCaseResource(rightResource);
    }

    @Override
    protected void handleAddElement(ModelElementChangeRightTarget addElement) {
        EObject rightElement = addElement.getRightElement();
        TdColumn columnSetSwitch = SwitchHelpers.COLUMN_SWITCH.doSwitch(rightElement);
        if (columnSetSwitch != null) {
            // ColumnSet columnSet = (ColumnSet) selectedObj;
            ColumnSet columnSet = null;
            if (selectedObj instanceof DBColumnFolderRepNode) {
                DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
                columnSet = columnFolderRepNode.getColumnSet();
            } else if (selectedObj instanceof ColumnSet) {
                columnSet = (ColumnSet) selectedObj;
            }
            ColumnSetHelper.addColumn(columnSetSwitch, columnSet);
            // MOD zshen 2010.06.10 for feature 12842.
            // Case of pk

            PrimaryKey primaryKey = ColumnHelper.getPrimaryKey(columnSetSwitch);
            if (primaryKey != null) {
                TableHelper.addPrimaryKey((TdTable) columnSet, columnSetSwitch);
            }
            Set<ForeignKey> foreignKeySet = ColumnHelper.getForeignKey(columnSetSwitch);
            for (ForeignKey foreignKey : foreignKeySet) {
                if (foreignKey != null) {
                    TableHelper.addForeignKey((TdTable) columnSet, foreignKey, columnSetSwitch);
                }
            }
            return;
        }
        // MOD handle default value for a column 13411
        // MOD mzhao 13411, handle default value changes (TdExpression)
        TdExpression addedExpression = DataqualitySwitchHelper.TDEXPRESSION_SWITCH.doSwitch(rightElement);
        if (addedExpression != null) {
            TdColumn parentColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(addElement.getLeftParent());
            if (parentColumn != null) {
                parentColumn.setInitialValue(addedExpression);
            }
        }
    }

    @Override
    protected void handleRemoveElement(ModelElementChangeLeftTarget removeElement) {
        // MOD mzhao 13411, handle column changes 2010-08-23
        TdColumn removeColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumn != null) {
            ColumnSet columnSet = null;
            if (selectedObj instanceof DBColumnFolderRepNode) {
                DBColumnFolderRepNode columnFolderRepNode = (DBColumnFolderRepNode) selectedObj;
                columnSet = columnFolderRepNode.getColumnSet();
            } else if (selectedObj instanceof ColumnSet) {
                columnSet = (ColumnSet) selectedObj;
            }

            popRemoveElementConfirm();
            ColumnSetHelper.removeColumn(removeColumn, columnSet);
            return;
        }
        // MOD mzhao 13411, handle default value changes (TdExpression)
        TdExpression removedExpression = DataqualitySwitchHelper.TDEXPRESSION_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removedExpression != null) {
            TdColumn expressionOwner = SwitchHelpers.COLUMN_SWITCH.doSwitch(removedExpression.eContainer());
            if (expressionOwner != null) {
                expressionOwner.setInitialValue(null);
            }
        }
    }
}

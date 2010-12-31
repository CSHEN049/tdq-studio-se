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
package org.talend.dataprofiler.core.ui.views.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.recycle.IRecycleBin;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dq.analysis.ColumnDependencyAnalysisHandler;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class DQRepositoryViewLabelProvider extends AdapterFactoryLabelProvider {

    public DQRepositoryViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Image getImage(Object element) {

        if (element instanceof IFolderNode) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        } else if (element instanceof TdColumn) {
            if (ColumnHelper.isPrimaryKey((TdColumn) element)) {
                // get the icon for primary key
                return ImageLib.getImage(ImageLib.PK_COLUMN);
            }
        } else if (element instanceof IEcosComponent) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (element instanceof IEcosCategory) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (element instanceof IndicatorCategory) {
            return ImageLib.getImage(ImageLib.IND_CATEGORY);
        } else if (element instanceof IndicatorDefinition) {
            return ImageLib.getImage(ImageLib.IND_DEFINITION);
        } else if (element instanceof TdView) {
            return ImageLib.getImage(ImageLib.VIEW);
        } else if (element instanceof TdXmlSchema) {
            return ImageLib.getImage(ImageLib.XML_DOC);
        } else if (element instanceof TdXmlElementType) {
            return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        } else if (element instanceof IRecycleBin) {
            return ImageLib.getImage(ImageLib.RECYCLEBIN_EMPTY);
        } else
        // MOD qiongli
        if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            Object obj = rbn.getObject();
            // MOD qiongli 2010-10-8,bug 15674
            if (obj instanceof Property) {
                Property property = (Property) obj;
                Item item = property.getItem();
                if (item instanceof TDQAnalysisItem) {
                    IFile file = PropertyHelper.getItemFile(property);
                    Analysis analysis = AnaResourceFileHelper.getInstance().findAnalysis(file);
                    ColumnDependencyAnalysisHandler analysisHandler = new ColumnDependencyAnalysisHandler();
                    analysisHandler.setAnalysis(analysis);
                    if (analysisHandler.getResultMetadata().getExecutionNumber() != 0) {
                        if (!analysisHandler.getResultMetadata().isLastRunOk()) {
                            return ImageLib.createErrorIcon(ImageLib.ANALYSIS_OBJECT).createImage();
                        } else if (analysisHandler.getResultMetadata().isOutThreshold()) {
                            return ImageLib.createInvalidIcon(ImageLib.ANALYSIS_OBJECT).createImage();
                        }
                    }
                    return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                } else if (item instanceof TDQReportItem) {
                    return ImageLib.getImage(ImageLib.REPORT_OBJECT);
                } else if (item instanceof TDQPatternItem) {
                    return ImageLib.getImage(ImageLib.PATTERN_REG);
                } else if (item instanceof TDQBusinessRuleItem) {
                    return ImageLib.getImage(ImageLib.DQ_RULE);
                } else if (item instanceof TDQIndicatorDefinitionItem) {
                    return ImageLib.getImage(ImageLib.IND_DEFINITION);
                } else if (item instanceof MDMConnectionItem) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (item instanceof ConnectionItem) {
                    return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                }
            } else if (obj instanceof IFolder) {
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            }
        } else if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            IRepositoryViewObject viewObject = node.getObject();
            ENodeType type = node.getType();
            if (element instanceof RecycleBinRepNode) {
                return ImageLib.getImage(ImageLib.RECYCLEBIN_EMPTY);
            } else if (type.equals(ENodeType.SYSTEM_FOLDER)) {
                if (viewObject.getLabel().equals(EResourceConstant.DATA_PROFILING.getName())) {
                    return ImageLib.getImage(ImageLib.DATA_PROFILING);
                } else if (viewObject.getLabel().equals(EResourceConstant.METADATA.getName())) {
                    return ImageLib.getImage(ImageLib.METADATA);
                } else if (viewObject.getLabel().equals(EResourceConstant.DB_CONNECTIONS.getName())) {
                    return ImageLib.getImage(ImageLib.CONNECTION);
                } else if (viewObject.getLabel().equals(EResourceConstant.MDM_CONNECTIONS.getName())) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (viewObject.getLabel().equals(EResourceConstant.LIBRARIES.getName())) {
                    return ImageLib.getImage(ImageLib.LIBRARIES);
                } else if (viewObject.getLabel().equals(EResourceConstant.EXCHANGE.getName())) {
                    return ImageLib.getImage(ImageLib.EXCHANGE);
                }
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            } else if (type.equals(ENodeType.SIMPLE_FOLDER)) {
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            } else if (type.equals(ENodeType.REPOSITORY_ELEMENT)) {
                Item item = viewObject.getProperty().getItem();
                if (item instanceof DatabaseConnectionItem) {
                    return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
                } else if (item instanceof MDMConnectionItem) {
                    return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                } else if (item instanceof TDQAnalysisItem) {
                    return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                } else if (item instanceof TDQReportItem) {
                    return ImageLib.getImage(ImageLib.REPORT_OBJECT);
                } else if (item instanceof TDQIndicatorDefinitionItem) {
                    return ImageLib.getImage(ImageLib.IND_DEFINITION);
                } else if (item instanceof TDQPatternItem) {
                    return ImageLib.getImage(ImageLib.PATTERN_REG);
                } else if (item instanceof TDQBusinessRuleItem) {
                    return ImageLib.getImage(ImageLib.DQ_RULE);
                }
            } else if (type.equals(ENodeType.TDQ_REPOSITORY_ELEMENT)) {
                if (node instanceof DBCatalogRepNode) {
                    return ImageLib.getImage(ImageLib.CATALOG);
                } else if (node instanceof DBSchemaRepNode) {
                    return ImageLib.getImage(ImageLib.SCHEMA);
                } else if (node instanceof DBTableFolderRepNode) {
                    return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                } else if (node instanceof DBViewFolderRepNode) {
                    return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                } else if (node instanceof DBTableRepNode) {
                    return ImageLib.getImage(ImageLib.TABLE);
                } else if (node instanceof DBViewRepNode) {
                    return ImageLib.getImage(ImageLib.VIEW);
                } else if (node instanceof DBColumnRepNode) {
                    return ImageLib.getImage(ImageLib.TD_COLUMN);
                } else if (node instanceof MDMSchemaRepNode) {
                    return ImageLib.getImage(ImageLib.XML_DOC);
                } else if (node instanceof MDMXmlElementRepNode) {
                    return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
                } else if (node instanceof DBColumnFolderRepNode) {
                    return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
                }
            }
        } else if (element instanceof MDMConnection) {
            return ImageLib.getImage(ImageLib.MDM_CONNECTION);
        }
        // ~

        return super.getImage(element);
    }

    public String getText(Object element) {
        String tableOwner = null;
        if (element instanceof ModelElement) {
            tableOwner = ColumnSetHelper.getTableOwner((ModelElement) element);
        }
        // if (element instanceof AbstractFolderNode) {
        // if (((IFolderNode) element).getChildren() != null) {
        //                return ((IFolderNode) element).getName() + "(" + ((IFolderNode) element).getChildren().length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        // }
        //
        // return ((IFolderNode) element).getName();
        // } else if (element instanceof IEcosComponent) {
        // return ((IEcosComponent) element).getName();
        // } else if (element instanceof IEcosCategory) {
        // return ((IEcosCategory) element).getName();
        // } else if (element instanceof IndicatorDefinition) {
        // return ((IndicatorDefinition) element).getName();
        // } else if (element instanceof IndicatorCategory) {
        // return ((IndicatorCategory) element).getName();
        // } else if (element instanceof IRecycleBin) {
        // return ((IRecycleBin) element).getName();
        // }
        //
        // // PTODO qzhang fixed bug 4176: Display expressions as children of the
        // // patterns
        // if (element instanceof RegularExpression) {
        // RegularExpression regExp = (RegularExpression) element;
        // return regExp.getExpression().getLanguage();
        // } else if (element instanceof Connection) {
        // return ((Connection) element).getName();
        // }
        //
        // // MOD mzhao feature 10238
        // if (element instanceof TdXmlSchema) {
        // return ((TdXmlSchema) element).getName();
        // } else if (element instanceof TdXmlElementType) {
        // String elemLabe = ((TdXmlElementType) element).getName();
        // String elementType = ((TdXmlElementType) element).getJavaType();
        // if (elementType != null && !StringUtils.isEmpty(elementType)) {
        // elemLabe += " (" + elementType + ")";
        // }
        // return elemLabe;
        // } else if ((element instanceof TdTable || element instanceof TdView) && tableOwner != null &&
        // !"".equals(tableOwner)) {
        // return super.getText(element) + "(" + tableOwner + ")";
        // } else
        // // MOD qiongli :get the name of recycle bin's child
        // if (element instanceof DQRecycleBinNode) {
        // DQRecycleBinNode rbn = (DQRecycleBinNode) element;
        // Object obj = rbn.getObject();
        // // MOD qiongli 2010-8-10,bug 15674
        // if (obj instanceof Property) {
        // Property property = (Property) obj;
        // Item item = property.getItem();
        // if (item instanceof ConnectionItem) {
        // Connection connection = ((ConnectionItem) item).getConnection();
        // if (connection.eIsProxy()) {
        // connection = (Connection) EObjectHelper.resolveObject(connection);
        // }
        // return connection.getName();
        // }
        // return property.getLabel();
        // } else if (obj instanceof IFolder) {
        // return ((IFolder) obj).getName();
        // }
        //
        // } else
        if (element instanceof IRepositoryNode) {
            IRepositoryNode node = (IRepositoryNode) element;
            if (node instanceof RecycleBinRepNode) {
                return node.getLabel();
            } else if (node instanceof DBTableFolderRepNode) {
                return ((DBTableFolderRepNode) node).getNodeName();
            } else if (node instanceof DBViewFolderRepNode) {
                return ((DBViewFolderRepNode) node).getNodeName();
            } else if (node instanceof DBColumnFolderRepNode) {
                return ((DBColumnFolderRepNode) node).getNodeName();
            }
            return node.getObject().getLabel();
        }
        String text = super.getText(element);
        return "".equals(text) ? DefaultMessagesImpl.getString("DQRepositoryViewLabelProvider.noName") : text;
    }
}

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
package org.talend.dq.indicators.definitions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.CwmResource;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dataquality.indicators.util.IndicatorsSwitch;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * This class contains the singleton instance for the default indicator' definitions.
 */
public final class DefinitionHandler {

    /**
     * The label of the Regular Expression Matching indicator definition.
     */
    public static final String REGULAR_EXPRESSION_MATCHING = "Regular Expression Matching"; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(DefinitionHandler.class);

    private static DefinitionHandler instance;

    private static final String DQ_RULE_CATEGORY = "_8i9eQBI5Ed6TWL6NwMMHzQ"; //$NON-NLS-1$

    private static final String USER_DEFINED_COUNT_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_COUNT_CATEGORY;

    private static final String USER_DEFINED_FREQUENCY_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_FREQUENCY_CATEGORY;

    private static final String USER_DEFINED_MATCH_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_MATCH_CATEGORY;

    private static final String USER_DEFINED_REAL_VALUE_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_REAL_VALUE_CATEGORY;

    private static final String USER_DEFINED_COMPARISON_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_COMPARISON_CATEGORY;

    private static final String USER_DEFINED_NOMINAL_CORRELATION_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_NOMINAL_CORRELATION_CATEGORY;

    private static final String USER_DEFINED_INTERVAL_CORRELATION_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_INTERVAL_CORRELATION_CATEGORY;

    private static final String USER_DEFINED_TIME_CORRELATION_CATEGORY = IndicatorCategoryHelper.USER_DEFINED_TIME_CORRELATION_CATEGORY;

    private static final String DQ_RULE_DEFINITION = "_UUIyoCOMEd6YB57jaCfKaA"; //$NON-NLS-1$

    private static final String FD_RULE_DEFINITION = "_YqcX0XHpEd6udst2R2sgpA"; //$NON-NLS-1$

    private static final String REGULAR_EXPRESSION_MATCHING_DEFINITION = "_yb-_8Dh8Ed2XmO7pl5Yuyg"; //$NON-NLS-1$ 

    private static Map<String, IndicatorCategory> userDefinedIndicatorCategoryMap;

    private List<IndicatorDefinition> indicatorDefinitions = new ArrayList<IndicatorDefinition>();

    private EList<IndicatorCategory> indicatorCategories;

    /**
     * plugin relative path to the default file.
     */
    public static final String FILENAME = ".Talend." + FactoriesUtil.DEFINITION; //$NON-NLS-1$

    private static final String PLUGIN_PATH = "/org.talend.dataquality/" + FILENAME; //$NON-NLS-1$

    public static DefinitionHandler getInstance() {
        if (instance == null) {
            instance = new DefinitionHandler();
            // try to copy in workspace
            if (!getTalendDefinitionFile().exists()) {
                instance.copyDefinitionsIntoFolder(ResourceManager.getLibrariesFolder());
            }
            // MOD mzhao feature 13676.
            instance.initializeDefinitions();
        }
        return instance;
    }

    private DefinitionHandler() {

    }

    private void initializeDefinitions() {
        this.indicatorDefinitions.clear();
        this.indicatorCategories = loadDefinitionsFromFile().getCategories();
        indicatorDefinitions.addAll((List<IndicatorDefinition>) IndicatorResourceFileHelper.getInstance().getAllElement());
    }

    /**
     * 
     * DOC mzhao feature 13676 split system indicators.
     * 
     * @return
     */
    public IndicatorsDefinitions loadDefinitionsFromFile() {

        Resource definitionsFile = getDefCategoryResourceFromFile();

        EList<EObject> contents = definitionsFile.getContents();
        if (contents == null) {
            log.error("No content found in given resource: " + definitionsFile.getURI());
            return null;
        }
        if (contents.isEmpty()) {
            log.error("No content found in given resource: " + definitionsFile.getURI());
            return null;
        }
        DefinitionSwitch<IndicatorsDefinitions> catSwitch = new DefinitionSwitch<IndicatorsDefinitions>() {

            @Override
            public IndicatorsDefinitions caseIndicatorsDefinitions(IndicatorsDefinitions object) {
                return object;
            }

        };
        return catSwitch.doSwitch(contents.get(0));
    }

    /**
     * 
     * DOC mzhao Get definition category files from file.
     * 
     * @return
     */
    private Resource getDefCategoryResourceFromFile() {
        // MOD scorreia 2008-08-04 use EMFUtil instead of EMFSharedResources
        // because this file does not need to be saved
        // with the other files. Moreover, we need to be able to edit it when
        // needed (with default ".definition" editor
        // for development purposes)
        Resource definitionsFile = null;

        IPath definitionPath = ResourceManager.getLibrariesFolder().getFullPath().append(FILENAME);
        URI uri = URI.createPlatformResourceURI(definitionPath.toString(), false);
        try { // load from workspace path
              // do not create it here if it does not exist.
            definitionsFile = EMFSharedResources.getInstance().getResource(uri, true);
            if (log.isDebugEnabled()) {
                log.debug("Definition of indicators loaded from " + uri);
            }
        } catch (RuntimeException e) {
            if (log.isDebugEnabled()) {
                log.debug("ERROR: " + e.getMessage(), e);
            }
        }
        if (definitionsFile == null) {
            uri = URI.createPlatformPluginURI(PLUGIN_PATH, false);
            try { // load from plugin path
                definitionsFile = EMFSharedResources.getInstance().getResource(uri, true);
                if (log.isDebugEnabled()) {
                    log.debug("Definition of indicators loaded from " + uri);
                }
            } catch (RuntimeException e) {
                if (log.isDebugEnabled()) {
                    log.debug("ERROR: " + e.getMessage(), e);
                }
            }
        }

        if (definitionsFile == null) {
            // try to load from a local file
            definitionsFile = EMFSharedResources.getInstance().getResource(
                    URI.createFileURI(".." + File.separator + PLUGIN_PATH), true);
        }
        if (definitionsFile == null) {
            log.error("No resource found at " + PLUGIN_PATH + " URI= " + uri);
            return null;
        }
        return definitionsFile;
    }

    /**
     * Method "getIndicatorsDefinitions".
     * 
     * @return the singleton analysis categories (or throws an exception if a problem occured)
     */
    public List<IndicatorDefinition> getIndicatorsDefinitions() {
        if (indicatorDefinitions == null || indicatorDefinitions.isEmpty()) {
            initializeDefinitions();
        }
        if (indicatorDefinitions == null) {
            throw new RuntimeException(Messages.getString("DefinitionHandler.IndicatorsDefinition")); //$NON-NLS-1$
        }
        return indicatorDefinitions;
    }

    /**
     * 
     * DOC klliu Comment method "reloadIndicatorsDefinitions".
     */
    public void reloadIndicatorsDefinitions() {
        initializeDefinitions();
    }

    /**
     * DOC bZhou Comment method "copyDefinitionsIntoFolder".
     * 
     * @param ifolder
     * @return
     */
    public Resource copyDefinitionsIntoFolder(IFolder ifolder) {
        URI destinationUri = URI.createPlatformResourceURI(ifolder.getFullPath().toString(), false);
        return copyDefinitionsIntoFolder(destinationUri);
    }

    /**
     * DOC bZhou Comment method "copyDefinitionsIntoFolder".
     * 
     * @param destinationUri
     * @return
     */
    public Resource copyDefinitionsIntoFolder(URI destinationUri) {
        // MOD mzhao feature 13676,Reload from original place of .talend.definition file. 2010-07-09
        URI uri = URI.createPlatformPluginURI(PLUGIN_PATH, false);
        Resource resource = EMFSharedResources.getInstance().getResource(uri, true);
        EMFUtil.changeUri(resource, destinationUri);

        if (EMFSharedResources.getInstance().saveResource(resource)) {
            if (log.isInfoEnabled()) {
                log.info("Indicator default definitions correctly saved in " + resource.getURI());
            }
        } else {
            log.error("Failed to save default indicator definitions in " + resource.getURI());

        }
        return resource;
    }

    /**
     * Method "setDefaultIndicatorDefinition" sets the indicator's default definition.
     * 
     * @param indicator the indicator
     * @return true when set, false when not set.
     */
    public boolean setDefaultIndicatorDefinition(Indicator indicator) {
        return indicatorSwitch.doSwitch(indicator);
    }

    /**
     * 
     * DOC mzhao Get indicator definition by label.
     * 
     * @param label
     * @return
     */
    public IndicatorDefinition getIndicatorDefinition(String label) {
        for (IndicatorDefinition indicatorDefinition : indicatorDefinitions) {
            if (indicatorDefinition != null && indicatorDefinition.getLabel() != null
                    && indicatorDefinition.getLabel().compareTo(label) == 0) {
                return indicatorDefinition;
            }
        }
        return null;
    }

    public boolean updateRegex(String dbmsName, String regexpFunction) {
        boolean ok = true;
        boolean replaced = false;
        IndicatorDefinition regexIndDef = this.getIndicatorDefinition(REGULAR_EXPRESSION_MATCHING);
        EList<TdExpression> sqlGenericExpression = regexIndDef.getSqlGenericExpression();

        for (Expression expression : sqlGenericExpression) {
            // MOD qiongli 2011-4-18,bug 16723.data cleansing.
            if (DbmsLanguageFactory.compareDbmsLanguage(dbmsName, expression.getLanguage())) {
                replaced = replaceBodyWith(expression, regexpFunction);
            }
        }
        if (!replaced) {
            // add new expression
            String genericSQL = getGenericSQL(dbmsName, regexpFunction);
            TdExpression createdExpression = BooleanExpressionHelper.createTdExpression(dbmsName, genericSQL);
            sqlGenericExpression.add(createdExpression);
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "getGenericSQL".
     * 
     * @param dbmsName
     * @param regexpFunction
     * @return
     */
    private String getGenericSQL(String dbmsName, String regexpFunction) {
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dbmsName);
        return dbmsLanguage.createGenericSqlWithRegexFunction(regexpFunction);
    }

    /**
     * DOC scorreia Comment method "replaceBodyWith".
     * 
     * @param expression
     * @param regexpFunction
     * @return
     */
    private boolean replaceBodyWith(Expression expression, String regexpFunction) {
        expression.setBody(this.getGenericSQL(expression.getLanguage(), regexpFunction));
        return true;
    }

    /**
     * Note: scorreia. All indicator definitions defined in .Talend.definition file must be implemented here.
     * 
     * WARNING: The label of the indicator definition in .Talend.definition must be exactly the same as the strings used
     * here.
     */
    private final IndicatorsSwitch<Boolean> indicatorSwitch = new IndcatorSwitchImp(); // EOC

    // IndicatorsSwitch

    /**
     * Method "getDQRuleIndicatorCategory".
     * 
     * @return the category of the DQ Rule indicators
     */
    public IndicatorCategory getDQRuleIndicatorCategory() {
        return getIndicatorCategory(DQ_RULE_CATEGORY);
    }

    /**
     * Method "getDQRuleDefaultIndicatorDefinition".
     * 
     * @return the default indicator definition of the DQ rule.
     */
    public IndicatorDefinition getDQRuleDefaultIndicatorDefinition() {
        return getDefinitionById(DQ_RULE_DEFINITION);
    }

    public IndicatorDefinition getFDRuleDefaultIndicatorDefinition() {
        return getDefinitionById(FD_RULE_DEFINITION);
    }

    /**
     * 
     * DOC mzhao Get indicator definition by definition id (xmi id).
     * 
     * @param definitionId
     * @return
     */
    public IndicatorDefinition getDefinitionById(String definitionId) {
        for (IndicatorDefinition indDef : indicatorDefinitions) {
            CwmResource resource = (CwmResource) indDef.eResource();
            EObject object = resource.getEObject(definitionId);
            if (object != null && DefinitionPackage.eINSTANCE.getIndicatorDefinition().equals(object.eClass())) {
                return (IndicatorDefinition) object;
            }
        }
        return null;
    }

    public IndicatorCategory getUserDefinedCountIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_COUNT_CATEGORY);
    }

    public IndicatorCategory getUserDefinedMatchIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_MATCH_CATEGORY);
    }

    public IndicatorCategory getUserDefinedFrequencyIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_FREQUENCY_CATEGORY);
    }

    public IndicatorCategory getUserDefinedRealValueIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_REAL_VALUE_CATEGORY);
    }

    public IndicatorCategory getUserDefinedComparisonIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_COMPARISON_CATEGORY);
    }

    public IndicatorCategory getUserDefinedNominalCorrelationIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_NOMINAL_CORRELATION_CATEGORY);
    }

    public IndicatorCategory getUserDefinedIntervalCorrelationIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_INTERVAL_CORRELATION_CATEGORY);
    }

    public IndicatorCategory getUserDefinedTimeCorrelationIndicatorCategory() {
        return getIndicatorCategory(USER_DEFINED_TIME_CORRELATION_CATEGORY);
    }

    private IndicatorCategory getIndicatorCategory(String categoryId) {
        // MOD by zshen for feature 18724 if it is proxy then reload the definitions
        if (indicatorCategories.size() > 0 && indicatorCategories.get(0).eIsProxy()) {
            this.initializeDefinitions();
        }
        for (IndicatorCategory indCategory : indicatorCategories) {

            CwmResource resource = (CwmResource) indCategory.eResource();
            if (resource != null) {
                EObject object = resource.getEObject(categoryId);
                if (object != null && DefinitionPackage.eINSTANCE.getIndicatorCategory().equals(object.eClass())) {
                    return (IndicatorCategory) object;
                }
            }
        }
        return null;
    }

    public Map<String, IndicatorCategory> getUserDefinedIndicatorCategoryMap() {
        if (userDefinedIndicatorCategoryMap == null) {
            userDefinedIndicatorCategoryMap = new HashMap<String, IndicatorCategory>();

            // init user defined indicator categories
            List<IndicatorCategory> categoryList = new ArrayList<IndicatorCategory>();
            categoryList.add(getUserDefinedCountIndicatorCategory());
            categoryList.add(getUserDefinedFrequencyIndicatorCategory());
            categoryList.add(getUserDefinedMatchIndicatorCategory());
            categoryList.add(getUserDefinedRealValueIndicatorCategory());
            // categoryList.add(getUserDefinedComparisonIndicatorCategory());
            // categoryList.add(getUserDefinedIntervalCorrelationIndicatorCategory());
            // categoryList.add(getUserDefinedNominalCorrelationIndicatorCategory());
            // categoryList.add(getUserDefinedTimeCorrelationIndicatorCategory());

            for (IndicatorCategory category : categoryList) {
                userDefinedIndicatorCategoryMap.put(category.getLabel(), category);
            }

            categoryList = null;
        }
        return userDefinedIndicatorCategoryMap;
    }

    public Collection<String> getUserDefinedIndicatorCategoryLabels() {
        return getUserDefinedIndicatorCategoryMap().keySet();
    }

    public Collection<IndicatorCategory> getUserDefinedIndicatorCategoryList() {
        return getUserDefinedIndicatorCategoryMap().values();
    }

    public IndicatorCategory getIndicatorCategoryByLabel(String label) {
        return getUserDefinedIndicatorCategoryMap().get(label);
    }

    public static IFile getTalendDefinitionFile() {
        return ResourceManager.getLibrariesFolder().getFile(FILENAME);
    }

    public static void reload() {
        instance = null;
    }

    /**
     * DOC judge the database can run Regular Expression Matching Indicator or not.
     * 
     * @param dbmsLanguage
     * @return
     */
    public boolean canRunRegularExpressionMatchingIndicator(DbmsLanguage dbmsLanguage, boolean isJavaEngin, Pattern pattern) {

        EList<PatternComponent> sqlGenericExpression = pattern.getComponents();
        for (PatternComponent exp : sqlGenericExpression) {
            String compareLanguage = null;
            if (!isJavaEngin) {
                compareLanguage = dbmsLanguage.getDbmsName();
            } else {
                compareLanguage = ExecutionLanguage.JAVA.getName();
            }
            if (exp instanceof RegularExpression) {
                String expressionLanguage = ((RegularExpression) exp).getExpression().getLanguage();
                if (DbmsLanguageFactory.isAllDatabaseType(expressionLanguage)
                        || DbmsLanguageFactory.compareDbmsLanguage(compareLanguage, expressionLanguage)) {
                    return true;
                }
            }
        }
        return false;
    }
}

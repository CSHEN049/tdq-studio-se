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
package org.talend.dq.helper;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.talend.commons.utils.TalendURLClassLoader;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.IndicatorSqlFactory;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dataquality.indicators.sql.util.IndicatorSqlSwitch;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIHelper {

    public static final String JAREXTENSIONG = "jar";//$NON-NLS-1$

    private static IndicatorSqlSwitch<UserDefIndicator> userDefIndSwitch = new IndicatorSqlSwitch<UserDefIndicator>() {

        @Override
        public UserDefIndicator caseUserDefIndicator(UserDefIndicator object) {
            return object;
        }

    };

    private UDIHelper() {
    }

    private static Logger log = Logger.getLogger(UDIHelper.class);

    // ("select * from *").length
    private static final int MIN_EXPRESSION_LENGTH = 16;

    public static IndicatorCategory getUDICategory(IndicatorDefinition indicatorDefinition) {
        if (indicatorDefinition != null) {
            EList<IndicatorCategory> categories = indicatorDefinition.getCategories();
            if (categories != null && categories.size() > 0) {
                return categories.get(0);
            }
        }
        return null;
    }

    public static IndicatorCategory getUDICategory(Indicator indicator) {
        if (indicator != null) {
            return getUDICategory(indicator.getIndicatorDefinition());
        }
        return null;
    }

    /**
     * Set the category of the IndicatorDefinition, if the category is null set UserDefinedCount category.
     * 
     * @param definition
     * @param category
     */
    public static void setUDICategory(IndicatorDefinition definition, IndicatorCategory category) {
        if (definition != null) {
            if (category == null) {
                category = DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory();
            }

            EList<IndicatorCategory> categories = definition.getCategories();
            if (categories != null) {
                categories.clear();
                categories.add(category);
            }
        }
    }

    public static void setUDICategory(IndicatorDefinition indicatorDefinition, String categoryLabel) {
        setUDICategory(indicatorDefinition, DefinitionHandler.getInstance().getIndicatorCategoryByLabel(categoryLabel));
    }

    public static void setUDICategory(Indicator indicator, String categoryLabel) {
        if (indicator != null) {
            setUDICategory(indicator.getIndicatorDefinition(), categoryLabel);
        }
    }

    public static IndicatorDefinition createUDI(String name, String author, String description, String purpose, String status,
            String category, String javaClassName, String javaJarPath) {
        IndicatorDefinition id = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        id.setName(name);
        MetadataHelper.setAuthor(id, author == null ? PluginConstant.EMPTY_STRING : author);
        MetadataHelper.setDescription(description == null ? PluginConstant.EMPTY_STRING : description, id);
        MetadataHelper.setPurpose(purpose == null ? PluginConstant.EMPTY_STRING : purpose, id);
        // MOD mzhao feature 7479 2009-10-16
        MetadataHelper.setDevStatus(id, status == null ? PluginConstant.EMPTY_STRING : status);
        TaggedValueHelper.setTaggedValue(id, TaggedValueHelper.CLASS_NAME_TEXT, javaClassName);
        TaggedValueHelper.setTaggedValue(id, TaggedValueHelper.JAR_FILE_PATH, javaJarPath);
        setUDICategory(id, category);
        return id;
    }

    public static Set<String> getAllIndicatorNames(IFolder folder) {
        Set<String> list = new HashSet<String>();
        return getNestFolderIndicatorNames(list, folder);
    }

    private static Set<String> getNestFolderIndicatorNames(Set<String> list, IFolder folder) {
        try {
            for (IResource resource : folder.members()) {
                if (resource instanceof IFile) {
                    IndicatorDefinition id = IndicatorResourceFileHelper.getInstance().findIndDefinition((IFile) resource);
                    if (id != null) {
                        list.add(id.getName());
                    }
                } else {
                    getNestFolderIndicatorNames(list, (IFolder) resource);
                }
            }
        } catch (CoreException e) {
            log.error(e, e);
        }
        return list;
    }

    public static String getMatchingIndicatorName(IndicatorDefinition indicatorDefinition, Pattern pattern) {
        if (indicatorDefinition != null) {
            return pattern.getName() + "(" + indicatorDefinition.getName() + ")";//$NON-NLS-1$//$NON-NLS-2$
        } else {
            return pattern.getName();
        }
    }

    public static boolean isCount(Indicator indicator) {
        return isCategory(indicator, DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory());
    }

    public static boolean isRealValue(Indicator indicator) {
        return isCategory(indicator, DefinitionHandler.getInstance().getUserDefinedRealValueIndicatorCategory());
    }

    public static boolean isFrequency(Indicator indicator) {
        return isCategory(indicator, DefinitionHandler.getInstance().getUserDefinedFrequencyIndicatorCategory());
    }

    public static boolean isMatching(Indicator indicator) {
        return isCategory(indicator, DefinitionHandler.getInstance().getUserDefinedMatchIndicatorCategory());
    }

    public static boolean isCategory(Indicator indicator, IndicatorCategory indicatorCategory) {
        if (indicator != null) {
            return isCategory(indicator.getIndicatorDefinition(), indicatorCategory);
        }
        return false;
    }

    public static boolean isCategory(IndicatorDefinition indicatorDefinition, IndicatorCategory indicatorCategory) {
        if (indicatorDefinition != null && indicatorCategory != null) {
            return indicatorCategory.equals(getUDICategory(indicatorDefinition));
        }
        return false;
    }

    public static boolean isUDI(Indicator indicator) {
        return indicator instanceof UserDefIndicator;
    }

    /**
     * yyi 2009-09-22 To check the expression is null, empty or less than 16 characters Feature : 8866.
     */
    public static boolean isUDIValid(IndicatorDefinition indicatorDefinition) {

        if (0 == indicatorDefinition.getSqlGenericExpression().size()) {
            return false;
        }

        if ("".equals(indicatorDefinition.getName())) {
            return false;
        }

        if ('\'' == indicatorDefinition.getName().charAt(0)) {
            return false;
        }

        for (Expression exp : indicatorDefinition.getSqlGenericExpression()) {
            if (null == exp.getBody() || exp.getBody().length() + 1 < MIN_EXPRESSION_LENGTH) {
                return false;
            }
        }

        return true;
    }

    public static ReturnCode validate(IndicatorDefinition indicatorDefinition) {

        ReturnCode rc = new ReturnCode(true);
        List<String> errorList = new ArrayList<String>();

        // MOD mzhao feature 11128, In case of Java UDI, No expression is allowed to be saved.
        if (!containsJavaUDI(indicatorDefinition)) {
            if (0 == indicatorDefinition.getSqlGenericExpression().size()) {
                errorList.add(Messages.getString("UDIHelper.validateNoExpression"));//$NON-NLS-1$
                rc.setOk(false);
            }
        }

        if (PluginConstant.EMPTY_STRING.equals(indicatorDefinition.getName())) {
            errorList.add(Messages.getString("UDIHelper.validateNoName"));//$NON-NLS-1$
            rc.setOk(false);
        }

        for (Expression exp : indicatorDefinition.getSqlGenericExpression()) {
            if (null == exp.getBody() || exp.getBody().length() + 1 < MIN_EXPRESSION_LENGTH) {
                errorList.add(Messages.getString("UDIHelper.validateTooShort"));//$NON-NLS-1$
                rc.setOk(false);
            }
        }

        String message = Messages.getString("UDIHelper.validateCannotSave");//$NON-NLS-1$
        String wrap = System.getProperty("line.separator");//$NON-NLS-1$
        for (int i = 0; i < errorList.size(); i++) {
            message += wrap + (i + 1) + org.talend.dataquality.PluginConstant.DOT_STRING + errorList.get(i);
        }
        rc.setMessage(message);

        return rc;
    }

    /**
     * 
     * DOC mzhao feature 11128, If the execute engine and by the same time Java User Defined Indicator is also defined,
     * then compute via Java UDI, here convert common udi to a Java UDI.
     * 
     * @param udi
     * @return
     * @throws Exception
     */
    public static Indicator adaptToJavaUDI(Indicator indicator) throws Throwable {
        UserDefIndicator adaptedUDI = null;
        if (userDefIndSwitch.doSwitch(indicator) != null) {
            EList<TaggedValue> taggedValues = indicator.getIndicatorDefinition().getTaggedValue();
            String userJavaClassName = null;
            String jarPath = null;
            for (TaggedValue tv : taggedValues) {
                if (tv.getTag().equals(PluginConstant.CLASS_NAME_TEXT)) {
                    userJavaClassName = tv.getValue();
                    continue;
                }
                if (tv.getTag().equals(PluginConstant.JAR_FILE_PATH)) {
                    jarPath = tv.getValue();
                }
            }
            // MOD by zshen for feature 18724
            if (validateJavaUDI(userJavaClassName, jarPath)) {
                List<URL> jarUrls = new ArrayList<URL>();
                for (IFile file : getContainJarFile(jarPath)) {
                    jarUrls.add(file.getLocationURI().toURL());
                }
                    TalendURLClassLoader cl;
                cl = new TalendURLClassLoader(jarUrls.toArray(new URL[jarUrls.size()]));// new URL[] {
                                                                                        // file.getLocation().toFile().toURI().toURL()
                                                                                        // });
                    Class<?> clazz = null;
                clazz = cl.findClass(userJavaClassName);
                    if (clazz != null) {
                        UserDefIndicator judi = (UserDefIndicator) clazz.newInstance();
                        judi.setIndicatorDefinition(indicator.getIndicatorDefinition());
                        if (indicator instanceof JavaUserDefIndicator) {
                            ((JavaUserDefIndicator) indicator).setJavaUserDefObject(judi);
                        } else {
                            JavaUserDefIndicator judiTemplate = IndicatorSqlFactory.eINSTANCE.createJavaUserDefIndicator();
                            judiTemplate.setJavaUserDefObject(judi);
                            judiTemplate.setIndicatorDefinition(indicator.getIndicatorDefinition());
                            judiTemplate.setAnalyzedElement(indicator.getAnalyzedElement());
                            adaptedUDI = judiTemplate;
                        }
                    }

            }
        }
        return adaptedUDI;
    }

    private static boolean validateJavaUDI(String className, String jarPath) {
        if (className == null || jarPath == null || className.trim().equals(PluginConstant.EMPTY_STRING)
                || jarPath.trim().equals(PluginConstant.EMPTY_STRING)) {
            return false;
        }
        return true;
    }

    private static boolean containsJavaUDI(IndicatorDefinition definition) {
        EList<TaggedValue> tvs = definition.getTaggedValue();
        for (TaggedValue tv : tvs) {
            if (tv.getTag().equals(PluginConstant.CLASS_NAME_TEXT)) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC klliu Comment method "isJavaUDI".
     * 
     * @param indicator
     * @return
     */
    public static boolean isJavaUDI(Indicator indicator) {
        // TODO Auto-generated method stub
        IndicatorDefinition definition = indicator.getIndicatorDefinition();
        boolean systemIndicator = definition != null && definition.eResource() != null
                && definition.eResource().getURI().toString().contains(EResourceConstant.SYSTEM_INDICATORS.getName());
        return systemIndicator;
    }

    /**
     * 
     * zshen Comment method "getLibJarFileList".
     * 
     * @return
     */
    public static List<IFile> getLibJarFileList() {
        List<IFile> fileList = new ArrayList<IFile>();
        try {
            for (org.eclipse.core.resources.IResource fileResource : ResourceManager.getUDIJarFolder().members()) {
                if (IResource.FILE == fileResource.getType()
                        && JAREXTENSIONG.equalsIgnoreCase(fileResource.getFullPath().getFileExtension())) {
                    fileList.add((IFile) fileResource);
                }
            }
        } catch (CoreException e) {
            log.error(e, e);
        }
        return fileList;
    }

    /**
     * 
     * zshen Comment method "getContainJarFile".
     * 
     * @param jarPathStr
     * @return
     */
    public static List<IFile> getContainJarFile(String jarPathStr) {
        List<IFile> fileList = new ArrayList<IFile>();

        for (String containJarName : jarPathStr.split("\\|\\|")) {//$NON-NLS-1$
            for (IFile libJarFile : getLibJarFileList()) {
                if (libJarFile.getName().equalsIgnoreCase(containJarName)) {
                    fileList.add(libJarFile);
                    break;
                }
            }
        }
        return fileList;
    }

}

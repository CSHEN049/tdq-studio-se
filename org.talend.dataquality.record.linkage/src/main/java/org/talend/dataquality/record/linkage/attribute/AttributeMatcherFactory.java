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
package org.talend.dataquality.record.linkage.attribute;

import java.util.ArrayList;
import java.util.List;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * @author scorreia
 * 
 * Factory that helps to create attribute matchers from their label or type.
 */
public final class AttributeMatcherFactory {

    private static List<String> labels = new ArrayList<String>();

    /**
     * private constructor.
     */
    private AttributeMatcherFactory() {
    }

    /**
     * Method "createMatcher".
     * 
     * @param matcherLabel the type of attribute matcher
     * @return the attribute matcher or null if the input type is null
     */
    public static IAttributeMatcher createMatcher(String matcherLabel) {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type.getLabel().equalsIgnoreCase(matcherLabel)) {
                return createMatcher(type);
            }
        }
        // TODO log matcher not found
        return null;
    }

    /**
     * Method "createMatcher". If the type is {@link AttributeMatcherType#custom}, then the resulting IAttributeMatcher
     * class is instantiated given the className argument. If the type is different, then the
     * {@link AttributeMatcherFactory#createMatcher(AttributeMatcherType)} method is called.
     * 
     * @param type the type of the attribute matcher
     * @param className the class name that implements IAttributeMatcher
     * @return the instantiated Attribute Matcher class
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static IAttributeMatcher createMatcher(AttributeMatcherType type, String className) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        if (type != null) {
            switch (type) {
            case custom:
                return (IAttributeMatcher) Class.forName(className).newInstance();
            default:
                return createMatcher(type);
            }
        }
        // TODO : log no matcher found
        return null;
    }

    /**
     * Method "createMatcher".
     * 
     * @param type the type of attribute matcher
     * @return the attribute matcher or null if the input type is null
     */
    public static IAttributeMatcher createMatcher(AttributeMatcherType type) {
        if (type != null) {
            switch (type) {
            case dummy:
                return new DummyMatcher();
            case exact:
                return new ExactMatcher();
            case levenshtein:
                return new LevenshteinMatcher();
            case soundex:
                return new SoundexMatcher();
            case doubleMetaphone:
                return new DoubleMetaphoneMatcher();
            case exactIgnoreCase:
                return new ExactIgnoreCaseMatcher();
            case metaphone:
                return new MetaphoneMatcher();
            case jaro:
                return new JaroMatcher();
            case jaroWinkler:
                return new JaroWinklerMatcher();
            case soundexFR:
                return new SoundexFRMatcher();
            case qgrams:
                return new QGramsMatcher();
            default:
                break;
            }
        }
        // else
        // TODO : log no matcher found
        return null;
    }

    public static List<String> getMatcherLabels() {
        if (!labels.isEmpty()) {
            return labels;
        }
        // else fill in the labels
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            labels.add(type.getLabel());
        }
        return labels;
    }

}

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
package org.talend.dataquality.helpers;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.IQRIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MeanIndicator;
import org.talend.dataquality.indicators.MedianIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.ModeIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.ValueIndicator;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dataquality.indicators.sql.util.IndicatorSqlSwitch;
import org.talend.dataquality.indicators.util.IndicatorsSwitch;

/**
 * @author scorreia
 * 
 * Helper class for handling indicator attributes.
 */
public final class IndicatorHelper {

    private static Logger log = Logger.getLogger(IndicatorHelper.class);

    /**
     * The available threshold types.
     */
    private static enum ThresholdType {
        VALUE_THRESHOLD("Value Threshold"), //$NON-NLS-1$
        PERCENTAGE_THRESHOLD("Percentage Threshold"); //$NON-NLS-1$

        private String label;

        /**
         * Getter for label.
         * 
         * @return the label
         */
        public String getLabel() {
            return this.label;
        }

        ThresholdType(String label) {
            this.label = label;
        }
    }

    private IndicatorHelper() {
    }

    public static void setDataThreshold(Indicator indicator, String min, String max) {
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
            indicator.setParameters(parameters);
        }
        setDataThreshold(parameters, min, max);
    }

    public static void setDataThreshold(IndicatorParameters parameters, String min, String max) {
        assert parameters != null;
        Domain validDomain = parameters.getDataValidDomain();
        if (validDomain == null) {
            validDomain = DomainHelper.createDomain("Data threshold"); //$NON-NLS-1$
            parameters.setDataValidDomain(validDomain);
        }
        // remove previous ranges
        assert validDomain.getRanges().size() < 2;
        validDomain.getRanges().clear();
        RangeRestriction rangeRestriction = DomainHelper.createStringRangeRestriction(min, max);
        validDomain.getRanges().add(rangeRestriction);
    }

    /**
     * Method "getDataThreshold".
     * 
     * @param indicator
     * @return an array with 2 strings representing the data thresholds or null. If the array is not null, its content
     * can be null but its size is always 2.
     */
    public static String[] getDataThreshold(Indicator indicator) {
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            return null;
        }
        return getDataThreshold(parameters);
    }

    /**
     * Method "setIndicatorThreshold".
     * 
     * @param parameters
     * @param min the minimal value of the threshold (can be null)
     * @param max the maximal value of the threshold (can be null)
     */
    public static void setIndicatorThreshold(IndicatorParameters parameters, String min, String max) {
        setIndicatorThreshold(parameters, min, max, ThresholdType.VALUE_THRESHOLD);
    }

    /**
     * Method "setIndicatorThresholdInPercent" set the threholds of the indicator in percentage of the row count value.
     * 
     * @param parameters
     * @param min the minimal value of the threshold (can be null)
     * @param max the minimal value of the threshold (can be null)
     */
    public static void setIndicatorThresholdInPercent(IndicatorParameters parameters, String min, String max) {
        setIndicatorThreshold(parameters, min, max, ThresholdType.PERCENTAGE_THRESHOLD);
    }

    private static void setIndicatorThreshold(IndicatorParameters parameters, String min, String max, ThresholdType thresholdType) {
        assert parameters != null;
        Domain validDomain = parameters.getIndicatorValidDomain();
        if (validDomain == null) {
            validDomain = DomainHelper.createDomain("Indicator threshold"); //$NON-NLS-1$
            parameters.setIndicatorValidDomain(validDomain);
        }
        EList<RangeRestriction> ranges = validDomain.getRanges();
        for (RangeRestriction rangeRestriction : ranges) {
            if (thresholdType.getLabel().equals(rangeRestriction.getName())) {
                rangeRestriction.setLowerValue(DomainHelper.createStringValue(null, min));
                rangeRestriction.setUpperValue(DomainHelper.createStringValue(null, max));
                return;
            }
        }
        // else no previous range found, create a new one
        RangeRestriction rangeRestriction = DomainHelper.createStringRangeRestriction(min, max);
        rangeRestriction.setName(thresholdType.getLabel());
        ranges.add(rangeRestriction);
    }

    /**
     * Method "getIndicatorThreshold".
     * 
     * @param indicator
     * @return an array of thresholds if any or null. When the array is not null, its size is 2 but its elements can be
     * null. The first element is the lower threshold and the second element is the higher threshold.
     */
    public static String[] getIndicatorThreshold(Indicator indicator) {
        return getIndicatorThreshold(indicator.getParameters());
    }

    /**
     * DOC Zqin Comment method "getIndicatorThreshold".
     * 
     * @param parameters
     * @return an array of thresholds if any or null. When the array is not null, its size is 2 but its elements can be
     * null. The first element is the lower threshold and the second element is the higher threshold.
     */
    public static String[] getIndicatorThreshold(IndicatorParameters parameters) {
        if (parameters == null) {
            return null;
        }
        return getIndicatorThreshold(parameters, ThresholdType.VALUE_THRESHOLD);
    }

    /**
     * Method "getIndicatorThresholdInPercent" returns the threshold in percentage.
     * 
     * @param indicator
     * @return an array of thresholds if any or null. When the array is not null, its size is 2 but its elements can be
     * null. The first element is the lower threshold and the second element is the higher threshold.
     */
    public static String[] getIndicatorThresholdInPercent(Indicator indicator) {
        return getIndicatorThresholdInPercent(indicator.getParameters());
    }

    /**
     * Method "getIndicatorThresholdInPercent" returns the threshold in percentage.
     * 
     * @param parameters
     * @return an array of thresholds if any or null. When the array is not null, its size is 2 but its elements can be
     * null. The first element is the lower threshold and the second element is the higher threshold.
     */
    public static String[] getIndicatorThresholdInPercent(IndicatorParameters parameters) {
        if (parameters == null) {
            return null;
        }
        return getIndicatorThreshold(parameters, ThresholdType.PERCENTAGE_THRESHOLD);
    }

    private static String[] getIndicatorThreshold(IndicatorParameters parameters, ThresholdType thresholdType) {
        Domain validDomain = parameters.getIndicatorValidDomain();
        if (validDomain == null) {
            return null;
        }
        EList<RangeRestriction> ranges = validDomain.getRanges();
        for (RangeRestriction rangeRestriction : ranges) {
            if (thresholdType.getLabel().equals(rangeRestriction.getName())) {
                return new String[] { DomainHelper.getMinValue(rangeRestriction), DomainHelper.getMaxValue(rangeRestriction) };
            }
        }
        return null;
    }

    /**
     * Method "getDataThreshold".
     * 
     * @param parameters
     * @return an array with two elements. returns null when no threshold has been found. One element of the array can
     * be null but not both. In this case, it means that there is only one defined threshold, either the upper or the
     * lower threshold.
     */
    public static String[] getDataThreshold(IndicatorParameters parameters) {
        Domain validDomain = parameters.getDataValidDomain();
        if (validDomain == null) {
            return null;
        }
        EList<RangeRestriction> ranges = validDomain.getRanges();
        if (ranges.size() != 1) {
            // log.warn("Data threshold contain too many ranges (or no range): " + ranges.size() + " range(s).");
            return null;
        }
        RangeRestriction rangeRestriction = ranges.get(0);
        if (rangeRestriction == null) {
            return new String[] { null, null };
        }
        return new String[] { DomainHelper.getMinValue(rangeRestriction), DomainHelper.getMaxValue(rangeRestriction) };
    }

    /**
     * Method "getIndicatorLeaves" returns the leaf indicators when the given indicator is a composite indicator or the
     * given indicator.
     * 
     * @param indicator the indicator
     * @return the leaf indicators
     */
    public static List<Indicator> getIndicatorLeaves(Indicator indicator) {
        List<Indicator> leafIndicators = new ArrayList<Indicator>();
        if (indicator instanceof CompositeIndicator) {
            CompositeIndicator compositeIndicator = (CompositeIndicator) indicator;
            try {
                for (Indicator ind : compositeIndicator.getAllChildIndicators()) {
                    leafIndicators.addAll(getIndicatorLeaves(ind));
                }
            } catch (Exception e) {
                log.error(e);
            }
        } else {
            leafIndicators.add(indicator);
        }
        return leafIndicators;
    }

    /**
     * Method "getIndicatorLeaves".
     * 
     * @param result
     * @return all the leaf indicators
     */
    public static List<Indicator> getIndicatorLeaves(AnalysisResult result) {
        List<Indicator> leafIndicators = new ArrayList<Indicator>();
        EList<Indicator> indicators = result.getIndicators();
        for (Indicator indicator : indicators) {
            leafIndicators.addAll(getIndicatorLeaves(indicator));
        }
        return leafIndicators;
    }

    /**
     * Method "getIndicators".
     * 
     * @param result
     * @return
     */
    public static List<Indicator> getIndicators(AnalysisResult result) {
        List<Indicator> indicators = new ArrayList<Indicator>();

        for (Indicator indicator : result.getIndicators()) {
            List<Indicator> indicatorLeaves = getIndicatorLeaves(indicator);
            if (indicator instanceof CompositeIndicator) {
                indicators.add(indicator);
            }

            indicators.addAll(indicatorLeaves);
        }

        return indicators;
    }

    /**
     * Method "getExpectedValue".
     * 
     * @param indicator usually a mode indicator
     * @return the expected value of the indicator
     */
    public static String getExpectedValue(Indicator indicator) {
        return getExpectedValue(indicator.getParameters());
    }

    /**
     * DOC zqin Comment method "getExpectedValue".
     * 
     * @param parameters
     * @return
     */
    public static String getExpectedValue(IndicatorParameters parameters) {
        if (parameters == null) {
            return null;
        }
        Domain indValidDomain = parameters.getIndicatorValidDomain();
        if (indValidDomain == null) {
            return null;
        }
        return DomainHelper.getIndicatorExpectedValue(Collections.singleton(indValidDomain));
    }

    public static void setIndicatorExpectedValue(IndicatorParameters parameters, String value) {
        assert parameters != null;
        Domain validDomain = parameters.getIndicatorValidDomain();
        if (validDomain == null) {
            validDomain = DomainHelper.createIndicatorExpectedValueDomain();
            parameters.setIndicatorValidDomain(validDomain);
        }
        DomainHelper.setIndicatorExpectedValuePattern(Collections.singleton(validDomain), value);
    }

    /**
     * Method "propagateDataThresholdsInChildren" will propage the data threshold to the indicator if the given
     * indicator is a BoxIndicator (Otherwise, nothing is done).
     * 
     * @param indicator an instance of BoxIndicator
     * 
     * 
     */
    public static void propagateDataThresholdsInChildren(Indicator indicator) {
        if (IndicatorsPackage.eINSTANCE.getBoxIndicator().equals(indicator.eClass())) {
            BoxIndicator boxIndicator = (BoxIndicator) indicator;
            String[] dataThreshold = IndicatorHelper.getDataThreshold(boxIndicator);
            if (dataThreshold == null) {
                // clear all data thresholds
                final EList<Indicator> allChildIndicators = boxIndicator.getAllChildIndicators();
                for (Indicator ind : allChildIndicators) {
                    clearDataThresholds(ind);
                }
                return;
            }

            // --- add thresholds in min and max indicators
            RangeIndicator rangeIndicator = boxIndicator.getRangeIndicator();
            setDataThresholds(rangeIndicator, dataThreshold);

            // --- add thresholds in lower and upper quartile indicators
            IQRIndicator iqr = boxIndicator.getIQR();
            setDataThresholds(iqr, dataThreshold);

            // --- add threholds to the mean and median indicator
            setDataThreshold(boxIndicator.getMeanIndicator(), dataThreshold[0], dataThreshold[1]);
            setDataThreshold(boxIndicator.getMedianIndicator(), dataThreshold[0], dataThreshold[1]);
        } else if (IndicatorsPackage.eINSTANCE.getRangeIndicator().equals(indicator.eClass())
                || IndicatorsPackage.eINSTANCE.getIQRIndicator().equals(indicator.eClass())) {
            RangeIndicator rangeIndicator = (RangeIndicator) indicator;
            String[] dataThreshold = IndicatorHelper.getDataThreshold(rangeIndicator);
            if (dataThreshold == null) {
                // clear all data thresholds
                final EList<Indicator> allChildIndicators = rangeIndicator.getAllChildIndicators();
                for (Indicator ind : allChildIndicators) {
                    clearDataThresholds(ind);
                }
                return;
            }

            // --- add thresholds in (min and max) or (lower and upper quartile) indicators
            setDataThresholds(rangeIndicator, dataThreshold);
        }
    }

    /**
     * DOC scorreia Comment method "clearDataThresholds".
     * 
     * @param ind
     */
    private static void clearDataThresholds(Indicator ind) {
        final IndicatorParameters parameters = ind.getParameters();
        if (parameters != null) {
            parameters.setDataValidDomain(null);
        }
    }

    /**
     * DOC scorreia Comment method "setDataThresholds".
     * 
     * @param rangeIndicator
     * @param dataThreshold
     */
    private static void setDataThresholds(RangeIndicator rangeIndicator, String[] dataThreshold) {
        if (rangeIndicator != null) {
            IndicatorHelper.setDataThreshold(rangeIndicator, dataThreshold[0], dataThreshold[1]);
            MinValueIndicator lowerValue = rangeIndicator.getLowerValue();
            if (lowerValue != null) {
                IndicatorHelper.setDataThreshold(lowerValue, dataThreshold[0], dataThreshold[1]);
            }
            MaxValueIndicator upperValue = rangeIndicator.getUpperValue();
            if (upperValue != null) {
                IndicatorHelper.setDataThreshold(upperValue, dataThreshold[0], dataThreshold[1]);
            }
        }
    }

    /**
     * Method "ignoreCaseOption".
     * 
     * @param indicator
     * @return the ignoreCase option or null if it is not set.
     */
    public static Boolean ignoreCaseOption(Indicator indicator) {
        IndicatorParameters parameters = indicator.getParameters();
        return (parameters != null) ? ignoreCaseOption(parameters) : null;
    }

    /**
     * Method "ignoreCaseOption".
     * 
     * @param parameters
     * @return the ignoreCase option or null if it is not set.
     */
    public static Boolean ignoreCaseOption(IndicatorParameters parameters) {
        TextParameters textParameter = parameters.getTextParameter();
        return (textParameter != null) ? textParameter.isIgnoreCase() : null;
    }

    /**
     * DOC bZhou Comment method "getIndicatorValue".
     * 
     * @param indicator
     * @return
     */
    public static String getIndicatorValue(Indicator indicator) {

        IndicatorsSwitch<String> mySwitch = new IndicatorsSwitch<String>() {

            @Override
            public String caseAverageLengthIndicator(AverageLengthIndicator object) {
                return createStandardNumber(object.getAverageLength());
            }

            @Override
            public String caseBlankCountIndicator(BlankCountIndicator object) {
                return String.valueOf(object.getBlankCount());
            }

            @Override
            public String caseDefValueCountIndicator(DefValueCountIndicator object) {
                return String.valueOf(object.getDefaultValCount());
            }

            @Override
            public String caseDistinctCountIndicator(DistinctCountIndicator object) {
                return String.valueOf(object.getDistinctValueCount());
            }

            @Override
            public String caseDuplicateCountIndicator(DuplicateCountIndicator object) {
                return String.valueOf(object.getDuplicateValueCount());
            }

            @Override
            public String caseMaxLengthIndicator(MaxLengthIndicator object) {
                return String.valueOf(object.getLength());
            }

            @Override
            public String caseMeanIndicator(MeanIndicator object) {
                return createStandardNumber(object.getMean());
            }

            @Override
            public String caseMedianIndicator(MedianIndicator object) {
                return createStandardNumber(object.getMedian());
            }

            @Override
            public String caseMinLengthIndicator(MinLengthIndicator object) {
                return String.valueOf(object.getLength());
            }

            @Override
            public String caseModeIndicator(ModeIndicator object) {
                return String.valueOf(object.getMode());
            }

            @Override
            public String caseNullCountIndicator(NullCountIndicator object) {
                return String.valueOf(object.getNullCount());
            }

            @Override
            public String casePatternMatchingIndicator(PatternMatchingIndicator object) {
                return String.valueOf(object.getMatchingValueCount());
            }

            @Override
            public String caseRowCountIndicator(RowCountIndicator object) {
                return String.valueOf(object.getCount());
            }

            @Override
            public String caseUniqueCountIndicator(UniqueCountIndicator object) {
                return String.valueOf(object.getUniqueValueCount());
            }

            @Override
            public String caseValueIndicator(ValueIndicator object) {
                return object.getValue();
            }

        };

        IndicatorSqlSwitch<String> sqlSwitch = new IndicatorSqlSwitch<String>() {

            @Override
            public String caseWhereRuleIndicator(WhereRuleIndicator object) {
                return String.valueOf(object.getUserCount());
            }
        };

        String result = mySwitch.doSwitch(indicator);

        return result == null ? sqlSwitch.doSwitch(indicator) : result;
    }

    /**
     * DOC bZhou Comment method "getIndicatorPercentValue".
     * 
     * @param indicator, never null
     * @return null in case of error
     */
    public static String getIndicatorPercentValue(Indicator indicator) {

        try {
            // MOD SeB 11/03/2010, bug 11751 : number format exception conveerting from string to double
            // use NumberFormt instead of formatter.
            String indicatorValue = getIndicatorValue(indicator);
            if (indicatorValue != null) { // MOD scorreia fixing NPE in bug 12250
                double userCount = NumberFormat.getInstance().parse(indicatorValue).doubleValue();
                double count = Double.valueOf(indicator.getCount());
                return computePercent(userCount, count);
            }
        } catch (NumberFormatException e) {
            log.warn("could not parse indicator: " + indicator.getName(), e);
        } catch (ParseException e) {
            log.warn("could not parse indicator: " + indicator.getName(), e);
        }
        return null;
    }

    /**
     * DOC bZhou Comment method "computePercent".
     * 
     * @param userCount
     * @param count
     * @return
     */
    private static String computePercent(double userCount, double count) {
        double result = userCount / count;
        return result != Double.NaN ? String.valueOf(result) : null;
    }

    private static String createStandardNumber(Object input) {
        // MOD SeB 11/03/2010 bug 11751 : convert number to String using current locale
        // MOD yyi 2010-04-15 bug 12483 : Unify the decimal format as US.
        return NumberFormat.getInstance(Locale.US).format(input);
    }

    /**
     * MOD gdbu 2011-4-14 bug : 18975
     * 
     * DOC gdbu Comment method "getLongFromObject".
     * 
     * @param objects
     * @return
     */
    public static Long getLongFromObject(Object objects) {
        Long c = 0L;
        String toString = String.valueOf(objects + "");//$NON-NLS-1$
        try {
            c = Long.valueOf(toString);
        } catch (Exception e) {
            if (toString.contains(".")) {//$NON-NLS-1$
                toString = toString.substring(0, toString.indexOf("."));//$NON-NLS-1$
            }
            c = Long.valueOf(toString);
        }
        return c;
    }

    /**
     * MOD gdbu 2011-4-28 bug : 18975
     * 
     * DOC gdbu Comment method "getIntegerFromObject".
     * 
     * @param objects
     * @return
     */
    public static Integer getIntegerFromObject(Object objects) {
        Integer c = 0;
        String toString = String.valueOf(objects + "");//$NON-NLS-1$
        try {
            c = Integer.valueOf(toString);
        } catch (Exception e) {
            if (toString.contains(".")) {//$NON-NLS-1$
                toString = toString.substring(0, toString.indexOf("."));//$NON-NLS-1$
            }
            c = Integer.valueOf(toString);
        }
        return c;
    }
}

// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking;

import org.talend.dataquality.duplicating.RandomWrapper;

/**
 * created by jgonzalez on 18 juin 2015. This class is an abstract class that all other functions extends. All the
 * methods and fiels that all functions share are stored here.
 *
 */
public abstract class Function<T> {

    protected String EMPTY_STRING = ""; //$NON-NLS-1$

    protected String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

    protected String LOWER = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

    protected RandomWrapper rnd = null;

    protected static int seq = 0;

    protected Integer integerParam = 0;

    protected String[] parameters = new String[1];

    protected boolean keepNull = false;

    /**
     * DOC jgonzalez Comment method "setSeq". This function is used for the GENERATE_SEQUENCE function.
     * 
     * @param i The element that starts the sequence.
     */
    public void setSeq(String s) {
        int i = 0;
        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            // Do nothing
        }
        seq = i;
    }

    /**
     * DOC jgonzalez Comment method "setRandomWrapper". This method is used to set the RandomWrapper used by all
     * functions.
     * 
     * @param rand The RandomWrapper.
     */
    public void setRandomWrapper(RandomWrapper rand) {
        rnd = rand;
    }

    /**
     * DOC jgonzalez Comment method "setKeepNull". This function sets a boolean used to keep null values.
     * 
     * @param keep The value of the boolean.
     */
    public void setKeepNull(boolean keep) {
        this.keepNull = keep;
    }

    /**
     * DOC jgonzalez Comment method "parse". This function is called at the beginning of the job and parses the
     * parameter. Moreover, it will call methods setKeepNull and setRandomWrapper
     * 
     * @param extraParameter The parameter we try to parse.
     * @param keepNullValues The parameter used for setKeepNull.
     * @param rand The parameter used for setRandomMWrapper.
     */
    public void parse(String extraParameter, boolean keepNullValues, RandomWrapper rand) {
        if (extraParameter != null) {
            try {
                parameters = extraParameter.split(","); //$NON-NLS-1$
                integerParam = parameters.length == 1 ? Integer.parseInt(parameters[0]) : 0;
            } catch (NumberFormatException e) {
                // We do nothing here because parameters[] is already set.
            }
        }
        setKeepNull(keepNullValues);
        setRandomWrapper(rand);
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This method applies a function on a field and returns the its
     * new value.
     * 
     * @param t The input value.
     * @return A new value after applying the function.
     */
    public abstract T generateMaskedRow(T t);
}

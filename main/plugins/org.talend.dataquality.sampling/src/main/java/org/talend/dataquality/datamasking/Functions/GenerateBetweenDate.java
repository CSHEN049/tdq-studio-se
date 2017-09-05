// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.Functions;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 18 juin 2015. This function will return a date between the two given as parameters.
 *
 */
public class GenerateBetweenDate extends Function<Date> implements Serializable {

    private static final long serialVersionUID = 7513182257849118816L;

    @Override
    public Date generateMaskedRow(Date date) {
        if (date == null && keepNull) {
            return null;
        } else {
            if (parameters.length == 2) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy"); //$NON-NLS-1$
                Date minDate = null;
                Date maxDate = null;
                try {
                    minDate = df.parse(parameters[0].trim());
                    maxDate = df.parse(parameters[1].trim());
                } catch (ParseException e) {
                    return new Date(System.currentTimeMillis());
                }
                if (minDate.after(maxDate)) {
                    Date tmp = minDate;
                    minDate = maxDate;
                    maxDate = tmp;
                } else if (minDate.equals(maxDate)) {
                    return minDate;
                }
                long min = minDate.getTime();
                long max = maxDate.getTime();
                long number = min + ((long) (rnd.nextDouble() * (max - min)));
                Date newDate = new Date(number);
                return newDate;
            } else {
                return new Date(System.currentTimeMillis());
            }
        }
    }
}

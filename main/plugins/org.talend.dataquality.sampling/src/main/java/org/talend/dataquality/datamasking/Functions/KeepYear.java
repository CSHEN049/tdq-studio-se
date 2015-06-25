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
package org.talend.dataquality.datamasking.Functions;

import java.util.Calendar;
import java.util.Date;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 18 juin 2015.This function will set the month and day fields of the date to January the
 * first, and won’t change the year.
 *
 */
public class KeepYear extends Function<Date> {

    private static Calendar c = Calendar.getInstance();

    @Override
    public Date generateMaskedRow(Date date) {
        if (date == null && keepNull) {
            return null;
        } else {
            Date newDate = new Date(System.currentTimeMillis());
            if (date != null) {
                c.setTime(date);
            } else {
                c.setTime(newDate);
            }
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, Calendar.JANUARY);
            newDate = c.getTime();
            return newDate;
        }
    }
}
// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

/**
 * created by jgonzalez on 19 juin 2015. See GenerateCreditCardFormat.
 *
 */
public class GenerateCreditCardFormatString extends GenerateCreditCardFormat<String> implements Serializable {

    private static final long serialVersionUID = 3682663337119470753L;

    protected boolean keepFormat = ("true").equals(parameters[0]); //$NON-NLS-1$ 

    @Override
    public String generateMaskedRow(String str) {
        if ((str == null || EMPTY_STRING.equals(str)) && keepNull) {
            return str;
        } else {
            CreditCardType cct_format = null;
            if (str == null || EMPTY_STRING.equals(str)) {
                cct_format = super.chooseCreditCardType();
                return super.generateCreditCard(cct_format).toString();
            } else {
                try {
                    cct_format = super.getCreditCardType(Long.parseLong(str.replaceAll("\\s+", EMPTY_STRING))); //$NON-NLS-1$ 
                } catch (NumberFormatException e) {
                    cct_format = super.chooseCreditCardType();
                    return super.generateCreditCard(cct_format).toString();
                }
                if (cct_format != null) {
                    return super.generateCreditCardFormat(cct_format, str, keepFormat);
                } else {
                    cct_format = super.chooseCreditCardType();
                    return super.generateCreditCard(cct_format).toString();
                }
            }
        }
    }
}

// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
 * created by jgonzalez on 19 juin 2015. This function will generate a valid credit card number. It can be used on
 * String and Long values.
 *
 */
public abstract class GenerateCreditCardSimple<T2> extends GenerateCreditCard<T2> implements Serializable {

    private static final long serialVersionUID = -3556491458582882652L;

    protected Long number = null;

    protected void generateCreditCard() {
        CreditCardType cct = super.chooseCreditCardType();
        Long card = super.generateCreditCard(cct);
        this.number = card;
    }

    @Override
    public abstract T2 generateMaskedRow(T2 t);
}

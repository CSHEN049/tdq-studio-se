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

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 24 juin 2015. This function will return null.
 * 
 * @param <T>
 *
 */
public class SetToNull<T> extends Function<T> implements Serializable {

    private static final long serialVersionUID = -1747988334179919785L;

    @Override
    public T generateMaskedRow(T t) {
        return null;
    }

}

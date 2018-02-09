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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.talend.dataquality.datamasking.Function;

/**
 * created by jgonzalez on 24 juin 2015. This function works like GenerateFromFile, the only difference is that it will
 * use the hashCode() function provided by Java to choose an element from the list. When having the hashCode, we apply a
 * modulo according to the number of elements in the list.
 *
 */
public abstract class GenerateFromFileHash<T2> extends Function<T2> {

    private BufferedReader in = null;

    protected List<String> StringTokens = new ArrayList<>();

    protected void init() {
        try {
            in = new BufferedReader(new FileReader(parameters[0]));
            while (in.ready()) {
                StringTokens.add(in.readLine().trim());
            }
            in.close();
        } catch (IOException | NullPointerException e) {
            // We do nothing here because in is already set.
        } 
    }

    @Override
    public abstract T2 generateMaskedRow(T2 t);
}

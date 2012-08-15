// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis.connpool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.dataquality.analysis.Analysis;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class TdqAnalysisConnectionPoolMap {

    private Analysis analysis;

    public Analysis getAnalysis() {
        return this.analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    private TdqAnalysisConnectionPoolMap(Analysis analysis) {
        this.setAnalysis(analysis);
    }

    /**
     * the TdqAnalysisConnectionPoolMap's map, [key=Analysis][value=TdqAnalysisConnectionPoolMap].
     */
    private static final Map<Analysis, TdqAnalysisConnectionPoolMap> INSTANCE_MAP = Collections
            .synchronizedMap(new HashMap<Analysis, TdqAnalysisConnectionPoolMap>());

    /**
     * DOC xqliu Comment method "getInstance".
     * 
     * @param analysis
     * @return
     */
    public static TdqAnalysisConnectionPoolMap getInstance(Analysis analysis) {
        TdqAnalysisConnectionPoolMap tdqAnalysisConnectionPoolMap = INSTANCE_MAP.get(analysis);
        if (tdqAnalysisConnectionPoolMap == null) {
            tdqAnalysisConnectionPoolMap = new TdqAnalysisConnectionPoolMap(analysis);
            INSTANCE_MAP.put(analysis, tdqAnalysisConnectionPoolMap);
        }
        return tdqAnalysisConnectionPoolMap;
    }

    /**
     * DOC xqliu Comment method "clearMap".
     */
    public static void clearMap() {
        INSTANCE_MAP.clear();
    }

    /**
     * the connections pool map,
     * [key=org.talend.core.model.metadata.builder.connection.Connection][value=TdqAnalysisConnectionPool].
     */
    private final Map<org.talend.core.model.metadata.builder.connection.Connection, TdqAnalysisConnectionPool> connectionPools = Collections
            .synchronizedMap(new HashMap<org.talend.core.model.metadata.builder.connection.Connection, TdqAnalysisConnectionPool>());

    /**
     * get the TdqAnalysisConnectionPool accroding to the key.
     * 
     * @param key
     * @return
     */
    public synchronized TdqAnalysisConnectionPool getConnectionPool(
            org.talend.core.model.metadata.builder.connection.Connection key, int maxConnections) {
        TdqAnalysisConnectionPool pool = connectionPools.get(key);
        if (pool == null) {
            pool = new TdqAnalysisConnectionPool(key, maxConnections);
            connectionPools.put(key, pool);
        }
        return pool;
    }

    /**
     * DOC xqliu Comment method "closePools".
     */
    public synchronized void closePools() {
        Set<Connection> keySet = connectionPools.keySet();
        for (Connection key : keySet) {
            TdqAnalysisConnectionPool tdqAnalysisConnectionPool = connectionPools.get(key);
            if (tdqAnalysisConnectionPool != null) {
                tdqAnalysisConnectionPool.closeConnectionPool();
            }
        }
        connectionPools.clear();
    }

    /**
     * DOC xqliu Comment method "closePool".
     * 
     * @param key
     */
    public synchronized void closePool(org.talend.core.model.metadata.builder.connection.Connection key) {
        TdqAnalysisConnectionPool tdqAnalysisConnectionPool = connectionPools.get(key);
        if (tdqAnalysisConnectionPool != null) {
            tdqAnalysisConnectionPool.closeConnectionPool();
            connectionPools.remove(key);
        }
    }
}

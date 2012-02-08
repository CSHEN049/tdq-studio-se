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
package org.talend.dq.writer.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * @author scorreia
 * 
 * This class saves the analysis.
 */
public class ReportWriter extends AElementPersistance {

    static Logger log = Logger.getLogger(ReportWriter.class);

    /**
     * DOC bZhou ReportWriter constructor comment.
     */
    ReportWriter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    public void addDependencies(ModelElement element) {
        TdReport report = (TdReport) element;
        List<Analysis> analyses = ReportHelper.getAnalyses(report);
        for (Analysis ana : analyses) {
            TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(report, ana);
            if (dependencyReturn.isOk()) {
                try {
                    Property property = PropertyHelper.getProperty(ana);
                    if (property != null) {
                        Item item = property.getItem();
                        if (item instanceof TDQAnalysisItem) {
                            TDQAnalysisItem anaItem = (TDQAnalysisItem) item;
                            anaItem.setAnalysis(ana);
                        }
                    }
                    ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                            .saveResource(ana.eResource());
                } catch (PersistenceException e) {
                    log.error(e, e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.REP;
    }

    public ReturnCode save(Item item, boolean careDependency) {
        TDQReportItem repItem = (TDQReportItem) item;
        Report report = repItem.getReport();
        // MOD yyi 2012-02-07 TDQ-4621:Update dependencies(connection) when careDependency is true.
        if (careDependency) {
            return saveWithDependencies(repItem, report);
        } else {
            return saveWithoutDependencies(repItem, report);
        }
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#updateDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    // @Override
    // protected void updateDependencies(ModelElement element) {
    // TdReport report = (TdReport) element;
    // // update supplier dependency
    // // if report have supplier dependency, add codes here
    // // update client dependency
    // EList<Dependency> clientDependency = report.getClientDependency();
    // try {
    // for (Dependency cDependency : clientDependency) {
    // EList<ModelElement> supplier = cDependency.getSupplier();
    // for (ModelElement me : supplier) {
    // if (me instanceof Analysis) {
    // Analysis analysis = (Analysis) me;
    // TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(report,
    // analysis);
    // if (dependencyReturn.isOk()) {
    // RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(analysis);
    // if (repositoryNode != null) {
    // TDQAnalysisItem analysisItem = (TDQAnalysisItem) repositoryNode.getObject().getProperty()
    // .getItem();
    // analysisItem.setAnalysis(analysis);
    // }
    // ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
    // .saveResource(analysis.eResource());
    // }
    // }
    // }
    // }
    // } catch (PersistenceException e) {
    // log.error(e, e);
    // }
    //
    // }
}

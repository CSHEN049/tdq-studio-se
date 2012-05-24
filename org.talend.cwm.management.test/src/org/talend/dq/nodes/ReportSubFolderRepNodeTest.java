// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend �C www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.dq.nodes;

// import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.sqlexplorer.service.GlobalServiceRegister;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.nodes.ReportSubFolderRepNode.ReportSubFolderType;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
// @RunWith(PowerMockRunner.class)
@PrepareForTest({ ReportUtils.class, ResourceFileMap.class, GlobalServiceRegister.class, IExtensionRegistry.class,
        IConfigurationElement.class, ProjectManager.class, CoreRuntimePlugin.class })
public class ReportSubFolderRepNodeTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReportSubFolderRepNode reportSubRepNode;

    private Report report;

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        reportSubRepNode = new ReportSubFolderRepNode(null, null, ENodeType.REPOSITORY_ELEMENT);
        reportSubRepNode.setFiltering(false);
        report = mock(Report.class);
        reportSubRepNode.setReport(report);

    }

    /**
     * DOC qiongli Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getChildren()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetChildren() throws Exception {
        reportSubRepNode.setReportSubFolderType(ReportSubFolderType.GENERATED_DOCS);
        mockForGetChildren();
        List<IRepositoryNode> children = reportSubRepNode.getChildren();
        assertFalse(children.isEmpty());
        assertTrue(children.size() == 3);
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getLabel()}.
     */
    @Test
    public void testGetLabel() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#isVirtualFolder()}.
     */
    @Test
    public void testIsVirtualFolder() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getReportSubFolderType()}.
     */
    @Test
    public void testGetReportSubFolderType() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dq.nodes.ReportSubFolderRepNode#setReportSubFolderType(org.talend.dq.nodes.ReportSubFolderRepNode.ReportSubFolderType)}
     * .
     */
    @Test
    public void testSetReportSubFolderType() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getReport()}.
     */
    @Test
    public void testGetReport() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dq.nodes.ReportSubFolderRepNode#setReport(orgomg.cwmx.analysis.informationreporting.Report)}.
     */
    @Test
    public void testSetReport() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getReportSubFolderChildren()}.
     */
    @Test
    public void testGetReportSubFolderChildren() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dq.nodes.ReportSubFolderRepNode#ReportSubFolderRepNode(org.talend.core.model.repository.IRepositoryViewObject, org.talend.repository.model.RepositoryNode, org.talend.repository.model.IRepositoryNode.ENodeType)}
     * .
     */
    @Test
    public void testReportSubFolderRepNode() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getCount()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCount_1() throws Exception {
        // ReportSubFolderRepNode reportSubRepNode = mock(ReportSubFolderRepNode.class);
        reportSubRepNode.setReportSubFolderType(ReportSubFolderType.GENERATED_DOCS);
        mockForGetChildren();
        String count = reportSubRepNode.getCount();
        count = count.replace("(", "").replace(")", "");
        assertTrue(Integer.parseInt(count) == 3);

    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getCount()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCount_2() throws Exception {
        List<IRepositoryNode> ls = new ArrayList<IRepositoryNode>();
        IRepositoryNode repositoryNode = mock(IRepositoryNode.class);
        ls.add(repositoryNode);
        reportSubRepNode.getReportSubFolderChildren().addAll(ls);

        String count = reportSubRepNode.getCount();
        count = count.replace("(", "").replace(")", "");
        assertTrue(Integer.parseInt(count) == 1);

    }

    private void mockForGetChildren() throws Exception {
        IResource[] res = new IResource[3];
        for (int i = 0; i < 3; i++) {
            IFile fe = mock(IFile.class);
            when(fe.getFullPath()).thenReturn(new Path(""));
            res[i] = fe;
        }
        PowerMockito.mockStatic(ReportUtils.class);
        IFile repFile = mock(IFile.class);
        when(ReportUtils.getReportListFiles(repFile)).thenReturn(res);
        PowerMockito.mockStatic(ResourceFileMap.class);
        when(ResourceFileMap.findCorrespondingFile(report)).thenReturn(repFile);

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager projManager = mock(ProjectManager.class);
        when(projManager.getProjectNode("")).thenReturn(null);
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        CoreRuntimePlugin coreRunPlugin = mock(CoreRuntimePlugin.class);
        when(CoreRuntimePlugin.getInstance()).thenReturn(coreRunPlugin).thenReturn(coreRunPlugin).thenReturn(coreRunPlugin);
        when(ProjectManager.getInstance()).thenReturn(projManager).thenReturn(projManager).thenReturn(projManager);
        replayAll();
    }

}

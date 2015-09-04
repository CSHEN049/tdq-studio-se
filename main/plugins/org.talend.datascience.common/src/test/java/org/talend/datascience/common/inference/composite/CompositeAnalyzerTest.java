package org.talend.datascience.common.inference.composite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.classifier.SemanticCategoryEnum;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.AnalyzerTest;
import org.talend.datascience.common.inference.Analyzers;
import org.talend.datascience.common.inference.semantic.SemanticAnalyzer;
import org.talend.datascience.common.inference.semantic.SemanticType;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.DataTypeAnalyzer;

public class CompositeAnalyzerTest extends AnalyzerTest {

    Analyzer<Analyzers.Result> analyzer = null;

    @Before
    public void setUp() throws Exception {
        final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
        final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .setMode(CategoryRecognizerBuilder.Mode.LUCENE);
        analyzer = Analyzers.with(new DataTypeAnalyzer(), new SemanticAnalyzer(builder));
    }

    @After
    public void tearDown() throws Exception {
        analyzer.end();
    }

    @Test
    public void testDataTypeAndSemantic() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_100.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<Analyzers.Result> result = analyzer.getResult();
        assertEquals(18, result.size());
        // Composite result assertions (there should be a DataType and a SemanticType)
        for (Analyzers.Result columnResult : result) {
            assertNotNull(columnResult.get(DataType.class));
            assertNotNull(columnResult.get(SemanticType.class));
        }
        // Data type assertions
        assertEquals(DataType.Type.INTEGER, result.get(0).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(1).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(2).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(3).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(4).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(5).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(6).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(7).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(8).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(9).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(10).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(11).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.DOUBLE, result.get(12).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(13).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(14).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(15).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(16).get(DataType.class).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(17).get(DataType.class).getSuggestedType());
        // Semantic types assertions
        String[] expectedCategories = new String[] { "", //
                "", //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                SemanticCategoryEnum.FIRST_NAME.getId(), //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "", //
                SemanticCategoryEnum.GENDER.getId(), //
                "" //
        };
        for (int i = 0; i < expectedCategories.length; i++) {
            assertEquals(expectedCategories[i], result.get(i).get(SemanticType.class).getSuggestedCategory());
        }
    }
}

// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.standardization.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.standardization.constant.PluginConstant;
import org.talend.dataquality.standardization.index.IndexBuilder;
import org.talend.dataquality.standardization.query.FirstNameStandardize;

/**
 * DOC klliu class global comment.
 */
public class HandleLuceneImpl implements HandleLucene {

    private final int hitsPerPage = 10;


    private Map<String, String[]> hits = new HashMap<String, String[]>();

    private ArrayList<String> soreDoc = null;

    /**
     * Input filename to be indexed once for all and indexfolder to store the files of indexing.
     * 
     * @param filename
     * @param indexfolder
     * @return
     */
    public boolean createIndex(String filename, String indexfolder) {
        IndexBuilder idxBuilder = getIndexBuilder(indexfolder);
        int[] columnsToBeIndexed = new int[] { 0, 1, 2, 3 };
        try {
            idxBuilder.initializeIndex(filename, columnsToBeIndexed);
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String getReplaceSearchResult(String folderName, String inputName, Map<String, String> information2value,
            boolean fuzzyQuery) throws IOException, ParseException {
        String result = null;
        if (inputName != null && folderName != null) {
            IndexSearcher searcher = getIndexSearcher(folderName);
            Analyzer searchAnalyzer = getAnalyzer();
            FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, hitsPerPage);
            String countryText = null;
            String genderText = null;
            Set<String> indexKinds = information2value.keySet();
            for (String indexKind : indexKinds) {
                if (indexKind.equals(PluginConstant.FIRST_NAME_STANDARDIZE_COUNTRY)) {
                    countryText = information2value.get(indexKind);
                } else if (indexKind.equals(PluginConstant.FIRST_NAME_STANDARDIZE_GENDER)) {
                    genderText = information2value.get(indexKind);
                }
            }
            if (countryText == null && genderText == null) {
                try {
                    result = stdname.replaceName(inputName, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (countryText != null && genderText != null) {
                try {
                    result = stdname.replaceNameWithCountryGenderInfo(inputName, countryText, genderText, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (countryText != null && genderText == null) {
                try {
                    result = stdname.replaceNameWithCountryInfo(inputName, countryText, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (countryText == null && genderText != null) {
                try {
                    result = stdname.replaceNameWithGenderInfo(inputName, genderText, fuzzyQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return result;

    }
    public Map<String, String[]> getSearchResult(String folderName, String inputName, Map<String, String> information2value,
            boolean fuzzyQuery) throws IOException, ParseException {
        IndexSearcher searcher = getIndexSearcher(folderName);

        Analyzer searchAnalyzer = getAnalyzer();

        FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, hitsPerPage);

        ScoreDoc[] docs;
        try {
            docs = stdname.standardize(inputName, information2value, fuzzyQuery);
            treatSearchResult(searcher, inputName, docs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        searcher.close();

        return getHits();
    }

    private void treatSearchResult(IndexSearcher searcher, String inputName, ScoreDoc[] docs) {
        soreDoc = new ArrayList<String>();
        for (int i = 0; i < docs.length; ++i) {
            int docId = docs[i].doc;
            Document d = null;
            try {
                d = searcher.doc(docId);
                String name = d.get("name");
                soreDoc.add(name);
            } catch (CorruptIndexException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        String[] resultArray = new String[soreDoc.size()];
        hits.put(inputName, soreDoc.toArray(resultArray));

    }



    private Map<String, String[]> getHits() {
        return hits;
    }

    private Analyzer getAnalyzer() {
        return new StandardAnalyzer(Version.LUCENE_30);
    }

    private IndexBuilder getIndexBuilder(String folderName) {
        return new IndexBuilder(folderName);
    }

    private IndexSearcher getIndexSearcher(String folderName) {
        Directory dir = null;
        IndexSearcher is = null;
        try {
            dir = FSDirectory.open(new File(folderName));
            is = new IndexSearcher(dir);
        } catch (CorruptIndexException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return is;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.standardization.main.HandleLucene#replaceName(java.lang.String, java.lang.String,
     * boolean)
     */
    public String replaceName(String folderName, String inputName, boolean fuzzyQuery) throws ParseException, IOException {
        String result = null;
        IndexSearcher searcher = getIndexSearcher(folderName);
        Analyzer searchAnalyzer = getAnalyzer();
        FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, hitsPerPage);
        result = stdname.replaceName(inputName, fuzzyQuery);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.standardization.main.HandleLucene#replaceNameWithCountryGenderInfo(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public String replaceNameWithCountryGenderInfo(String folderName, String inputName, String inputCountry, String inputGender,
            boolean fuzzyQuery) throws Exception {
        String result = null;
        IndexSearcher searcher = getIndexSearcher(folderName);
        Analyzer searchAnalyzer = getAnalyzer();
        FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, hitsPerPage);
        result = stdname.replaceNameWithCountryGenderInfo(inputName, inputCountry, inputGender, fuzzyQuery);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.standardization.main.HandleLucene#replaceNameWithCountryInfo(java.lang.String,
     * java.lang.String, java.lang.String, boolean)
     */
    public String replaceNameWithCountryInfo(String folderName, String inputName, String inputCountry, boolean fuzzyQuery)
            throws Exception {
        String result = null;
        IndexSearcher searcher = getIndexSearcher(folderName);
        Analyzer searchAnalyzer = getAnalyzer();
        FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, hitsPerPage);
        result = stdname.replaceNameWithCountryInfo(inputName, inputCountry, fuzzyQuery);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.standardization.main.HandleLucene#replaceNameWithGenderInfo(java.lang.String,
     * java.lang.String, java.lang.String, boolean)
     */
    public String replaceNameWithGenderInfo(String folderName, String inputName, String inputGender, boolean fuzzyQuery)
            throws Exception {
        String result = null;
        IndexSearcher searcher = getIndexSearcher(folderName);
        Analyzer searchAnalyzer = getAnalyzer();
        FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, hitsPerPage);
        result = stdname.replaceNameWithGenderInfo(inputName, inputGender, fuzzyQuery);
        return result;
    }

}

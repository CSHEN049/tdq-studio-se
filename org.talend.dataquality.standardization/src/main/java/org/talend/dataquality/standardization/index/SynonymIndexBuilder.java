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
package org.talend.dataquality.standardization.index;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.talend.dataquality.standardization.i18n.Messages;

/**
 * @author scorreia A class to create an index with synonyms.
 */
/**
 * DOC sizhaoliu class global comment. Detailled comment
 */
public class SynonymIndexBuilder {

    public static final String F_WORD = "word";//$NON-NLS-1$

    public static final String F_SYN = "syn";//$NON-NLS-1$

    private Directory indexDir;

    /**
     * Default synonym separator is '|'.
     */
    private char separator = '|';

    private Analyzer analyzer;

    private IndexWriter writer;

    private final Error error = new Error();

    /**
     * SynonymIndexBuilder constructor.
     */
    public SynonymIndexBuilder() {
    }

    /**
     * Method "getError".
     * 
     * @return the last error
     */
    public Error getError() {
        return this.error;
    }

    /**
     * set a separator for a string which contains synonyms.
     * 
     * @param synonymSeparator
     */
    public void setSynonymSeparator(char synonymSeparator) {
        this.separator = synonymSeparator;
    }

    // FIXME not used yet. Need to be implemented
    // public void initIndexInRAM() {
    // indexDir = new RAMDirectory();
    // }

    /**
     * Method "initIndexInFS" initializes the lucene index folder.
     * 
     * @param path the path of the index (will be created if it does not exist)
     */
    public void initIndexInFS(String path) {

        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            indexDir = FSDirectory.open(file);
        } catch (IOException e) {
            error.set(false, Messages.getString("SynonymIndexBuilder.failLoad"));//$NON-NLS-1$
            // e.printStackTrace();
        }
    }

    /**
     * insert an entire document into index.
     * 
     * @param word the reference word: must not be null
     * @param synonyms the list of synonyms separated by the separator (can be null)
     * @throws IOException
     */
    public void insertDocument(String word, String synonyms) throws IOException {
        // insert document without duplication verification
        getWriter().addDocument(generateDocument(word, synonyms));
    }

    /**
     * insert an entire document into index if it does not already exists.
     * 
     * @param word the reference string
     * @param synonyms the synonyms (can be null)
     * @return true if inserted, false otherwise
     * @throws IOException
     */
    public boolean insertDocumentIfNotExists(String word, String synonyms) throws IOException {
        if (searchDocumentByWord(word).totalHits == 0) {
            getWriter().addDocument(generateDocument(word, synonyms));
            return true;
        } // else
        error.set(false, Messages.getString("SynonymIndexBuilder.aDocument", word.trim()));//$NON-NLS-1$
        return false;
    }

    /**
     * Update an entire synonym document if and only if it exists and it's unique.
     * 
     * WARNING If some changes in the index are not committed, this may cause trouble to find the document to update.
     * Make sure that a commit has been done before calling this method except if you know exactly what you are doing.
     * 
     * WARNING! Beware that if several documents match the word, nothing will be done.
     * 
     * @param word the reference word
     * @param synonyms the list of synonyms (can be null)
     * @throws IOException
     */
    public int updateDocument(String word, String synonyms) throws IOException {
        int nbUpdatedDocuments = 0;
        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            break;
        case 1:
            getWriter().updateDocument(new Term(F_WORD, word.trim()), generateDocument(word, synonyms));
            nbUpdatedDocuments = 1;
            break;
        default:
            nbUpdatedDocuments = -1;// to avoid insertion by the component when nbUpdatedDocuments == 0
            error.set(false, Messages.getString("SynonymIndexBuilder.documents", docs.totalHits, word));//$NON-NLS-1$
            break;
        }
        return nbUpdatedDocuments;

    }

    /**
     * delete an entire document by word.
     * 
     * @param word
     * @throws IOException
     */
    public int deleteDocumentByWord(String word) throws IOException {
        TopDocs docs = searchDocumentByWord(word);
        switch (docs.totalHits) {
        case 0:
            error.set(false, Messages.getString("SynonymIndexBuilder.doesnotExsit", word));//$NON-NLS-1$
            return 0;
        case 1:
            getWriter().deleteDocuments(new Term(F_WORD, word.trim()));
            // System.out.println("The document named <" + word + "> has been deleted.");
            return 1;
        default:
            error.set(false, Messages.getString("SynonymIndexBuilder.documents", docs.totalHits, word));//$NON-NLS-1$
            break;
        }
        return 0;
    }

    /**
     * delete all synonym documents.
     * 
     * @throws IOException
     */
    public void deleteAllDocuments() throws IOException {
        getWriter().deleteAll();
        // getWriter().commit();
    }

    /**
     * Add a synonym to an existing document. If several documents are found given the input word, nothing is done. If
     * the synonym is null, nothing is done.
     * 
     * @param word a word (must not be null)
     * @param newSynonym the new synonym to add to the list of synonyms
     * @return 1 if added or 0 if no change has been done
     * @throws IOException
     */
    public int addSynonymToDocument(String word, String newSynonym) throws IOException {
        assert word != null;
        if (newSynonym == null) {
            return 0;
        }
        // trim synonym
        newSynonym = newSynonym.trim();
        if (newSynonym.length() == 0) {
            return 0;
        }

        // reuse related synonym index search instead of created a new search
        SynonymIndexSearcher idxSearcher = getNewSynIdxSearcher();
        TopDocs docs = idxSearcher.searchDocumentByWord(word);

        int nbDocs = 0;
        if (docs.totalHits == 1) { // don't do anything if several documents match
            Document doc = idxSearcher.getDocument(docs.scoreDocs[0].doc);

            // search if synonym exists
            boolean synExists = false;
            String[] synonyms = doc.getValues(F_SYN);
            for (String str : synonyms) {
                if (str.toLowerCase().equals(newSynonym.toLowerCase())) {
                    error.set(false, Messages.getString("SynonymIndexBuilder.synonym", newSynonym, str));//$NON-NLS-1$
                    // FIXME should the synonym be rejected when an equivalent one already exists in the document?
                    synExists = true;
                    break;
                }
            }
            // create a new document and replace the original one if synonym does not exist
            if (!synExists) {
                doc.add(createSynField(newSynonym));
                getWriter().updateDocument(new Term(F_WORD, word), doc);
                // System.out.println("The synonym <" + newSynonym + "> is added to word.");
                nbDocs = 1;
            }
        } else {
            if (docs.totalHits == 0) {
                error.set(false, Messages.getString("SynonymIndexBuilder.document", word));//$NON-NLS-1$
            } else {
                error.set(false, Messages.getString("SynonymIndexBuilder.documents", docs.totalHits, word));//$NON-NLS-1$
            }
        }
        // FIXME avoid use of idxSearcher?
        idxSearcher.close();
        return nbDocs;
    }

    /**
     * remove a synonym from the document to which it belongs.
     * 
     * @param synonymToDelete
     * @return the number of deleted synonyms
     * @throws IOException
     */
    public int removeSynonymFromDocument(String word, String synonymToDelete) throws IOException {
        assert word != null;
        if (synonymToDelete == null) {
            error.set(false, Messages.getString("SynonymIndexBuilder.theSynonym", word));//$NON-NLS-1$
            return 0;
        }
        if (synonymToDelete.toLowerCase().equals(word.toLowerCase())) {
            error.set(false, Messages.getString("SynonymIndexBuilder.synonymToDelete", synonymToDelete, word));//$NON-NLS-1$
            return 0;
        }
        int deleted = 0;

        SynonymIndexSearcher newSynIdxSearcher = getNewSynIdxSearcher();
        TopDocs docs = newSynIdxSearcher.searchDocumentByWord(word);
        if (docs.totalHits == 1) { // don't do anything if more than one document is found
            Document doc = newSynIdxSearcher.getDocument(docs.scoreDocs[0].doc);
            String[] synonyms = doc.getValues(F_SYN);
            Set<String> synonymList = new HashSet<String>();

            for (String str : synonyms) {
                if (str.equals(word)) {
                    // do nothing. because the word will be added to the document
                    // automatically in the method generateDocument().
                } else if (str.toLowerCase().equals(synonymToDelete.toLowerCase())) {
                    // we don't require the synonymToDelete to be case sensitive.
                    // System.out.println("The synonym <" + synonymToDelete + "> is removed from the word.");
                    deleted++;
                } else {
                    synonymList.add(str);
                }
            }

            // if the value of deleted is 0, we can know that the synonymToDelete doesn't exist
            if (deleted == 0) {
                error.set(false, Messages.getString("SynonymIndexBuilder.synonymNotExsit", synonymToDelete));//$NON-NLS-1$
            } else {
                Document newDoc = generateDocument(word, synonymList);
                getWriter().updateDocument(new Term(F_WORD, word), newDoc);
            }

        } else {
            error.set(false, Messages.getString("SynonymIndexBuilder.documentNotExsit", word));//$NON-NLS-1$
            deleted = 0;
        }
        newSynIdxSearcher.close();
        return deleted;
    }

    /**
     * Method "deleteIndexFromFS".
     * 
     * @param path the path of the index
     * @return true if the path is deleted (and if the path did not exist)
     */
    public boolean deleteIndexFromFS(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            // folder does not exist. can create an index without deleting.
            return true;
        }

        if (folder.isDirectory()) {
            File[] filelist = folder.listFiles();
            if (filelist.length == 0) {// folder is empty
                if (!folder.delete()) {
                    error.set(false, Messages.getString("SynonymIndexBuilder.couldNotDelete", folder.getAbsolutePath()));//$NON-NLS-1$
                    return false;
                }
            } else {
                Status status = null;
                FSDirectory directory = null;
                try {
                    directory = FSDirectory.open(folder);
                    CheckIndex check = new CheckIndex(directory);
                    status = check.checkIndex();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    directory.close();
                }
                boolean allDeleted = true;
                if (status == null || status.missingSegments) {
                    error.set(false, Messages.getString("SynonymIndexBuilder.notAnIndexFolder", folder.getAbsolutePath()));//$NON-NLS-1$
                    return false;
                } else {// an index already exists in folder
                    for (File f : filelist) {
                        if (!f.delete() && allDeleted) {
                            allDeleted = false;
                        }
                    }
                    if (allDeleted && !folder.delete()) {
                        allDeleted = false;
                    }
                    if (!allDeleted) {
                        error.set(false, Messages.getString("SynonymIndexBuilder.couldNotDelete", folder.getAbsolutePath()));//$NON-NLS-1$
                        return false;
                    }
                }
            }
        } else {// folder is a file
            error.set(false, Messages.getString("SynonymIndexBuilder.pathIsFile", folder.getAbsolutePath()));//$NON-NLS-1$
            return false;
        }
        return true;
    }

    /**
     * ADDED BY ytao 2011/02/11 If only need to initialize the index, do nothing after fold open, but just invoke this
     * method at the end, index will be reset.
     * 
     * (Ensure that usingCreateMode is true) // where is it ensured? who wrote this sentence?
     * 
     * Not sure that the index is deleted and recreated, may be just delete all documents of index since the index files
     * are "_1a.cfs" and "segments.gen" and "segments_1e" currently, if these files are not exists, API will not work.
     * 
     * ADDED by sizhaoliu : usingCreateMode is not used any more. we now have a separated SynonymIndexSearcher.
     */
    public void closeIndex() {
        try {
            this.getWriter().optimize();
            this.getWriter().close();
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Commits all pending changes.
     */
    public void commit() {
        try {
            this.getWriter().commit();
        } catch (CorruptIndexException e) {
            error.set(false, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            error.set(false, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Getter for analyzer.
     * 
     * @return the analyzer
     * @throws IOException
     */
    public Analyzer getAnalyzer() throws IOException {
        if (analyzer == null) {
            // the entry and the synonyms are indexed as provided
            // analyzer = new KeywordAnalyzer();

            // most used analyzer in lucene
            analyzer = new StandardAnalyzer(Version.LUCENE_30);

            // analyzer = new SynonymAnalyzer();
        }
        return this.analyzer;
    }

    /**
     * Getter for writer.
     * 
     * @return the writer
     * @throws IOException
     * @throws
     */
    IndexWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new IndexWriter(indexDir, this.getAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        }
        return this.writer;
    }

    /**
     * Method "getNumDocs".
     * 
     * @return the number of documents or -1 if an error happened
     */
    public int getNumDocs() {
        try {
            return this.getWriter().numDocs();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Get a new read-only searcher at each call.
     * 
     * @return
     * @throws CorruptIndexException
     * @throws IOException
     */
    private IndexSearcher getNewIndexSearcher() throws IOException {
        // FIXME optimization could be done if we use the IndexReader.reopen() method instead of creating a new object
        // at each call.

        CheckIndex check = new CheckIndex(indexDir);
        Status status = check.checkIndex();
        if (status.missingSegments) {
            System.err.println(Messages.getString("SynonymIndexBuilder.print"));//$NON-NLS-1$
        }
        return new IndexSearcher(indexDir);
    }

    private SynonymIndexSearcher getNewSynIdxSearcher() throws IOException {
        return new SynonymIndexSearcher(getNewIndexSearcher());
    }

    private Document generateDocument(String word, String synonyms) {
        // System.out.println("\t Generating doc for " + word + " and " + synonyms);
        String[] split = synonyms == null ? new String[0] : StringUtils.split(synonyms, separator);
        Set<String> set = new HashSet<String>();
        Collections.addAll(set, split);
        return generateDocument(word, set);
    }

    /**
     * generate a document.
     * 
     * @param word
     * @param synonyms
     * @return
     */
    private Document generateDocument(String word, Set<String> synonyms) {
        Document doc = new Document();
        word = word.trim();
        Field field = new Field(F_WORD, word, Field.Store.YES, Field.Index.NOT_ANALYZED, TermVector.NO);
        field.setBoost(1.5F); // increase the importance of the reference word
        doc.add(field);
        // --- store entry also in synonym list so that we can search for it too
        // without the need to search in the word field (will be tokenized given the analyzer)
        boolean addWordInSyn = true;
        if (synonyms != null) {
            for (String syn : synonyms) {
                if (syn != null) {
                    syn = syn.trim();
                    if (syn.length() > 0) {
                        // add only non empty synonyms
                        doc.add(createSynField(syn));
                        if (syn.equals(word)) {
                            addWordInSyn = false;
                        }
                    }
                }
            }
        }
        if (addWordInSyn) {
            doc.add(createSynField(word));
        }
        return doc;
    }

    private Field createSynField(String synonym) {
        return new Field(F_SYN, synonym, Field.Store.YES, Field.Index.ANALYZED, TermVector.YES);
    }

    /**
     * search a document by the word. use only inside the builder.
     * 
     * @param word
     * @return
     * @throws IOException
     */
    private TopDocs searchDocumentByWord(String word) throws IOException {
        TopDocs docs = null;
        // FIXME can we avoid the creation of a new searcher (use IndexReader.reopen?)
        SynonymIndexSearcher newSynIdxSearcher = getNewSynIdxSearcher();
        docs = newSynIdxSearcher.searchDocumentByWord(word);
        newSynIdxSearcher.close();
        return docs;
    }

}

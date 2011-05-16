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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.pattern.PatternToExcelEnum;

import com.csvreader.CsvReader;

/**
 * CsvFileTableViewer is a table viewer for preview a simple csv file.
 */
public class CsvFileTableViewer extends Composite {

    private static final char CURRENT_SEPARATOR = '\t';

    private boolean useTextQualifier = true;

    private static final Image WARN_IMG = ImageLib.getImage(ImageLib.LEVEL_WARNING);

    private CsvReader reader;

    private TableViewer viewer;

    private boolean quotesError = false;

    private boolean hasPatternHeaders = false;

    /**
     * DOC yyi CsvFileTableViewer class global comment. Detailled comment
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     */
    class ViewContentProvider implements IStructuredContentProvider {

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {

            CsvReader csvReader = (CsvReader) parent;

            List<Object> rows = new ArrayList<Object>();

            try {
                while (csvReader.readRecord()) {
                    rows.add(csvReader.getValues());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return rows.toArray(new Object[rows.size()]);
        }
    }

    /**
     * DOC yyi CsvFileTableViewer class global comment. Detailled comment
     */
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object obj, int index) {
            String[] values = (String[]) obj;
            return index < values.length ? values[index] : null;
        }

        public Image getColumnImage(Object obj, int index) {
            String[] values = (String[]) obj;
            return index < values.length ? getImage(values[index]) : null;
        }

        public Image getImage(Object obj) {
            if (!checkQuoteMarks(obj.toString())) {
                quotesError = true;
                return WARN_IMG;
            } else {
                return null;
            }
        }
    }

    public CsvFileTableViewer(final Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout());
        createView(this);
        viewer.setInput(reader);
    }

    private void createHeader(String[] headers) {
        int columnCount = headers.length;
        TableColumn[] columns = viewer.getTable().getColumns();
        for (TableColumn column : columns) {
            column.dispose();
        }
        for (int i = 0; i < columnCount; i++) {
            TableColumn tableColumn = new TableColumn(viewer.getTable(), SWT.NONE);
            tableColumn.setText(headers[i]);
            tableColumn.pack();
        }
    }

    private void createView(Composite parent) {
        viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 100;
        viewer.getTable().setLayoutData(data);
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
    }

    private boolean checkQuoteMarks(String text) {
        if (0 == text.length())
            return true;

        if ('\"' == text.charAt(0) && '\"' == text.charAt(text.length() - 1))
            return true;

        if ('\"' != text.charAt(0) && '\"' != text.charAt(text.length() - 1))
            return true;

        return false;
    }

    private boolean checkFileHeader(String[] headers) {

        List<String> patternEnum = new ArrayList<String>();
        for (PatternToExcelEnum tmpEnum : PatternToExcelEnum.values()) {
            patternEnum.add(tmpEnum.getLiteral());
        }

        for (String header : headers) {
            if (!patternEnum.contains(trimQuote(header)))
                return false;
        }
        return true;
    }

    private String trimQuote(String text) {
        if (text.length() < 2)
            return text;

        int beginLen = 0;
        int endLen = text.length();

        if ('\"' == text.charAt(beginLen) && '\"' == text.charAt(endLen - 1)) {
            return text.substring(1, endLen - 1);
        }

        return text;
    }

    /**
     * set a csv file.
     * 
     * @param csvFile
     * @return whether the file be loaded.
     */
    public boolean setCsvFile(File csvFile) {
        quotesError = false;
        hasPatternHeaders = false;
        try {
            reader = new CsvReader(new FileReader(csvFile), CURRENT_SEPARATOR);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return false;
        }
        reader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        reader.setUseTextQualifier(useTextQualifier);
        try {
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            hasPatternHeaders = checkFileHeader(headers);
            createHeader(headers);

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        viewer.setInput(reader);
        return true;
    }

    /**
     * Returns <code>true</code> if the file contains a patterns header.
     * 
     * @return whether the file contains a patterns header.
     */
    public boolean isHeadersInvalid() {
        return !hasPatternHeaders;
    }

    /**
     * Returns <code>true</code> if Use Text Qualifier.
     * 
     * @return whether Use Text Qualifier
     */
    public boolean isUseTextQualifier() {
        return useTextQualifier;
    }

    /**
     * Sets the csv reader use text qualifier if the argument is <code>true</code>.
     * 
     * @param useTextQualifier
     */
    public void setUseTextQualifier(boolean useTextQualifier) {
        this.useTextQualifier = useTextQualifier;
    }

    /**
     * Returns <code>true</code> if the file contains quotes error value.
     * 
     * @return
     */
    public boolean isQuotesError() {
        return quotesError;
    }
}

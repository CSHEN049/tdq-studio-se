// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.help;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum BookMarkEnum {
    MySQLRegular("http://dev.mysql.com/doc/refman/5.0/en/regexp.html", "MySQL Regular Expressions"), //$NON-NLS-1$ //$NON-NLS-2$
    OracleRegular("http://www.oracle.com/technology/obe/obe10gdb/develop/regexp/regexp.htm", "Oracle Regular Expressions"), //$NON-NLS-1$ //$NON-NLS-2$
    SQLServer2005Regular("http://msdn.microsoft.com/en-us/magazine/cc163473.aspx", "SQL Server 2005 Regular Expressions"), //$NON-NLS-1$ //$NON-NLS-2$
    PostgreSQLRegular("http://www.postgresql.org/docs/current/static/functions-matching.html", "PostgreSQL Regular Expressions"), //$NON-NLS-1$ //$NON-NLS-2$
    BoxPlot("http://en.wikipedia.org/wiki/Box_plot", "Box Plot Graphic"), //$NON-NLS-1$ //$NON-NLS-2$
    EclipseSQLExplorer("http://eclipsesql.sourceforge.net/index.php", "Eclipse SQL Explorer"), //$NON-NLS-1$ //$NON-NLS-2$
    TOSDownloadPage("http://www.talend.com/download.php", "Talend.com Download Page"), //$NON-NLS-1$ //$NON-NLS-2$
    TOSForum("http://www.talendforge.org/forum/index.php", "Talend.com Forum"), //$NON-NLS-1$ //$NON-NLS-2$
    TOSBugtracker("http://www.talendforge.org/bugs/my_view_page.php", "Talend.com Bugtracker"); //$NON-NLS-1$ //$NON-NLS-2$

    private String href;

    private String label;

    public String getHref() {
        return href;
    }

    public String getLabel() {
        return label;
    }

    private BookMarkEnum(String href, String label) {
        this.href = href;
        this.label = label;
    }

    private static final BookMarkEnum[] VALUES_ARRAY = new BookMarkEnum[] { MySQLRegular, OracleRegular, SQLServer2005Regular,
            PostgreSQLRegular, BoxPlot, EclipseSQLExplorer, TOSDownloadPage, TOSForum, TOSBugtracker };

    public static final List<BookMarkEnum> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
}

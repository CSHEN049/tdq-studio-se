//============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//============================================================================
package org.talend.dq.dbms;

import org.talend.utils.ProductVersion;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class HiveDbmsLanguage extends DbmsLanguage {

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     */
    public HiveDbmsLanguage() {
        super(DbmsLanguage.HIVE);
    }

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     */
    public HiveDbmsLanguage(String dbmsType) {
        super(dbmsType);
    }

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param dbVersion
     */
    public HiveDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    public String toQualifiedName(String catalog, String schema, String table) {
        return super.toQualifiedName(null, null, table);
    }

}

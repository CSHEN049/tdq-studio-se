// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.ecos.service;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.JavaTypeMapper;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Version;
import org.talend.dataprofiler.ecos.EcosPlugin;
import org.talend.dataprofiler.ecos.model.RevisionInfo;
import org.talend.dataprofiler.ecos.model.VersionInfo;
import org.talend.dataprofiler.ecos.model.impl.Revision;
import org.talend.dataprofiler.ecos.proxy.EcosystemSocketFactory;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class EcosystemService {

    public static String VERSION_LIST_URL = "http://talendforge.org/exchange/top/api/get_version_list.php"; //$NON-NLS-1$

    public static String REVISION_LIST_URL = "http://talendforge.org/exchange/top/api/get_revision_list.php"; //$NON-NLS-1$

    private static Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.(RC|M)\\d+)?_r\\d+"); //$NON-NLS-1$

    private static Pattern DEFAULT_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.*(\\d*)"); //$NON-NLS-1$

    private static MultiValueMap versionMap = new MultiValueMap();

    static {
        System.setProperty("axis.socketFactory", EcosystemSocketFactory.class.getName());
    }

    /**
     * Make sure that the version match x.x.x or x.x.xMx or x.x.xRCx, where x are all digit.
     * 
     * @param version
     * @return
     */
    public static String normalizeVersion(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            String str = version.substring(0, version.indexOf("_r"));
            return str.replaceAll("\\.RC", "RC").replaceAll("\\.M", "M");
        } else {
            // try again, ignore M, RC
            matcher = DEFAULT_PATTERN.matcher(version);
            matcher.find();
            return matcher.group();
        }
    }

    @SuppressWarnings("unchecked")
    public static String[] getBranch(String version) {
        version = getMainVersion(version);
        if (versionMap.isEmpty()) {
            getVersionList();
        }
        Collection<String> branch = versionMap.getCollection(version);
        return branch.toArray(new String[branch.size()]);
    }

    public static String getMainVersion(String version) {
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+).*");
        Matcher matcher = pattern.matcher(version);
        if (matcher.matches()) {
            version = matcher.group(1);
        }
        return version;
    }

    public static String[] getVersionList() {
        versionMap.clear();
        try {
            String jsonContent = sendGetRequest(VERSION_LIST_URL);
            List<VersionInfo> list = parseJsonObject(jsonContent, VersionInfo.class);

            Pattern pattern = Pattern.compile("(\\d+\\.\\d+).*");
            for (VersionInfo info : list) {
                String name = info.getName();
                Matcher matcher = pattern.matcher(name);
                if (matcher.matches()) {
                    versionMap.put(matcher.group(1), String.valueOf(info.getId()));
                }
            }
            // sort version
            List<String> versions = new ArrayList<String>(versionMap.keySet());
            Collections.sort(versions, new Comparator<String>() {

                public int compare(String o1, String o2) {
                    Version ver1 = new Version(o1);
                    Version ver2 = new Version(o2);
                    return ver2.compareTo(ver1);
                }

            });
            return versions.toArray(new String[versions.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static List parseJsonObject(String jsonContent, Class clazz) throws Exception {
        // need factory for creating parser to use
        JsonFactory jf = new JsonFactory();
        List result = (List) new JavaTypeMapper().read(jf.createJsonParser(new StringReader(jsonContent)));
        List objList = new ArrayList(result.size());
        for (int i = 0; i < result.size(); i++) {
            Object obj = clazz.newInstance();
            Object source = result.get(i);
            BeanUtils.copyProperties(obj, source);
            objList.add(obj);
        }
        return objList;
    }

    public static String sendGetRequest(String urlAddress) throws Exception {
        HttpClient httpclient = new HttpClient();
        GetMethod getMethod = new GetMethod(urlAddress);
        httpclient.executeMethod(getMethod);
        String response = getMethod.getResponseBodyAsString();
        getMethod.releaseConnection();
        return response;
    }

    public static String sendPostRequest(String urlAddress, Map<String, String> parameters) throws Exception {
        HttpClient httpclient = new HttpClient();
        PostMethod postMethod = new PostMethod(urlAddress);
        if (parameters != null) {
            NameValuePair[] postData = new NameValuePair[parameters.size()];
            int i = 0;
            for (String key : parameters.keySet()) {
                String value = parameters.get(key);
                postData[i++] = new NameValuePair(key, value);
            }
            postMethod.addParameters(postData);
        }

        httpclient.executeMethod(postMethod);
        String response = postMethod.getResponseBodyAsString();
        postMethod.releaseConnection();
        return response;
    }

    /**
     * Return true if revision1 is newer than revision2.
     * 
     * @param revision1
     * @param revision2
     * @return
     */
    public static boolean isRevisionNewerThan(Revision revision1, Revision revision2) {
        String[] rev1 = revision1.getName().split("\\.");
        String[] rev2 = revision2.getName().split("\\.");
        for (int i = 0; i < rev1.length && i < rev2.length; i++) {
            int a = Integer.parseInt(rev1[i]);
            int b = Integer.parseInt(rev2[i]);
            if (a == b) {
                continue;
            } else {
                return a > b;
            }
        }
        // the two revision has different length, the longer one is newer
        return rev1.length > rev2.length;
    }

    public static List<RevisionInfo> getRevisionList(String category, String version) throws Exception {
        StringBuffer url = new StringBuffer();
        url.append(REVISION_LIST_URL).append("?categories=").append(category).append("&version=");
        String[] branch = getBranch(version);
        url.append(StringUtils.join(branch, ","));
        String jsonContent = sendGetRequest(url.toString());
        System.out.println(url);
        return parseJsonObject(jsonContent, RevisionInfo.class);
    }

    /**
     * Get the folder that will store downloaded component.
     * 
     * @return
     */
    public static File getComponentFolder() {
        URL url = FileLocator.find(EcosPlugin.getDefault().getBundle(), new Path("Patterns"), null); //$NON-NLS-1$
        try {
            URL fileUrl = FileLocator.toFileURL(url);
            return new File(fileUrl.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

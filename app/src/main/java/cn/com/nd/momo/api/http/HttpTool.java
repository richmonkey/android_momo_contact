
package cn.com.nd.momo.api.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.nd.momo.api.exception.MoMoException;
import cn.com.nd.momo.api.util.Log;

import im.momo.contact.Token;
import com.tinyline.util.GZIPInputStream;

public final class HttpTool {
    private static final String TAG = "HttpTool";

    private static final int TIMEOUT_MILLISEC = 30000;

    public static int OAUTH_DISABLED = 401;

    private static final int NET_WORK_NOT_ACTIVIE = 600;

    private static final String NET_WORK_NOT_ACTIVIE_DESC = "网络不通";

    private static final int NET_WORK_SOCKET_TIME_OUT = 480;

    private static final String NET_WORK_SOCKET_TIME_OUT_DESC = "连接超时";

    private static final String CONTENT_ENCODING = HTTP.UTF_8;

    private static final String CONTENT_TYPE = "application/json";

    private static final String HEADERNAME_OAUTH = "Authorization";

    // URL to POST or Get from server
    private String m_strURL;

    public String getUrl() {
        return m_strURL;
    }

    // response string from server
    private String m_strResponse = "";

    public HttpTool(String strUrl) {
        m_strURL = strUrl;
    }

    public String GetResponse() {
        return m_strResponse;
    }

    /**
     * Do Get method, after this function end, call GetResponse to get returned
     * string
     * 
     * @return http code from server after calling Get method
     */
    public int DoGet() throws MoMoException {
        String auth = String.format("Bearer %s", Token.getInstance().accessToken);
        return DoGet(auth, "");
    }

    public int DoGet(String strHeader, String currentVersion) throws MoMoException {
        return DoGet(strHeader, currentVersion, "UTF-8");
    }

    public int DoGet(String strHeader, String currentVersion, String encoding) throws MoMoException {
        BufferedReader in = null;
        InputStream is;
        int nRet = 0;

        try {
            // make a client
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(m_strURL));

            // add header
            if (strHeader != null) {
                request.addHeader("Authorization", strHeader);
                if (currentVersion != null && currentVersion.length() > 0) {
                    request.addHeader("X-MOMO-VERSION", currentVersion);
                }
            }

            request.addHeader("Accept-Encoding", "gzip");
            // sending request
            HttpResponse response = client.execute(request);
            nRet = response.getStatusLine().getStatusCode();

            is = response.getEntity().getContent();

            for (Header header : response.getAllHeaders()) {
                String name = header.getName();
                String value = header.getValue();
                if (name.equals("Content-Encoding") && value.equals("gzip")) {
                    is = new GZIPInputStream(is);
                }
            }

            // get response code and string
            in = new BufferedReader(new InputStreamReader(is, encoding));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            m_strResponse = sb.toString();

            // record received byte count
            HttpTransportListener.GetInstance().AddRcvCount(m_strResponse.getBytes("UTF-8").length);

        } catch (SocketTimeoutException e) {
            // 连接超时
            e.printStackTrace();
            nRet = HttpStatus.SC_REQUEST_TIMEOUT;
            m_strResponse = "连接超时";
            throw new MoMoException(nRet, m_strResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MoMoException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        if (nRet == HttpStatus.SC_BAD_REQUEST) {
            // 状态码400，需解析返回数据后抛出异常
            try {
                throw new MoMoException(new JSONObject(m_strResponse));
            } catch (Exception e) {
                throw new MoMoException(e);
            }
        } else if (nRet != HttpStatus.SC_OK) {
            // 其他非200状态码也抛出异常
            throw new MoMoException(nRet, m_strResponse);
        }

        return nRet;
    }

    /**
     * post method
     * 
     * @param c
     * @param strHeader: null for default auth header
     * @return
     * @throws JSONException
     */
    public int DoPost(JSONObject c, String strHeader) throws MoMoException {
        BufferedReader in = null;
        int nRet = 0;

        try {
            // make a POST client
            HttpPost request = new HttpPost(m_strURL);

            HttpParams httpParams = new BasicHttpParams();
            // 请求超时10秒
            int timeoutConnection = 10000;
            HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
            // socket超时60秒
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

            HttpClient client = new DefaultHttpClient(httpParams);

            // add header
            if (strHeader != null) {
                request.addHeader("Authorization", strHeader);
            }

            if (c != null) {
                // get json string and pass to entigy
                HttpEntity entity;
                StringEntity s = new StringEntity(c.toString(), "UTF-8");
                s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                s.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8));
                entity = s;
                request.setEntity(entity);
            }

            // record send byte count
            if (c != null) {
                HttpTransportListener.GetInstance().AddSendCount(
                        c.toString().getBytes("UTF-8").length);
            }

            // POST and get response code
            HttpResponse response = client.execute(request);
            nRet = response.getStatusLine().getStatusCode();

            // get response string
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line).append(NL);
            }
            in.close();
            m_strResponse = sb.toString();

            // record received byte count
            HttpTransportListener.GetInstance().AddRcvCount(m_strResponse.getBytes("UTF-8").length);

        } catch (SocketException e) {
            // 无网络链接
            e.printStackTrace();
            nRet = NET_WORK_NOT_ACTIVIE;
            m_strResponse = "网络不通";
            throw new MoMoException(nRet, m_strResponse);
        } catch (UnknownHostException e) {
            // 无网络链接
            e.printStackTrace();
            nRet = NET_WORK_NOT_ACTIVIE;
            m_strResponse = "网络不通";
            throw new MoMoException(nRet, m_strResponse);
        } catch (SocketTimeoutException e) {
            // 连接超时
            e.printStackTrace();
            nRet = NET_WORK_SOCKET_TIME_OUT;
            m_strResponse = "连接超时";
            throw new MoMoException(nRet, m_strResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MoMoException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        if (nRet == HttpStatus.SC_BAD_REQUEST) {
            // 状态码400，需解析返回数据后抛出异常
            try {
                throw new MoMoException(new JSONObject(m_strResponse));
            } catch (Exception e) {
                throw new MoMoException(e);
            }
        } else if (nRet != HttpStatus.SC_OK) {
            // 其他非200状态码也抛出异常
            throw new MoMoException(nRet, m_strResponse);
        }

        return nRet;
    }

    public int DoPostArray(JSONArray c, String strHeader) throws MoMoException {
        BufferedReader in = null;
        int nRet = 0;

        try {
            // make a POST client
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(m_strURL);

            // add header
            if (strHeader != null) {
                request.addHeader("Authorization", strHeader);
            }

            if (c != null) {
                // get json string and pass to entigy
                HttpEntity entity;
                StringEntity s = new StringEntity(c.toString(), "UTF-8");
                s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                s.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8));
                entity = s;
                request.setEntity(entity);
            }

            // POST and get response code
            HttpResponse response = client.execute(request);
            nRet = response.getStatusLine().getStatusCode();

            // get response string
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            m_strResponse = sb.toString();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            throw new MoMoException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }
            }
        }

        if (nRet == HttpStatus.SC_BAD_REQUEST) {
            // 状态码400，需解析返回数据后抛出异常
            try {
                throw new MoMoException(new JSONObject(m_strResponse));
            } catch (Exception e) {
                throw new MoMoException(e);
            }
        } else if (nRet != HttpStatus.SC_OK) {
            // 其他非200状态码也抛出异常
            throw new MoMoException(nRet, m_strResponse);
        }

        return nRet;
    }

    /**
     * Do POST method to server, after this function end, call GetResponse to
     * get returned string
     * 
     * @param c JSON pair to POST to server
     * @return http code from server after calling POST method
     */
    public int DoPost(JSONObject c) throws MoMoException {
        String auth = String.format("Bearer %s", Token.getInstance().accessToken);
        return DoPost(c, auth);
    }

    public int DoPostArray(JSONArray arr) throws MoMoException {
        String auth = String.format("Bearer %s", Token.getInstance().accessToken);
        return DoPostArray(arr, auth);
    }

    public int DoPostByteArray(ByteArrayEntity bytearray) throws MoMoException {
        String auth = String.format("Bearer %s", Token.getInstance().accessToken);
        return DoPostByteArray(bytearray, auth);
    }

    public int DoPostByteArray(ByteArrayEntity bytearray, String strHeader) throws MoMoException {
        BufferedReader in = null;
        int nRet = 0;

        try {
            // make a POST client
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(m_strURL);

            request.setEntity(bytearray);

            // add header
            if (strHeader != null) {
                request.addHeader("Authorization", strHeader);
            }
            // record send byte count
            // HttpTransportListener.GetInstance().AddSendCount(c.toString().getBytes("UTF-8").length);

            // POST and get response code
            HttpResponse response = client.execute(request);
            nRet = response.getStatusLine().getStatusCode();

            // get response string
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            m_strResponse = sb.toString();

            // record received byte count
            // HttpTransportListener.GetInstance().AddRcvCount(m_strResponse.getBytes("UTF-8").length);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            throw new MoMoException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }
            }
        }

        if (nRet == HttpStatus.SC_BAD_REQUEST) {
            // 状态码400，需解析返回数据后抛出异常
            try {
                throw new MoMoException(new JSONObject(m_strResponse));
            } catch (Exception e) {
                throw new MoMoException(e);
            }
        } else if (nRet != HttpStatus.SC_OK) {
            // 其他非200状态码也抛出异常
            throw new MoMoException(nRet, m_strResponse);
        }

        return nRet;
    }

    /**
     * 下载文件
     * 
     * @param url
     * @param headers
     * @return
     * @throws MoMoException
     */
    public static byte[] DownLoadBytes(String url, String headers) throws MoMoException {
        URL aryURI = null;
        URLConnection conn = null;
        InputStream is = null;
        byte[] byteResult = null;
        try {
            aryURI = new URL(url);
            conn = aryURI.openConnection();
            conn.setReadTimeout(TIMEOUT_MILLISEC);
            // String headers = token.getAuthHeader(url, "GET");
            conn.addRequestProperty("Authorization", headers);
            conn.connect();
            is = conn.getInputStream();
            HttpTransportListener.GetInstance().AddRcvCount(conn.getContentLength());
            byteResult = getBytes(is);
            is.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            throw new MoMoException(e);
        }

        return byteResult;
    }

    private static byte[] getBytes(InputStream is)
            throws Exception {
        byte[] buffer = new byte[1024 * 5];
        byte[] data = null;

        // Collection chunks = new ArrayList();
        // ArrayList<Byte> vData = new ArrayList<Byte>();

        int read = -1;
        // int size = 0;

        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
        } finally {
            if (bos != null) {
                bos.close();
            }
        }

        while ((read = is.read(buffer)) != -1) {
            if (read > 0) {
                // byte[] chunk = new byte[read];
                // System.arraycopy(buffer,0,vData,0,read);
                // //chunks.add(chunk);
                //
                //
                // size += chunk.length;

                for (int i = 0; i < read; i++) {
                    // vData.add(buffer[i]);
                    bos.write(buffer[i]);
                }
            }
        }

        if (bos != null && bos.size() != 0) {
            data = bos.toByteArray();
        }

        if (bos != null) {
            bos.close();
        }

        return data;
    }

    /**
     * 执行get请求
     * 
     * @param requestUrl 请求地址
     * @return
     * @throws MoMoException
     * @throws MoMoException
     */
    public static String get(String requestUrl) throws MoMoException {
        HashMap<String, String> headerMap = new HashMap<String, String>();
        String auth = String.format("Bearer %s", Token.getInstance().accessToken);
        headerMap.put(HEADERNAME_OAUTH, auth);
        return get(requestUrl, headerMap, CONTENT_TYPE, CONTENT_ENCODING);
    }

    /**
     * 执行get请求
     * 
     * @param requestUrl 请求地址
     * @param headerMap 请求头
     * @return
     * @throws MoMoException
     */
    public static String get(String requestUrl, HashMap<String, String> headerMap)
            throws MoMoException {
        return get(requestUrl, headerMap, CONTENT_TYPE, CONTENT_ENCODING);
    }

    /**
     * 执行get请求
     * 
     * @param requestUrl 请求地址
     * @param headerMap 请求头
     * @param content_type 内容类型
     * @param content_encoding 内容编码
     * @return
     * @throws MoMoException
     */
    public static String get(String requestUrl, HashMap<String, String> headerMap,
            String content_type, String content_encoding) throws MoMoException {
        BufferedReader in = null;
        InputStream is;

        Log.d(requestUrl);

        try {
            // make a client
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(requestUrl));

            // add header
            // add header
            if (headerMap != null)
                for (String name : headerMap.keySet()) {
                    request.addHeader(name, headerMap.get(name));
                }

            request.addHeader("Accept-Encoding", "gzip");
            // sending request
            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();

            is = response.getEntity().getContent();

            for (Header header : response.getAllHeaders()) {
                String name = header.getName();
                String value = header.getValue();
                if (name.equals("Content-Encoding") && value.equals("gzip")) {
                    is = new GZIPInputStream(is);
                }
            }

            // get response code and string
            in = new BufferedReader(new InputStreamReader(is, content_encoding));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String strResponse = sb.toString();

            if (responseCode == HttpStatus.SC_BAD_REQUEST) {
                // 状态码400，需解析返回数据后抛出异常
                try {
                    throw new MoMoException(new JSONObject(strResponse));
                } catch (Exception e) {
                    throw new MoMoException(e);
                }
            } else if (responseCode != HttpStatus.SC_OK) {
                // 其他非200状态码也抛出异常
                throw new MoMoException(responseCode, strResponse);
            }

            Log.d(strResponse);
            return strResponse;
        } catch (SocketException e) {
            // 无网络链接
            throw new MoMoException(NET_WORK_NOT_ACTIVIE, NET_WORK_NOT_ACTIVIE_DESC);
        } catch (UnknownHostException e) {
            // 无网络链接
            throw new MoMoException(NET_WORK_NOT_ACTIVIE, NET_WORK_NOT_ACTIVIE_DESC);
        } catch (SocketTimeoutException e) {
            // 连接超时
            throw new MoMoException(NET_WORK_SOCKET_TIME_OUT, NET_WORK_SOCKET_TIME_OUT_DESC);
        } catch (Exception e) {
            throw new MoMoException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new MoMoException(e);
                }
            }
        }
    }

    /**
     * 执行post请求
     * 
     * @param requestUrl 请求地址
     * @param params 参数
     * @return
     * @throws MoMoException
     * @throws MoMoException
     */
    public static String post(String requestUrl, JSONObject params) throws MoMoException {
        HashMap<String, String> headerMap = new HashMap<String, String>();
        String auth = String.format("Bearer %s", Token.getInstance().accessToken);
        headerMap.put(HEADERNAME_OAUTH, auth);
        return post(requestUrl, params, headerMap, CONTENT_TYPE, CONTENT_ENCODING);
    }

    /**
     * 执行post请求
     * 
     * @param requestUrl 请求地址
     * @param params 参数
     * @param headerMap 请求头
     * @return
     * @throws MoMoException
     */
    public static String post(String requestUrl, JSONObject params,
            HashMap<String, String> headerMap) throws MoMoException {
        return post(requestUrl, params, headerMap, CONTENT_TYPE, CONTENT_ENCODING);
    }

    /**
     * 执行post请求
     * 
     * @param requestUrl 请求地址
     * @param params 参数
     * @param headerMap 请求头
     * @param content_type 内容类型
     * @param content_encoding 内容编码
     * @return
     * @throws MoMoException
     */
    public static String post(String requestUrl, JSONObject params,
            HashMap<String, String> headerMap, String content_type, String content_encoding)
            throws MoMoException {
        Log.d(requestUrl);

        BufferedReader in = null;
        try {
            // make a POST client
            HttpPost request = new HttpPost(requestUrl);

            HttpParams httpParams = new BasicHttpParams();
            // 请求超时10秒
            int timeoutConnection = 10000;
            HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
            // socket超时60秒
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

            HttpClient client = new DefaultHttpClient(httpParams);

            // add header
            if (headerMap != null)
                for (String name : headerMap.keySet()) {
                    request.addHeader(name, headerMap.get(name));
                }

            if (params != null) {
                // get json string and pass to entigy
                HttpEntity entity;
                StringEntity s = new StringEntity(params.toString(), content_encoding);

                Log.d(params.toString());

                s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, content_type));
                s.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, content_encoding));
                entity = s;
                request.setEntity(entity);
            }

            // POST and get response code
            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();

            // get response string
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line).append(NL);
            }
            in.close();

            String strResponse = sb.toString();

            if (responseCode == HttpStatus.SC_BAD_REQUEST) {
                // 状态码400，需解析返回数据后抛出异常
                try {
                    throw new MoMoException(new JSONObject(strResponse));
                } catch (Exception e) {
                    throw new MoMoException(e);
                }
            } else if (responseCode != HttpStatus.SC_OK) {
                // 其他非200状态码也抛出异常
                throw new MoMoException(responseCode, strResponse);
            }

            Log.d(strResponse);
            return strResponse;

        } catch (SocketException e) {
            // 无网络链接
            throw new MoMoException(NET_WORK_NOT_ACTIVIE, NET_WORK_NOT_ACTIVIE_DESC);
        } catch (UnknownHostException e) {
            // 无网络链接
            throw new MoMoException(NET_WORK_NOT_ACTIVIE, NET_WORK_NOT_ACTIVIE_DESC);
        } catch (SocketTimeoutException e) {
            // 连接超时
            throw new MoMoException(NET_WORK_SOCKET_TIME_OUT, NET_WORK_SOCKET_TIME_OUT_DESC);
        } catch (Exception e) {
            throw new MoMoException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new MoMoException(e);
                }
            }
        }
    }
}

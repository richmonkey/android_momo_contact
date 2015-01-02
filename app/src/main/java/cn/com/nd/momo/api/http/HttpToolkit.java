
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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import cn.com.nd.momo.api.oauth.OAuthHelper;
import cn.com.nd.momo.api.util.Log;

import com.tinyline.util.GZIPInputStream;

public final class HttpToolkit {
    private static final String TAG = "HttpToolkit";

    private static final int TIMEOUT_MILLISEC = 30000;

    // server return code

    private static final int NET_WORK_NOT_ACTIVIE = 600;

    private static final int NET_WORK_SOCKET_TIME_OUT = 480;

    // URL to POST or Get from server
    private String m_strURL;

    public String getUrl() {
        return m_strURL;
    }

    // response string from server
    private String m_strResponse = "";

    private OAuthHelper mOAuth = new OAuthHelper();

    public HttpToolkit(String strUrl) {
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
    public int DoGet() {
        return DoGet(OAuthHelper.getAuthHeader(m_strURL, "GET"), "");
    }

    public int DoGet(String currentVersion) {
        return DoGet(OAuthHelper.getAuthHeader(m_strURL, "GET"), currentVersion);
    }

    public int DoGet(int timeout) {
        String strHeader = OAuthHelper.getAuthHeader(m_strURL, "GET");
        BufferedReader in = null;
        InputStream is;
        int nRet = 0;
        try {
            // Set the timeout in milliseconds until a connection is
            // established.
            HttpParams httpParameters = new BasicHttpParams();
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
            HttpConnectionParams.setSoTimeout(httpParameters, timeout);
            // make a client
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet request = new HttpGet();
            request.setURI(new URI(m_strURL));

            // add header
            if (strHeader != null) {
                request.addHeader("Authorization", strHeader);
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
            in = new BufferedReader(new InputStreamReader(is));
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
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            nRet = HttpStatus.SC_REQUEST_TIMEOUT;
            m_strResponse = "连接超时";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return nRet;
    }

    public int DoGet(String strHeader, String currentVersion) {
        return DoGet(strHeader, currentVersion, "UTF-8");
    }

    public int DoGet(String strHeader, String currentVersion, String encoding) {
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
            nRet = 408;
            m_strResponse = "连接超时";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return nRet;
    }

    /**
     * post method
     * 
     * @param c
     * @param strHeader: null for default auth header
     * @return
     */
    public int DoPost(JSONObject c, String strHeader) {
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
        } catch (UnknownHostException e) {
            // 无网络链接
            e.printStackTrace();
            nRet = NET_WORK_NOT_ACTIVIE;
            m_strResponse = "网络不通";
        } catch (SocketTimeoutException e) {
            // 连接超时
            e.printStackTrace();
            nRet = NET_WORK_SOCKET_TIME_OUT;
            m_strResponse = "连接超时";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
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
    public int DoPost(JSONObject c) {
        return DoPost(c, OAuthHelper.getAuthHeader(m_strURL, "POST"));
    }

    public int DoPostByteArray(ByteArrayEntity bytearray) {
        return DoPostByteArray(bytearray, OAuthHelper.getAuthHeader(m_strURL, "POST"));
    }

    public int DoPostByteArray(ByteArrayEntity bytearray, String strHeader) {
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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }
            }
        }

        return nRet;
    }

    public int DoDelete() {
        return DoDelete(OAuthHelper.getAuthHeader(m_strURL, "DELETE"));
    }

    public int DoDelete(String strHeader) {
        BufferedReader in = null;
        int nRet = 0;

        try {
            // make a delete client
            HttpClient client = new DefaultHttpClient();
            HttpDelete request = new HttpDelete(m_strURL);

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
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.toString());
                }
            }
        }

        return nRet;
    }

    public byte[] DownLoadBytes() {
        URL aryURI = null;
        URLConnection conn = null;
        InputStream is = null;
        byte[] byteResult = null;
        try {
            aryURI = new URL(m_strURL);
            conn = aryURI.openConnection();
            conn.setReadTimeout(TIMEOUT_MILLISEC);
            String headers = OAuthHelper.getAuthHeader(m_strURL, "GET");
            conn.addRequestProperty("Authorization", headers);
            conn.connect();
            is = conn.getInputStream();
            HttpTransportListener.GetInstance().AddRcvCount(conn.getContentLength());

            // get bitmap from input stream
            byteResult = getBytes(is);
            is.close();
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        return byteResult;
    }

    private byte[] buffer = new byte[1024 * 5];

    private byte[] getBytes(InputStream is)
            throws Exception {
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

        // if(size>0)
        // {
        // ByteArrayOutputStream bos = null;
        // try
        // {
        // bos = new ByteArrayOutputStream(size);
        // for(Iterator itr=chunks.iterator();itr.hasNext();)
        // {
        // byte[] chunk = (byte[])itr.next();
        // bos.write(chunk);
        // }
        // data = bos.toByteArray();
        // }
        // finally
        // {
        // if(bos!=null)
        // {
        // bos.close();
        // }
        // }
        // }

        // buffer = null;
        // return data;

    }

}


package cn.com.nd.momo.api.http;

/**
 * @author Administrator
 */
public class HttpTransportListener {

    private static HttpTransportListener m_Listener = null;

    private long m_lByteSend;

    private long m_lByteRcv;

    public static HttpTransportListener GetInstance() {
        if (m_Listener == null) {
            m_Listener = new HttpTransportListener();
        }

        return m_Listener;
    }

    public long GetByteSendCount() {
        return m_lByteSend;
    }

    public long GetByteRcvCount() {
        return m_lByteRcv;
    }

    /**
     * add sent byte count this function is to be call by http agent
     * 
     * @param nByteLen sent byte count
     */
    public synchronized void AddSendCount(long lByteLen) {
        m_lByteSend += lByteLen;
    }

    /**
     * add receive byte count this function is to be call by http agent
     * 
     * @param nByteLen receive byte count
     */
    public synchronized void AddRcvCount(long lByteLen) {
        m_lByteRcv += lByteLen;
    }

    /**
     * save the result to the application config
     */
    public void Save() {

    }

    /**
     * read data from application config
     */
    private void ReadFromConfig() {

    }

    private HttpTransportListener() {
        ReadFromConfig();
    }
}

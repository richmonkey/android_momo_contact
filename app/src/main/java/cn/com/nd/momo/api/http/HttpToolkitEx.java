
package cn.com.nd.momo.api.http;

import org.json.JSONObject;

import android.os.Handler;
import cn.com.nd.momo.api.util.Log;

public class HttpToolkitEx {
    public static int post(final Handler handler, final String url, final JSONObject jsonObj,
            final HttpResponse resp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpToolkit toolkit = new HttpToolkit(url);
                Log.i("post");
                final int retCode = toolkit.DoPost(jsonObj);
                final String retContent = toolkit.GetResponse();
                Log.i("request url:" + url);
                Log.i("retCode:" + retCode);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resp.onResponse(retCode, retContent);
                    }
                });
            }
        }).start();
        return 0;
    }

    public static int get(final Handler handler, final String url, final HttpResponse resp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpToolkit toolkit = new HttpToolkit(url);
                final int retCode = toolkit.DoGet();
                final String retContent = toolkit.GetResponse();
                Log.i("request url:" + url);
                Log.i("retCode:" + retCode);
                Log.i("retContent:" + retContent);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resp.onResponse(retCode, retContent);
                    }
                });
            }
        }).start();
        return 0;
    }

    public interface HttpResponse {
        public void onResponse(int retCode, String retContent);
    }
}

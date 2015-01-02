package im.momo.contact.api;

import cn.com.nd.momo.api.RequestUrl;
import im.momo.contact.Token;
import com.google.gson.Gson;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by tsung on 10/10/14.
 */
class IMHttpRetrofit {
    final IMHttp service;

    IMHttpRetrofit() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(RequestUrl.URL_API)
                .setConverter(new GsonConverter(new Gson()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (Token.getInstance().accessToken != null && !Token.getInstance().accessToken.equals("")) {
                            request.addHeader("Authorization", "Bearer " + Token.getInstance().accessToken);
                        }
                    }
                })
                .build();

        service = adapter.create(IMHttp.class);
    }

    public IMHttp getService() {
        return service;
    }
}

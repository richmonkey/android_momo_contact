package im.momo.contact.api;

import im.momo.contact.Token;
import im.momo.contact.api.body.PostAuthRefreshToken;
import im.momo.contact.api.body.PostAuthToken;
import im.momo.contact.api.body.PostPhone;
import im.momo.contact.api.body.PostTextValue;
import im.momo.contact.api.types.Audio;
import im.momo.contact.api.types.Code;
import im.momo.contact.api.types.Image;
import im.momo.contact.api.types.User;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * Created by tsung on 10/10/14.
 */
public interface IMHttp {
    @GET("/verify_code")
    Observable<Code> getVerifyCode(@Query("zone") String zone, @Query("number") String number);

    @POST("/auth/token")
    Observable<Token> postAuthToken(@Body PostAuthToken code);

    @POST("/auth/refresh_token")
    Observable<Token> postAuthRefreshToken(@Body PostAuthRefreshToken refreshToken);

    @POST("/images")
    Observable<Image> postImages(@Header("Content-Type") String contentType, @Body TypedFile file);

    @POST("/audios")
    Observable<Audio> postAudios(@Header("Content-Type") String contentType, @Body TypedFile file);

    @Multipart
    @PUT("/users/me/avatar")
    Observable<Image> putUsersMeAvatar(@Part("file") TypedFile file);

    @PUT("/users/me/nickname")
    Observable<Object> putUsersMeNickname(@Body PostTextValue nickname);

    @PUT("/users/me/state")
    Observable<Object> putUsersMeState(@Body PostTextValue state);

    @POST("/users")
    Observable<ArrayList<User>> postUsers(@Body List<PostPhone> phones);
}

package x.homework.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import x.homework.model.ApiResponse;

/**
 * @author michael sanchez
 */
public interface FooApi {
    // For this exercise, all gets are url based, so just using a single method here that takes the
    // supplied url
    //
    // Json query param is appended by an interceptor (see: ApiUtils for retrofit config)

    // (Required by the retrofit builder)
    String BASE_URL = "http://opml.radiotime.com";

    @GET
    Call<ApiResponse> get(@Url String url);
    
}
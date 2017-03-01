package x.homework.network.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import x.homework.model.ParentItemPostProcessor;
import x.homework.network.FooApi;

/**
 * @author michael sanchez
 */
public class RetrofitUtils {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ParentItemPostProcessor())
                .create();
    }

    public static Retrofit getRetrofit() {
        return LazyHolder.INSTANCE;
    }

    private static Retrofit init() {
        // (logging added for easy viewing of response data)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new JsonQParamInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(FooApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    // appends the "render=json" query param/value to every request
    private static class JsonQParamInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request origReq = chain.request();
            HttpUrl origUrl = origReq.url();
            HttpUrl newUrl = origUrl.newBuilder().addQueryParameter("render", "json").build();
            return chain.proceed(origReq.newBuilder().url(newUrl).build());
        }
    }

    // TODO in real life - replace singleton pattern with Dagger or other DI
    private static class LazyHolder {
        // init will be called only when the field is first referenced
        private static final Retrofit INSTANCE = init();
    }
}
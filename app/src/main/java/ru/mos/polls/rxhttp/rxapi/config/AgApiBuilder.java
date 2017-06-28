package ru.mos.polls.rxhttp.rxapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.rxhttp.session.Session;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.04.17 11:59.
 */

public class AgApiBuilder {
    public static final String URL_AG = "https://emp.mos.ru:443/?token=ag_test_token";

    public static AgApi build() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        /**
         * Используется для автоматической подстановки сесии в запрос</br>
         * не используется, пока оставим
         */
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                RequestBody requestBody = request.body();
                if (requestBody.contentType().subtype().contains("json")) {
                    requestBody = processAuth(requestBody);
                    if (requestBody != null) {
                        request = request.newBuilder()
                                .post(requestBody)
                                .build();
                    }
                }
                return chain.proceed(request);
            }

            private RequestBody processAuth(RequestBody requestBody) {
                RequestBody result = null;
                String body = asString(requestBody);
                try {
                    JSONObject jsonBody = new JSONObject(body);
                    JSONObject authJson = new JSONObject();
                    authJson.put("session_id", Session.get().getSession());
                    result = RequestBody.create(requestBody.contentType(), jsonBody.toString().getBytes());
                } catch (JSONException ignored) {
                }
                return result;
            }

            private String asString(final RequestBody request) {
                String result = null;
                try {
                    final Buffer buffer = new Buffer();
                    if (request != null) {
                        request.writeTo(buffer);
                    }
                    result = buffer.readUtf8();
                }
                catch (IOException ignored) {
                }
                return result;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(authInterceptor)
                .addInterceptor(logInterceptor)
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_AG)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();

        return retrofit.create(AgApi.class);
    }
}

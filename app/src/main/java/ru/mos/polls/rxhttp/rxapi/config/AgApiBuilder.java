package ru.mos.polls.rxhttp.rxapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mos.polls.BuildConfig;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.04.17 11:59.
 */

public class AgApiBuilder {
    public static final String URL_AG = "https://emp.mos.ru:443/";

    public static AgApi build() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(getDefaultURLParamsInterceptor())
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_AG)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();

        return retrofit.create(AgApi.class);
    }

    /**
     * Добавляем  {@link UUID}, {@link Token} для каждого запроса
     */
    public static Interceptor getDefaultURLParamsInterceptor() {
        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("token", token().get())
                    .addQueryParameter("client_req_id", getUUID())
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
        return authInterceptor;
    }

    public static Token token() {
        Token result = Token.RELEASE;
        if (BuildConfig.BUILD_TYPE.equals("customer")) {
            result = Token.UAT;
        } else if (BuildConfig.BUILD_TYPE.equals("debug")) {
            result = Token.TEST;
        }
        return result;
    }

    private static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Константы токенов приложения для контуров,
     * todo вынести в productFlavors
     */
    public enum Token {
        TEST("ag_test_token"),
        UAT("ag_uat_token3"),
        RELEASE("35e59a5eaab111e3b266416c74617269");

        private String value;

        Token(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }

    }

}

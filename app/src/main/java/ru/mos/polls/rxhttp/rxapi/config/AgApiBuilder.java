package ru.mos.polls.rxhttp.rxapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mos.polls.BuildConfig;


public class AgApiBuilder {
    public static final String URL_AG = "https://emp.mos.ru:443/";
    private static final String URL_AG_FORMATTED = "https://emp.mos.ru:443%s";
    private static final String URL_AG_RESOURCE_FORMATTED_TEST = "http://test.service.ag.mos.ru%s";
    private static final String URL_AG_RESOURCE_FORMATTED = "http://service.ag.mos.ru%s";
    private static final String URL_AG_RESOURCE_FORMATTED_UAT = "http://uat.service.ag.mos.ru%s";

    public static boolean forTestProd;

    public static void setForTestProd(boolean forTestProd) {
        AgApiBuilder.forTestProd = forTestProd;
    }

    public static String url(String method) {
        return String.format(URL_AG_FORMATTED, method);
    }

    public static String resourceURL(String method) {
        String url = URL_AG_RESOURCE_FORMATTED;
        if (BuildConfig.BUILD_TYPE.equals("customer")) {
            url = URL_AG_RESOURCE_FORMATTED_UAT;
        } else if (BuildConfig.BUILD_TYPE.equals("debug")) {
            url = URL_AG_RESOURCE_FORMATTED_TEST;
        }
        return String.format(url, method);
    }

    public static AgApi build() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
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
        if (forTestProd) return result;
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

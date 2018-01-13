package ru.mos.polls.util;

import ru.mos.polls.BuildConfig;

/**
 * Created by Trunks on 12.01.2018.
 */

public class UrlHelper {

    public static final String host = "release".equalsIgnoreCase(BuildConfig.BUILD_TYPE) ? "ag.mos.ru" : "testing.ag.mos.ru";
    public static final String urlConstructorPattern = "/house/constructor";
    public static final String MAIN_URL = "http://%s";
    private static final String urlConstructor = String.format(urlConstructorPattern, host);
    private static final String cookiesPattern = "EMPSESSION=%s";


    public static String getHouseConstructorUrl() {
        return String.format(MAIN_URL, host) + urlConstructor;
    }

    public static String getMainUrl() {
        return String.format(MAIN_URL, host);
    }
}

package ru.mos.elk;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 21.04.2014.
 */
public class Statistics {

    public static void serviceRequest(String url, String requestBody, String strJson) {
        Map<String, String> articleParams = new HashMap<String, String>();
        articleParams.put("url", url);
        articleParams.put("query", requestBody);
        articleParams.put("result", strJson);
        FlurryAgent.logEvent("Service Request", articleParams);
    }


    public static void logon() {
        FlurryAgent.logEvent("authorization");
    }

    public static void regitrated() {
        FlurryAgent.logEvent("registration");
    }

    public static void passwRecovered() {
        FlurryAgent.logEvent("password recovery");
    }

    public static void bannerShow(String bannerId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("banner_id", bannerId);
        FlurryAgent.logEvent("banner show", map);
    }

    public static void bannerLink(String bannerId, String anchor) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("banner_id", bannerId);
        map.put("anchor", anchor);
        FlurryAgent.logEvent("banner link", map);
    }

    public static void bannerDismiss(String bannerId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("banner_id", bannerId);
        FlurryAgent.logEvent("banner dismiss", map);
    }

    /**
     * Экран авторизации, факт перехода в "Регистрация"
     */
    public static void authEnterRegistration() {
        FlurryAgent.logEvent("auth_enter_registration");
    }

    /**
     * Экран авторизации, факт перехода в "Восстановление"
     */
    public static void getPassword() {
        FlurryAgent.logEvent("get_password");
    }

    public static void customEvent(String name) {
        FlurryAgent.logEvent(name);
    }

    public static void customEvent(String name, Map<String, String> params) {
        FlurryAgent.logEvent(name, params);
    }
}

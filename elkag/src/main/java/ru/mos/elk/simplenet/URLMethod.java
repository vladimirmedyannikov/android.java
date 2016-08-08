package ru.mos.elk.simplenet;

public enum URLMethod {
    GET("GET"),
    POST("POST");

    private String method;

    URLMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

}

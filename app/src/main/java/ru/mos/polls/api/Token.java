package ru.mos.polls.api;

public enum Token {
    AG("ag_test_token", "ag_uat_token3","35e59a5eaab111e3b266416c74617269");

    private final String token;
    private String customToken = null;
    private final String releaseToken;
    private boolean isCustom = false;

    Token(String token, String uatToken,String releaseToken) {
        this.token = token;
        this.customToken = uatToken;
        this.releaseToken = releaseToken;
    }

    public String getToken(boolean isDebug) {
        if(isCustom)
            return customToken;
        return isDebug? token:releaseToken;
    }

    public void setIsCustom(boolean isCustom){
        this.isCustom = isCustom;
    }

    public void setCustomToken(String customToken){
        this.customToken = customToken;
    }
}
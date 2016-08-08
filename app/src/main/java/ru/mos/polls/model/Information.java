package ru.mos.polls.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.io.Serializable;

import ru.mos.polls.WebViewActivity;


public class Information implements Serializable {
    private String title;
    private String link;

    public Information() {
        this(null, null);
    }

    public Information(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void showInfoActivity(Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.INFORMATION_TITLE, getTitle());
        intent.putExtra(WebViewActivity.INFORMATION_URL, getLink());
        intent.putExtra(WebViewActivity.ONLY_LOAD_FIRST_URL, false);
        context.startActivity(intent);
    }

    public boolean isFilled() {
        return !TextUtils.isEmpty(getTitle()) && !TextUtils.isEmpty(getLink());
    }
}

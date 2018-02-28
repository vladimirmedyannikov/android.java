package ru.mos.polls.rxhttp.rxapi.model.base;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.Page;


public class PageAuthRequest extends AuthRequest {
    @SerializedName("count_per_page")
    private int size;
    @SerializedName("page_number")
    private int num;

    public PageAuthRequest(Page page) {
        super();
        size = page.getSize();
        num = page.getNum();
    }
}

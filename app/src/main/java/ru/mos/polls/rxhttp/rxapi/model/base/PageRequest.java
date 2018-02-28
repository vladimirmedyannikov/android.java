package ru.mos.polls.rxhttp.rxapi.model.base;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.Page;


public class PageRequest {
    @SerializedName("count_per_page")
    protected int size;
    @SerializedName("page_number")
    protected int num;

    PageRequest(Page page) {
        size = page.getSize();
        num = page.getNum();
    }
}

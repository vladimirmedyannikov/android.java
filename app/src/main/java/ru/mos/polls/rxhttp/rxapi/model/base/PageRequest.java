package ru.mos.polls.rxhttp.rxapi.model.base;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.Page;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.06.17 12:54.
 */

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

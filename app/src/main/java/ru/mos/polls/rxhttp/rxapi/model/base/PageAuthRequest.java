package ru.mos.polls.rxhttp.rxapi.model.base;

import com.google.gson.annotations.SerializedName;

import ru.mos.polls.rxhttp.rxapi.model.Page;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.06.17 13:06.
 */

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

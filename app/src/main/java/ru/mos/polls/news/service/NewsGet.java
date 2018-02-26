package ru.mos.polls.news.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.news.model.News;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 26.02.18.
 */

public class NewsGet {
    public static class Request extends AuthRequest {
        @SerializedName("count_per_page")
        private int countPerPage;
        @SerializedName("page_number")
        private int pageNumber;

        public Request(Page page) {
            this.countPerPage = page.getSize();
            this.pageNumber = page.getNum();
        }
    }

    public static class Response extends GeneralResponse<List<News>> {
    }
}

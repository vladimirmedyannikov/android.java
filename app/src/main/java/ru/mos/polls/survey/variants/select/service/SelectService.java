package ru.mos.polls.survey.variants.select.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.base.AuthRequest;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.survey.variants.select.model.FindObjects;
import ru.mos.polls.survey.variants.select.model.VariantsObjects;

public class SelectService {

    public static class Request extends AuthRequest {

        /**
         * category : add_t_nclinic_0
         * search :
         * auth : {"session_id":"2f0ed804b9c393b0fc6885ce5d4d0b33"}
         */

        private String category;
        private String search;
        private String parent;
        @SerializedName("soesg_category_code")
        private String soesgCategoryCode;

        public void setCategory(String category) {
            this.category = category;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public void setSoesgCategoryCode(String soesgCategoryCode) {
            this.soesgCategoryCode = soesgCategoryCode;
        }
    }

    public static class VariantsResponse extends GeneralResponse<SelectService.Result<VariantsObjects>> {
    }

    public static class ObjectsResponse extends GeneralResponse<SelectService.Result<FindObjects>> {

    }

    public static class Result<T> {
        List<T> objects;

        public List<T> getObjects() {
            return objects;
        }
    }
}

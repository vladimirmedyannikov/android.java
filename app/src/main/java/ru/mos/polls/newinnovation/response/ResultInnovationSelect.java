package ru.mos.polls.newinnovation.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

import ru.mos.polls.newinnovation.Innovation;

/**
 * Формат ответа сервиса {@link ru.mos.polls.rxhttp.api.AgApi#select(Map)} описывает список городских новинок
 *
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 17.05.17 11:22.
 */

public class ResultInnovationSelect {
    @SerializedName("novelties")
    private List<Innovation> innovations;

    public List<Innovation> getInnovations() {
        return innovations;
    }
}

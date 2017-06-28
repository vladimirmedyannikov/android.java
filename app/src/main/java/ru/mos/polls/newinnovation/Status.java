package ru.mos.polls.newinnovation;

import com.google.gson.annotations.SerializedName;

/**
 * Статус городской новинки {@link Innovation}
 * @see #ACTIVE - доступные для оценки
 * @see #PASSED - оцененные
 * @see #OLD - пропущенные, неоцененные пользователем
 *
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.04.17 11:53.
 */

public enum Status {
    @SerializedName("active")
    ACTIVE,
    @SerializedName("passed")
    PASSED,
    @SerializedName("old")
    OLD
}

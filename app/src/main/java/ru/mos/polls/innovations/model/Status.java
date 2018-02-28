package ru.mos.polls.innovations.model;

import com.google.gson.annotations.SerializedName;

/**
 * Статус городской новинки {@link Innovation}
 * @see #ACTIVE - доступные для оценки
 * @see #PASSED - оцененные
 * @see #OLD - пропущенные, неоцененные пользователем
 *
 */

public enum Status {
    @SerializedName("active")
    ACTIVE,
    @SerializedName("passed")
    PASSED,
    @SerializedName("old")
    OLD
}

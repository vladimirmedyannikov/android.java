package ru.mos.polls.newdb;

import android.arch.persistence.room.TypeConverter;

import ru.mos.polls.rxhttp.rxapi.model.novelty.Status;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 03.06.17 14:42.
 */

public class Converters {

    @TypeConverter
    public static Status statusFrom(final String value) {
        return Status.valueOf(value.toUpperCase());
    }

    @TypeConverter
    public static String statusTo(Status status) {
        return status.name();
    }

}

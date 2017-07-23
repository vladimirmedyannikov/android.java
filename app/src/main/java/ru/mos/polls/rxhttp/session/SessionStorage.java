package ru.mos.polls.rxhttp.session;

import android.support.annotation.Nullable;

public interface SessionStorage {

    @Nullable
    String getSession();

    void setSession(@Nullable String session);

    boolean hasSession();

    void clear();
}

package ru.mos.polls.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.mos.polls.newdb.AppDatabase;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.06.17 11:21.
 */
@Module
class DBModule {

    @Provides
    @Singleton
    AppDatabase provide(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "ag-database").build();
    }
}

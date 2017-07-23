package ru.mos.polls.newdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import ru.mos.polls.rxhttp.rxapi.model.novelty.Innovation;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 02.06.17 11:19.
 */
@Database(entities = {Innovation.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public abstract InnovationDao innovationDao();
}

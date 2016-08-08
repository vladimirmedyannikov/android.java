package ru.mos.polls.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PollsHelper extends SQLiteOpenHelper {

    public PollsHelper(Context context) {
        super(context, PollsSchema.DB_NAME, null, PollsSchema.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        PollsSchema.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("drop view if exists view_group");
            db.execSQL(PollsSchema.SQL_CREATE_POLLGROUP_VIEW);
        }
        /**
         * в третьей версии бд в таблицу point_history добавляем столбец action
         * удаляем данные из point_history
         */
        if (oldVersion < 3) {
            db.execSQL(PollsSchema.SQL_CLEAR_POINT_HISTORY);
            db.execSQL(PollsSchema.SQL_ADD_ACTION_TO_POINT_HISTORY);
        }
        /**
         * с пятой версии бд, список голосований не кэшируется,
         * удаляем ненужные таблицы
         */
        if (oldVersion < 5) {
            db.execSQL(PollsSchema.SQL_DROP_POLLS);
            db.execSQL(PollsSchema.SQL_DROP_POLLS_GROUPS);
            db.execSQL(PollsSchema.SQL_DROP_QUESTIONS);
            db.execSQL(PollsSchema.SQL_DROP_VARIANTS);
        }
    }
}

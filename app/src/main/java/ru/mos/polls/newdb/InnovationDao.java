package ru.mos.polls.newdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.mos.polls.rxhttp.rxapi.model.novelty.Innovation;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 02.06.17 11:27.
 */
@Dao
public interface InnovationDao {

    @Query("SELECT * FROM innovation WHERE id = :id")
    Innovation getById(int id);

    @Query("SELECT * FROM innovation")
    List<Innovation> all();

    @Query("SELECT * FROM innovation LIMIT :num,:size")
    List<Innovation> getByPage(int num, int size);

    @Query("SELECT count(*) FROM innovation")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Innovation> innovations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Innovation innovation);

    @Delete
    void delete(Innovation innovation);

    @Query("DELETE FROM innovation")
    void deleteAll();
}

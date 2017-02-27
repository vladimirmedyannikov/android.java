package ru.mos.polls.geotarget.manager;

import java.util.List;

import ru.mos.polls.geotarget.model.Area;

/**
 * @since 2.3.0
 */

public interface AreasManager {

    void save(List<Area> areas);

    List<Area> get();

    void clear();

    void remove(int id);

}

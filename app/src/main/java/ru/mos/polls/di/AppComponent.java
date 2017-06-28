package ru.mos.polls.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.mos.polls.rxhttp.rxapi.config.AgApi;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.06.17 11:13.
 */
@Singleton
@Component(modules = {ApiModule.class/*, DBModule.class*/})
public interface AppComponent {

    AgApi api();
/**
 * Пока не получается добавить в компоенет зависимость на бд с использованием {@link android.arch.persistence.room.Room}
 */
//    AppDatabase db(Context context);

}

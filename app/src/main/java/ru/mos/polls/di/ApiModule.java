package ru.mos.polls.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.mos.polls.rxhttp.rxapi.config.AgApi;
import ru.mos.polls.rxhttp.rxapi.config.AgApiBuilder;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.06.17 11:15.
 */
@Module
class ApiModule {

    @Provides
    @Singleton
    AgApi provide() {
        return AgApiBuilder.build();
    }
}
package ru.mos.polls.rxhttp;

import android.content.Context;
import android.os.Bundle;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.newinnovation.service.NoveltySelect;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

/**
 * Пример исопльзования выполнения запросов
 *
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 28.06.17 8:42.
 */

public class ExampleApi {

    /**
     * Для вызова расскоментировать в {@link ru.mos.polls.MainActivity#onCreate(Bundle)}
     * @param context
     */
    public void testLoadInnovationDetails(Context context) {
        Progressable progressable = new Progressable() {
            @Override
            public void begin() {

            }

            @Override
            public void end() {

            }
        };

        HandlerApiResponseSubscriber<NoveltySelect.Response.Result> handler
                = new HandlerApiResponseSubscriber<NoveltySelect.Response.Result>(context, progressable) {

            @Override
            protected void onResult(NoveltySelect.Response.Result result) {

            }
        };

        AGApplication
                .api
                .noveltySelect(new NoveltySelect.Request(new Page()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}

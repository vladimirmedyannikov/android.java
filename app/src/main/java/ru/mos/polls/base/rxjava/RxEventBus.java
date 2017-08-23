package ru.mos.polls.base.rxjava;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by wlTrunks on 14.06.2017.
 */

public class RxEventBus {
    /**
     * Класс для отлавливания событий вместо броадкастов
     */
    public RxEventBus() {
    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObserverable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}

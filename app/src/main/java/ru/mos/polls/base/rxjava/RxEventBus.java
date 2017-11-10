package ru.mos.polls.base.rxjava;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
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
    private final BackpressureStrategy mBackpressureStrategy = BackpressureStrategy.BUFFER;

    public void send(Object o) {
        bus.onNext(o);
    }

    public Flowable<Object> toObserverable() {
        return bus.toFlowable(mBackpressureStrategy);
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }

    public <T> Flowable<T> filteredObservable(final Class<T> eventClass) {
        return bus.ofType(eventClass).toFlowable(mBackpressureStrategy);
    }
}

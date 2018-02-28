package ru.mos.polls.base.rxjava;

import io.reactivex.subscribers.DisposableSubscriber;

public abstract class RxEventDisposableSubscriber extends DisposableSubscriber {

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }

}

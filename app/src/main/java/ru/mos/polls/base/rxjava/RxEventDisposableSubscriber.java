package ru.mos.polls.base.rxjava;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Trunks on 10.11.2017.
 */

public abstract class RxEventDisposableSubscriber extends DisposableSubscriber {

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }

}

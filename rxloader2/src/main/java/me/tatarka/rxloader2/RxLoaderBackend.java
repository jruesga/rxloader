package me.tatarka.rxloader2;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;

/**
 * @author Evan Tatarka
 */
interface RxLoaderBackend {
    <T> CachingWeakRefSubscriber<T> get(String tag);

    <T> void put(String tag, BaseRxLoader<T> rxLoader, CachingWeakRefSubscriber<T> subscriber);

    <T> void setSave(String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef);

    void unsubscribeAll();

    void clearAll();
}

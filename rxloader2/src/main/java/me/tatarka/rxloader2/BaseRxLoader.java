package me.tatarka.rxloader2;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

abstract class BaseRxLoader<T> {
    private RxLoaderBackend manager;
    private String tag;
    private RxLoaderObserver<T> observer;
    private Scheduler scheduler;
    private SaveCallback<T> saveCallback;

    BaseRxLoader(RxLoaderBackend manager, String tag, RxLoaderObserver<T> observer) {
        scheduler = AndroidSchedulers.mainThread();
        this.manager = manager;
        this.tag = tag;
        this.observer = observer;

        CachingWeakRefSubscriber<T> subscription = manager.get(tag);
        if (subscription != null) {
            subscription.set(observer);
        }
    }

    protected BaseRxLoader<T> start(Observable<T> observable) {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber == null) {
            manager.put(tag, this, createSubscriber(observable));
        }
        return this;
    }

    protected BaseRxLoader<T> restart(Observable<T> observable) {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber != null) {
            subscriber.dispose();
        }
        manager.put(tag, this, createSubscriber(observable));
        if (saveCallback != null) {
            manager.setSave(tag, observer, new WeakReference<>(saveCallback));
        }
        return this;
    }

    protected void notifyError(Throwable cause) {
        observer.onError(cause);
    }

    protected BaseRxLoader<T> save(SaveCallback<T> saveCallback) {
        this.saveCallback = saveCallback;
        manager.setSave(tag, observer, new WeakReference<>(saveCallback));
        return this;
    }

    protected BaseRxLoader<T> save() {
        return save(new ParcelableSaveCallback<T>());
    }

    private CachingWeakRefSubscriber<T> createSubscriber(Observable<T> observable) {
        CachingWeakRefSubscriber<T> subscriber = new CachingWeakRefSubscriber<>(observer);
        observable.observeOn(scheduler).subscribe(subscriber);
        return subscriber;
    }

    /**
     * Cancels the task.
     *
     * @return true if the task was started, false otherwise
     */
    public boolean unsubscribe() {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber != null) {
            subscriber.dispose();
            return true;
        }
        return false;
    }

    /**
     * Clears the loader's state. After a configuration change you will no longer received cached
     * values and {@link #start(io.reactivex.Observable)} will cause the observable to be executed again.
     * This is useful if the loader is handling transient state (showing a Toast for example).
     */
    public void clear() {
        CachingWeakRefSubscriber<T> subscriber = manager.get(tag);
        if (subscriber != null) {
            subscriber.clear();
        }
    }
}

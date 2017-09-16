package me.tatarka.rxloader2;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

class CachingWeakRefSubscriber<T> implements Observer<T>, Disposable {
    private WeakReference<RxLoaderObserver<T>> subscriberRef;
    private Disposable disposable;
    private SaveCallback<T> saveCallback;
    private boolean isCleared;
    private boolean isComplete;
    private boolean isError;
    private boolean hasValue;
    private Throwable error;
    private T lastValue;

    CachingWeakRefSubscriber(RxLoaderObserver<T> observer) {
        set(observer);
        isCleared = false;
    }

    public void set(RxLoaderObserver<T> observer) {
        subscriberRef = new WeakReference<>(observer);
        if (observer == null) return;

        if (!isCleared && !(isComplete || isError)) {
            observer.onStarted();
        }

        if (hasValue) {
            observer.onNext(lastValue);
        }

        if (isComplete) {
            observer.onComplete();
        } else if (isError) {
            observer.onError(error);
        }
    }

    public void setSave(SaveCallback<T> callback) {
        saveCallback = callback;
        if (callback == null) return;

        if (hasValue) {
            callback.onNext(lastValue);
        }
    }

    public Observer<T> get() {
        return subscriberRef.get();
    }

    @Override
    public void onComplete() {
        isComplete = true;
        Observer<T> subscriber = subscriberRef.get();
        if (subscriber != null) subscriber.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        isError = true;
        error = e;
        Observer<T> subscriber = subscriberRef.get();
        if (subscriber != null) subscriber.onError(e);
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T value) {
        hasValue = true;
        lastValue = value;
        Observer<T> subscriber = subscriberRef.get();
        if (subscriber != null) subscriber.onNext(value);
        if (saveCallback != null) saveCallback.onNext(value);
    }

    @Override
    public void dispose() {
        saveCallback = null;
        subscriberRef.clear();
        if (disposable != null) disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        Observer<T> subscriber = subscriberRef.get();
        return subscriber == null;
    }

    public void clear() {
        dispose();
        isCleared = true;
        isComplete = false;
        isError = false;
        hasValue = false;
        error = null;
        lastValue = null;
    }

    interface SaveCallback<T> {
        void onNext(T value);
    }
}

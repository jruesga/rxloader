package me.tatarka.rxloader2;

import io.reactivex.Observable;

/**
 * Manages a given {@link io.reactivex.Observable}, handling activity destruction, orientation changes, and
 * posting to the UI thread. You construct one using {@link me.tatarka.rxloader2.RxLoaderManager#create(String,
 * io.reactivex.Observable, RxLoaderObserver)}.
 *
 * @param <T> the observable's value type
 */
public class RxLoader<T> extends BaseRxLoader<T> {
    private Observable<T> observable;

    RxLoader(RxLoaderBackend manager, String tag, Observable<T> observable, RxLoaderObserver<T> observer) {
        super(manager, tag, observer);
        this.observable = observable;
    }

    /**
     * Starts the {@link io.reactivex.Observable} by subscribing to it. If the {@code Observable} is already
     * started, then does nothing.
     *
     * @return the {@code RxLoader} for chaining
     */
    public RxLoader<T> start() {
        start(observable);
        return this;
    }

    /**
     * Restarts the {@link io.reactivex.Observable} by subscribing to it, unsubscribing first if it has
     * already been started.
     *
     * @return the {@code RxLoader} for chaining
     */
    public RxLoader<T> restart() {
        restart(observable);
        return this;
    }

    /**
     * Saves the last value that the {@link io.reactivex.Observable} returns in {@link
     * io.reactivex.Observer#onNext(Object)} in the Activities'ss ore Fragment's instanceState bundle. When
     * the {@code Activity} or {@code Fragment} is recreated, then the value will be redelivered.
     *
     * The value <b>must</b> implement {@link android.os.Parcelable}. If not, you should use {@link
     * me.tatarka.rxloader2.RxLoader#save(SaveCallback)} to save and restore the value yourself.
     *
     * @return the {@code RxLoader} for chaining
     */
    public RxLoader<T> save() {
        super.save();
        return this;
    }

    /**
     * Saves the last value that the {@link io.reactivex.Observable} returns in {@link
     * io.reactivex.Observer#onNext(Object)} in the Activities's ore Fragment's instanceState bundle. When the
     * {@code Activity} or {@code Fragment} is recreated, then the value will be redelivered.
     *
     * @param saveCallback the callback to handle saving and restoring the value
     * @return the {@code RxLoader} for chaining
     */
    public RxLoader<T> save(SaveCallback<T> saveCallback) {
        super.save(saveCallback);
        return this;
    }
}

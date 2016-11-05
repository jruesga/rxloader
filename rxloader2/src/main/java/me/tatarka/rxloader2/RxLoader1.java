package me.tatarka.rxloader2;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * A version of {@link me.tatarka.rxloader2.RxLoader} that accepts an argument to construct the
 * observable.
 *
 * @param <A> the argument type
 * @param <T> the observable's value type
 * @see me.tatarka.rxloader2.RxLoader
 */
public class RxLoader1<A, T> extends BaseRxLoader<T> {
    private Function<A, Observable<T>> observableFunc;

    RxLoader1(RxLoaderBackend manager, String tag,
              Function<A, Observable<T>> observableFunc, RxLoaderObserver<T> observer) {
        super(manager, tag, observer);
        this.observableFunc = observableFunc;
    }

    /**
     * Starts the {@link io.reactivex.Observable} with the given argument.
     *
     * @param arg1 the argument
     * @return the {@code RxLoader1} for chaining
     * @see RxLoader#start()
     */
    public RxLoader1<A, T> start(A arg1) {
        try {
            start(observableFunc.apply(arg1));
        } catch (Throwable cause) {
            notifyError(cause);
        }
        return this;
    }

    /**
     * Restarts the {@link io.reactivex.Observable} with the given argument.
     *
     * @param arg1 the argument
     * @return the {@code RxLoader1} for chaining
     * @see RxLoader#restart()
     */
    public RxLoader1<A, T> restart(A arg1) {
        try {
            restart(observableFunc.apply(arg1));
        } catch (Throwable cause) {
            notifyError(cause);
        }
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
     * @return the {@code RxLoader1} for chaining
     */
    public RxLoader1<A, T> save() {
        super.save();
        return this;
    }

    /**
     * Saves the last value that the {@link io.reactivex.Observable} returns in {@link
     * io.reactivex.Observer#onNext(Object)} in the Activities's ore Fragment's instanceState bundle. When the
     * {@code Activity} or {@code Fragment} is recreated, then the value will be redelivered.
     *
     * @param saveCallback the callback to handle saving and restoring the value
     * @return the {@code RxLoader1} for chaining
     */
    public RxLoader1<A, T> save(SaveCallback<T> saveCallback) {
        super.save(saveCallback);
        return this;
    }
}

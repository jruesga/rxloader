package me.tatarka.rxloader.sample;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by evan on 8/22/14.
 */
public class SampleObservables {
    private static final String TAG = "RxLoader Sample";
    
    public static Observable<String> delay() {
        return Observable.timer(2, TimeUnit.SECONDS).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                Log.d(TAG, "2 second delay!");
                return "Async Complete!";
            }
        });
    }
    
    public static Function<String, Observable<String>> inputDelay() {
        return new Function<String, Observable<String>>() {
            @Override
            public Observable<String> apply(final String input) throws Exception {
                return Observable.timer(2, TimeUnit.SECONDS).map(new Function<Long, String>() {
                    @Override
                    public String apply(Long aLong) throws Exception {
                        Log.d(TAG, "2 second delay! [" + input + "]");
                        return "Async Complete! [" + input + "]";
                    }
                });
            }
        };
    }

    public static Observable<Long> count() {
        return Observable.interval(100, TimeUnit.MILLISECONDS).doOnEach(new Consumer<Notification<Long>>() {
            @Override
            public void accept(Notification<Long> longNotification) throws Exception {
                Log.d(TAG, "tick!");
            }
        });
    }
}

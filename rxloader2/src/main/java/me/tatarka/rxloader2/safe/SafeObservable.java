/*
 * Copyright (C) 2016 Jorge Ruesga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.tatarka.rxloader2.safe;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * An observable helper to create safe observables that doesn't throw uncatched exceptions.
 */
public class SafeObservable {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SchedulerSupport(SchedulerSupport.NONE)
    public static <T> Observable<T> fromCallable(Callable<? extends T> supplier) {
        ObjectHelper.requireNonNull(supplier, "supplier is null");
        return RxJavaPlugins.onAssembly(new SafeObservableFromCallable<>(supplier));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SchedulerSupport(SchedulerSupport.NONE)
    public static <T> Observable<T> fromNullCallable(Callable<? extends T> supplier) {
        ObjectHelper.requireNonNull(supplier, "supplier is null");
        return RxJavaPlugins.onAssembly(new NullSafeObservableFromCallable<>(supplier));
    }

}

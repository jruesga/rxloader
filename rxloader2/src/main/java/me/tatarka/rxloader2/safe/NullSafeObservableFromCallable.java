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

/**
 * This is a version from {@link io.reactivex.internal.operators.observable.ObservableFromCallable}
 * that doesn't raise exceptions when subscription is disposed (otherwise exceptions
 * can not be consumed, and are directly send to the vm) and doesn't complain about {@link null}
 * references. Instead, it returns a {@link Boolean#TRUE} reference}.
 */
class NullSafeObservableFromCallable<T> extends SafeObservableFromCallable<T> {
    NullSafeObservableFromCallable(Callable<? extends T> callable) {
        super(callable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T call() throws Exception {
        T result = callable.call();
        if (result == null) {
            // Caller isn't going to check anything because it expect
            // a null return from this. Just return a TRUE object, so
            // rx2 doesn't complaint about a null ref.
            return (T) Boolean.TRUE;
        }
        return result;
    }
}


package me.tatarka.rxloader2;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;

/**
 * Persists the task by running it in a fragment with {@code setRetainInstanceState(true)}. This is
 * used internally by {@link RxLoaderManager}.
 *
 * @author Evan Tatarka
 */
public class RxLoaderBackendNestedFragment extends Fragment implements RxLoaderBackend {
    private WeakReference<RxLoaderBackendFragmentHelper> helperRef;
    private List<PendingPut<?>> pendingPuts = new ArrayList<>();
    private boolean hasSavedState;
    private boolean wasDetached;
    private String stateId;

    private RxLoaderBackendFragmentHelper getHelper() {
        if (helperRef != null) {
            return helperRef.get();
        } else {
            Activity activity = getActivity();
            if (activity == null) {
                return null;
            }

            RxLoaderBackendFragment backendFragment = (RxLoaderBackendFragment) activity
                    .getFragmentManager().findFragmentByTag(RxLoaderManager.FRAGMENT_TAG);
            if (backendFragment == null) {
                backendFragment = new RxLoaderBackendFragment();
                activity.getFragmentManager().beginTransaction()
                        .add(backendFragment, RxLoaderManager.FRAGMENT_TAG)
                        .commit();
            }

            RxLoaderBackendFragmentHelper helper = backendFragment.getHelper();
            setHelper(helper);
            helperRef = new WeakReference<>(helper);
            return helper;
        }
    }

    private void setHelper(RxLoaderBackendFragmentHelper helper) {
        helperRef = new WeakReference<>(helper);
        for (PendingPut pendingPut: pendingPuts) {
            put(pendingPut.tag, pendingPut.rxLoader, pendingPut.subscriber);
        }
        pendingPuts.clear();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.onCreate(getStateId(), savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!hasSavedState) {
            RxLoaderBackendFragmentHelper helper = getHelper();
            if (helper != null) {
                helper.onDestroy(getStateId());
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.onDetach(getStateId());
        }
        pendingPuts.clear();
        wasDetached = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        hasSavedState = true;
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.onSaveInstanceState(outState);
        }
    }

    @Override
    public <T> CachingWeakRefSubscriber<T> get(String tag) {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            return helper.get(getStateId(), tag);
        }
        return null;
    }

    @Override
    public <T> void put(String tag, BaseRxLoader<T> rxLoader, CachingWeakRefSubscriber<T> subscriber) {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.put(getStateId(), tag, wasDetached ? null : rxLoader, subscriber);
        } else {
            pendingPuts.add(new PendingPut<T>(tag, rxLoader, subscriber));
        }
    }

    @Override
    public <T> void setSave(String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.setSave(getStateId(), tag, observer, saveCallbackRef);
        }
    }

    @Override
    public void unsubscribeAll() {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.unsubscribeAll(getStateId());
        }
    }

    @Override
    public void clearAll() {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.clearAll(getStateId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.onDestroyView(getStateId());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private String getStateId() {
        if (stateId != null) {
            return stateId;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            throw new IllegalStateException(
                    "NestedFragments are unsupported. Use the compat version.");
        }

        Fragment parentFragment = getParentFragment();
        stateId = parentFragment.getTag();
        if (stateId == null) {
            int id = parentFragment.getId();
            if (id > 0) {
                stateId = Integer.toString(id);
            }
        }

        if (stateId == null) {
            throw new IllegalStateException("Fragment does not have a valid id");
        }
        
        return stateId;
    }

    private static class PendingPut<T> {
        String tag;
        BaseRxLoader<T> rxLoader;
        CachingWeakRefSubscriber<T> subscriber;

        private PendingPut(String tag, BaseRxLoader<T> rxLoader, CachingWeakRefSubscriber<T> subscriber) {
            this.tag = tag;
            this.rxLoader = rxLoader;
            this.subscriber = subscriber;
        }
    }
}

package me.tatarka.rxloader2;

import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.Observer;

/**
 * Persists the task by running it in a fragment with {@code setRetainInstanceState(true)}. This is
 * used internally by {@link me.tatarka.rxloader2.RxLoaderManager}.
 *
 * @author Evan Tatarka
 */
public class RxLoaderBackendNestedFragmentCompat extends Fragment implements RxLoaderBackend {
    private WeakReference<RxLoaderBackendFragmentHelper> helperRef;
    private List<PendingPut<?>> pendingPuts = new ArrayList<>();
    private boolean hasSavedState;
    private boolean wasDetached;
    private String stateId;
    
    private RxLoaderBackendFragmentHelper getHelper() {
        if (helperRef != null) {
            return helperRef.get();
        } else {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                return null;
            }

            RxLoaderBackendFragmentCompat backendFragment = (RxLoaderBackendFragmentCompat) activity
                    .getSupportFragmentManager().findFragmentByTag(RxLoaderManager.FRAGMENT_TAG);
            if (backendFragment == null) {
                backendFragment = new RxLoaderBackendFragmentCompat();
                if (!(activity.getSupportFragmentManager().isStateSaved())) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .add(backendFragment, RxLoaderManager.FRAGMENT_TAG)
                            .commit();
                }
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
            pendingPuts.add(new PendingPut<>(tag, rxLoader, subscriber));
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

    private String getStateId() {
        if (stateId != null) {
            return stateId;
        }

        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            stateId = parentFragment.getTag();
            if (stateId == null) {
                int id = parentFragment.getId();
                if (id > 0) {
                    stateId = Integer.toString(id);
                }
            }
        }

        if (stateId == null) {
            throw new IllegalStateException("Fragment dose not have a valid id");
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

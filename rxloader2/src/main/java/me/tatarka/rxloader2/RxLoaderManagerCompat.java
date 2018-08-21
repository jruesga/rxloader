package me.tatarka.rxloader2;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import static me.tatarka.rxloader2.RxLoaderManager.FRAGMENT_TAG;

/**
 * Get an instance of {@link me.tatarka.rxloader2.RxLoaderManager} that works with the support
 * library.
 *
 * @author Evan Tatarka
 */
public final class RxLoaderManagerCompat {
    private RxLoaderManagerCompat() {

    }

    /**
     * Get an instance of {@code RxLoaderManager} that is tied to the lifecycle of the given {@link
     * FragmentActivity}.
     *
     * @param activity the activity
     * @return the {@code RxLoaderManager}
     */
    public static RxLoaderManager get(FragmentActivity activity) {
        RxLoaderBackendFragmentCompat manager = (RxLoaderBackendFragmentCompat) activity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (manager == null) {
            manager = new RxLoaderBackendFragmentCompat();
            if (!(activity.getSupportFragmentManager().isStateSaved())) {
                activity.getSupportFragmentManager().beginTransaction().add(manager, FRAGMENT_TAG)
                        .commit();
            }
        }
        return new RxLoaderManager(manager);
    }

    /**
     * Get an instance of {@code RxLoaderManager} that is tied to the lifecycle of the given {@link
     * Fragment}.
     *
     * @param fragment the fragment
     * @return the {@code RxLoaderManager}
     */
    public static RxLoaderManager get(Fragment fragment) {
        RxLoaderBackendNestedFragmentCompat manager = (RxLoaderBackendNestedFragmentCompat) fragment.getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (manager == null) {
            manager = new RxLoaderBackendNestedFragmentCompat();
            if (!(fragment.getChildFragmentManager().isStateSaved())) {
                fragment.getChildFragmentManager().beginTransaction().add(manager, FRAGMENT_TAG)
                        .commit();
            }
        }
        return new RxLoaderManager(manager);
    }

}

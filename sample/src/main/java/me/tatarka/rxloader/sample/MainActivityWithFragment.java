package me.tatarka.rxloader.sample;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

public class MainActivityWithFragment extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new MainFragment())
                    .commit();
        }
    }
}

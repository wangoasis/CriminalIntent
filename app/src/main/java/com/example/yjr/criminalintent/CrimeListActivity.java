package com.example.yjr.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by yjr on 2015/3/17.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}

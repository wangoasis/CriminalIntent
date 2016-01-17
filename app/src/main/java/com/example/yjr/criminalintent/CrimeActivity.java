package com.example.yjr.criminalintent;

import android.support.v4.app.Fragment;

import java.util.UUID;


public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        UUID id = (UUID)getIntent().getSerializableExtra(CrimeFragment.extraCrimeID);
        return CrimeFragment.newInstance(id);
    }
}

package com.example.yjr.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by yjr on 2015/3/20.
 */
public class CrimePageActivity extends FragmentActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPager = new ViewPager(this);
        viewPager.setId(R.id.viewPager);
        setContentView(viewPager);

        final ArrayList<Crime> crimes = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                UUID id = crimes.get(i).getID();
                return CrimeFragment.newInstance(id);
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i2) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//
//                Crime crime = crimes.get(i);
//                if (crime.getTitle() != null) {
//                    setTitle(crime.getTitle());
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });

        viewPager.setOffscreenPageLimit(1);

        UUID id = (UUID) getIntent().getSerializableExtra(CrimeFragment.extraCrimeID);
        for (int i = 0; i < crimes.size(); i++) {
            if (crimes.get(i).getID().equals(id)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }
}

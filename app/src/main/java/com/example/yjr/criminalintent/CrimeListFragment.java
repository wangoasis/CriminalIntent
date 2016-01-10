package com.example.yjr.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yjr on 2015/3/17.
 */
public class CrimeListFragment extends ListFragment {

    private ArrayList<Crime> crimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimeList_title);
        crimes = CrimeLab.get(getActivity()).getCrimes();

        CrimeAdapter crimeListAdapter = new CrimeAdapter(crimes);
        setListAdapter(crimeListAdapter);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // get the crime from the adapter
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);

        // start an instance of CrimePagerActivity
        Intent i = new Intent(getActivity(), CrimePageActivity.class);
        i.putExtra(CommonStrings.extraCrimeID, c.getID());
        startActivityForResult(i, 0);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), android.R.layout.simple_list_item_1, crimes);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_layout, null);
            }

            // configure the view for this crime
            Crime crime = getItem(position);

            TextView listItemTitle = (TextView)convertView.findViewById(R.id.list_item_edit_title);
            listItemTitle.setText(crime.getTitle());
            TextView listItemDetail = (TextView)convertView.findViewById(R.id.list_item_edit_detail);
            listItemDetail.setText(crime.getDate().toString());

            CheckBox listItemIsSolved = (CheckBox)convertView.findViewById(R.id.list_item_checkbox);

            listItemIsSolved.setChecked(crime.isSolved());

            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i = new Intent(getActivity(), CrimePageActivity.class);
                i.putExtra(CommonStrings.extraCrimeID, crime.getID());
                startActivityForResult(i, CommonStrings.REQUEST_CRIME);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package com.example.yjr.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by yjr on 2015/3/17.
 */
public class CrimeFragment extends Fragment {


    private Crime crime;
    private EditText editTitle;
    private Button buttonDetails;
    private CheckBox checkboxIsSolved;

    public static CrimeFragment newInstance(UUID id) {

        Bundle args = new Bundle();
        args.putSerializable(CommonStrings.extraCrimeID, id);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);

        return crimeFragment;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            editTitle.removeTextChangedListener(textWatcher);
            editTitle.setText(s.toString());
            editTitle.setSelection(s.length());
            editTitle.addTextChangedListener(textWatcher);
        }

        @Override
        public void afterTextChanged(Editable s) {
            crime.setTitle(s.toString());
        }
    };

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTitle = (EditText)v.findViewById(R.id.edit_crime_title);
        editTitle.setText(crime.getTitle());
        editTitle.setSelection(editTitle.getText().length());
        editTitle.addTextChangedListener(textWatcher);

        buttonDetails = (Button)v.findViewById(R.id.button_crime_detail);
        buttonDetails.setText(crime.getDate().toString());
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                //DatePickerFragment datePickerFragment = new DatePickerFragment();
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(crime.getDate());
                datePickerFragment.setTargetFragment(CrimeFragment.this, CommonStrings.REQUEST_CODE_DIALOG);
                datePickerFragment.show(fragmentManager, CommonStrings.DIALOG_DATE);
            }
        });

        checkboxIsSolved = (CheckBox)v.findViewById(R.id.checkbox_crime);
        checkboxIsSolved.setChecked(crime.isSolved());
        checkboxIsSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID id = (UUID) getArguments().getSerializable(CommonStrings.extraCrimeID);
        crime = CrimeLab.get(getActivity()).getCrime(id);

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == CommonStrings.REQUEST_CODE_DIALOG) {

            Date date = (Date) data.getSerializableExtra(CommonStrings.DIALOG_DATE);

            crime.setDate(date);
            buttonDetails.setText(crime.getDate().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }
}

package com.example.yjr.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    public static final String extraCrimeID = "crime_id";
    public static final String DIALOG_DATE = "dialog_date";
    public static final String DIALOG_IMAGE = "dialog_image";

    public static final int REQUEST_DIALOG_DATE = 0;
    public static final int REQUEST_PHOTO = 1;
    public static final int REQUEST_CONTACT = 2;

    private Crime crime;
    private EditText editTitle;
    private Button buttonDetails;
    private CheckBox checkboxIsSolved;
    private ImageButton buttonTakePhoto;
    private ImageView photoView;
    private Button buttonSendReport;
    private Button buttonChooseContact;

    public static CrimeFragment newInstance(UUID id) {

        Bundle args = new Bundle();

        // need a special UUID to create a crime
        args.putSerializable(extraCrimeID, id);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);

        return crimeFragment;
    }

    // the listener of EditText which inputs the crime title
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            // to avoid iteration and the destrction of activity, must implement this method like this
            // based on my trial, runs successfully
            editTitle.removeTextChangedListener(textWatcher);
            editTitle.setText(s.toString());
            editTitle.setSelection(s.length());
            editTitle.addTextChangedListener(textWatcher);
        }

        @Override
        public void afterTextChanged(Editable s) {

            // get the result of EditText, set the title of crime
            crime.setTitle(s.toString());
        }
    };

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // the EditText for users to input the title of crime
        editTitle = (EditText)v.findViewById(R.id.edit_crime_title);
        // once the crime have a previous title, shows it on the EditText
        editTitle.setText(crime.getTitle());
        // set the selection on the rightest location of text
        editTitle.setSelection(editTitle.getText().length());
        editTitle.addTextChangedListener(textWatcher);

        // the button to select the date
        // click the button will create a dialogFragment to select the date
        buttonDetails = (Button)v.findViewById(R.id.button_crime_detail);
        buttonDetails.setText(crime.getDate().toString());
        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create a dialogFragment for selecting the date
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                // the dialogFragment must use previous date to present
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(crime.getDate());
                // after selecting the date and click "confirm", the dialogFragment will return a result to target Fragment
                datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DIALOG_DATE);
                datePickerFragment.show(fragmentManager, DIALOG_DATE);
            }
        });

        // a checkbox to set whether the crime has been solved
        checkboxIsSolved = (CheckBox)v.findViewById(R.id.checkbox_crime);
        checkboxIsSolved.setChecked(crime.isSolved());
        checkboxIsSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        // a button to start the Camera activity
        buttonTakePhoto = (ImageButton)v.findViewById(R.id.crime_imageButton);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        // a ImageView to show a scale-down image of crime photo
        photoView = (ImageView)v.findViewById(R.id.crime_imageView);
        // create a ImageFragment, display the whole image
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = crime.getPhoto();
                if(photo==null)
                    return;

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String filename = getActivity().getFileStreamPath(photo.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(filename).show(fm, DIALOG_IMAGE);
            }
        });

        // create a context menu for users to delete the photo when clicking for a long period
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // nothing acts when there is no photo and the ImageView is empty
                if(crime.getPhoto()==null)
                    return false;

                ActionMode actionMode = getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    // display a context menu, which contains a icon of deleting
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater1 = mode.getMenuInflater();
                        inflater1.inflate(R.menu.crime_list_item_context, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    // delete the photo of crime, and from the disk
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete_crime:
                                deletePrePhotoOnDisk();
                                // delete from the model
                                crime.setPhoto(null);
                                // set the ImageView empty
                                photoView.setImageDrawable(null);
                                mode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });

                return true;
            }
        });

        buttonSendReport = (Button)v.findViewById(R.id.button_crime_report);
        buttonSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.crime_report_subject);
                startActivity(intent);
            }
        });

        buttonChooseContact = (Button)v.findViewById(R.id.button_crime_contact);
        buttonChooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });
        if(crime.getSuspect()!=null)
            buttonChooseContact.setText(crime.getSuspect());

        return v;
    }

    // set the format of the crime report
    private String getCrimeReport() {

        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    // display a scale-down photo on the ImageView once the crime have a photo
    private void showPhoto() {

        Photo p = crime.getPhoto();
        if(p != null) {
            String filename = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            BitmapDrawable b = PictureUtils.getScaledDrawable(getActivity(), filename);
            photoView.setImageDrawable(b);
        }
    }

    // onCreate, get the UUID to create a crime
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID id = (UUID) getArguments().getSerializable(extraCrimeID);
        crime = CrimeLab.get(getActivity()).getCrime(id);

        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // get the result of selecting the date
        if (requestCode == REQUEST_DIALOG_DATE) {

            Date date = (Date) data.getSerializableExtra(DIALOG_DATE);

            crime.setDate(date);
            buttonDetails.setText(crime.getDate().toString());
        } else if(requestCode == REQUEST_PHOTO) 
        // get the result of taking photos
            {

            // get the picture name of CrimeCameraFragment
            String filename = data.getStringExtra(CrimeCameraFragment.PHOTO_FILENAME);
            Photo photo = new Photo(filename);

            deletePrePhotoOnDisk();
            crime.setPhoto(photo);
            showPhoto();
        } else if(requestCode == REQUEST_CONTACT)
        // get the result of choosing from contacts
            {
                Uri contactUri = data.getData();

                Cursor cursor = getActivity().getContentResolver().query(contactUri,
                                                                         new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                                                                         null, null, null);
                if(cursor.getCount()==0) {
                    cursor.close();
                    return;
                }

                cursor.moveToFirst();
                String suspectName = cursor.getString(0);
                crime.setSuspect(suspectName);
                buttonChooseContact.setText(suspectName);
                cursor.close();
        }
    }

    // delete photo on the disk
    private void deletePrePhotoOnDisk() {

        Photo photo = crime.getPhoto();
        if(photo!=null) {
            String preFilename = photo.getFilename();
            String preFilePath = getActivity().getFileStreamPath(preFilename).getAbsolutePath();
            File preImage = new File(preFilePath);
            preImage.delete();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            case R.id.crime_fragment_delete:
                // display a alertDialog to confirm or cancel the action of deleting
                new AlertDialog.Builder(getActivity()).
                        setTitle(R.string.alert_question).
                        setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePrePhotoOnDisk();
                        CrimeLab.get(getActivity()).deleteCrime(crime);
                        getActivity().finish();
                    }
                }).
                        setNegativeButton(R.string.label_cancel, null).
                        show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // save the crime information to local json file
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    // shows photo on the ImageView
    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    // clear the ImageView
    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanPhotoView(photoView);
    }
}

package com.example.yjr.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {

    public static final String FILE_PATH = "file_path";

    private ImageView mImageView;

    public static ImageFragment newInstance(String filename) {

        Bundle args = new Bundle();
        args.putSerializable(FILE_PATH, filename);

        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setArguments(args);

        imageFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return imageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mImageView = new ImageView(getActivity());
        String filename = (String) getArguments().getSerializable(FILE_PATH);
        BitmapDrawable bitmapDrawable = PictureUtils.getScaledDrawable(getActivity(), filename);
        mImageView.setImageDrawable(bitmapDrawable);
        return mImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanPhotoView(mImageView);
    }
}

package com.example.yjr.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CrimeCameraFragment extends Fragment {

    public static final String PHOTO_FILENAME = "photo_file_name";

    private Camera mCamera;
    private View mProgressBarContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takeButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCamera != null)
                    mCamera.takePicture(shutterCallback, null, pictureCallback);
            }
        });

        SurfaceView mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(mCamera!=null) {
                    try {
                        mCamera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                if(mCamera==null)
                    return;

                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes());
                parameters.setPreviewSize(s.width, s.height);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                if(mCamera!=null) {
                    mCamera.stopPreview();
                }
            }
        });

        mProgressBarContainer = v.findViewById(R.id.progress_camera_container);
        mProgressBarContainer.setVisibility(View.INVISIBLE);

        return v;
    }

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

            mProgressBarContainer.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String filename = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream os = null;
            boolean success = true;

            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            } finally {

                if(os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        success = false;
                        e.printStackTrace();
                    }
                }
            }

            if(success) {

                Intent intent = new Intent();
                intent.putExtra(PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, intent);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizeList) {

        Camera.Size bestSize = sizeList.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for(Camera.Size s : sizeList) {
            int area = s.width * s.height;
            if(area > largestArea) {
                largestArea = area;
                bestSize = s;
            }
        }
        return bestSize;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open(0);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mCamera!=null) {
            mCamera.release();
            mCamera = null;
        }
    }
}

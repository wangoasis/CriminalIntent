package com.example.yjr.criminalintent;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

public class CrimeCameraFragment extends Fragment {

    private Camera mCamera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takeButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
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

        return v;
    }

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

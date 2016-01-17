package com.example.yjr.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class PictureUtils {

    public static BitmapDrawable getScaledDrawable(Activity activity, String filename) {

        float deskHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        float deskWidth = activity.getWindowManager().getDefaultDisplay().getWidth();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        float srcHeight = options.outHeight;
        float srcWidth = options.outWidth;

        int inSampleSize = 1;

        if(srcHeight > deskHeight || srcWidth > deskWidth) {
            if(srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / deskHeight);
            } else {
                inSampleSize = Math.round(srcWidth / deskWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap bitmap = BitmapFactory.decodeFile(filename, options);
        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void cleanPhotoView(ImageView imageView) {

        if(!(imageView.getDrawable() instanceof BitmapDrawable))
            return;

        BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}

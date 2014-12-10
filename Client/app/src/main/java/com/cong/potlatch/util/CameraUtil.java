package com.cong.potlatch.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.cong.potlatch.ui.CreateGiftActivity;

import static com.cong.potlatch.util.LogUtils.LOGD;
import static com.cong.potlatch.util.LogUtils.makeLogTag;

/**
 * Created by cong on 11/26/14.
 */
public class CameraUtil {

    private static final String TAG = makeLogTag(CameraUtil.class);
    private static final int CAMERA_PIC_REQUEST = 0;


    public static void takePicture(Activity context) {
        //                intent = new Intent(this, CreateGiftActivity.class);
//                startActivity(intent);
//                finish();
        //  - Create a new intent to launch the MediaStore, Image capture function
        // Hint: use standard Intent from MediaStore class
        // See: http://developer.android.com/reference/android/provider/MediaStore.html
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //  - Set the imagePath for this image file using the pre-made function
        // getOutputMediaFile to create a new filename for this specific image;
        Uri path = FileUtils.getOutputMediaFileUri(FileUtils.MEDIA_TYPE_IMAGE);

        PrefUtils.setImageLocation(context, path.toString());
        //  - Add the filename to the Intent as an extra. Use the Intent-extra name
        // from the MediaStore class, EXTRA_OUTPUT
        i.putExtra(MediaStore.EXTRA_OUTPUT, path);

        //  - Start a new activity for result, using the new intent and the request
        // code CAMERA_PIC_REQUEST
        context.startActivityForResult(i, CAMERA_PIC_REQUEST);
    }
    public static void onActivityResult(Activity context,int requestCode, int resultCode,Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                LOGD(TAG,"Captured OK " + PrefUtils.getImageLocation(context));

                Intent intent = new Intent(context, CreateGiftActivity.class);
                context.startActivity(intent);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                LOGD(TAG, "Captured cancelled");
            } else {
                // Image capture failed, advise user
                LOGD(TAG, "Captured failed");
            }
        }
    }

}

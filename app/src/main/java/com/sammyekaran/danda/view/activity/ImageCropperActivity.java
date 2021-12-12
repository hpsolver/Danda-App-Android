package com.sammyekaran.danda.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sammyekaran.danda.utils.BitmapUtils;
import com.sammyekaran.danda.R;
import com.fenchtose.nocropper.BitmapResult;
import com.fenchtose.nocropper.CropInfo;
import com.fenchtose.nocropper.CropMatrix;
import com.fenchtose.nocropper.CropResult;
import com.fenchtose.nocropper.CropState;
import com.fenchtose.nocropper.CropperCallback;
import com.fenchtose.nocropper.CropperView;
import com.fenchtose.nocropper.ScaledCropper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class ImageCropperActivity extends AppCompatActivity {

    private static final String TAG = "ImageCropperActivity";
    CropperView mImageView;

    private Bitmap originalBitmap;
    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;

    private int rotationCount = 0;

    private HashMap<String, CropMatrix> matrixMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);
        mImageView = findViewById(R.id.cropperView);
        mImageView.setGestureEnabled(true);
        loadNewImage(getIntent().getStringExtra("filePath"));

        findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageAsync();
            }
        });
        findViewById(R.id.rotate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });

        findViewById(R.id.snap_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapImage();
            }
        });


        mImageView.setDebug(true);
        mImageView.setGridCallback(new CropperView.GridCallback() {
            @Override
            public boolean onGestureStarted() {
                return true;
            }

            @Override
            public boolean onGestureCompleted() {
                return false;
            }
        });
    }

    private void loadNewImage(String filePath) {
        rotationCount = 0;
        Log.i(TAG, "load image: " + filePath);
        mBitmap = BitmapFactory.decodeFile(filePath);
        originalBitmap = mBitmap;
        Log.i(TAG, "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float) maxP / 1280;
        Log.i(TAG, "scaled: " + scale1280 + " - " + (1 / scale1280));

        if (mImageView.getWidth() != 0) {
            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
                    return true;
                }
            });

        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                (int) (mBitmap.getHeight() / scale1280), true);

        mImageView.setImageBitmap(mBitmap);
        final CropMatrix matrix = matrixMap.get(filePath);
        if (matrix != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mImageView.setCropMatrix(matrix, true);
                }
            }, 30);
        }
    }


    private void cropImageAsync() {
        CropState state = mImageView.getCroppedBitmapAsync(new CropperCallback() {
            @Override
            public void onCropped(Bitmap bitmap) {
                if (bitmap != null) {
                    try {
                        File dataDirectory = new File(getExternalFilesDir(null), "/danda");
                        if (!dataDirectory.exists()) {
                            dataDirectory.mkdirs();
                        }
                        String filePath = dataDirectory.getAbsolutePath() +"/"+ System.currentTimeMillis() + ".jpg";
                        File file = new File(filePath);
                        BitmapUtils.writeBitmapToFile(bitmap, file, 90);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("uri", file.getAbsolutePath());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onOutOfMemoryError() {

            }
        });

        if (state == CropState.FAILURE_GESTURE_IN_PROCESS) {
            Toast.makeText(this, "unable to crop. Gesture in progress", Toast.LENGTH_SHORT).show();
        }
    }

    private void cropImage() {

        BitmapResult result = mImageView.getCroppedBitmap();

        if (result.getState() == CropState.FAILURE_GESTURE_IN_PROCESS) {
            Toast.makeText(this, "unable to crop. Gesture in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = result.getBitmap();

        if (bitmap != null) {
            Log.d("Cropper", "crop1 bitmap: " + bitmap.getWidth() + ", " + bitmap.getHeight());
            try {
                BitmapUtils.writeBitmapToFile(bitmap, new File(Environment.getExternalStorageDirectory() + "/crop_test.jpg"), 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cropOriginalImage();


    }

    private ScaledCropper prepareCropForOriginalImage() {
        CropResult result = mImageView.getCropInfo();
        if (result.getCropInfo() == null) {
            return null;
        }

        float scale;
        if (rotationCount % 2 == 0) {
            // same width and height
            scale = (float) originalBitmap.getWidth() / mBitmap.getWidth();
        } else {
            // width and height are interchanged
            scale = (float) originalBitmap.getWidth() / mBitmap.getHeight();
        }

        CropInfo cropInfo = result.getCropInfo().rotate90XTimes(mBitmap.getWidth(), mBitmap.getHeight(), rotationCount);
        return new ScaledCropper(cropInfo, originalBitmap, scale);
    }

    private void cropOriginalImage() {
        if (originalBitmap != null) {
            ScaledCropper cropper = prepareCropForOriginalImage();
            if (cropper == null) {
                return;
            }

            Bitmap bitmap = cropper.cropBitmap();
            if (bitmap != null) {
                try {
                    BitmapUtils.writeBitmapToFile(bitmap, new File(Environment.getExternalStorageDirectory() + "/crop_test_info_orig.jpg"), 90);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cropOriginalImageAsync() {
        if (originalBitmap != null) {
            ScaledCropper cropper = prepareCropForOriginalImage();
            if (cropper == null) {
                return;
            }

            cropper.crop(new CropperCallback() {
                @Override
                public void onCropped(Bitmap bitmap) {
                    if (bitmap != null) {
                        try {
                            BitmapUtils.writeBitmapToFile(bitmap, new File(Environment.getExternalStorageDirectory() + "/crop_test_info_orig.jpg"), 90);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
        rotationCount++;
    }

    private void snapImage() {
        if (isSnappedToCenter) {
            mImageView.cropToCenter();
        } else {
            mImageView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }
}

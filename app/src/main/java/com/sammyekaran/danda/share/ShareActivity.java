package com.sammyekaran.danda.share;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.sammyekaran.danda.R;
import com.sammyekaran.danda.base.BaseActivity;
import com.sammyekaran.danda.model.Album;
import com.sammyekaran.danda.utils.BitmapUtils;
import com.sammyekaran.danda.utils.GalleryHelper;
import com.sammyekaran.danda.view.adapter.SectionsPagerAdapter;
import com.fenchtose.nocropper.CropState;
import com.fenchtose.nocropper.CropperCallback;
import com.fenchtose.nocropper.CropperView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ShareActivity extends BaseActivity {
    private static final String TAG = "ShareActivity";



    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ViewPager mViewPager;
    private boolean isSnappedToCenter = false;
    private CropperView mImageView;
    private ImageView mGifImageView;
    public static Bitmap mBitmap;
    public static String mGifPath;
    private Spinner directorySpinner;
    private GalleryFragment galleryFragment;
    private GifFragment gifFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mImageView = findViewById(R.id.cropperView);
        mGifImageView = findViewById(R.id.imageViewGif);

        if (checkPermissionsArray(PERMISSIONS)) {
            setupViewPager();
        } else {
            verifyPermissions(PERMISSIONS);
        }

        findViewById(R.id.snap_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snapImage();
            }
        });

        findViewById(R.id.rotate_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });
        findViewById(R.id.tvNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentTabNumber()==0){
                    cropImageAsync();
                }else if(getCurrentTabNumber()==1){
                    File file = new File(mGifPath);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("uri", file.getAbsolutePath());
                    resultIntent.putExtra("type", "G");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }

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


    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for manager the tabs
     */
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        galleryFragment = new GalleryFragment();
        gifFragment = new GifFragment();
        adapter.addFragment(galleryFragment);
        adapter.addFragment(gifFragment);

        directorySpinner = findViewById(R.id.spinnerDirectory);
        mViewPager = findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.images));
        tabLayout.getTabAt(1).setText(getString(R.string.gif));

        //for first time
        setGallerySpinnerAdapter(directorySpinner);

        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int position = tab.getPosition();
                        if (position == 0) {
                            findViewById(R.id.snap_button).setVisibility(View.VISIBLE);
                            findViewById(R.id.rotate_button).setVisibility(View.VISIBLE);
                            mImageView.setVisibility(View.VISIBLE);
                            mGifImageView.setVisibility(View.GONE);
                            setGallerySpinnerAdapter(directorySpinner);
                        } else if (position == 1) {
                            findViewById(R.id.snap_button).setVisibility(View.GONE);
                            findViewById(R.id.rotate_button).setVisibility(View.GONE);
                            mImageView.setVisibility(View.GONE);
                            mGifImageView.setVisibility(View.VISIBLE);
                            setGallerySpinnerAdapter(directorySpinner);
                        }
                    }
                });
    }

    private void setGallerySpinnerAdapter(Spinner directorySpinner) {
        ArrayList<Album> album = new ArrayList<>();
        album.add(new Album("Gallery", ""));
        album.addAll(GalleryHelper.getAlbums(this));

        final ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < album.size(); i++) {
            directoryNames.add(album.get(i).getAlbumName());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directoryNames.get(position));
                if (mViewPager.getCurrentItem() == 0)
                    galleryFragment.setupGridView(directoryNames.get(position));
                else
                    gifFragment.setupGridView(directoryNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public int getTask() {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * verifiy all the permissions passed to the array
     *
     * @param permissions
     */
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     *
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    private void snapImage() {
        if (isSnappedToCenter) {
            mImageView.cropToCenter();
        } else {
            mImageView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
        //    Glide.with(this).load(mBitmap).into(mImageView);

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
                        String filePath = dataDirectory.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
                        File file = new File(filePath);
                        BitmapUtils.writeBitmapToFile(bitmap, file, 90);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("uri", file.getAbsolutePath());
                        resultIntent.putExtra("type", "I");
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

}

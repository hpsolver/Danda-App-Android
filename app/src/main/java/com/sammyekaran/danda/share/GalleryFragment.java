package com.sammyekaran.danda.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sammyekaran.danda.R;
import com.sammyekaran.danda.model.Image;
import com.sammyekaran.danda.utils.GalleryHelper;
import com.fenchtose.nocropper.CropMatrix;
import com.fenchtose.nocropper.CropperView;
import com.sammyekaran.danda.model.Album;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    //widgets
    private RecyclerView gridView;
    private Spinner directorySpinner;
    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";

    private int rotationCount = 0;
    private HashMap<String, CropMatrix> matrixMap = new HashMap<>();

    CropperView mImageView;

    private Bitmap originalBitmap;
    private boolean isSnappedToCenter = false;
    private ArrayList<Image> mImages;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        gridView = view.findViewById(R.id.gridView);

        mImageView = ((ShareActivity) getActivity()).findViewById(R.id.cropperView);
        directories = new ArrayList<>();

        ImageView shareClose = ((ShareActivity) getActivity()).findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    private boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    getFile(file);
                } else {
                    if ((file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) && file.length() > 0)
                    {
                        String temp = file.getPath().substring(0, file.getPath().lastIndexOf('/'));
                        if (!directories.contains(temp))
                            directories.add(temp);
                    }
                }
            }
        }
        return directories;
    }


    public void setupGridView(String albumName) {
        final ArrayList<String> imgURLs = new ArrayList<>();
        mImages = new ArrayList<>();
        if (albumName.equals("Gallery")) {
            mImages.addAll(GalleryHelper.getAllShownImagesPath(getActivity()));
        } else {
            mImages.addAll(GalleryHelper.getImagesOfAlbum(getActivity(), albumName));
        }
        Log.d(TAG, "setupGalleryView: directory chosen: " + albumName +  mImages.size() + " "+ "k");

        for (int i = 0; i < mImages.size(); i++) {
            imgURLs.add(mImages.get(i).getImagePath());
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        gridView.setLayoutManager(layoutManager);
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), imgURLs, new GridImageAdapter.ItemClick() {
            @Override
            public void onItemClick(int position) {
                if (BitmapFactory.decodeFile(imgURLs.get(position)) != null) {
                    loadNewImage(imgURLs.get(position));
                } else {
                    Toast.makeText(getActivity(), "File format not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try {

            if (imgURLs.size() > 0) {
                if (BitmapFactory.decodeFile(imgURLs.get(0)) != null) {
                    loadNewImage(imgURLs.get(0));
                } else if (BitmapFactory.decodeFile(imgURLs.get(1)) != null) {
                    loadNewImage(imgURLs.get(1));
                } else if (BitmapFactory.decodeFile(imgURLs.get(2)) != null) {
                    loadNewImage(imgURLs.get(2));
                } else {
                    Toast.makeText(getActivity(), "File format not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No Image data founded", Toast.LENGTH_SHORT).show();
            }


        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }


    }



    private void loadNewImage(String filePath) {
        rotationCount = 0;
        Log.i(TAG, "load image: " + filePath);
        ShareActivity.mBitmap = BitmapFactory.decodeFile(filePath);
        originalBitmap = ShareActivity.mBitmap;
        Log.i(TAG, "bitmap: " + ShareActivity.mBitmap.getWidth() + " " + ShareActivity.mBitmap.getHeight());

        int maxP = Math.max(ShareActivity.mBitmap.getWidth(), ShareActivity.mBitmap.getHeight());
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

        ShareActivity.mBitmap = Bitmap.createScaledBitmap(ShareActivity.mBitmap, (int) (ShareActivity.mBitmap.getWidth() / scale1280),
                (int) (ShareActivity.mBitmap.getHeight() / scale1280), true);

        mImageView.setImageBitmap(ShareActivity.mBitmap);
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
}

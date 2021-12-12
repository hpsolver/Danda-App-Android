package com.sammyekaran.danda.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sammyekaran.danda.R;
import com.sammyekaran.danda.model.Album;
import com.sammyekaran.danda.model.Image;
import com.sammyekaran.danda.utils.GalleryHelper;

import java.io.File;
import java.util.ArrayList;

import static com.sammyekaran.danda.share.ShareActivity.mGifPath;

public class GifFragment extends Fragment {
    private static final String TAG = "GifFragment";

    //widgets
    private RecyclerView recyclerView;
    private Spinner directorySpinner;
    //vars
    private ArrayList<String> directories;
    ImageView mImageView;
    private ArrayList<Image> mImages;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gif, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        directorySpinner = ((ShareActivity) getActivity()).findViewById(R.id.spinnerDirectory);
        mImageView = ((ShareActivity) getActivity()).findViewById(R.id.imageViewGif);
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
                    if (file.getName().endsWith(".gif")||file.getName().endsWith(".GIF") && file.length() > 0)
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
        Log.d(TAG, "setupGridView: directory chosen: " + albumName);
        final ArrayList<String> imgURLs = new ArrayList<>();
        mImages = new ArrayList<>();
        if (albumName.equals("Gallery")) {
            mImages.addAll(GalleryHelper.getAllShownGifPath(getActivity()));
        } else {
            mImages.addAll(GalleryHelper.getGifsOfAlbum(getActivity(), albumName));
        }

        for (int i = 0; i < mImages.size(); i++) {
            imgURLs.add(mImages.get(i).getImagePath());
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), imgURLs, new GridImageAdapter.ItemClick() {
            @Override
            public void onItemClick(int position) {
                if (BitmapFactory.decodeFile(imgURLs.get(position)) != null) {
                    mGifPath=imgURLs.get(position);
                    loadNewImage(imgURLs.get(position));
                } else {
                    Toast.makeText(getActivity(), "File format not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try {

            if (imgURLs.size() > 0) {
                if (BitmapFactory.decodeFile(imgURLs.get(0)) != null) {
                    mGifPath=imgURLs.get(0);
                    loadNewImage(imgURLs.get(0));
                } else if (BitmapFactory.decodeFile(imgURLs.get(1)) != null) {
                    mGifPath=imgURLs.get(1);
                    loadNewImage(imgURLs.get(1));
                } else if (BitmapFactory.decodeFile(imgURLs.get(2)) != null) {
                    mGifPath=imgURLs.get(2);
                    loadNewImage(imgURLs.get(2));
                } else {
                    Toast.makeText(getActivity(), "File format not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "No Gif data founded", Toast.LENGTH_SHORT).show();
                mImageView.setImageResource(android.R.color.transparent);
            }


        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }

    }

    private void loadNewImage(String filePath) {
        Log.i(TAG, "load image: " + filePath);
        ShareActivity.mBitmap = BitmapFactory.decodeFile(filePath);
        Log.i(TAG, "bitmap: " + ShareActivity.mBitmap.getWidth() + " " + ShareActivity.mBitmap.getHeight());
        int maxP = Math.max(ShareActivity.mBitmap.getWidth(), ShareActivity.mBitmap.getHeight());
        float scale1280 = (float) maxP / 1280;
        Log.i(TAG, "scaled: " + scale1280 + " - " + (1 / scale1280));

        ShareActivity.mBitmap = Bitmap.createScaledBitmap(ShareActivity.mBitmap, (int) (ShareActivity.mBitmap.getWidth() / scale1280),
                (int) (ShareActivity.mBitmap.getHeight() / scale1280), true);

        Glide.with(getActivity())
                .load(filePath)
                .into(mImageView);
    }
}

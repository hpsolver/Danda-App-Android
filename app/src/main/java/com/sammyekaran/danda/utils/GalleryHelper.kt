package com.sammyekaran.danda.utils

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import androidx.annotation.RequiresApi
import com.sammyekaran.danda.model.Album
import com.sammyekaran.danda.model.Image
import java.io.File


/**
 * Created by Lalit on 10-03-2018.
 */
object GalleryHelper {
    /**
     * This method is used to get all the Albums from the device which contains the image file.
     * [context] an instance of the caller.
     */
    @JvmStatic
    fun getAlbums(context: Context): ArrayList<Album> {
        val albums = ArrayList<Album>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA)
        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val cursor = context.contentResolver.query(uri, projection, null, null, orderBy)
        if (cursor != null) {
            var file: File
            while (cursor.moveToNext()) {
                val albumName = cursor.getString(cursor.getColumnIndex(projection[0]))
                val albumThumb = cursor.getString(cursor.getColumnIndex(projection[1]))
                file = File(albumThumb)
                if (file.exists() && !isAlbumExist(albums, albumName)) {
                    albums.add(Album(albumName, albumThumb))
                }
            }
            cursor.close()
        }
        return albums
    }

    /**
     * This method is used to get all the images of the given album.
     */
    @JvmStatic
    fun getImagesOfAlbum(context: Context, bucketPath: String): ArrayList<Image> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?"
        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"

        val images = ArrayList<Image>()
        val cursor = context.contentResolver.query(uri, projection, selection, arrayOf(bucketPath), orderBy)
        if (cursor != null) {
            var file: File
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(projection[0]))
                file = File(path)
                if (file.exists() && !isImageExist(images, path)) {
                    if (path.endsWith("png") ||
                            path.endsWith("jpeg") ||
                            path.endsWith("jpg"))
                        images.add(Image(path))

                }
            }
            cursor.close()
        }
        return images
    }

    @JvmStatic
    fun getGifsOfAlbum(context: Context, bucketPath: String): ArrayList<Image> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?"
        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"

        val images = ArrayList<Image>()
        val cursor = context.contentResolver.query(uri, projection, selection, arrayOf(bucketPath), orderBy)
        if (cursor != null) {
            var file: File
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(projection[0]))
                file = File(path)
                if (file.exists() && !isImageExist(images, path)) {
                    if (path.endsWith("gif"))
                        images.add(Image(path))
                }
            }
            cursor.close()
        }
        return images
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @JvmStatic
    fun getAllShownImagesPath(activity: Activity): ArrayList<Image> {
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<Image>()
        var absolutePathOfImage: String? = null
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val projection = arrayOf(MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        cursor = activity.contentResolver.query(uri, projection, null,
                null, orderBy)!!
        column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            if (absolutePathOfImage.endsWith("png") ||
                    absolutePathOfImage.endsWith("jpeg") ||
                    absolutePathOfImage.endsWith("jpg"))
                listOfAllImages.add(Image(absolutePathOfImage))
        }
        return listOfAllImages
    }

    @JvmStatic
    fun getAllShownGifPath(activity: Activity): ArrayList<Image>? {
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<Image>()
        var absolutePathOfImage: String? = null
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val projection = arrayOf(MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        cursor = activity.contentResolver.query(uri, projection, null,
                null, orderBy)!!
        column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            if (absolutePathOfImage.endsWith("gif"))
                listOfAllImages.add(Image(absolutePathOfImage))
        }
        return listOfAllImages
    }

    /**
     * It is used to check that whether the album already exist in the list or not.
     */
    private fun isAlbumExist(albums: ArrayList<Album>, albumName: String): Boolean {
        return albums.any { it.albumName == albumName }
    }

    /**
     * It is used to check that whether the image already exist in the list or not
     */
    private fun isImageExist(images: ArrayList<Image>, imagePath: String): Boolean {
        return images.any { it.imagePath == imagePath }
    }
}
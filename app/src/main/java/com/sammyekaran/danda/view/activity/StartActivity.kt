package com.sammyekaran.danda.view.activity

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.FacebookSdk
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseActivity
import com.sammyekaran.danda.databinding.ActivityStartBinding
import com.sammyekaran.danda.share.ShareActivity
import com.sammyekaran.danda.utils.BitmapUtils
import com.sammyekaran.danda.utils.ImageZoomHelper
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.fragment.EditProfileFragment
import com.sammyekaran.danda.view.fragment.HomeFragment
import com.sammyekaran.danda.view.fragment.NotificationFragment
import com.sammyekaran.danda.view.fragment.ProfileFragment
import com.sammyekaran.danda.viewmodel.StartActivityViewModel
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.view_live_dialog.view.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File


class StartActivity : BaseActivity() {


    private val TAG = "StartActivity"
    lateinit var imageZoomHelper: ImageZoomHelper
    val REQUEST_TAKE_GALLERY_VIDEO = 101
    val STORAGE_PERMISSION_CODE = 102
    val VIDEO_CAPTURE = 103
    val SELECT_GIF = 103

    private val REQUEST_CODE_CAMERA = 1001
    private val REQUEST_CODE_GALLERY = 1002
    private val REQUEST_CODE_CROP = 1004
    private val UPDATE_REQUEST_CODE = 1005


    private lateinit var navController: NavController
    val viewModel: StartActivityViewModel by viewModel()
    lateinit var binding: ActivityStartBinding
    lateinit var mMenu: Menu
    var permissionNeeded = 0
    lateinit var filemanagerstring: String
    val sharedPref: SharedPref by inject()
    private var appUpdateManager: AppUpdateManager? = null
    private var listener: InstallStateUpdatedListener? = null
    var doubleBackToExitPressedOnce = false


    val permission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

    companion object {
        const val REQUEST_CODE_UPLOAD = 1003
        val isBlocked="0"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext);
        MobileAds.initialize(this);
        appUpdateManager = AppUpdateManagerFactory.create(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)
        findView()
        navigationListner()
        listner()
        keyboardListner()

        updateListener()
        checkUpdate()

        if (intent.hasExtra("key") && intent.getStringExtra("key").equals("uploadFeed")) {
            navController.popBackStack()
            navController.navigate(R.id.mainFragment)
        }

    }

    private fun updateListener() {
        listener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                showDownloadCompleteSnackbar()
            }
        }
        //Register Listener
        appUpdateManager?.registerListener(listener!!)
    }

    private fun showDownloadCompleteSnackbar() {
        val snackbar = Snackbar
                .make(rootLayout, "New version downloaded successfully!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Install Now") { appUpdateManager?.completeUpdate() }
        snackbar.setActionTextColor(Color.GREEN)
        snackbar.show()

    }

    override fun onResume() {
        super.onResume()

    }

    private fun findView() {


        imageZoomHelper = ImageZoomHelper(this)
        //Getting the Navigation Controller
        navController = Navigation.findNavController(this, R.id.nav_host)
        //Setting the navigation controller to Bottom Nav
        bottomNavigation?.setupWithNavController(navController)

        //Menu
        mMenu = bottomNavigation.menu


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.splashFragment ||
                    destination.id == R.id.forgetPassword || destination.id == R.id.registerFragment ||
                    destination.id == R.id.countryListFragment ||
                    destination.id == R.id.chatFragment || destination.id == R.id.commentFragment ||
                    destination.id == R.id.supportConversationFragment ||
                    destination.id == R.id.resetPasswordFragment
            ) {
                bottomNavigation.visibility = View.GONE
                imageButtonAdd.visibility = View.GONE
            } else {
                bottomNavigation.visibility = View.VISIBLE
                imageButtonAdd.visibility = View.VISIBLE
            }
            if (destination.id == R.id.mainFragment) {
                mMenu.findItem(R.id.action_main).setIcon(R.drawable.ic_icon_home_active)
                mMenu.findItem(R.id.action_search).setIcon(R.drawable.ic_icon_search)
                mMenu.findItem(R.id.action_notification).setIcon(R.drawable.ic_icon_notification)
                mMenu.findItem(R.id.action_profile).setIcon(R.drawable.ic_profile)
            } else if (destination.id == R.id.exploreDataFragment) {
                mMenu.findItem(R.id.action_main).setIcon(R.drawable.ic_icon_home)
                mMenu.findItem(R.id.action_search).setIcon(R.drawable.ic_icon_search_active)
                mMenu.findItem(R.id.action_notification).setIcon(R.drawable.ic_icon_notification)
                mMenu.findItem(R.id.action_profile).setIcon(R.drawable.ic_profile)
            } else if (destination.id == R.id.notificationFragment) {
                mMenu.findItem(R.id.action_main).setIcon(R.drawable.ic_icon_home)
                mMenu.findItem(R.id.action_search).setIcon(R.drawable.ic_icon_search)
                mMenu.findItem(R.id.action_notification).setIcon(R.drawable.ic_icon_notification_active)
                mMenu.findItem(R.id.action_profile).setIcon(R.drawable.ic_profile)
            } else if (destination.id == R.id.profileFragment) {
                mMenu.findItem(R.id.action_main).setIcon(R.drawable.ic_icon_home)
                mMenu.findItem(R.id.action_search).setIcon(R.drawable.ic_icon_search)
                mMenu.findItem(R.id.action_notification).setIcon(R.drawable.ic_icon_notification)
                mMenu.findItem(R.id.action_profile).setIcon(R.drawable.ic_icon_profile_active)
            }
        }

    }

    private fun listner() {
        imageButtonAdd.setOnClickListener {

            if (isBlocked.isEmpty()){
                return@setOnClickListener
            }else if (isBlocked == "1"){

            }else{
                if (checkPermission(permission) > 0) {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            STORAGE_PERMISSION_CODE
                    )
                } else {
                    uploadAndLiveDialog()
                }
            }


        }
    }

    private fun checkUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        Log.d(TAG, "Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Request the update.
                appUpdateManager?.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.FLEXIBLE,
                        // The current activity making the update request.
                        this,
                        // Include a request code to later monitor this update request.
                        UPDATE_REQUEST_CODE)
            } else {
                Log.d(TAG, "No Update available")
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.appzorro.noti")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }


    private fun keyboardListner() {
        KeyboardVisibilityEvent.setEventListener(
                this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    bottomNavigation.visibility = View.GONE
                    imageButtonAdd.visibility = View.GONE
                } else {
                    if (navController.currentDestination?.id == R.id.loginFragment || navController.currentDestination?.id == R.id.splashFragment ||
                            navController.currentDestination?.id == R.id.forgetPassword || navController.currentDestination?.id == R.id.registerFragment ||
                            navController.currentDestination?.id == R.id.countryListFragment ||
                            navController.currentDestination?.id == R.id.chatFragment || navController.currentDestination?.id == R.id.commentFragment ||
                            navController.currentDestination?.id == R.id.supportConversationFragment || navController.currentDestination?.id == R.id.resetPasswordFragment
                    ) {

                    } else {
                        bottomNavigation.visibility = View.VISIBLE
                        imageButtonAdd.visibility = View.VISIBLE
                    }

                }
            }
        })
    }


    private fun navigationListner() {
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_main -> {
                    if (navController.currentDestination?.id == R.id.mainFragment) {

                    } else {
                        navController.navigate(R.id.mainFragment)
                        return@OnNavigationItemSelectedListener true
                    }

                }
                R.id.action_search -> {
                    if (navController.currentDestination?.id == R.id.exploreDataFragment) {

                    } else {
                        navController.navigate(R.id.exploreDataFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                R.id.action_notification -> {
                    if (navController.currentDestination?.id == R.id.notificationFragment) {

                    } else {
                        bottomNavigation.getBadge(R.id.action_notification)?.let { badgeDrawable ->
                            if (badgeDrawable.isVisible)  // check whether the item showing badge
                                bottomNavigation.removeBadge(R.id.action_notification)  //  remove badge notification
                        }
                        NotificationFragment.isApiHit = false
                        navController.navigate(R.id.notificationFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                R.id.action_profile -> {
                    if (navController.currentDestination?.id == R.id.profileFragment) {

                    } else {
                        navController.navigate(R.id.profileFragment)
                        return@OnNavigationItemSelectedListener true
                    }
                }
            }
            false
        }
        bottomNavigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun checkPermission(permissions: Array<String>): Int {
        permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permissions.indices) {
                val result = ContextCompat.checkSelfPermission(this, permissions[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }


    private fun uploadAndLiveDialog() {

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_live_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.window!!.findViewById<View>(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent)
        view.textViewCancel.setOnClickListener { dialog.dismiss() }

        view.textViewTakePhoto.setOnClickListener {
            dialog.dismiss()
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAMERA)
        }

        view.textViewGallery.setOnClickListener {
            dialog.dismiss()
            startActivityForResult(Intent(this@StartActivity, ShareActivity::class.java), REQUEST_CODE_GALLERY)
        }


        view.textViewVideo.setOnClickListener {
            dialog.dismiss()
            selectVideo()
        }

        view.textViewCaptureVideo.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
            intent.putExtra("EXTRA_VIDEO_QUALITY", 0)
            startActivityForResult(intent, VIDEO_CAPTURE)
        }


        dialog.show()
        val lp = WindowManager.LayoutParams()
        val window = dialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }


    private fun selectVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)
    }

    private fun getPath(uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver?.query(uri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            Log.e("HOME_FRAGMENT", "getRealPathFromURI Exception : $e")
            return ""
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            uploadAndLiveDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (navController.currentDestination?.id == R.id.editProfileFragment) {
            val fragment = EditProfileFragment()
            fragment.onActivityResult(requestCode, resultCode, data)
        } else if (navController.currentDestination?.id == R.id.homeFragment) {
            val fragment = HomeFragment()
            fragment.onActivityResult(requestCode, resultCode, data)
        } else if (navController.currentDestination?.id == R.id.profileFragment) {
            val fragment = ProfileFragment()
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        if (resultCode != Activity.RESULT_CANCELED) {

            when (requestCode) {

                REQUEST_CODE_UPLOAD -> {
                    if (navController.currentDestination?.id == R.id.homeFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.homeFragment)
                    } else if (navController.currentDestination?.id == R.id.profileFragment) {
                        navController.popBackStack()
                        navController.navigate(R.id.profileFragment)
                    }
                }

                REQUEST_CODE_CAMERA -> {

                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    val tempUri = BitmapUtils.getImageUri(applicationContext, imageBitmap);

                    startActivityForResult(Intent(this, ImageCropperActivity::class.java)
                            .putExtra("filePath", BitmapUtils.getFilePathFromUri(this, tempUri!!)), REQUEST_CODE_CROP)

                }

                REQUEST_CODE_GALLERY -> {
                    if (data?.extras?.getString("uri") != null) {
                        val arguments = Bundle().apply {
                            putString("path", data.extras?.getString("uri"))
                            putString("type", data.extras?.getString("type"))
                        }
                        startActivityForResult(
                                Intent(this, UploadActivity::class.java).putExtras(arguments),
                                REQUEST_CODE_UPLOAD
                        )

                    }
                }

                REQUEST_CODE_CROP -> {
                    if (data?.extras?.getString("uri") != null) {
                        val arguments = Bundle().apply {
                            putString("path", data.extras?.getString("uri"))
                            putString("type", "I")
                        }
                        startActivityForResult(
                                Intent(this, UploadActivity::class.java).putExtras(arguments),
                                REQUEST_CODE_UPLOAD
                        )

                    }
                }

                VIDEO_CAPTURE -> {
                    val selectedUri = data!!.data
                    val selectedVideoPath = getRealPathFromURI(selectedUri!!)
                    if (selectedVideoPath != null) {
                        val arguments = Bundle().apply {
                            putString("path", selectedVideoPath)
                            putString("type", "V")
                        }

                        startActivityForResult(
                                Intent(this, UploadActivity::class.java).putExtras(arguments),
                                REQUEST_CODE_UPLOAD
                        )
                    }

                }

                REQUEST_TAKE_GALLERY_VIDEO -> {

                    val selectedImageUri = data?.data

                    // OI FILE Manager
                    filemanagerstring = selectedImageUri!!.path!!

                    // MEDIA GALLERY
                    val selectedVideoPath = getPath(selectedImageUri)

                    val f = File(selectedVideoPath)
                    val fileSizeInKb = f.length() / 1024
                    val fileSizeInMb = fileSizeInKb / 1024

                    if (fileSizeInMb > 50) {
                        Toast.makeText(this, "Can't share more than 50 Mb.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (selectedVideoPath != null) {
                            val arguments = Bundle().apply {
                                putString("path", selectedVideoPath)
                                putString("type", "V")
                            }

                            startActivityForResult(
                                    Intent(this, UploadActivity::class.java).putExtras(arguments),
                                    REQUEST_CODE_UPLOAD
                            )
                        }
                    }
                }
            }
        }

    }

    fun showExitDialog() {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setTitle("Exit")
        dialogBuilder.setMessage("Do you want to exit from the App ?")
        dialogBuilder.setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> finishAffinity() }
        dialogBuilder.setNegativeButton("No") { dialog: DialogInterface, which -> dialog.dismiss() }
        dialogBuilder.show()

    }

    fun showAppealDialog() {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setTitle("Alert")
        dialogBuilder.setMessage("Sorry, you can't do anything on Danda as your account is blocked. To appeal, please contact support through info@danda.com")
        dialogBuilder.setPositiveButton("Ok") { dialog: DialogInterface?, which: Int -> finishAffinity() }
        dialogBuilder.show()

    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val count = intent.getStringExtra("count")
            /* bottomNavigation.showBadge(R.id.action_notification).apply {
                number = count.toInt()
                backgroundColor = resources.getColor(R.color.colorPrimaryDark)
                maxCharacterCount = 2}*/

            if (count != null) {
                bottomNavigation.getOrCreateBadge(R.id.action_notification).number = count.toInt()
            }
        }

    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return imageZoomHelper.onDispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }


    override fun onBackPressed() {
        val navigationController = nav_host.findNavController()
        when (navigationController.currentDestination?.id) {
            R.id.mainFragment -> {
                if (doubleBackToExitPressedOnce) {
                    showExitDialog()
                }

                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

                Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
            }
            R.id.chatFragment -> {
                navigationController.navigateUp()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager?.unregisterListener(listener!!)
    }
}

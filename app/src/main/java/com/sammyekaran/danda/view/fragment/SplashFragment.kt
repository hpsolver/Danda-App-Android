package com.sammyekaran.danda.view.fragment

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sammyekaran.danda.R
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import org.koin.android.ext.android.inject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashFragment : Fragment() {
    val sharePref: SharedPref by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_splash, container, false)
        printHashKey()
        nextScreen()
        return view
    }

    fun printHashKey(){
        // Add code to print out the key hash

        // Add code to print out the key hash
        try {
            val info: PackageInfo = activity!!.getPackageManager().getPackageInfo(
                    "com.sammyekaran.danda",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }
    fun nextScreen() {
        Handler().postDelayed({
            if (sharePref.getBoolean(Constants.IS_LOGIN)) {
                findNavController().navigate(R.id.action_splash_to_main)
            } else {
                findNavController().navigate(R.id.action_splash_to_login)
            }
        }, 2000)


    }

}

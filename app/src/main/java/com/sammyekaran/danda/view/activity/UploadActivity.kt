package com.sammyekaran.danda.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.sammyekaran.danda.R
import com.sammyekaran.danda.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_upload.*
import org.koin.android.ext.android.inject

class UploadActivity : AppCompatActivity() {

    lateinit var graph: NavGraph
    lateinit var navController: NavController
    val commonUtils:CommonUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        setStartDestination()
        listner()
    }

    private fun listner() {
        //Getting the Navigation Controller
        navController = Navigation.findNavController(this, R.id.nav_upload)
        imageViewBack.setOnClickListener {
            if (graph.startDestination== navController.currentDestination?.id) {
                commonUtils.hideSoftKeyBoard(this)
                finish()
            } else {
                navController.popBackStack()
            }
        }
    }

    private fun setStartDestination() {

        val intent = intent.extras
        val navHostFragment = nav_upload as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        graph = inflater.inflate(R.navigation.upload_nav_graph)


        val navArgumentPath = NavArgument.Builder().setDefaultValue(intent?.get("path")).build()
        val navArgumentType = NavArgument.Builder().setDefaultValue(intent?.get("type")).build()
        graph.addArgument("path", navArgumentPath)
        graph.addArgument("type", navArgumentType)


        if (intent?.get("type")!! == "I" || intent.get("type")!!.equals("G")) {
            graph.startDestination = R.id.uploadFeedFragment
        } else {
            graph.startDestination = R.id.videoPlayFragment
        }

        navHostFragment.navController.graph = graph
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (graph.startDestination== navController.currentDestination?.id) {
            finish()
        } else {
            navController.popBackStack()
        }
    }
}

package com.sammyekaran.danda.view.fragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentVideoPlayBinding
import kotlinx.android.synthetic.main.fragment_video_play.*


class VideoPlayFragment : BaseFragment<FragmentVideoPlayBinding>() {

    var selectedVideoPath = ""
    lateinit var binding: FragmentVideoPlayBinding


    override fun getLayoutId(): Int {
        return com.sammyekaran.danda.R.layout.fragment_video_play
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()


        val args: VideoPlayFragmentArgs by navArgs()
        selectedVideoPath = args.path

        val uri = Uri.parse(selectedVideoPath)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener {

        })

        listner()
    }

    private fun listner() {
        buttonDone.setOnClickListener {

            val mp = MediaPlayer.create(activity, Uri.parse(selectedVideoPath))
            val duration = mp.duration
            mp.release()

            if (duration > 60000) {
             baseshowFeedbackMessage(rootLayoutVideoPlay,"Select video less than 1 min")
            } else {
                val action = VideoPlayFragmentDirections.actionVideoPlayFragmentToUploadFeedFragment(selectedVideoPath,"V")
                findNavController().navigate(action)
            }


        }

    }
}

package sampel.skycap.com.livedataviewmodel.customBinding

import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import com.sammyekaran.danda.R
import com.bumptech.glide.Glide


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {

    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_icon_avatar)
            .error(R.drawable.ic_icon_avatar)
            .into(view)
    } else {
        view.setImageResource(R.drawable.ic_icon_avatar)
    }
}


@BindingAdapter("bindText")
fun bindFollowUnfollow(view: TextView, type: String) {
    if (type.equals("0")) {
        view.visibility = View.VISIBLE
        view.text = "Follow"
        view.setBackgroundResource(R.drawable.button_effect)
        view.setTextColor(view.context.resources.getColor(R.color.white))
    } else if (type.equals("1")) {
        view.visibility = View.VISIBLE
        view.text = "Unfollow"
        view.setBackgroundResource(R.drawable.unfollow_button_bg)
        view.setTextColor(view.context.resources.getColor(R.color.colorPrimaryDark))
    } else if (type == "3") {
        view.visibility = View.GONE
    } else if (type == "2") {
        view.visibility = View.VISIBLE
        view.text = "Unfollow"
        view.setBackgroundResource(R.drawable.unfollow_button_bg)
        view.setTextColor(view.context.resources.getColor(R.color.colorPrimaryDark))
    }
}


@BindingAdapter("app:goneImageView")
fun goneImageView(view: View, string: String) {
    if (!string.isEmpty())
        view.visibility = if (string.equals("I")) View.VISIBLE else View.GONE
}


@BindingAdapter("app:goneVideoView")
fun goneVideoView(view: View, string: String) {
    view.visibility = if (string.equals("I")) View.GONE else View.VISIBLE
}


@BindingAdapter("postFromUrl")
fun bindPostFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .into(view)
    }
}


@BindingAdapter("setFocus")
fun setFocus(view: EditText, b: Boolean) {
    if (b) {
        view.isEnabled = false
        view.isFocusable = false
        view.setBackgroundResource(0)
    } else {
        view.isEnabled = true
        view.isFocusable = true
        view.setBackgroundResource(R.drawable.rounded_color_primary)
    }
}

@BindingAdapter("videoFromUrl")
fun bindVideoFromUrl(view: VideoView, videoUrl: String?) {

    if (!videoUrl.isNullOrEmpty()) {
        val uri = Uri.parse(videoUrl)
        view.setVideoURI(uri)

    }
}

@BindingAdapter("likeDrawable")
fun likeDrawable(view: ImageView, type: String) {
    if (type.equals("0")) {
        view.setImageResource(R.drawable.ic_icon_like)
    } else {
        view.setImageResource(R.drawable.ic_icon_like_red)
    }


}

@BindingAdapter("visibleOrGone")
fun visibleOrGone( view: ImageView,visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}




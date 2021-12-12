package com.sammyekaran.danda.model.sharePost;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("post_id")
@Expose
private String postId;

public String getPostId() {
return postId;
}

public void setPostId(String postId) {
this.postId = postId;
}

}

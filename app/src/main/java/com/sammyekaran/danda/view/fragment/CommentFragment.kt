package com.sammyekaran.danda.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sammyekaran.danda.R
import com.sammyekaran.danda.base.BaseFragment
import com.sammyekaran.danda.databinding.FragmentCommentBinding
import com.sammyekaran.danda.model.getcomment.Datum
import com.sammyekaran.danda.model.getcomment.GetCommentModel
import com.sammyekaran.danda.model.postcomment.PostCommentModel
import com.sammyekaran.danda.model.tagSuggestion.TagSuggestion
import com.sammyekaran.danda.utils.CommonUtils
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import com.sammyekaran.danda.view.adapter.CommentAdapter
import com.sammyekaran.danda.view.adapter.SuggestionAdapter
import com.sammyekaran.danda.viewmodel.CommentViewModel
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_comment.rvSuggestion
import kotlinx.android.synthetic.main.fragment_search.recyclerView
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class CommentFragment : BaseFragment<FragmentCommentBinding>() {

    lateinit var mAdapter: CommentAdapter
    lateinit var layoutManager: LinearLayoutManager
    lateinit var binding: FragmentCommentBinding
    val viewModel: CommentViewModel by viewModel()
    val sharedPref: SharedPref by inject()
    val commonUtils: CommonUtils by inject()
    var mPostId = ""
    var mCommentsList: MutableList<Datum> = mutableListOf()


    override fun getLayoutId(): Int {
        return R.layout.fragment_comment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = viewModel
        //setRecyclerViewScrollListener()
        val args: CommentFragmentArgs by navArgs()
        mPostId = args.postId
        listner()
        setAdapter(mCommentsList)
        if (mCommentsList.size == 0) {
            viewModel.fetchComments(mPostId)
        }

        editTextCommentTextChangeListner()

    }

    private fun editTextCommentTextChangeListner() {
        editTextComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("After Text", p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Before Text", p0.toString() + " " + p1 + " " + p2 + " " + p3)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("On Text", p0.toString() + " " + p1 + " " + p2 + " " + p3)
                parseText(p0.toString())
            }

        })
    }

    private fun parseText(text: String) {
        val words = text.split("\\s".toRegex())
        if (words[words.size - 1].trim().isNotEmpty() && words[words.size - 1].trim().length > 1 && words[words.size - 1].trim().take(1) == "@") {
            println(words[words.size - 1].substring(1))
            viewModel.tagSuggestion(words[words.size - 1].substring(1))
        }


    }

    private fun listner() {
        textViewPost.setOnClickListener {
            if (!editTextComment.text.toString().trim().isEmpty()) {
                commonUtils.hideSoftKeyBoard(activity!!)

                var tagUser = ""
                if (editTextComment.text!!.contains(" ")) {
                    val words = editTextComment.text?.split("\\s".toRegex())!!.toMutableList()

                    for (item in words) {
                        if (item.startsWith("@")) {
                            tagUser += item.substring(1) + ","
                        }
                    }
                    if (tagUser.isNullOrEmpty()) {
                        tagUser = tagUser.substring(tagUser.length - 1)
                    }
                }else{
                    if (editTextComment.text!!.startsWith("@")) {
                        tagUser += editTextComment.text
                    }
                }

                viewModel.postComment(
                        sharedPref.getString(Constants.USER_ID),
                        mPostId,
                        editTextComment.text.toString(), tagUser.trim()
                )
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserver()

    }

    fun setAdapter(data: List<Datum>?) {
        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView?.isNestedScrollingEnabled = false
        mAdapter = CommentAdapter(data, viewModel)
        recyclerView?.adapter = mAdapter

    }

    private fun initObserver() {
        EditProfileFragment.isApiHit = true

        viewModel.isLoading.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(isLoading: Boolean) {
                if (isLoading) {
                    baseShowProgressBar("")
                } else {
                    baseHideProgressDialog()
                }
            }
        })

        viewModel.tagSuggestion.observe(viewLifecycleOwner, Observer<TagSuggestion> { t ->
            if (t?.response?.status?.equals("1")!!) {

                rvSuggestion.visibility = View.VISIBLE

                val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                rvSuggestion.layoutManager = layoutManager
                val adapter = SuggestionAdapter(t.response?.data, object : SuggestionAdapter.ItemClick {
                    override fun onSuggestionClick(username: String?) {
                        rvSuggestion.visibility = View.GONE

                        val words = editTextComment.text?.split("\\s".toRegex())!!.toMutableList()
                        if (words[words.size - 1].trim().isNotEmpty() && words[words.size - 1].trim().length > 1 && words[words.size - 1].trim().take(1) == "@") {
                            words[words.size - 1].replace(words[words.size - 1], "@" + username)
                            words.removeAt(words.size - 1)
                            words.add("@" + username)
                            val sb = StringBuffer()
                            for (item in words) {
                                sb.append(item + " ")
                            }
                            val str = sb.toString()
                            editTextComment.setText(str)
                            editTextComment.setSelection(editTextComment.text!!.length)
                        }
                    }

                })
                rvSuggestion.adapter = adapter

            } else {
                rvSuggestion.visibility = View.GONE
            }
        })


        viewModel.getAllPost().observe(viewLifecycleOwner, object : Observer<GetCommentModel> {
            override fun onChanged(comments: GetCommentModel) {
                if (comments.response?.status.equals("0")) {
                    //baseshowFeedbackMessage(rootLayoutComment, comments.response?.message!!)
                } else {
                    mCommentsList.clear()
                    for (items in comments.response?.data!!) {
                        mCommentsList.add(items)
                    }
                    mAdapter.customNotify(mCommentsList)
                }
            }

        })
        viewModel.postComment.observe(viewLifecycleOwner, object : Observer<PostCommentModel> {
            override fun onChanged(comment: PostCommentModel) {
                rvSuggestion.visibility=View.GONE
                if (comment.response?.status.equals("0")) {
                    baseshowFeedbackMessage(rootLayoutComment, comment.response?.message!!)
                } else {
                    if (editTextComment.text.toString().isEmpty())
                        return
                    editTextComment.setText("")
                    val data = Datum()
                    data.comment = comment.response?.data?.comment
                    data.followerId = comment.response?.data?.followerId
                    data.id = comment.response?.data?.id
                    data.profilePic = comment.response?.data?.profilePic
                    data.username = comment.response?.data?.username

                    mCommentsList.add(data)
                    mAdapter.customNotify(mCommentsList)
                }
            }

        })

        viewModel.errorListener!!.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                rvSuggestion.visibility=View.GONE
                baseshowFeedbackMessage(rootLayoutComment, message!!)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.getAllPost().removeObservers(this)
    }
}
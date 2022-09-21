package com.vibs.maashakti.zoom_image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.vibs.maashakti.R
import com.vibs.maashakti.zoom_image.zoomage.ZoomageView


class ZoomPhotoFragmentDialog : DialogFragment() {
    private var fView: View? = null

    private lateinit var zivPhoto: ZoomageView
    private lateinit var ivClose: ImageView
    private lateinit var ivPrevious: ImageView
    private lateinit var ivNext: ImageView
    private lateinit var adapter: ZoomPhotosAdapter

    private var imageList: ArrayList<String> = arrayListOf()
    private var currentPosition: Int = 0
    override fun onStart() {
        super.onStart()
        if (dialog!!.window != null) {

            dialog!!.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        );
        imageList = requireArguments().getStringArrayList(PHOTO_URLS) ?: arrayListOf()
        currentPosition = requireArguments().getInt(CURRENT_POSITION) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        fView = inflater.inflate(R.layout.fragment_zoom_photo, container, false)
        initUi()
        return fView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData() {
        if (imageList.count() <= 1) {
            ivNext.visibility = View.GONE
        } else {
            ivNext.visibility = View.VISIBLE
        }
        updateUi(currentPosition)
        setImageForCurrentIndex()
    }

    private fun initUi() {
        ivClose = fView?.findViewById(R.id.ivClose)!!
        ivPrevious = fView?.findViewById(R.id.ivPrevious)!!
        ivNext = fView?.findViewById(R.id.ivNext)!!
        zivPhoto = fView?.findViewById(R.id.zivPhoto)!!

        ivClose.setOnClickListener { dismiss() }

        ivNext.setOnClickListener {
            nextImage()
        }

        ivPrevious.setOnClickListener {
            previousImage()
        }
    }

    private fun previousImage() {
        if (currentPosition == 0) else {
            currentPosition -= 1
        }
        updateUi(currentPosition)
        setImageForCurrentIndex()
    }

    private fun updateUi(currentPosition: Int) {
        when (currentPosition) {
            0 -> {
                ivPrevious.visibility = View.GONE
                ivNext.visibility = View.VISIBLE
            }
            imageList.lastIndex -> {
                ivNext.visibility = View.GONE
                ivPrevious.visibility = View.VISIBLE
            }
            else -> {
                ivNext.visibility = View.VISIBLE
                ivPrevious.visibility = View.VISIBLE
            }
        }
    }


    private fun nextImage() {
        if (currentPosition == imageList.lastIndex) {
            currentPosition = imageList.lastIndex
        } else {
            currentPosition += 1
        }
        updateUi(currentPosition)
        setImageForCurrentIndex()
    }

    private fun setImageForCurrentIndex() {
        zivPhoto.visibility = View.VISIBLE
        Glide.with(requireContext()).load(imageList[currentPosition]).into(zivPhoto)
    }


    companion object {
        const val CURRENT_POSITION = "CURRENT_POSITION"
        const val PHOTO_URLS = "PHOTO_URLS"

        private const val TAG = "ZoomPhotoFragmentDialog"

        fun newInstance(
            imageList: ArrayList<String>,
            currentPosition: Int = 0
        ): ZoomPhotoFragmentDialog {
            val f = ZoomPhotoFragmentDialog()

            // Supply num input as an argument.
            val args = Bundle()
            args.putStringArrayList(PHOTO_URLS, imageList)
            args.putInt(CURRENT_POSITION, currentPosition)
            f.arguments = args
            return f
        }
    }
}
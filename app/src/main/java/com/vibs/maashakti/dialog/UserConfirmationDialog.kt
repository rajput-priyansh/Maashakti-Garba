package com.vibs.maashakti.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.vibs.maashakti.R
import com.vibs.maashakti.api.models.ParticipantData
import com.vibs.maashakti.databinding.DialogUserInfoBinding
import com.vibs.maashakti.databinding.DialogYesNoChoiceBinding
import com.vibs.maashakti.zoom_image.ZoomPhotoFragmentDialog
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_CANCELABLE
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_MESSAGE
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_NEGATIVE_ACTION
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_POSITIVE_ACTION
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_TITLE


class UserConfirmationDialog(private val mCallBAck: BnBConfirmationDialogListener?) :
    DialogFragment() {
    private lateinit var binder: DialogUserInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.white_background_round_corners)
        binder = DialogUserInfoBinding.bind(
            inflater.inflate(
                R.layout.dialog_user_info,
                container,
                false
            )
        )
        initUI()

        return binder.root
    }

    /**
     * USe to init the dialog UI
     */
    private fun initUI() {

        arguments?.getBoolean(DIALOG_CANCELABLE, false)
            ?.let { dialog?.setCancelable(it) }

        arguments?.getString(DIALOG_TITLE)?.let { title ->
            binder.tvTitle.text = title
        }

        arguments?.getParcelable<ParticipantData>(DIALOG_MESSAGE)?.let { userInfo ->

            binder.tvUserName.text = userInfo.userName ?: ""
            binder.tvUserContact.text = userInfo.userContactNo ?: ""
            binder.tvNfcNo.text = userInfo.userNfcNo ?: ""

            if (userInfo.giftBy.isNullOrEmpty()) {
                binder.tvGiftBy.visibility = View.GONE
                binder.lblGiftBy.visibility = View.GONE
            } else {
                binder.tvGiftBy.visibility = View.VISIBLE
                binder.lblGiftBy.visibility = View.VISIBLE
                binder.tvGiftBy.text = userInfo.giftBy
            }

            userInfo.userPhotoUrl?.let { image_url ->
                Glide.with(this).load(image_url).placeholder(R.drawable.ic_image_place_holder).into(binder.ivProfileImage)

                binder.ivProfileImage.setOnClickListener {
                    val list: ArrayList<String> = ArrayList()
                    list.add(image_url)
                    val dialog = ZoomPhotoFragmentDialog.newInstance(list)
                    dialog.show(childFragmentManager, "ZoomImage")
                }
            }
        }

        arguments?.getString(DIALOG_POSITIVE_ACTION)?.let { test ->
            binder.btnYes.text = test
        }

        arguments?.getString(DIALOG_NEGATIVE_ACTION)?.let { text ->
            if (text.isEmpty()) {
                binder.btnNo.visibility = View.GONE
            } else {
                binder.btnNo.visibility = View.VISIBLE
            }
            binder.btnNo.text = text
        }

        binder.btnNo.setOnClickListener {
            mCallBAck?.onNegativeButtonClick()
        }

        binder.btnYes.setOnClickListener {
            mCallBAck?.onPositiveButtonClick()
        }

    }
}
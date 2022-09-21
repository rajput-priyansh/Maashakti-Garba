package com.vibs.maashakti.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.vibs.maashakti.R
import com.vibs.maashakti.databinding.DialogYesNoChoiceBinding
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_CANCELABLE
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_MESSAGE
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_NEGATIVE_ACTION
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_POSITIVE_ACTION
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_TITLE


class BnBConfirmationDialog(private val mCallBAck: BnBConfirmationDialogListener?) :
    DialogFragment() {
    private lateinit var binder: DialogYesNoChoiceBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.white_background_round_corners)
        binder = DialogYesNoChoiceBinding.bind(
            inflater.inflate(
                R.layout.dialog_yes_no_choice,
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

        val message = arguments?.getString(DIALOG_MESSAGE)

        arguments?.getBoolean(DIALOG_CANCELABLE, false)
            ?.let { dialog?.setCancelable(it) }

        arguments?.getString(DIALOG_TITLE)?.let { title ->
            binder.tvTitle.text = title
        }

        binder.tvMessage.text = message

        arguments?.getString(DIALOG_POSITIVE_ACTION)?.let { test ->
            binder.btnYes.text = test
        }

        arguments?.getString(DIALOG_NEGATIVE_ACTION)?.let { text ->
            binder.btnNo.text = text
        }

        binder.tvMessage.visibility = if(!message.isNullOrEmpty()) View.VISIBLE else View.GONE

        binder.btnNo.setOnClickListener {
            mCallBAck?.onNegativeButtonClick()
        }

        binder.btnYes.setOnClickListener {
            mCallBAck?.onPositiveButtonClick()
        }
    }
}
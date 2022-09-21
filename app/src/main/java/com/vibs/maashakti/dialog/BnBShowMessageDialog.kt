package com.vibs.maashakti.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.vibs.maashakti.R
import com.vibs.maashakti.databinding.DialogShowMessageBinding
import com.vibs.weatherdemosdk.base.BaseFragment.Companion.DIALOG_MESSAGE


class BnBShowMessageDialog : DialogFragment() {
    lateinit var binder: DialogShowMessageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.white_background_round_corners)
        val view = inflater.inflate(R.layout.dialog_show_message, container, false)
        binder = DialogShowMessageBinding.bind(view)
        binder.tvMessage.text = arguments?.getString(DIALOG_MESSAGE) ?: ""
        return view
    }
}
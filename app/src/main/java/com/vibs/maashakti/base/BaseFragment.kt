package com.vibs.weatherdemosdk.base

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.vibs.maashakti.api.models.ParticipantData
import com.vibs.maashakti.base.BaseActivity
import com.vibs.maashakti.base.StartActivityCallback
import com.vibs.maashakti.dialog.*


open class BaseFragment(private val resLayout: Int) : Fragment(resLayout) {

    companion object{
        const val DIALOG_MESSAGE = "DIALOG_MESSAGE"
        const val DIALOG_NEGATIVE_ACTION = "DIALOG_NEGATIVE_ACTION"
        const val DIALOG_TITLE = "DIALOG_TITLE"
        const val DIALOG_CANCELABLE = "DIALOG_CANCELABLE"
        const val DIALOG_POSITIVE_ACTION = "DIALOG_POSITIVE_ACTION"
        const val TIMEOUT_SUCCESS_DIALOG = 2000L
    }
    private var progressDialog: ProgressDialog? = null
    private var permissionCallback: PermissionCallback? = null
    private var startActivityCallback: StartActivityCallback? = null
    private var bnBBnBShowMessageDialog: BnBShowMessageDialog? = null
    private var bnBConfirmationDialog: BnBConfirmationDialog? = null
    private var userConfirmationDialog: UserConfirmationDialog? = null

    private var activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            startActivityCallback?.onActivityResult(result)
        }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val grantedPermissions = arrayListOf<String>()
            val deniedPermissions = arrayListOf<String>()

            permissions.entries.forEach { permission ->
                if (permission.value) {
                    grantedPermissions.add(permission.key)
                } else {
                    deniedPermissions.add(permission.key)
                }
            }
            permissionCallback?.onPermissionGranted(grantedPermissions)
            permissionCallback?.onPermissionDenied(deniedPermissions)
        }

    override fun onDestroy() {
        hideProgressDialog()
        bnBBnBShowMessageDialog?.dismiss()
        bnBConfirmationDialog?.dismiss()
        userConfirmationDialog?.dismiss()
        super.onDestroy()
    }

    open fun initData() {

    }

    open fun initUi() {
    }

    open fun observer() {

    }


    open fun openSuccessDialog(
        dialogTag: Int,
        message: String,
        isCancelOnSpecificTime: Boolean = false
    ) {
        bnBBnBShowMessageDialog = BnBShowMessageDialog()
        bnBBnBShowMessageDialog?.arguments = Bundle().apply {
            putString(DIALOG_MESSAGE, message)
        }
        bnBBnBShowMessageDialog?.show(
            parentFragmentManager,
            bnBBnBShowMessageDialog?.javaClass?.name
        )

        if (isCancelOnSpecificTime) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (context != null || activity != null) {
                    bnBBnBShowMessageDialog?.dismiss()
                    onSuccessDialogClose(dialogTag)
                }
            }, TIMEOUT_SUCCESS_DIALOG)
        }
    }

    open fun openConfirmationDialogDialog(
        dialogTag: Int,
        title: String,
        message: String,
        positiveButton: String? = null,
        negativeButton: String? = null,
        cancelable: Boolean = false
    ) {
        bnBConfirmationDialog = BnBConfirmationDialog(object : BnBConfirmationDialogListener {
            override fun onPositiveButtonClick() {
                bnBConfirmationDialog?.dismiss()
                onDialogPositiveButtonClick(dialogTag)
            }

            override fun onNegativeButtonClick() {
                bnBConfirmationDialog?.dismiss()
                onDialogNegativeButtonClick(dialogTag)
            }

        })
        val bundle = Bundle()
        bundle.putString(DIALOG_TITLE, title)
        bundle.putString(DIALOG_MESSAGE, message)
        bundle.putString(DIALOG_POSITIVE_ACTION, positiveButton)
        bundle.putString(DIALOG_NEGATIVE_ACTION, negativeButton)
        bundle.putBoolean(DIALOG_CANCELABLE, cancelable)
        bnBConfirmationDialog?.arguments = bundle
        bnBConfirmationDialog?.show(parentFragmentManager, bnBConfirmationDialog?.javaClass?.name)
    }


            open fun openUserConfirmationDialogDialog(
        dialogTag: Int,
        title: String,
        message: ParticipantData,
        positiveButton: String? = null,
        negativeButton: String? = null,
        cancelable: Boolean = false
    ) {
        userConfirmationDialog = UserConfirmationDialog(object : BnBConfirmationDialogListener {
            override fun onPositiveButtonClick() {
                userConfirmationDialog?.dismiss()
                onDialogPositiveButtonClick(dialogTag)
            }

            override fun onNegativeButtonClick() {
                userConfirmationDialog?.dismiss()
                onDialogNegativeButtonClick(dialogTag)
            }

        })
        val bundle = Bundle()
        bundle.putString(DIALOG_TITLE, title)
        bundle.putParcelable(DIALOG_MESSAGE, message)
        bundle.putString(DIALOG_POSITIVE_ACTION, positiveButton)
        bundle.putString(DIALOG_NEGATIVE_ACTION, negativeButton)
        bundle.putBoolean(DIALOG_CANCELABLE, cancelable)
        userConfirmationDialog?.arguments = bundle
        userConfirmationDialog?.show(parentFragmentManager, userConfirmationDialog?.javaClass?.name)
    }

    /**
     * Use to occur on positive button click
     */
    open fun onDialogPositiveButtonClick(dialogTag: Int) {

    }

    /**
     * Use to occur on negative button click
     */
    open fun onDialogNegativeButtonClick(dialogTag: Int) {

    }


    open fun refreshOnInternet() {

    }

    /**
     * Check whether a permission is granted or not.
     *
     * @param permission
     * @return
     */
    open fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request permissions and get the result on callback.
     *
     * @param permissions - list of permissions
     * @param callback - callback instance
     */
    open fun requestPermission(permissions: Array<String>, callback: PermissionCallback?) {
        this.permissionCallback = callback
        requestPermissionLauncher.launch(permissions)
    }

    /**
     * Request permission and get the result on callback.
     *
     * @param permission - single permission
     * @param callback - callback instance
     */
    open fun requestPermission(permission: String, callback: PermissionCallback?) {
        this.permissionCallback = callback
        requestPermissionLauncher.launch(arrayOf(permission))
    }

    /**
     * @param isDismissOnBack - if false then dialog will not dismiss on destroy
     * Use to display progress dialog
     */
    open fun showProgressDialog(isDismissOnBack: Boolean = true) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(requireContext(), isDismissOnBack)
        }
        progressDialog?.show()
    }

    /**
     * Use to hide progress dialog
     */
    open fun hideProgressDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    fun showNoInternetDialog() {
        (activity as BaseActivity).showNoInternetDialog()
    }

    fun hideNoInternetDialog() {
        (activity as BaseActivity).hideNoInternetDialog()
    }


    /**
     * Use to manage Apis error
     */
    open fun manageOnApiError(message: String?) {
    }

    /**
     * Use to manage Apis success
     */
    open fun manageOnApiSuccess(message: String) {
    }

    open fun startNewActivityForResult(intent: Intent, callback: StartActivityCallback) {
        activityLauncher.launch(intent)
        this.startActivityCallback = callback
    }

    /**
     * Use to occur on success dialog close
     */
    open fun onSuccessDialogClose(dialogTag: Int) {

    }

    fun showToast(msg: String?) {
        if (msg.isNullOrBlank()) return
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}
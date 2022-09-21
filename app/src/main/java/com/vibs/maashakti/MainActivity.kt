package com.vibs.maashakti

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.vibs.maashakti.api.Status
import com.vibs.maashakti.databinding.ActivityMainBinding
import com.vibs.maashakti.nfc.NfcIntentActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

interface MainListener {
    fun currentFragment()
}

@OptIn(ExperimentalUnsignedTypes::class)
class MainActivity : NfcIntentActivity(), MainListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[CardViewModel::class.java]
    }
    
    override val TAG: String
        get() = "MainActivity"

    
    override fun onNfcTag(tag: Tag) {
        //Empty Body
    }

    
    override fun onNfcId(sId: String) {
        if (navController.currentDestination?.id == R.id.FirstFragment) {
            //perform API call
            val map: MutableMap<String, RequestBody> = HashMap()

            val regex = Regex("[^A-Za-z0-9]")
            val result = regex.replace(sId, "")

            map["user_nfc_no"] =
                result.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            map["secret_token"] =
                "mashakti@admin".toRequestBody("multipart/form-data".toMediaTypeOrNull())

            makeVerifyCardData(map)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()

        initUi()

        observer()
    }
    
    override fun initData() {
        super.initData()
    }
    
    override fun initUi() {
        super.initUi()
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC",
                Toast.LENGTH_SHORT).show()
        } else if (!nfcAdapter.isEnabled) {
            // NFC is available for device but not enabled
            startActivity(Intent("android.settings.NFC_SETTINGS"))
        } else {
            /*Toast.makeText(this, "This device support NFC",
                Toast.LENGTH_SHORT).show()*/
        }
    }

    override fun observer() {
        super.observer()

        viewModel.cardVerifyResponse.observe(this) {
            if (it == null)
                return@observe

            if (it.statusCode == 1) {
                it.participantData?.let { item ->
                    openUserConfirmationDialogDialog(
                        98,
                        "Player Info!",
                        item,
                        "Okay",
                        "",
                        false
                    )
                }
            } else {
                openConfirmationDialogDialog(
                    98,
                    "Fail!",
                    it.msg ?: "Something went wrong, Please try again later!",
                    "Okay",
                    "Cancel",
                    false
                )
            }
        }

    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        /*val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()*/

        onBackPressed()
        return true
    }

    /**
     * Make API call to get verify details
     */
    private fun makeVerifyCardData(map: Map<String, RequestBody>) {

        if (!isNetworkConnected) {
            showNoInternetDialog()
            return
        }

        viewModel.makeVerifyCardData(map).observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.SUCCESS -> {
                    hideProgressDialog()
                    viewModel.setCardVerifyResponseData(it.data)
                }
                Status.ERROR -> {
                    viewModel.setCardVerifyResponseData(null)
                    hideProgressDialog()
                }
            }
        }
    }

    override fun currentFragment() {

    }
}
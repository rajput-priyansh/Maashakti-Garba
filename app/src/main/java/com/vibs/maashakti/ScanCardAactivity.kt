package com.vibs.maashakti

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast

import com.vibs.maashakti.nfc.NfcIntentActivity
import com.vibs.maashakti.nfc.byteArrayToHex
import com.vibs.maashakti.nfc.readFromTag

@OptIn(ExperimentalUnsignedTypes::class)
class ScanCardAactivity: NfcIntentActivity() {
    override val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_nfc)
    }

    override fun onResume() {
        super.onResume()
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC NOT SUPPORT", Toast.LENGTH_SHORT).show()

        } else if (!nfcAdapter!!.isEnabled) {
            Toast.makeText(this, "NFC NOT Enabled", Toast.LENGTH_SHORT).show()
        } else {
//            Toast.makeText(this, "NFC is Enabled", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun openNfcSettings(view: View) {
        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
    }

    override fun onNfcId(sId: String) {
        val regex = Regex("[^A-Za-z0-9]")
        val result = regex.replace(sId, "")
        val intent = Intent()
        intent.putExtra(SecondFragment.NFC_CODE, result)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onNfcTag(tag: Tag) {
        val bytes = readFromTag(tag)
        /*if (bytes.isNotEmpty()) {
            val intent = Intent(this, ReadActivity::class.java).apply {
                putExtra(PARCEL_TAG, tag)
                putExtra(PARCEL_TAGDATA, TagData(bytes))
            }
            startActivity(intent)
        } else {
            showReadErrorModalDialog(tag)
        }*/
    }
}
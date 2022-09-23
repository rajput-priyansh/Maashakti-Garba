package com.vibs.maashakti

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.vibs.maashakti.base.BaseActivity
import com.vibs.maashakti.databinding.ActivityLoginBinding
import com.vibs.maashakti.utils.PreferenceManager

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override val TAG: String
        get() = "LoginActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding.btLogin.setOnClickListener {
            if (binding.etUserName.text?.trim()?.isEmpty() == true) {
                binding.etUserName.error = "User Name can not be empty!"
                return@setOnClickListener
            } else {
                binding.etUserName.error = null
            }

            if (binding.etPassword.text?.trim()?.isEmpty() == true) {
                binding.etPassword.error = "Password can not be empty!"
                return@setOnClickListener
            } else {
                binding.etPassword.error = null
            }

            if (binding.etUserName.text?.trim().toString() == "maashaktigarba" && binding.etPassword.text.toString() == "MaaShakti@2022") {
                PreferenceManager.with(this@LoginActivity)
                PreferenceManager.saveUserToken(binding.etUserName.text.toString())
                startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            } else {
                Toast.makeText(this, "Invalid User name or Password!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
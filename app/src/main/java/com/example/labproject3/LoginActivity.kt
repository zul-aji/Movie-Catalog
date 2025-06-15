package com.example.labproject3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.labproject3.data.TokenPreferences
import com.example.labproject3.databinding.ActivityLoginBinding
import com.example.labproject3.ui.user.SigninFragment
import com.example.labproject3.ui.startNewActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenPreferences: TokenPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        tokenPreferences = TokenPreferences(this)

        if (tokenPreferences.getToken() != "") {
            startNewActivity(HomeActivity::class.java)
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_login_fragment, SigninFragment())
            commit()
        }
    }
}
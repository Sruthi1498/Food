package com.example.foodly.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodly.R
import com.example.foodly.util.ConnectionManager.Companion.checkConnectivity
import com.example.foodly.util.Constants
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var submitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences =
            this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        etName = findViewById(R.id.signup_name)
        etEmail = findViewById(R.id.signup_email)
        etMobileNumber = findViewById(R.id.signup_mobile)
        etDeliveryAddress = findViewById(R.id.signup_address)
        etPassword = findViewById(R.id.signup_password)
        etConfirmPassword = findViewById(R.id.signup_password_con)
        submitBtn = findViewById(R.id.sign_up_button)

        submitBtn.setOnClickListener {
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

            if (checkErrors()) {
                if (checkConnectivity(this)) {

                    try {
                        val regUser = JSONObject()
                        regUser.put("name", etName.text)
                        regUser.put("mobile_number", etMobileNumber.text)
                        regUser.put("password", etPassword.text)
                        regUser.put("address", etDeliveryAddress.text)
                        regUser.put("email", etEmail.text)


                        val queue = Volley.newRequestQueue(this)
                        val url = Constants.url.registerURL

                        val jsonRequest = object :
                            JsonObjectRequest(Request.Method.POST, url, regUser, Response.Listener {

                                val response = it.getJSONObject("data")
                                val success = response.getBoolean("success")

                                if (success) {
                                    val data = response.getJSONObject("data")
                                    editor.putBoolean("isLoggedIn", true)
                                    editor
                                        .putString("user_id", data.getString("user_id"))
                                    editor
                                        .putString("name", data.getString("name"))
                                    editor
                                        .putString("email", data.getString("email"))
                                    editor
                                        .putString(
                                            "mobile_number",
                                            data.getString("mobile_number")
                                        )

                                    editor
                                        .putString("address", data.getString("address"))
                                    editor.apply()

                                    Toast.makeText(
                                        this,
                                        "User Registered Successfully " + data.getString("name"),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    userSuccessfullyRegistered()
                                } else {
                                    val errorMessageServer = response.getString("errorMessage")
                                    Toast.makeText(
                                        this,
                                        "${errorMessageServer.toString()} ",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            },
                                Response.ErrorListener {
                                    Toast.makeText(this, "User not registered. Please try again!!!", Toast.LENGTH_SHORT).show()
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = Constants.url.json
                                headers["token"] = Constants.url.key
                                return headers
                            }
                        }
                        queue.add(jsonRequest)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("ERROR")
                    dialog.setMessage("Internet Connection  Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()

                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(this, "User not registered , Please Try Again ", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun userSuccessfullyRegistered() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkErrors(): Boolean {
        var noError = 0
        if (etName.text.isBlank() || etName.text.length <= 3) {
            etName.error = "Please Enter Your Name Properly"
        } else {
            noError++
        }

        if (etEmail.text.isBlank()) {
            etEmail.error = "Please Enter Your Email Properly"
        } else {
            noError++
        }

        if (etMobileNumber.text.isBlank() || etMobileNumber.text.length != 10) {
            etMobileNumber.error = " Please Check your Mobile No"
        } else {
            noError++
        }

        if (etDeliveryAddress.text.isBlank()) {
            etDeliveryAddress.error = "Please Check Your Delivery Address"
        } else {
            noError++
        }

        if (etPassword.text.isBlank() || etPassword.text.length <= 4) {
            etPassword.error = "Invalid Password!"
        } else {
            noError++
        }

        if (etConfirmPassword.text.isBlank()) {
            etConfirmPassword.error = "Field Missing!"
        } else {
            noError++
        }

        if (etPassword.text.isNotBlank() || etConfirmPassword.text.isNotBlank()) {
            if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                noError++
            } else {
                etConfirmPassword.error = "Password's Do not Match"
            }
        }

        return noError == 7
    }
}
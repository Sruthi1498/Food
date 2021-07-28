package com.example.foodly.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.foodly.R


class ProfileFragment(val contextParam: Context) : Fragment() {

    lateinit var tvName: TextView
    lateinit var tvMobileNo: TextView
    lateinit var tvEmail: TextView
    lateinit var tvAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences = contextParam.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.displayNameTxtView)
        tvMobileNo = view.findViewById(R.id.displayMobileNoTxtView)
        tvEmail = view.findViewById(R.id.displayEmailTxtView)
        tvAddress = view.findViewById(R.id.displayAddressTxtView)

        spannableStringWithColor(tvName,0,4,"Name :   " + sharedPreferences.getString("name",""))

        spannableStringWithColor(tvMobileNo,0,14,"Mobile Number :   " + sharedPreferences.getString("mobile_number", ""))

        spannableStringWithColor(tvEmail,0,5,"Email :   " + sharedPreferences.getString("email", ""))

        spannableStringWithColor(tvAddress,0,8,"Address :   " + sharedPreferences.getString("address", ""))



        return view
    }
    private fun spannableStringWithColor(view: TextView, start:Int, end:Int, s: String) {
        val wordToSpan: Spannable =
            SpannableString(s)
        wordToSpan.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(view.context, R.color.red)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        wordToSpan.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = wordToSpan
    }

}
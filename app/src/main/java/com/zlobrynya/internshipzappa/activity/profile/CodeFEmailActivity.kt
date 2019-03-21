package com.zlobrynya.internshipzappa.activity.profile

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.activity_code_f_email.*
import org.w3c.dom.Text

class CodeFEmailActivity: AppCompatActivity() {

    private var code: String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_f_email)

        firstNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (firstNumber.text.toString() == ""){
                    firstNumber.requestFocus()
                } else {
                    secondNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        secondNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (secondNumber.text.toString() == ""){
                    firstNumber.requestFocus()
                } else {
                    thirdNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        thirdNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (thirdNumber.text.toString() == ""){
                    secondNumber.requestFocus()
                } else {
                    fourthNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        fourthNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (fourthNumber.text.toString() == ""){
                    thirdNumber.requestFocus()
                } else {
                    fifthNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        fifthNumber.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (fifthNumber.text.toString() == ""){
                    fourthNumber.requestFocus()
                } else {
                    fifthNumber.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        button2.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                code = firstNumber.text.toString() + secondNumber.text.toString() +
                        thirdNumber.text.toString() + fourthNumber.text.toString()
                if (!code.equals("1111")){
                    allert_text.visibility = View.VISIBLE
                    firstNumber.setTextColor(resources.getColor(R.color.color_accent))
                    secondNumber.setTextColor(resources.getColor(R.color.color_accent))
                    thirdNumber.setTextColor(resources.getColor(R.color.color_accent))
                    fourthNumber.setTextColor(resources.getColor(R.color.color_accent))
                    fifthNumber.setTextColor(resources.getColor(R.color.color_accent))
                }else{
                    allert_text.visibility = View.GONE
                    firstNumber.setTextColor(resources.getColor(R.color.white))
                    secondNumber.setTextColor(resources.getColor(R.color.white))
                    thirdNumber.setTextColor(resources.getColor(R.color.white))
                    fourthNumber.setTextColor(resources.getColor(R.color.white))
                    fifthNumber.setTextColor(resources.getColor(R.color.white))
                }
                Log.d("code", "$code")
            }

        })
    }

}
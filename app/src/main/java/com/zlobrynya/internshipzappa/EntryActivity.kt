package com.zlobrynya.internshipzappa

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_entry.*

class EntryActivity : AppCompatActivity() {

    //!!!!!!!!!!!!!!
    lateinit var entryActivity: EntryActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        entryActivity = this

        btnEnter.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmailEntry)
            val pass = findViewById<EditText>(R.id.etPasswordEntry)

            Log.i("Log", email.text.toString())

            if (pass.text.toString().isEmpty()){
                pushToast("Поле поряля не может быть пустым")
                return@setOnClickListener
            }

            if (checkMail(email.text.toString())){
                val entryData = "login=" + email.text + "&password=" + pass.text

                PostLoginData.getInstance().getPost(entryData).subscribeOn(Schedulers.newThread())
                    ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<Int> {
                        override fun onComplete() {
                            println("Complete")
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(responseCode: Int) {
                            if (responseCode == 200)
                                checkPass(email.text.toString(), pass.text.toString(), entryActivity)
                        }

                        override fun onError(e: Throwable) {
                            println(e.toString())
                        }
                    })
            }else{
                pushToast("Неправильная почта.")
            }

        }
    }

    private fun checkMail(email: String): Boolean{
        return email.contains("@")
    }


    private fun pushToast(mess: String){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }


    fun startActivityReg(view: View){
        val change_activity = Intent(this, RegActivity::class.java)
        startActivity(change_activity)
        finish()
    }

    //Пока сервер отвечает только 200, ВРЕМЕННОЕ решение
    private fun checkPass(log: String, pass: String, context: Context){
        val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val savedLog = log
        val savedText = sharedPreferences.getInt(savedLog ,0)
        if (pass?.hashCode() == savedText){
            Log.i("LOGGG", "LOGGG")
            pushToast("Вы авторизовались")
        } else{
            pushToast("Неверный пароль или логин.")
        }
    }

}

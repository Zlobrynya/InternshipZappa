package com.zlobrynya.internshipzappa

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EntryActivity : AppCompatActivity() {


    //!!!!!!!!!!!!!!
    lateinit var entryActivity: EntryActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        val btn = findViewById<Button>(R.id.btnEnter)
        entryActivity = this

        btn.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmailEntry)
            val pass = findViewById<EditText>(R.id.etPasswordEntry)

            Log.i("Log", email.text.toString())

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
        }
    }


    private fun checkPass(log: String, pass: String, context: Context){
        val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val savedLog = log
        val savedText = sharedPreferences.getString(savedLog ,"")
        //Log.i("Check1", savedText.toString())
        if (pass.hashCode() == savedText.toInt())
            Log.i("LOGGG", "LOGGG")

        /*val editor = sharedPreferences.edit()
        editor.putString("users","qwerty")
        editor.apply()*/
    }

}

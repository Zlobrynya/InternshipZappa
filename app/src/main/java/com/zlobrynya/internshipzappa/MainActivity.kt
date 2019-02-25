package com.zlobrynya.internshipzappa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zlobrynya.internshipzappa.tools.YandexKassa
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var yandexKassa: YandexKassa;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        yandexKassa = YandexKassa(this)

        button.setOnClickListener {
            val intent = yandexKassa.payBasket(100.0,"test","testDesk",false)
            startActivityForResult(intent, yandexKassa.REQUEST_CODE_TOKENIZE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        yandexKassa.sentTokenToServer(data,requestCode,resultCode)
    }

}

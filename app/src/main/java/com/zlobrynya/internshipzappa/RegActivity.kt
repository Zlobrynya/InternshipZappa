package com.zlobrynya.internshipzappa
import java.util.concurrent.TimeUnit
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_reg.*
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.math.E

class RegActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
    }

    fun ChangeActivityEntry(view: View){
        val change_activity = Intent(this, EntryActivity::class.java)
        startActivity(change_activity)
    }

    fun ChangeActivityMain(view: View){
        val change_activity = Intent(this, EntryActivity::class.java)
        startActivity(change_activity)
    }

    private fun SavePerson(view: View) {
        val sPref = getPreferences(MODE_PRIVATE)
        val pass = sPref.edit()
        val login = sPref.edit()
        login.putString("Email", etEmailReg.getText().toString())
        pass.putString("Password", etPasswordReg.getText().toString())
        login.commit()
        pass.commit()
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show()
    }

    fun ShowPerson(view: View) {
        val sPref = getPreferences(Context.MODE_PRIVATE)
        val saved_Email = sPref.getString("Email", "")
        val saved_pass = sPref.getString("Password", "")
        etEmailReg.setText(saved_Email)
        etPasswordReg.setText(saved_pass)
        Toast.makeText(this, "Person loaded", Toast.LENGTH_SHORT).show()
    }
}
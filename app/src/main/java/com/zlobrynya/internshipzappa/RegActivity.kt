package com.zlobrynya.internshipzappa
import java.util.concurrent.TimeUnit
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_reg.*

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

    fun RegPerson(view: View){
        val RP = MyTask ()
        RP.execute()
    }

    internal inner class MyTask:AsyncTask<Void?, Void, Void>() {
        override protected fun doInBackground(vararg params: Void?): Void? {
            try{
               TimeUnit.SECONDS.sleep(2)
            }
            catch (e: InterruptedException){
                e.printStackTrace()
            }
            return null
        }

        override protected fun onPreExecute(){
            super.onPreExecute()
            tvProgress.setText("Подождите")
        }

        protected override fun onPostExecute(result: Void?){
            super.onPostExecute(result)
            tvProgress.setText("Готово")
        }
    }
}
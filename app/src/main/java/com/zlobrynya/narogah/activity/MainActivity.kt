package com.zlobrynya.narogah.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zlobrynya.narogah.R
import com.zlobrynya.narogah.tools.GetDataServer
import com.zlobrynya.narogah.tools.OurException
import com.zlobrynya.narogah.tools.database.MenuDB
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    private lateinit var menuDb: MenuDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("check", "it's fine")
        menuDb = MenuDB(this)

    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    //качаем данные c сервера
    private fun getData() {
        val getDataServer = GetDataServer(this)
        getDataServer.getData()
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Boolean> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Boolean) {
                    startMenu()
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fine")
                    e.printStackTrace()
                    val outE = e as OurException
                    val menu_db = menuDb.getCountRow()
                    Log.e("err", outE.codeRequest.toString())
                    when (outE.codeRequest) {
                        0 -> if (menu_db == 0) allert(getString(R.string.code_0), R.string.close_app)
                        else allert(getString(R.string.offline), R.string.well)
                        404, 500 -> if (menu_db == 0) allert(getString(R.string.code_404), R.string.close_app)
                        else allert(getString(R.string.offline), R.string.well)
                        503 -> if (menu_db == 0) allert(getString(R.string.code_503), R.string.close_app)
                        else allert(getString(R.string.offline), R.string.well)
                        else -> if (menu_db == 0) allert(getString(R.string.code_0), R.string.close_app)
                        else allert(getString(R.string.offline), R.string.well)
                    }
                }
            })
    }

    override fun onDestroy() {
        menuDb.closeDataBase()
        super.onDestroy()
    }

    //вызов диалога
    private fun allert(text: String, id: Int) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle(getString(R.string.something_wrong))
            .setMessage(text)
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.repeat_connection)

            ) { dialog, _ ->
                run {
                    Log.i("check", "it's fine")
                    getData()
                    dialog.cancel()
                }
            }
            /*.setNeutralButton(getString(R.string.call)) { dialog, _ ->
                run {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+7(8142)63-23-89"))
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
            }*/
            .setNegativeButton(
                getString(id)
            ) { dialog, _ ->
                run {
                    when (id) {
                        R.string.close_app -> this.finish()
                        R.string.well -> startMenu()
                    }
                }
            }
        val alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))

    }

    /* private fun allertNoInternet(text: String){
         val builder = AlertDialog.Builder(this)
         builder.setTitle(getString(R.string.something_wrong))
             .setMessage(text)
             .setCancelable(false)
             .setPositiveButton(getString(R.string.repeat_connection)
             ) { dialog, _ ->
                 run {
                     getData()
                     dialog.cancel()
                 }
             }
             .setNegativeButton(getString(R.string.well)
             ) { dialog, id ->
                 run {
                     startMenu()
                 }
             }
             .setNeutralButton(getString(R.string.call)){
                     dialog, id ->
                 run {
                     val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+7(8142)63-23-89"))
                     if (intent.resolveActivity(packageManager) != null) {
                         startActivity(intent)
                     }
                 }
             }
         val alert = builder.create()
         alert.show()
     }*/


    fun startMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}

package com.zlobrynya.narogah.activity.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.zlobrynya.narogah.R
import com.zlobrynya.narogah.activity.MenuActivity
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.changeUserDataDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.changeUserDataRespDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.regDTO
import com.zlobrynya.narogah.tools.retrofit.DTOs.accountDTOs.regRespDTO
import com.zlobrynya.narogah.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_code_f_email.*
import retrofit2.Response

class CodeFEmailActivity : AppCompatActivity() {

    private var code: String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_f_email)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(resources.getDrawable(R.drawable.actionbar))
        supportActionBar!!.elevation = 0F

        //принимаем параметры и формируем отсылку

        firstNumber.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    hideKeyboard(v!!)
                }
            }
        }

        firstNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length < 5) {
                    allCorrect()
                }
            }

        })


        val id = intent.getStringExtra("id")

        when (id) {
            "0" -> {
                supportActionBar!!.title = "Регистрация"

                button2.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val newRegister = regDTO()

                        newRegister.email = intent.getStringExtra("email")
                        newRegister.code = intent.getStringExtra("code")
                        newRegister.password = intent.getStringExtra("password")
                        newRegister.name = intent.getStringExtra("name")
                        newRegister.phone = intent.getStringExtra("phone")

                        code = firstNumber.text.toString()

                        newRegister.code = code

                        postRegister(newRegister)
                    }

                })
            }

            "1" -> {
                supportActionBar!!.title = "Редактирование профиля"
                button2.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val userCredentials = changeUserDataDTO()

                        val sharedPreferences =
                            applicationContext!!.getSharedPreferences(
                                applicationContext.getString(R.string.user_info),
                                Context.MODE_PRIVATE
                            )
                        val jwt = sharedPreferences.getString(
                            applicationContext.getString(R.string.access_token),
                            "null"
                        )!!.toString()

                        userCredentials.new_email = intent.getStringExtra("new_email")
                        //userCredentials.email = intent.getStringExtra("change_email")
                        userCredentials.name = intent.getStringExtra("change_name")
                        userCredentials.phone = intent.getStringExtra("change_phone")
                        userCredentials.birthday = intent.getStringExtra("change_birthday")
                        code = firstNumber.text.toString()

                        if(code != "") userCredentials.code = code.toInt()

                        postChangeData(jwt, userCredentials)
                    }
                })
            }
        }
    }

    private fun postRegister(newRegister: regDTO) {
        RetrofitClientInstance.getInstance()
            .postRegData(newRegister)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<regRespDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<regRespDTO>) {
                    if (t.isSuccessful) {
                        allCorrect()
                        val sharedPreferencesStat = applicationContext.getSharedPreferences(
                            applicationContext.getString(R.string.user_info),
                            Context.MODE_PRIVATE
                        )
                        val savedEmail = applicationContext.getString(R.string.user_email)
                        val access_token = applicationContext.getString(R.string.access_token)
                        val editor = sharedPreferencesStat.edit()
                        editor.putString(savedEmail, newRegister.email)
                        editor.putString(access_token, t.body()!!.access_token)
                        editor.apply()

                        val intent = Intent(applicationContext, MenuActivity::class.java)
                        startActivity(intent)
                    } else {
                        allWrong()
                    }
                }

                override fun onError(e: Throwable) {
                    showNoInternetConnectionAlert(newRegister)
                }
            })
    }

    private fun postChangeData(jwt: String, newChange: changeUserDataDTO) {
        RetrofitClientInstance.getInstance()
            .postChangeUserCredentials(jwt, newChange)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<changeUserDataRespDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<changeUserDataRespDTO>) {
                    if (t.isSuccessful) {
                        allCorrect()
                        val sharedPreferencesStat = applicationContext.getSharedPreferences(
                            applicationContext.getString(R.string.user_info),
                            Context.MODE_PRIVATE
                        )
                        val savedEmail = applicationContext.getString(R.string.user_email)
                        val access_token = applicationContext.getString(R.string.access_token)
                        val editor = sharedPreferencesStat.edit()
                        editor.putString(savedEmail, newChange.new_email)
                        editor.putString(access_token, t.body()!!.access_token)
                        editor.apply()
                        val intent = Intent(applicationContext, MenuActivity::class.java)
                        startActivity(intent)
                    } else {
                        allWrong()
                    }
                }

                override fun onError(e: Throwable) {
                    showNoInternetConnectionAlert(jwt, newChange)
                }
            })
    }

    private fun allWrong() {
        allert_text.visibility = View.VISIBLE
        firstNumber.setTextColor(resources.getColor(R.color.color_accent))

    }

    private fun allCorrect() {
        allert_text.visibility = View.GONE
        firstNumber.setTextColor(resources.getColor(R.color.white))

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private var alertIsShown = false
    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     * Перегрузка 1
     */
    private fun showNoInternetConnectionAlert(jwt: String, newChange: changeUserDataDTO) {
        if (!alertIsShown) {
            alertIsShown = true
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Ошибка соединения")
                .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
                .setCancelable(false)
                .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                        postChangeData(jwt, newChange)
                    }
                }
                .setNegativeButton("ОТМЕНА") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                    }
                }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
        }
    }

    /**
     * Выводит диалоговое окно с сообщением об отсутствии интернета
     * Перегрузка 2
     */
    private fun showNoInternetConnectionAlert(newRegister: regDTO) {
        if (!alertIsShown) {
            alertIsShown = true
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Ошибка соединения")
                .setMessage("Без подключения к сети невозможно продолжить бронирование.\nПроверьте соединение и попробуйте снова")
                .setCancelable(false)
                .setPositiveButton("ПОВТОРИТЬ") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                        postRegister(newRegister)
                    }
                }
                .setNegativeButton("ОТМЕНА") { dialog, which ->
                    run {
                        dialog.dismiss()
                        alertIsShown = false
                    }
                }
            val alert = builder.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.color_accent))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.color_accent))
        }
    }
}
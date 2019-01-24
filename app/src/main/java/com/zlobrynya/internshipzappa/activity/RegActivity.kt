package com.zlobrynya.internshipzappa.activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.tools.PostLoginData
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_reg.*



class RegActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
    }

    fun savePerson(view: View) {
        val password = etPasswordReg.text.toString()
        val log = etEmailReg.text.toString()
        val regActivity = this


        if (etPasswordReg.text.toString().isEmpty() || etPasswordReg2.text.toString().isEmpty()) {
            pushToast("Поле пароля не может быть пустым")
            return
        }

        if (!checkMail(log)){
            pushToast("Неправильная почта.")
            return
        }


        if (etPasswordReg.text.toString().hashCode() != etPasswordReg2.text.toString().hashCode())
            Toast.makeText(this, "Пароли не совпадают.", Toast.LENGTH_SHORT).show()
        else {
            val entryData = "login=" + log + "&password=" + password



            PostLoginData.getInstance().getPost(entryData).subscribeOn(Schedulers.newThread())
                ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("Complete")
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(responseCode: Int) {
                        if (responseCode == 200){
                            val sharedPreferences = regActivity.getSharedPreferences("users", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putInt(log,password.hashCode())
                            editor.apply()

                            Toast.makeText(regActivity, "Регистрация прошла успешно.", Toast.LENGTH_SHORT).show()
                            regActivity.finish()
                        }
                    }

                    override fun onError(e: Throwable) {
                        println(e.toString())
                    }
                })


        }
    }

    private fun checkMail(email: String): Boolean{
        return email.contains("@")
    }

    private fun pushToast(mess: String){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show()
    }
}
package com.zlobrynya.internshipzappa.fragment


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.activity.Menu2Activity
import com.zlobrynya.internshipzappa.activity.profile.CodeFEmailActivity
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.*
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.respDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import retrofit2.Response
import java.text.DateFormat

import java.text.SimpleDateFormat
import java.util.*

/**
 * Фрагмент редактирования профиля.
 *
 */
class EditProfileFragment : Fragment() {

    var lastCLickTime: Long = 0

    var calendar = Calendar.getInstance()

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        closeFragment()
    }

    /**
     * Закрывает текущий фрагмент и снимает его со стека
     */
    private fun closeFragment() {
        val trans = fragmentManager!!.beginTransaction()
        trans.remove(this)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.commit()
        fragmentManager!!.popBackStack()
    }

    private fun reloadActivity() {
        val intent = Intent(context, Menu2Activity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        initToolBar(view)


        val username = arguments!!.getString("name")
        val dob = arguments!!.getString("dob")
        val email = arguments!!.getString("email")
        val phone = arguments!!.getString("phone")
        val changedPhone = replaceStartPhone(phone)

        view.edit_profile_username_input_layout.editText!!.setText(username)
        view.edit_profile_dob_input_layout.editText!!.setText(dob)
        view.edit_profile_email_input_layout.editText!!.setText(email)
        view.edit_profile_phone_number_input_layout.editText!!.setText(changedPhone)

        val sharedPreferences =
            context?.getSharedPreferences(this.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
        val savedName = context?.getString(R.string.key_user_name)
        val savedDate = context?.getString(R.string.key_user_date)
        val savedEmail = context?.getString(R.string.key_user_email)
        val savedPhone = context?.getString(R.string.key_user_phone)
        Log.d("ALOHA3", sharedPreferences?.getString(savedName, "").toString())
        Log.d("ALOHA4", sharedPreferences?.getString(savedDate, "").toString())
        Log.d("ALOHA5", sharedPreferences?.getString(savedEmail, "").toString())
        Log.d("ALOHA6", sharedPreferences?.getString(savedPhone, "").toString())

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            }
        }

        view.edit_profile_dob.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    context!!, R.style.DatePickerTheme,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        view.edit_profile_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                edit_profile_username.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val name = edit_profile_username_input_layout.editText!!.text.toString()
                        val validateName = validateName(name)

                        if (!hasFocus && !validateName) {
                            edit_profile_username_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_name)
                            edit_profile_username.setCompoundDrawables(null, null, icon, null)
                        } else {
                            edit_profile_username_input_layout.isErrorEnabled = false
                            edit_profile_username.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        view.edit_profile_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                edit_profile_email.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = edit_profile_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!hasFocus && !validateEmail) {
                            edit_profile_email_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            edit_profile_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            edit_profile_email_input_layout.isErrorEnabled = false
                            edit_profile_email.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        view.edit_profile_phone_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                edit_profile_phone_number.onFocusChangeListener = object : View.OnFocusChangeListener {
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val phone = edit_profile_phone_number_input_layout.editText!!.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!hasFocus && !validatePhone) {
                            edit_profile_phone_number_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                            edit_profile_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            edit_profile_phone_number_input_layout.isErrorEnabled = false
                            edit_profile_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        view.btnSaveChanges.setOnClickListener {
            val newName = edit_profile_username_input_layout.editText!!.text.toString()
            val newDate = edit_profile_dob_input_layout.editText!!.text.toString()
            val newEmail = edit_profile_email_input_layout.editText!!.text.toString()
            val newPhone = edit_profile_phone_number_input_layout.editText!!.text.toString()
            val outputDateStr: String

            if (newDate != "") {
                val inputFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy")
                val outputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = inputFormat.parse(newDate)
                outputDateStr = outputFormat.format(date)
            } else {
                outputDateStr = ""
            }

            val validateName = validateName(newName)
            val validatePhone = validatePhone(newPhone)
            val validateEmail = validateEmail(newEmail)

            if (validateName && validateEmail && validatePhone) {
                //не трогать
                val newChangeData = changeUserDataDTO()
                val email = arguments!!.getString("email")
                if (email != newEmail) {
                    Log.d("BOOP", "Валидация прошла")
                    newChangeData.birthday = outputDateStr
                    newChangeData.name = newName
                    newChangeData.new_email = newEmail
                    newChangeData.email = email
                    newChangeData.phone = newPhone
                    if (SystemClock.elapsedRealtime() - lastCLickTime < 10000) {
                        return@setOnClickListener
                    } else {
                        lastCLickTime = SystemClock.elapsedRealtime()
                        checkExistenceEmail(newChangeData)
                    }
                } else {
                    /**
                     * TODO поменять дату на нужный формат
                     */
                    newChangeData.birthday = outputDateStr
                    newChangeData.name = newName
                    newChangeData.new_email = ""
                    newChangeData.email = email
                    newChangeData.phone = newPhone
                    changeUserCredentials(newChangeData)
                    closeFragment()
                    reloadActivity()
                }
            }

        }

        return view
    }

    override fun onResume() {
        super.onResume()
        lastCLickTime = 0
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar(view: View) {
        view.edit_profile.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        view.edit_profile.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
    }

    private fun validateName(name: String): Boolean {
        val nameLength = 2
        return name.matches("[a-zA-Zа-яА-ЯёЁ]*".toRegex()) && name.length >= nameLength
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun updateDate() {
        val myFormat = "dd.MM.yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        edit_profile_dob.text = sdf.format(calendar.time).toEditable()
    }

    private fun validatePhone(phone: String): Boolean {
        val phoneLength = 16
        return phone.length == phoneLength
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun replaceStartPhone(str: String?): String {
        val newstr = str!!.replace(" ", "")
        val newstra = newstr.replace("(", "")
        val newstrb = newstra.replace(")", "")
        val newstrc = newstrb.replace("-", "")
        return newstrc.replace("+7", "")
    }

    /**
     * TODO меняет данные юзера (если мыло старое)
     * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
     *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
     *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
     */
    private fun changeUserCredentials(newChangeUser: changeUserDataDTO) {

        Log.i("checkChangeCredentials", newChangeUser.name)
        Log.i("checkChangeCredentials", newChangeUser.phone)
        //Log.i("checkChangeCredentials", newChangeUser.birthday)
        Log.i("checkChangeCredentials", newChangeUser.code.toString())
        Log.i("checkChangeCredentials", newChangeUser.email)
        Log.i("checkChangeCredentials", newChangeUser.new_email)

        val sharedPreferences =
            activity!!.getSharedPreferences(this.getString(R.string.user_info), Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString(this.getString(R.string.access_token), "null")!!.toString()

        RetrofitClientInstance.getInstance()
            .postChangeUserCredentials(jwt, newChangeUser)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<changeUserDataRespDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<changeUserDataRespDTO>) {
                    Log.i("checkChangeCredentials", "${t.code()}")

                    if (t.isSuccessful) {
                        /**
                         * TODO при получении проверять, что поля не равны нулл
                         */
                        Log.i("checkChangeCredentials", "${t.code()}")
                        /*val sharedPreferencesStat = context!!.getSharedPreferences(
                            context!!.getString(R.string.user_info),
                            Context.MODE_PRIVATE
                        )
                        val access_token = context!!.getString(R.string.access_token)
                        val editor = sharedPreferencesStat.edit()
                        editor.putString(access_token, t.body()!!.access_token)
                        editor.apply()*/
                    } else {
                        /**
                         * TODO
                         * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
                         *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
                         *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
                         */
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("check", "that's not fineIn")
                    //запрос не выполнен, всё плохо
                }

            })
    }

    private fun checkExistenceEmail(newChange: changeUserDataDTO) {
        val view = this.view
        if (view != null) {
            view.progress_spinner.visibility = View.VISIBLE
        }
        val newVerify = verifyEmailDTO()
        newVerify.email = newChange.new_email
        RetrofitClientInstance.getInstance()
            .getEmailExistence(newVerify.email)
            .subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<respDTO>> {

                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Response<respDTO>) {
                    Log.i("checkEmailExistence", t.code().toString())
                    if (t.isSuccessful) {
                        Log.i("checkEmailExistence", "${t.code()}")
                        Log.i("checkEmailExistence", t.body()!!.desc)
                        val view = this@EditProfileFragment.view
                        if (view != null) {
                            view.progress_spinner.visibility = View.GONE
                        }
                    } else {
                        RetrofitClientInstance.getInstance()
                            .postVerifyData(newVerify)
                            .subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe(object : Observer<Response<verifyRespDTO>> {

                                override fun onComplete() {}

                                override fun onSubscribe(d: Disposable) {}

                                override fun onNext(t: Response<verifyRespDTO>) {
                                    Log.i("checkCode", "${t.code()}")

                                    if (t.isSuccessful) {
                                        val intent = Intent(context, CodeFEmailActivity::class.java)
                                        intent.putExtra("change_name", newChange.name)
                                        intent.putExtra("change_phone", newChange.phone)
                                        intent.putExtra("change_email", newChange.email)
                                        intent.putExtra("change_birthday", newChange.birthday)
                                        intent.putExtra("new_email", newChange.new_email)
                                        intent.putExtra("id", "1")
                                        startActivity(intent)
                                    }
                                    val view = this@EditProfileFragment.view
                                    if (view != null) {
                                        view.progress_spinner.visibility = View.GONE
                                    }
                                }

                                override fun onError(e: Throwable) {
                                    Log.i("check", "that's not fineIn")
                                }

                            })
                    }
                }

                override fun onError(e: Throwable) {
                    Log.i("checkReg", "that's not fineIn")
                }
            })
    }
}

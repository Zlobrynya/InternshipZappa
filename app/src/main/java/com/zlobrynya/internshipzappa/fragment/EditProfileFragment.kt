package com.zlobrynya.internshipzappa.fragment


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
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
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.changeUserDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.changeUserDataRespDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.userDataDTO
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.accountDTOs.verifyEmailDTO
import com.zlobrynya.internshipzappa.tools.retrofit.RetrofitClientInstance
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import retrofit2.Response

import java.text.SimpleDateFormat
import java.util.*

/**
 * Фрагмент редактирования профиля.
 *
 */
class EditProfileFragment : Fragment() {

    var calendar = Calendar.getInstance()

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        // Удалим фрагмент со стека
        val trans = fragmentManager!!.beginTransaction()
        trans.remove(this)
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        trans.commit()
        fragmentManager!!.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        initToolBar(view)


        val username = arguments!!.getString("name")
        val dob = arguments!!.getString("dob")
        val email = arguments!!.getString("email")
        val phone = arguments!!.getString("phone")

        view.edit_profile_username_input_layout.editText!!.setText(username)
        view.edit_profile_dob_input_layout.editText!!.setText(dob)
        view.edit_profile_email_input_layout.editText!!.setText(email)
        view.edit_profile_phone_number_input_layout.editText!!.setText(phone)

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
                        if (!hasFocus) {
                            val name = edit_profile_username_input_layout.editText!!.text.toString()
                            val validateName = validateName(name)

                            if (!validateName) {
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

                        if (!validateEmail) {
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

        view.edit_profile_phone_number.setOnClickListener {
            phone_masked.visibility = View.VISIBLE
            phone_masked.requestFocus()
            phone_masked.addTextChangedListener(object: TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val phone = phone_masked.text.toString()
                    val validatePhone = validatePhone(phone)

                    if (!validatePhone) {
                        edit_profile_phone_number_input_layout.error =
                                getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                        edit_profile_phone_number.setCompoundDrawables(null, null, icon, null)
                    } else {
                        edit_profile_phone_number_input_layout.isErrorEnabled = false
                        edit_profile_phone_number.setCompoundDrawables(null, null, null, null)
                    }
                }
            })
        }

        view.edit_profile_phone_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                phone_masked.visibility = View.VISIBLE
                phone_masked.requestFocus()
                phone_masked.addTextChangedListener(object: TextWatcher {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val phone = phone_masked.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!validatePhone) {
                            edit_profile_phone_number_input_layout.error =
                                    getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                            edit_profile_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            edit_profile_phone_number_input_layout.isErrorEnabled = false
                            edit_profile_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                })
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*
                phone_masked.visibility = View.VISIBLE
                phone_masked.requestFocus()
                phone_masked.addTextChangedListener(object: TextWatcher {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val phone = phone_masked.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!validatePhone) {
                            edit_profile_phone_number_input_layout.error =
                                    getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                            edit_profile_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            edit_profile_phone_number_input_layout.isErrorEnabled = false
                            edit_profile_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                })
                */
            }
        })

        view.btnSaveChanges.setOnClickListener {
            val newName = edit_profile_username_input_layout.editText!!.text.toString()
            val newDate = edit_profile_dob_input_layout.editText!!.text.toString()
            val newEmail = edit_profile_email_input_layout.editText!!.text.toString()
            val newPhone = edit_profile_phone_number_input_layout.editText!!.text.toString()

            /*val sharedPreferences =
                context!!.getSharedPreferences(context!!.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
            val savedName = context!!.getString(R.string.key_user_name)
            val savedDate = context!!.getString(R.string.key_user_date)
            val savedEmail = context!!.getString(R.string.key_user_email)
            val savedPhone = context!!.getString(R.string.key_user_phone)
            val editor = sharedPreferences.edit()
            editor.putString(savedName, name)
            editor.putString(savedDate, date)
            editor.putString(savedEmail, email)
            editor.putString(savedPhone, phone)
            editor.apply()*/

            //не трогать
            val newChangeData = changeUserDataDTO()
            val email = arguments!!.getString("email")
            if(email != newEmail){
                Log.i("checkChangeCredentials", "pupa")
            }else{
                /**
                 * TODO поменять дату на нужный формат
                 */

                newChangeData.birthday = newDate
                newChangeData.name = newName
                Log.i("checkChangeCredentials", newChangeData.name)
                newChangeData.new_email = ""
                newChangeData.email = email
                newChangeData.phone = newPhone
                changeUserCredentials(newChangeData)
            }
        }

        return view
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
        val phoneLength = 10
        return phone.length == phoneLength
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * TODO меняет данные юзера (если мыло старое)
     * юзер неавторизирован или ещё какая херня, но запрос выполнен. Посмотреть код t.code() и обработать
     *если 401 запустить активити авторизации, если успешно авторизовался выкинуть обратно сюда и обновить
     *содержимое фрагмента, видимо через отслеживание результата активити опять, хз
     */
    private fun changeUserCredentials(newChangeUser: changeUserDataDTO){

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
                    Log.i("checkMyCredentials", "${t.code()}")

                    if (t.isSuccessful) {
                        /**
                         * TODO при получении проверять, что поля не равны нулл
                         */
                        Log.i("checkChangeCredentials", "${t.code()}")
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
}

package com.zlobrynya.internshipzappa.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zlobrynya.internshipzappa.R

import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * Фрагмент профиля.
 *
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)

<<<<<<< HEAD
        val sharedPreferences = context?.getSharedPreferences(this.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
        val savedName = context?.getString(R.string.key_user_name)
        val savedDate = context?.getString(R.string.key_user_date)
        val savedEmail = context?.getString(R.string.key_user_email)
        val savedPhone = context?.getString(R.string.key_user_phone)
        Log.d("ALOHA3", sharedPreferences?.getString(savedName, "").toString())
        Log.d("ALOHA4", sharedPreferences?.getString(savedDate, "").toString())
        Log.d("ALOHA5", sharedPreferences?.getString(savedEmail, "").toString())
        Log.d("ALOHA6", sharedPreferences?.getString(savedPhone, "").toString())
        if (sharedPreferences?.getString(savedName, "") != "") view.profile_username_input_layout.editText!!.setText(sharedPreferences?.getString(savedName, ""))
        if (sharedPreferences?.getString(savedDate, "") != "") view.profile_dob_input_layout.editText!!.setText(sharedPreferences?.getString(savedDate, ""))
        if (sharedPreferences?.getString(savedEmail, "") != "") view.profile_email_input_layout.editText!!.setText(sharedPreferences?.getString(savedEmail, ""))
        if (sharedPreferences?.getString(savedPhone, "") != "") view.profile_phone_number_input_layout.editText!!.setText(sharedPreferences?.getString(savedPhone, ""))

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
            }
        }

        view.profile_dob.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(context!!, R.style.DatePickerTheme,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        val icon = resources.getDrawable(com.zlobrynya.internshipzappa.R.drawable.error)

        icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)

        view.profile_username.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                profile_username.onFocusChangeListener = object: View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        if (!hasFocus) {
                            val name = profile_username_input_layout.editText!!.text.toString()
                            val validateName = validateName(name)

                            if (!validateName) {
                                profile_username_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_name)
                                profile_username.setCompoundDrawables(null, null, icon, null)
                            } else {
                                profile_username_input_layout.isErrorEnabled = false
                                profile_username.setCompoundDrawables(null, null, null, null)
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

        view.profile_email.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                profile_email.onFocusChangeListener = object : View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val email = profile_email_input_layout.editText!!.text.toString()
                        val validateEmail = validateEmail(email)

                        if (!validateEmail) {
                            profile_email_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_email)
                            profile_email.setCompoundDrawables(null, null, icon, null)
                        } else {
                            profile_email_input_layout.isErrorEnabled = false
                            profile_email.setCompoundDrawables(null, null, null, null)
                        }
                    }

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        view.profile_phone_number.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                profile_phone_number.onFocusChangeListener = object: View.OnFocusChangeListener{
                    override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        val phone = profile_phone_number_input_layout.editText!!.text.toString()
                        val validatePhone = validatePhone(phone)

                        if (!validatePhone) {
                            profile_phone_number_input_layout.error = getString(com.zlobrynya.internshipzappa.R.string.error_phone)
                            profile_phone_number.setCompoundDrawables(null, null, icon, null)
                        } else {
                            profile_phone_number_input_layout.isErrorEnabled = false
                            profile_phone_number.setCompoundDrawables(null, null, null, null)
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s!!.replaceFirst(regex = "[8]".toRegex(), replacement = "")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })



        view.btnSaveChanges.setOnClickListener {
            val name = profile_username_input_layout.editText!!.text.toString()
            val date = profile_dob_input_layout.editText!!.text.toString()
            val email = profile_email_input_layout.editText!!.text.toString()
            val phone = profile_phone_number_input_layout.editText!!.text.toString()

            val sharedPreferences = context!!.getSharedPreferences(context!!.getString(R.string.key_shared_users), Context.MODE_PRIVATE)
            val savedName = context!!.getString(R.string.key_user_name)
            val savedDate = context!!.getString(R.string.key_user_date)
            val savedEmail = context!!.getString(R.string.key_user_email)
            val savedPhone = context!!.getString(R.string.key_user_phone)
            val editor = sharedPreferences.edit()
            editor.putString(savedName,name)
            editor.putString(savedDate,date)
            editor.putString(savedEmail,email)
            editor.putString(savedPhone,phone)
            editor.apply()
=======
        view.btnEdit.setOnClickListener {
            val trans = fragmentManager!!.beginTransaction()
            val editProfileFragment = EditProfileFragment()
            trans.add(R.id.root_frame2, editProfileFragment)
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            trans.addToBackStack(null)
            trans.commit()
>>>>>>> 44ea66b9ec165fcbca70092cb049d5bef496feb3
        }

        return view
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}

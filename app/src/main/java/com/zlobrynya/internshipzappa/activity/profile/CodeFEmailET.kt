package com.zlobrynya.internshipzappa.activity.profile

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText


class CodeFEmailET(context: Context, attrs: AttributeSet) : EditText(context, attrs) {

    private var keyImeChangeListener: KeyImeChange? = null

    fun setKeyImeChangeListener(listener: KeyImeChange) {
        keyImeChangeListener = listener
    }

    interface KeyImeChange {
        fun onKeyIme(keyCode: Int, event: KeyEvent)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyImeChangeListener != null) {
            keyImeChangeListener!!.onKeyIme(keyCode, event)
        }
        return false
    }
}
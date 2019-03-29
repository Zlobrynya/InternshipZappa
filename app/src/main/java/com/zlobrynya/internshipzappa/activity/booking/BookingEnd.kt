package com.zlobrynya.internshipzappa.activity.booking

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.zlobrynya.internshipzappa.R
import kotlinx.android.synthetic.main.activity_end_booking.*

class BookingEnd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_booking)

        val code = intent.getIntExtra("code", 400)
        val na = intent.getStringExtra("name")
        when (code) {
            200 -> {
                otmena.text = getString(R.string.prinyal, na)
                izmena.text = getString(R.string.recall)
            }
            else -> {
                otmena.text = getString(R.string.otclonena, na)
                izmena.text = getString(R.string.izmeni)
            }
        }

        btn_back.setOnClickListener(onClickListener) // Установим обработчик нажатий на кнопку "Вернуться"
    }

    /**
     * Обработчик нажатий
     */
    private val onClickListener = View.OnClickListener {
        when (it.id) {
            // Кнопка вернуться
            R.id.btn_back -> finishBooking()
        }
    }

    /**
     * Закрывает активити, снимает со стека все предыдушие фрагменты
     */
    private fun finishBooking() {
        Log.d("BOOP", "Завершаем бронирование")
        setResult(Activity.RESULT_OK)
        this.finish()
    }

    override fun onBackPressed() {
        finishBooking()
        //super.onBackPressed()
    }

}


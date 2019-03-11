package com.zlobrynya.internshipzappa.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.zlobrynya.internshipzappa.R
import com.zlobrynya.internshipzappa.adapter.AdapterTable
import kotlinx.android.synthetic.main.activity_table_select.*
import android.support.v7.widget.DividerItemDecoration
import com.zlobrynya.internshipzappa.adapter.Table


/**
 * Активити для выбора столика для бронирования
 */
class TableSelectActivity : AppCompatActivity() {

    /**
     * Обработчик нажатий на стрелочку в тулбаре
     */
    private val navigationClickListener = View.OnClickListener {
        this.finish()
    }

    /**
     * Список столиков
     */
    private val tableList: ArrayList<Table> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_select)

        initToolBar()
        initTableList()
        initRecycler()
    }

    /**
     * Настраивает тулбар
     */
    private fun initToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_button) // Установим иконку в тулбаре
        toolbar.setNavigationOnClickListener(navigationClickListener) // Установим обработчик нажатий на тулбар
    }

    /**
     * Заполняет данными список столиков
     */
    private fun initTableList() {
        tableList.add(Table("4 места", "Диваны", 1))
        tableList.add(Table("4 места", "Стулья", 2))
        tableList.add(Table("6 мест, у окна", "Диваны", 3))
        tableList.add(Table("6 мест, у окна", "Диваны", 4))
        tableList.add(Table("8 мест, у окна", "Бутылки", 5))
    }

    /**
     * Настраивает ресайклер вью
     */
    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        table_recycler.layoutManager = layoutManager
        table_recycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL) // Разделитель элементов внутри ресайклера
        )
        table_recycler.adapter = AdapterTable(tableList)
    }
}

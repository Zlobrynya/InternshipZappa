package com.zlobrynya.internshipzappa.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import ru.yandex.money.android.sdk.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashSet

/*
* Класс для создание и отправки токена оплаты заказа
* */


class YandexKassa(val context: Context) {
    private val RUB = Currency.getInstance("RUB")
    private val SECRET_KEY = "1FDSF15DF12SDF"
    private val NAME_ORG = "NA ROGAH"
    //!!!!!!!!!!!!!!!!!!!
    private val TEST = true

    val REQUEST_CODE_TOKENIZE = 33

    //запуск токенизации
    fun payBasket(amount: Double, name: String, desk: String, logo: Boolean = true): Intent? {
        val amountBig = BigDecimal(amount)
        if (validateAmount(amountBig)){
            val paymentMethodType = getPaymentMethodTypes()
            val paymentParameters = PaymentParameters(
                Amount(amountBig, RUB),
                name,
                desk,
                SECRET_KEY,
                NAME_ORG,
                paymentMethodType
            )
            val uiParameters = UiParameters(logo)
            var testParameters: TestParameters;
            if (TEST){
                testParameters = TestParameters(
                    true,
                    false,
                    MockConfiguration(false, false, 5)
                )
            }else{
                testParameters = TestParameters()
            }
            return Checkout.createTokenizeIntent(context,paymentParameters,testParameters,uiParameters)
        }
        return null
    }

    fun sentTokenToServer(data: Intent?, requestCode: Int, resultCode: Int){
        if (requestCode == REQUEST_CODE_TOKENIZE){
            when(resultCode){
                Activity.RESULT_OK -> {
                    //paymentToken - Платежный токен.
                    //paymentMethodType - Способ оплаты.
                    val result = Checkout.createTokenizationResult(data!!)
                    Toast.makeText(context, result.paymentToken, Toast.LENGTH_SHORT).show()
                    //дальше оптправляем на сервер
                }
                Activity.RESULT_CANCELED -> {
                    // user canceled tokenization
                    Toast.makeText(context, "Операция отменена.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //проверка на то что в цена != 0
    private fun validateAmount(amountBig: BigDecimal): Boolean = amountBig.compareTo(BigDecimal.ZERO) > 0

    private fun getPaymentMethodTypes(): Set<PaymentMethodType>{
        val paymentMethodType = HashSet<PaymentMethodType>()
        paymentMethodType.add(PaymentMethodType.BANK_CARD)
        paymentMethodType.add(PaymentMethodType.SBERBANK)
        paymentMethodType.add(PaymentMethodType.GOOGLE_PAY)
        return paymentMethodType
    }
}
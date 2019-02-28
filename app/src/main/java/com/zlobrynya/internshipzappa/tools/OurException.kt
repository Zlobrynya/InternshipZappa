package com.zlobrynya.internshipzappa.tools

/*
* Исключение
* Создана что бы иметь возможность перехватить ошибку (записать код возврата ответа сервера если нужно)
* и передать через RxJava
* */
class OurException(var codeRequest: Int = 0): RuntimeException() {}
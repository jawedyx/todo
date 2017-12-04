package pw.jawedyx.jawex

import android.app.Application

class App : Application() {


    companion object {

        private lateinit var  helper : DBHelper

        fun getRef(): DBHelper {
            return helper
        }

    }

    override fun onCreate() {
        super.onCreate()
        helper = DBHelper(applicationContext)
    }

}
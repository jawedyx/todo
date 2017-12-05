package pw.jawedyx.jawex

import android.app.Application

class App : Application() {


    companion object {

        private lateinit var  helper : DBHelper
        private var colorNames: Array<String> = arrayOf("Red", "Pink", "Purple", "Deep Purple", "Indigo", "Blue", "Teal", "Amber", "Grey" )
        private var colorValues: Array<String> = arrayOf("#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#009688", "#FFC107", "#9E9E9E")

        fun getRef(): DBHelper {
            return helper
        }

        fun getColorNamesList(): Array<String> {
            return colorNames
        }

        fun getColorValuesList(): Array<String>{
            return colorValues
        }

    }

    override fun onCreate() {
        super.onCreate()
        helper = DBHelper(applicationContext)
    }

}
package pw.jawedyx.jawex

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context : Context) : SQLiteOpenHelper(context, "Jawex", null, 2){


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table Notes (id integer primary key autoincrement, color integer, content text, created_date integer, title text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldV: Int, newV: Int) {
        if(oldV == 1 && newV == 2){
            db?.execSQL("alter table Notes add column title text")
        }

    }
}
package pw.jawedyx.jawex

import android.app.IntentService
import android.content.Intent
import java.util.*

class NoteService : IntentService("NoteService") {


    override fun onHandleIntent(intent: Intent?) {

        if(intent!!.hasExtra("fill")){
            val notes : ArrayList<Note> = ArrayList()

            val cursor = App.getRef().readableDatabase.query(
                    "Notes",
                    null, //arrayOf("color", "content", "created_date")
                    null,
                    null,
                    null,
                    null,
                    null
            )

            if(cursor.moveToFirst()){

                val colorIndex = cursor.getColumnIndex("color")
                val contentIndex = cursor.getColumnIndex("content")
                val created_date = cursor.getColumnIndex("created_date")

                do {
                    notes.add(Note(cursor.getInt(colorIndex), cursor.getString(contentIndex), cursor.getLong(created_date)))

                }while (cursor.moveToNext())
            }

            cursor.close()

            val response = Intent()
            response.action = "RESPONSE"
            response.addCategory(Intent.CATEGORY_DEFAULT)
            response.putExtra("fill_out", notes)
            sendBroadcast(response)

        }

    }
}
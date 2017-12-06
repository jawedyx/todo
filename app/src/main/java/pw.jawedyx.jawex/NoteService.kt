package pw.jawedyx.jawex

import android.app.IntentService
import android.content.ContentValues
import android.content.Intent
import java.util.*

class NoteService : IntentService("NoteService") {


    override fun onHandleIntent(intent: Intent?) {

        val response = Intent()
        response.action = "RESPONSE"
        response.addCategory(Intent.CATEGORY_DEFAULT)

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
                val idIndex = cursor.getColumnIndex("id")
                val colorIndex = cursor.getColumnIndex("color")
                val contentIndex = cursor.getColumnIndex("content")
                val created_date = cursor.getColumnIndex("created_date")

                do {
                    notes.add(Note(cursor.getInt(idIndex), cursor.getInt(colorIndex), cursor.getString(contentIndex), cursor.getLong(created_date)))
                }while (cursor.moveToNext())
            }

            cursor.close()

            Collections.reverse(notes) //В обратном порядке


            response.putExtra("fill_out", notes)
            sendBroadcast(response)

        }

        if(intent.hasExtra("insert")){

            val values = intent.getParcelableExtra<ContentValues>("insert")
            val result = App.getRef().writableDatabase.insert("Notes", null, values)

            response.putExtra("insert_out", result)
            sendBroadcast(response)
        }

        if(intent.hasExtra("update")){
            val values = intent.getParcelableExtra<ContentValues>("update")
            val id = intent.getStringExtra("bottom_edit_id")

            val result = App.getRef().writableDatabase.update("Notes", values, "id = ?", arrayOf(id))

            response.putExtra("update_out", result)
            sendBroadcast(response)
        }

        if(intent.hasExtra("remove")){
            val id = intent.getIntExtra("remove", 0)
            val result = App.getRef().writableDatabase.delete("Notes", "id = $id", null)

            response.putExtra("remove_out", result)
            sendBroadcast(response)
        }

    }
}
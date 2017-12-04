package pw.jawedyx.jawex

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card.view.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

     val receiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if(intent!!.hasExtra("fill_out")){
                val notes = intent.getSerializableExtra("fill_out")
                recycler.adapter = RAdapter(notes as ArrayList<Note>)
                recycler.layoutManager = LinearLayoutManager(applicationContext)
                recycler.adapter.notifyDataSetChanged()
                Log.wtf("jawex", "Inside object receiver")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val fillerIntent = Intent(applicationContext, NoteService::class.java)
        startService(fillerIntent.putExtra("fill", 0)) //Заполнить данными главный экран

        val intentFilter = IntentFilter("RESPONSE")
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(receiver, intentFilter)

        recycler.adapter = RAdapter()

        fab.setOnClickListener { view ->
            val values  = ContentValues()
            values.put("color", Color.GRAY)
            values.put("content", "Добавлено из FAB")
            values.put("created_date", System.currentTimeMillis())

            App.getRef().writableDatabase.insert("Notes", null, values)

            Toast.makeText(applicationContext, "It works", Toast.LENGTH_LONG).show()



        }





    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    }

    private class RAdapter(var data: ArrayList<Note>) : RecyclerView.Adapter<RAdapter.ViewHolder>() {

        constructor(): this(ArrayList<Note>())


        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent!!.context).inflate(R.layout.card, parent, false)
            return RAdapter.ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RAdapter.ViewHolder, position: Int) {
            val note = data.get(position)
            holder.textView.setText(note.text)
            holder.dateView.setText(Date(note.createdTime).toString())
            note.color?.let { holder.cardView.setBackgroundColor(it) }




        }

        class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            var textView : TextView
            var cardView : CardView
            var dateView : TextView


            init {
                textView = item.card_text
                cardView = item.card
                dateView = item.date
            }
        }

     }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

package pw.jawedyx.jawex

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card.view.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var isEdit: Boolean = false


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.hasExtra("fill_out")){
                val notes = intent.getSerializableExtra("fill_out")

                recycler.adapter = RAdapter(notes as ArrayList<Note>)
                recycler.layoutManager = LinearLayoutManager(applicationContext)
                (recycler.adapter as RAdapter).setListener(object : RAdapter.Listener{
                    override fun onClick(position: Int) {
                        //todo Вернуть редактирование
                        isEdit = true
                    }
                })

                (recycler.adapter as RAdapter).setLongClickListener(object: RAdapter.LongClickListener{
                    override fun onLongClick(position: Int, view: View?) {
                        Snackbar.make(view!!, "Удалить заметку?", Snackbar.LENGTH_SHORT).setAction("Да", View.OnClickListener {
                            val removeIntent = Intent(applicationContext, NoteService::class.java)
                            startService(removeIntent.putExtra("remove", notes.get(position).nid))
                        }).show()
                    }
                })

                recycler.adapter.notifyDataSetChanged()
            }

            if(intent.hasExtra("insert_out")){
                val result = intent.getLongExtra("insert_out",0)

                if(result > 0)
                    Toast.makeText(applicationContext, "Заметка добавлена!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(applicationContext, "Ошибка при добавлении!", Toast.LENGTH_SHORT).show()
            }

            if(intent.hasExtra("update_out")){
                val result = intent.getIntExtra("update_out", 0)
                if(result > 0) Toast.makeText(applicationContext, "Изменения сохранены", Toast.LENGTH_SHORT).show()

            }

            if(intent.hasExtra("remove_out")){
                val result = intent.getIntExtra("remove_out", 0)
                if (result > 0)  Toast.makeText(applicationContext, "Заметка удалена!", Toast.LENGTH_SHORT).show()
                startService(Intent(applicationContext, NoteService::class.java).putExtra("fill", 0)) //Обновить данные из базы
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
            var cIntent = Intent(applicationContext, CreateNoteActivity::class.java)
            startActivity(cIntent)
        }
    }



    private  class RAdapter(var data: ArrayList<Note>) : RecyclerView.Adapter<RAdapter.ViewHolder>() {
        var format: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy в HH:mm", Locale.ENGLISH)
        private var listener: Listener? = null
        private var lclistener: LongClickListener? = null
        constructor(): this(ArrayList<Note>())

        interface Listener {
            fun onClick(position: Int)
        }

        interface LongClickListener{
            fun onLongClick(position: Int, view: View?)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent!!.context).inflate(R.layout.card, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val note = data.get(position)
            val dateText = format.format(note.createdTime)

            holder.idView.text = note.nid.toString()
            holder.textView.text = note.text
            holder.dateView.text = dateText
            note.color?.let { holder.cardView.setBackgroundColor(it) }
            if(!TextUtils.isEmpty(note.title)){
                holder.title.text = note.title
            }else{
                holder.title.visibility = View.GONE
            }


            holder.cardView.setOnClickListener(object  : View.OnClickListener{
                override fun onClick(v: View?) {
                    if(listener != null){
                        listener!!.onClick(position)
                    }
                }
            })

            holder.cardView.setOnLongClickListener( object : View.OnLongClickListener{

                override fun onLongClick(view: View?): Boolean {
                    if(lclistener != null){
                        lclistener!!.onLongClick(position, view)
                    }
                    return true
                }
            })

        }

        inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            var textView : TextView
            var cardView : CardView
            var dateView : TextView
            var idView : TextView
            var title : TextView


            init {
                textView = item.card_text
                cardView = item.card
                dateView = item.date
                idView = item.item_sql_id
                title = item.card_title
            }
        }

        fun setListener(listener: Listener) {
            this.listener = listener
        }

        fun setLongClickListener(lclistener: LongClickListener){
            this.lclistener = lclistener
        }

     }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

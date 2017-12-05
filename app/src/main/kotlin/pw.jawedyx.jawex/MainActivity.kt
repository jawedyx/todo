package pw.jawedyx.jawex

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.card.view.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var choosedColor: Int = Color.LTGRAY //Стандартный цвет заметки
    private var isEdit: Boolean = false


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.hasExtra("fill_out")){
                val notes = intent.getSerializableExtra("fill_out")
                val sheetBehaviour = BottomSheetBehavior.from(sheet)

                recycler.adapter = RAdapter(notes as ArrayList<Note>)
                recycler.layoutManager = LinearLayoutManager(applicationContext)
                (recycler.adapter as RAdapter).setListener(object : RAdapter.Listener{
                    override fun onClick(position: Int) {
                        sheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        sheet.bottom_edit.setText(notes.get(position).text, TextView.BufferType.EDITABLE)
                        notes.get(position).color?.let { sheet.bottom_edit.setBackgroundColor(it) }
                        sheet.bottom_edit_id.text = notes.get(position).nid.toString()
                        isEdit = true


                    }
                })
                recycler.adapter.notifyDataSetChanged()
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


        val sheetBehaviour = BottomSheetBehavior.from(sheet)
        sheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        bottom_color_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, App.getColorNamesList())
        bottom_color_list.setOnItemClickListener({ adapterView, view, i, l ->
            choosedColor = Color.parseColor(App.getColorValuesList().get(i))
            bottom_edit.setBackgroundColor(choosedColor)
        })

        sheetBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if(!sheetBehaviour.state.equals(BottomSheetBehavior.STATE_HIDDEN)){
                    fab.setImageDrawable(resources.getDrawable(R.drawable.ic_create_black_24dp))
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_COLLAPSED -> {
                        fab.setImageDrawable(resources.getDrawable(R.drawable.ic_create_black_24dp))
                    }
                    else -> {
                        fab.setImageDrawable(resources.getDrawable(R.drawable.ic_add_black_24dp))
                    }
                }
            }
        })

        fab.setOnClickListener { view ->
            if(!sheetBehaviour.state.equals(BottomSheetBehavior.STATE_HIDDEN)){
                
                var values = ContentValues()
                if(isEdit){

                    if(!choosedColor.equals(Color.LTGRAY)) values.put("color", choosedColor) //TODO check without color pick
                    values.put("content", bottom_edit.text.toString())

                    var result = App.getRef().writableDatabase.update("Notes", values, "id = ?", arrayOf(sheet.bottom_edit_id.text.toString()) )
                    if(result > 0) Toast.makeText(applicationContext, "Изменения сохранены" + result, Toast.LENGTH_SHORT).show()
                    isEdit = false
                }else{
                    values.put("color", choosedColor)
                    values.put("content", bottom_edit.text.toString())
                    values.put("created_date", System.currentTimeMillis())
                    App.getRef().writableDatabase.insert("Notes", null, values)
                }

                sheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                bottom_edit.text.clear()
                bottom_edit.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                startService(fillerIntent.putExtra("fill", 0))

            }else{
                sheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    }

    private  class RAdapter(var data: ArrayList<Note>) : RecyclerView.Adapter<RAdapter.ViewHolder>() {
        var format: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy в HH:mm", Locale.ENGLISH)
        private var listener: Listener? = null
        constructor(): this(ArrayList<Note>())

        interface Listener {
            fun onClick(position: Int)
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

            holder.idView.setText(note.nid.toString())
            holder.textView.setText(note.text)
            holder.dateView.setText(dateText)
            note.color?.let { holder.cardView.setBackgroundColor(it) }

            holder.cardView.setOnClickListener {
                if(listener != null){
                    listener!!.onClick(position)
                }
            }
        }

        inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
            var textView : TextView
            var cardView : CardView
            var dateView : TextView
            var idView : TextView


            init {
                textView = item.card_text
                cardView = item.card
                dateView = item.date
                idView = item.item_sql_id
            }
        }

        fun setListener(listener: Listener) {
            this.listener = listener
        }

     }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

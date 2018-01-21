package pw.jawedyx.jawex

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.color_view.view.*
import kotlinx.android.synthetic.main.content_note.*

class CreateNoteActivity : AppCompatActivity() {
    private var choosedColor: Int = Color.LTGRAY //Стандартный цвет заметки

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        setSupportActionBar(note_toolbar)

        color_recycler.adapter = ColorAdapter()
        (color_recycler.adapter as ColorAdapter).setListener(object : ColorAdapter.Listener {
            override fun onClick(position: Int) {
                choosedColor = Color.parseColor(App.getColorValuesList().get(position))
                note_toolbar.setBackgroundColor(choosedColor)
            }
        })

        note_add.setOnClickListener {
            //test button
        }
    }

    private class ColorAdapter : RecyclerView.Adapter<ColorAdapter.ViewHolder>(){
        private var listener : Listener? = null

        interface Listener{
            fun onClick(position: Int)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.color_view, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return App.getColorValuesList().size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.colorItem?.setCardBackgroundColor(Color.parseColor(App.getColorValuesList().get(position)))

            holder?.itemView?.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    listener?.onClick(position)
                }
            })
        }

        inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item){
            val colorItem : CardView

            init {
                colorItem = item.note_outer_color_card
            }
        }

        fun setListener(listener: Listener) {
            this.listener = listener
        }
    }


}

package pw.jawedyx.jawex

import android.graphics.Color
import java.io.Serializable

data class Note(
        var nid: Int,
        var color : Int? = Color.RED,
        var text: String,
        var createdTime : Long,
        var title: String?
        ) : Serializable
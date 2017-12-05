package pw.jawedyx.jawex

import android.graphics.Color
import java.io.Serializable

data class Note(
        var color : Int? = Color.RED,
        var text: String,
        var createdTime : Long
        ) : Serializable
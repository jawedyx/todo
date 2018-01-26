package pw.jawedyx.jawex

import java.io.Serializable

data class Note(
        var nid: Int, //id в базе
        var color : String? = "FF0000",
        var text: String,
        var createdTime : Long,
        var title: String?
        ) : Serializable
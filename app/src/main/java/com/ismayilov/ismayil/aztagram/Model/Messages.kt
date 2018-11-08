package com.ismayilov.ismayil.aztagram.Model

class Messages {

    var message: String? = null
    var seen: Boolean? = null
    var time: Long? = null
    var type: String? = null
    var sender_id: String? = null

    constructor(message: String?, seen: Boolean?, time: Long?, type: String?, sender_id: String?) {
        this.message = message
        this.seen = seen
        this.time = time
        this.type = type
        this.sender_id = sender_id
    }

    constructor()

    override fun toString(): String {
        return "Messages(message=$message, seen=$seen, time=$time, type=$type, sender_id=$sender_id)"
    }

}
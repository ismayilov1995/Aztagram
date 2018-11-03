package com.ismayilov.ismayil.aztagram.Model

class Comments {

    var user_id:String? = null
    var comment:String? = null
    var comment_likes:String? = null
    var comment_date:Long? = null

    constructor(user_id: String?, comment: String?, comment_likes: String?, comment_date: Long?) {
        this.user_id = user_id
        this.comment = comment
        this.comment_likes = comment_likes
        this.comment_date = comment_date
    }

    constructor()


    override fun toString(): String {
        return "Comments(user_id=$user_id, comment=$comment, comment_like=$comment_likes, comment_date=$comment_date)"
    }

}
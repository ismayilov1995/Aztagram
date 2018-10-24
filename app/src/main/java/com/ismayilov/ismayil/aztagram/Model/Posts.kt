package com.ismayilov.ismayil.aztagram.Model

class Posts {

    var user_id:String? = null
    var post_id:String? = null
    var upload_date:Long? = null
    var description:String? = null
    var file_url:String? = null

    constructor(){}

    constructor(user_id: String?, post_id: String?, upload_date: Long?, description: String?, file_url: String?) {
        this.user_id = user_id
        this.post_id = post_id
        this.upload_date = upload_date
        this.description = description
        this.file_url = file_url
    }

    override fun toString(): String {
        return "Posts(user_id=$user_id, post_id=$post_id, upload_date=$upload_date, description=$description, file_url=$file_url)"
    }


}
package com.ismayilov.ismayil.aztagram.Model

class UserPosts {
    var user_id:String? = null
    var user_name:String? = null
    var user_photo:String? = null
    var post_id:String? = null
    var post_description:String? = null
    var post_url:String? = null
    var post_upload_date:Long? = null

    constructor(user_id: String?, user_name: String?, user_photo: String?, post_id: String?, post_description: String?, post_url: String?, post_upload_date: Long?) {
        this.user_id = user_id
        this.user_name = user_name
        this.user_photo = user_photo
        this.post_id = post_id
        this.post_description = post_description
        this.post_url = post_url
        this.post_upload_date = post_upload_date
    }

    constructor()

    override fun toString(): String {
        return "UserPosts(user_id=$user_id, user_name=$user_name, user_photo=$user_photo, post_id=$post_id, post_description=$post_description, post_url=$post_url, post_upload_date=$post_upload_date)"
    }
}
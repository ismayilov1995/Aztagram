package com.ismayilov.ismayil.aztagram.Model

class UsersDetails {

    var follower:String? = null
    var followers:String? = null
    var post:String? = null
    var profile_picture:String? = null
    var biography:String? = null
    var web_sitie:String? = null

    constructor()

    constructor(follower: String?, followers: String?, post: String?, profile_picture: String?, biography: String?, web_sitie: String?) {
        this.follower = follower
        this.followers = followers
        this.post = post
        this.profile_picture = profile_picture
        this.biography = biography
        this.web_sitie = web_sitie
    }

    override fun toString(): String {
        return "UsersDetails(follower=$follower, followers=$followers, post=$post, profile_picture=$profile_picture, biography=$biography, web_sitie=$web_sitie)"
    }


}
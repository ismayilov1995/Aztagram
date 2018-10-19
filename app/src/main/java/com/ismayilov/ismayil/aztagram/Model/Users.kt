package com.ismayilov.ismayil.aztagram.Model

class Users {

    var email: String? = null
    var password: String? = null
    var user_name: String? = null
    var full_name: String? = null
    var phone_number: String? = null
    var email_phone_number: String? = null
    var user_id: String? = null
    var users_details:UsersDetails? = null

    constructor() {}

    constructor(email: String?, password: String?, user_name: String?, full_name: String?, phone_number: String?, email_phone_number: String?, user_id: String?, users_details: UsersDetails?) {
        this.email = email
        this.password = password
        this.user_name = user_name
        this.full_name = full_name
        this.phone_number = phone_number
        this.email_phone_number = email_phone_number
        this.user_id = user_id
        this.users_details = users_details
    }

    override fun toString(): String {
        return "Users(email=$email, password=$password, user_name=$user_name, full_name=$full_name, phone_number=$phone_number, email_phone_number=$email_phone_number, user_id=$user_id, users_details=$users_details)"
    }


}

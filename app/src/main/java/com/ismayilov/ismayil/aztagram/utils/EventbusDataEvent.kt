package com.ismayilov.ismayil.aztagram.utils

import com.ismayilov.ismayil.aztagram.Model.Users

class EventbusDataEvent {
    internal class RegistDataLogistic(var phoneNumb:String?, var email:String?, var verificationID:String?,var code:String?,var registeredByEmail:Boolean)

    internal class BrodcastUserData(var user:Users?)

    internal class SharedImagePath(var filePath:String?,var fileTypeIsImage:Boolean?)

    internal class SendChoosenFilePath(var filePath:String?)

    internal class SendCameraRequestPermission(var canUseCamera:Boolean?)

    internal class SendPostIdWhereCommenting(var postId:String?)
}
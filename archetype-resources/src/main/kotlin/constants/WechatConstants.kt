package ${package}.constants

class WechatConstants {
    companion object {
        const val GET_SESSION_FROM_CODE = "https://api.weixin.qq.com/sns/jscode2session"
        const val GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token"
        const val SEND_TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send"
        const val SEND_UNIFORM_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send"
        const val ORDER_DONE_TEMPLATE_ID = ""
        const val PAYMENT_SUCCESS_TEMPLATE_ID = ""
        const val GOODS_PACKAGE_PAYMENT_SUCCESS_NOTIFY_TEMPLATE_ID = ""
        const val USER_PACKAGE_ACTIVE_NOTIFY_TEMPLATE_ID = ""
        const val PAYMENT_REMINDER_TEMPLATE_ID = ""
    }
}
package ${package}.models

import io.swagger.annotations.ApiModelProperty
import java.util.*

data class Response<T>(
        @ApiModelProperty(position = 1, notes = "状态码(0:操作成功, 1000:未指定错误码的一般错误, 1001:尚未登录, 1002:登录信息已失效, 其他:指定了错误码需要做对应处理的错误)")
        val code: Int,

        @ApiModelProperty(position = 2, notes = "消息(操作成功是为:操作成功, 错误时为具体错误原因)")
        val message: String?,

        @ApiModelProperty(position = 3, notes = "数据对象(没有数据返回时不会返回data)")
        val data: T? = null,

        @ApiModelProperty(position = 4, notes = "返回时间")
        val time: Date = Date()
) {
    companion object {
        fun <T> error(code: Int, message: String?, data: T? = null): Response<T> {
            return Response(code, message, data)
        }

        fun <T> error(message: String?): Response<T> {
            return Response(1000, message)
        }

        fun <T> success(data: T? = null): Response<T> {
            return Response(0, "操作成功", data)
        }
    }

    object Errors {
        fun <T> apiNotPublished() = error<T>("尚未发布此接口")
        fun <T> tokenNotFound() = error<T>(1001, "尚未登录")
        fun <T> tokenInvalid() = error<T>(1002, "登录信息已失效")
        fun <T> permissionDenied() = error<T>(1003, "权限不足")
        fun <T> AccountNotExist() = error<T>(1004, "用户不存在")
        fun <T> PasswordIncorrect() = error<T>(1005, "密码不正确")
        fun <T> wechatNotAuthorized() = error<T>(1016, "微信授权失败")
    }
}

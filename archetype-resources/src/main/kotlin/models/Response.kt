package ${package_name}.models

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

data class Response<T>(
    @field:Schema(description = "状态码(0:操作成功, 1000:未指定错误码的一般错误, 1001:尚未登录, 1002:登录信息已失效, 其他:指定了错误码需要做对应处理的错误)")
    val code: Int,

    @field:Schema(description = "消息(操作成功是为:操作成功, 错误时为具体错误原因)")
    val message: String?,

    @field:Schema(description = "数据对象(没有数据返回时不会返回data)")
    val data: T? = null,

    @field:Schema(description = "返回时间")
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
        fun <T> accountNotExist() = error<T>(1004, "用户不存在")
        fun <T> passwordIncorrect() = error<T>(1005, "账号或密码不正确")
        fun <T> accountAlreadyExist() = error<T>(1006, "用户已存在")
    }
}

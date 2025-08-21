package ${package_name}.viewmodels.user

import io.swagger.v3.oas.annotations.media.Schema

data class SignInResponse(
    @field:Schema(description = "Token")
    val token: String,

    @field:Schema(description = "用户是否存在(小程序用)")
    var userExists: Boolean = false,

    @field:Schema(description = "是否绑定手机号(小程序用)")
    var mobileBound: Boolean = false
)

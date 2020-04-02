package ${package_name}.viewmodels.user

import io.swagger.annotations.ApiModelProperty

data class SignInResponse(
        @ApiModelProperty(position = 0, notes = "Token")
        val token: String,

        @ApiModelProperty(position = 1, notes = "用户是否存在(小程序用)")
        var userExists: Boolean = false,

        @ApiModelProperty(position = 2, notes = "是否绑定手机号(小程序用)")
        var mobileBound: Boolean = false
)

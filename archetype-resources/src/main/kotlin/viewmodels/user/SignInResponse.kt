package ${package_name}.viewmodels.user

import io.swagger.annotations.ApiModelProperty

data class SignInResponse(
        @ApiModelProperty(position = 0, notes = "Token")
        val token: String,

        @ApiModelProperty(position = 1, notes = "用户名")
        var username: String? = null,

        @ApiModelProperty(position = 2, notes = "微信昵称")
        var nickname: String? = null
)

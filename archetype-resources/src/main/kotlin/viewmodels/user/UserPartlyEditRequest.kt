package ${package_name}.viewmodels.user

import io.swagger.annotations.ApiModelProperty

data class UserPartlyEditRequest(
        @ApiModelProperty(position = 0, notes = "主键", required = false, hidden = true)
        var id: Long? = null,

        @ApiModelProperty(position = 1, notes = "手机号", required = false, hidden = false)
        val mobile: String? = null,

        @ApiModelProperty(position = 2, notes = "微信openid", required = false, hidden = false)
        val openId: String? = null,

        @ApiModelProperty(position = 3, notes = "微信昵称", required = false, hidden = false)
        val nickname: String? = null,

        @ApiModelProperty(position = 4, notes = "用户名", required = false, hidden = false)
        val username: String? = null,

        @ApiModelProperty(position = 5, notes = "密码", required = false, hidden = false)
        var password: String? = null,

        @ApiModelProperty(position = 6, notes = "盐", required = false, hidden = false)
        var salt: String? = null,

        @ApiModelProperty(position = 7, notes = "微信头像url", required = false, hidden = false)
        val avatarUrl: String? = null
)

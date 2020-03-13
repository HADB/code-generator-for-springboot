package ${package}.models

import io.swagger.annotations.ApiModelProperty
import ${package}.annotations.NoArg
import java.math.BigDecimal
import java.util.*

@NoArg
data class User(
        @ApiModelProperty(position = 0, notes = "主键")
        val id: Long = 0,

        @ApiModelProperty(position = 1, notes = "手机号")
        val mobile: String? = null,

        @ApiModelProperty(position = 2, notes = "微信openid")
        val openId: String? = null,

        @ApiModelProperty(position = 3, notes = "微信昵称")
        val nickname: String? = null,

        @ApiModelProperty(position = 4, notes = "用户名")
        val username: String? = null,

        @ApiModelProperty(position = 5, notes = "密码")
        var password: String? = null,

        @ApiModelProperty(position = 6, notes = "盐")
        var salt: String? = null,

        @ApiModelProperty(position = 7, notes = "微信头像url")
        val avatarUrl: String? = null,

        @ApiModelProperty(position = 8, notes = "角色")
        val role: String? = null,

        @ApiModelProperty(position = 9, notes = "创建时间")
        val createTime: Date? = null,

        @ApiModelProperty(position = 10, notes = "更新时间")
        val updateTime: Date? = null
)

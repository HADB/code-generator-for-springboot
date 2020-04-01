package ${package_name}.viewmodels.user

import io.swagger.annotations.ApiModelProperty
import ${package_name}.models.Paging
import java.math.BigDecimal
import java.util.*

data class UserSearchRequest(
        @ApiModelProperty(position = 0, notes = "手机号")
        val mobile: String? = null,

        @ApiModelProperty(position = 1, notes = "微信openid")
        val openId: String? = null,

        @ApiModelProperty(position = 2, notes = "微信昵称")
        val nickname: String? = null,

        @ApiModelProperty(position = 3, notes = "用户名")
        val username: String? = null,

        @ApiModelProperty(position = 4, notes = "密码")
        val password: String? = null,

        @ApiModelProperty(position = 5, notes = "盐")
        val salt: String? = null,

        @ApiModelProperty(position = 6, notes = "微信头像url")
        val avatarUrl: String? = null,

        @ApiModelProperty(position = 7, notes = "角色")
        val role: String? = null,

        @ApiModelProperty(position = 8, notes = "创建时间 From")
        val createTimeFrom: Date? = null,

        @ApiModelProperty(position = 9, notes = "创建时间 To")
        val createTimeTo: Date? = null,

        @ApiModelProperty(position = 10, notes = "更新时间 From")
        val updateTimeFrom: Date? = null,

        @ApiModelProperty(position = 11, notes = "更新时间 To")
        val updateTimeTo: Date? = null,

        @ApiModelProperty(position = 97, notes = "排序字段")
        val sortBy: String? = null,

        @ApiModelProperty(position = 98, notes = "排序顺序")
        val sortOrder: String? = null,

        @ApiModelProperty(position = 99, notes = "分页(默认第1页，每页显示10条)")
        val paging: Paging = Paging(1,10)
)

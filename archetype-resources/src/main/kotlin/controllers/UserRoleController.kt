package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.models.Response
import ${package_name}.models.UserRole
import ${package_name}.services.UserRoleService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.userRole.UserRoleEditRequest
import ${package_name}.viewmodels.userRole.UserRoleSearchRequest
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Api(tags = ["用户-角色"])
@CrossOrigin
@RestController
@RequestMapping("/user-role")
class UserRoleController {
    @Resource
    private lateinit var userRoleService: UserRoleService

    /*
     * 新增
     */
    @ApiOperation(value = "新增「用户-角色」")
    @RequestMapping
    fun add(@RequestBody request: UserRoleEditRequest): Response<Any> {
        val userRoleId = userRoleService.editUserRole(request)
        return Response.success(userRoleId)
    }

    /*
     * 修改
     */
    @ApiOperation(value = "修改「用户-角色」")
    @ApiImplicitParam(name = "id", value = "UserRole ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: UserRoleEditRequest): Response<Any> {
        request.id = id
        val userRole = userRoleService.getUserRoleById(id) ?: return Response.error("UserRole 不存在")
        userRoleService.editUserRole(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @ApiOperation(value = "删除「用户-角色」")
    @ApiImplicitParam(name = "id", value = "UserRole ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        userRoleService.deleteUserRole(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「用户-角色」详情")
    @ApiImplicitParam(name = "id", value = "UserRole ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<UserRole> {
        val userRole = userRoleService.getUserRoleById(id)
        return Response.success(userRole)
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「用户-角色」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: UserRoleSearchRequest): Response<SearchResponse<UserRole>> {
        val results = userRoleService.searchPagingUserRoles(request)
        val count = userRoleService.searchPagingUserRolesCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
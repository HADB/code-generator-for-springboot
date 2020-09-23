package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.models.Response
import ${package_name}.models.Role
import ${package_name}.services.RoleService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.role.RoleEditRequest
import ${package_name}.viewmodels.role.RoleSearchRequest
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Api(tags = ["角色"])
@CrossOrigin
@RestController
@RequestMapping("/role")
class RoleController {
    @Resource
    private lateinit var roleService: RoleService

    /*
     * 新增
     */
    @ApiOperation(value = "新增「角色」")
    @RequestMapping(method = [RequestMethod.POST])
    fun add(@RequestBody request: RoleEditRequest): Response<Any> {
        val roleId = roleService.editRole(request)
        return Response.success(roleId)
    }

    /*
     * 修改
     */
    @ApiOperation(value = "修改「角色」")
    @ApiImplicitParam(name = "id", value = "Role ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: RoleEditRequest): Response<Any> {
        request.id = id
        val role = roleService.getRoleById(id) ?: return Response.error("Role 不存在")
        roleService.editRole(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @ApiOperation(value = "删除「角色」")
    @ApiImplicitParam(name = "id", value = "Role ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        roleService.deleteRole(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「角色」详情")
    @ApiImplicitParam(name = "id", value = "Role ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<Role> {
        val role = roleService.getRoleById(id)
        return Response.success(role)
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「角色」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: RoleSearchRequest): Response<SearchResponse<Role>> {
        val results = roleService.searchPagingRoles(request)
        val count = roleService.searchPagingRolesCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
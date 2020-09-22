package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.models.Response
import ${package_name}.models.RolePermission
import ${package_name}.services.RolePermissionService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.rolePermission.RolePermissionEditRequest
import ${package_name}.viewmodels.rolePermission.RolePermissionSearchRequest
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Api(tags = ["角色-权限"])
@CrossOrigin
@RestController
@RequestMapping("/role-permission")
class RolePermissionController {
    @Resource
    private lateinit var rolePermissionService: RolePermissionService

    /*
     * 新增
     */
    @ApiOperation(value = "新增「角色-权限」")
    @RequestMapping
    fun add(@RequestBody request: RolePermissionEditRequest): Response<Any> {
        val rolePermissionId = rolePermissionService.editRolePermission(request)
        return Response.success(rolePermissionId)
    }

    /*
     * 修改
     */
    @ApiOperation(value = "修改「角色-权限」")
    @ApiImplicitParam(name = "id", value = "RolePermission ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: RolePermissionEditRequest): Response<Any> {
        request.id = id
        val rolePermission = rolePermissionService.getRolePermissionById(id) ?: return Response.error("RolePermission 不存在")
        rolePermissionService.editRolePermission(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @ApiOperation(value = "删除「角色-权限」")
    @ApiImplicitParam(name = "id", value = "RolePermission ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        rolePermissionService.deleteRolePermission(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「角色-权限」详情")
    @ApiImplicitParam(name = "id", value = "RolePermission ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<RolePermission> {
        val rolePermission = rolePermissionService.getRolePermissionById(id)
        return Response.success(rolePermission)
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「角色-权限」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: RolePermissionSearchRequest): Response<SearchResponse<RolePermission>> {
        val results = rolePermissionService.searchPagingRolePermissions(request)
        val count = rolePermissionService.searchPagingRolePermissionsCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
package ${package_name}.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiOperation
import ${package_name}.models.Response
import ${package_name}.models.Permission
import ${package_name}.services.PermissionService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.permission.PermissionEditRequest
import ${package_name}.viewmodels.permission.PermissionSearchRequest
import org.springframework.web.bind.annotation.*
import javax.annotation.Resource

@Api(tags = ["权限"])
@CrossOrigin
@RestController
@RequestMapping("/permission")
class PermissionController {
    @Resource
    private lateinit var permissionService: PermissionService

    /*
     * 新增
     */
    @ApiOperation(value = "新增「权限」")
    @RequestMapping
    fun add(@RequestBody request: PermissionEditRequest): Response<Any> {
        val permissionId = permissionService.editPermission(request)
        return Response.success(permissionId)
    }

    /*
     * 修改
     */
    @ApiOperation(value = "修改「权限」")
    @ApiImplicitParam(name = "id", value = "Permission ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: PermissionEditRequest): Response<Any> {
        request.id = id
        val permission = permissionService.getPermissionById(id) ?: return Response.error("Permission 不存在")
        permissionService.editPermission(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @ApiOperation(value = "删除「权限」")
    @ApiImplicitParam(name = "id", value = "Permission ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        permissionService.deletePermission(id)
        return Response.success()
    }

    /*
     * 获取详情
     */
    @ApiOperation(value = "获取「权限」详情")
    @ApiImplicitParam(name = "id", value = "Permission ID", required = true, dataTypeClass = Long::class)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<Permission> {
        val permission = permissionService.getPermissionById(id)
        return Response.success(permission)
    }

    /*
     * 搜索
     */
    @ApiOperation(value = "搜索「权限」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: PermissionSearchRequest): Response<SearchResponse<Permission>> {
        val results = permissionService.searchPagingPermissions(request)
        val count = permissionService.searchPagingPermissionsCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
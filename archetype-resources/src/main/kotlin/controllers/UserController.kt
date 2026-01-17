package ${package_name}.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Operation
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.AllowSignedIn
import ${package_name}.annotations.AllowUserNotExist
import ${package_name}.annotations.CurrentUser
import ${package_name}.helpers.PasswordHelper
import ${package_name}.models.Response
import ${package_name}.models.User
import ${package_name}.services.UserService
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.user.*
import org.springframework.web.bind.annotation.*
import jakarta.annotation.Resource

@Tag(name = "用户")
@CrossOrigin
@RestController
@RequestMapping("/user")
class UserController {
    @Resource
    private lateinit var userService: UserService

    @Resource
    private lateinit var passwordHelper: PasswordHelper


    @Operation(summary = "新增或修改「用户」")
    @RequestMapping("", method = [RequestMethod.PUT])
    fun addOrEdit(@RequestBody request: UserEditRequest): Response<Any> {
        val userId = userService.addOrEditUser(request)
        return Response.success(userId)
    }

    @Operation(summary = "修改「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun editById(@PathVariable id: Long, @RequestBody request: UserEditRequest): Response<Any> {
        request.id = id
        userService.getUserById(id) ?: return Response.error("User 不存在")
        userService.addOrEditUser(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PATCH])
    fun editPartlyById(@PathVariable id: Long, @RequestBody request: UserPartlyEditRequest): Response<Any> {
        request.id = id
        userService.getUserById(id) ?: return Response.error("User 不存在")
        userService.editUserPartly(request)
        return Response.success()
    }

    @Operation(summary = "删除「用户」")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun deleteById(@PathVariable id: Long): Response<Any> {
        userService.deleteUser(id)
        return Response.success()
    }

    @Operation(summary = "获取「用户」详情")
    @Parameter(name = "id", description = "User ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getById(@PathVariable id: Long): Response<User> {
        val user = userService.getUserById(id)
        return Response.success(user)
    }

    @Operation(summary = "搜索「用户」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: UserSearchRequest): Response<SearchResponse<User>> {
        val results = userService.searchUsers(request)
        val count = userService.searchUsersCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }

    @Operation(summary = "检查「用户」登录状态")
    @RequestMapping("/status", method = [RequestMethod.GET])
    @AllowSignedIn
    fun checkStatus(): Response<Any> {
        return Response.success()
    }

    @Operation(summary = "账号密码登录")
    @RequestMapping("/password-sign-in", method = [RequestMethod.POST])
    @AllowAnonymous
    fun passwordSignIn(@RequestBody request: PasswordSignInRequest, @RequestAttribute service: String): Response<Any> {
        val user = userService.getUserByUsername(request.username) ?: return Response.Errors.accountNotExist()

        if (!passwordHelper.verify(request.password, user)) {
            return Response.Errors.passwordIncorrect()
        }

        val response = userService.signIn(service, user)
        return Response.success(response)
    }

    @Operation(summary = "注销")
    @RequestMapping("/sign-out", method = [RequestMethod.POST])
    @AllowSignedIn
    fun signOut(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        userService.signOut(service, user)
        return Response.success()
    }


    @Operation(summary = "用户信息")
    @RequestMapping("/info", method = [RequestMethod.GET])
    @AllowSignedIn
    fun info(@RequestAttribute service: String, @CurrentUser user: User): Response<Any> {
        return Response.success(user)
    }
}

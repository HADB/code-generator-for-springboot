package ${package_name}.others

import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.AllowSignedIn
import ${package_name}.annotations.AllowUserNotExist
import ${package_name}.annotations.BuiltInRole
import ${package_name}.constants.AppConstants
import ${package_name}.constants.BuiltInRoleKey
import ${package_name}.helpers.ResponseHelper
import ${package_name}.helpers.TokenHelper
import ${package_name}.models.Response
import ${package_name}.models.User
import ${package_name}.services.UserService
import ${package_name}.viewmodels.user.UserSearchRequest
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class AuthorizationInterceptor : HandlerInterceptor {

    @Resource
    private lateinit var tokenHelper: TokenHelper

    @Resource
    private lateinit var responseHelper: ResponseHelper

    @Resource
    private lateinit var userService: UserService


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) {
            return true
        }

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
        response.setHeader("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers")

        val builtInRole = handler.method.getAnnotation(BuiltInRole::class.java)
        val allowAnonymous = handler.method.getAnnotation(AllowAnonymous::class.java) != null
        val allowSignedIn = handler.method.getAnnotation(AllowSignedIn::class.java) != null
        val allowUserNotExist = handler.method.getAnnotation(AllowUserNotExist::class.java) != null

        if (request.method == RequestMethod.OPTIONS.name) {
            responseHelper.setResponse(response, Response.success())
            return false
        }

        // 设置终端类型
        val service = request.getHeader(AppConstants.SERVICE) ?: AppConstants.Service.DEFAULT
        request.setAttribute(AppConstants.SERVICE, service)

        // 允许匿名访问，直接通过
        if (allowAnonymous) {
            return true
        }

        val token = request.getHeader(AppConstants.TOKEN)

        // 不存在token，不通过
        if ((token == null || token.isEmpty())) {
            responseHelper.setResponse(response, Response.Errors.tokenNotFound())
            return false
        }

        val key = tokenHelper.getTokenKey(service, token)

        // Token 失效
        if (key == null) {
            responseHelper.setResponse(response, Response.Errors.tokenInvalid())
            return false
        }

        // Token有效
        request.setAttribute(AppConstants.KEY, key)

        if (allowUserNotExist) {
            return true
        }

        // 获取 user
        val user = userService.searchUser(UserSearchRequest(id = key.toLong()))
        if (user == null) {
            responseHelper.setResponse(response, Response.Errors.tokenInvalid())
            return false
        }
        val userRoles = userService.getRolesByUserId(user.id)

        // 允许已登录用户访问，直接通过
        if (allowSignedIn) {
            return true
        }

        // 系统管理员，直接通过
        if (userRoles.any { r -> r.key == BuiltInRoleKey.Admin }) {
            return true
        }

        // 验证代码内置角色
        if (!builtInRole?.roles.isNullOrEmpty()) {
            for (role in builtInRole.roles) {
                // 拥有其中一种角色则通过
                if (userRoles.any { r -> r.key == role }) {
                    return true
                }
            }
        }

        // 验证 API 权限
        val controllerMappingPath = handler.bean.javaClass.getAnnotation(RequestMapping::class.java).value[0]
        val methodMappingPath = handler.method.getAnnotation(RequestMapping::class.java).value[0]
        if (checkApiPermission(user, request.method, "$${controllerMappingPath}$${methodMappingPath}")) {
            return true
        }

        // 权限不足
        responseHelper.setResponse(response, Response.Errors.permissionDenied())
        return false
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) = Unit

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) = Unit

    private fun checkApiPermission(user: User, method: String, path: String): Boolean {
        val userPermissions = userService.getPermissionsByUserId(user.id)
        return userPermissions.any { p -> p.apiMethod == method && p.apiPath == path }
    }
}

package ${package_name}.others

import ${package_name}.constants.AppConstants
import ${package_name}.models.User
import ${package_name}.services.UserService
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import javax.annotation.Resource

@Component
class CurrentUserResolver : HandlerMethodArgumentResolver {
    @Resource
    private lateinit var userService: UserService

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (parameter.parameterType.isAssignableFrom(User::class.java) && parameter.hasParameterAnnotation(${package_name}.annotations.CurrentUser::class.java)) {
            return true
        }
        return false
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        val key = webRequest.getAttribute(AppConstants.KEY, RequestAttributes.SCOPE_REQUEST)?.toString()
        val service = webRequest.getAttribute(AppConstants.SERVICE, RequestAttributes.SCOPE_REQUEST)?.toString()
        if (service != null && key != null) {
            return userService.getUserByKey(service, key)
        }
        return null
    }
}

package ${package}.others

import ${package}.constants.AppConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import ${package}.models.User
import ${package}.services.UserService

@Component
class CurrentUserResolver : HandlerMethodArgumentResolver {
    @Autowired
    lateinit var userService: UserService

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (parameter.parameterType.isAssignableFrom(User::class.java) && parameter.hasParameterAnnotation(${package}.annotations.CurrentUser::class.java)) {
            return true
        }
        return false
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): Any? {
        if (webRequest.getAttribute(AppConstants.USER_ID, RequestAttributes.SCOPE_REQUEST) == null) {
            return null
        }
        val currentUserId = webRequest.getAttribute(AppConstants.USER_ID, RequestAttributes.SCOPE_REQUEST)?.toString()?.toLong()
        val service = webRequest.getAttribute(AppConstants.SERVICE, RequestAttributes.SCOPE_REQUEST)?.toString()
        if (currentUserId != null) {
            return userService.getUserById(currentUserId)
        }
        return null
    }
}

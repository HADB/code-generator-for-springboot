package ${package_name}.configurations

import ${package_name}.others.AuthorizationInterceptor
import ${package_name}.others.CurrentUserResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry


@Configuration
class AppWebMvcConfigurer : WebMvcConfigurer {
    @Autowired
    private lateinit var currentUserResolver: CurrentUserResolver

    @Autowired
    private lateinit var authorizationInterceptor: AuthorizationInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v2/api-docs")
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/webjars/**")
        super.addInterceptors(registry)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/")
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*")
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(currentUserResolver)
        super.addArgumentResolvers(argumentResolvers)
    }
}
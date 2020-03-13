package ${package}.others

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import ${package}.services.UserService
import ${package}.viewmodels.user.UserEditRequest

@Component
class DbInitializationRunner : CommandLineRunner {
    @Autowired
    private lateinit var userService: UserService

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(vararg args: String?) {
        val admin = userService.getUserByUsername("Admin")
        if (admin == null) {
            userService.addUserWithPassword(UserEditRequest(
                    username = "Admin",
                    role = "Admin",
                    password = "Qcga1WKe3idhi2r1"
            ))
            logger.info("已创建初始用户Admin")
        } else {
            logger.info("初始用户Admin已存在")
        }

        val bean = userService.getUserByUsername("Bean")
        if (bean == null) {
            userService.addUserWithPassword(UserEditRequest(
                    username = "Bean",
                    role = "Admin",
                    password = "Dream2020@DO"
            ))
            logger.info("已创建初始用户Bean")
        } else {
            logger.info("初始用户Bean已存在")
        }
    }
}

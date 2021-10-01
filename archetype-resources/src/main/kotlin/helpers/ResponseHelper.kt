package ${package_name}.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import ${package_name}.models.Response
import org.springframework.stereotype.Component
import javax.annotation.Resource
import javax.servlet.http.HttpServletResponse

@Component
class ResponseHelper {
    @Resource
    private lateinit var objectMapper: ObjectMapper

    fun setResponse(httpResponse: HttpServletResponse, response: Response<Any>) {
        httpResponse.characterEncoding = "UTF-8"
        httpResponse.contentType = "application/json"
        httpResponse.status = 200
        httpResponse.writer.write(objectMapper.writeValueAsString(response))
        httpResponse.writer.flush()
    }
}

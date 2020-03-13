package ${package}.helpers

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ${package}.models.Response
import javax.servlet.http.HttpServletResponse

@Component
class ResponseHelper {
    @Autowired
    lateinit var jacksonMapper: ObjectMapper

    fun setResponse(httpResponse: HttpServletResponse, response: Response<Any>) {
        httpResponse.characterEncoding = "UTF-8"
        httpResponse.contentType = "application/json"
        httpResponse.status = 200
        httpResponse.writer.write(jacksonMapper.writeValueAsString(response))
        httpResponse.writer.flush()
    }
}

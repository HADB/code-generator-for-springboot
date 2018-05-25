package ${package_name}.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ${package_name}.annotations.AllowAnonymous
import ${package_name}.annotations.Permission
import ${package_name}.models.Response
import ${package_name}.services.${model_upper_camelcase}Service
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}EditRequest
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}SearchRequest

@CrossOrigin
@RestController
@RequestMapping("/${model_camelcase}")
class ${model_upper_camelcase}Controller {
    @Autowired
    lateinit var ${model_camelcase}Service: ${model_upper_camelcase}Service

    /*
     * 添加
     */
    @PostMapping
    @Permission
    fun add(@RequestBody request: ${model_upper_camelcase}EditRequest): Response {
        ${model_camelcase}Service.edit${model_upper_camelcase}(request)
        return Response.success()
    }

    /*
     * 修改
     */
    @PutMapping("/{id}")
    @Permission
    fun edit(@PathVariable("id") id: Long, @RequestBody request: ${model_upper_camelcase}EditRequest): Response {
        request.id = id
        ${model_camelcase}Service.edit${model_upper_camelcase}(request)
        return Response.success()
    }

    /*
     * 删除
     */
    @DeleteMapping("/{id}")
    @Permission
    fun delete(@PathVariable("id") id: Long): Response {
        ${model_camelcase}Service.delete${model_upper_camelcase}(id)
        return Response.success()
    }

    /*
     * 获取全部
     */
    @GetMapping("/all")
    @AllowAnonymous
    fun all(): Response {
        val result = ${model_camelcase}Service.getAll${model_upper_camelcase}s()
        return Response.success(result)
    }

    /*
     * 搜索
     */
    @PostMapping("/search")
    @AllowAnonymous
    fun search(@RequestBody request: ${model_upper_camelcase}SearchRequest): Response {
        val results = ${model_camelcase}Service.searchPaging${model_upper_camelcase}s(request)
        val count = ${model_camelcase}Service.searchPaging${model_upper_camelcase}sCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
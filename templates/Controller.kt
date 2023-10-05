package ${package_name}.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Operation
import ${package_name}.models.Response
import ${package_name}.models.${model_upper_camelcase}
import ${package_name}.services.${model_upper_camelcase}Service
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}EditRequest
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}PartlyEditRequest
import ${package_name}.viewmodels.${model_camelcase}.${model_upper_camelcase}SearchRequest
import org.springframework.web.bind.annotation.*
import jakarta.annotation.Resource

@Tag(name = "${model_description}")
@CrossOrigin
@RestController
@RequestMapping("/${model_dasherize}")
class ${model_upper_camelcase}Controller {
    @Resource
    private lateinit var ${model_camelcase}Service: ${model_upper_camelcase}Service

    @Operation(summary = "新增「${model_description}」")
    @RequestMapping("", method = [RequestMethod.POST])
    fun add(@RequestBody request: ${model_upper_camelcase}EditRequest): Response<Any> {
        val ${model_camelcase}Id = ${model_camelcase}Service.edit${model_upper_camelcase}(request)
        return Response.success(${model_camelcase}Id)
    }

    @Operation(summary = "修改「${model_description}」")
    @Parameter(name = "id", description = "${model_upper_camelcase} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: ${model_upper_camelcase}EditRequest): Response<Any> {
        request.id = id
        ${model_camelcase}Service.get${model_upper_camelcase}ById(id) ?: return Response.error("${model_upper_camelcase} 不存在")
        ${model_camelcase}Service.edit${model_upper_camelcase}(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「${model_description}」")
    @Parameter(name = "id", description = "${model_upper_camelcase} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PATCH])
    fun editPartly(@PathVariable("id") id: Long, @RequestBody request: ${model_upper_camelcase}PartlyEditRequest): Response<Any> {
        request.id = id
        ${model_camelcase}Service.get${model_upper_camelcase}ById(id) ?: return Response.error("${model_upper_camelcase} 不存在")
        ${model_camelcase}Service.edit${model_upper_camelcase}Partly(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「${model_description}」")
    @Parameter(name = "id", description = "${model_upper_camelcase} ID", required = true)
    @RequestMapping("/{id}/patch", method = [RequestMethod.PUT])
    fun editPartlyCompatible(@PathVariable("id") id: Long, @RequestBody request: ${model_upper_camelcase}PartlyEditRequest): Response<Any> {
        request.id = id
        ${model_camelcase}Service.get${model_upper_camelcase}ById(id) ?: return Response.error("${model_upper_camelcase} 不存在")
        ${model_camelcase}Service.edit${model_upper_camelcase}Partly(request)
        return Response.success()
    }

    @Operation(summary = "删除「${model_description}」")
    @Parameter(name = "id", description = "${model_upper_camelcase} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        ${model_camelcase}Service.delete${model_upper_camelcase}(id)
        return Response.success()
    }

    @Operation(summary = "获取「${model_description}」详情")
    @Parameter(name = "id", description = "${model_upper_camelcase} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<${model_upper_camelcase}> {
        val ${model_camelcase} = ${model_camelcase}Service.get${model_upper_camelcase}ById(id)
        return Response.success(${model_camelcase})
    }

    @Operation(summary = "搜索「${model_description}」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: ${model_upper_camelcase}SearchRequest): Response<SearchResponse<${model_upper_camelcase}>> {
        val results = ${model_camelcase}Service.searchPaging${model_upper_camelcase}s(request)
        val count = ${model_camelcase}Service.searchPaging${model_upper_camelcase}sCount(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
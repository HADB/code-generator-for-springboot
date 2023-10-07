package ${package_name}.controllers

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Operation
import ${package_name}.models.Response
import ${package_name}.models.${model_name_pascal_case}
import ${package_name}.services.${model_name_pascal_case}Service
import ${package_name}.viewmodels.common.SearchResponse
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}EditRequest
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}PartlyEditRequest
import ${package_name}.viewmodels.${model_name_camel_case}.${model_name_pascal_case}SearchRequest
import org.springframework.web.bind.annotation.*
import jakarta.annotation.Resource

@Tag(name = "${model_description}")
@CrossOrigin
@RestController
@RequestMapping("/${model_name_snake_case}")
class ${model_name_pascal_case}Controller {
    @Resource
    private lateinit var ${model_name_camel_case}Service: ${model_name_pascal_case}Service

    @Operation(summary = "新增「${model_description}」")
    @RequestMapping("", method = [RequestMethod.POST])
    fun add(@RequestBody request: ${model_name_pascal_case}EditRequest): Response<Any> {
        val ${model_name_camel_case}Id = ${model_name_camel_case}Service.edit${model_name_pascal_case}(request)
        return Response.success(${model_name_camel_case}Id)
    }

    @Operation(summary = "修改「${model_description}」")
    @Parameter(name = "id", description = "${model_name_pascal_case} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PUT])
    fun edit(@PathVariable("id") id: Long, @RequestBody request: ${model_name_pascal_case}EditRequest): Response<Any> {
        request.id = id
        ${model_name_camel_case}Service.get${model_name_pascal_case}ById(id) ?: return Response.error("${model_name_pascal_case} 不存在")
        ${model_name_camel_case}Service.edit${model_name_pascal_case}(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「${model_description}」")
    @Parameter(name = "id", description = "${model_name_pascal_case} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.PATCH])
    fun editPartly(@PathVariable("id") id: Long, @RequestBody request: ${model_name_pascal_case}PartlyEditRequest): Response<Any> {
        request.id = id
        ${model_name_camel_case}Service.get${model_name_pascal_case}ById(id) ?: return Response.error("${model_name_pascal_case} 不存在")
        ${model_name_camel_case}Service.edit${model_name_pascal_case}Partly(request)
        return Response.success()
    }

    @Operation(summary = "部分修改「${model_description}」")
    @Parameter(name = "id", description = "${model_name_pascal_case} ID", required = true)
    @RequestMapping("/{id}/patch", method = [RequestMethod.PUT])
    fun editPartlyCompatible(@PathVariable("id") id: Long, @RequestBody request: ${model_name_pascal_case}PartlyEditRequest): Response<Any> {
        request.id = id
        ${model_name_camel_case}Service.get${model_name_pascal_case}ById(id) ?: return Response.error("${model_name_pascal_case} 不存在")
        ${model_name_camel_case}Service.edit${model_name_pascal_case}Partly(request)
        return Response.success()
    }

    @Operation(summary = "删除「${model_description}」")
    @Parameter(name = "id", description = "${model_name_pascal_case} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable("id") id: Long): Response<Any> {
        ${model_name_camel_case}Service.delete${model_name_pascal_case}(id)
        return Response.success()
    }

    @Operation(summary = "获取「${model_description}」详情")
    @Parameter(name = "id", description = "${model_name_pascal_case} ID", required = true)
    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun get(@PathVariable("id") id: Long): Response<${model_name_pascal_case}> {
        val ${model_name_camel_case} = ${model_name_camel_case}Service.get${model_name_pascal_case}ById(id)
        return Response.success(${model_name_camel_case})
    }

    @Operation(summary = "搜索「${model_description}」")
    @RequestMapping("/search", method = [RequestMethod.POST])
    fun search(@RequestBody request: ${model_name_pascal_case}SearchRequest): Response<SearchResponse<${model_name_pascal_case}>> {
        val results = ${model_name_camel_case}Service.searchPaging${model_name_plural_pascal_case}(request)
        val count = ${model_name_camel_case}Service.searchPaging${model_name_plural_pascal_case}Count(request)
        val response = SearchResponse(results, count)
        return Response.success(response)
    }
}
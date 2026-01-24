# coding=utf-8
"""
crud-code-generator-for-springboot
"""

import argparse
import os
import platform
import shutil
import string
import subprocess

import inflection

CURRENT_PATH = os.getcwd()  # 当前目录
TEMPLATE_PATH = os.path.join(CURRENT_PATH, "templates")  # 模板目录
ARCHETYPE_RESOURCE_PATH = os.path.join(CURRENT_PATH, "archetype-resources")  # 原型资源目录

project_info = {
    "project_path": None,
    "package_name": None,
    "group_id": None,
    "artifact_id": None,
    "version": None,
    "description": None,
    "port": None,
    "debug": False,
}

file_info = {}


def substitute(template_str, **vars):
    merged_vars = {**project_info, **file_info, **vars}
    template = string.Template(template_str)
    return template.substitute(merged_vars)


def init_project():
    print("初始化项目，复制 .sql 文件")
    g = os.walk(ARCHETYPE_RESOURCE_PATH)
    for path, _, file_list in g:
        for file_name in file_list:
            if file_name == ".DS_Store":
                continue
            sub_path = path[len(ARCHETYPE_RESOURCE_PATH) + 1 :]

            with open(os.path.join(path, file_name), "r", encoding="utf-8") as file_read:
                if "src/main/resources/ddl" in path or "src\\main\\resources\\ddl" in path:
                    directory_path = os.path.join(project_info["project_path"], sub_path)
                    if not os.path.exists(directory_path):
                        os.makedirs(directory_path)
                    file_path = os.path.join(directory_path, file_name)
                    if project_info["debug"]:
                        print(file_path)
                    content = file_read.read()
                    with open(file_path, "w", encoding="utf-8") as file_write:
                        file_write.write(content)


def get_column_type_property_name(column):
    column_type = "String"
    property_name = column["name"]
    if column["type"] == "bigint":
        column_type = "Long"
    elif column["type"] == "double":
        column_type = "Double"
    elif column["type"] == "decimal":
        column_type = "BigDecimal"
    elif column["type"] == "tinyint" and column["name"].startswith("is_"):
        column_type = "Boolean"
    elif column["type"] == "tinyint" or column["type"] == "int":
        column_type = "Int"
    elif column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
        column_type = "Date"
    return column_type, property_name


def copy_archetype_resources():
    if project_info["debug"]:
        print("复制骨架资源文件")
    g = os.walk(ARCHETYPE_RESOURCE_PATH)
    for path, _, file_list in g:
        for file_name in file_list:
            if file_name == ".DS_Store":
                continue
            sub_path = path[len(ARCHETYPE_RESOURCE_PATH) + 1 :]

            directory_path = os.path.join(project_info["project_path"], sub_path)
            if "src/main/kotlin" in path or "src\\main\\kotlin" in path:
                directory_path = os.path.join(project_info["project_path"], "src/main/kotlin", *project_info["package_name"].split("."), sub_path[len("src/main/kotlin") + 1 :])
            if not os.path.exists(directory_path):
                os.makedirs(directory_path)
            file_path = os.path.join(directory_path, file_name)
            if os.path.exists(file_path) and os.path.splitext(file_name)[-1] == ".sql":
                if project_info["debug"]:
                    print("跳过:" + file_path)
                continue
            if project_info["debug"]:
                print("准备复制:" + file_path)

            # 直接复制文件列表（不进行模板替换）
            copy_files = [
                ".gitlab-ci.yml",
                "gradle-wrapper.jar",
                "gradlew",
                "gradlew.bat",
                "renovate.json",
            ]
            binary_extensions = [".jar", ".class", ".zip", ".tar", ".gz"]

            is_copy = file_name in copy_files or any(file_name.endswith(ext) for ext in binary_extensions)

            if is_copy:
                # 直接复制
                with open(os.path.join(path, file_name), "rb") as file_read:
                    content = file_read.read()
                with open(file_path, "wb") as file_write:
                    file_write.write(content)
            else:
                # 进行模板替换
                with open(os.path.join(path, file_name), "r", encoding="utf-8") as file_read:
                    content = substitute(file_read.read())
                with open(file_path, "w", encoding="utf-8") as file_write:
                    file_write.write(content)

            if project_info["debug"]:
                print("已复制:" + file_path)
            if file_name == "generator.sh" and platform.system() != "Windows":
                subprocess.run(["chmod", "+x", file_path])


def run_package():
    global file_info
    if project_info["debug"]:
        print("执行 CRUD")
    input_path = os.path.join(project_info["project_path"], "src/main/resources/ddl")
    # kotlin 输出目录
    kotlin_output_path = os.path.join(project_info["project_path"], "src", "main", "kotlin", *project_info["package_name"].split("."))
    # mapper 输出目录
    mapper_output_path = os.path.join(project_info["project_path"], "src", "main", "resources", "mapper")
    if os.path.exists(kotlin_output_path):
        shutil.rmtree(kotlin_output_path)
    if os.path.exists(mapper_output_path):
        shutil.rmtree(mapper_output_path)

    for input_file_name in os.listdir(input_path):
        file_info = {}
        input_file_path = os.path.join(input_path, input_file_name)
        if not os.path.isdir(input_file_path):
            if not input_file_name.endswith(".sql"):
                if project_info["debug"]:
                    print("跳过: " + input_file_name)
                continue
            if project_info["debug"]:
                print("处理: " + input_file_name)
            file_info["table_name"] = os.path.splitext(input_file_name)[0].strip()  # 文件名

            if file_info["table_name"] == "t_flyway_history":
                continue

            file_info["model_name"] = file_info["table_name"][2:] if file_info["table_name"].startswith("t_") else file_info["table_name"]
            file_info["model_name_pascal_case"] = inflection.camelize(file_info["model_name"], True)  # PascalCase
            file_info["model_name_pascal_case_plural"] = inflection.pluralize(file_info["model_name_pascal_case"])  # PascalCases 复数形式
            file_info["model_name_camel_case"] = inflection.camelize(file_info["model_name"], False)  # camelCase
            file_info["model_name_snake_case"] = inflection.underscore(file_info["model_name"])  # snake_case
            file_info["model_name_kebab_case"] = inflection.dasherize(file_info["model_name_snake_case"])  # kebab-case

            table_description = file_info["model_name"]  # 表注释
            file_read = open(input_file_path, "r", encoding="UTF-8")
            columns = []  # 字段数组

            for line in file_read:
                if line.find("CREATE TABLE ") >= 0:
                    if line.strip().split()[2].split(".")[-1].strip("`") != file_info["table_name"]:
                        print("表名与文件名不一致！")
                        return
                    continue
                if line.find(" KEY ") >= 0 or line.find(" INDEX ") >= 0 or line.find(" UNIQUE ") >= 0:
                    continue  # 跳过索引
                if line.find("CHARSET=") >= 0:
                    table_description = line[line.find("COMMENT") + 8 :].split("'")[1]  # 读取表注释
                    continue
                if len(line.strip().split()) < 2:
                    continue
                column = {
                    "name": line.strip().split()[0].strip("`"),
                    "type": line.strip().split()[1].split("(")[0].lower(),
                    "nullable": True,
                    "default": "null",
                }
                if line.find("NOT NULL ") > 0:
                    column["nullable"] = False
                    column["default"] = None

                if line.find("DEFAULT ") > 0:
                    column["default"] = line[line.find("DEFAULT ") + 8 :].split()[0].replace("'", '"')  # 字段默认值
                    if column["default"] == "NULL" or column["default"] == "CURRENT_TIMESTAMP":
                        column["default"] = "null"
                    if column["type"].find("decimal") >= 0:
                        column["default"] = f"BigDecimal({column['default'].replace('"', '')})"
                    if (column["type"].find("int") >= 0 or column["type"].find("double") >= 0 or column["type"].find("decimal") >= 0) and column["default"].find('"') >= 0:
                        column["default"] = column["default"].replace('"', "")
                    if column["type"] == "tinyint" and column["name"].startswith("is_"):
                        if column["default"] == "0":
                            column["default"] = "false"
                        else:
                            column["default"] = "true"

                column["comment"] = line[line.find("COMMENT") + 8 :].split("'")[1]  # 字段注释
                columns.append(column)
            file_read.close()

            # [Model]Mapper.xml
            lines = []
            for column in columns:
                property_name = column["name"]
                if file_info["model_name"] == "user" and (column["name"] == "password" or column["name"] == "salt"):
                    continue
                lines.append(f"        `{file_info['model_name_snake_case']}`.`{column['name']}`")

            column_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if file_info["model_name"] == "user" and (column["name"] == "password" or column["name"] == "salt"):
                    continue
                if column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
                    lines.append(f'        <if test="request.{inflection.camelize(column["name"], False)}From != null">')
                    lines.append(f"            AND `{file_info['model_name_snake_case']}`.`{column['name']}` &gt;= #{{request.{inflection.camelize(column['name'], False)}From}}")
                    lines.append("        </if>")
                    lines.append(f'        <if test="request.{inflection.camelize(column["name"], False)}To != null">')
                    lines.append(f"            AND `{file_info['model_name_snake_case']}`.`{column['name']}` &lt;= #{{request.{inflection.camelize(column['name'], False)}To}}")
                    lines.append("        </if>")
                    continue
                if column["type"] == "varchar" or column["type"] == "text":
                    lines.append(
                        f"        <if test=\"request.{inflection.camelize(column['name'], False)} != null and request.{inflection.camelize(column['name'], False)} !=''\">"
                    )
                else:
                    lines.append(f'        <if test="request.{inflection.camelize(column["name"], False)} != null">')
                lines.append(f"            AND `{file_info['model_name_snake_case']}`.`{column['name']}` = #{{request.{inflection.camelize(column['name'], False)}}}")
                lines.append("        </if>")

            lines.append("        <if test=\"request.keywords != null and request.keywords !=''\">")
            lines.append("            AND (")
            keyword_lines = []
            for column in columns:
                if column["name"] == "id" or column["name"].endswith("_number"):
                    keyword_lines.append(f"            `{file_info['model_name_snake_case']}`.`{column['name']}` = #{{request.keywords}}")
                elif column["type"] == "varchar" and ("name" in column["name"] or "description" in column["name"]):
                    keyword_lines.append(f"            `{file_info['model_name_snake_case']}`.`{column['name']}` LIKE CONCAT('%', #{{request.keywords}}, '%')")
            lines.append(" OR\n".join(keyword_lines))
            lines.append("            )")
            lines.append("        </if>")
            search_where = "\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id":
                    continue
                lines.append(f"        `{column['name']}`")
            name_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id":
                    continue
                elif column["name"] == "create_time" or column["name"] == "update_time" or column["name"] == "created_time" or column["name"] == "updated_time":
                    lines.append("        NOW()")
                else:
                    lines.append(f"        #{{{file_info['model_name_camel_case']}.{inflection.camelize(column['name'], False)}}}")
            value_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id" or column["name"] == "create_time" or column["name"] == "created_time":
                    continue
                elif column["name"] == "update_time" or column["name"] == "updated_time":
                    lines.append(f"        `{column['name']}` = NOW()")
                else:
                    lines.append(
                        f"        `{column['name']}` = COALESCE(#{{{file_info['model_name_camel_case']}.{inflection.camelize(column['name'], False)}}}, `{column['name']}`)"
                    )
            update_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id":
                    continue
                elif column["name"] == "create_time" or column["name"] == "update_time" or column["name"] == "created_time" or column["name"] == "updated_time":
                    lines.append("        NOW()")
                else:
                    lines.append(f"        #{{request.{inflection.camelize(column['name'], False)}}}")
            partly_value_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id" or column["name"] == "create_time" or column["name"] == "created_time":
                    continue
                elif column["name"] == "update_time" or column["name"] == "updated_time":
                    lines.append(f"        `{column['name']}` = NOW()")
                else:
                    lines.append(f'        <if test="request.{inflection.camelize(column["name"], False)} != null">')
                    lines.append(f"            `{column['name']}` = #{{request.{inflection.camelize(column['name'], False)}}},")
                    lines.append("        </if>")
            partly_update_list = "\n".join(lines)

            if file_info["model_name"] == "user":
                file_read = open(os.path.join(TEMPLATE_PATH, "UserMapper.xml"), "r", encoding="UTF-8")
            else:
                file_read = open(os.path.join(TEMPLATE_PATH, "Mapper.xml"), "r", encoding="UTF-8")

            content = substitute(
                file_read.read(),
                column_list=column_list,
                name_list=name_list,
                search_where=search_where,
                update_list=update_list,
                value_list=value_list,
                partly_update_list=partly_update_list,
                partly_value_list=partly_value_list,
            )

            if not os.path.exists(mapper_output_path):
                os.makedirs(mapper_output_path)

            file_write = open(
                os.path.join(mapper_output_path, file_info["model_name_pascal_case"] + "Mapper.xml"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model].kt
            content = (
                f"package {project_info['package_name']}.models\n\n"
                f"import io.swagger.v3.oas.annotations.media.Schema\n"
                f"import {project_info['package_name']}.annotations.NoArg\n"
                f"import java.math.BigDecimal\n"
                f"import java.util.*\n"
                f"\n"
                f"@NoArg\n"
                f"data class {file_info['model_name_pascal_case']}(\n"
            )
            lines = []
            swagger_index = 0
            for column in columns:
                column_type, property_name = get_column_type_property_name(column)

                if column["nullable"]:
                    column_type += "?"
                if column["default"]:
                    column_type += " = " + column["default"]
                if column["name"] == "id":
                    column_type += " = 0"
                line_text = f'    @field:Schema(description = "{column["comment"]}")\n'

                # 特殊处理 for User.kt
                if file_info["model_name"] == "user" and (property_name != "id" and property_name != "create_time" and property_name != "update_time"):
                    line_text += f"    var {inflection.camelize(property_name, False)}: {column_type}"
                else:
                    line_text += f"    val {inflection.camelize(property_name, False)}: {column_type}"
                lines.append(line_text)
                swagger_index += 1
            content += f"{',\n\n'.join(lines)},\n"
            content += ")\n"

            output_models_path = os.path.join(kotlin_output_path, "models")
            if not os.path.exists(output_models_path):
                os.makedirs(output_models_path)
            file_write = open(os.path.join(output_models_path, file_info["model_name_pascal_case"] + ".kt"), "w", encoding="UTF-8")
            file_write.write(content)
            file_write.close()

            # [Model]EditRequest.kt
            content = (
                f"package {project_info['package_name']}.viewmodels.{file_info['model_name_camel_case']}\n\n"
                f"import io.swagger.v3.oas.annotations.media.Schema\n"
                f"import java.math.BigDecimal\n"
                f"import java.util.*\n"
                f"import jakarta.validation.constraints.NotNull\n"
                f"\n"
                f"data class {file_info['model_name_pascal_case']}EditRequest(\n"
            )
            lines = []
            swagger_index = 0
            for column in columns:
                define = "val"
                required = True
                hidden = False
                line_text = ""
                column_type, property_name = get_column_type_property_name(column)

                if column["nullable"]:
                    column_type += "?"
                    required = False
                if column["default"]:
                    column_type += " = " + column["default"]
                if column["name"] == "id":
                    define = "var"
                    required = False
                    hidden = True
                if column["name"] == "create_time" or column["name"] == "update_time" or column["name"] == "created_time" or column["name"] == "updated_time":
                    continue
                if column["name"] == "id":
                    column_type += " = 0"
                if required:
                    line_text += f'    @field:NotNull(message = "{inflection.camelize(property_name, False)} 不能为空")\n'
                line_text += f'    @field:Schema(description = "{column["comment"]}", required = {"true" if required else "false"}, hidden = {"true" if hidden else "false"})\n'
                line_text += f"    {define} {inflection.camelize(property_name, False)}: {column_type}"
                lines.append(line_text)
                swagger_index += 1
            content += f"{',\n\n'.join(lines)},\n"
            content += ")\n"

            output_viewmodels_path = os.path.join(kotlin_output_path, "viewmodels", file_info["model_name_camel_case"])
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(
                os.path.join(output_viewmodels_path, file_info["model_name_pascal_case"] + "EditRequest.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]PartlyEditRequest.kt
            content = (
                f"package {project_info['package_name']}.viewmodels.{file_info['model_name_camel_case']}\n\n"
                f"import io.swagger.v3.oas.annotations.media.Schema\n"
                f"import java.math.BigDecimal\n"
                f"import java.util.*\n"
                f"import jakarta.validation.constraints.NotNull\n"
                f"\n"
                f"data class {file_info['model_name_pascal_case']}PartlyEditRequest(\n"
            )
            lines = []
            swagger_index = 0
            for column in columns:
                define = "var"
                hidden = "true"
                if column["name"] == "create_time" or column["name"] == "update_time" or column["name"] == "created_time" or column["name"] == "updated_time":
                    continue
                column_type, property_name = get_column_type_property_name(column)
                if column["name"] != "id":
                    define = "val"
                    hidden = "false"
                    column_type += "? = null"
                # 特殊处理 for UserPartlyEditRequest.kt
                if file_info["model_name"] == "user" and (column["name"] == "password" or column["name"] == "salt"):
                    define = "var"
                line_text = f'    @field:Schema(description = "{column["comment"]}", required = false, hidden = {hidden})\n'
                line_text += f"    {define} {inflection.camelize(property_name, False)}: {column_type}"

                lines.append(line_text)
                swagger_index += 1
            content += f"{',\n\n'.join(lines)},\n"
            content += ")\n"

            output_viewmodels_path = os.path.join(kotlin_output_path, "viewmodels", file_info["model_name_camel_case"])
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(
                os.path.join(output_viewmodels_path, file_info["model_name_pascal_case"] + "PartlyEditRequest.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]SearchRequest.kt
            content = (
                f"package {project_info['package_name']}.viewmodels.{file_info['model_name_camel_case']}\n\n"
                f"import io.swagger.v3.oas.annotations.media.Schema\n"
                f"import {project_info['package_name']}.models.Paging\n"
                f"import {project_info['package_name']}.viewmodels.common.SortOrder\n"
                f"import java.math.BigDecimal\n"
                f"import java.util.*\n"
                f"\n"
                f"data class {file_info['model_name_pascal_case']}SearchRequest(\n"
            )
            lines = []
            swagger_index = 0
            for column in columns:
                column_type, property_name = get_column_type_property_name(column)
                column_type += "? = null"
                define = "val"
                if column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
                    line_text = f'    @field:Schema(description = "{column["comment"]} From")\n'
                    line_text += f"    {define} {inflection.camelize(column['name'] + 'From', False)}: {column_type}"
                    lines.append(line_text)
                    swagger_index += 1
                    line_text = f'    @field:Schema(description = "{column["comment"]} To")\n'
                    line_text += f"    {define} {inflection.camelize(column['name'] + 'To', False)}: {column_type}"
                    lines.append(line_text)
                    swagger_index += 1
                    continue

                line_text = f'    @field:Schema(description = "{column["comment"]}")\n'
                line_text += f"    {define} {inflection.camelize(property_name, False)}: {column_type}"
                lines.append(line_text)
                swagger_index += 1

            line_text = '    @field:Schema(description = "搜索关键词")\n'
            line_text += "    val keywords: String? = null"
            lines.append(line_text)
            swagger_index += 1

            line_text = '    @field:Schema(description = "排序条件")\n'
            line_text += "    val sortOrders: List<SortOrder>? = null"
            lines.append(line_text)
            swagger_index += 1

            line_text = '    @field:Schema(description = "分页(默认第1页，每页显示10条)")\n'
            line_text += "    val paging: Paging = Paging(1, 10)"
            lines.append(line_text)
            content += f"{',\n\n'.join(lines)},\n"
            content += ")\n"

            output_viewmodels_path = os.path.join(kotlin_output_path, "viewmodels", file_info["model_name_camel_case"])
            if not os.path.exists(output_viewmodels_path):
                os.makedirs(output_viewmodels_path)
            file_write = open(
                os.path.join(output_viewmodels_path, file_info["model_name_pascal_case"] + "SearchRequest.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]Mapper.kt
            file_read = open(os.path.join(TEMPLATE_PATH, "Mapper.kt"), "r", encoding="UTF-8")
            content = substitute(file_read.read())

            output_mappers_path = os.path.join(kotlin_output_path, "mappers")
            if not os.path.exists(output_mappers_path):
                os.makedirs(output_mappers_path)
            file_write = open(
                os.path.join(output_mappers_path, file_info["model_name_pascal_case"] + "Mapper.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]Service.kt
            if file_info["model_name"] == "user":
                file_read = open(os.path.join(TEMPLATE_PATH, "UserService.kt"), "r", encoding="UTF-8")
                columns_data = []
                add_user_with_password_columns_data = []
                for column in columns:
                    property_name = column["name"]
                    if column["name"] == "create_time" or column["name"] == "update_time" or column["name"] == "created_time" or column["name"] == "updated_time":
                        continue
                    columns_data.append(f"            {inflection.camelize(property_name, False)} = request.{inflection.camelize(property_name, False)}")

                    if column["name"] != "password" and column["name"] != "salt":
                        add_user_with_password_columns_data.append(f"            {inflection.camelize(property_name, False)} = request.{inflection.camelize(property_name, False)}")
                    else:
                        add_user_with_password_columns_data.append(f"            {inflection.camelize(property_name, False)} = {inflection.camelize(property_name, False)}")

                content = substitute(
                    file_read.read(),
                    columns_data=",\n".join(columns_data),
                    add_user_with_password_columns_data=",\n".join(add_user_with_password_columns_data),
                )
            else:
                file_read = open(os.path.join(TEMPLATE_PATH, "Service.kt"), "r", encoding="UTF-8")

                columns_data = []
                for column in columns:
                    property_name = column["name"]
                    if column["name"] == "create_time" or column["name"] == "update_time" or column["name"] == "created_time" or column["name"] == "updated_time":
                        continue
                    columns_data.append(f"            {inflection.camelize(property_name, False)} = request.{inflection.camelize(property_name, False)},")
                content = substitute(file_read.read(), columns_data="\n".join(columns_data))

            output_services_path = os.path.join(kotlin_output_path, "services")
            if not os.path.exists(output_services_path):
                os.makedirs(output_services_path)
            file_write = open(
                os.path.join(output_services_path, file_info["model_name_pascal_case"] + "Service.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()

            # [Model]Controller.kt
            file_read = open(os.path.join(TEMPLATE_PATH, "Controller.kt"), "r", encoding="UTF-8")

            content = substitute(file_read.read(), model_description=table_description)

            output_controllers_path = os.path.join(kotlin_output_path, "controllers")
            if not os.path.exists(output_controllers_path):
                os.makedirs(output_controllers_path)
            file_write = open(
                os.path.join(output_controllers_path, file_info["model_name_pascal_case"] + "Controller.kt"),
                "w",
                encoding="UTF-8",
            )
            file_write.write(content)
            file_write.close()


def str_input(prompt, required=False, default=None):
    required_str = " (必填)" if required else ""
    default_str = f" [{default}]" if default is not None else ""
    user_input = input(f"{prompt}{required_str}{default_str}: ").strip()
    result = user_input or default
    if required and not result:
        print("该项输入为必填项")
        return str_input(prompt, required, default)
    return result


def bool_input(prompt, default):
    user_input = input(f"{prompt} [{'Y/n' if default else 'y/N'}]: ").strip().lower()
    if user_input in ["y", "yes"]:
        return True
    elif user_input in ["n", "no"]:
        return False
    elif user_input == "":
        return default
    else:
        print("请输入 y 或 n，或者直接回车使用默认值")
        return bool_input(prompt, default)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Code Generator for Spring Boot")
    parser.add_argument("command", choices=["init", "run"], help="Command to execute (init for interactive mode, run for non-interactive mode)")
    parser.add_argument("--group_id", help="项目 Group ID")
    parser.add_argument("--artifact_id", help="项目 Artifact ID")
    parser.add_argument("--version", help="项目版本号", default="1.0.0")
    parser.add_argument("--port", help="端口号", default=8000)
    parser.add_argument("--package_name", help="项目包名")
    parser.add_argument("--project_path", help="项目路径")
    parser.add_argument("--description", help="项目描述")
    parser.add_argument("--debug", action="store_true", help="是否开启调试模式", default=False)

    args = parser.parse_args()

    if args.command == "init":
        project_info["group_id"] = str_input("请输入项目 Group ID", required=True)
        project_info["artifact_id"] = str_input("请输入项目 Artifact ID", required=True)
        default_package_name = f"{project_info['group_id']}.{project_info['artifact_id']}".replace("-", "_")
        project_info["package_name"] = str_input("请输入项目包名", required=False, default=default_package_name)
        project_info["version"] = str_input("请输入项目版本号", required=True, default=args.version)
        project_info["port"] = str_input("请输入端口号", required=True, default=args.port)
        project_info["project_path"] = str_input("请输入项目路径", required=True)
        project_info["description"] = str_input("请输入项目描述")
        project_info["debug"] = bool_input("是否开启调试模式", args.debug)

    elif args.command == "run":
        project_info["group_id"] = args.group_id
        project_info["artifact_id"] = args.artifact_id
        default_package_name = f"{project_info['group_id']}.{project_info['artifact_id']}".replace("-", "_")
        project_info["package_name"] = args.package_name or default_package_name
        project_info["version"] = args.version
        project_info["port"] = args.port
        project_info["project_path"] = args.project_path
        project_info["description"] = args.description
        project_info["debug"] = args.debug

    if project_info["debug"]:
        print("debug mode")
        print(project_info)
    project_path = project_info.get("project_path", "")
    if not os.path.exists(project_path):
        os.makedirs(project_path)

    if not os.path.exists(os.path.join(project_path, "pom.xml")):
        # 初始化项目（复制 .sql 文件）
        init_project()

    run_package()
    copy_archetype_resources()

    if args.command == "init":
        # 提交 commit
        os.chdir(project_path)
        subprocess.run(["git", "init"])
        subprocess.run(["git", "checkout", "-b", "generator"])
        subprocess.run(["git", "add", "."])
        subprocess.run(["git", "commit", "-m", "gen: init project"])
    print("generator 执行完成")

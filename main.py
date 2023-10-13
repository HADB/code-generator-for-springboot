# coding=utf-8
"""
crud-code-generator-for-springboot
"""
import getopt
import os
import shutil
import string
import subprocess
import sys

import inflection
import inflect

inflect_engine = inflect.engine()
CURRENT_PATH = os.getcwd()  # 当前目录
TEMPLATE_PATH = os.path.join(CURRENT_PATH, "templates")  # 模板目录
ARCHETYPE_RESOURCE_PATH = os.path.join(CURRENT_PATH, "archetype-resources")  # 原型资源目录

controller_names = []

project_info = {
    "project_path": None,
    "package_name": None,
    "group_id": None,
    "artifact_id": None,
    "version": None,
    "description": None,
    "port": None,
    "payment": False,
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
            if file_name == ".DS_Store" or (not project_info["payment"] and file_name == "t_payment.sql"):
                continue
            sub_path = path[len(ARCHETYPE_RESOURCE_PATH) + 1 :]

            with open(os.path.join(path, file_name), "r", encoding="utf-8") as file_read:
                if "src/main/resources/sql" in path or "src\\main\\resources\\sql" in path:
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
    controller_names.sort()
    controller_names_text = ", ".join(controller_names)
    for path, _, file_list in g:
        for file_name in file_list:
            if file_name == ".DS_Store":
                continue
            sub_path = path[len(ARCHETYPE_RESOURCE_PATH) + 1 :]

            with open(os.path.join(path, file_name), "r", encoding="utf-8") as file_read:
                directory_path = os.path.join(project_info["project_path"], sub_path)
                if "src/main/kotlin" in path or "src\\main\\kotlin" in path:
                    directory_path = os.path.join(
                        project_info["project_path"],
                        "src/main/kotlin",
                        *project_info["package_name"].split("."),
                        sub_path[len("src/main/kotlin") + 1 :]
                    )
                if not os.path.exists(directory_path):
                    os.makedirs(directory_path)
                file_path = os.path.join(directory_path, file_name)
                if os.path.exists(file_path) and os.path.splitext(file_name)[-1] == ".sql":
                    if project_info["debug"]:
                        print("跳过:" + file_path)
                    continue
                if not project_info["payment"] and file_name in [
                    "t_payment.sql",
                    "PaymentController.kt",
                    "PaymentService.kt",
                    "PaymentMapper.kt",
                    "PaymentStatus.kt",
                    "PaymentMapper.xml",
                    "WxPrepayResponse.kt",
                ]:
                    continue
                if project_info["debug"]:
                    print("准备复制:" + file_path)
                content = substitute(file_read.read(), controller_names_text=controller_names_text)

                with open(file_path, "w", encoding="utf-8") as file_write:
                    file_write.write(content)
                    if project_info["debug"]:
                        print("已复制:" + file_path)
                if file_name == "generator.sh":
                    subprocess.run(["chmod", "+x", file_path])


def run_package():
    global file_info
    if project_info["debug"]:
        print("执行 CRUD")
    input_path = os.path.join(project_info["project_path"], "src/main/resources/sql")
    # kotlin 输出目录
    kotlin_output_path = os.path.join(
        project_info["project_path"], "src", "main", "kotlin", *project_info["package_name"].split(".")
    )
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
            if not project_info["payment"] and file_info["table_name"] == "t_payment":
                continue
            if file_info["table_name"] == "t_flyway_history":
                continue
            file_info["model_name"] = (
                file_info["table_name"][2:] if file_info["table_name"].startswith("t_") else file_info["table_name"]
            )
            file_info["model_name_pascal_case"] = inflection.camelize(file_info["model_name"], True)  # PascalCase
            file_info["model_name_plural_pascal_case"] = inflection.camelize(
                inflect_engine.plural(file_info["model_name"]), True
            )  # PascalCases
            file_info["model_name_camel_case"] = inflection.camelize(file_info["model_name"], False)  # camelCase
            file_info["model_name_snake_case"] = inflection.dasherize(file_info["model_name"])  # snake_case

            table_description = file_info["model_name"]  # 表注释
            file_read = open(input_file_path, "r", encoding="UTF-8")
            columns = []  # 字段数组

            for line in file_read:
                if line.find("CREATE TABLE ") >= 0:
                    if line.strip().split()[2].split(".")[-1].strip("`") != file_info["table_name"]:
                        print("表名与文件名不一致！")
                        return
                    controller_names.append('"%s"' % file_info["model_name_pascal_case"])
                    continue
                if line.find(" KEY ") >= 0:
                    continue  # 跳过索引
                if line.find("CHARSET=") >= 0:
                    table_description = line[line.find("COMMENT") + 8 :].split("'")[1]  # 读取表注释
                    continue
                if len(line.strip().split()) < 2:
                    continue
                column = {
                    "name": line.strip().split()[0].strip("`"),
                    "type": line.strip().split()[1].split("(")[0].lower(),
                }
                if line.find("NOT NULL ") > 0:
                    column["nullable"] = False  # 字段是否可空
                    column["default"] = None
                else:
                    column["nullable"] = True
                    column["default"] = "null"

                if line.find("DEFAULT ") > 0:
                    column["default"] = line[line.find("DEFAULT ") + 8 :].split()[0].replace("'", '"')  # 字段默认值
                    if column["default"] == "NULL" or column["default"] == "CURRENT_TIMESTAMP":
                        column["default"] = "null"
                    if column["type"].find("decimal") >= 0:
                        column["default"] = "BigDecimal(%s)" % column["default"].replace('"', "")
                    if (
                        column["type"].find("int") >= 0
                        or column["type"].find("double") >= 0
                        or column["type"].find("decimal") >= 0
                    ) and column["default"].find('"') >= 0:
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
                if column["name"] == "is_delete":
                    continue
                if file_info["model_name"] == "user" and (column["name"] == "password" or column["name"] == "salt"):
                    continue
                lines.append("        `%s`.`%s`" % (file_info["model_name_camel_case"], column["name"]))

            column_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if (
                    column["name"] == "id"
                    or column["name"] == "sort_weight"
                    or (
                        file_info["model_name"] == "user" and (column["name"] == "password" or column["name"] == "salt")
                    )
                ):
                    continue
                if column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
                    lines.append(
                        '        <if test="request.%sFrom != null">' % (inflection.camelize(column["name"], False))
                    )
                    lines.append(
                        "            AND `%s`.`%s` &gt;= #{request.%sFrom}"
                        % (
                            file_info["model_name_camel_case"],
                            column["name"],
                            inflection.camelize(column["name"], False),
                        )
                    )
                    lines.append("        </if>")
                    lines.append(
                        '        <if test="request.%sTo != null">' % (inflection.camelize(column["name"], False))
                    )
                    lines.append(
                        "            AND `%s`.`%s` &lt;= #{request.%sTo}"
                        % (
                            file_info["model_name_camel_case"],
                            column["name"],
                            inflection.camelize(column["name"], False),
                        )
                    )
                    lines.append("        </if>")
                    continue
                if column["name"] == "is_delete":
                    lines.append("        AND `%s`.`%s` = 0" % (file_info["model_name_camel_case"], column["name"]))
                    continue
                if column["type"] == "varchar" or column["type"] == "text":
                    lines.append(
                        "        <if test=\"request.%s != null and request.%s !=''\">"
                        % (
                            inflection.camelize(column["name"], False),
                            inflection.camelize(column["name"], False),
                        )
                    )
                else:
                    lines.append(
                        '        <if test="request.%s != null">' % (inflection.camelize(column["name"], False))
                    )
                lines.append(
                    "            AND `%s`.`%s` = #{request.%s}"
                    % (
                        file_info["model_name_camel_case"],
                        column["name"],
                        inflection.camelize(column["name"], False),
                    )
                )
                lines.append("        </if>")
            search_where = "\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id" or column["name"] == "is_delete":
                    continue
                lines.append("        `%s`" % (column["name"]))
            name_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "id" or column["name"] == "is_delete":
                    continue
                elif (
                    column["name"] == "create_time"
                    or column["name"] == "update_time"
                    or column["name"] == "created_time"
                    or column["name"] == "updated_time"
                ):
                    lines.append("        NOW()")
                else:
                    lines.append(
                        "        #{%s.%s}"
                        % (file_info["model_name_camel_case"], inflection.camelize(column["name"], False))
                    )
            value_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if (
                    column["name"] == "id"
                    or column["name"] == "create_time"
                    or column["name"] == "created_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                elif column["name"] == "update_time" or column["name"] == "updated_time":
                    lines.append("        `%s` = NOW()" % (column["name"]))
                else:
                    lines.append(
                        "        `%s` = #{%s.%s}"
                        % (
                            column["name"],
                            file_info["model_name_camel_case"],
                            inflection.camelize(column["name"], False),
                        )
                    )
            update_list = ",\n".join(lines)

            lines = []
            for column in columns:
                if (
                    column["name"] == "id"
                    or column["name"] == "create_time"
                    or column["name"] == "created_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                elif column["name"] == "update_time" or column["name"] == "updated_time":
                    lines.append("        `%s` = NOW()" % (column["name"]))
                else:
                    if column["type"] == "varchar" or column["type"] == "text":
                        lines.append(
                            "        <if test=\"request.%s != null and request.%s !=''\">"
                            % (inflection.camelize(column["name"], False), inflection.camelize(column["name"], False))
                        )
                    else:
                        lines.append(
                            '        <if test="request.%s != null">' % (inflection.camelize(column["name"], False))
                        )
                    lines.append(
                        "            `%s` = #{request.%s},"
                        % (column["name"], inflection.camelize(column["name"], False))
                    )
                    lines.append("        </if>")
            update_partly_list = "\n".join(lines)

            lines = []
            for column in columns:
                if column["name"] == "sort_weight":
                    lines.append("`%s`.`sort_weight` DESC" % (file_info["model_name_camel_case"]))
            if not lines:
                lines.append("`%s`.`id` DESC" % (file_info["model_name_camel_case"]))
            orders = ", ".join(lines)

            if file_info["model_name"] == "user":
                file_read = open(os.path.join(TEMPLATE_PATH, "UserMapper.xml"), "r", encoding="UTF-8")
            else:
                file_read = open(os.path.join(TEMPLATE_PATH, "Mapper.xml"), "r", encoding="UTF-8")

            content = substitute(
                file_read.read(),
                column_list=column_list,
                name_list=name_list,
                orders=orders,
                search_where=search_where,
                update_list=update_list,
                update_partly_list=update_partly_list,
                value_list=value_list,
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
            content = "package %s.models\n\n" % project_info["package_name"]
            content += "import io.swagger.v3.oas.annotations.media.Schema\n"
            content += "import %s.annotations.NoArg\n" % project_info["package_name"]
            content += "import java.math.BigDecimal\n"
            content += "import java.util.*\n\n"
            content += "@NoArg\n"
            content += "data class %s(\n" % (file_info["model_name_pascal_case"])
            lines = []
            swagger_index = 0
            for column in columns:
                if column["name"] == "is_delete":
                    continue
                else:
                    column_type, property_name = get_column_type_property_name(column)

                    if column["nullable"]:
                        column_type += "?"
                    if column["default"]:
                        column_type += " = " + column["default"]
                    if column["name"] == "id":
                        column_type += " = 0"
                    line_text = '    @Schema(description = "%s")\n' % (column["comment"])

                    # 特殊处理 for Payment.kt
                    if file_info["model_name"] == "payment" and (
                        property_name == "status"
                        or property_name == "wx_transaction_id"
                        or property_name == "wx_payment_open_id"
                        or property_name == "message"
                        or property_name == "payment_time"
                    ):
                        line_text += "    var %s: %s" % (
                            inflection.camelize(property_name, False),
                            column_type,
                        )

                    # 特殊处理 for User.kt
                    elif file_info["model_name"] == "user" and (
                        property_name != "id" and property_name != "create_time" and property_name != "update_time"
                    ):
                        line_text += "    var %s: %s" % (inflection.camelize(property_name, False), column_type)
                    else:
                        line_text += "    val %s: %s" % (inflection.camelize(property_name, False), column_type)
                    lines.append(line_text)
                    swagger_index += 1
            content += "%s,\n" % (",\n\n".join(lines))
            content += ")\n"

            output_models_path = os.path.join(kotlin_output_path, "models")
            if not os.path.exists(output_models_path):
                os.makedirs(output_models_path)
            file_write = open(
                os.path.join(output_models_path, file_info["model_name_pascal_case"] + ".kt"), "w", encoding="UTF-8"
            )
            file_write.write(content)
            file_write.close()

            # [Model]EditRequest.kt
            content = "package %s.viewmodels.%s\n\n" % (
                project_info["package_name"],
                file_info["model_name_camel_case"],
            )
            content += "import io.swagger.v3.oas.annotations.media.Schema\nimport java.math.BigDecimal\nimport java.util.*\nimport jakarta.validation.constraints.NotNull\n\n"
            content += "data class %sEditRequest(\n" % (file_info["model_name_pascal_case"])
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
                if (
                    column["name"] == "create_time"
                    or column["name"] == "update_time"
                    or column["name"] == "created_time"
                    or column["name"] == "updated_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                if column["name"] == "id":
                    column_type += " = 0"
                if required:
                    line_text += '    @NotNull(message = "%s 不能为空")\n' % (inflection.camelize(property_name, False))
                line_text += '    @Schema(description = "%s", required = %s, hidden = %s)\n' % (
                    column["comment"],
                    "true" if required else "false",
                    "true" if hidden else "false",
                )
                line_text += "    %s %s: %s" % (define, inflection.camelize(property_name, False), column_type)
                lines.append(line_text)
                swagger_index += 1
            content += "%s,\n" % (",\n\n".join(lines))
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
            content = "package %s.viewmodels.%s\n\n" % (
                project_info["package_name"],
                file_info["model_name_camel_case"],
            )
            content += "import io.swagger.v3.oas.annotations.media.Schema\nimport java.math.BigDecimal\nimport java.util.*\nimport jakarta.validation.constraints.NotNull\n\n"
            content += "data class %sPartlyEditRequest(\n" % (file_info["model_name_pascal_case"])
            lines = []
            swagger_index = 0
            for column in columns:
                define = "var"
                hidden = "true"
                if (
                    column["name"] == "create_time"
                    or column["name"] == "update_time"
                    or column["name"] == "created_time"
                    or column["name"] == "updated_time"
                    or column["name"] == "is_delete"
                ):
                    continue
                column_type, property_name = get_column_type_property_name(column)
                if column["name"] != "id":
                    define = "val"
                    hidden = "false"
                    column_type += "? = null"
                # 特殊处理 for UserPartlyEditRequest.kt
                if file_info["model_name"] == "user" and (column["name"] == "password" or column["name"] == "salt"):
                    define = "var"
                line_text = '    @Schema(description = "%s", required = false, hidden = %s)\n' % (
                    column["comment"],
                    hidden,
                )
                line_text += "    %s %s: %s" % (define, inflection.camelize(property_name, False), column_type)
                lines.append(line_text)
                swagger_index += 1
            content += "%s,\n" % (",\n\n".join(lines))
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
            content = "package %s.viewmodels.%s\n\n" % (
                project_info["package_name"],
                file_info["model_name_camel_case"],
            )
            content += (
                "import io.swagger.v3.oas.annotations.media.Schema\nimport %s.models.Paging\nimport %s.viewmodels.common.SortOrder\nimport java.math.BigDecimal\nimport java.util.*\n\n"
                % (project_info["package_name"], project_info["package_name"])
            )
            content += "data class %sSearchRequest(\n" % (file_info["model_name_pascal_case"])
            lines = []
            swagger_index = 0
            for column in columns:
                if column["name"] == "id" or column["name"] == "sort_weight" or column["name"] == "is_delete":
                    continue
                column_type, property_name = get_column_type_property_name(column)
                column_type += "? = null"
                define = "val"
                if column["type"] == "datetime" or column["type"] == "time" or column["type"] == "date":
                    line_text = '    @Schema(description = "%s From")\n' % (column["comment"])
                    line_text += "    %s %s: %s" % (
                        define,
                        inflection.camelize(column["name"] + "From", False),
                        column_type,
                    )
                    lines.append(line_text)
                    swagger_index += 1
                    line_text = '    @Schema(description = "%s To")\n' % (column["comment"])
                    line_text += "    %s %s: %s" % (
                        define,
                        inflection.camelize(column["name"] + "To", False),
                        column_type,
                    )
                    lines.append(line_text)
                    swagger_index += 1
                    continue

                line_text = '    @Schema(description = "%s")\n' % (column["comment"])
                line_text += "    %s %s: %s" % (
                    define,
                    inflection.camelize(property_name, False),
                    column_type,
                )
                lines.append(line_text)
                swagger_index += 1

            line_text = '    @Schema(description = "排序条件")\n'
            line_text += "    val sortOrders: List<SortOrder>? = null"
            lines.append(line_text)
            swagger_index += 1

            line_text = '    @Schema(description = "分页(默认第1页，每页显示10条)")\n'
            line_text += "    val paging: Paging = Paging(1, 10)"
            lines.append(line_text)
            content += "%s,\n" % (",\n\n".join(lines))
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
                bind_mobile_columns_data = []
                for column in columns:
                    property_name = column["name"]
                    if (
                        column["name"] == "create_time"
                        or column["name"] == "update_time"
                        or column["name"] == "created_time"
                        or column["name"] == "updated_time"
                        or column["name"] == "is_delete"
                    ):
                        continue
                    columns_data.append(
                        "            %s = request.%s"
                        % (
                            inflection.camelize(property_name, False),
                            inflection.camelize(property_name, False),
                        )
                    )

                    if column["name"] != "password" and column["name"] != "salt":
                        add_user_with_password_columns_data.append(
                            "            %s = request.%s"
                            % (
                                inflection.camelize(property_name, False),
                                inflection.camelize(property_name, False),
                            )
                        )
                    else:
                        add_user_with_password_columns_data.append(
                            "            %s = %s"
                            % (
                                inflection.camelize(property_name, False),
                                inflection.camelize(property_name, False),
                            )
                        )
                    if column["name"] != "mobile" and column["name"] != "id":
                        bind_mobile_columns_data.append(
                            "            mobileUser.%s = currentUser.%s"
                            % (
                                inflection.camelize(property_name, False),
                                inflection.camelize(property_name, False),
                            )
                        )

                content = substitute(
                    file_read.read(),
                    columns_data=",\n".join(columns_data),
                    add_user_with_password_columns_data=",\n".join(add_user_with_password_columns_data),
                    bind_mobile_columns_data="\n".join(bind_mobile_columns_data),
                )
            else:
                file_read = open(os.path.join(TEMPLATE_PATH, "Service.kt"), "r", encoding="UTF-8")

                columns_data = []
                for column in columns:
                    property_name = column["name"]
                    if (
                        column["name"] == "create_time"
                        or column["name"] == "update_time"
                        or column["name"] == "created_time"
                        or column["name"] == "updated_time"
                        or column["name"] == "is_delete"
                    ):
                        continue
                    columns_data.append(
                        "            %s = request.%s"
                        % (
                            inflection.camelize(property_name, False),
                            inflection.camelize(property_name, False),
                        )
                    )
                content = substitute(file_read.read(), columns_data=",\n".join(columns_data))

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


if __name__ == "__main__":
    OPTS, ARGS = getopt.getopt(
        sys.argv[1:],
        "",
        [
            "group_id=",
            "artifact_id=",
            "version=",
            "port=",
            "package_name=",
            "project_path=",
            "description=",
            "payment",
            "debug",
        ],
    )
    for name, value in OPTS:
        if name == "--group_id":
            project_info["group_id"] = value
        elif name == "--artifact_id":
            project_info["artifact_id"] = value
        elif name == "--version":
            project_info["version"] = value
        elif name == "--port":
            project_info["port"] = value
        elif name == "--package_name":
            project_info["package_name"] = value
        elif name == "--project_path":
            project_info["project_path"] = value
        elif name == "--description":
            project_info["description"] = value
        elif name == "--payment":
            project_info["payment"] = True
        elif name == "--debug":
            project_info["debug"] = True
    if project_info["debug"]:
        print("debug mode")
        print(project_info)
    if not os.path.exists(project_info["project_path"]):
        os.makedirs(project_info["project_path"])

    if not os.path.exists(os.path.join(project_info["project_path"], "pom.xml")):
        # 初始化项目（复制 .sql 文件）
        init_project()
    run_package()
    copy_archetype_resources()
    print("generator 执行完成")

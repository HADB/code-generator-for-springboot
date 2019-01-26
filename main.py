# coding=utf-8
"""
crud-code-generator-for-springboot
"""
import os
import sys
import getopt
import inflection
import string
import shutil

CURRENT_PATH = os.getcwd()  #当前路径
INPUT_PATH = os.path.join(CURRENT_PATH, 'inputs')  #输入路径
TEMPLATE_PATH = os.path.join(CURRENT_PATH, 'templates')  #模板路径
PACKAGE_NAME = 'demo.package.name'

opts, args = getopt.getopt(sys.argv[1:], 'p:')
for name, value in opts:
    if name == '-p':
        print(name, value)
        PACKAGE_NAME = value

OUTPUT_PATH = os.path.join(CURRENT_PATH, 'outputs')  #输出路径
shutil.rmtree(OUTPUT_PATH)
KOTLIN_OUTPUT_PATH = os.path.join(OUTPUT_PATH, 'main', 'kotlin', *PACKAGE_NAME.split('.'))
MYBATIS_OUTPUT_PATH = os.path.join(OUTPUT_PATH, 'main', 'resources', 'mybatis')

for input_file_name in os.listdir(INPUT_PATH):
    input_file_path = os.path.join(INPUT_PATH, input_file_name)
    if not os.path.isdir(input_file_path):
        file_name = os.path.splitext(input_file_name)[0].strip()
        table_name = file_name[2:]
        file_read = open(input_file_path, 'r')
        columns = []
        for line in file_read:
            column = {}
            column['name'] = line.strip().split()[0].strip('`')
            column['type'] = line.strip().split()[1].split('(')[0].lower()
            if line.find('NOT NULL') > 0:
                column['nullable'] = False
            else:
                column['nullable'] = True

            if line.find('DEFAULT') > 0:
                column['default'] = line[line.find('DEFAULT') + 8:].split()[0]
            else:
                column['default'] = None

            column['comment'] = line[line.find('COMMENT') + 8:].split('\'')[1]
            columns.append(column)
        file_read.close()

        # [Model]Mapper.xml
        lines = []
        for column in columns:
            property_name = column['name']
            if column['name'] == 'is_delete':
                continue
            if column['name'].startswith('is_'):
                property_name = column['name'][3:]

            lines.append('        `%s`.`%s` as `%s`' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(property_name, False)))
        column_list = ',\n'.join(lines)

        lines = []
        for column in columns:
            property_name = column['name']
            if column['name'] == 'id' or column['name'] == 'is_delete':
                continue
            if column['name'].startswith('is_'):
                property_name = column['name'][3:]

            lines.append('        `%s`' % (column['name']))
        name_list = ',\n'.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'sort_weight':
                continue
            if column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
                lines.append('        <if test="request.%sFrom != null">' % (inflection.camelize(column['name'], False)))
                lines.append('            AND `%s`.`%s` &gt;= #{request.%sFrom}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'], False)))
                lines.append('        </if>')
                lines.append('        <if test="request.%sTo != null">' % (inflection.camelize(column['name'], False)))
                lines.append('            AND `%s`.`%s` &lt;= #{request.%sTo}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'], False)))
                lines.append('        </if>')
                continue
            if column['name'] == 'is_delete':
                lines.append('        AND `%s`.`%s` = 0' % (inflection.camelize(table_name, False), column['name']))
                continue
            if column['type'] == 'varchar' or column['type'] == 'text':
                lines.append('        <if test="request.%s != null and request.%s !=\'\'">' % (inflection.camelize(column['name'], False), inflection.camelize(column['name'], False)))
            elif column['name'].startswith('is_'):
                lines.append('        <if test="request.%s != null">' % (inflection.camelize(column['name'][3:], False)))                
            else:
                lines.append('        <if test="request.%s != null">' % (inflection.camelize(column['name'], False)))
            if column['name'].startswith('is_'):
                lines.append('            AND `%s`.`%s` = #{request.%s}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'][3:], False)))
            else:
                lines.append('            AND `%s`.`%s` = #{request.%s}' % (inflection.camelize(table_name, False), column['name'], inflection.camelize(column['name'], False)))
            lines.append('        </if>')
        search_where = '\n'.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'sort_weight':
                lines.append('`%s`.`sort_weight` DESC' % (inflection.camelize(table_name, False)))
        if (not lines):
            lines.append('`%s`.`id` ASC' % (inflection.camelize(table_name, False)))
        orders = ', '.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'is_delete':
                continue
            elif column['name'] == 'create_time' or column['name'] == 'update_time':
                lines.append('        NOW()')
            elif column['name'].startswith('is_'):
                lines.append('        #{%s.%s}' % (inflection.camelize(table_name, False), inflection.camelize(column['name'][3:], False)))
            else:
                lines.append('        #{%s.%s}' % (inflection.camelize(table_name, False), inflection.camelize(column['name'], False)))
        value_list = ',\n'.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'create_time' or column['name'] == 'is_delete':
                continue
            elif column['name'] == 'update_time':
                lines.append('        `update_time` = NOW()')
            elif column['name'].startswith('is_'):
                lines.append('        `%s` = #{%s.%s}' % (column['name'], inflection.camelize(table_name, False), inflection.camelize(column['name'][3:], False)))
            else:
                lines.append('        `%s` = #{%s.%s}' % (column['name'], inflection.camelize(table_name, False), inflection.camelize(column['name'], False)))
        update_list = ',\n'.join(lines)

        file_read = open(os.path.join(TEMPLATE_PATH, 'Mapper.xml'), 'r')
        content = file_read.read()
        t = string.Template(content)
        content = t.substitute(
            table_name='t_' + table_name,
            package_name=PACKAGE_NAME,
            model_upper_camelcase=inflection.camelize(table_name),
            model_camelcase=inflection.camelize(table_name, False),
            column_list=column_list,
            search_where=search_where,
            orders=orders,
            name_list=name_list,
            value_list=value_list,
            update_list=update_list)

        output_mybatis_path = os.path.join(MYBATIS_OUTPUT_PATH, 'mapper')
        if not os.path.exists(output_mybatis_path):
            os.makedirs(output_mybatis_path)
        file_write = open(os.path.join(output_mybatis_path, inflection.camelize(table_name) + 'Mapper.xml'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model].kt
        content = ''
        content += 'package %s.models\n\n' % (PACKAGE_NAME)
        content += 'import io.swagger.annotations.ApiModelProperty\n'
        content += 'import %s.annotations.NoArg\n' % (PACKAGE_NAME)
        content += 'import java.util.*\n\n'
        content += '@NoArg\n'
        content += 'data class %s(\n' % (inflection.camelize(table_name))
        lines = []
        swagger_index = 0
        for column in columns:
            lineText = ''
            if column['name'] == 'is_delete':
                continue
            else:
                type = 'String'
                property_name = column['name']
                if column['type'] == 'bigint':
                    type = 'Long'
                elif column['type'] == 'tinyint' and column['name'].startswith('is_'):
                    type = 'Boolean'
                    property_name = column['name'][3:]
                elif column['type'] == 'tinyint' or column['type'] == 'int':
                    type = 'Int'
                elif column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
                    type = 'Date'

                if column['nullable']:
                    type += '?'
                if column['name'] == 'create_time' or column['name'] == 'update_time':
                    type += ' = null'
                if column['name'] == 'id' or column['name'] == 'is_delete':
                    type += ' = 0'
                lineText += '        @ApiModelProperty(position = %s, notes = "%s")\n' % (swagger_index, column['comment'])
                lineText += '        val %s: %s' % (inflection.camelize(property_name, False), type)
                lines.append(lineText)
                swagger_index += 1
        content += '%s\n' % (',\n\n'.join(lines))
        content += ')\n'

        output_models_path = os.path.join(KOTLIN_OUTPUT_PATH, 'models')
        if not os.path.exists(output_models_path):
            os.makedirs(output_models_path)
        file_write = open(os.path.join(output_models_path, inflection.camelize(table_name) + '.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]EditRequest.kt
        content = ''
        content += 'package %s.viewmodels.%s\n\n' % (PACKAGE_NAME, inflection.camelize(table_name, False))
        content += 'import io.swagger.annotations.ApiModelProperty\nimport java.util.*\nimport javax.validation.constraints.NotNull\n\n'
        content += 'data class %sEditRequest(\n' % (inflection.camelize(table_name))
        lines = []
        swagger_index = 0
        for column in columns:
            type = 'String'
            property_name = column['name']
            define = 'val'
            required = True
            hidden = False
            lineText = ''
            if column['type'] == 'bigint':
                type = 'Long'
            elif column['type'] == 'tinyint' and column['name'].startswith('is_'):
                type = 'Boolean'
                property_name = column['name'][3:]
            elif column['type'] == 'tinyint' or column['type'] == 'int':
                type = 'Int'
            elif column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
                type = 'Date'

            if column['nullable']:
                type += '?'
                required = False
            if column['name'] == 'id':
                define = 'var'
                required = False
                hidden = True
            if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'is_delete':
                continue
            if column['name'] == 'id':
                type += ' = 0'
            if required:
                lineText += '        @NotNull(message = "%s 不能为空")\n' % (inflection.camelize(property_name, False))
            lineText += '        @ApiModelProperty(position = %s, notes = "%s", required = %s, hidden = %s)\n' % (swagger_index, column['comment'], 'true' if required else 'false', 'true'
                                                                                                                  if hidden else 'false')
            lineText += '        %s %s: %s' % (define, inflection.camelize(property_name, False), type)
            lines.append(lineText)
            swagger_index += 1
        content += '%s\n' % (',\n\n'.join(lines))
        content += ')\n'

        output_viewmodels_path = os.path.join(KOTLIN_OUTPUT_PATH, 'viewmodels', inflection.camelize(table_name, False))
        if not os.path.exists(output_viewmodels_path):
            os.makedirs(output_viewmodels_path)
        file_write = open(os.path.join(output_viewmodels_path, inflection.camelize(table_name) + 'EditRequest.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]SearchRequest.kt
        content = ''
        content += 'package %s.viewmodels.%s\n\n' % (PACKAGE_NAME, inflection.camelize(table_name, False))
        content += 'import io.swagger.annotations.ApiModelProperty\nimport %s.models.Paging\nimport java.util.*\n\n' % (PACKAGE_NAME)
        content += 'data class %sSearchRequest(\n' % (inflection.camelize(table_name))
        lines = []
        swagger_index = 0
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'sort_weight' or column['name'] == 'is_delete':
                continue
            type = 'String?'
            property_name = column['name']
            define = 'val'
            required = True
            hidden = False
            lineText = ''
            if column['type'] == 'bigint':
                type = 'Long?'
            elif column['type'] == 'tinyint' and column['name'].startswith('is_'):
                type = 'Boolean?'
                property_name = column['name'][3:]
            elif column['type'] == 'tinyint' or column['type'] == 'int':
                type = 'Int?'
            elif column['type'] == 'datetime' or column['type'] == 'time' or column['type'] == 'date':
                type = 'Date?'
                lineText = '        @ApiModelProperty(position = %s, notes = "%s From")\n' % (swagger_index, column['comment'])
                lineText += '        %s %s: %s' % (define, inflection.camelize(column['name'] + "From", False), type)
                lines.append(lineText)
                swagger_index += 1
                lineText = '        @ApiModelProperty(position = %s, notes = "%s To")\n' % (swagger_index, column['comment'])
                lineText += '        %s %s: %s' % (define, inflection.camelize(column['name'] + "To", False), type)
                lines.append(lineText)
                swagger_index += 1
                continue

            lineText += '        @ApiModelProperty(position = %s, notes = "%s")\n' % (swagger_index, column['comment'])
            lineText += '        %s %s: %s' % (define, inflection.camelize(property_name, False), type)
            lines.append(lineText)
            swagger_index += 1
        lineText = '        @ApiModelProperty(position = %s, notes = "分页(默认第1页，每页显示10条)")\n' % (swagger_index)
        lineText += ('        val paging: Paging = Paging(1,10)')
        lines.append(lineText)
        content += '%s\n' % (',\n\n'.join(lines))
        content += ')\n'

        output_viewmodels_path = os.path.join(KOTLIN_OUTPUT_PATH, 'viewmodels', inflection.camelize(table_name, False))
        if not os.path.exists(output_viewmodels_path):
            os.makedirs(output_viewmodels_path)
        file_write = open(os.path.join(output_viewmodels_path, inflection.camelize(table_name) + 'SearchRequest.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]Mapper.kt
        file_read = open(os.path.join(TEMPLATE_PATH, 'Mapper.kt'), 'r')
        content = file_read.read()
        t = string.Template(content)
        content = t.substitute(package_name=PACKAGE_NAME, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False))

        output_mappers_path = os.path.join(KOTLIN_OUTPUT_PATH, 'mappers')
        if not os.path.exists(output_mappers_path):
            os.makedirs(output_mappers_path)
        file_write = open(os.path.join(output_mappers_path, inflection.camelize(table_name) + 'Mapper.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]Service.kt
        file_read = open(os.path.join(TEMPLATE_PATH, 'Service.kt'), 'r')
        content = file_read.read()
        t = string.Template(content)
        columns_data = []
        for column in columns:
            property_name = column['name']
            if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'is_delete':
                continue
            if column['name'].startswith('is_'):
                property_name = column['name'][3:]
            columns_data.append('                %s = request.%s' % (inflection.camelize(property_name, False), inflection.camelize(property_name, False)))
        content = t.substitute(
            package_name=PACKAGE_NAME, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False), columns_data=',\n'.join(columns_data))

        output_services_path = os.path.join(KOTLIN_OUTPUT_PATH, 'services')
        if not os.path.exists(output_services_path):
            os.makedirs(output_services_path)
        file_write = open(os.path.join(output_services_path, inflection.camelize(table_name) + 'Service.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]Controller.kt
        file_read = open(os.path.join(TEMPLATE_PATH, 'Controller.kt'), 'r')
        content = file_read.read()
        t = string.Template(content)
        content = t.substitute(
            package_name=PACKAGE_NAME, model_dasherize=inflection.dasherize(table_name), model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False))

        output_controllers_path = os.path.join(KOTLIN_OUTPUT_PATH, 'controllers')
        if not os.path.exists(output_controllers_path):
            os.makedirs(output_controllers_path)
        file_write = open(os.path.join(output_controllers_path, inflection.camelize(table_name) + 'Controller.kt'), 'w')
        file_write.write(content)
        file_write.close()
# coding=utf-8
"""
crud-code-generator-for-springboot
"""
import os
import inflection
import string

CURRENT_PATH = os.getcwd()  #当前路径
INPUT_PATH = os.path.join(CURRENT_PATH, 'inputs')  #输入路径
OUTPUT_PATH = os.path.join(CURRENT_PATH, 'outputs')  #输出路径
TEMPLATE_PATH = os.path.join(CURRENT_PATH, 'templates')  #模板路径
PACKAGE_NAME = 'run.monkey.op.xiaotong'

for input_file_name in os.listdir(INPUT_PATH):
    input_file_path = os.path.join(INPUT_PATH, input_file_name)
    if not os.path.isdir(input_file_path):
        file_name = os.path.splitext(input_file_name)[0].strip()
        table_name = file_name[2:]
        output_file_path = os.path.join(OUTPUT_PATH, table_name)
        if not os.path.exists(output_file_path):
            os.makedirs(output_file_path)
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
            lines.append('        `%s` as `%s`' % (column['name'], inflection.camelize(column['name'], False)))
        as_list = ',\n'.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'is_delete':
                continue
            else:
                lines.append('        `%s`' % (column['name']))
        name_list = ',\n'.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'is_delete':
                continue
            elif column['name'] == 'create_time' or column['name'] == 'update_time':
                lines.append('        NOW()')
            else:
                lines.append('        #{%s.%s}' % (inflection.camelize(table_name, False), inflection.camelize(column['name'], False)))
        value_list = ',\n'.join(lines)

        lines = []
        for column in columns:
            if column['name'] == 'id' or column['name'] == 'create_time' or column['name'] == 'is_delete':
                continue
            elif column['name'] == 'update_time':
                lines.append('        `update_time` = NOW()')
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
            as_list=as_list,
            name_list=name_list,
            value_list=value_list,
            update_list=update_list)
        file_write = open(os.path.join(output_file_path, inflection.camelize(table_name) + 'Mapper.xml'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model].kt
        content = ''
        content += 'package %s.models\n\n' % (PACKAGE_NAME)
        content += 'import %s.annotations.NoArg\n' % (PACKAGE_NAME)
        content += 'import java.util.*\n\n'
        content += '@NoArg\n'
        content += 'data class %s(\n' % (inflection.camelize(table_name))
        lines = []
        for column in columns:
            type = 'String'
            if column['type'] == 'bigint':
                type = 'Long'
            elif column['type'] == 'tinyint' or column['type'] == 'int':
                type = 'Int'
            elif column['type'] == 'datetime' or column['type'] == 'time':
                type = 'Date'

            if column['nullable']:
                type += '?'
            if column['name'] == 'create_time' or column['name'] == 'update_time':
                type += ' = Date(0)'
            if column['name'] == 'id' or column['name'] == 'is_delete':
                type += ' = 0'
            lines.append('        val %s: %s' % (inflection.camelize(column['name'], False), type))
        max_length = len(max(lines, key=len))
        for index, line in enumerate(lines):
            if index < len(lines) - 1:
                lines[index] += ','
            lines[index] = lines[index].ljust(max_length + 5)
        for index, column in enumerate(columns):
            lines[index] += '// ' + column['comment']
        content += '%s\n' % ('\n'.join(lines))
        content += ')\n'

        file_write = open(os.path.join(output_file_path, inflection.camelize(table_name) + '.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]EditRequest.kt
        content = ''
        content += 'package %s.viewmodels.%s\n\n' % (PACKAGE_NAME, inflection.camelize(table_name, False))
        content += 'import java.util.*\n\n'
        content += 'data class %sEditRequest(\n' % (inflection.camelize(table_name))
        lines = []
        for column in columns:
            type = 'String'
            define = 'val'
            if column['type'] == 'bigint':
                type = 'Long'
            elif column['type'] == 'tinyint' or column['type'] == 'int':
                type = 'Int'
            elif column['type'] == 'datetime' or column['type'] == 'time':
                type = 'Date'

            if column['nullable']:
                type += '?'
            if column['name'] == 'id':
                type += ' = 0'
                define = 'var'
            if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'is_delete':
                continue
            lines.append('        %s %s: %s' % (define, inflection.camelize(column['name'], False), type))
        max_length = len(max(lines, key=len))
        for index, line in enumerate(lines):
            if index < len(lines) - 1:
                lines[index] += ','
            lines[index] = lines[index].ljust(max_length + 5)
        i = 0
        for column in columns:
            if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'is_delete':
                continue
            lines[i] += '// ' + column['comment']
            i += 1
        content += '%s\n' % ('\n'.join(lines))
        content += ')\n'

        file_write = open(os.path.join(output_file_path, inflection.camelize(table_name) + 'EditRequest.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]Mapper.kt
        file_read = open(os.path.join(TEMPLATE_PATH, 'Mapper.kt'), 'r')
        content = file_read.read()
        t = string.Template(content)
        content = t.substitute(package_name=PACKAGE_NAME, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False))
        file_write = open(os.path.join(output_file_path, inflection.camelize(table_name) + 'Mapper.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]Service.kt
        file_read = open(os.path.join(TEMPLATE_PATH, 'Service.kt'), 'r')
        content = file_read.read()
        t = string.Template(content)
        columns_data = []
        for column in columns:
            if column['name'] == 'create_time' or column['name'] == 'update_time' or column['name'] == 'is_delete':
                continue
            columns_data.append('                %s = request.%s' % (inflection.camelize(column['name'], False), inflection.camelize(column['name'], False)))
        content = t.substitute(
            package_name=PACKAGE_NAME, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False), columns_data=',\n'.join(columns_data))
        file_write = open(os.path.join(output_file_path, inflection.camelize(table_name) + 'Service.kt'), 'w')
        file_write.write(content)
        file_write.close()

        # [Model]Controller.kt
        file_read = open(os.path.join(TEMPLATE_PATH, 'Controller.kt'), 'r')
        content = file_read.read()
        t = string.Template(content)
        content = t.substitute(package_name=PACKAGE_NAME, model_upper_camelcase=inflection.camelize(table_name), model_camelcase=inflection.camelize(table_name, False))
        file_write = open(os.path.join(output_file_path, inflection.camelize(table_name) + 'Controller.kt'), 'w')
        file_write.write(content)
        file_write.close()
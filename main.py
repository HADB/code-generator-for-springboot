# coding=utf-8
"""
生成Mybatis基础的增删改查SQL
"""
import os
import inflection

CURRENT_PATH = os.getcwd()  #当前路径
INPUT_PATH = os.path.join(CURRENT_PATH, 'inputs')  #输入路径
OUTPUT_PATH = os.path.join(CURRENT_PATH, 'outputs')  #输出路径
INPUT_FILES = os.listdir(INPUT_PATH)  #输入文件

for inputFileName in INPUT_FILES:
    inputFilePath = os.path.join(INPUT_PATH, inputFileName)
    if not os.path.isdir(inputFilePath):
        fileName = os.path.splitext(inputFileName)[0].strip()
        tableName = fileName[2:]
        outputFilePath = os.path.join(OUTPUT_PATH, tableName)
        if not os.path.exists(outputFilePath):
            os.makedirs(outputFilePath)
        fileRead = open(inputFilePath, 'r')
        columns = []
        for line in fileRead:
            column = line.strip().split()[0].strip('`')
            columns.append(column)
            print(column)

        fileWrite = open(os.path.join(outputFilePath, inflection.camelize(tableName) + 'Mapper.xml'), 'w')

        # columns
        fileWrite.write('<sql id="%sColumns">\n' % (tableName))
        lines = []
        for column in columns:
            lines.append('    `%s` as `%s`' % (column, inflection.camelize(column, False)))
        fileWrite.write('%s\n' % (',\n'.join(lines)))
        fileWrite.write('</sql>')
        fileWrite.close()

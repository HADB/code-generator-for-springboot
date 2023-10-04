#!/bin/zsh

cd "${project_path}"

# 获取当前分支的名称
current_branch=$$(git symbolic-ref --short HEAD)

# 检查是否在 generator 分支
if [ "$$current_branch" != "generator" ]; then
    echo "当前不在 generator 分支，请切换到 generator 分支"
    exit 1
fi

# 检查是否存在 CODE_GENERATOR_PATH 环境变量
if [ -z "$$CODE_GENERATOR_PATH" ]; then
    echo "CODE_GENERATOR_PATH 环境变量不存在，请设置 CODE_GENERATOR_PATH 环境变量"
    exit 1
fi

# 进入 code generator 目录
cd "$$CODE_GENERATOR_PATH"

# 运行 main.py 并传递参数
python3 main.py \
--group_id=${group_id} \
--artifact_id=${artifact_id} \
--version=${version} \
--port=${port} \
--package_name=${package_name} \
--project_path=${project_path} \
--description=${description} \
--registry_instance=${registry_instance} \
--registry_namespace=${registry_namespace} \
--registry_username=${registry_username} \
--registry_password=${registry_password}

# 进入项目目录
cd "${project_path}"

git add .

git commit -m "gen: auto commit"

echo "执行完成"
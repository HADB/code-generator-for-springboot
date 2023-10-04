#!/bin/zsh

cd "${project_path}"

# 获取当前分支的名称
current_branch=$$(git symbolic-ref --short HEAD)


# 检查是否在 generator 分支
if [ "$$current_branch" != "generator" ]; then
    # 如果不在 generator 分支且当前分支不干净
    if ! git diff --quiet; then
        echo "当前分支不干净，请先提交您的更改或者清空未提交的更改"
        exit 1
    else
        # 切换到 generator 分支
        git checkout generator
        if [ $$? -ne 0 ]; then
            echo "切换到 generator 分支时出错"
            exit 1
        else
            echo "已切换到 generator 分支"
        fi
    fi
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
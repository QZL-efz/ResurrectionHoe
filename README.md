# 🌾 ResurrectionHoe

> 我是新手，这是我第一次自己尝试开发插件，部分代码借助了人工智能完成，还请高手多多指教QAQ

这是一个用于 Minecraft Paper 服务器的插件，添加了两件新物品：“复生之锄”和“金坷垃”。

本插件仅用于娱乐，添加的物品无法在无权限下获取，可能需要通过管理员发放或另外加自定义配方的插件实现获取。

## ✨ 新物品

### ⛏️ 复生之锄

- **形态**: 金锄
- **附魔**: 时运 III （破坏作物时可以有所加成）
- **特性**: 右键地面可将 5×3×5 范围内的泥土/草方块/土径/灵魂沙/灵魂土转化为湿润耕地
- **特殊效果**: 且可以在下界生成湿润的耕地，且生成的耕地不会自动干涸（防干涸默认开启，可在配置文件中关闭）

### 🪴 金坷垃

- **形态**: 骨粉
- **附魔**: 效率 III（该附魔只是装饰作用）
- **特性**: 右键作物可催熟 5×3×5 范围内的所有作物
- **消耗**: 使用后消耗 1 个

## 🎮 指令

| 指令               | 权限   | 说明     |
| ---------------- | ---- | ------ |
| `/rh`            | 所有玩家 | 显示插件信息 |
| `/rh info`       | 所有玩家 | 显示插件信息 |
| `/rh reload`     | OP   | 重载插件   |
| `/rh hoe`        | OP   | 获取复生之锄 |
| `/rh fertilizer` | OP   | 获取金坷垃  |

## 🛠️ 构建

```bash
mvn clean package
```

生成的 JAR 文件位于 `target/ResurrectionHoe-<version>.jar`

## 📥 安装

1. 将 JAR 文件放入 Paper 服务器的 `plugins/` 目录
2. 重启服务器

## 📋 权限节点

| 权限                           | 默认   | 说明                |
| ---------------------------- | ---- | ----------------- |
| `resurrectionhoe.info`       | true | 使用 /rh info       |
| `resurrectionhoe.reload`     | op   | 使用 /rh reload     |
| `resurrectionhoe.hoe`        | op   | 使用 /rh hoe        |
| `resurrectionhoe.fertilizer` | op   | 使用 /rh fertilizer |

## 📝 支持的游戏版本

- Minecraft 1.21.x

## ⚙️ 配置

插件首次加载后，会在 `plugins/ResurrectionHoe/config.yml` 生成配置文件：

```yaml
# 是否启用耕地不干涸功能
# true - 启用，复生之锄生成的耕地不会干涸
# false - 禁用，所有耕地行为与普通耕地一致
enable-farmland-moisture: true
```

### 配置说明

| 配置项                        | 类型      | 默认值    | 说明          |
| -------------------------- | ------- | ------ | ----------- |
| `enable-farmland-moisture` | boolean | `true` | 是否启用耕地不干涸特性 |

**配置生效方式：**

- 修改配置后使用 `/rh reload` 重载插件
- 重启服务器也会自动生效

## 📄 许可证

[MIT License](LICENSE)

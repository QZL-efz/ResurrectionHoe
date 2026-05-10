# 🌾 ResurrectionHoe

> 我是新手，这是我第一次自己尝试开发插件，部分代码借助了人工智能完成，还请高手多多指教QAQ
>
> 项目名字叫复生之锄，因为它是我第一个添加的物品

这是一个用于 Minecraft Paper 服务器的插件，添加了三件新物品：“复生之锄”、“金坷垃”和“引路之锹”。

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
- **特性**: 右键使用可催熟 7×5×7 范围内的所有作物
- **消耗**: 成功催熟作物消耗 1 个；未成功催熟不消耗但 1 秒内无法再次使用

### ⛏️ 引路之锹

- **形态**: 铁铲
- **附魔**: 经验修补
- **特性**: 右键地面可将 3×3×3 范围内上方为空气的草方块/泥土/砂土/菌丝体/灰化土/缠根泥土转化为土径

## 🎮 指令

| 指令                                     | 权限   | 说明                  |
| -------------------------------------- | ---- | ------------------- |
| `/rh`                                  | 所有玩家 | 显示插件信息              |
| `/rh info`                             | 所有玩家 | 显示插件信息              |
| `/rh reload`                           | OP   | 重载插件                |
| `/rh give <hoe|fertilizer|shovel> [玩家] [数量]` | OP   | 获取物品（玩家和数量可选） |

### 指令示例

```bash
/rh give hoe                    # 给自己1个复生之锄
/rh give fertilizer 5            # 给自己5个金坷垃
/rh give shovel 3                # 给自己3个引路之锹
/rh give hoe PlayerName 10      # 给 PlayerName 10个复生之锄
/rh give fertilizer AnotherName 3  # 给 AnotherName 3个金坷垃
```

## 🛠️ 构建

```bash
mvn clean package
```

生成的 JAR 文件位于 `target/ResurrectionHoe-<version>.jar`

## 📥 安装

1. 将 JAR 文件放入 Paper 服务器的 `plugins/` 目录
2. 重启服务器

## 📋 权限节点

| 权限                       | 默认   | 说明            |
| ------------------------ | ---- | ------------- |
| `resurrectionhoe.info`   | true | 使用 /rh info   |
| `resurrectionhoe.reload` | op   | 使用 /rh reload |
| `resurrectionhoe.give`   | op   | 使用 /rh give   |

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

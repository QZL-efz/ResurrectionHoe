# 🌾 ResurrectionHoe

> 我是新手，这是我第一次自己尝试开发插件，部分代码借助了人工智能完成，还请高手多多指教QAQ
>
> 项目名字叫复生之锄，因为它是我第一个添加的物品

这是一个用于 Minecraft Paper 服务器的插件，添加了四件新物品："复生之锄"、"可再生石斧"、"引路之锹"和"寻觅之镰"。

本插件仅用于娱乐，添加的物品无法在无权限下获取，可能需要通过管理员发放或另外加自定义配方的插件实现获取。

## ✨ 新物品

### ⛏️ 复生之锄

- **形态**: 金锄
- **附魔**: 时运 III （破坏作物时可以有所加成）
- **特性**: 右键地面可将 5×3×5 范围内的泥土/草方块/土径/灵魂沙/灵魂土转化为湿润耕地；上方的花草会被直接破坏。(玩家需要同时有受影响的方块的建筑与破坏权限)
- **特殊效果**: 且可以在下界生成湿润的耕地，且生成的耕地不会自动干涸。（防干涸默认开启，可在配置文件中关闭）

### 🪓 可再生石斧

- **形态**: 石斧
- **附魔**: 效率 III
- **特性**: 右键树木时可连锁破坏整棵树，并在根部掉落对应树苗
- **限制**:
  - 只能破坏自然生成的树木或由树苗长大的树木
  - 巨型树木（2x2树干）会使斧头断裂消失
  - 5秒使用冷却时间

### 🔪 寻觅之镰

- **形态**: 石锄
- **附魔**: 经验修补
- **特性**: 破坏草及其变种时，有一定概率获得额外掉落物！
- **掉落概率**: 可在配置文件中设置，默认 0.2 (20%)
- **草变种**: 包括 Short Grass、Tall Grass、Fern、Large Fern
- **战利品表**: 可在配置文件中自定义，格式为 `[物品类型] [物品数量] [权重]`，权重越高越容易被选中

### ⛏️ 引路之锹

- **形态**: 铁铲
- **附魔**: 经验修补（该附魔只是装饰作用）
- **特性**: 右键地面可将 3×3×3 范围内上方为空气或花草的草方块/泥土/砂土/菌丝体/灰化土/缠根泥土转化为土径；上方的花草会被直接破坏。(玩家需要同时有受影响的方块的建筑与破坏权限)

## 🎮 指令

| 指令                                      | 权限   | 说明            |
| --------------------------------------- | ---- | ------------- |
| `/rh`                                   | 所有玩家 | 显示插件信息        |
| `/rh info`                              | 所有玩家 | 显示插件信息        |
| `/rh reload`                            | OP   | 重载插件          |
| `/rh give <hoe\|axe\|shovel\|scythe> [玩家] [数量]` | OP   | 获取物品（玩家和数量可选） |

### 指令示例

```bash
/rh give hoe                    # 给自己1个复生之锄
/rh give axe 5                  # 给自己5个可再生石斧
/rh give shovel 3               # 给自己3个引路之锹
/rh give scythe 10              # 给自己10个寻觅之镰
/rh give hoe PlayerName 10      # 给 PlayerName 10个复生之锄
/rh give axe AnotherName 3      # 给 AnotherName 3个可再生石斧
/rh give scythe AnotherName 5   # 给 AnotherName 5个寻觅之镰
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

# 寻觅之镰配置
seekers-scythe:
  # 掉落概率 (0.0 - 1.0)，默认 0.2
  drop-chance: 0.2
  
  # 战利品表，格式：[物品类型] [物品数量] [权重]
  # 权重越高，越容易被选中
  loot-table:
    - "WHEAT_SEEDS 1 200"
    - "BEETROOT_SEEDS 1 200"
    - "CARROT 1 200"
    - "POTATO 1 200"
    - "MELON_SEEDS 1 150"
    - "PUMPKIN_SEEDS 1 150"
    - "GOLDEN_CARROT 1 10"
    - "GOLDEN_APPLE 1 5"
    - "ENCHANTED_GOLDEN_APPLE 1 1"
```

### 配置说明

| 配置项                        | 类型      | 默认值    | 说明          |
| -------------------------- | ------- | ------ | ----------- |
| `enable-farmland-moisture` | boolean | `true` | 是否启用耕地不干涸特性 |
| `seekers-scythe.drop-chance` | double | `0.2` | 寻觅之镰的掉落概率 (0.0-1.0) |
| `seekers-scythe.loot-table` | list | 见上文 | 寻觅之镰的战利品表 |

**配置生效方式：**

- 修改配置后使用 `/rh reload` 重载插件
- 重启服务器也会自动生效

## 📄 许可证

[MIT License](LICENSE)

# 🌾 ResurrectionHoe

这是我第一次尝试开发插件，借助Trae完成。
这是一个用于 Minecraft Paper 服务器的插件，提供特殊的“复生之锄”和“金坷垃”物品。

## ✨ 功能特性

### 🌱 复生之锄
- **材质**: 金锄
- **附魔**: 时运 III
- **特性**: 右键地面可将 5×3×5 范围内的泥土/草方块/土径/灵魂沙/灵魂土转化为湿润耕地
- **特殊效果**: 生成的耕地不会自动干涸

### 💎 金坷垃
- **形态**: 骨粉
- **附魔**: 效率 V
- **特性**: 右键作物可催熟 5×3×5 范围内的所有作物
- **消耗**: 使用后消耗 1 个

## 🎮 指令

| 指令 | 权限 | 说明 |
|------|------|------|
| `/rh` | 所有玩家 | 显示插件信息 |
| `/rh info` | 所有玩家 | 显示插件信息 |
| `/rh reload` | OP | 重载插件 |
| `/rh hoe` | OP | 获取复生之锄 |
| `/rh fertilizer` | OP | 获取金坷垃 |

## 🛠️ 构建

```bash
mvn clean package
```

生成的 JAR 文件位于 `target/ResurrectionHoe-<version>.jar`

## 📥 安装

1. 将 JAR 文件放入 Paper 服务器的 `plugins/` 目录
2. 重启服务器

## 📋 权限节点

| 权限 | 默认 | 说明 |
|------|------|------|
| `resurrectionhoe.info` | true | 使用 /rh info |
| `resurrectionhoe.reload` | op | 使用 /rh reload |
| `resurrectionhoe.hoe` | op | 使用 /rh hoe |
| `resurrectionhoe.fertilizer` | op | 使用 /rh fertilizer |

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

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enable-farmland-moisture` | boolean | `true` | 是否启用耕地不干涸特性 |

**配置生效方式：**
- 修改配置后使用 `/rh reload` 重载插件
- 或重启服务器

### 配置影响

| 配置值 | 复生之锄描述 | 新耕地标签 | 已有标签耕地 |
|--------|-------------|-----------|-------------|
| `true` | 显示"生成的耕地不会干涸" | 获得不干涸标签 | 保持检测 |
| `false` | 不显示该描述 | 不获得标签 | 仅破坏时删除标签 |

## 📄 许可证

[MIT License](LICENSE)

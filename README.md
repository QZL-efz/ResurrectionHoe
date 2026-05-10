# 🌾 ResurrectionHoe

一个用于 Minecraft Paper 服务器的插件，提供特殊的复生之锄和金坷垃物品。

## ✨ 功能特性

### 🌱 复生之锄
- **材质**: 金锄
- **附魔**: 时运 III
- **特性**: 右键地面可将 5×3×5 范围内的泥土/草方块/土径/灵魂沙/灵魂土转化为湿润耕地
- **特殊效果**: 生成的耕地不会自动干涸

### 💎 金坷垃
- **形态**: 骨粉
- **附魔**: 效率 V（隐藏显示）
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

## 📄 许可证

[MIT License](LICENSE)
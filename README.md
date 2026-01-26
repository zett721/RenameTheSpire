# RenameTheSpire - 卡牌重命名Mod

一个功能强大的杀戮尖塔Mod，让你可以自定义任意卡牌的名称，支持批处理规则和智能标签管理。

**版本**: v0.5  
**作者**: zett  
**依赖**: BaseMod, ModTheSpire

---

## ✨ 主要功能

-  **单卡重命名** - 自定义任意卡牌名称
-  **双语索引** - 可以用中文名或英文ID作为索引
-  **批处理规则** - 统一给所有卡添加前缀/后缀
-  **智能标签管理** - 自动处理 STRIKE 标签，让完美打击等卡牌正确识别改名后的卡
-  **优先级控制** - 单卡配置优先于批处理规则

---

## 🚀 快速开始

### 1. 安装
(在创意工坊装了就不需要了)
将 `RenameTheSpire.jar` 复制到游戏的 `mods` 文件夹：
```
xxx\SteamLibrary\steamapps\common\SlayTheSpire\mods

└── RenameTheSpire.jar  ← 新增
```

### 2. 首次运行

- 启动游戏，选择 **"Play with mods"**
- 勾选 `BaseMod` 和 `RenameTheSpire`
- 点击 **"Play"**
- Mod 会自动创建配置文件

### 3. 编辑配置

**配置文件位置**：
```
Windows: %LOCALAPPDATA%\ModTheSpire\RenameTheSpire\card_renames.json
Mac: ~/Library/Preferences/ModTheSpire/RenameTheSpire/card_renames.json
Linux: ~/.config/ModTheSpire/RenameTheSpire/card_renames.json
```

**快速打开**（Windows）：
1. 按 `Win + R`
2. 输入 `%LOCALAPPDATA%\ModTheSpire\RenameTheSpire`
3. 编辑 `card_renames.json`

---

## 📝 配置格式

### 完整配置示例
> **提示**：若不需要批处理功能，请将 `enabled` 设为 `false`。

```json
{
  "batch_rules": {
    "enabled": true,
    "prefix": "",
    "suffix": "打击"
  },
  "renames": {
    "Strike_G": "猎宝打击",
    "打击": "猎宝打击",
    "防御": "鸡煲防御"
  }
}
```

### 配置说明

#### batch_rules（批处理规则）

| 字段 | 类型 | 说明 |
|------|------|------|
| `enabled` | boolean | 是否启用批处理（true/false） |
| `prefix` | string | 前缀（加在名称前面） |
| `suffix` | string | 后缀（加在名称后面） |

#### renames（单卡重命名）

格式：`"卡牌索引": "新名称"`

**卡牌索引可以是**：
- 英文ID（如 `"Strike_R"`）- 精确匹配
- 中文名（如 `"打击"`）- 匹配所有同名卡

## 🎯 优先级规则

```
1. 单卡重命名（renames）        ← 最高优先级
2. 批处理规则（batch_rules）
3. 保持原名
```


## 🔍 如何找卡牌ID

### 方法1：从游戏的 cards.json 查找

1. 找到游戏安装目录
2.  将`desktop-1.0.jar`后缀改为zip，解压
3. 导航到 `localization/zhs/cards.json`（中文）或者 `localization/eng/cards.json`（英文）
4. JSON的Key就是卡牌ID

### 方法2：直接用中文名

---

## ⚠️ 注意事项

### 1. 修改后需要重启游戏

配置文件只在游戏启动时加载。

### 2. JSON格式规则

- 每项后面要有逗号，**最后一项不要逗号**
- 必须用双引号 `"`，不能用单引号 `'`

### 3. 中文名索引的特性

用中文名索引会匹配所有同名卡：
- `"打击": "猎宝打击"` → 所有职业的"打击"都会改

如果只想改特定职业，用英文ID：
- `"Strike_R": "猎宝打击"` → 只改战士的

## 🐛 常见问题

### Q: 配置文件在哪里？

**A**: 看游戏启动日志，会显示完整路径。或按 `Win+R` 输入 `%LOCALAPPDATA%\ModTheSpire\RenameTheSpire`

### Q: 修改后没生效？

**A**: 需要重启游戏。每次启动时才会重新加载配置。

### Q: JSON格式错误/卡在Mod加载界面怎么办？

**A**: 检查：
1. 逗号是否正确（最后一项不要逗号）
2. 引号是否正确（必须是双引号）




---

## 🔧 开发信息

- **GitHub**:[欢迎点我](https://github.com/zett721/RenameTheSpire)
- **依赖**: BaseMod, ModTheSpire
- **许可**: MIT
---

## 如何本地编译：
- 克隆本项目。
- 找到steamapps\workshop\content\646570这个文件夹，1605060445和1605833019两个文件夹下还有ModTheSpire.jar和BaseMod.jar，复制到lib文件夹。
- 从你的《杀戮尖塔》游戏安装目录 (SlayTheSpire/) 中复制 desktop-1.0.jar 到 lib 文件夹。
- 运行 mvn clean package 即可生成 JAR。
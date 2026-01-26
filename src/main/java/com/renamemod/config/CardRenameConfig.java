package com.renamemod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡牌重命名配置管理类
 * 使用纯JSON文件保存配置，格式简单易编辑
 * 支持批处理规则（前缀/后缀）和单卡重命名
 */
public class CardRenameConfig {

    private static CardRenameConfig instance;
    private HashMap<String, String> renames;
    private BatchRules batchRules;
    private Gson gson;
    private Path configFilePath;

    // 批处理规则
    public static class BatchRules {
        public boolean enabled = false;
        public String prefix = "";
        public String suffix = "";

        public BatchRules() {
        }
    }

    // 配置对象，用于JSON序列化
    private static class ConfigData {
        public BatchRules batch_rules;
        public Map<String, String> renames;

        public ConfigData() {
            this.batch_rules = new BatchRules();
            this.renames = new HashMap<>();
        }
    }

    private CardRenameConfig() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping() // 不转义中文
                .create();
        renames = new HashMap<>();
        batchRules = new BatchRules();

        // 确定配置文件路径
        initConfigPath();

        // 加载配置
        loadConfig();
    }

    /**
     * 初始化配置文件路径
     */
    private void initConfigPath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        String configDir;
        if (os.contains("win")) {
            configDir = System.getenv("LOCALAPPDATA") + "\\ModTheSpire\\RenameTheSpire";
        } else if (os.contains("mac")) {
            configDir = userHome + "/Library/Preferences/ModTheSpire/RenameTheSpire";
        } else {
            configDir = userHome + "/.config/ModTheSpire/RenameTheSpire";
        }

        configFilePath = Paths.get(configDir, "card_renames.json");

        // 确保目录存在
        try {
            Files.createDirectories(configFilePath.getParent());
        } catch (IOException e) {
            System.err.println("[RenameTheSpire] 创建配置目录失败: " + e.getMessage());
        }
    }

    /**
     * 获取单例实例
     */
    public static CardRenameConfig getInstance() {
        if (instance == null) {
            instance = new CardRenameConfig();
        }
        return instance;
    }

    /**
     * 加载配置文件
     */
    private void loadConfig() {
        try {
            System.out.println("[RenameTheSpire] 正在加载配置文件...");
            System.out.println("[RenameTheSpire] 配置文件位置: " + configFilePath.toString());

            if (Files.exists(configFilePath)) {
                // 读取JSON文件
                String json = new String(Files.readAllBytes(configFilePath), StandardCharsets.UTF_8);

                // 解析JSON
                ConfigData data = gson.fromJson(json, ConfigData.class);

                if (data != null) {
                    // 加载批处理规则
                    if (data.batch_rules != null) {
                        batchRules = data.batch_rules;
                        System.out.println("[RenameTheSpire] 批处理规则: " +
                                (batchRules.enabled ? "启用" : "禁用"));
                        if (batchRules.enabled) {
                            if (!batchRules.prefix.isEmpty()) {
                                System.out.println("  前缀: \"" + batchRules.prefix + "\"");
                            }
                            if (!batchRules.suffix.isEmpty()) {
                                System.out.println("  后缀: \"" + batchRules.suffix + "\"");
                            }
                        }
                    }

                    // 加载单卡重命名
                    if (data.renames != null) {
                        renames = new HashMap<>(data.renames);
                        System.out.println("[RenameTheSpire] 配置加载成功！已加载 " +
                                renames.size() + " 个单卡重命名配置");

                        // 打印所有配置（用于调试）
                        if (!renames.isEmpty()) {
                            System.out.println("[RenameTheSpire] 单卡重命名配置：");
                            for (Map.Entry<String, String> entry : renames.entrySet()) {
                                System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
                            }
                        }
                    }
                } else {
                    System.out.println("[RenameTheSpire] 配置文件格式错误，使用默认配置");
                    createDefaultConfig();
                }
            } else {
                // 第一次运行，创建默认配置
                System.out.println("[RenameTheSpire] 未找到配置文件，创建默认配置");
                createDefaultConfig();
            }

        } catch (IOException e) {
            System.err.println("[RenameTheSpire] 加载配置失败: " + e.getMessage());
            e.printStackTrace();
            createDefaultConfig();
        }
    }

    /**
     * 创建默认配置示例
     */
    private void createDefaultConfig() {
        // 批处理规则默认禁用
        batchRules = new BatchRules();
        batchRules.enabled = false;
        batchRules.prefix = "";
        batchRules.suffix = "打击";

        // 添加一些示例配置
        renames.put("Perfected Strike", "完美打击");
        renames.put("Strike_R", "打击");
        renames.put("Defend_R", "防御");

        System.out.println("[RenameTheSpire] 已创建默认配置");
        saveConfig();
    }

    /**
     * 保存配置文件
     */
    public void saveConfig() {
        try {
            // 创建配置对象
            ConfigData data = new ConfigData();
            data.batch_rules = batchRules;
            data.renames = new HashMap<>(renames);

            // 转换为JSON
            String json = gson.toJson(data);

            // 写入文件
            Files.write(configFilePath, json.getBytes(StandardCharsets.UTF_8));

            System.out.println("[RenameTheSpire] 配置已保存到: " + configFilePath.toString());

        } catch (IOException e) {
            System.err.println("[RenameTheSpire] 保存配置失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取卡牌的自定义名称
     * 优先级：单卡重命名 > 批处理规则 > 原名
     * 
     * @param cardID       卡牌ID
     * @param originalName 卡牌原名
     * @return 自定义名称，如果没有配置则返回 null
     */
    public String getCustomName(String cardID, String originalName) {
        // 优先级1：检查单卡重命名（用ID）
        String customName = renames.get(cardID);
        if (customName != null && !customName.isEmpty()) {
            return customName;
        }

        // 优先级2：检查单卡重命名（用原名）
        if (originalName != null) {
            customName = renames.get(originalName);
            if (customName != null && !customName.isEmpty()) {
                return customName;
            }
        }

        // 优先级3：应用批处理规则
        if (batchRules.enabled && originalName != null) {
            String result = originalName;

            // 添加前缀
            if (batchRules.prefix != null && !batchRules.prefix.isEmpty()) {
                result = batchRules.prefix + result;
            }

            // 添加后缀
            if (batchRules.suffix != null && !batchRules.suffix.isEmpty()) {
                result = result + batchRules.suffix;
            }

            // 如果有变化，返回结果
            if (!result.equals(originalName)) {
                return result;
            }
        }

        // 没有配置，返回null（保持原名）
        return null;
    }

    /**
     * 向后兼容的方法
     */
    public String getCustomName(String cardID) {
        return renames.get(cardID);
    }

    /**
     * 设置卡牌的自定义名称
     * 
     * @param cardId  卡牌ID
     * @param newName 新名称
     */
    public void setRename(String cardId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            removeRename(cardId);
        } else {
            renames.put(cardId, newName);
            System.out.println("[RenameTheSpire] 设置重命名: " + cardId + " -> " + newName);
        }
    }

    /**
     * 删除卡牌的重命名配置
     * 
     * @param cardId 卡牌ID
     */
    public void removeRename(String cardId) {
        if (renames.remove(cardId) != null) {
            System.out.println("[RenameTheSpire] 移除重命名: " + cardId);
        }
    }

    /**
     * 获取所有重命名配置
     * 
     * @return 重命名映射的副本
     */
    public Map<String, String> getAllRenames() {
        return new HashMap<>(renames);
    }

    /**
     * 清空所有重命名配置
     */
    public void clearAll() {
        renames.clear();
        System.out.println("[RenameTheSpire] 已清空所有重命名配置");
        saveConfig();
    }

    /**
     * 获取配置文件路径（用于提示用户）
     */
    public String getConfigFilePath() {
        return configFilePath.toString();
    }

    /**
     * 重新加载配置文件
     */
    public void reload() {
        System.out.println("[RenameTheSpire] 重新加载配置文件...");
        loadConfig();
    }

    /**
     * 获取批处理规则
     */
    public BatchRules getBatchRules() {
        return batchRules;
    }
}

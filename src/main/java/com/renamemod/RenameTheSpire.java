package com.renamemod;

import basemod.BaseMod;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.renamemod.config.CardRenameConfig;

/**
 * RenameTheSpire - 卡牌重命名Mod
 * 支持通过JSON文件自定义任意卡牌名称
 */
@SpireInitializer
public class RenameTheSpire implements PostInitializeSubscriber {

    public static final String MOD_ID = "RenameTheSpire";

    public RenameTheSpire() {
        System.out.println("[" + MOD_ID + "] 正在初始化...");

        // 初始化配置管理器
        CardRenameConfig.getInstance();

        // 订阅BaseMod的事件
        BaseMod.subscribe(this);

        System.out.println("[" + MOD_ID + "] 订阅完成");
    }

    public static void initialize() {
        new RenameTheSpire();
    }

    @Override
    public void receivePostInitialize() {
        System.out.println("[" + MOD_ID + "] 初始化完成！");

        System.out.println("[" + MOD_ID + "] ====================================");
        System.out.println("[" + MOD_ID + "] 卡牌重命名功能已启用");
        System.out.println("[" + MOD_ID + "] ");
        System.out.println("[" + MOD_ID + "] 配置文件位置：");
        System.out.println("[" + MOD_ID + "] " + CardRenameConfig.getInstance().getConfigFilePath());
        System.out.println("[" + MOD_ID + "] ");
        System.out.println("[" + MOD_ID + "] 使用方法：");
        System.out.println("[" + MOD_ID + "] 1. 打开上述配置文件");
        System.out.println("[" + MOD_ID + "] 2. 编辑 JSON，格式：\"卡牌索引\": \"新名称\"");
        System.out.println("[" + MOD_ID + "] 3. 索引可以是英文ID或中文名");
        System.out.println("[" + MOD_ID + "] 4. 保存文件并重启游戏");
        System.out.println("[" + MOD_ID + "] ");
        System.out.println("[" + MOD_ID + "] 示例：");
        System.out.println("[" + MOD_ID + "] \"打击\": \"超级打击\"");
        System.out.println("[" + MOD_ID + "] \"防御\": \"铁壁防御\"");
        System.out.println("[" + MOD_ID + "] ====================================");
    }
}

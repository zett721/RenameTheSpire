package com.renamemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.renamemod.config.CardRenameConfig;

/**
 * 卡牌名称Patch
 * 在卡牌初始化后，从配置文件读取并应用自定义名称
 * 
 * 智能标签管理：
 * - 检测改名后的卡牌名称是否包含"打击"或"Strike"
 * - 如果包含但卡牌没有STRIKE标签，自动添加
 * - 让游戏机制自然地跟随卡牌名称变化
 */
@SpirePatch(clz = AbstractCard.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {
        String.class, // cardID
        String.class, // name
        String.class, // imgUrl
        int.class, // cost
        String.class, // rawDescription
        AbstractCard.CardType.class, // type
        AbstractCard.CardColor.class, // color
        AbstractCard.CardRarity.class, // rarity
        AbstractCard.CardTarget.class // target
})
public class CardNamePatch {

    /**
     * 在卡牌构造函数执行完毕后，查找并应用自定义名称
     * 同时智能管理STRIKE标签
     * 
     * @param __instance 刚创建的卡牌实例
     */
    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance) {
        if (__instance.cardID == null) {
            return;
        }

        CardRenameConfig config = CardRenameConfig.getInstance();

        // 获取自定义名称（支持批处理规则）
        String customName = config.getCustomName(__instance.cardID, __instance.name);

        // 如果找到了自定义名称，应用它
        if (customName != null && !customName.isEmpty()) {
            System.out.println("[RenameTheSpire] 重命名卡牌: " + __instance.cardID);
            System.out.println("  原名称: " + __instance.name);
            System.out.println("  新名称: " + customName);

            // 应用新名称
            __instance.name = customName;

            // 智能标签管理
            manageStrikeTag(__instance, customName);
        }
    }

    /**
     * 智能管理STRIKE标签
     * 
     * 逻辑：
     * - 如果新名称包含"打击"或"Strike"，但卡牌没有STRIKE标签 → 添加标签
     * - 如果新名称不包含这些关键词，保持原标签不变（尊重原设计）
     * 
     * @param card    卡牌实例
     * @param newName 新名称
     */
    private static void manageStrikeTag(AbstractCard card, String newName) {
        // 检查新名称是否包含"打击"相关的关键词
        boolean nameIndicatesStrike = containsStrikeKeyword(newName);

        // 检查卡牌当前是否有STRIKE标签
        boolean hasStrikeTag = card.hasTag(CardTags.STRIKE);

        // 如果名称暗示这是打击牌，但卡牌没有标签 → 添加
        if (nameIndicatesStrike && !hasStrikeTag) {
            card.tags.add(CardTags.STRIKE);
            System.out.println("  [智能标签] 检测到名称包含打击关键词，添加STRIKE标签");
        }

        // 注意：如果名称不包含打击关键词，我们不移除现有标签
        // 这样可以保留原本就是打击牌的特性
    }

    /**
     * 检查名称是否包含"打击"相关的关键词
     * 
     * 支持的关键词：
     * - 中文："打击"
     * - 英文："Strike"（大小写不敏感）
     * 
     * @param name 卡牌名称
     * @return 是否包含打击关键词
     */
    private static boolean containsStrikeKeyword(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // 转换为小写进行比较（英文不区分大小写）
        String lowerName = name.toLowerCase();

        // 检查中文"打击"
        if (name.contains("打击")) {
            return true;
        }

        // 检查英文"strike"
        if (lowerName.contains("strike")) {
            return true;
        }

        return false;
    }
}

package com.renamemod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTags;
import com.renamemod.config.CardRenameConfig;

/**
 * 卡牌名称Patch
 * 挂载在 initializeDescription() 上，而非构造函数。
 *
 * 原因：挂载构造函数时，Patch 在 AbstractCard.<init> 完成后立即触发，
 * 但 MOD 的子类构造函数（如 LOA 的 AbstractPageCard）在 super() 返回后
 * 仍可能继续执行并覆盖 name 字段，导致改名失效。
 *
 * initializeDescription() 在整个构造链全部完成后才被调用，
 * 因此在此处修改 name 不会被任何子类构造逻辑覆盖。
 *
 * 注意：此方法在卡牌升级等场景下会被多次调用，
 * 每次调用都会重新应用自定义名称，确保改名持久生效。
 *
 * 智能标签管理：
 * - 检测改名后的卡牌名称是否包含"打击"或"Strike"
 * - 如果包含但卡牌没有STRIKE标签，自动添加
 */
@SpirePatch(clz = AbstractCard.class, method = "initializeDescription")
public class CardNamePatch {

    /**
     * 在 initializeDescription() 执行完毕后，查找并应用自定义名称
     *
     * @param __instance 卡牌实例
     */
    @SpirePostfixPatch
    public static void Postfix(AbstractCard __instance) {
        if (__instance.cardID == null) {
            return;
        }

        CardRenameConfig config = CardRenameConfig.getInstance();

        // 获取自定义名称（支持按ID或原名查找，以及批处理规则）
        String customName = config.getCustomName(__instance.cardID, __instance.name);

        // 如果找到了自定义名称，且当前名称还不是目标名称，则应用
        if (customName != null && !customName.isEmpty() && !customName.equals(__instance.name)) {
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
        boolean nameIndicatesStrike = containsStrikeKeyword(newName);
        boolean hasStrikeTag = card.hasTag(CardTags.STRIKE);

        if (nameIndicatesStrike && !hasStrikeTag) {
            card.tags.add(CardTags.STRIKE);
        }
    }

    /**
     * 检查名称是否包含"打击"相关的关键词
     *
     * @param name 卡牌名称
     * @return 是否包含打击关键词
     */
    private static boolean containsStrikeKeyword(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        String lowerName = name.toLowerCase();

        return name.contains("打击") || lowerName.contains("strike");
    }
}

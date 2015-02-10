package HxCKDMS.HxCEnchants;

import net.minecraftforge.common.config.Configuration;

public class Config
{
    public static boolean DebugMode;
    public static boolean Feedback;
    public static boolean EDT;
    public static int enchXPrate;

    public static int enchAdrenalineBoostID;
    public static boolean enchAdrenalineBoostEnable;
    public static int enchAdrenalineBoostLVL;
    public static int enchAdrenalineBoostWeight;

    public static int enchAirStriderID;
    public static boolean enchAirStriderEnable;
    public static int enchAirStriderLVL;
    public static int enchAirStriderWeight;

    public static int enchArrowExplosiveID;
    public static boolean enchArrowExplosiveEnable;
    public static int enchArrowExplosiveLVL;
    public static int enchArrowExplosiveWeight;

    public static int enchArrowLightningID;
    public static boolean enchArrowLightningEnable;
    public static int enchArrowLightningLVL;
    public static int enchArrowLightningWeight;

    public static int enchArrowSeekingID;
    public static boolean enchArrowSeekingEnable;
    public static int enchArrowSeekingLVL;
    public static int enchArrowSeekingWeight;

    public static int enchBattleHealingID;
    public static boolean enchBattleHealingEnable;
    public static int enchBattleHealingLVL;
    public static int enchBattleHealingWeight;

    public static int enchBoundID;
    public static boolean enchBoundEnable;
    public static int enchBoundLVL;
    public static int enchBoundWeight;

    public static int enchFlameTouchID;
    public static boolean enchFlameTouchEnable;
    public static int enchFlameTouchLVL;
    public static int enchFlameTouchWeight;

    public static int enchFlyID;
    public static boolean enchFlyEnable;
    public static int enchFlyWeight;

    public static int enchJumpBoostID;
    public static boolean enchJumpBoostEnable;
    public static int enchJumpBoostLVL;
    public static int enchJumpBoostWeight;

    public static int enchLeadFootedID;
    public static boolean enchLeadFootedEnable;
    public static int enchLeadFootedLVL;
    public static int enchLeadFootedWeight;

    public static int enchLifeStealID;
    public static boolean enchLifeStealEnable;
    public static int enchLifeStealLVL;
    public static int enchLifeStealWeight;

    public static int enchPoisonID;
    public static boolean enchPoisonEnable;
    public static int enchPoisonLVL;
    public static int enchPoisonWeight;

    public static int enchRegenID;
    public static boolean enchRegenEnable;
    public static int enchRegenLVL;
    public static int enchRegenWeight;
    public static int enchRegenRate;

    public static int enchRepairID;
    public static boolean enchRepairEnable;
    public static int enchRepairLVL;
    public static int enchRepairWeight;
    public static int enchRepairRate;

    public static int enchShroudID;
    public static boolean enchShroudEnable;
    public static int enchShroudLVL;
    public static int enchShroudWeight;

    public static int enchSoulTearID;
    public static boolean enchSoulTearEnable;
    public static int enchSoulTearLVL;
    public static int enchSoulTearWeight;

    public static int enchSwiftnessID;
    public static boolean enchSwiftnessEnable;
    public static int enchSwiftnessLVL;
    public static int enchSwiftnessWeight;

    public static int enchStealthID;
    public static boolean enchStealthEnable;
    public static int enchStealthLVL;
    public static int enchStealthWeight;

    public static int enchVampirismID;
    public static boolean enchVampirismEnable;
    public static int enchVampirismLVL;
    public static int enchVampirismWeight;

    public static int enchVitalityID;
    public static boolean enchVitalityEnable;
    public static int enchVitalityLVL;
    public static int enchVitalityWeight;

    public static int enchWitherProtectionID;
    public static boolean enchWitherProtectionEnable;
    public static int enchWitherProtectionLVL;
    public static int enchWitherProtectionWeight;

    public static int enchExamineID;
    public static boolean enchExamineEnable;
    public static int enchExamineLVL;
    public static int enchExamineWeight;

    public Config(Configuration config)
    {
        config.load();
        /** Config Group Descriptions **/
        config.setCategoryComment("Debugging", "This allows you to see all debugging things. WARNING SPAMS CONSOLE.");
        config.setCategoryComment("Features", "This allows you to tweak anything we add.");
        config.setCategoryComment("Armor Enchants", "This allows you to disable any enchants if you feel they are useless.");
        config.setCategoryComment("Arrow Enchants", "This allows you to disable any enchants if you feel they are useless.");
        config.setCategoryComment("Tool/Weapon Enchants", "This allows you to disable any enchants if you feel they are useless.");
        
        config.setCategoryComment("Armor Enchant IDs", "This allows you to set the ID of the Enchants");
        config.setCategoryComment("Arrow Enchant IDs", "This allows you to set the ID of the Enchants");
        config.setCategoryComment("Tool/Weapon Enchant IDs", "This allows you to set the ID of the Enchants");

        config.setCategoryComment("Armor Enchant LVLs", "This allows you to set the Max Level of the Enchants");
        config.setCategoryComment("Arrow Enchant LVLs", "This allows you to set the Max Level of the Enchants");
        config.setCategoryComment("Tool/Weapon Enchant LVLs", "This allows you to set the Max Level of the Enchants");

        config.setCategoryComment("Armor Enchant Weights", "This allows you to set the Weight of the Enchants. The higher the number the less difficult to obtain.");
        config.setCategoryComment("Arrow Enchant Weights", "This allows you to set the Weight of the Enchants. The higher the number the less difficult to obtain.");
        config.setCategoryComment("Tool/Weapon Enchant Weights", "This allows you to set the Weight of the Enchants. The higher the number the less difficult to obtain.");

        /** Debugging **/
        DebugMode = config.getBoolean("Debug", "Debugging", false, "Only enable this if you want a ton of debug spam.");

        /** Features **/
        Feedback = config.getBoolean("Feedback", "Features", false, "ChatMessages and such from enchants.");
        enchRegenRate = config.getInt("Regen Rate Default", "Features", 3, 1, 99999, "");
        enchRepairRate = config.getInt("Repair Speed Default", "Features", 1, 1, 99999, "DON'T EXCEED THE MAX (Java will crash)");
        enchXPrate = config.getInt("Extra exp Default", "Features", 1, 1, 9999999, "Increasing this means more xp gain from Examine Enchant.");
        EDT = config.getBoolean("ExplosionDestroysTerrain", "Features", false, "");

        /** Armor Enchants **/
        enchAdrenalineBoostEnable = config.getBoolean("AdrenalineBoost", "Armor Enchants", true, "Enable Enchant Adrenaline Boost?");
        enchAirStriderEnable = config.getBoolean("AirStrider", "Armor Enchants", false, "Enable Enchant Air Strider? DOESN'T WORK SERVER SIDE");
        enchBattleHealingEnable = config.getBoolean("BattleHealing", "Armor Enchants", true, "Enable Enchant Battle Healing?");
        enchFlyEnable = config.getBoolean("Fly", "Armor Enchants", true, "Enable Enchant Fly?");
        enchJumpBoostEnable = config.getBoolean("JumpBoost", "Armor Enchants", true, "Enable Enchant Jump Boost");
        enchRegenEnable = config.getBoolean("Regen", "Armor Enchants", true, "Enable Enchant Regen?");
        enchSwiftnessEnable = config.getBoolean("Swiftness", "Armor Enchants", true, "Enable Enchant Swiftness?");
        enchVitalityEnable = config.getBoolean("Vitality", "Armor Enchants", true, "Enable Enchant Vitality?");
        enchWitherProtectionEnable = config.getBoolean("WitherProtection", "Armor Enchants", true, "Enable Enchant Wither Protection?");
        enchRepairEnable = config.getBoolean("Repair", "Armor Enchants", true, "Enable Enchant Repair?");
        enchBoundEnable = config.getBoolean("Bound", "Armor Enchants", false, "Enable Enchant Bound? DISABLED IT IS BROKEN");
        enchLeadFootedEnable = config.getBoolean("LeadFooted", "Armor Enchants", false, "Enable Enchant Lead Footed? DISABLED IT IS BROKEN");
        enchShroudEnable = config.getBoolean("Shroud", "Armor Enchants", false, "Enable Enchant Shroud? DISABLED IT IS BROKEN");
        enchStealthEnable = config.getBoolean("Stealth", "Armor Enchants", true, "Enable Enchant Stealth?");

        enchAdrenalineBoostID = config.getInt("AdrenalineBoostID", "Armor Enchant IDs", 300, 300, 320, "");
        enchAirStriderID = config.getInt("AirStriderID", "Armor Enchant IDs", 301, 300, 320, "");
        enchBattleHealingID = config.getInt("BattleHealingID", "Armor Enchant IDs", 302, 300, 320, "");
        enchFlyID = config.getInt("FlyID", "Armor Enchant IDs", 303, 300, 320, "");
        enchJumpBoostID = config.getInt("JumpBoostID", "Armor Enchant IDs", 304, 300, 320, "");
        enchRepairID = config.getInt("RepairID", "Armor Enchant IDs", 305, 300, 320, "");
        enchRegenID = config.getInt("RegenID", "Armor Enchant IDs", 306, 300, 320, "");
        enchSwiftnessID = config.getInt("SwiftnessID", "Armor Enchant IDs", 307, 300, 320, "");
        enchVitalityID = config.getInt("VitalityID", "Armor Enchant IDs", 308, 300, 320, "");
        enchWitherProtectionID = config.getInt("WitherProtectionID", "Armor Enchant IDs", 309, 300, 320, "");
        enchBoundID = config.getInt("BoundID", "Armor Enchant IDs", 310, 300, 320, "");
        enchLeadFootedID = config.getInt("LeadFootedID", "Armor Enchant IDs", 311, 300, 320, "");
        enchShroudID = config.getInt("ShroudID", "Armor Enchant IDs", 312, 300, 320, "");
        enchStealthID = config.getInt("StealthID", "Armor Enchant IDs", 313, 300, 320, "");

        enchAdrenalineBoostLVL = config.getInt("AdrenalineBoostLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchAirStriderLVL = config.getInt("AirStriderLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchBattleHealingLVL = config.getInt("BattleHealingLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchJumpBoostLVL = config.getInt("JumpBoostLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchRepairLVL = config.getInt("RepairLVL", "Armor Enchant LVLs", 4, 5, 10, "");
        enchRegenLVL = config.getInt("RegenLVL", "Armor Enchant LVLs", 4, 2, 10, "");
        enchSwiftnessLVL = config.getInt("SwiftnessLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchVitalityLVL = config.getInt("VitalityLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchWitherProtectionLVL = config.getInt("WitherProtectionLVL", "Armor Enchant LVLs", 4, 1, 10, "");
        enchBoundLVL = config.getInt("BoundLVL", "Armor Enchant LVLs", 3, 1, 10, "");
        enchLeadFootedLVL = config.getInt("LeadFootedLVL", "Armor Enchant LVLs", 1, 1, 10, "");
        enchShroudLVL = config.getInt("ShroudLVL", "Armor Enchant LVLs", 1, 1, 10, "");
        enchStealthLVL = config.getInt("StealthLVL", "Armor Enchant LVLs", 2, 1, 10, "");

        enchAdrenalineBoostWeight = config.getInt("AdrenalineBoostWeight", "Armor Enchant Weights", 10, 1, 100, "");
        enchAirStriderWeight = config.getInt("AirStriderWeight", "Armor Enchant Weights", 8, 1, 100, "");
        enchBattleHealingWeight = config.getInt("BattleHealingWeight", "Armor Enchant Weights", 3, 1, 100, "");
        enchFlyWeight = config.getInt("FlyWeight", "Armor Enchant Weights", 1, 1, 100, "");
        enchJumpBoostWeight = config.getInt("JumpBoostWeight", "Armor Enchant Weights", 10, 1, 100, "");
        enchRegenWeight = config.getInt("RegenWeight", "Armor Enchant Weights", 1, 1, 100, "");
        enchRepairWeight = config.getInt("RepairWeight", "Armor Enchant Weights", 1, 1, 100, "");
        enchSwiftnessWeight = config.getInt("SwiftnessWeight", "Armor Enchant Weights", 10, 1, 100, "");
        enchVitalityWeight = config.getInt("VitalityWeight", "Armor Enchant Weights", 4, 1, 100, "");
        enchWitherProtectionWeight = config.getInt("WitherProtectionWeight", "Armor Enchant Weights", 8, 1, 100, "");
        enchBoundWeight = config.getInt("BoundWeight", "Armor Enchant Weights", 5, 1, 100, "");
        enchLeadFootedWeight = config.getInt("LeadFootedWeight", "Armor Enchant Weights", 10, 1, 100, "");
        enchShroudWeight = config.getInt("ShroudWeight", "Armor Enchant Weights", 2, 1, 100, "");
        enchStealthWeight = config.getInt("StealthWeight", "Armor Enchant Weights", 6, 1, 100, "");

        /** Arrow Enchants **/
        enchArrowExplosiveEnable = config.getBoolean("ArrowExplosive", "Arrow Enchants", true, "Enable Enchant Arrow Explosive?");
        enchArrowLightningEnable = config.getBoolean("ArrowLightning", "Arrow Enchants", true, "Enable Enchant Arrow Lightning?");
        enchArrowSeekingEnable = config.getBoolean("ArrowSeeking", "Arrow Enchants", true, "Enable Enchant Arrow Seeking?");

        enchArrowExplosiveID = config.getInt("ArrowExplosiveID", "Arrow Enchant IDs", 321, 321, 330, "");
        enchArrowLightningID = config.getInt("ArrowLightningID", "Arrow Enchant IDs", 322, 321, 330, "");
        enchArrowSeekingID = config.getInt("ArrowSeekingID", "Arrow Enchant IDs", 323, 321, 330, "");

        enchArrowExplosiveLVL = config.getInt("ArrowExplosiveLVL", "Arrow Enchant LVLs", 4, 1, 10, "");
        enchArrowLightningLVL = config.getInt("ArrowLightningLVL", "Arrow Enchant LVLs", 1, 1, 10, "");
        enchArrowSeekingLVL = config.getInt("ArrowSeekingLVL", "Arrow Enchant LVLs", 4, 1, 10, "");

        enchArrowExplosiveWeight = config.getInt("ArrowExplosiveWeight", "Arrow Enchant Weights", 2, 1, 100, "");
        enchArrowLightningWeight = config.getInt("ArrowLightningWeight", "Arrow Enchant Weights", 5, 1, 100, "");
        enchArrowSeekingWeight = config.getInt("ArrowSeekingWeight", "Arrow Enchant Weights", 1, 1, 100, "");

        /** Tool/Weapon Enchants **/
        enchFlameTouchEnable = config.getBoolean("FlameTouch", "Tool/Weapon Enchants", true, "Enable Enchant Auto-Smelt?");
        enchLifeStealEnable = config.getBoolean("LifeSteal", "Tool/Weapon Enchants", true, "Enable Enchant Life Steal?");
        enchPoisonEnable = config.getBoolean("Poison", "Tool/Weapon Enchants", true, "Enable Enchant Poison?");
        enchVampirismEnable = config.getBoolean("Vampirism", "Tool/Weapon Enchants", true, "Enable Enchant Vampirism?");
        enchExamineEnable = config.getBoolean("Examine", "Tool/Weapon Enchants", true, "Enable Enchant Examine?");

        enchFlameTouchID = config.getInt("FlameTouchID", "Tool/Weapon Enchant IDs", 331, 331, 350, "");
        enchLifeStealID = config.getInt("LifeStealID", "Tool/Weapon Enchant IDs", 332, 331, 350, "");
        enchPoisonID = config.getInt("PoisonID", "Tool/Weapon Enchant IDs", 333, 331, 350, "");
        enchVampirismID = config.getInt("VampirismID", "Tool/Weapon Enchant IDs", 334, 331, 350, "");
        enchExamineID = config.getInt("ExamineID", "Tool/Weapon Enchant IDs", 335, 331, 350, "");

        enchFlameTouchLVL = config.getInt("FlameTouchLVL", "Tool/Weapon Enchant LVLs", 4, 1, 10, "");
        enchLifeStealLVL = config.getInt("LifeStealLVL", "Tool/Weapon Enchant LVLs", 5, 1, 10, "");
        enchPoisonLVL = config.getInt("PoisonLVL", "Tool/Weapon Enchant LVLs", 4, 1, 10, "");
        enchVampirismLVL = config.getInt("VampirismLVL", "Tool/Weapon Enchant LVLs", 5, 1, 10, "");
        enchExamineLVL = config.getInt("ExamineLVL", "Tool/Weapon Enchant LVLs", 10, 1, 10, "");

        enchFlameTouchWeight = config.getInt("FlameTouchWeight", "Tool/Weapon Enchant Weights", 2, 1, 100, "");
        enchLifeStealWeight = config.getInt("LifeStealWeight", "Tool/Weapon Enchant Weights", 7, 1, 100, "");
        enchPoisonWeight = config.getInt("PoisonWeight", "Tool/Weapon Enchant Weights", 10, 1, 100, "");
        enchVampirismWeight = config.getInt("VampirismWeight", "Tool/Weapon Enchant Weights", 3, 1, 100, "");
        enchExamineWeight = config.getInt("ExamineWeight", "Tool/Weapon Enchant Weights", 1, 1, 100, "");

        if(config.hasChanged())
        {
            config.save();
        }
    }
}
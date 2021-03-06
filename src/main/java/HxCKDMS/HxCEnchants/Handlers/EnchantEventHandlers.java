package HxCKDMS.HxCEnchants.Handlers;

import HxCKDMS.HxCEnchants.Configurations.Configurations;
import HxCKDMS.HxCEnchants.api.AABBUtils;
import HxCKDMS.HxCEnchants.api.EnchantingUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hxckdms.hxccore.libraries.GlobalVariables;
import hxckdms.hxccore.utilities.HxCPlayerInfoHandler;
import hxckdms.hxccore.utilities.TeleportHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockReed;
import net.minecraft.block.IGrowable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;

import static HxCKDMS.HxCEnchants.Configurations.Configurations.*;
import static HxCKDMS.HxCEnchants.lib.Reference.HealthUUID;
import static HxCKDMS.HxCEnchants.lib.Reference.SpeedUUID;
import static net.minecraft.enchantment.Enchantment.enchantmentsList;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantments;

@SuppressWarnings("all")
public class EnchantEventHandlers {
    private int repairTimer = 60, regenTimer = 60, vitTimer = 600, flightCheckDelay = updateTime, sateTimer = 60;

    @SubscribeEvent
    public void playerMineBlockEvent(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester != null) {
            ItemStack tool = event.harvester.getHeldItem();
            if (isEnabled("FlameTouch")) {
                int AutoSmeltLevel = (short) EnchantmentHelper.getEnchantmentLevel(enchantments.get("FlameTouch").id, tool);
                if (AutoSmeltLevel > 0) {
                    for (int i = 0; i < event.drops.size(); i++) {
                        ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(event.drops.get(i));

                        if (smelted != null) {
                            ItemStack drop = smelted.copy();
                            if (AutosmeltMultipliesWithOres && event.drops.get(i).getDisplayName().contains("Ore"))
                                drop.stackSize *= AutoSmeltLevel;
                            if (AutosmeltWithFortune && EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, tool) > 0)
                                drop.stackSize += event.world.rand.nextInt(EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, tool));
                            event.drops.set(i, drop);

                            event.world.spawnEntityInWorld(new EntityXPOrb(event.world, event.x, event.y, event.z, (int) FurnaceRecipes.smelting().func_151398_b(drop)));
                        }
                    }
                }
            }

            if (isEnabled("VoidTouch")) {
                short voidLevel = (short) EnchantmentHelper.getEnchantmentLevel(enchantments.get("VoidTouch").id, tool);
                if (voidLevel > 0 && event.drops.size() > 0) {
                    List<ItemStack> stacks = event.drops;
                    for (int i = stacks.size() - 1; i == 0; i--) {
                        for (String block : VoidedItems) {
                            if (event.drops.get(i).getDisplayName().equalsIgnoreCase(new ItemStack(Block.getBlockFromName(block)).getDisplayName())) {
                                event.drops.remove(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private int auraDelayTimer = AuraUpdateDelay;

    @SubscribeEvent
    @SuppressWarnings("all")
    public void playerTickEvent(LivingEvent.LivingUpdateEvent event) {
        flightCheckDelay--;
        auraDelayTimer--;
        repairTimer--;
        regenTimer--;
        sateTimer--;
        vitTimer--;

        if (event.entityLiving != null && event.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityLiving;
            ItemStack ArmourHelm = player.inventory.armorItemInSlot(3),
                    ArmourChest = player.inventory.armorItemInSlot(2),
                    ArmourLegs = player.inventory.armorItemInSlot(1),
                    ArmourBoots = player.inventory.armorItemInSlot(0);

            if (isEnabled("Swiftness")) {
                IAttributeInstance ps = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                short speedLevel = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Swiftness").id, ArmourLegs);
                double speedBoost = speedLevel * SpeedTweak;
                AttributeModifier SpeedBuff = new AttributeModifier(SpeedUUID, "SpeedBuffedPants", speedBoost, 0);
                if (!ps.func_111122_c().contains(SpeedBuff) && speedLevel != 0)
                    ps.applyModifier(SpeedBuff);
                if (ps.func_111122_c().contains(SpeedBuff) && speedLevel <= 0)
                    ps.removeModifier(SpeedBuff);
            }

            if (isEnabled("Nightvision") && (!player.worldObj.isDaytime() || !player.worldObj.canBlockSeeTheSky((int) player.posX, (int) player.posY, (int) player.posZ))) {
                short vision = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Nightvision").id, ArmourHelm);
                if (vision > 0)
                    player.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 600, 1, true));
            }

            if (isEnabled("Gluttony")) {
                short gluttony = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Gluttony").id, ArmourHelm);
                LinkedHashMap<Boolean, Item> tmp = hasFood(player);
                if (gluttony > 0 && !tmp.isEmpty() && player.getFoodStats().getFoodLevel() <= (gluttony) + 6 && tmp.containsKey(true) && tmp.get(true) != null) {
                    player.getFoodStats().addStats(((ItemFood) Items.apple).func_150905_g(new ItemStack(tmp.get(true))), ((ItemFood) Items.apple).func_150906_h(new ItemStack(tmp.get(true))));
                    for (short slot = 0; slot < player.inventory.mainInventory.length; slot++) {
                        if (player.inventory.mainInventory[slot] != null && player.inventory.mainInventory[slot].getItem() instanceof ItemFood && player.inventory.mainInventory[slot].getItem() == tmp.get(true)) {
                            player.inventory.decrStackSize(slot, 1);
                            break;
                        }
                    }
                }
            }

            if (vitTimer <= 0 && isEnabled("Vitality")) {
                IAttributeInstance ph = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
                short vitalityLevel = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Vitality").id, ArmourChest);
                double vitality = vitalityLevel * VitalityPerLevel;
                AttributeModifier HealthBuff = new AttributeModifier(HealthUUID, "HealthBuffedChestplate", vitality, 0);
                if (!ph.func_111122_c().contains(HealthBuff) && vitalityLevel != 0)
                    ph.applyModifier(HealthBuff);
                if (ph.func_111122_c().contains(HealthBuff) && vitalityLevel <= 0)
                    ph.removeModifier(HealthBuff);

                vitTimer = 600;
            }

            if (isEnabled("Fly") && flightCheckDelay <= 0) {
                if (EnchantmentHelper.getEnchantmentLevel(enchantments.get("Fly").id, ArmourBoots) > 0 && !HxCPlayerInfoHandler.getBoolean(player, "flightEnc")) {
                    player.capabilities.allowFlying = true;
                    HxCPlayerInfoHandler.setBoolean(player, "flightEnc", true);
                } else if (EnchantmentHelper.getEnchantmentLevel(enchantments.get("Fly").id, ArmourBoots) < 1 && HxCPlayerInfoHandler.getBoolean(player, "flightEnc")) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    HxCPlayerInfoHandler.setBoolean(player, "flightEnc", false);
                }
                player.sendPlayerAbilities();
                flightCheckDelay = updateTime;
            }

            if (player.inventory.armorItemInSlot(0) != null && player.inventory.armorItemInSlot(0).hasTagCompound() && player.inventory.armorItemInSlot(0).isItemEnchanted() && player.motionY < -0.8 && !player.isSneaking()) {
                int tmp = 0, tmp2 = 0;
                if (isEnabled("FeatherFall"))
                    tmp = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("FeatherFall").id, player.inventory.armorItemInSlot(0));
                if (isEnabled("MeteorFall"))
                    tmp2 = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("MeteorFall").id, player.inventory.armorItemInSlot(0));

                if (tmp > 0)
                    player.addVelocity(0, 0.01f * (float) tmp, 0);

                if (tmp2 > 0)
                    player.addVelocity(0, 0.02f * (float) -tmp2, 0);;
            }

            if (auraDelayTimer <= 0) {
                auraDelayTimer = AuraUpdateDelay * 100;

                List ents = player.worldObj.getEntitiesWithinAABB(Entity.class, AABBUtils.getAreaBoundingBox((short) Math.round(player.posX), (short) Math.round(player.posY), (short) Math.round(player.posZ), 10));
                if (ArmourChest != null && ArmourChest.hasTagCompound() && ArmourChest.isItemEnchanted() &&
                        ArmourLegs != null && ArmourLegs.hasTagCompound() && ArmourLegs.isItemEnchanted() &&
                        ArmourBoots != null && ArmourBoots.hasTagCompound() && ArmourBoots.isItemEnchanted() &&
                        ArmourHelm != null && ArmourHelm.hasTagCompound() && ArmourHelm.isItemEnchanted() &&
                        !ents.isEmpty()) {

                    LinkedHashMap<Enchantment, Integer> sharedEnchants = new LinkedHashMap<>();
                    LinkedHashMap<Integer, Integer> enchs = (LinkedHashMap<Integer, Integer>) getEnchantments(ArmourBoots);
                    ((LinkedHashMap<Integer, Integer>) getEnchantments(ArmourLegs)).forEach(enchs::putIfAbsent);
                    ((LinkedHashMap<Integer, Integer>) getEnchantments(ArmourChest)).forEach(enchs::putIfAbsent);
                    ((LinkedHashMap<Integer, Integer>) getEnchantments(ArmourHelm)).forEach(enchs::putIfAbsent);

                    enchs.keySet().forEach(ench -> {
                        if (getEnchantments(ArmourBoots).containsKey(ench) &&
                                getEnchantments(ArmourLegs).containsKey(ench) &&
                                getEnchantments(ArmourChest).containsKey(ench) &&
                                getEnchantments(ArmourHelm).containsKey(ench)) {

                            int tmpz = (int) getEnchantments(ArmourBoots).get(ench);
                            tmpz += (int) getEnchantments(ArmourLegs).get(ench);
                            tmpz += (int) getEnchantments(ArmourChest).get(ench);
                            tmpz += (int) getEnchantments(ArmourHelm).get(ench);

                            sharedEnchants.put(Enchantment.enchantmentsList[ench], tmpz);
                        }
                    });
                    ents.removeIf(ent -> ent == player);
                    if (sharedEnchants != null && !sharedEnchants.isEmpty() && !ents.isEmpty())
                        handleAuraEvent(player, ents, sharedEnchants);
                }
            }


            if (isEnabled("Repair") && repairTimer <= 0) {
                RepairItems(player);
                repairTimer = Configurations.repairTimer;
            }

            if (isEnabled("Saturation") && sateTimer <= 0 && player.getFoodStats().getSaturationLevel() < 10) {
                player.getFoodStats().addStats(EnchantmentHelper.getEnchantmentLevel(enchantments.get("Saturation").id, ArmourHelm), EnchantmentHelper.getEnchantmentLevel(enchantments.get("Saturation").id, ArmourHelm));
                sateTimer = Configurations.regenTimer * 2;
            }

            if (isEnabled("Regen") && regenTimer <= 0) {
                short H = 0, C = 0, L = 0, B = 0, rid = (short) enchantments.get("Regen").id;
                byte Regen = 0;

                if (ArmourHelm != null)
                    H = (short) EnchantmentHelper.getEnchantmentLevel((int)rid, player.inventory.armorItemInSlot(3));
                if (ArmourChest != null)
                    C = (short) EnchantmentHelper.getEnchantmentLevel((int)rid, player.inventory.armorItemInSlot(2));
                if (ArmourLegs != null)
                    L = (short) EnchantmentHelper.getEnchantmentLevel((int)rid, player.inventory.armorItemInSlot(1));
                if (ArmourBoots != null)
                    B = (short) EnchantmentHelper.getEnchantmentLevel((int)rid, player.inventory.armorItemInSlot(0));

                if (H > 0) Regen += 1;
                if (B > 0) Regen += 1;
                if (C > 0) Regen += 1;
                if (L > 0) Regen += 1;

                if (player.getHealth() < player.getMaxHealth() && Regen > 0) {
                    float hp = player.getMaxHealth() - player.getHealth();
                    regenTimer = Configurations.regenTimer;
                    player.heal(Regen);
                }
            }
            player.sendPlayerAbilities();

        }
    }

    @SubscribeEvent
    public void breakBlockEvent(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player.getHeldItem() != null && player.getHeldItem().hasTagCompound() && player.getHeldItem().isItemEnchanted()) {
            ItemStack item = player.getHeldItem(); int worldeater = enchantments.get("EarthEater").id;
            Block block = event.block;
            LinkedHashMap<Integer, Integer> enchs = (LinkedHashMap<Integer, Integer>) getEnchantments(item);
            if (enchs.keySet().contains(worldeater)) {
                int l = enchs.get(worldeater), width, depth, height;

                height = Math.round(l / Configurations.EarthEaterHeightModifier);
                width = Math.round(l / Configurations.EarthEaterWidthModifier);
                depth = Math.round(l / Configurations.EarthEaterDepthModifier);

                float rot = player.getRotationYawHead();
                if (player.rotationPitch < 45 && player.rotationPitch > -45) {
                    if ((rot > 45 && rot < 135) || (rot > -45 && rot < -135)) {
//                        System.out.println("West");
                        for (int x = event.x - (depth); x <= event.x; x++) {
                            for (int y = event.y - 1; y <= event.y + (height-1); y++) {
                                for (int z = event.z - (width/2); z <= event.z + (width/2); z++) {
                                    if (player.worldObj.getBlock(x, y, z).getMaterial() == block.getMaterial() &&
                                            player.canHarvestBlock(player.worldObj.getBlock(x, y, z)) &&
                                            player.worldObj.getBlock(x, y, z).getBlockHardness(player.worldObj, x, y, z) > 0) {
                                        player.worldObj.getBlock(x, y, z).harvestBlock(event.world, player, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
                                        player.worldObj.setBlockToAir(x, y, z);
                                    }
                                }
                            }
                        }
                    } else if ((rot > 225 && rot < 315) || (rot > -225 && rot < -315)) {
//                        System.out.println("East");
                        for (int x = event.x; x <= event.x + (depth); x++) {
                            for (int y = event.y - 1; y <= event.y + (height-1); y++) {
                                for (int z = event.z - (width/2); z <= event.z + (width/2); z++) {
                                    if (player.worldObj.getBlock(x, y, z).getMaterial() == block.getMaterial() &&
                                            player.canHarvestBlock(player.worldObj.getBlock(x, y, z)) &&
                                            player.worldObj.getBlock(x, y, z).getBlockHardness(player.worldObj, x ,y ,z) > 0) {
                                        player.worldObj.getBlock(x, y, z).harvestBlock(event.world, player, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
                                        player.worldObj.setBlockToAir(x, y, z);
                                    }
                                }
                            }
                        }
                    } else if ((rot < 225 && rot > 135) || (rot > -225 && rot < -135)) {
//                        System.out.println("North");
                        for (int x = event.x- (width/2); x <= event.x + (width/2); x++) {
                            for (int y = event.y - 1; y <= event.y + (height-1); y++) {
                                for (int z = event.z - (depth); z <= event.z; z++) {
                                    if (player.worldObj.getBlock(x, y, z).getMaterial() == block.getMaterial() &&
                                            player.canHarvestBlock(player.worldObj.getBlock(x, y, z)) &&
                                            player.worldObj.getBlock(x, y, z).getBlockHardness(player.worldObj, x, y, z) > 0) {
                                        player.worldObj.getBlock(x, y, z).harvestBlock(event.world, player, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
                                        player.worldObj.setBlockToAir(x, y, z);
                                    }
                                }
                            }
                        }
                    } else {
//                        System.out.println("South");
                        for (int x = event.x- (width/2); x <= event.x + (width/2); x++) {
                            for (int y = event.y - 1; y <= event.y + (height-1); y++) {
                                for (int z = event.z; z <= event.z + (depth); z++) {
                                    if (player.worldObj.getBlock(x, y, z).getMaterial() == block.getMaterial() &&
                                            player.canHarvestBlock(player.worldObj.getBlock(x, y, z)) &&
                                            player.worldObj.getBlock(x, y, z).getBlockHardness(player.worldObj, x ,y ,z) > 0) {
                                        player.worldObj.getBlock(x, y, z).harvestBlock(event.world, player, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
                                        player.worldObj.setBlockToAir(x, y, z);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    int xMod = ((rot > 45 && rot < 135) || (rot > -45 && rot < -135) ? (height/2) : (width/2));
                    int zMod = ((rot > 45 && rot < 135) || (rot > -45 && rot < -135) ? (width/2) : (height/2));
                    for (int x = event.x - xMod; x <= event.x + xMod; x++) {
                        for (int z = event.z - zMod; z <= event.z + zMod; z++) {
                            if (player.rotationPitch < -45) {
                                for (int y = event.y; y <= event.y + (depth); y++) {
                                    if (player.worldObj.getBlock(x, y, z).getMaterial() == block.getMaterial() &&
                                            player.canHarvestBlock(player.worldObj.getBlock(x, y, z)) &&
                                            player.worldObj.getBlock(x, y, z).getBlockHardness(player.worldObj, x, y, z) > 0) {
                                        player.worldObj.getBlock(x, y, z).harvestBlock(event.world, player, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
                                        player.worldObj.setBlockToAir(x, y, z);
                                    }
                                }
                            } else {
                                for (int y = event.y - (depth); y <= event.y; y++) {
                                    if (player.worldObj.getBlock(x, y, z).getMaterial() == block.getMaterial() &&
                                            player.canHarvestBlock(player.worldObj.getBlock(x, y, z)) &&
                                            player.worldObj.getBlock(x, y, z).getBlockHardness(player.worldObj, x, y, z) > 0) {
                                        player.worldObj.getBlock(x, y, z).harvestBlock(event.world, player, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
                                        player.worldObj.setBlockToAir(x, y, z);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void PlayerEvent(PlayerEvent.BreakSpeed event) {
        if (Configurations.enchantments.get("SpeedMine").enabled && event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().isItemEnchanted() && getEnchantmentLevel(enchantments.get("SpeedMine").id, event.entityPlayer.getHeldItem()) > 0)
            event.newSpeed = (event.originalSpeed + event.originalSpeed*(getEnchantmentLevel(enchantments.get("SpeedMine").id, event.entityPlayer.getHeldItem()) / 10));
    }


    @SubscribeEvent
    public void livingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if(event.entityLiving instanceof EntityPlayer && ((EntityPlayer) event.entityLiving).inventory.armorItemInSlot(1) != null && ((EntityPlayer) event.entityLiving).inventory.armorItemInSlot(1).hasTagCompound() && ((EntityPlayer) event.entityLiving).inventory.armorItemInSlot(1).isItemEnchanted()) {
            ItemStack legs = ((EntityPlayer) event.entityLiving).inventory.armorItemInSlot(1);
            int JumpBoostLevel = getEnchantmentLevel(enchantments.get("JumpBoost").id, legs);
            if (JumpBoostLevel > 0) {
                EntityPlayer player = (EntityPlayer) event.entityLiving;
                double JumpBuff = player.motionY + 0.1 * JumpBoostLevel;
                player.motionY += JumpBuff;
            }
        }
    }

    @SubscribeEvent
    public void livingFallEvent(LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            if (player.inventory.armorItemInSlot(0) != null && player.inventory.armorItemInSlot(0).hasTagCompound() && player.inventory.armorItemInSlot(0).isItemEnchanted()) {
                ItemStack boots = player.inventory.armorItemInSlot(0);
                int featherFall = 0, meteorFall = 0;
                if (enchantments.get("FeatherFall").enabled)
                    featherFall = getEnchantmentLevel(enchantments.get("FeatherFall").id, boots);
                if (enchantments.get("MeteorFall").enabled)
                    meteorFall = getEnchantmentLevel(enchantments.get("MeteorFall").id, boots);

                if (featherFall < 4 && featherFall > 0)event.distance /= featherFall;
                else if (featherFall > 4) event.distance = 0;

                if (meteorFall > 0 && event.distance > 10) {
                    player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, event.distance / 5 * meteorFall, false);
                    event.distance = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public void livingHurtEvent(LivingHurtEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
            float ammount = event.ammount;
            boolean allowEffect = !(event.source.damageType.equalsIgnoreCase("wither") ||
                    event.source.damageType.equalsIgnoreCase("starve") ||
                    event.source.damageType.equalsIgnoreCase("fall") ||
                    event.source.damageType.equalsIgnoreCase("explosion.player") ||
                    event.source.damageType.equalsIgnoreCase("explosion") ||
                    event.source.damageType.equalsIgnoreCase("inWall") ||
                    event.source.damageType.equalsIgnoreCase("poison"));

            ItemStack ArmourHelm = player.inventory.armorItemInSlot(3),
                    ArmourChest = player.inventory.armorItemInSlot(2);

            int AdrenalineBoostLevel = 0, BattleHealingLevel = 0, WitherProt = 0, DivineInterventionLevel = 0, ExplosiveDischarge = 0;

            if (ArmourChest != null && ArmourChest.hasTagCompound() && ArmourChest.isItemEnchanted()) {
                if (isEnabled("BattleHealing"))
                    BattleHealingLevel = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("BattleHealing").id, ArmourChest);

                if (isEnabled("DivineIntervention"))
                    DivineInterventionLevel = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("DivineIntervention").id, ArmourChest);

                if (isEnabled("ExplosiveDischarge") && allowEffect)
                    ExplosiveDischarge = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("ExplosiveDischarge").id, ArmourChest);

                if (BattleHealingLevel > 0 && allowEffect) {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), BattleHealingLevel * 60, BattleHealingLevel));
                }


                if (DivineInterventionLevel > 0 && player.prevHealth - ammount <= 1) {
                    player.heal(5);
                    int x, y, z;
                    if (player.getBedLocation(0) != null) {
                        x = player.getBedLocation(0).posX;
                        y = player.getBedLocation(0).posY;
                        z = player.getBedLocation(0).posZ;
                    } else {
                        ChunkCoordinates coords = GlobalVariables.server.worldServerForDimension(0).getSpawnPoint();
                        x = coords.posX;
                        y = coords.posY;
                        z = coords.posZ;
                    }
                    if (player.dimension != 0) TeleportHelper.teleportEntityToDimension(player, x, y, z, 0);
                    else player.playerNetServerHandler.setPlayerLocation(x, y, z, 90, 0);
                    Map<Integer, Integer> enchs = EnchantmentHelper.getEnchantments(ArmourChest);
                    enchs.remove((int) enchantments.get("DivineIntervention").id);
                    if (DivineInterventionLevel > 1) enchs.put((int) enchantments.get("DivineIntervention").id, DivineInterventionLevel - 1);
                    EnchantmentHelper.setEnchantments(enchs, ArmourChest);
                }

                if (ExplosiveDischarge > 0) {
                    player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 2f * ExplosiveDischarge, false);
                }
            }

            if (ArmourHelm != null && ArmourHelm.hasTagCompound() && ArmourHelm.isItemEnchanted()) {
                if (isEnabled("AdrenalineBoost"))
                    AdrenalineBoostLevel = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("AdrenalineBoost").id, ArmourHelm);

                if (isEnabled("WitherProtection"))
                    WitherProt = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("WitherProtection").id, ArmourHelm);

                if (WitherProt > 0 && event.source.damageType.equalsIgnoreCase("wither")) {
                    event.ammount = 0;
                }

                if(AdrenalineBoostLevel > 0 && allowEffect) {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 60, AdrenalineBoostLevel/2));
                    player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), 60, AdrenalineBoostLevel/2));
                    player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), 60, AdrenalineBoostLevel/2));
                    player.addPotionEffect(new PotionEffect(Potion.jump.getId(), 60, AdrenalineBoostLevel/2));
                    player.addPotionEffect(new PotionEffect(Potion.resistance.getId(), 60, AdrenalineBoostLevel/2));
                }
            }
        }

        if (event.source.getSourceOfDamage() instanceof EntityPlayerMP && ((EntityPlayerMP) event.source.getSourceOfDamage()).getHeldItem() != null && ((EntityPlayerMP) event.source.getSourceOfDamage()).getHeldItem().hasTagCompound() && ((EntityPlayerMP) event.source.getSourceOfDamage()).getHeldItem().isItemEnchanted()) {
            EntityPlayerMP player = (EntityPlayerMP)event.source.getSourceOfDamage();
            ItemStack weapon = player.getHeldItem();
            LinkedHashMap<Enchantment, Integer> enchants = new LinkedHashMap<>();
            LinkedHashMap<Integer, Integer> enchs = (LinkedHashMap<Integer, Integer>) getEnchantments(weapon);
            enchs.forEach((x, y) -> enchants.put(Enchantment.enchantmentsList[x], y));

            float damage = event.ammount;
            EntityLivingBase victim = event.entityLiving;
            if (enchants.containsKey(enchantmentsList[enchantments.get("LifeSteal").id])) {
                player.heal(damage/10 * enchants.get(enchantmentsList[enchantments.get("LifeSteal").id]));
            }

            if (enchants.containsKey(enchantmentsList[enchantments.get("Piercing").id])) {
                victim.attackEntityFrom(new DamageSource("Piercing").setDamageBypassesArmor().setDamageAllowedInCreativeMode()
                        .setDamageIsAbsolute(), damage * (PiercingPercent * enchants.get(enchantmentsList[enchantments.get("Piercing").id])));
            }

            if (enchants.containsKey(enchantmentsList[enchantments.get("Vorpal").id])) {
                victim.attackEntityFrom(new DamageSource("Vorpal").setDamageBypassesArmor().setDamageAllowedInCreativeMode().setDamageIsAbsolute(),
                        enchants.get(enchantmentsList[enchantments.get("Vorpal").id]) * 0.35f * damage);
            }

            if (enchants.containsKey(enchantmentsList[enchantments.get("BloodRazor").id])) {
                event.ammount += (victim.getMaxHealth() * (0.05f * enchants.get(enchantmentsList[enchantments.get("BloodRazor").id])));
            }

            if (enchants.containsKey(enchantmentsList[enchantments.get("SCurse").id])) {
                int SCurseLevel = enchants.get(enchantmentsList[enchantments.get("SCurse").id]);
                victim.attackEntityFrom(new DamageSource("scurse").setDamageBypassesArmor().setDamageAllowedInCreativeMode().setDamageIsAbsolute(), damage * (0.2f * SCurseLevel));
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 120 * SCurseLevel, SCurseLevel, true));
                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 120, Math.round(SCurseLevel /3), true));
                player.addPotionEffect(new PotionEffect(Potion.weakness.getId(), 120 * SCurseLevel, SCurseLevel, true));
            }

            if (enchants.containsKey(enchantmentsList[enchantments.get("VoidTouch").id])) {
                short voidLevel = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("VoidTouch").id, weapon);
                if (voidLevel > 0) {
                    victim.attackEntityFrom(new DamageSource("Void").setDamageBypassesArmor().setDamageAllowedInCreativeMode().setDamageIsAbsolute(), damage * (0.2f * voidLevel));
                }
            }

            if (enchants.containsKey(enchantmentsList[enchantments.get("OverCharge").id]) && weapon.getTagCompound().getInteger("Charge") > 0) {
                int OverCharge = enchants.get(enchantmentsList[enchantments.get("OverCharge").id]);
                int storedCharge = weapon.getTagCompound().getInteger("Charge");
                if (OverCharge > 0) {
                    List<EntityLivingBase> list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AABBUtils.getAreaBoundingBox((int)Math.round(player.posX), (int)Math.round(player.posY), (int)Math.round(player.posZ), OverCharge*5));
                    int ndamage = (int) ((damage * 0.3f * ((list.size()) + (storedCharge/15)) / list.size()));
                    list.stream().filter(liv -> liv != player).forEach(liv -> liv.attackEntityFrom(new DamageSource("OverCharge").setDamageIsAbsolute().setDamageAllowedInCreativeMode(), ndamage));
                    weapon.getTagCompound().setInteger("Charge", 0);
                    Map<Integer, Integer> enchas = EnchantmentHelper.getEnchantments(weapon);
                    if (overChargeDecays) enchas.remove((int) enchantments.get("OverCharge").id);
                    if (OverCharge > 1 && overChargeDecays) enchas.put((int) enchantments.get("OverCharge").id, OverCharge - 1);
                    EnchantmentHelper.setEnchantments(enchas, weapon);
                }
            }
        }
    }



    @SubscribeEvent
    public void LivingDeathEvent(LivingDeathEvent event) {
        Entity deadent = event.entity;
        if (deadent instanceof EntityLivingBase && event.source.getSourceOfDamage() instanceof EntityPlayerMP && (!((EntityPlayerMP) event.source.getSourceOfDamage()).getDisplayName().contains("[")) && !(event.source.getSourceOfDamage() instanceof FakePlayer)){
            EntityPlayerMP Attacker = (EntityPlayerMP) event.source.getSourceOfDamage();
            ItemStack item;
            if (Attacker.getHeldItem() != null && (Attacker.getHeldItem().getItem() instanceof ItemSword || Attacker.getHeldItem().getItem() instanceof ItemAxe)) item = Attacker.getHeldItem();
            else return;

            if (item.hasTagCompound() && item.isItemEnchanted()){
                short vampireLevel = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Vampirism").id, item);
                short examineLevel = (short) EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Examine").id, item);
                if (examineLevel > 0)
                    if (deadent instanceof EntityLiving) {
                        deadent.worldObj.spawnEntityInWorld(new EntityXPOrb(deadent.worldObj, deadent.posX, deadent.posY + 1, deadent.posZ, examineLevel));
                    }

                if (vampireLevel > 0) {
                    if (deadent instanceof EntityAnimal)
                        Attacker.getFoodStats().addStats(1, 0.3F);
                    else if (deadent instanceof EntityPlayerMP)
                        Attacker.getFoodStats().addStats(10, 0.5F);
                    else if (deadent instanceof EntityVillager)
                        Attacker.getFoodStats().addStats(5, 0.5F);
                    else if (((EntityLivingBase) deadent).isEntityUndead())
                        Attacker.getFoodStats().addStats(0, 0);
                    else if (deadent instanceof EntitySlime)
                        Attacker.getFoodStats().addStats(1, 0.1F);
                    else if (deadent instanceof EntityEnderman)
                        Attacker.getFoodStats().addStats(2, 0.2F);
                    else if (deadent instanceof EntityMob)
                        Attacker.getFoodStats().addStats(3, 0.2F);

                    else Attacker.getFoodStats().addStats(1, 0.1F);
                }
            }
        }
    }

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
		if (!event.entityLiving.worldObj.isRemote && event.target != null && event.target instanceof EntityPlayer) {
            ItemStack Boots = ((EntityPlayer) event.target).inventory.armorItemInSlot(0);
            if (EnchantmentHelper.getEnchantmentLevel(enchantments.get("Stealth").id, Boots) > 0) {
                ((EntityLiving) event.entityLiving).setAttackTarget(null);
                ((EntityLiving) event.entityLiving).setRevengeTarget(null);
            }
		}	
	}
	
    public void handleAuraEvent(EntityPlayerMP player, List<Entity> ents, LinkedHashMap<Enchantment, Integer> sharedEnchants) {
        World world = player.getEntityWorld();
        for (Entity entity : ents) {
            if (entity instanceof EntityLivingBase && entity != player && !entity.isDead) {
                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("AuraDeadly").id])) {
                    if ((AurasAffectPlayers || !(entity instanceof EntityPlayer)) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal) && !((EntityLivingBase) entity).isPotionActive(Potion.wither)) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.wither.getId(), 100, 1, true));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("AuraDark").id])) {
                    if ((AurasAffectPlayers || !(entity instanceof EntityPlayer)) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal) && !((EntityLivingBase) entity).isPotionActive(Potion.blindness)) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.blindness.getId(), 100, 1, true));
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.confusion.getId(), 100, 1, true));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("AuraFiery").id])) {
                    if ((AurasAffectPlayers || !(entity instanceof EntityPlayer)) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal) && !entity.isBurning()) {
                        entity.setFire(sharedEnchants.get(enchantmentsList[enchantments.get("AuraFiery").id]) * 2);
                    }
                    findBlocksWithinAABB(world, AABBUtils.getAreaBoundingBox((int) player.posX, (int) player.posY, (int) player.posZ, sharedEnchants.get(enchantmentsList[enchantments.get("AuraFiery").id])), Arrays.asList(Blocks.ice, Blocks.snow_layer, Blocks.snow)).forEach(meltable -> {
                        if (world.getBlock(meltable.chunkPosX, meltable.chunkPosY, meltable.chunkPosZ) == Blocks.ice)
                            world.setBlock(meltable.chunkPosX, meltable.chunkPosY, meltable.chunkPosZ, Blocks.water);
                        else world.setBlockToAir(meltable.chunkPosX, meltable.chunkPosY, meltable.chunkPosZ);
                    });
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("AuraThick").id])) {
                    if ((AurasAffectPlayers || !(entity instanceof EntityPlayer)) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal) && !((EntityLivingBase) entity).isPotionActive(Potion.moveSlowdown)) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 100, 1, true));
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 100, 1, true));
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.weakness.getId(), 100, 1, true));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("AuraToxic").id])) {
                    if ((AurasAffectPlayers || !(entity instanceof EntityPlayer)) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal) && !((EntityLivingBase) entity).isPotionActive(Potion.poison)) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.getId(), 500, 1, true));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("HealingAura").id])) {
                    if ((!(entity instanceof EntityPlayer)) && !(entity instanceof EntityMob) && !((EntityLivingBase) entity).isPotionActive(Potion.regeneration)) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 500, Math.round(sharedEnchants.get(enchantmentsList[enchantments.get("GaiaAura").id])/4/3), true));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("IcyAura").id])) {
                    if ((AurasAffectPlayers || !(entity instanceof EntityPlayer)) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal) && !((EntityLivingBase) entity).isPotionActive(Potion.moveSlowdown)) {
                        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 500, 1, true));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("ChargedAura").id])) {
                    if (!(entity instanceof EntityPlayer) && !(entity instanceof EntityGolem) && !(entity instanceof EntityAnimal)) {
                        world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ));
                    }
                }

                if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("RepulsiveAura").id])) {
                    if (!(entity instanceof EntityAnimal || entity instanceof EntityVillager || entity instanceof EntityGolem || entity instanceof EntityPlayer)) {
                        double motionX = player.posX - entity.posX;
                        double motionY = player.boundingBox.minY + player.height - entity.posY;
                        double motionZ = player.posZ - entity.posZ;
                        entity.setVelocity(-motionX / 8, -motionY / 8, -motionZ / 8);
                    }
                }
            }

            if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("GaiaAura").id])) {
                int ran = world.rand.nextInt(Math.round(250 / (GaiasAuraSpeed * sharedEnchants.get(enchantmentsList[enchantments.get("GaiaAura").id]))));
                if (ran == 0) {
                    List<ChunkPosition> crops = getCropsWithinAABB(player.worldObj, AABBUtils.getAreaBoundingBox((short) Math.round(player.posX), (short) Math.round(player.posY), (short) Math.round(player.posZ), sharedEnchants.get(enchantmentsList[enchantments.get("GaiaAura").id])/4));
                    for (ChunkPosition pos : crops)
                        player.worldObj.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ).updateTick(player.worldObj, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, new Random());
                }
            }

            if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("IcyAura").id])) {
                List<ChunkPosition> blocks = findBlocksWithinAABB(player.worldObj, AABBUtils.getAreaBoundingBox((short) Math.round(player.posX), (short) Math.round(player.posY), (short) Math.round(player.posZ), sharedEnchants.get(enchantmentsList[enchantments.get("GaiaAura").id])/4), Arrays.asList(Blocks.lava, Blocks.flowing_lava, Blocks.water, Blocks.fire));
                for (ChunkPosition pos : blocks) {
                    if (world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ) == Blocks.lava)
                        world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, Blocks.obsidian, 0, 3);
                    if (world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ) == Blocks.flowing_lava)
                        world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, Blocks.stone, 0, 3);
                    if (world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ) == Blocks.water)
                        world.setBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, Blocks.ice, 0, 3);
                    if (world.getBlock(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ) == Blocks.fire)
                        world.setBlockToAir(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
                }
            }

            if (sharedEnchants.keySet().contains(enchantmentsList[enchantments.get("AuraMagnetic").id])) {
                if (entity instanceof EntityItem) {
                    double motionX = player.posX - entity.posX;
                    double motionY = player.boundingBox.minY + player.height - entity.posY;
                    double motionZ = player.posZ - entity.posZ;
                    entity.setVelocity(motionX / 4, motionY / 4, motionZ / 4);
                } else if (entity instanceof EntityXPOrb) {
                    new PlayerPickupXpEvent(player, (EntityXPOrb) entity);
                }
            }
        }
    }
    //Generic Aura find block AABB
    private static List<ChunkPosition> findBlocksWithinAABB(World world, AxisAlignedBB box, List<Block> targets) {
        List<ChunkPosition> blocks = new ArrayList<>();

        for(int x = (int)box.minX; (double)x <= box.maxX; ++x) {
            for(int y = (int)box.minY; (double)y <= box.maxY; ++y) {
                for(int z = (int)box.minZ; (double)z <= box.maxZ; ++z) {
                    Block block = world.getBlock(x, y, z);
                    if(block != null && targets.contains(block))
                        blocks.add(new ChunkPosition(x, y, z));
                }
            }
        }
        return blocks;
    }
    //Specifically for Growth AOE Aura now supports any plants if can
    private static List<ChunkPosition> getCropsWithinAABB(World world, AxisAlignedBB box) {
        List<ChunkPosition> blocks = new ArrayList<>();

        for(int x = (int)box.minX; (double)x <= box.maxX; ++x) {
            for(int y = (int)box.minY; (double)y <= box.maxY; ++y) {
                for(int z = (int)box.minZ; (double)z <= box.maxZ; ++z) {
                    Block block = world.getBlock(x, y, z);
                    if(block != null && (block instanceof IGrowable || block instanceof BlockReed || block instanceof BlockCactus))
                        blocks.add(new ChunkPosition(x, y, z));
                }
            }
        }
        return blocks;
    }

    private LinkedHashMap<Boolean, Item> hasFood(EntityPlayerMP player) {
        LinkedHashMap<Boolean, Item> meh = new LinkedHashMap<>();
        for (ItemStack item : player.inventory.mainInventory)
            if (item != null && item.getItem() instanceof ItemFood)
                meh.put(true, item.getItem());
        return meh;
    }

    private void RepairItems(EntityPlayerMP player){
        ItemStack Inv;
        ItemStack Armor;
        long tmp = 0;
        for(int j = 0; j < 36; j++){
            Inv = player.inventory.getStackInSlot(j);
            if (Inv != null && Inv.isItemStackDamageable() && Inv.hasTagCompound() && Inv.isItemEnchanted() && Inv.getMaxDamage() != Inv.getItemDamageForDisplay()){
                int a = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Repair").id, Inv);
                int b = Inv.getItemDamageForDisplay() - a;
                if (Inv.getItemDamageForDisplay() > 0 && tmp >= Inv.getItemDamageForDisplay()) {
                    Inv.setItemDamage(b);
                }
            }
        }
        for(int j = 0; j < 4; j++){
            Armor = player.getCurrentArmor(j);
            if (Armor != null && Armor.isItemStackDamageable() && Armor.hasTagCompound() && Armor.isItemEnchanted()){
                int c = EnchantmentHelper.getEnchantmentLevel((int) enchantments.get("Repair").id, Armor);
                int d = Armor.getItemDamageForDisplay() - c;
                if (Armor.getItemDamageForDisplay() > 0 && (tmp >= Armor.getItemDamageForDisplay())) {
                    Armor.setItemDamage(d);
                }
            }
        }
    }

    public static void chargeItem(EntityPlayer player) {
        if (player.getHeldItem() != null && player.getHeldItem().isItemEnchanted() && player.experienceLevel > 0) {
            player.addExperienceLevel(-1);

            if (player.getHeldItem().hasTagCompound()) {
                player.getHeldItem().getTagCompound().setLong("Charge", player.getHeldItem().getTagCompound().getLong("Charge") + EnchantingUtils.xpFromLevel(player.experienceLevel));
            } else {
                NBTTagCompound tg = new NBTTagCompound();
                tg.setLong("Charge", EnchantingUtils.xpFromLevel(player.experienceLevel));
                player.getHeldItem().setTagCompound(tg);
            }
        }
    }

    public static void flash(EntityPlayerMP player) {
        if (player.getCurrentArmor(0) != null && player.getCurrentArmor(0).isItemEnchanted()) {
            int FlashLevel = EnchantmentHelper.getEnchantmentLevel((int) Configurations.enchantments.get("FlashStep").id, player.getCurrentArmor(0));
            if (FlashLevel > 0) {
                World world = player.worldObj;
                Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
                Vec3 vec31 = player.getLook(1.0f);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * 200, vec31.yCoord * 200, vec31.zCoord * 200);
                MovingObjectPosition rayTrace = GlobalVariables.server.worldServerForDimension(player.dimension).rayTraceBlocks(vec3, vec32);
                if (rayTrace != null) {
                    if (rayTrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK ) {
                        for (int i = 0; i < 5; i++) {
                            if (world.getBlock(rayTrace.blockX, rayTrace.blockY + i, rayTrace.blockZ) == Blocks.air) {
                                player.playerNetServerHandler.setPlayerLocation(rayTrace.blockX, rayTrace.blockY + i, rayTrace.blockZ, player.cameraYaw, player.cameraPitch);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    //Generic check if enchant is enabled function incase of it changing just change one part and done not go and replace all
    private static boolean isEnabled(String name) {
        return enchantments.get(name).enabled;
    }
}

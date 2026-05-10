package com.example.resurrectionhoe;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ResurrectionHoe extends JavaPlugin implements Listener {

    private NamespacedKey hoeKey;
    private NamespacedKey fertilizerKey;
    private ConcurrentHashMap<String, Boolean> resurrectionFarmlands;
    private String buildTime;
    private boolean enableFarmlandMoisture;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();

        hoeKey = new NamespacedKey(this, "resurrection_hoe");
        fertilizerKey = new NamespacedKey(this, "golden_fertilizer");
        resurrectionFarmlands = new ConcurrentHashMap<>();
        buildTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        getLogger().info("ResurrectionHoe 插件已启用！");
        getServer().getPluginManager().registerEvents(this, this);

        getCommand("resurrectionhoe").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
                    showInfo(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("resurrectionhoe.reload")) {
                        sender.sendMessage("§c你没有权限执行此指令！");
                        return true;
                    }
                    reloadConfig();
                    reloadConfigValues();
                    sender.sendMessage("§a插件配置已重载！");
                    return true;
                }
                if (args[0].equalsIgnoreCase("hoe")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("只有玩家才能使用此指令！");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (!player.hasPermission("resurrectionhoe.hoe")) {
                        player.sendMessage("§c你没有权限执行此指令！");
                        return true;
                    }
                    ItemStack hoe = createResurrectionHoe();
                    player.getInventory().addItem(hoe);
                    player.sendMessage("§a你获得了复生之锄！");
                    return true;
                }
                if (args[0].equalsIgnoreCase("fertilizer")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("只有玩家才能使用此指令！");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (!player.hasPermission("resurrectionhoe.fertilizer")) {
                        player.sendMessage("§c你没有权限执行此指令！");
                        return true;
                    }
                    ItemStack fertilizer = createGoldenFertilizer();
                    player.getInventory().addItem(fertilizer);
                    player.sendMessage("§a你获得了金坷垃！");
                    return true;
                }
                sender.sendMessage("§c未知指令！使用 /rh 查看帮助");
                return true;
            }
        });

        getCommand("resurrectionhoe").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
                List<String> completions = new ArrayList<>();
                if (args.length == 1) {
                    completions.add("info");
                    if (sender.hasPermission("resurrectionhoe.reload")) {
                        completions.add("reload");
                    }
                    if (sender.hasPermission("resurrectionhoe.hoe")) {
                        completions.add("hoe");
                    }
                    if (sender.hasPermission("resurrectionhoe.fertilizer")) {
                        completions.add("fertilizer");
                    }
                }
                return completions;
            }
        });
    }

    private void reloadConfigValues() {
        FileConfiguration config = getConfig();
        enableFarmlandMoisture = config.getBoolean("enable-farmland-moisture", true);
        getLogger().info("耕地不干涸功能: " + (enableFarmlandMoisture ? "启用" : "禁用"));
    }

    private void showInfo(CommandSender sender) {
        sender.sendMessage("§6═══════════════════════════════");
        sender.sendMessage("§e  ResurrectionHoe §f- §a复生之锄");
        sender.sendMessage("");
        sender.sendMessage("§b  版本: §f" + getDescription().getVersion());
        sender.sendMessage("§b  构建时间: §f" + buildTime);
        sender.sendMessage("§b  耕地不干涸: §f" + (enableFarmlandMoisture ? "§a启用" : "§c禁用"));
        sender.sendMessage("");
        sender.sendMessage("§7  指令:");
        sender.sendMessage("§f  /rh info §7- §f显示插件信息");
        sender.sendMessage("§f  /rh reload §7- §f重载插件（OP）");
        sender.sendMessage("§f  /rh hoe §7- §f获得复生之锄（OP）");
        sender.sendMessage("§f  /rh fertilizer §7- §f获得金坷垃（OP）");
        sender.sendMessage("§6═══════════════════════════════");
    }

    private ItemStack createResurrectionHoe() {
        ItemStack hoe = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta meta = hoe.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + "复生之锄");
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "右键地面使用");
        lore.add(ChatColor.BLUE + "可在5×5范围内耕地");
        if (enableFarmlandMoisture) {
            lore.add(ChatColor.DARK_PURPLE + "生成的耕地不会干涸");
        }
        meta.setLore(lore);
        
        meta.addEnchant(Enchantment.FORTUNE, 3, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(hoeKey, PersistentDataType.BYTE, (byte) 1);
        
        hoe.setItemMeta(meta);
        return hoe;
    }

    private ItemStack createGoldenFertilizer() {
        ItemStack fertilizer = new ItemStack(Material.BONE_MEAL);
        ItemMeta meta = fertilizer.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + "金坷垃");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "右键作物使用",
                ChatColor.GREEN + "可催熟5×3×5范围内的作物"
        ));
        meta.addEnchant(Enchantment.EFFICIENCY, 5, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(fertilizerKey, PersistentDataType.BYTE, (byte) 1);
        
        fertilizer.setItemMeta(meta);
        return fertilizer;
    }

    private boolean isResurrectionHoe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(hoeKey, PersistentDataType.BYTE);
    }

    private boolean isGoldenFertilizer(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(fertilizerKey, PersistentDataType.BYTE);
    }

    private String getBlockKey(Block block) {
        Location loc = block.getLocation();
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private boolean isResurrectionFarmland(Block block) {
        if (block.getType() != Material.FARMLAND) return false;
        return resurrectionFarmlands.containsKey(getBlockKey(block));
    }

    private void markResurrectionFarmland(Block block) {
        if (enableFarmlandMoisture) {
            resurrectionFarmlands.put(getBlockKey(block), true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (isResurrectionHoe(item)) {
            Block clickedBlock = event.getClickedBlock();
            Location center = clickedBlock.getLocation();

            List<Block> farmlandsToMoisturize = new ArrayList<>();

            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -2; z <= 2; z++) {
                        Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        Material type = block.getType();
                        
                        if (type == Material.DIRT || type == Material.GRASS_BLOCK || type == Material.DIRT_PATH || 
                            type == Material.SOUL_SAND || type == Material.SOUL_SOIL) {
                            Block above = block.getRelative(0, 1, 0);
                            Material aboveType = above.getType();
                            
                            if (aboveType == Material.AIR || isFlowerOrGrass(aboveType)) {
                                if (isFlowerOrGrass(aboveType)) {
                                    spawnBlockBreakParticles(above);
                                    above.breakNaturally();
                                }
                                block.setType(Material.FARMLAND);
                                markResurrectionFarmland(block);
                                farmlandsToMoisturize.add(block);
                            }
                        } else if (type == Material.FARMLAND) {
                            markResurrectionFarmland(block);
                            farmlandsToMoisturize.add(block);
                        }
                    }
                }
            }

            if (enableFarmlandMoisture) {
                for (Block farmland : farmlandsToMoisturize) {
                    setFarmlandMoisture(farmland, 7);
                }
            }
            event.setCancelled(true);
        } else if (isGoldenFertilizer(item)) {
            Block clickedBlock = event.getClickedBlock();
            Location center = clickedBlock.getLocation();
            int grown = 0;

            for (int x = -2; x <= 2; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -2; z <= 2; z++) {
                        Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        if (isGrowable(block)) {
                            growCrop(block);
                            spawnGrowthParticles(block);
                            grown++;
                        }
                    }
                }
            }

            if (grown > 0) {
                item.setAmount(item.getAmount() - 1);
                player.getInventory().setItemInMainHand(item);
            }
            event.setCancelled(true);
        }
    }

    private boolean isGrowable(Block block) {
        Material type = block.getType();
        return type.name().endsWith("_CROP") || 
               type == Material.CACTUS || 
               type == Material.SUGAR_CANE ||
               type == Material.COCOA ||
               type.name().endsWith("_SAPLING") ||
               type == Material.MELON_STEM ||
               type == Material.PUMPKIN_STEM;
    }

    private void growCrop(Block block) {
        BlockState state = block.getState();
        if (state instanceof Ageable) {
            Ageable ageable = (Ageable) state;
            ageable.setAge(ageable.getMaximumAge());
            state.update();
        } else {
            try {
                Object blockData = block.getBlockData();
                Class<?> blockDataClass = blockData.getClass();
                java.lang.reflect.Method setAgeMethod = null;
                java.lang.reflect.Method getMaximumAgeMethod = null;
                
                for (java.lang.reflect.Method m : blockDataClass.getMethods()) {
                    if (m.getName().equals("setAge") && m.getParameterCount() == 1) {
                        setAgeMethod = m;
                    }
                    if (m.getName().equals("getMaximumAge") && m.getParameterCount() == 0) {
                        getMaximumAgeMethod = m;
                    }
                }
                
                if (setAgeMethod != null && getMaximumAgeMethod != null) {
                    int maxAge = (int) getMaximumAgeMethod.invoke(blockData);
                    setAgeMethod.invoke(blockData, maxAge);
                    block.setBlockData((org.bukkit.block.data.BlockData) blockData);
                }
            } catch (Exception e) {
                getLogger().warning("无法催熟作物: " + e.getMessage());
            }
        }
    }

    private void spawnGrowthParticles(Block block) {
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().spawnParticle(Particle.HEART, loc, 10, 0.3, 0.3, 0.3, 0.1);
        block.getWorld().playSound(loc, Sound.BLOCK_GRASS_PLACE, 0.5f, 1.0f);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!enableFarmlandMoisture) return;
        
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND && isResurrectionFarmland(block)) {
            setFarmlandMoisture(block, 7);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            resurrectionFarmlands.remove(getBlockKey(block));
        }
    }

    private void setFarmlandMoisture(Block farmland, int moisture) {
        try {
            Object blockData = farmland.getBlockData();
            Class<?> blockDataClass = blockData.getClass();
            java.lang.reflect.Method setMoistureMethod = null;
            for (java.lang.reflect.Method m : blockDataClass.getMethods()) {
                if (m.getName().equals("setMoisture") && m.getParameterCount() == 1) {
                    setMoistureMethod = m;
                    break;
                }
            }
            if (setMoistureMethod != null) {
                setMoistureMethod.invoke(blockData, moisture);
                farmland.setBlockData((org.bukkit.block.data.BlockData) blockData);
            }
        } catch (Exception e) {
            try {
                Object state = farmland.getState();
                Class<?> stateClass = state.getClass();
                java.lang.reflect.Method setMoistureMethod = null;
                for (java.lang.reflect.Method m : stateClass.getMethods()) {
                    if (m.getName().equals("setMoisture") && m.getParameterCount() == 1) {
                        setMoistureMethod = m;
                        break;
                    }
                }
                if (setMoistureMethod != null) {
                    setMoistureMethod.invoke(state, moisture);
                    ((BlockState) state).update();
                }
            } catch (Exception ex) {
                getLogger().warning("无法设置耕地湿润度: " + ex.getMessage());
            }
        }
    }

    private void spawnBlockBreakParticles(Block block) {
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().spawnParticle(Particle.BLOCK, loc, 20, 0.3, 0.3, 0.3, 0.05, block.getBlockData());
        block.getWorld().playSound(loc, Sound.BLOCK_GRASS_BREAK, 1.0f, 1.0f);
    }

    private boolean isFlowerOrGrass(Material material) {
        return material.name().endsWith("_FLOWER") || 
               material.name().endsWith("_GRASS") ||
               material == Material.SHORT_GRASS ||
               material == Material.TALL_GRASS ||
               material == Material.FERN ||
               material == Material.LARGE_FERN ||
               material == Material.DEAD_BUSH ||
               material == Material.CORNFLOWER ||
               material == Material.LILY_OF_THE_VALLEY ||
               material == Material.WITHER_ROSE ||
               material == Material.POPPY ||
               material == Material.DANDELION ||
               material == Material.BLUE_ORCHID ||
               material == Material.ALLIUM ||
               material == Material.AZURE_BLUET ||
               material == Material.RED_TULIP ||
               material == Material.ORANGE_TULIP ||
               material == Material.WHITE_TULIP ||
               material == Material.PINK_TULIP ||
               material == Material.OXEYE_DAISY ||
               material == Material.SUNFLOWER ||
               material == Material.LILAC ||
               material == Material.ROSE_BUSH ||
               material == Material.PEONY ||
               material == Material.SWEET_BERRY_BUSH ||
               material.name().endsWith("_MUSHROOM") ||
               material == Material.PINK_PETALS ||
               material == Material.MOSS_BLOCK ||
               material == Material.TORCHFLOWER;
    }

    @Override
    public void onDisable() {
        getLogger().info("ResurrectionHoe 插件已禁用！");
    }
}
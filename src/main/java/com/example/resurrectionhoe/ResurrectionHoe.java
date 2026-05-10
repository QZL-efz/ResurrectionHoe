package com.example.resurrectionhoe;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class ResurrectionHoe extends JavaPlugin implements Listener {

    private NamespacedKey hoeKey;
    private NamespacedKey axeKey;
    private NamespacedKey shovelKey;
    private NamespacedKey scytheKey;
    private ConcurrentHashMap<String, Boolean> resurrectionFarmlands;
    private ConcurrentHashMap<UUID, Long> axeCooldowns;
    private boolean enableFarmlandMoisture;
    private double scytheDropChance;
    private List<LootItem> scytheLootTable;
    private Random random;

    private static class LootItem {
        Material material;
        int amount;
        int weight;

        LootItem(Material material, int amount, int weight) {
            this.material = material;
            this.amount = amount;
            this.weight = weight;
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();

        hoeKey = new NamespacedKey(this, "resurrection_hoe");
        axeKey = new NamespacedKey(this, "regenerative_axe");
        shovelKey = new NamespacedKey(this, "path_shovel");
        scytheKey = new NamespacedKey(this, "seekers_scythe");
        resurrectionFarmlands = new ConcurrentHashMap<>();
        axeCooldowns = new ConcurrentHashMap<>();
        random = new Random();
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
                if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("resurrectionhoe.give")) {
                        sender.sendMessage("§c你没有权限执行此指令！");
                        return true;
                    }
                    if (args.length < 2) {
                        sender.sendMessage("§c用法: /rh give <hoe|axe|shovel|scythe> [玩家名] [数量]");
                        return true;
                    }
                    String itemType = args[1].toLowerCase();
                    if (!itemType.equals("hoe") && !itemType.equals("axe") && !itemType.equals("shovel") && !itemType.equals("scythe")) {
                        sender.sendMessage("§c无效的物品类型！请使用 hoe, axe, shovel 或 scythe");
                        return true;
                    }
                    Player targetPlayer;
                    int amount = 1;
                    if (args.length >= 3) {
                        targetPlayer = getServer().getPlayer(args[2]);
                        if (targetPlayer == null) {
                            try {
                                amount = Integer.parseInt(args[2]);
                                if (amount <= 0) {
                                    sender.sendMessage("§c数量必须为正整数！");
                                    return true;
                                }
                                if (!(sender instanceof Player)) {
                                    sender.sendMessage("§c请指定接收玩家！");
                                    return true;
                                }
                                targetPlayer = (Player) sender;
                            } catch (NumberFormatException e) {
                                sender.sendMessage("§c无法找到玩家: " + args[2]);
                                return true;
                            }
                        } else {
                            if (args.length >= 4) {
                                try {
                                    amount = Integer.parseInt(args[3]);
                                    if (amount <= 0) {
                                        sender.sendMessage("§c数量必须为正整数！");
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§c请输入有效的正整数！");
                                    return true;
                                }
                            }
                        }
                    } else {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage("§c请指定接收玩家！");
                            return true;
                        }
                        targetPlayer = (Player) sender;
                    }

                    ItemStack item;
                    String itemName;
                    if (itemType.equals("hoe")) {
                        item = createResurrectionHoe();
                        itemName = "复生之锄";
                    } else if (itemType.equals("axe")) {
                        item = createRegenerativeAxe();
                        itemName = "可再生石斧";
                    } else if (itemType.equals("scythe")) {
                        item = createSeekersScythe();
                        itemName = "寻觅之镰";
                    } else {
                        item = createPathShovel();
                        itemName = "引路之锹";
                    }
                    item.setAmount(amount);
                    targetPlayer.getInventory().addItem(item);

                    if (targetPlayer.equals(sender)) {
                        sender.sendMessage("§a你获得了 " + amount + " 个" + itemName + "！");
                    } else {
                        sender.sendMessage("§a已给 " + targetPlayer.getName() + " " + amount + " 个" + itemName + "！");
                        targetPlayer.sendMessage("§a你获得了 " + amount + " 个" + itemName + "！");
                    }
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
                    if (sender.hasPermission("resurrectionhoe.give")) {
                        completions.add("give");
                    }
                } else if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("resurrectionhoe.give")) {
                    completions.add("hoe");
                    completions.add("axe");
                    completions.add("shovel");
                    completions.add("scythe");
                } else if (args.length == 3 && args[0].equalsIgnoreCase("give") && sender.hasPermission("resurrectionhoe.give")) {
                    for (Player player : getServer().getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                }
                return completions;
            }
        });
    }

    private void reloadConfigValues() {
        FileConfiguration config = getConfig();
        enableFarmlandMoisture = config.getBoolean("enable-farmland-moisture", true);
        scytheDropChance = config.getDouble("seekers-scythe.drop-chance", 0.2);
        loadLootTable(config);
        getLogger().info("耕地不干涸功能: " + (enableFarmlandMoisture ? "启用" : "禁用"));
    }

    private void loadLootTable(FileConfiguration config) {
        scytheLootTable = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("seekers-scythe");
        if (section != null) {
            List<String> lootStrings = section.getStringList("loot-table");
            for (String line : lootStrings) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    try {
                        Material material = Material.valueOf(parts[0].toUpperCase());
                        int amount = Integer.parseInt(parts[1]);
                        int weight = Integer.parseInt(parts[2]);
                        if (amount > 0 && weight > 0) {
                            scytheLootTable.add(new LootItem(material, amount, weight));
                        }
                    } catch (IllegalArgumentException e) {
                        getLogger().warning("无法解析战利品表行: " + line);
                    }
                }
            }
        }
        if (scytheLootTable.isEmpty()) {
            getLogger().warning("战利品表为空或无效，使用默认战利品表");
            addDefaultLootTable();
        }
    }

    private void addDefaultLootTable() {
        scytheLootTable.add(new LootItem(Material.WHEAT_SEEDS, 1, 200));
        scytheLootTable.add(new LootItem(Material.BEETROOT_SEEDS, 1, 200));
        scytheLootTable.add(new LootItem(Material.CARROT, 1, 200));
        scytheLootTable.add(new LootItem(Material.POTATO, 1, 200));
        scytheLootTable.add(new LootItem(Material.MELON_SEEDS, 1, 150));
        scytheLootTable.add(new LootItem(Material.PUMPKIN_SEEDS, 1, 150));
        scytheLootTable.add(new LootItem(Material.GOLDEN_CARROT, 1, 10));
        scytheLootTable.add(new LootItem(Material.GOLDEN_APPLE, 1, 5));
        scytheLootTable.add(new LootItem(Material.ENCHANTED_GOLDEN_APPLE, 1, 1));
    }

    private LootItem getRandomLoot() {
        if (scytheLootTable.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (LootItem item : scytheLootTable) {
            totalWeight += item.weight;
        }
        int rand = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (LootItem item : scytheLootTable) {
            currentWeight += item.weight;
            if (rand < currentWeight) {
                return item;
            }
        }
        return scytheLootTable.get(0);
    }

    private void showInfo(CommandSender sender) {
        sender.sendMessage("§6═══════════════════════════════");
        sender.sendMessage("§e  ResurrectionHoe §f- §a复生之锄");
        sender.sendMessage("");
        sender.sendMessage("§b  版本: §f" + getDescription().getVersion());
        sender.sendMessage("§b  耕地不干涸: §f" + (enableFarmlandMoisture ? "§a启用" : "§c禁用"));
        sender.sendMessage("");
        sender.sendMessage("§7  指令:");
        sender.sendMessage("§f  /rh info §7- §f显示插件信息");
        sender.sendMessage("§f  /rh reload §7- §f重载插件（OP）");
        sender.sendMessage("§f  /rh give <hoe|axe|shovel|scythe> [玩家名] [数量] §7- §f获取物品（OP）");
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

    private ItemStack createRegenerativeAxe() {
        ItemStack axe = new ItemStack(Material.STONE_AXE);
        ItemMeta meta = axe.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "可再生石斧");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "破坏树木时可连锁破坏");
        lore.add(ChatColor.GRAY + "破坏后会在根部生成树苗");
        lore.add(ChatColor.GRAY + "对巨型树木会使斧头断裂！");
        lore.add(ChatColor.DARK_RED + "5秒冷却");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.EFFICIENCY, 3, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(axeKey, PersistentDataType.BYTE, (byte) 1);

        axe.setItemMeta(meta);
        return axe;
    }

    private ItemStack createSeekersScythe() {
        ItemStack scythe = new ItemStack(Material.STONE_HOE);
        ItemMeta meta = scythe.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "寻觅之镰");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "破坏草及其变种时");
        lore.add(ChatColor.GRAY + "有概率获得额外掉落物！");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(scytheKey, PersistentDataType.BYTE, (byte) 1);

        scythe.setItemMeta(meta);
        return scythe;
    }

    private ItemStack createPathShovel() {
        ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta meta = shovel.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "引路之锹");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "右键方块使用");
        lore.add(ChatColor.BLUE + "可在3×3×3范围内生成土径");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(shovelKey, PersistentDataType.BYTE, (byte) 1);

        shovel.setItemMeta(meta);
        return shovel;
    }

    private boolean isResurrectionHoe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(hoeKey, PersistentDataType.BYTE);
    }

    private boolean isRegenerativeAxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(axeKey, PersistentDataType.BYTE);
    }

    private boolean isSeekersScythe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(scytheKey, PersistentDataType.BYTE);
    }

    private boolean isPathShovel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(shovelKey, PersistentDataType.BYTE);
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

    private boolean canPlayerModifyBlock(Player player, Block block) {
        if (player.hasPermission("resurrectionhoe.bypass-protection")) {
            return true;
        }

        org.bukkit.event.block.BlockBreakEvent breakEvent = new org.bukkit.event.block.BlockBreakEvent(block, player);
        getServer().getPluginManager().callEvent(breakEvent);
        if (breakEvent.isCancelled()) {
            return false;
        }

        org.bukkit.inventory.ItemStack itemInHand = player.getInventory().getItemInMainHand();
        org.bukkit.event.block.BlockPlaceEvent placeEvent = new org.bukkit.event.block.BlockPlaceEvent(block, block.getState(), block.getRelative(org.bukkit.block.BlockFace.DOWN), itemInHand, player, true);
        getServer().getPluginManager().callEvent(placeEvent);
        return !placeEvent.isCancelled();
    }

    private boolean isGrassVariant(Material material) {
        return material == Material.SHORT_GRASS ||
               material == Material.TALL_GRASS ||
               material == Material.FERN ||
               material == Material.LARGE_FERN;
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

                            if ((aboveType == Material.AIR || isFlowerOrGrass(aboveType)) && canPlayerModifyBlock(player, block)) {
                                if (isFlowerOrGrass(aboveType) && canPlayerModifyBlock(player, above)) {
                                    spawnBlockBreakParticles(above);
                                    above.breakNaturally();
                                } else if (isFlowerOrGrass(aboveType)) {
                                    continue;
                                }
                                block.setType(Material.FARMLAND);
                                markResurrectionFarmland(block);
                                farmlandsToMoisturize.add(block);
                            }
                        } else if (type == Material.FARMLAND && canPlayerModifyBlock(player, block)) {
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
        } else if (isRegenerativeAxe(item)) {
            UUID playerUUID = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            Long lastUse = axeCooldowns.get(playerUUID);
            if (lastUse != null && currentTime - lastUse < 5000) {
                player.sendMessage("§c斧头还在冷却中（" + (5 - (currentTime - lastUse) / 1000) + "秒）！");
                return;
            }

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            Material type = clickedBlock.getType();
            if (!isLog(type)) {
                return;
            }

            if (!canPlayerModifyBlock(player, clickedBlock)) {
                return;
            }

            Set<Block> treeBlocks = findTreeBlocks(clickedBlock);

            if (treeBlocks.isEmpty()) {
                return;
            }

            boolean isGiantTree = isGiantTreeTrunk(clickedBlock);

            if (isGiantTree) {
                player.sendMessage("§c你的斧头'啪'地一声断了！");
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                axeCooldowns.put(playerUUID, currentTime);
                event.setCancelled(true);
                return;
            }

            Material saplingType = getSaplingType(type);
            Block lowestLog = findLowestLogBlock(treeBlocks);

            for (Block block : treeBlocks) {
                if (canPlayerModifyBlock(player, block)) {
                    spawnBlockBreakParticles(block);
                    block.breakNaturally();
                }
            }

            if (lowestLog != null && saplingType != null) {
                boolean hasLeaves = false;
                for (Block block : treeBlocks) {
                    if (isLeaves(block.getType())) {
                        hasLeaves = true;
                        break;
                    }
                }

                if (hasLeaves) {
                    lowestLog.getWorld().dropItemNaturally(lowestLog.getLocation(), new ItemStack(saplingType));
                }
            }

            axeCooldowns.put(playerUUID, currentTime);
            event.setCancelled(true);
        } else if (isPathShovel(item)) {
            Block clickedBlock = event.getClickedBlock();
            Location center = clickedBlock.getLocation();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        Material type = block.getType();

                        if (type == Material.GRASS_BLOCK || type == Material.DIRT ||
                            type == Material.COARSE_DIRT ||
                            type == Material.MYCELIUM ||
                            type == Material.PODZOL ||
                            type == Material.ROOTED_DIRT) {
                            Block above = block.getRelative(0, 1, 0);
                            Material aboveType = above.getType();

                            if ((aboveType == Material.AIR || isFlowerOrGrass(aboveType)) && canPlayerModifyBlock(player, block)) {
                                if (isFlowerOrGrass(aboveType) && canPlayerModifyBlock(player, above)) {
                                    spawnBlockBreakParticles(above);
                                    above.breakNaturally();
                                } else if (isFlowerOrGrass(aboveType)) {
                                    continue;
                                }
                                block.setType(Material.DIRT_PATH);
                            }
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            resurrectionFarmlands.remove(getBlockKey(block));
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (isSeekersScythe(item) && isGrassVariant(block.getType())) {
            if (random.nextDouble() < scytheDropChance) {
                LootItem loot = getRandomLoot();
                if (loot != null) {
                    ItemStack drop = new ItemStack(loot.material, loot.amount);
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                    block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, block.getLocation().add(0.5, 0.5, 0.5), 5);
                    block.getWorld().playSound(block.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3f, 1.2f);
                }
            }
        }
    }

    private boolean isLog(Material material) {
        return material.name().endsWith("_LOG") ||
               material == Material.OAK_WOOD ||
               material == Material.BIRCH_WOOD ||
               material == Material.SPRUCE_WOOD ||
               material == Material.JUNGLE_WOOD ||
               material == Material.ACACIA_WOOD ||
               material == Material.DARK_OAK_WOOD ||
               material == Material.MANGROVE_WOOD ||
               material == Material.CHERRY_WOOD ||
               material == Material.BAMBOO_BLOCK ||
               material == Material.STRIPPED_OAK_LOG ||
               material == Material.STRIPPED_BIRCH_LOG ||
               material == Material.STRIPPED_SPRUCE_LOG ||
               material == Material.STRIPPED_JUNGLE_LOG ||
               material == Material.STRIPPED_ACACIA_LOG ||
               material == Material.STRIPPED_DARK_OAK_LOG ||
               material == Material.STRIPPED_MANGROVE_LOG ||
               material == Material.STRIPPED_CHERRY_LOG ||
               material == Material.STRIPPED_BAMBOO_BLOCK ||
               material == Material.OAK_LOG ||
               material == Material.BIRCH_LOG ||
               material == Material.SPRUCE_LOG ||
               material == Material.JUNGLE_LOG ||
               material == Material.ACACIA_LOG ||
               material == Material.DARK_OAK_LOG ||
               material == Material.MANGROVE_LOG ||
               material == Material.CHERRY_LOG;
    }

    private boolean isLeaves(Material material) {
        return material.name().endsWith("_LEAVES") ||
               material == Material.OAK_LEAVES ||
               material == Material.BIRCH_LEAVES ||
               material == Material.SPRUCE_LEAVES ||
               material == Material.JUNGLE_LEAVES ||
               material == Material.ACACIA_LEAVES ||
               material == Material.DARK_OAK_LEAVES ||
               material == Material.MANGROVE_LEAVES ||
               material == Material.CHERRY_LEAVES ||
               material == Material.AZALEA_LEAVES ||
               material == Material.FLOWERING_AZALEA_LEAVES;
    }

    private boolean isNaturalTreeBlock(Block block) {
        Material type = block.getType();
        if (!isLog(type) && !isLeaves(type)) {
            return false;
        }

        Block below = block.getRelative(0, -1, 0);
        if (isLog(below.getType()) || below.getType() == Material.GRASS_BLOCK || below.getType() == Material.DIRT) {
            return true;
        }

        return false;
    }

    private Set<Block> findTreeBlocks(Block startBlock) {
        Set<Block> treeBlocks = new HashSet<>();
        Set<Block> visited = new HashSet<>();
        List<Block> toCheck = new ArrayList<>();

        toCheck.add(startBlock);

        while (!toCheck.isEmpty()) {
            Block current = toCheck.remove(0);
            if (visited.contains(current)) continue;
            visited.add(current);

            Material type = current.getType();
            if (!isLog(type) && !isLeaves(type)) continue;

            if (!isNaturalTreeBlock(current)) continue;

            treeBlocks.add(current);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        Block neighbor = current.getRelative(dx, dy, dz);
                        if (!visited.contains(neighbor)) {
                            toCheck.add(neighbor);
                        }
                    }
                }
            }
        }

        return treeBlocks;
    }

    private boolean isGiantTreeTrunk(Block block) {
        int x = block.getX();
        int z = block.getZ();
        int y = block.getY();

        Block block1 = block.getWorld().getBlockAt(x, y, z);
        Block block2 = block.getWorld().getBlockAt(x + 1, y, z);
        Block block3 = block.getWorld().getBlockAt(x, y, z + 1);
        Block block4 = block.getWorld().getBlockAt(x + 1, y, z + 1);

        boolean b1 = isLog(block1.getType());
        boolean b2 = isLog(block2.getType());
        boolean b3 = isLog(block3.getType());
        boolean b4 = isLog(block4.getType());

        return (b1 && b2 && b3 && b4);
    }

    private Block findLowestLogBlock(Set<Block> treeBlocks) {
        Block lowest = null;
        int lowestY = Integer.MAX_VALUE;

        for (Block block : treeBlocks) {
            if (isLog(block.getType())) {
                int y = block.getY();
                if (y < lowestY) {
                    lowestY = y;
                    lowest = block;
                }
            }
        }

        return lowest;
    }

    private Material getSaplingType(Material logType) {
        String name = logType.name();
        if (name.contains("OAK")) return Material.OAK_SAPLING;
        if (name.contains("BIRCH")) return Material.BIRCH_SAPLING;
        if (name.contains("SPRUCE")) return Material.SPRUCE_SAPLING;
        if (name.contains("JUNGLE")) return Material.JUNGLE_SAPLING;
        if (name.contains("ACACIA")) return Material.ACACIA_SAPLING;
        if (name.contains("DARK_OAK")) return Material.DARK_OAK_SAPLING;
        if (name.contains("MANGROVE")) return Material.MANGROVE_PROPAGULE;
        if (name.contains("CHERRY")) return Material.CHERRY_SAPLING;
        if (name.contains("BAMBOO")) return Material.BAMBOO;
        return Material.OAK_SAPLING;
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (!enableFarmlandMoisture) return;

        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND && isResurrectionFarmland(block)) {
            setFarmlandMoisture(block, 7);
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
        block.getWorld().playSound(loc, Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
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

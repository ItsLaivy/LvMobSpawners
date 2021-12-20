package codes.laivy.lvmobspawners.configuration;

import codes.laivy.lvmobspawners.utils.CustomPlayerHeads;
import codes.laivy.lvmobspawners.utils.GuiUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static codes.laivy.lvmobspawners.LvMobSpawners.getPlugin;

public class SpawnerConfiguration {

    public static final Map<String, SpawnerConfiguration> configurations = new LinkedHashMap<>();

    public static void loadConfigSpawners() {
        File file = new File(getPlugin().getDataFolder(), "spawners.yml");

        YamlConfiguration yaml;
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            yaml = YamlConfiguration.loadConfiguration(file);

            yaml.options().header(
                    "--------------- LvMobSpawners --------------- #\n" +
                    "Created By: Daniel Richard\n" +
                    "- Discord: DanielZinh#7616\n" +
                    "- Mail: dnlfg.contato@gmail.com\n" +
                    "--------------- LvMobSpawners --------------- #\n" +
                    "\n"
            );

            yaml.set("spawners.COW.entity type", "COW");
            yaml.set("spawners.COW.spawner display name", "&7Spawner de &eVaca");
            yaml.set("spawners.COW.spawner head texture", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19");
            yaml.set("spawners.COW.spawner head large", true);
            yaml.set("spawners.COW.spawner head speed", 0.15D);
            yaml.set("spawners.COW.default configurations.spawn time", 25);
            yaml.set("spawners.COW.default configurations.default mobs in area", 10);
            yaml.set("spawners.COW.default configurations.mobs in area (max upgrades)", 15);
            yaml.set("spawners.COW.default configurations.default xp booster", 1);
            yaml.set("spawners.COW.default configurations.xp booster (max upgrades)", 3);
            yaml.set("spawners.COW.default configurations.xp booster (per level up)", 0.1D);
            yaml.set("spawners.COW.prices (needs vault).spawner name", "&7Spawner de &eVaca");
            yaml.set("spawners.COW.prices (needs vault).spawner lore", Arrays.asList("&7Spawner para o nascimento", "&7de vacas..."));
            yaml.set("spawners.COW.prices (needs vault).spawner price (in shop)", 3000D);
            yaml.set("spawners.COW.prices (needs vault).spawner spawn time upgrade price (* level)", 10000D);
            yaml.set("spawners.COW.prices (needs vault).spawner mobs in area upgrade price (* level)", 1000D);
            yaml.set("spawners.COW.prices (needs vault).spawner xp booster upgrade price (* level)", 1000D);
            yaml.set("spawners.COW.stack.enabled (more performance)", true);
            yaml.set("spawners.COW.stack.stack radius", 15);
            yaml.set("spawners.COW.stack.max stack", 100);
            yaml.set("spawners.COW.stack.stack numbers format", " &e{stack_size}x");
            yaml.set("spawners.COW.spawner mobs area check radius (blocks)", 100);

            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        yaml = YamlConfiguration.loadConfiguration(file);

        if (yaml.contains("spawners")) {
            for (String p : yaml.getConfigurationSection("spawners").getKeys(false)) {
                configurations.put(p, new SpawnerConfiguration(p));
            }
        }
    }

    public static Map<String, SpawnerConfiguration> getConfigurations() {
        return configurations;
    }

    //
    // Objetos
    //

    private final String ID;

    private EntityType entityType;
    private String spawnerDisplayName;
    private String spawnerHeadTexture;
    private boolean spawnerHeadLarge;
    private double spawnerHeadSpeed;

    private long maxSpawnTime;
    private long mobsInAreaDefault;
    private long mobsInAreaMaxUpgrades;

    private long XPBoosterDefault;
    private long XPBoosterMaxUpgrades;
    private double XPBoosterPerLevelUp;

    private String shopSpawnerName;
    private List<String> shopSpawnerLore;
    private double shopSpawnerPrice;

    private double shopSpawnerIntervalUpgrade;
    private double shopSpawnerMobsInAreaUpgrade;
    private double shopSpawnerXPBoosterUpgrade;

    private boolean stackable;
    private long stackRadius;
    private long maxStack;
    private String stackMessage;

    private long mobsAreaCheckRadius;

    public SpawnerConfiguration(String ID) {
        this.ID = ID;

        try {
            YamlConfiguration c = YamlConfiguration.loadConfiguration(new File(getPlugin().getDataFolder(), "spawners.yml"));
            String i = "spawners." + ID + ".";

            try {
                entityType = EntityType.valueOf(c.getString(i + "entity type"));
            } catch (IllegalArgumentException ignore) {
                getPlugin().broadcastColoredMessage("&cNão foi possível identificar nenhuma entidade com esse ID '" + c.getString(i + "entity type") + "'");
            }

            spawnerDisplayName = replace(c.getString(i + "spawner display name"));
            spawnerHeadTexture = c.getString(i + "spawner head texture");
            spawnerHeadLarge = c.getBoolean(i + "spawner head large");
            spawnerHeadSpeed = c.getDouble(i + "spawner head speed");

            maxSpawnTime = c.getInt(i + "default configurations.spawn time");
            mobsInAreaDefault = c.getInt(i + "default configurations.default mobs in area");
            mobsInAreaMaxUpgrades = c.getInt(i + "default configurations.mobs in area (max upgrades)");
            XPBoosterDefault = c.getInt(i + "default configurations.default xp booster");
            XPBoosterMaxUpgrades = c.getInt(i + "default configurations.xp booster (max upgrades)");
            XPBoosterPerLevelUp = c.getDouble(i + "default configurations.xp booster (per level up)");

            shopSpawnerName = replace(c.getString(i + "prices (needs vault).spawner name"));

            List<String> lore = new LinkedList<>();
            for (Object a : c.getList(i + "prices (needs vault).spawner lore")) {
                lore.add(replace(a.toString()));
            }

            shopSpawnerLore = lore;
            shopSpawnerPrice = c.getDouble(i + "prices (needs vault).spawner price (in shop)");

            shopSpawnerIntervalUpgrade = c.getDouble(i + "prices (needs vault).spawner spawn time upgrade price (* level)");
            shopSpawnerMobsInAreaUpgrade = c.getDouble(i + "prices (needs vault).spawner mobs in area upgrade price (* level)");
            shopSpawnerXPBoosterUpgrade = c.getDouble(i + "prices (needs vault).spawner xp booster upgrade price (* level)");

            stackable = c.getBoolean(i + "stack.enabled (more performance)");
            stackRadius = c.getInt(i + "stack.stack radius");
            maxStack = c.getInt(i + "stack.max stack");
            stackMessage = replace(c.getString(i + "stack.stack numbers format"));

            mobsAreaCheckRadius = c.getInt(i + "spawner mobs area check radius (blocks)");

            if (maxSpawnTime < 1) {
                throw new IllegalArgumentException("O intervalo de nascimento dos monstros não pode ser menor do que um");
            }
            if (spawnerHeadSpeed <= 0) {
                throw new IllegalArgumentException("A velocidade de rotação da cabeça do spawner precisa ser maior que zero!");
            }

            getConfigurations().put(ID, this);
        } catch (NullPointerException e) {
            e.printStackTrace();
            getPlugin().broadcastColoredMessage("&cNão foi possível carregar o spawner '" + ID + "', você tem certeza que inseriu todos os parametros corretamente?");
        }
    }

    public static String replace(String str) {
        return str.replace("&", "§");
    }

    public ItemStack getItemStack() {
        return new GuiUtils().createItem(CustomPlayerHeads.create(spawnerHeadTexture), shopSpawnerName, shopSpawnerLore);
    }

    public String getID() {
        return ID;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getSpawnerDisplayName() {
        return spawnerDisplayName;
    }

    public String getSpawnerHeadTexture() {
        return spawnerHeadTexture;
    }

    public boolean isSpawnerHeadLarge() {
        return spawnerHeadLarge;
    }

    public double getSpawnerHeadSpeed() {
        return spawnerHeadSpeed;
    }

    public long getMaxSpawnTime() {
        return maxSpawnTime;
    }

    public long getMobsInAreaDefault() {
        return mobsInAreaDefault;
    }

    public long getMobsInAreaMaxUpgrades() {
        return mobsInAreaMaxUpgrades;
    }

    public long getXPBoosterDefault() {
        return XPBoosterDefault;
    }

    public long getXPBoosterMaxUpgrades() {
        return XPBoosterMaxUpgrades;
    }

    public double getXPBoosterPerLevelUp() {
        return XPBoosterPerLevelUp;
    }

    public String getShopSpawnerName() {
        return shopSpawnerName;
    }

    public List<String> getShopSpawnerLore() {
        return shopSpawnerLore;
    }

    public double getShopSpawnerPrice() {
        return shopSpawnerPrice;
    }

    public double getShopSpawnerIntervalUpgrade() {
        return shopSpawnerIntervalUpgrade;
    }

    public double getShopSpawnerMobsInAreaUpgrade() {
        return shopSpawnerMobsInAreaUpgrade;
    }

    public double getShopSpawnerXPBoosterUpgrade() {
        return shopSpawnerXPBoosterUpgrade;
    }

    public boolean isStackable() {
        return stackable;
    }

    public long getStackRadius() {
        return stackRadius;
    }

    public long getMaxStack() {
        return maxStack;
    }

    public String getStackMessage() {
        return stackMessage;
    }

    public long getMobsAreaCheckRadius() {
        return mobsAreaCheckRadius;
    }
}

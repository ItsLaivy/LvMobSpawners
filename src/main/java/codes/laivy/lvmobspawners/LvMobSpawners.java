package codes.laivy.lvmobspawners;

import codes.laivy.lvmobspawners.commands.ShopCommand;
import codes.laivy.lvmobspawners.configuration.SpawnerConfiguration;
import codes.laivy.lvmobspawners.developers.Inventorys;
import codes.laivy.lvmobspawners.listeners.BukkitEvents;
import codes.laivy.lvmobspawners.spawners.CustomSpawner;
import codes.laivy.lvmobspawners.spawners.SpawnerTexture;
import codes.laivy.lvmobspawners.utils.Cuboid;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class LvMobSpawners extends JavaPlugin {

    private Economy econ = null;

    private final Map<Long, CustomSpawner> spawners = new LinkedHashMap<>();
    private final Map<Cuboid, SpawnerTexture> spawnersTextures = new LinkedHashMap<>();

    public static LvMobSpawners getPlugin() {
        return LvMobSpawners.getPlugin(LvMobSpawners.class);
    }

    @Override
    public void onEnable() {
        setupEconomy();
        SpawnerConfiguration.loadConfigSpawners();

        getServer().getPluginManager().registerEvents(new BukkitEvents(), this);
        getServer().getPluginManager().registerEvents(new ShopCommand(), this);
        getCommand("spawners").setExecutor(new ShopCommand());

        File file = new File(getDataFolder(), "spawners.data");
        if (file.exists()) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (String e : yaml.getConfigurationSection("data").getKeys(false)) {
                String i = "data." + e + ".";

                if (SpawnerConfiguration.getConfigurations().containsKey(yaml.getString(i + "configuration id"))) {
                    Location l = new Location(Bukkit.getWorld(yaml.getString(i + "location.world")), yaml.getDouble(i + "location.x"), yaml.getDouble(i + "location.y"), yaml.getDouble(i + "location.z"));
                    UUID owner = UUID.fromString(yaml.getString(i + "owner"));
                    SpawnerConfiguration c = SpawnerConfiguration.getConfigurations().get(yaml.getString(i + "configuration id"));

                    long spawnIntervalLevel = yaml.getLong(i + "spawn interval level");
                    long mobsInAreaLevel = yaml.getLong(i + "mobs in area level");
                    long xpBoosterLevel = yaml.getLong(i + "xp booster level");

                    new CustomSpawner(l, c, owner, spawnIntervalLevel, mobsInAreaLevel, xpBoosterLevel);
                } else {
                    broadcastColoredMessage("&cNão foi possível inserir o spawner ID '" + e + "' pois o arquivo de configuração dela não pôde ser encontrado.");
                }
            }
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Map.Entry<Cuboid, SpawnerTexture> map : spawnersTextures.entrySet()) {
                    if (map.getKey().contains(player.getLocation())) {
                        SpawnerTexture texture = map.getValue();
                        SpawnerConfiguration c = texture.getConfiguration();
                        texture.getStand().setHeadPose(texture.getStand().getHeadPose().add(0, texture.getConfiguration().getSpawnerHeadSpeed(), 0));
                        long check = c.getMobsAreaCheckRadius();

                        map.getValue().updateHologram();

                        for (Map.Entry<Long, CustomSpawner> m : texture.getSpawners().entrySet()) {
                            CustomSpawner spawner = m.getValue();

                            if (spawner.canSpawn(0.5)) {
                                int row = 0;
                                for (Entity e : spawner.getLocation().getWorld().getNearbyEntities(spawner.getLocation(), check, check, check)) {
                                    if (e.getType() == c.getEntityType()) {
                                        row++;
                                    }
                                }

                                if (row <= (spawner.getMaxMobsInAreaLevel() + c.getMobsInAreaDefault())) {
                                    spawner.getLocation().getWorld().spawn(spawner.getLocation(), c.getEntityType().getEntityClass());
                                }
                            }
                        }
                    }
                }

                if (player.getOpenInventory().getTopInventory().getName().contains("M.Spawner ")) {
                    long id = Long.parseLong(player.getOpenInventory().getTopInventory().getName().replace("M.Spawner ", ""));
                    player.openInventory(Inventorys.getSpawnerInventory(getSpawners().get(id)));
                }
            }
        }, 5, 5);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onDisable() {
        File file = new File(getDataFolder(), "spawners.data");
        if (file.exists()) file.delete();

        if (spawners.size() > 0) {
            file.getParentFile().mkdirs();

            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            for (Map.Entry<Long, CustomSpawner> map : spawners.entrySet()) {
                CustomSpawner s = map.getValue();
                SpawnerTexture t = s.getTexture();

                t.getStand().remove();

                yaml.set("data." + s.getID() + ".owner", t.getOwner().toString());
                yaml.set("data." + s.getID() + ".configuration id", t.getConfiguration().getID());
                yaml.set("data." + s.getID() + ".spawn interval level", s.getSpawnIntervalLevel());
                yaml.set("data." + s.getID() + ".mobs in area level", s.getMaxMobsInAreaLevel());
                yaml.set("data." + s.getID() + ".xp booster level", s.getMobsXPBoosterLevel());
                yaml.set("data." + s.getID() + ".location.world", s.getLocation().getWorld().getName());
                yaml.set("data." + s.getID() + ".location.x", s.getLocation().getX());
                yaml.set("data." + s.getID() + ".location.y", s.getLocation().getY());
                yaml.set("data." + s.getID() + ".location.z", s.getLocation().getZ());
            }

            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Economy getEconomy() {
        return econ;
    }
    private void setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            broadcastColoredMessage("&cNão foi possível linkar com o Vault pois o plugin não foi encontrado, algumas funcionalidades como lojas e upgrades não funcionarão.");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            broadcastColoredMessage("&cNão foi possível linkar com o Vault pois não foi encontrado nenhum plugin de economia");
            return;
        }
        econ = rsp.getProvider();
    }

    public void broadcastColoredMessage(String message) {
        getServer().getConsoleSender().sendMessage("§8[§6" + getDescription().getName() + "§8]§7" + " " + ChatColor.translateAlternateColorCodes('&', message));
    }

    //
    // Getters
    //

    public Map<Long, CustomSpawner> getSpawners() {
        return spawners;
    }
    public Map<Cuboid, SpawnerTexture> getSpawnersTextures() {
        return spawnersTextures;
    }

}

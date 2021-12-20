package codes.laivy.lvmobspawners.spawners;

import codes.laivy.lvmobspawners.configuration.SpawnerConfiguration;
import codes.laivy.lvmobspawners.developers.events.SpawnerPlaceEvent;
import codes.laivy.lvmobspawners.developers.events.SpawnerStackEvent;
import codes.laivy.lvmobspawners.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

import static codes.laivy.lvmobspawners.LvMobSpawners.getPlugin;

public class CustomSpawner {

    private SpawnerTexture texture = null;

    private Location location;

    private long spawnIntervalLevel;
    private long maxMobsInAreaLevel;
    private long mobsXPBoosterLevel;

    private final long id;
    private double spawnSec;

    public CustomSpawner(Location location, SpawnerConfiguration configuration, UUID owner, long spawnIntervalLevel, long maxMobsInAreaLevel, long mobsXPBoosterLevel) {
        this.location = location;
        this.id = getPlugin().getSpawners().size();

        if (configuration.isStackable()) {
            for (Map.Entry<Cuboid, SpawnerTexture> map : getPlugin().getSpawnersTextures().entrySet()) {
                if (map.getKey().contains(this.location)) {
                    if (map.getValue().getOwner().equals(owner)) {
                        if (map.getValue().getConfiguration() == configuration) {
                            if (map.getValue().getSpawners().size() < map.getValue().getConfiguration().getMaxStack()) {
                                SpawnerStackEvent event = new SpawnerStackEvent(this, map.getValue());
                                Bukkit.getPluginManager().callEvent(event);

                                if (event.isCancelled()) {
                                    break;
                                }

                                this.location = map.getValue().getLocation();
                                this.texture = map.getValue();
                                break;
                            }

                        }
                    }
                }
            }
        }

        boolean textureCreated = this.texture == null;
        if (this.texture == null) {
            this.texture = new SpawnerTexture(location, configuration, owner);
        }

        SpawnerPlaceEvent event = new SpawnerPlaceEvent(this, texture);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            if (textureCreated) this.texture.remove();
            return;
        }

        this.texture.getSpawners().put(getID(), this);
        this.spawnIntervalLevel = spawnIntervalLevel;
        this.maxMobsInAreaLevel = maxMobsInAreaLevel;
        this.mobsXPBoosterLevel = mobsXPBoosterLevel;

        this.spawnSec = configuration.getMaxSpawnTime() - spawnIntervalLevel;
        getPlugin().getSpawners().put(this.id, this);
    }

    public boolean canSpawn(double remove) {
        this.spawnSec -= remove;

        if (this.spawnSec <= 0) {
            this.spawnSec = getTexture().getConfiguration().getMaxSpawnTime() - spawnIntervalLevel;
            return true;
        } else return false;
    }

    public SpawnerTexture getTexture() {
        return texture;
    }

    public Location getLocation() {
        return location;
    }

    public long getID() {
        return id;
    }

    public double getSpawnSec() {
        return spawnSec;
    }

    //
    // Level
    //

    public long getSpawnIntervalLevel() {
        return spawnIntervalLevel;
    }
    public long getMaxMobsInAreaLevel() {
        return maxMobsInAreaLevel;
    }
    public long getMobsXPBoosterLevel() {
        return mobsXPBoosterLevel;
    }
    public void setSpawnIntervalLevel(long spawnIntervalLevel) {
        this.spawnIntervalLevel = spawnIntervalLevel;
    }
    public void setMaxMobsInAreaLevel(long maxMobsInAreaLevel) {
        this.maxMobsInAreaLevel = maxMobsInAreaLevel;
    }
    public void setMobsXPBoosterLevel(long mobsXPBoosterLevel) {
        this.mobsXPBoosterLevel = mobsXPBoosterLevel;
    }

    //
    // Level
    //

    public boolean remove() {
        CustomSpawner r1 = texture.getSpawners().remove(getID());
        CustomSpawner r2 = getPlugin().getSpawners().remove(getID());

        texture.updateHologram();

        if (r1 == null) getPlugin().broadcastColoredMessage("r1 cannot be executed");
        if (r2 == null) getPlugin().broadcastColoredMessage("r2 cannot be executed");

        boolean r3 = true;
        if (texture.getSpawners().size() == 0) {
            texture.getStand().remove();
            r3 = texture.remove();

            if (!r3) getPlugin().broadcastColoredMessage("r3 cannot be executed");
        }

        return r1 != null && r2 != null && r3;
    }

}

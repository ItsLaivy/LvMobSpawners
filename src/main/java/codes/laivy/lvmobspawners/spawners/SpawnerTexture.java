package codes.laivy.lvmobspawners.spawners;

import codes.laivy.lvmobspawners.configuration.SpawnerConfiguration;
import codes.laivy.lvmobspawners.utils.Cuboid;
import codes.laivy.lvmobspawners.utils.CustomPlayerHeads;
import codes.laivy.lvmobspawners.utils.GuiUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static codes.laivy.lvmobspawners.LvMobSpawners.getPlugin;

public class SpawnerTexture {

    private final SpawnerConfiguration configuration;
    private final UUID owner;
    private final Location location;

    private final Cuboid cuboid;

    private final ArmorStand stand;

    private final Map<Long, CustomSpawner> spawners = new LinkedHashMap<>();

    public SpawnerTexture(Location l, SpawnerConfiguration configuration, UUID owner) {
        this.configuration = configuration;
        this.location = l;
        this.owner = owner;

        long sRadius = configuration.getStackRadius();
        Location l0 = new Location(l.getWorld(), l.getX() + 0.5, l.getY() - (configuration.isSpawnerHeadLarge() ? 1 : 0), l.getZ() + 0.5);
        Location l1 = new Location(l.getWorld(), l.getX() + sRadius, l.getY() + sRadius, l.getZ() + sRadius);
        Location l2 = new Location(l.getWorld(), l.getX() - sRadius, l.getY() - sRadius, l.getZ() - sRadius);
        cuboid = new Cuboid(l1, l2);

        stand = l.getWorld().spawn(l0, ArmorStand.class);
        stand.setSmall(!configuration.isSpawnerHeadLarge());
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(configuration.getSpawnerDisplayName().replace("&", "§"));
        stand.setHelmet(new GuiUtils().createItem(CustomPlayerHeads.create(configuration.getSpawnerHeadTexture()), "SPTEXTURE " + hashCode()));

        getPlugin().getSpawnersTextures().put(cuboid, this);
    }

    public ArmorStand getStand() {
        return getSpawnerStand(this);
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public SpawnerConfiguration getConfiguration() {
        return configuration;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public Map<Long, CustomSpawner> getSpawners() {
        return spawners;
    }

    public void updateHologram() {
        getStand().setCustomName(configuration.getSpawnerDisplayName().replace("&", "§") + (spawners.size() > 1 ? configuration.getStackMessage().replace("{stack_size}", spawners.size() + "") : ""));
    }

    public boolean remove() {
        for (Map.Entry<Long, CustomSpawner> spawner : new LinkedList<>(spawners.entrySet())) {
            spawner.getValue().remove();
        }

        stand.remove();
        return getPlugin().getSpawnersTextures().remove(cuboid) != null;
    }

    private static ArmorStand getSpawnerStand(SpawnerTexture spawnerTexture) {
        for (Entity e : spawnerTexture.location.getWorld().getNearbyEntities(spawnerTexture.location, 1, 1, 1)) {
            if (e instanceof ArmorStand) {
                ItemStack h = ((ArmorStand) e).getHelmet();
                if (h.hasItemMeta()) {
                    if (h.getItemMeta().hasDisplayName()) {
                        if (h.getItemMeta().getDisplayName().equals("SPTEXTURE " + spawnerTexture.hashCode())) {
                            return (ArmorStand) e;
                        }
                    }
                }
            }
        }
        throw new NullPointerException();
    }

}

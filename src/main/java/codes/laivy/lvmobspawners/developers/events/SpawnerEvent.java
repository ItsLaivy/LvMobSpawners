package codes.laivy.lvmobspawners.developers.events;

import codes.laivy.lvmobspawners.spawners.CustomSpawner;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final CustomSpawner spawner;

    public SpawnerEvent(CustomSpawner spawner) {
        this.spawner = spawner;
    }

    public CustomSpawner getSpawner() {
        return spawner;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
}

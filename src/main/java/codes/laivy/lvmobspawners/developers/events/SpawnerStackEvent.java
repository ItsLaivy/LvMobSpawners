package codes.laivy.lvmobspawners.developers.events;

import codes.laivy.lvmobspawners.spawners.CustomSpawner;
import codes.laivy.lvmobspawners.spawners.SpawnerTexture;
import org.bukkit.event.Cancellable;

public class SpawnerStackEvent extends SpawnerEvent implements Cancellable {

    private final SpawnerTexture texture;
    private boolean cancelled;

    public SpawnerStackEvent(CustomSpawner spawner, SpawnerTexture texture) {
        super(spawner);
        this.texture = texture;
    }

    public SpawnerTexture getTexture() {
        return texture;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}

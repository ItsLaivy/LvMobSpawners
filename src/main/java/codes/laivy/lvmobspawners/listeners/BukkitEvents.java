package codes.laivy.lvmobspawners.listeners;

import codes.laivy.lvmobspawners.configuration.SpawnerConfiguration;
import codes.laivy.lvmobspawners.spawners.CustomSpawner;
import codes.laivy.lvmobspawners.spawners.SpawnerTexture;
import codes.laivy.lvmobspawners.utils.Cuboid;
import codes.laivy.lvmobspawners.utils.CustomPlayerHeads;
import codes.laivy.lvmobspawners.utils.GuiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static codes.laivy.lvmobspawners.developers.Inventorys.*;
import static codes.laivy.lvmobspawners.LvMobSpawners.getPlugin;

@SuppressWarnings("unused")
public class BukkitEvents implements Listener {

    private static final GuiUtils g = new GuiUtils();

    @EventHandler
    private void clickEvent(InventoryClickEvent e) {
        if (g.checkName(e, e.getInventory(), e.getClickedInventory(), true, "M.Spawner ")) {
            Player p = (Player) e.getWhoClicked();
            long id = Long.parseLong(e.getClickedInventory().getName().replace("M.Spawner ", ""));
            CustomSpawner spawner = getPlugin().getSpawners().get(id);
            SpawnerTexture t = spawner.getTexture();
            SpawnerConfiguration c = t.getConfiguration();

            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            if (e.getSlot() == 11 || e.getSlot() == 13) {
                if (e.getCurrentItem().getType() == Material.BARRIER) {
                    removeFor((Player) e.getWhoClicked(), spawner);
                } else if (e.getCurrentItem().getType() == Material.WATCH) {
                    if (spawner.getSpawnIntervalLevel() + 1 >= c.getMaxSpawnTime()) {
                        e.getWhoClicked().sendMessage("§cEsse atributo já está no seu nível máximo!");
                        return;
                    }

                    double value = c.getShopSpawnerIntervalUpgrade() * (spawner.getSpawnIntervalLevel() + 1);
                    if (getPlugin().getEconomy().has(p, value)) {
                        getPlugin().getEconomy().withdrawPlayer(p, value);
                        p.sendMessage("§7Atributo §aIntervalo de Nascimento §7melhorado com sucesso.");
                        spawner.setSpawnIntervalLevel(spawner.getSpawnIntervalLevel() + 1);
                        e.getWhoClicked().openInventory(getSpawnerInventory(spawner));
                    } else {
                        p.sendMessage("§cVocê não possui dinheiro o suficiente, você precisa de R$" + value + " para comprar isso.");
                    }
                }
            } else if (e.getSlot() == 14) {
                double value = c.getShopSpawnerMobsInAreaUpgrade() * (spawner.getMaxMobsInAreaLevel() + 1);
                if (getPlugin().getEconomy().has(p, value)) {
                    getPlugin().getEconomy().withdrawPlayer(p, value);
                    p.sendMessage("§7Atributo §eMobs na Área §7melhorado com sucesso.");
                    spawner.setMaxMobsInAreaLevel(spawner.getMaxMobsInAreaLevel() + 1);
                    e.getWhoClicked().openInventory(getSpawnerInventory(spawner));
                } else {
                    p.sendMessage("§cVocê não possui dinheiro o suficiente, você precisa de R$" + value + " para comprar isso.");
                }
            } else if (e.getSlot() == 15) {
                e.getWhoClicked().sendMessage("§cAtributo em desenvolvimento...");
            }
        } else if (g.checkName(e, e.getInventory(), e.getClickedInventory(), true, "G.Spawner ")) {
            int hashCode = Integer.parseInt(e.getClickedInventory().getName().replace("G.Spawner ", ""));

            int page = 0;
            for (String l : e.getClickedInventory().getItem(27).getItemMeta().getLore()) {
                if (l.contains("§7Página atual: §f")) {
                    l = l.replace("§7Página atual: §f", "").replace(" ", "");
                    page = Integer.parseInt(l) - 1;
                    break;
                }
            }

            for (Map.Entry<Cuboid, SpawnerTexture> map : getPlugin().getSpawnersTextures().entrySet()) {
                SpawnerTexture t = map.getValue();
                if (t.hashCode() == hashCode) {
                    if (e.getSlot() == 35) {
                        e.getWhoClicked().openInventory(getSpawnerTextureInventory(t, page + 1));
                    } else if (e.getSlot() == 27) {
                        if (page > 0) {
                            e.getWhoClicked().openInventory(getSpawnerTextureInventory(t, page - 1));
                        }
                    } else if (e.getSlot() == 34) {
                        for (Map.Entry<Long, CustomSpawner> m : new LinkedHashSet<>(t.getSpawners().entrySet())) {
                            removeFor((Player) e.getWhoClicked(), m.getValue());
                        }
                    }else if (e.getSlot() >= 0 && e.getSlot() < 27 && e.getCurrentItem().getType() != Material.AIR) {
                        Long id = null;
                        for (String l : e.getCurrentItem().getItemMeta().getLore()) {
                            if (l.contains("§7ID: §6")) {
                                id = Long.parseLong(l.replace("§7ID: §6", "").replace(" ", ""));
                            }
                        }

                        if (id != null) {
                            if (map.getValue().getSpawners().containsKey(id)) {
                                e.getWhoClicked().openInventory(getSpawnerInventory(map.getValue().getSpawners().get(id)));
                                return;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private static void removeFor(Player player, CustomSpawner spawner) {
        SpawnerConfiguration c = spawner.getTexture().getConfiguration();

        if (spawner.remove()) {
            List<String> lore = new LinkedList<>(c.getShopSpawnerLore());
            lore.addAll(Arrays.asList(
                    "",
                    "§7Intervalo de Nascimento: §f" + spawner.getSpawnIntervalLevel() + " níveis",
                    "§7Max. mobs ao redor: §f" + spawner.getMaxMobsInAreaLevel() + " níveis",
                    "§7Booster XP (Mobs): §f" + spawner.getMobsXPBoosterLevel() + " níveis"
            ));

            ItemStack i = g.createItem(CustomPlayerHeads.create(c.getSpawnerHeadTexture()), c.getShopSpawnerName(), lore);
            spawner.getLocation().getWorld().dropItem(spawner.getLocation(), i);
            player.sendMessage("§cSpawner removido com sucesso...");

            player.closeInventory();
        } else {
            player.sendMessage("§cNão foi possível remover o spawner ID '" + spawner.getID() + "'... Contate um administrador.");
        }
    }

    @EventHandler
    private void standManipulate(PlayerArmorStandManipulateEvent e) {
        for (Map.Entry<Cuboid, SpawnerTexture> map : getPlugin().getSpawnersTextures().entrySet()) {
            SpawnerTexture spawner = map.getValue();
            if (map.getValue().getStand() == e.getRightClicked()) {

                if (spawner.getOwner().equals(e.getPlayer().getUniqueId())) {
                    e.getPlayer().openInventory(getSpawnerTextureInventory(spawner, 0));
                } else e.getPlayer().sendMessage("§cSomente o dono pode editar os spawners.");

                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    private void placeSpawner(BlockPlaceEvent e) {
        ItemStack i = e.getItemInHand();

        if (i != null) {
            if (i.hasItemMeta()) {
                if (i.getItemMeta().hasDisplayName()) {
                    for (Map.Entry<String, SpawnerConfiguration> map : SpawnerConfiguration.getConfigurations().entrySet()) {
                        if (map.getValue().getShopSpawnerName().replace("&", "§").equals(i.getItemMeta().getDisplayName())) {
                            SpawnerConfiguration c = map.getValue();

                            long spawnInterval = 0;
                            long mobsInArea = 0;
                            long xpBooster = 0;

                            if (i.getItemMeta().hasLore()) {
                                List<String> lore = i.getItemMeta().getLore();

                                for (String f : lore) {
                                    if (f.contains("§7Intervalo de Nascimento:")) {
                                        spawnInterval = Long.parseLong(f.replace("§7Intervalo de Nascimento: §f", "").replace(" níveis", "").replace(" ", ""));
                                    } else if (f.contains("§7Max. mobs ao redor:")) {
                                        mobsInArea = Long.parseLong(f.replace("§7Max. mobs ao redor: §f", "").replace(" níveis", "").replace(" ", ""));
                                    } else if (f.contains("§7Booster XP (Mobs):")) {
                                        xpBooster = Long.parseLong(f.replace("§7Booster XP (Mobs): §f", "").replace(" níveis", "").replace(" ", ""));
                                    }
                                }
                            }

                            new CustomSpawner(e.getBlock().getLocation(), c, e.getPlayer().getUniqueId(), spawnInterval, mobsInArea, xpBooster);
                            e.setCancelled(true);

                            if (i.getAmount() == 1) i = null;
                            else i.setAmount(i.getAmount() - 1);
                            e.getPlayer().setItemInHand(i);

                            break;
                        }
                    }
                }
            }
        }
    }
}
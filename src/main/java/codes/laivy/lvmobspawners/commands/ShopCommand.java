package codes.laivy.lvmobspawners.commands;

import codes.laivy.lvmobspawners.configuration.SpawnerConfiguration;
import codes.laivy.lvmobspawners.utils.GuiUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static codes.laivy.lvmobspawners.LvMobSpawners.getPlugin;
import static codes.laivy.lvmobspawners.developers.Inventorys.getSpawnersShopInventory;

@SuppressWarnings("unused")
public class ShopCommand implements CommandExecutor, Listener {

    private static final GuiUtils g = new GuiUtils();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("spawners")) {
            if (getPlugin().getEconomy() != null) {
                if (sender instanceof Player) {
                    ((Player) sender).openInventory(getSpawnersShopInventory(0));
                } else {
                    sender.sendMessage("§cEsse comando só pode ser executado por players");
                }
            } else {
                sender.sendMessage("§cA função de loja de spawners está desativada.");
            }
        }
        return true;
    }

    @EventHandler
    private void clickEvent(InventoryClickEvent e) {
        if (g.checkName(e, e.getInventory(), e.getClickedInventory(), false, "Loja de Spawners")) {
            Player p = (Player) e.getWhoClicked();

            if (e.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            if (e.getSlot() >= 0 && e.getSlot() < 27) {
                String id = null;
                for (String l : e.getCurrentItem().getItemMeta().getLore()) {
                    if (l.contains("§7ID:")) {
                        id = l.replace("§7ID: §6", "").replace(" ", "");
                    }
                }

                if (id != null) {
                    if (SpawnerConfiguration.getConfigurations().containsKey(id)) {
                        SpawnerConfiguration c = SpawnerConfiguration.configurations.get(id);

                        if (getPlugin().getEconomy().has(p, c.getShopSpawnerPrice())) {
                            getPlugin().getEconomy().withdrawPlayer(p, c.getShopSpawnerPrice());
                            p.getInventory().addItem(c.getItemStack());
                            p.sendMessage(c.getShopSpawnerName().replace("&", "§") + " §7comprado com sucesso!");
                        } else {
                            p.sendMessage("§cVocê precisa de R$" + c.getShopSpawnerPrice() + " para comprar esse spawner.");
                        }
                    }
                }
            } else {
                int page = 0;
                for (String l : e.getClickedInventory().getItem(27).getItemMeta().getLore()) {
                    if (l.contains("§7Página atual: §f")) {
                        l = l.replace("§7Página atual: §f", "").replace(" ", "");
                        page = Integer.parseInt(l) - 1;
                        break;
                    }
                }

                if (e.getSlot() == 35) {
                    e.getWhoClicked().openInventory(getSpawnersShopInventory(page + 1));
                } else if (e.getSlot() == 27) {
                    if (page > 0) {
                        e.getWhoClicked().openInventory(getSpawnersShopInventory(page - 1));
                    }
                }
            }
        }
    }

}

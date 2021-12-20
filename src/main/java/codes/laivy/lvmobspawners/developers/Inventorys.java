package codes.laivy.lvmobspawners.developers;

import codes.laivy.lvmobspawners.configuration.SpawnerConfiguration;
import codes.laivy.lvmobspawners.spawners.CustomSpawner;
import codes.laivy.lvmobspawners.spawners.SpawnerTexture;
import codes.laivy.lvmobspawners.utils.CustomPlayerHeads;
import codes.laivy.lvmobspawners.utils.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static codes.laivy.lvmobspawners.LvMobSpawners.getPlugin;

public class Inventorys {

    private static final GuiUtils g = new GuiUtils();

    public static Inventory getSpawnerTextureInventory(SpawnerTexture texture, int page) {
        if (texture.getSpawners().size() == 1) {
            for (Map.Entry<Long, CustomSpawner> e : texture.getSpawners().entrySet()) {
                return getSpawnerInventory(e.getValue());
            }
        }

        Inventory gui = Bukkit.createInventory(null, 36, "G.Spawner " + texture.hashCode());

        int row = page * 27;
        int gRow = 0;

        List<CustomSpawner> list = new LinkedList<>();
        for (Map.Entry<Long, CustomSpawner> e : texture.getSpawners().entrySet()) {
            list.add(e.getValue());
        }

        for (int get = row; get < list.size(); get++) {
            CustomSpawner s = list.get(get);
            SpawnerConfiguration c = s.getTexture().getConfiguration();
            ItemStack i = c.getItemStack();
            ItemMeta m = i.getItemMeta();
            List<String> l = m.getLore();

            l.addAll(Arrays.asList(
                    "",
                    "§7Intervalo de Geração: §f" + (c.getMaxSpawnTime() - s.getSpawnIntervalLevel()) + " segundos",
                    "§7Máx. mobs na área: §f" + (s.getMaxMobsInAreaLevel() + c.getMobsInAreaDefault()) + " mobs",
                    "§7Booster de XP: §f" + (s.getMobsXPBoosterLevel() + c.getXPBoosterDefault()) + "x",
                    "§7ID: §6" + s.getID(),
                    "",
                    "§aClique para gerenciar esse spawner"
            ));

            m.setLore(l);

            i.setItemMeta(m);

            gui.setItem(gRow, i);
            gRow++;
        }

        for (int d = 27; d < 36; d++) {
            gui.setItem(d, g.createItem(Material.STAINED_GLASS_PANE, 15, ""));
        }

        gui.setItem(27, g.createItem(Material.STAINED_GLASS_PANE, 14, "&cPágina anterior",
                "&7Clique para acessar a página",
                "&7anterior desse spawner",
                "",
                "&7Página atual: §f" + (page + 1)
        ));
        gui.setItem(35, g.createItem(Material.STAINED_GLASS_PANE, 5, "&aPróxima página",
                "&7Clique para acessar a próxima",
                "&7página desse spawner",
                "",
                "&7Página atual: §f" + (page + 1)
        ));
        gui.setItem(31, g.createItem(Material.PAPER, null, "&eCréditos do Plugin",
                "&7Esse plugin foi desenvolvido por",
                "&fDanielZinh#7616",
                "",
                "&7Versão: &f" + getPlugin().getDescription().getVersion(),
                "&fwww.laivy.codes"
        ));

        gui.setItem(34, g.createItem(Material.BARRIER, null, "&cRemover todos", "&7Clique para remover todos os", "&7spawners estacados", "", "&4Atenção: &cesse processo jogará", "&c&nTODOS&c os spawners no chão"));

        return gui;
    }

    public static Inventory getSpawnerInventory(CustomSpawner spawner) {
        Inventory gui = Bukkit.createInventory(null, 36, "M.Spawner " + spawner.getID());
        SpawnerConfiguration c = spawner.getTexture().getConfiguration();

        if (getPlugin().getEconomy() != null) {

            gui.setItem(11, g.createItem(Material.BARRIER, null, "&cRemover", "&7Clique para remover", "&7esse spawner"));
            gui.setItem(13, g.createItem(Material.WATCH, null, "&aIntervalo de Nascimento",
                    "&7O tempo de nascimento entre",
                    "&7cada criatura",
                    "",
                    "&7Atual: &f" + (c.getMaxSpawnTime() - spawner.getSpawnIntervalLevel()) + " segundos",
                    "§7Preço: &cR$" + c.getShopSpawnerIntervalUpgrade() * (spawner.getSpawnIntervalLevel() + 1),
                    "",
                    "&aClique para melhorar"
            ));
            gui.setItem(14, g.createItem(Material.SKULL_ITEM, 2, "&eMobs em Área",
                    "&7A quantidade máxima de entidades",
                    "&7em uma área de &f" + c.getMobsAreaCheckRadius() + " &7blocos",
                    "",
                    "&7Atual: &f" + (spawner.getMaxMobsInAreaLevel() + c.getMobsInAreaDefault()) + " mobs",
                    "§7Preço: &cR$" + c.getShopSpawnerMobsInAreaUpgrade() * (spawner.getMaxMobsInAreaLevel() + 1),
                    "",
                    "&aClique para melhorar"
            ));
            gui.setItem(15, g.createItem(Material.EXP_BOTTLE, null, "&bBooster de XP",
                    "&7Os mobs que nascerem desse spawner",
                    "&7possuirão um booster de XP",
                    "",
                    "&7Atual: &f" + (spawner.getMobsXPBoosterLevel() + c.getXPBoosterDefault()) + "x",
                    "§7Preço: &cR$" + c.getShopSpawnerXPBoosterUpgrade() * (spawner.getMobsXPBoosterLevel() + 1),
                    "",
                    "&aClique para melhorar"
            ));
        } else {
            gui.setItem(13, g.createItem(Material.BARRIER, null, "&cRemover", "&7Clique para remover", "&7esse spawner"));
        }

        int success = 0;
        for (int row = 27; row < 36; row++) {
            success += 10;
            if (success >= 100 - ((spawner.getSpawnSec() / (c.getMaxSpawnTime() - spawner.getSpawnIntervalLevel())) * 100)) {
                gui.setItem(row, g.createItem(Material.STAINED_GLASS_PANE, 0, "§7Tempo restante: §f" + spawner.getSpawnSec() + " segundos"));
            } else {
                gui.setItem(row, g.createItem(Material.STAINED_GLASS_PANE, 5, "§7Tempo restante: §f" + spawner.getSpawnSec() + " segundos"));
            }
        }

        return gui;
    }

    public static Inventory getSpawnersShopInventory(int page) {
        Inventory gui = Bukkit.createInventory(null, 36, "Loja de Spawners");

        int row = page * 27;
        int gRow = 0;

        List<SpawnerConfiguration> list = new LinkedList<>();
        for (Map.Entry<String, SpawnerConfiguration> e : SpawnerConfiguration.getConfigurations().entrySet()) {
            list.add(e.getValue());
        }

        for (int get = row; get < list.size(); get++) {
            SpawnerConfiguration c = list.get(get);

            List<String> lore = new LinkedList<>(c.getShopSpawnerLore());
            lore.addAll(Arrays.asList("", "§7Preço: §cR$" + c.getShopSpawnerPrice(), "&7ID: &6" + c.getID(), "", "§aClique para Comprar"));
            gui.setItem(gRow, g.createItem(CustomPlayerHeads.create(c.getSpawnerHeadTexture()), c.getShopSpawnerName(), lore));
            gRow++;
        }

        for (row = 27; row < 36; row++) {
            gui.setItem(row, g.createItem(Material.STAINED_GLASS_PANE, 15, ""));
        }


        gui.setItem(27, g.createItem(Material.STAINED_GLASS_PANE, 14, "&cPágina anterior",
                "&7Clique para acessar a página",
                "&7anterior da loja de spawners",
                "",
                "&7Página atual: §f" + (page + 1)
        ));
        gui.setItem(35, g.createItem(Material.STAINED_GLASS_PANE, 5, "&aPróxima página",
                "&7Clique para acessar a próxima",
                "&7página da loja de spawners",
                "",
                "&7Página atual: §f" + (page + 1)
        ));
        gui.setItem(31, g.createItem(Material.PAPER, null, "&eCréditos do Plugin",
                "&7Esse plugin foi desenvolvido por",
                "&fDanielZinh#7616",
                "",
                "&7Versão: &f" + getPlugin().getDescription().getVersion(),
                "&fwww.laivy.codes"
        ));

        return gui;
    }

}

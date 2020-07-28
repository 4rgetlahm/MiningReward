package lt.Arnas.MiningReward;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {

    public static Plugin plugin;
    private Config config;

    //private HashMap<Player, Integer> playerData = new HashMap<>();

    private HashMap<Player, HashMap<Integer, Integer>> playerData = new HashMap<>();
    // HashMapas su playeriu ir jo blokais ir kiek jis ju isminino

    public void onEnable(){
        Bukkit.getServer().getLogger().log(Level.WARNING, "[MiningReward] Pluginas isijungia.");
        plugin = this;

        config = new Config();
        config.loadDefaultConfig();
        config.readConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void onDisable(){
        Bukkit.getServer().getLogger().log(Level.WARNING, "[MiningReward] Pluginas issijungia.");
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e){
        if(e.getPlayer() == null) return;
        if(!config.blockBegin.containsKey(e.getBlock().getTypeId())) return;
        //if(!e.getPlayer().hasPermission(config.rewardPermission)) return;

        if(!playerData.containsKey(e.getPlayer())){
            HashMap<Integer, Integer> emptyMap = new HashMap<>();
            playerData.put(e.getPlayer(), emptyMap);
            for(int block :  config.blockBegin.keySet()){
                playerData.get(e.getPlayer()).put(block, 0);
            }
        }

        playerData.get(e.getPlayer()).replace(e.getBlock().getTypeId(), playerData.get(e.getPlayer()).get(e.getBlock().getTypeId())+1);

        if(e.getPlayer().hasPermission(config.rewardPermission)){
            if(playerData.get(e.getPlayer()).get(e.getBlock().getTypeId()) > config.blockBeginPermission.get(e.getBlock().getTypeId())){

                Double rng = 1.0 / (config.blockEndPermission.get(e.getBlock().getTypeId()) - config.blockBeginPermission.get(e.getBlock().getTypeId()));

                if(playerData.get(e.getPlayer()).get(e.getBlock().getTypeId()) == config.blockEndPermission.get(e.getBlock().getTypeId())){ // jei guaranteed dropas

                    playerData.get(e.getPlayer()).replace(e.getBlock().getTypeId(), 0);
                    e.setCancelled(true);
                    e.getBlock().breakNaturally();

                    spawnChestForPlayer(e.getBlock().getLocation(), e.getPlayer());
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.playerMessage.replace("{NAME}", e.getPlayer().getName())));
                    if (config.globalMessageEnabled) {
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.globalMessage.replace("{NAME}", e.getPlayer().getName())));
                    }
                }
                else if (Math.random() < rng){
                    playerData.get(e.getPlayer()).replace(e.getBlock().getTypeId(), 0);

                    e.setCancelled(true);
                    e.getBlock().breakNaturally();

                    spawnChestForPlayer(e.getBlock().getLocation(), e.getPlayer());
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.playerMessage.replace("{NAME}", e.getPlayer().getName())));
                    if (config.globalMessageEnabled) {
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.globalMessage.replace("{NAME}", e.getPlayer().getName())));
                    }
                }
            }
        } else{
            if(playerData.get(e.getPlayer()).get(e.getBlock().getTypeId()) > config.blockBegin.get(e.getBlock().getTypeId())) { // jei turi daugiau uz minimuma

                Double rng = 1.0 / (config.blockEnd.get(e.getBlock().getTypeId()) - config.blockBegin.get(e.getBlock().getTypeId()));

                if(playerData.get(e.getPlayer()).get(e.getBlock().getTypeId()) == config.blockEnd.get(e.getBlock().getTypeId())){ // jei guaranteed dropas

                    playerData.get(e.getPlayer()).replace(e.getBlock().getTypeId(), 0);
                    e.setCancelled(true);
                    e.getBlock().breakNaturally();

                    spawnChestForPlayer(e.getBlock().getLocation(), e.getPlayer());
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.playerMessage.replace("{NAME}", e.getPlayer().getName())));
                    if (config.globalMessageEnabled) {
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.globalMessage.replace("{NAME}", e.getPlayer().getName())));
                    }
                }
                else if (Math.random() < rng){
                    playerData.get(e.getPlayer()).replace(e.getBlock().getTypeId(), 0);

                    e.setCancelled(true);
                    e.getBlock().breakNaturally();

                    spawnChestForPlayer(e.getBlock().getLocation(), e.getPlayer());
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', config.playerMessage.replace("{NAME}", e.getPlayer().getName())));
                    if (config.globalMessageEnabled) {
                        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', config.globalMessage.replace("{NAME}", e.getPlayer().getName())));
                    }
                }
            }
        }



        /*if(e.getPlayer().hasPermission(config.blockList.get(e.getBlock().getTypeId()))){
            if(playerData.containsKey(e.getPlayer())){
                playerData.replace(e.getPlayer(), playerData.get(e.getPlayer()) + config.blockValueList.get(e.getBlock().getTypeId()));
                System.out.println(playerData.get(e.getPlayer()));
            } else{
                playerData.put(e.getPlayer(), config.blockValueList.get(e.getBlock().getTypeId()));
            }

            if(playerData.get(e.getPlayer()) >= config.rewardAt){
                playerData.replace(e.getPlayer(), playerData.get(e.getPlayer()) - config.rewardAt);
                e.setCancelled(true);
                e.getBlock().breakNaturally();
                spawnChest(e.getBlock().getLocation());
            }
        }*/
    }

    public void spawnChestForPlayer(Location loc, Player player){
        loc.getBlock().setType(Material.CHEST);
        Chest rewardChest = (Chest) loc.getBlock().getState();
        for (Map.Entry<ItemStack, Double> entry : config.dropRate.entrySet()) {
            try {
                if (Math.random() < entry.getValue()) {
                    rewardChest.getBlockInventory().addItem(entry.getKey());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loc.add(0.5,0.5,0.5);
        new BukkitRunnable(){
            @Override
            public void run() {
                for(double size = 0; size < 1; size+=0.5) {
                    for (int degree = 0; degree < 360; degree++) {
                        double radians = Math.toRadians(degree);
                        double x = Math.cos(radians) * size;
                        double z = Math.sin(radians) * size;
                        loc.add(x, 0, z);
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FIREWORKS_SPARK, true, (float) (loc.getX() + x), (float) (loc.getY()+1), (float) (loc.getZ() + z), 0, 0, 0, 0, 1);
                            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                        }
                        loc.subtract(x, 0, z);
                    }
                }
            }
        }.runTaskAsynchronously(Main.plugin);
    }
}

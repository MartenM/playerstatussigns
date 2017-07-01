package tk.martenm.playerstatussigns.objects;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import tk.martenm.playerstatussigns.MainClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marten on 1-7-2017.
 * The PlayerStatusSign class
 */
public class PlayerStatusSign {

    private MainClass plugin;
    private UUID uuid;
    private Location loc;

    public PlayerStatusSign(MainClass plugin, UUID uuid, Location loc){
        this.plugin = plugin;
        this.loc = loc;
        this.uuid = uuid;
    }

    public void updateSign() {
        Sign sign;
        try {
            sign = getSign();
        } catch (Exception ex) {
            plugin.signs.remove(this);
            return;
        }

        OfflinePlayer offline_player = plugin.getServer().getOfflinePlayer(uuid);

        //Player is online
        if (offline_player.isOnline()) {
            Player player = plugin.getServer().getPlayer(uuid);

            //Essentials AFK check
            if (plugin.essentials != null) {
                if (plugin.essentials.getUser(uuid).isAfk()) {
                    List<String> list = plugin.getConfig().getStringList("format.afk");
                    for (int i = 0; i < 4; i++) {
                        sign.setLine(i, ChatColor.translateAlternateColorCodes('&', list.get(i)
                                .replace("%player%", player.getName())));
                    }
                    sign.update();
                    return;
                }
            }

            //Online and/or not afk
            List<String> list = plugin.getConfig().getStringList("format.online");
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, ChatColor.translateAlternateColorCodes('&', list.get(i)
                        .replace("%player%", player.getName())));
            }
            return;
        }
        //Player is NOT online

        Date date = new Date(offline_player.getLastPlayed());
        DateFormat formatter = new SimpleDateFormat(plugin.getConfig().getString("format.date"));
        String stringDate = formatter.format(date).replace(":", " / ");

        List<String> list = plugin.getConfig().getStringList("format.offline");
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, ChatColor.translateAlternateColorCodes('&', list.get(i)
                    .replace("%player%", offline_player.getName())
                    .replace("%since%", stringDate)));
        }


        sign.update();
    }

    private Sign getSign() throws Exception {
        Block block = loc.getBlock();
        if(block.getState() instanceof Sign){
            return (Sign) block.getState();
        }
        throw new Exception("No sign located");
    }

    public boolean signExists(){
        try{
            getSign();
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    public Location getLocation(){
        return loc;
    }

    public UUID getUuid(){
        return uuid;
    }
}

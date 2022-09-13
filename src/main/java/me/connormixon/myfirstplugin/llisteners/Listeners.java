package me.connormixon.myfirstplugin.llisteners;

import me.connormixon.myfirstplugin.MyFirstPlugin;
import me.connormixon.myfirstplugin.models.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.Date;

//TODO add new functions
//TODO add Player Kills Seperate from Entity Kills
//TODO Change Date to show Time in seconds, minutes and hours

public class Listeners implements Listener {

    private final MyFirstPlugin plugin;

    public Listeners(MyFirstPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)  {

        Player p = e.getPlayer();
        try {
            PlayerStats stats = getPlayerStatsFromDatabase(p);
            stats.setBlocksBroken(stats.getBlocksBroken() + 1);
            stats.setBalance(stats.getBalance() + 0.5);

            this.plugin.getDatabase().updatePlayerStats(stats);

        }catch (SQLException exception){

            exception.printStackTrace();

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player killer = event.getEntity().getKiller();
        Player p = event.getEntity();


        //Update the stats of both the killer and victim.
        try{
            PlayerStats pStats = getPlayerStatsFromDatabase(p);
            pStats.setDeaths(pStats.getDeaths() + 1);
            pStats.setBalance(pStats.getBalance() - 1.0);

            this.plugin.getDatabase().updatePlayerStats(pStats);

            if(killer == null){
                return;
            }

            PlayerStats killerStats = getPlayerStatsFromDatabase(killer);
            killerStats.setKills(killerStats.getKills() + 1);
            killerStats.setBalance(killerStats.getBalance() + 1.0);


            this.plugin.getDatabase().updatePlayerStats(killerStats);


        }catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        try{
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            playerStats.setLastLogin(new Date());
            this.plugin.getDatabase().updatePlayerStats(playerStats);
        }catch (SQLException joinE){
            joinE.printStackTrace();
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        try{
            PlayerStats playerStats = getPlayerStatsFromDatabase(p);
            playerStats.setLastLogout(new Date());
            this.plugin.getDatabase().updatePlayerStats(playerStats);
        }catch (SQLException quitE){
            quitE.printStackTrace();
        }
    }


    private PlayerStats getPlayerStatsFromDatabase(Player p) throws SQLException{
        PlayerStats stats = this.plugin.getDatabase().findPlayerStatsByUUID(p.getUniqueId().toString());

        if (stats == null) {

            stats = new PlayerStats(p.getUniqueId().toString(), 0, 0, 0,0, new Date(), new Date());

            this.plugin.getDatabase().createPlayerStats(stats);

            return stats;

        }

        return stats;
    }

}

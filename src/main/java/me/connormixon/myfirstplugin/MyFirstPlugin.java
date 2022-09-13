package me.connormixon.myfirstplugin;



import me.connormixon.myfirstplugin.db.Database;
import me.connormixon.myfirstplugin.llisteners.Listeners;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.Bukkit;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;


public final class MyFirstPlugin extends JavaPlugin implements Listener{
  World w;

  private Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("My first plugin has started!!! Hello!!!");
        getServer().getPluginManager().registerEvents(this, this);

        try{
             this.database = new Database();
            database.initializeDatabase();
        }catch (SQLException e) {
            System.out.println("Unable to connect to the Database and create tables.");

            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new Listeners(this),this);

    }

    public Database getDatabase() {
        return database;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        event.setJoinMessage(playerName + " has joined the server.");

    }



    @EventHandler
    public void onSleep(PlayerBedEnterEvent event){
        Player player = event.getPlayer();
        if ((player.getWorld().getTime() >= 12000 && player.getWorld().getTime() <= 22999) || (player.getWorld().hasStorm())) {
            player.getWorld().setStorm(false);
            player.getWorld().setTime(1);

        }
        else {
            player.sendMessage("You may only sleep at night or during Thunderstorms!");
        }
    }
    




}

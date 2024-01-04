package dev.leonk;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BoatHop extends JavaPlugin implements Listener {

  private FileConfiguration config;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    config = getConfig();
    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!cmd.getName().equals("boathop")) return false;
    if (!sender.hasPermission("boathop.config")) {
      sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
      return true;
    }
    // validate command
    switch (args.length) {
      case 0: break;
      case 2:
        if (!config.contains(args[0])) return false;
        // update config
        try { updateConfig(args[0], Double.parseDouble(args[1])); }
        catch (NumberFormatException e) { return false; }
        break;
      default: return false;
    }
    // show settings
    showConfig(sender);
    return true;
  }

  @EventHandler
  public void onBlockDamage(BlockDamageEvent event) {
    if (event.getItemInHand() == null) return;
    elevate(event.getPlayer());
  }

  private void elevate(Player player) {
    if (player.getVehicle() == null) return;
    if (!(player.getVehicle() instanceof Boat)) return;
    if (player.getLocation().getBlock().getRelative(0, 0, 0).getType() != Material.WATER) return;
    Boat boat = (Boat) player.getVehicle();
    Vector boatDirection = boat.getLocation().getDirection();
    // update boats velocity
    Vector velocity = boat.getVelocity()
      .add(boatDirection.multiply(config.getDouble("boost")))
      .add(new Vector(0., config.getDouble("jump"), 0.));
    boat.setVelocity(velocity);
  }

  private void showConfig(CommandSender sender) {
    StringBuilder msg = new StringBuilder();
    msg.append(String.format("%s%sBoatHop Settings:%s", ChatColor.GOLD, ChatColor.UNDERLINE, ChatColor.RESET));
    for (String key : config.getKeys(false)) {
      msg.append(String.format("\n%s%s: %s%s%s%s", ChatColor.GRAY, key, ChatColor.BOLD, ChatColor.GREEN, config.get(key), ChatColor.RESET));
    }
    sender.sendMessage(msg.toString());
  }

  private void updateConfig(String key, Double value) {
    config.set(key, value);
    saveConfig();
  }
}
package dev.leonk;

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
  public void onDisable() {
    saveConfig();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName() != "boathop") return false;
    if (args.length < 2) return false;
    if (!sender.hasPermission("boathop.config")) return false;
    String subcommand = args[0];
    if (!config.contains(subcommand)) return false;
    double value = Double.parseDouble(args[1]);
    config.set(subcommand, value);
    return false;
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
    // get the forward direction
    Vector direction = boat.getLocation().getDirection();
    // increase boats velocity
    Vector velocity = boat.getVelocity()
      .add(direction.multiply(config.getDouble("boost")))
      .add(new Vector(0., config.getDouble("jump"), 0.));
    boat.setVelocity(velocity);
  }
}
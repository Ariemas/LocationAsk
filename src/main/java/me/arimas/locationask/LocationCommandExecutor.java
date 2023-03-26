package me.arimas.locationask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocationCommandExecutor implements CommandExecutor {

    private final LocationAsk plugin;
    private final int requestTimeout;
    private final int requestCooldown;
    private final Map<UUID, UUID> locationRequests = new HashMap<>();
    private final Map<UUID, Long> requestTimestamps = new HashMap<>();

    public LocationCommandExecutor(LocationAsk plugin, int requestTimeout, int requestCooldown) {
        this.plugin = plugin;
        this.requestTimeout = requestTimeout;
        this.requestCooldown = requestCooldown;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + command.getName() + " <player>");
            return true;
        }

        Player player = (Player) sender;
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("requestlocation")) {
            handleRequestLocation(player, target);
        } else if (command.getName().equalsIgnoreCase("locationaccept")) {
            handleLocationAccept(player, target);
        }

        return true;
    }

    private void handleRequestLocation(Player player, Player target) {
        if (!(player.hasPermission("locationask.request"))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to request locations.");
            return;
        }

        if(!(target.hasPermission("locationask.accept"))) {
            player.sendMessage(ChatColor.RED + target.getName() +" does not have permission to accept location requests.");
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot request your own location.");
            return;
        }

        if (locationRequests.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have a pending location request.");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (requestTimestamps.containsKey(player.getUniqueId()) &&
                currentTime - requestTimestamps.get(player.getUniqueId()) < requestCooldown * 1000) {
            player.sendMessage(ChatColor.RED + "You must wait before sending another location request.");
            return;
        }

        locationRequests.put(target.getUniqueId(), player.getUniqueId());
        requestTimestamps.put(player.getUniqueId(), currentTime);
        player.sendMessage(ChatColor.GREEN + "Location request sent to " + target.getName() + ".");
        target.sendMessage(ChatColor.YELLOW + player.getName() + " has requested your location. Type " + ChatColor.AQUA + "/locationaccept " + player.getName() + ChatColor.YELLOW + " to accept.");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (locationRequests.containsKey(target.getUniqueId()) && locationRequests.get(target.getUniqueId()).equals(player.getUniqueId())) {
                    locationRequests.remove(target.getUniqueId());
                    player.sendMessage(ChatColor.RED + "Your location request to " + target.getName() + " has timed out.");
                    target.sendMessage(ChatColor.RED + "The location request from " + player.getName() + " has timed out.");
                }
            }
        }.runTaskLater(plugin, requestTimeout * 20);
    }

    private void handleLocationAccept(Player player, Player requester) {
        if (!locationRequests.containsKey(player.getUniqueId()) || !locationRequests.get(player.getUniqueId()).equals(requester.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You do not have a location request from " + requester.getName() + ".");
            return;
        }

        locationRequests.remove(player.getUniqueId());
        Location location = player.getLocation();
        String locationMessage = ChatColor.YELLOW + player.getName() + "'s location: " + ChatColor.AQUA + "World: " + location.getWorld().getName() + ", X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        requester.sendMessage(locationMessage);
        player.sendMessage(ChatColor.GREEN + "You have shared your location with " + requester.getName() + ".");
    }
}
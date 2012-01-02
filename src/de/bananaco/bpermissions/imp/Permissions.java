package de.bananaco.bpermissions.imp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

public class Permissions extends JavaPlugin {

	private WorldManager wm = WorldManager.getInstance();
	private final Map<String, String> mirrors = new HashMap<String, String>();
	private Listener loader = new WorldLoader(mirrors);
	
	@Override
	public void onDisable() {
		System.out.println(format("Disabled"));
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvent(Event.Type.WORLD_INIT, loader, Priority.Normal, this);
		System.out.println(format("Enabled"));
	}
	
	public static String format(String message) {
		return "[bPermissions] "+message;
	}
	
	public static boolean hasPermission(Player player, String node) {
		return WorldManager.getInstance().getWorld(player.getWorld().getName()).getUser(player.getName()).hasPermission(node);
	}
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(format(message));
	}

	// TODO proper commands
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		boolean allowed = true;
		
		if(sender instanceof Player) 
			allowed = hasPermission((Player) sender, "bPermissions.reload") || sender.isOp();
		
		if(!allowed) {
			sendMessage(sender, "you're not allowed to do that!");
			return true;
		}
		
		if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("reload")) {
				if(args.length == 1) {
					for(World world : wm.getAllWorlds())
						world.load();
					sendMessage(sender, "reloaded all worlds");
					return true;
				} else {
					String world = args[1];
					if(getServer().getWorld(world) != null) {
						wm.getWorld(world).load();
						sendMessage(sender, "reloaded world: "+world);
						return true;
					} else {
						sendMessage(sender, "no world by that name found");
						return true;
					}
				}
			}
		}
		
		sendMessage(sender, "Use \"/permissions reload\" to reload the permissions of this server!");
		return true;
	}

}

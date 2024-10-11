package nl.seyox.prophunt.listeners;

import nl.seyox.prophunt.PropHunt;
import nl.seyox.prophunt.objects.Game;
import nl.seyox.prophunt.objects.PropPlayer;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerDamageListener implements Listener {


    @EventHandler
    public void onPlayerMove(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Player hunter = (Player) event.getDamager();
        if (player.equals(hunter)) return;
        PropPlayer propPlayer = PropHunt.getGameManager().getPlayers().get(player);
        PropPlayer propHunter = PropHunt.getGameManager().getPlayers().get(hunter);
        if (propPlayer == null) return;
        if (propHunter != null) return;
        if (propPlayer.getPlayer().getHealth() <= 10) {
            propPlayer.die();
        } else {
            propPlayer.getPlayer().damage(10);
        }
    }

    @EventHandler
    public void onInteractBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        event.setCancelled(true);
        PropPlayer propHunter = PropHunt.getGameManager().getPlayers().get(player);
        if (propHunter != null) return;
        for (Game game : PropHunt.getGameManager().getGames()) {
            if (!game.getHunters().contains(player)) continue;
            for (PropPlayer gamePlayer : game.getPlayers()) {
                if (gamePlayer.getRealBlock() == null) continue;
                if (gamePlayer.getRealBlock() != event.getClickedBlock()) continue;
                if (gamePlayer.getPlayer().getHealth() <= 10) {
                    gamePlayer.die();
                } else {
                    gamePlayer.getPlayer().damage(10);
                }
                gamePlayer.removeRealBlock();
            }
        }
    }

}

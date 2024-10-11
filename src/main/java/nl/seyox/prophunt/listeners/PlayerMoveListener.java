package nl.seyox.prophunt.listeners;

import nl.seyox.prophunt.PropHunt;
import nl.seyox.prophunt.objects.PropPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PropPlayer propPlayer = PropHunt.getGameManager().getPlayers().get(player);
        if (propPlayer == null) return;
        if (propPlayer.getRealBlock() != null) return;
        Location tpLoc = player.getLocation().clone();
        tpLoc.setX(tpLoc.getX() - 0.5);
        tpLoc.setZ(tpLoc.getZ() - 0.5);
        tpLoc.setYaw(0);
        tpLoc.setPitch(0);
        propPlayer.getBlockDisplay().teleport(tpLoc);
    }

}

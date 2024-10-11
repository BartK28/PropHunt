package nl.seyox.prophunt.objects;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import nl.seyox.prophunt.GameManager;
import nl.seyox.prophunt.PropHunt;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.eclipse.sisu.bean.IgnoreSetters;

@Getter
@Setter
public class PropPlayer {

    private Player player;
    private BlockDisplay blockDisplay;
    private Location lastBlockLocation;
    private int noMoveTime = 0;
    private BossBar bossBar;
    private Block realBlock;
    private Material prop;
    private Game game;

    public PropPlayer(Player player, Material prop, Game game) {
        this.game = game;
        this.player = player;
        this.prop = prop;
        BlockDisplay blockDisplay = (BlockDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.BLOCK_DISPLAY);
        blockDisplay.setBlock(prop.createBlockData());
        blockDisplay.setInterpolationDuration(2);
        this.blockDisplay = blockDisplay;
        bossBar = net.kyori.adventure.bossbar.BossBar.bossBar(Component.text("§aYou are a " + prop.name()), 1f, net.kyori.adventure.bossbar.BossBar.Color.BLUE, net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS);
        player.showBossBar(bossBar);
        PropHunt.getGameManager().getPlayers().put(player, this);
    }

    public void placeRealBlock() {
        if (realBlock != null) return;
        realBlock = player.getLocation().add(0,0.3,0).getBlock();
        if (realBlock.getType() != Material.AIR) {
            setNoMoveTime(4);
            return;
        }
        realBlock.setType(prop);
        blockDisplay.remove();
        player.teleport(realBlock.getLocation().add(0.5, 0, 0.5));
    }

    public void removeRealBlock() {
        if (realBlock == null) return;
        realBlock.setType(Material.AIR);
        realBlock = null;
        BlockDisplay blockDisplay = (BlockDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.BLOCK_DISPLAY);
        blockDisplay.setBlock(prop.createBlockData());
        blockDisplay.setInterpolationDuration(2);
        this.blockDisplay = blockDisplay;
    }

    public void updateBossBar() {
        if (realBlock != null) {
            bossBar.name(Component.text("§aYou are solid!"));
        } else {
            if (noMoveTime > 1) {
                bossBar.name(Component.text("§aYou will become solid in §2" + (5 - noMoveTime) + "§a seconds!"));
            } else {
                bossBar.name(Component.text("§aYou are a " + prop.name()));
            }
        }
    }

    public void die() {
        player.setHealth(20);
        removeRealBlock();
        blockDisplay.remove();
        game.getPlayers().remove(this);
        game.getHunters().add(player);
        player.clearActivePotionEffects();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(game.getMap().getHunterSpawnLocation().toLocation());
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        game.broadcast("§6" + player.getName() + " has died!");
    }
}

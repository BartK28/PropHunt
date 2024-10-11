package nl.seyox.prophunt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.seyox.prophunt.objects.GameLocation;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@Getter
public class GameMap {

    private String name = "Farm";
    private GameLocation lobbyLocation = new GameLocation(0,0,0,0,0,"world");
    private int minPlayers = 2;
    private int maxPlayers = 10;
    private int hunters = 1;
    private int hunterSpawnDelay = 10;
    private int gameTime = 300;
    private GameLocation hunterSpawnLocation = new GameLocation(0,0,0,0,0,"world");
    private List<Material> props = Arrays.asList(Material.STONE, Material.DIAMOND_BLOCK, Material.IRON_BLOCK, Material.GOLD_BLOCK);
    private List<GameLocation> spawnLocation = Arrays.asList(lobbyLocation);

}

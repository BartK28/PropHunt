package nl.seyox.prophunt;

import com.google.gson.Gson;
import lombok.Getter;
import nl.seyox.prophunt.enums.GameMap;
import nl.seyox.prophunt.listeners.PlayerDamageListener;
import nl.seyox.prophunt.listeners.PlayerJoinListener;
import nl.seyox.prophunt.listeners.PlayerMoveListener;
import nl.seyox.prophunt.objects.Game;
import nl.seyox.prophunt.objects.PropPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Getter
public class GameManager {

    private List<GameMap> mapsAvailable = new ArrayList<>();

    private List<Game> games = new ArrayList<>();

    private HashMap<Player, PropPlayer> players = new HashMap<>();

    public GameManager() {
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(), PropHunt.getPlugin(PropHunt.class));
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), PropHunt.getPlugin(PropHunt.class));
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), PropHunt.getPlugin(PropHunt.class));
        File folder = new File("mapconfigs");
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (File file : folder.listFiles()) {
            try {
                FileReader reader = new FileReader(file);
                mapsAvailable.add(new Gson().fromJson(reader, GameMap.class));
                PropHunt.getPlugin(PropHunt.class).getLogger().info("Loaded map: " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mapsAvailable.isEmpty()) {
            PropHunt.getPlugin(PropHunt.class).getLogger().info("No maps found, generating default map");
            GameMap farm = new GameMap();
            try {
                FileWriter writer = new FileWriter(new File("mapconfigs/farm.json"));
                writer.write(new Gson().toJson(farm));
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Game game : games) {
                    game.tick();
                }
            }
        }.runTaskTimer(PropHunt.getPlugin(PropHunt.class), 0, 20);
    }

    public void startGame() {
        GameMap map = mapsAvailable.get(new Random().nextInt(mapsAvailable.size()));
        startGame(map);
    }

    public void startGame(GameMap map) {
        Game game = new Game(map);
        games.add(game);
    }
}

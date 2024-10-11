package nl.seyox.prophunt;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PropHunt extends JavaPlugin {

    @Getter
    private static GameManager gameManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        gameManager = new GameManager();
        gameManager.startGame();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

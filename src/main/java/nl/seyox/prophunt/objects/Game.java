package nl.seyox.prophunt.objects;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import nl.seyox.prophunt.PropHunt;
import nl.seyox.prophunt.enums.GameMap;
import nl.seyox.prophunt.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static jdk.incubator.vector.ByteVector.broadcast;

@Getter
public class Game {

    private List<PropPlayer> players = new ArrayList<>();
    private List<Player> hunters = new ArrayList<>();
    private List<Player> playersInGame = new ArrayList<>();
    private GameState state = GameState.LOBBY;
    private GameMap map;
    private int time = 0;

    public Game(GameMap map) {
        this.map = map;
    }

    public void addPlayer(Player player) {
        if (state != GameState.LOBBY && state != GameState.STARTING) {
            player.kick(Component.text("Game is already started"));
            return;
        }
        if (map.getMaxPlayers() < playersInGame.size() + 1) {
            player.kick(Component.text("Game is full"));
            return;
        }
        playersInGame.add(player);
        player.getInventory().clear();
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(map.getLobbyLocation().toLocation());
        player.showTitle(Title.title(Component.text("§6Welcome to prophunt!"), Component.text("§7Map: §f" + map.getName())));
    }

    public void tick() {
        if (state == GameState.LOBBY) {
            time++;
            if (playersInGame.isEmpty()) return;
            if (playersInGame.size() < map.getMinPlayers()) {
                if (time % 30 == 0) {
                    broadcast("§cWaiting for more players");
                }
                return;
            }
            broadcast("§6Minimum players reached, starting in 30 seconds!");
            state = GameState.STARTING;
            time = 30;
        } else if (state == GameState.STARTING) {
            if (time > 0) {
                time--;
                if (time == 20 || time == 10 || time == 5 || time == 4 || time == 3 || time == 2 || time == 1) {
                    broadcast("§6Starting in §e" + time + "§6 seconds!");
                }
            } else {
                state = GameState.IN_GAME;
                broadcast("§6Game has started!");
                int hunters = map.getHunters();
                for (int i = 0; i < hunters; i++) {
                    Player player = playersInGame.get(new Random().nextInt(playersInGame.size()));
                    while (this.hunters.contains(player)) {
                        player = playersInGame.get(new Random().nextInt(playersInGame.size()));
                    }
                    this.hunters.add(player);
                    player.sendMessage(Component.text("§6You are a hunter! You will be teleported in §e" + map.getHunterSpawnDelay() + "§6 seconds!"));
                }

                for (Player player : playersInGame) {
                    if (this.hunters.contains(player)) continue;
                    Material prop = map.getProps().get(new Random().nextInt(map.getProps().size()));
                    player.teleport(map.getSpawnLocation().get(new Random().nextInt(map.getSpawnLocation().size())).toLocation());
                    PropPlayer propPlayer = new PropPlayer(player, prop, this);
                    players.add(propPlayer);
                    player.sendMessage(Component.text("§6You have been spawned as a prop! Go hide!"));
                }

                for (PropPlayer player : players) {
                    player.getPlayer().addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(1, 10).withParticles(false).withIcon(false).withDuration(-1));
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player hunter : Game.this.hunters) {
                            hunter.teleport(map.getHunterSpawnLocation().toLocation());
                            hunter.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                        }
                    }
                }.runTaskLater(PropHunt.getPlugin(PropHunt.class), map.getHunterSpawnDelay() * 20);
                time = map.getGameTime();
            }
        } else if (state == GameState.IN_GAME) {
            if (time > 0) {
                time--;
            }
            for (PropPlayer player : players) {
                if (player.getLastBlockLocation() == null) {
                    player.setLastBlockLocation(player.getPlayer().getLocation());
                }
                player.getPlayer().sendMessage(player.getPlayer().getLocation().distance(player.getLastBlockLocation()) + " - " + player.getNoMoveTime());
                if (player.getPlayer().getLocation().distance(player.getLastBlockLocation()) > 1d) {
                    player.setLastBlockLocation(player.getPlayer().getLocation());
                    player.setNoMoveTime(0);
                    player.removeRealBlock();
                } else {
                    player.setNoMoveTime(player.getNoMoveTime() + 1);
                    if (player.getNoMoveTime() > 5) {
                        player.placeRealBlock();
                    }
                }
                player.updateBossBar();
            }
            if (players.size() == 1) {
                broadcast("§6Game over! §e" + players.get(0).getPlayer().getName() + " §6has won!");
                for (Player player : playersInGame) {
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(map.getLobbyLocation().toLocation());
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : playersInGame) {
                            player.kick(Component.text("§6Game over! §e" + players.get(0).getPlayer().getName() + " §6has won!"));
                            Bukkit.shutdown();
                        }
                    }
                }.runTaskLater(PropHunt.getPlugin(PropHunt.class), 100);
            }
        }
    }

    public void broadcast(String message) {
        for (Player player : playersInGame) {
            player.sendMessage(message);
        }
    }

}

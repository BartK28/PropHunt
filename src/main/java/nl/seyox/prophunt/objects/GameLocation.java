package nl.seyox.prophunt.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameLocation {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private String world;

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}

package org.openstreetmap.josm.plugins.pt_routefix;

import java.util.HashSet;
import java.util.Set;

public class StopArea {
    private String name;
    private Set<Platform> platforms = new HashSet<>();
    private Set<StopPosition> stopPositions = new HashSet<>();
    private Set<Stop> stops = new HashSet<>();

    public StopArea(String name) {
        super();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void addPlatform(Platform platform) {
        platforms.add(platform);
    }

    public void addStopPosition(StopPosition stopPosition) {
        stopPositions.add(stopPosition);
    }
    
    public void addBusStop(Stop stop) {
        stops.add(stop);
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public Set<StopPosition> getStopPositions() {
        return stopPositions;
    }

    public Set<Stop> getStops() {
        return stops;
    }
}

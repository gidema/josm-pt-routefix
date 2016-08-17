package org.openstreetmap.josm.plugins.pt_routefix;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;

public class PTData {
    private final Map<OsmPrimitive, Platform> platforms = new HashMap<>();
    private final Map<OsmPrimitive, StopPosition> stopPositions = new HashMap<>();
    private final Map<Relation, Route> routes = new HashMap<>();
    private final Map<Stop, Stop> stops = new HashMap<>();

    public void clear() {
        platforms.clear();
        stopPositions.clear();
        routes.clear();
        stops.clear();
    }

    public Collection<Platform> getPlatforms() {
        return platforms.values();
    }
    
    public Collection<StopPosition> getStopPositions() {
        return stopPositions.values();
    }
    
    public Collection<Route> getRoutes() {
        return routes.values();
    }

    public void add(Platform platform) {
        platforms.put(platform.getPrimitive(), platform);
    }

    public void add(StopPosition stopPosition) {
        stopPositions.put(stopPosition.getPrimitive(), stopPosition);
    }

    public void add(Route route) {
        routes.put(route.getRelation(), route);
    }
    
    public Platform getPlatform(OsmPrimitive primitive) {
        return platforms.get(primitive);
    }

    public StopPosition getStopPosition(OsmPrimitive primitive) {
        return stopPositions.get(primitive);
    }

    public void add(Stop stop) {
        this.stops.put(stop, stop);
    }

    public Collection<Stop> getStops() {
        return stops.values();
    }

    public Stop getStop(Stop stop) {
        return stops.get(stop);
    }
}

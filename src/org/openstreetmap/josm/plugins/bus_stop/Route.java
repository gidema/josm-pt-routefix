package org.openstreetmap.josm.plugins.bus_stop;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.bus_stop.issues.DuplicatePlatformOnRouteIssue;
import org.openstreetmap.josm.plugins.bus_stop.issues.DuplicateStopPositionOnRouteIssue;
import org.openstreetmap.josm.plugins.bus_stop.issues.Issue;


public class Route {
    private final Relation relation;
    private final RouteType type;
    private final String name;
    private final Map<String, Platform> platforms = new HashMap<>();
    private final Map<String, StopPosition> stopPositions = new HashMap<>();
    private List<RouteMember> members;
    private Set<Way> ways = new HashSet<>();
    private final Map<String, Stop> stops = new HashMap<>();
    private List<Issue> issues = new LinkedList<>();

    public Route(Relation relation) {
        super();
        assert PTUtil.isTaggedAsPTRoute(relation);
        this.relation = relation;
        this.name = relation.get("name");
        this.type = PTUtil.getRouteType(relation.get("route"));
    }
    
    public RouteType getType() {
        return type;
    }

    public Relation getRelation() {
        return relation;
    }

    public String getName() {
        return name;
    }

    public void setMembers(List<RouteMember> members) {
        this.members = members;
    }

    public boolean isInComplete() {
        return getRelation().hasIncompleteMembers();
    }
    
    public List<RouteMember> getMembers() {
        return members;
    }
    
    public Set<Way> getWays() {
        return ways;
    }

    public Map<String, Stop> getStops() {
        return stops;
    }

    @Override
    public int hashCode() {
        return relation.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (! (obj instanceof Route)) return false;
        return relation.equals(((Route)obj).getRelation());
    }

    @Override
    public String toString() {
        return name;
    }

    public Stop getStop(String stopName) {
        return stops.get(stopName);
    }

    public void addPlatform(Platform platform) {
        Platform replacedPlatform = platforms.put(platform.getName(), platform);
        // check if this route contains an other platform with the same name
        if (replacedPlatform != null && replacedPlatform != platform) {
            List<Platform> thePlatforms = Arrays.asList(platform, replacedPlatform);
            Issue issue = new DuplicatePlatformOnRouteIssue(this, name, thePlatforms);
            issues.add(issue);
            platform.addIssue(issue);
            replacedPlatform.addIssue(issue);
        }
    }
    
    public void addStopPosition(StopPosition stopPosition) {
        StopPosition replacedStopPosition = stopPositions.put(stopPosition.getName(), stopPosition);
        // check if this route contains an other stop position with the same name
        if (replacedStopPosition != null && replacedStopPosition != stopPosition) {
            List<StopPosition> positions = Arrays.asList(stopPosition, replacedStopPosition);
            Issue issue = new DuplicateStopPositionOnRouteIssue(this, name, positions);
            issues.add(issue);
        }
    }

    public Collection<Platform> getPlatforms() {
        return platforms.values();
    }
    
    public Collection<StopPosition> getStopPositions() {
        return stopPositions.values();
    }

    public StopPosition getStopPosition(String stopName) {
        // TODO Consider throwing exception in case of duplicate positions with this name
        return stopPositions.get(stopName);
    }

//    public void addIssue(Issue issue) {
//        issues.add(issue);
//    }
    
    public List<Issue> getIssues() {
        return issues;
    }

    public <T extends Issue> List<T> getIssues(Class<T> issueType) {
        List<T> result = new LinkedList<>();
        for (Issue issue : issues) {
            if (issueType.isInstance(issue)) {
                @SuppressWarnings("unchecked")
                T theIssue = (T)issue;
                result.add(theIssue);
            }
        }
        return result;
    }

    public void addStop(Stop stop) {
        stops.put(stop.getName(), stop);
    }
}


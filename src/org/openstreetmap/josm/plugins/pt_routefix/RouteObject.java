package org.openstreetmap.josm.plugins.pt_routefix;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.pt_routefix.issues.Issue;

public abstract class RouteObject {
    private final OsmPrimitive primitive;
    private final String name;
    private final Set<RouteType> taggedRouteTypes = new HashSet<>();
    private final Set<Route> relatedRoutes = new HashSet<>();
    private final Set<RouteType> relatedRouteTypes = new HashSet<>();
    private final Set<Stop> stops = new HashSet<>();
    private final Set<Issue> issues = new HashSet<>();

    public RouteObject(OsmPrimitive primitive) {
        super();
        this.primitive = primitive;
        this.name = null;
        if (primitive.hasTag("bus", "yes")) {
            addTaggedRouteType(RouteType.bus);
        }
    }

    public RouteObject(OsmPrimitive primitive, String name) {
        super();
        this.primitive = primitive;
        this.name = name;
        if (primitive.hasTag("bus", "yes")) {
            addTaggedRouteType(RouteType.bus);
        }
    }

    public OsmPrimitive getPrimitive() {
        return primitive;
    }

    public String getName() {
        String result = primitive.get("name");
        return result == null ? name : result;
    }

    public void addStop(Stop stop) {
        this.stops.add(stop);
    }

    public boolean hasStops() {
        return !stops.isEmpty();
    }

    public Set<Stop> getStops() {
        return stops;
    }

    public void addIssue(Issue issue) {
        issues.add(issue);
    }
    
    public boolean hasIssues() {
        return !issues.isEmpty();
    }
    
    public Collection<Issue> getIssues() {
        return issues;
    }
    
    public abstract RouteMemberType getType();

    public void addTaggedRouteType(RouteType routeType) {
        taggedRouteTypes.add(routeType);
    }

    public Set<RouteType> getTaggedRouteTypes() {
        return taggedRouteTypes;
    }

    public void addRelatedRoute(Route route) {
        relatedRoutes.add(route);
        relatedRouteTypes.add(route.getType());
    }

    public Set<Route> getRelatedRoutes() {
        return relatedRoutes;
    }

    public Set<RouteType> getRelatedRouteTypes() {
        return relatedRouteTypes;
    }

    @Override
    public int hashCode() {
        return primitive.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof RouteObject))
            return false;
        RouteObject other = (RouteObject) obj;
        return primitive.equals(other.primitive);
    }

    @Override
    public String toString() {
        return primitive.get("name");
    }
}

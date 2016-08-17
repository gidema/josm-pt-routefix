package org.openstreetmap.josm.plugins.pt_routefix;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;

public abstract class RouteMember {
    private final Route route;
    private final RelationMember relationMember;
    private final OsmPrimitive primitive;
    private final RouteObject routeObject; 
//    private final RouteMemberType type;
    private boolean connected;
//    private int error = 0;

    public RouteMember(Route route, RelationMember member, RouteObject routeObject) {
        super();
        this.route = route;
        this.relationMember = member;
        this.primitive =member.getMember();
        this.routeObject = routeObject;
//        this.checkRole();
    }

    public Route getRoute() {
        return route;
    }
    
    public RelationMember getRelationMember() {
        return relationMember;
    }

    public String getName() {
        return primitive.getName();
    }

    public OsmPrimitive getPrimitive() {
        return primitive;
    }

    public String getRole() {
        return relationMember.getRole();
    }

    public RouteObject getRouteObject() {
        return routeObject;
    }

    public RouteMemberType getType() {
        return routeObject.getType();
    }

//    public int getError() {
//        return error;
//    }
    /**
     * Is this member connected to the route's ways?
     * 
     * @return true if this member is connected to at least one of
     *      this route's ways
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Check if this route member's role matches the type of the member.
     * @return
     */
    public abstract TestError checkRole(Test tester);
    
    public void checkConnected() {
        switch (getType()) {
        case Way:
            connected = true;
            break;
        case Unknown:
        case Platform:
        case StopPosition:
            if (primitive.getType() != OsmPrimitiveType.NODE) {
                connected =  false;
                return;
            }
            Node node = (Node) primitive;
            connected =  false;
            for (OsmPrimitive referrer : node.getReferrers()) {
                if (route.getWays().contains(referrer)) {
                    connected =  true;
                    return;
                }
            }
            break;
        default:
            return;
        }
    }
}

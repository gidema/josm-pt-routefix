package org.openstreetmap.josm.plugins.bus_stop;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Pair;

public class PTUtil {
    public static boolean isTaggedAsPTRoute(Relation relation) {
        if (!relation.hasTag("type", "route")) {
            return false;
        }
        return relation.hasTag("route", "bus", "tram");
    }
    
    public static boolean isTaggedAsPlatform(OsmPrimitive primitive) {
        return primitive.hasTag("public_transport", "platform") ||
                primitive.hasTag("highway", "bus_stop", "platform");
    }

    public static boolean isTaggedAsStopPosition(OsmPrimitive primitive) {
        return primitive.hasTag("public_transport", "stop_position");
    }

    public static boolean isTaggedAsWay(OsmPrimitive primitive) {
        if (primitive.getType() != OsmPrimitiveType.WAY) {
            return false;
        }
        if (primitive.hasTag("highway", "platform", "bus_stop")) {
            return false;
        }
        return (primitive.hasKey("highway") ||
                primitive.hasKey("busway") ||
                primitive.hasKey("busway:left") ||
                primitive.hasKey("busway:right") ||
                primitive.hasTag("railway", "rail", "tram", "light_rail", "subway"));
    }
    
    public static RouteMemberType getMemberType(OsmPrimitive primitive) {
        if (isTaggedAsWay(primitive)) {
            return RouteMemberType.Way;
        }
        if (isTaggedAsPlatform(primitive)) {
            return RouteMemberType.Platform;
        }
        if (isTaggedAsStopPosition(primitive)) {
            return RouteMemberType.StopPosition;
        }
        return RouteMemberType.Unknown;
    }
    

    public static boolean isWayRole(String role) {
        return "".equals(role) ||
            "forward".equals(role) ||
            "backward".equals(role);
    }
    
    public static boolean isWayMember(RelationMember member) {
        return isWayRole(member.getRole()) &&
            isTaggedAsWay(member.getMember());
    }

    public static RouteType getRouteType(String type) {
        switch (type) {
        case "bus":
            return RouteType.bus;
        case "tram":
            return RouteType.tram;
        default:
            return null;
        }
    }
    
    /**
     * Find the way in a route that is nearest to a platform.
     *
     * @param platform
     * @param route The route to search
     * @return The nearest way in the route with respect to the platform.
     */
    public static Way findNearestWay(Platform platform, Route route) {
        double minDistance = Double.POSITIVE_INFINITY;
        Way nearestWay = null;
        for (RelationMember member : route.getRelation().getMembers()) {
            if ("".equals(member.getRole()) && member.getType() == OsmPrimitiveType.WAY) {
                Way way = member.getWay();
                double distance = getDistance(member.getWay(), platform.getNode());
                if (distance < minDistance) {
                    nearestWay = way;
                    minDistance = distance;
                }
            }
        }
        return nearestWay;
    }

    /**
     * Calculate the minimum distance between a node and a way.
     * This is done by calculating the distance to the nearest segment
     * 
     * @param way
     * @param node
     * @return
     */
    public static double getDistance(Way way, Node node) {
        double minSqDistance = Double.POSITIVE_INFINITY;
        EastNorth nodeEn = node.getEastNorth();
        for (Pair<Node, Node> nodePair :way.getNodePairs(false)) {
            EastNorth closestPoint =Geometry.closestPointToSegment(nodePair.a.getEastNorth(), nodePair.b.getEastNorth(), nodeEn);
            minSqDistance = Math.min(minSqDistance, closestPoint.distanceSq(nodeEn));
        }
        return Math.sqrt(minSqDistance);
    }
    
    public static class NearestWaySegment {
        private double distance = Double.POSITIVE_INFINITY;
        private int index;
        private Pair<Node, Node> segment;
        
        public NearestWaySegment(Way way, Node node) {
            super();
            double minSqDistance = Double.POSITIVE_INFINITY;
            EastNorth nodeEn = node.getEastNorth();
            int i = 0;
            for (Pair<Node, Node> nodePair :way.getNodePairs(false)) {
                EastNorth closestPoint = closestPoint(nodePair, nodeEn);
                double dist = closestPoint.distance(nodeEn);
                if (dist < distance) {
                    distance = dist;
                    segment = nodePair;
                    index = i;
                }
                i++;
            }
            distance = Math.sqrt(minSqDistance);
        }

        public int getIndex() {
            return index;
        }

        public Pair<Node, Node> getSegment() {
            return segment;
        }
    }

    /**
     * Find the point on the segment that is closest to the provided point.
     * @see Geometry.closestPointToSegment()
     * 
     * @param segment
     * @param point
     * @return
     */
    public static EastNorth closestPoint(Pair<Node, Node> segment, EastNorth point) {
        return Geometry.closestPointToSegment(segment.a.getEastNorth(), segment.b.getEastNorth(), point);
    }
    
}

package org.openstreetmap.josm.plugins.pt_routefix;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.pt_routefix.issues.DuplicateStopPositionOnRouteIssue;

public class RouteFactory {
    private final PTData ptData;
//    private final RouteMemberFactory memberFactory;

    public RouteFactory(PTData ptData) {
        super();
        this.ptData = ptData;
//        this.memberFactory = new RouteMemberFactory(ptData);
    }
    
    public Route createRoute(Relation relation) {
        if (!PTUtil.isTaggedAsPTRoute(relation)) {
            return null;
        }
        Route route = new Route(relation);
        processMembers(route);
        checkConnections(route);
        processStops(route);
        return route;
    }
    
    private void processMembers(Route route) {
        Relation relation = route.getRelation();
        
        List<RouteMember> members = new LinkedList<>();
        List<Way> ways = new LinkedList<>();
        for (RelationMember member : relation.getMembers()) {
                OsmPrimitive primitive = member.getMember();
                RouteMemberType memberType = PTUtil.getMemberType(primitive);
                switch (memberType) {
                case Way:
                    ways.add(member.getWay());
                    break;
                case Platform:
                    Platform platform = ptData.getPlatform(primitive);
                    if (platform != null) {
                        route.addPlatform(platform);
                        platform.addRelatedRoute(route);
                        members.add(new PlatformRouteMember(route, member, platform));
                    }
                    break;
                case StopPosition:
                    StopPosition stopPosition = ptData.getStopPosition(primitive);
                    if (stopPosition != null) {
                        route.addStopPosition(stopPosition);
                        stopPosition.addRelatedRoute(route);
                        members.add(new StopPositionRouteMember(route, member, stopPosition));
                    }
                    break;
                case Unknown:
                    break;
                default:
                    break;
                }
        }
        route.setMembers(members);
    }
    
    private static void checkConnections(Route route) {
        for (RouteMember routeMember : route.getMembers()) {
            routeMember.checkConnected();
        }
    }
    
    private void processStops(Route route) {
        for (Platform platform : route.getPlatforms()) {
            String stopName = platform.getName();
            List<DuplicateStopPositionOnRouteIssue> issues = route.getIssues(DuplicateStopPositionOnRouteIssue.class);
            boolean hasIssue = false;
            if (!issues.isEmpty()) {
                for (DuplicateStopPositionOnRouteIssue issue : issues) {
                    if (issue.getName().equals(stopName)) {
                        hasIssue = true;
                        break;
                    }
                }
            }
            if (!hasIssue) {
                StopPosition stopPosition = route.getStopPosition(stopName);
                if (stopPosition != null) {
                    Stop stop = new Stop(platform, stopPosition);
                    Stop existingStop = ptData.getStop(stop);
                    if (existingStop == null) {
                        ptData.add(stop);
                    }
                    else {
                        stop = existingStop;
                    }
                    route.addStop(stop);
                    platform.addStop(stop);
//                stopPosition.addStop(stop);
                }
            }
        }
    }
}

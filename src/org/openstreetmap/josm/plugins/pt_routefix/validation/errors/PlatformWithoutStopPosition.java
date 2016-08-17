package org.openstreetmap.josm.plugins.pt_routefix.validation.errors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.PTData;
import org.openstreetmap.josm.plugins.pt_routefix.PTUtil;
import org.openstreetmap.josm.plugins.pt_routefix.Platform;
import org.openstreetmap.josm.plugins.pt_routefix.Route;
import org.openstreetmap.josm.plugins.pt_routefix.RouteType;
import org.openstreetmap.josm.plugins.pt_routefix.Stop;
import org.openstreetmap.josm.plugins.pt_routefix.StopPosition;
import org.openstreetmap.josm.plugins.pt_routefix.PTUtil.NearestWaySegment;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Pair;

public class PlatformWithoutStopPosition extends TestError {
    private final Platform platform;
    private final PTData ptData;

    public PlatformWithoutStopPosition(PTRouteTest tester, Platform platform) {
        super(tester, Severity.WARNING, I18n.tr("PT Platform without stop_postion"),
            PTRouteTest.PLATFORM_WITHOUT_STOP_POSITION, platform.getPrimitive());
        this.platform = platform;
        this.ptData = tester.getPtData();
    }

    @Override
    public boolean isFixable() {
        // This error is only fixable if at least one of the routes for
        // this platform is complete.
        return getCompleteRoute() != null;
    }

    @Override
    public Command getFix() {
        Route route = getCompleteRoute();
        if (route == null) {
            return null;
        }
        Way nearestWay = findNearestWay(platform, route);
        return createStopPosition(nearestWay);
    }
    
    private Route getCompleteRoute() {
        for (Route route : platform.getRelatedRoutes()) {
            if (!route.isInComplete()) {
                return route;
            }
        }
        return null;
    }

    private Command createStopPosition(Way way) {
        NearestWaySegment nws = new NearestWaySegment(way, platform.getNode());
        Pair<Node, Node> segment = nws.getSegment();
        Node node;
        List<Command> commands = new LinkedList<>();
        if (PTUtil.isTaggedAsStopPosition(segment.a)) {
            node = segment.a;
        }
        else if (PTUtil.isTaggedAsStopPosition(segment.b)) {
            node = segment.b;
        }
        else {
            EastNorth targetPoint = PTUtil.closestPoint(segment, platform.getNode().getEastNorth());
            if (targetPoint.equals(platform.getNode().getEastNorth())) {
                // The busstop is connected to the highway already
                // skip this case for now
                return null;
            }
            if (targetPoint.equals(segment.a.getEastNorth())) {
                node = segment.a;
            }
            else if (targetPoint.equals(segment.b.getEastNorth())) {
                node = segment.a;
            }
            else {
                node = new Node(targetPoint);
                commands.add(new AddCommand(node));
                List<Node> newNodes = new ArrayList<>(way.getNodesCount() + 1);
                int i = 0;
                for (Node n : way.getNodes()) {
                    newNodes.add(n);
                    if (i == nws.getIndex()) {
                        newNodes.add(node);
                    }
                    i++;
                }
                commands.add(new ChangeNodesCommand(way, newNodes));
            }
        }
        StopPosition stopPosition = ptData.getStopPosition(node);
        if (stopPosition == null) {
            stopPosition = new StopPosition(node, platform.getName());
        }
        if (!platform.getName().equals(stopPosition.getName())) {
            return null;
        }
        Stop stop = new Stop(platform, stopPosition);
        platform.addStop(stop);
        stopPosition.addStop(stop);
        Map<String, String> newTags = new HashMap<>();
        newTags.put("name", platform.getName());
        for (RouteType routeType : platform.getRelatedRouteTypes()) {
            newTags.put(routeType.name(), "yes");
        }
        newTags.put("public_transport", "stop_position");
        commands.add(new ChangePropertyCommand(Collections.singletonList(node), newTags));
        for (Route route : platform.getRelatedRoutes()) {
            Command command = addStopPositionToRoute(route, stopPosition, platform);
            if (command != null) {
                commands.add(command);
            }
        }
        return new SequenceCommand("Add stop_postion", commands);
//        Main.main.undoRedo.add(new SequenceCommand("Add stop_postion", commands));
    }

    private static Command addStopPositionToRoute(Route route, StopPosition stopPosition, Platform platform) {
        Relation routeRel = route.getRelation();
        if (routeRel.getMemberPrimitivesList().indexOf(stopPosition.getPrimitive()) > -1) {
            return null;
        }
        Relation newRoute = new Relation();
        newRoute.cloneFrom(routeRel);
        int index = routeRel.getMemberPrimitivesList().indexOf(platform.getPrimitive());
        newRoute.addMember(index, new RelationMember("stop", stopPosition.getPrimitive()));
        return new ChangeCommand(routeRel, newRoute);
    }

    private static Way findNearestWay(Platform platform, Route route) {
        double minDistance = Double.POSITIVE_INFINITY;
        Way nearestWay = null;
        for (RelationMember member : route.getRelation().getMembers()) {
            if ("".equals(member.getRole()) && member.getType() == OsmPrimitiveType.WAY) {
                Way way = member.getWay();
                double distance = PTUtil.getDistance(member.getWay(), platform.getNode());
                if (distance < minDistance) {
                    nearestWay = way;
                    minDistance = distance;
                }
            }
        }
        return nearestWay;
    }
    
}

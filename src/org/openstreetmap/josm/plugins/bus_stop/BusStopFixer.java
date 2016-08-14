package org.openstreetmap.josm.plugins.bus_stop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Pair;

public class BusStopFixer {
    private Route route;
    
    public BusStopFixer(Relation route) {
        super();
        this.route = new Route(route);
    }

    public void run() {
        final DataSet dataSet;
        Relation relation = route.getRelation();
//        if (relation.hasIncompleteMembers()) {
//            OsmServerReader objectReader = new OsmServerObjectReader(relation.getPrimitiveId(), true /* full download */);
//            try {
//                dataSet = objectReader.parseOsm(null);
//                if (dataSet == null) return;
//                objectReader = null;
//                final OsmDataLayer layer = Main.getLayerManager().getEditLayer();
//                
//                SwingUtilities.invokeAndWait(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                layer.mergeFrom(dataSet);
//                                layer.onPostDownloadFromServer();
//                                Main.map.repaint();
//                            }
//                        }
//                );
//            } catch (InvocationTargetException | InterruptedException | OsmTransferException e) {
//                JOptionPane.showMessageDialog(Main.panel, 
//                I18n.tr("An error occurred while downloading the route members"));
//                return;
//            }

//            Future<?> future = Main.worker.submit(new DownloadRelationTask(Collections.singletonList(relation), Main.main.getEditLayer()));
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException e) {
//                JOptionPane.showMessageDialog(Main.panel, 
//                    I18n.tr("An error occurred while downloading the route members"));
//                return;
//            }
//        }
//        for (Stop stop : route.getStops()) {
//            fixStop(stop);
//        }
    }

    private void fixStop(Stop stop) {
//        if (stop.isValid() && !stop.isComplete()) {
//            if (stop.getStopPosition() == null) {
//                createStopPosition(stop.getPlatform());
//            }
//        }
    }

    private void createStopPosition(Platform platform) {
        Way nearestWay = findNearestWay(platform, route);
        createStopPosition(platform, nearestWay);
    }
    
    private void createStopPosition(Platform platform, Way way) {
        NearestWaySegment nws = new NearestWaySegment(way, platform.getNode());
        Pair<Node, Node> segment = nws.getSegment();
        Node stopPosition;
        List<Command> commands = new LinkedList<>();
        if ("stop_position".equals(segment.a.get("public_transport"))) {
            stopPosition = segment.a;
        }
        else if ("stop_position".equals(segment.b.get("public_transport"))) {
            stopPosition = segment.b;
        }
        else {
            EastNorth targetPoint = closestPoint(segment, platform.getNode().getEastNorth());
            if (targetPoint.equals(platform.getNode().getEastNorth())) {
                // The busstop is connected to the highway already
                // skip this case for now
                return;
            }
            if (targetPoint.equals(segment.a.getEastNorth())) {
                stopPosition = segment.a;
            }
            else if (targetPoint.equals(segment.b.getEastNorth())) {
                stopPosition = segment.a;
            }
            else {
                stopPosition = new Node(targetPoint);
                commands.add(new AddCommand(stopPosition));
                List<Node> newNodes = new ArrayList<>(way.getNodesCount() + 1);
                int i = 0;
                for (Node node : way.getNodes()) {
                    newNodes.add(node);
                    if (i == nws.getIndex()) {
                        newNodes.add(stopPosition);
                    }
                    i++;
                }
                commands.add(new ChangeNodesCommand(way, newNodes));
            }
        }
        Map<String, String> newTags = new HashMap<>();
        newTags.put("name", platform.getName());
        newTags.put("bus", "yes");
        newTags.put("public_transport", "stop_position");
        commands.add(new ChangePropertyCommand(Collections.singletonList(stopPosition), newTags));
        Command command = addStopPositionToRoute(stopPosition, platform.getNode());
        if (command != null) {
            commands.add(command);
        }
        Main.main.undoRedo.add(new SequenceCommand("Add stop_postion", commands));
        
    }
    
//    private static Collection<? extends Command> addStopPositionToRoutes(
//            Platform platform, Node stopPosition) {
//        List<Command> commands = new LinkedList<>();
//        for (Relation route : platform.getRoutes()) {
//            Command command = addStopPositionToRoute(stopPosition, route, platform.getNode());
//            if (command != null) {
//                commands.add(command);
//            }
//        }
//        return commands;
//    }
//
    private Command addStopPositionToRoute(Node stopPosition, Node platform) {
        Relation routeRel = route.getRelation();
        if (routeRel.getMemberPrimitivesList().indexOf(stopPosition) > -1) {
            return null;
        }
        Relation newRoute = new Relation();
        newRoute.cloneFrom(routeRel);
        int index = routeRel.getMemberPrimitivesList().indexOf(platform);
        newRoute.addMember(index, new RelationMember("stop", stopPosition));
        return new ChangeCommand(routeRel, newRoute);
    }

    private static Way findNearestWay(Platform platform, Route route) {
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
    private static double getDistance(Way way, Node node) {
        double minSqDistance = Double.POSITIVE_INFINITY;
        EastNorth nodeEn = node.getEastNorth();
        for (Pair<Node, Node> nodePair :way.getNodePairs(false)) {
            EastNorth closestPoint =Geometry.closestPointToSegment(nodePair.a.getEastNorth(), nodePair.b.getEastNorth(), nodeEn);
            minSqDistance = Math.min(minSqDistance, closestPoint.distanceSq(nodeEn));
        }
        return Math.sqrt(minSqDistance);
    }
    
    static EastNorth closestPoint(Pair<Node, Node> segment, EastNorth point) {
        return Geometry.closestPointToSegment(segment.a.getEastNorth(), segment.b.getEastNorth(), point);
    }
    
    private static class NearestWaySegment {
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

}

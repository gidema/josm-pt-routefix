package org.openstreetmap.josm.plugins.pt_routefix;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.tools.Utils;

public class Platform extends RouteObject {
    private Node node;

    public Platform(OsmPrimitive primitive) {
        super(primitive);
        node = (Node) (primitive instanceof Node ? primitive : null);
        if (primitive.hasTag("bus", "yes") ||
                primitive.hasTag("highway", "bus_stop")) {
            addTaggedRouteType(RouteType.bus);
        }
    }

    @Override
    public RouteMemberType getType() {
        return RouteMemberType.Platform;
    }

    public Node getNode() {
        return node;
    }
    
    public Map<String, String> getMissingTags() {
        Map<String, String> tags = new HashMap<>();
        if (!getPrimitive().hasTag("public_transport", "platform")) {
            tags.put("public_transport", "platform");
        }
        for (RouteType routeType : getRelatedRouteTypes()) {
            if (!getPrimitive().hasTag(routeType.toString(), "yes")) {
                tags.put(routeType.toString(), "yes");
                addTaggedRouteType(routeType);
            }
        }
        return tags;
    }
    
    public boolean isConnectedToRoute(Route route) {
        if (node == null) {
            return false;
        }
        boolean connected = false;
        for (final Way way : Utils.filteredCollection(node.getReferrers(), Way.class)) {
            for (RelationMember member : route.getRelation().getMembers()) {
                if (!member.getRole().equals("platform") && member.getMember() == way) {
                    connected = true;
                    break;
                }
           }
        }
        return connected;
    }
}

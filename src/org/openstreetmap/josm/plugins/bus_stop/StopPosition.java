package org.openstreetmap.josm.plugins.bus_stop;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

public class StopPosition extends RouteObject {
    public StopPosition(OsmPrimitive primitive) {
        super(primitive);
    }

    public StopPosition(OsmPrimitive primitive, String name) {
        super(primitive, name);
    }

    @Override
    public RouteMemberType getType() {
        return RouteMemberType.StopPosition;
    }

    public Node getNode() {
        try {
            return Node.class.cast(getPrimitive());
        }
        catch (ClassCastException e) {
            return null;
        }
    }
    
    public boolean isValid() {
        return getPrimitive().getType() == OsmPrimitiveType.NODE;
    }

    public Map<String, String> getMissingTags() {
        Map<String, String> tags = new HashMap<>();
        if (!getPrimitive().hasTag("public_transport", "stop_position")) {
            tags.put("public_transport", "stop_postion");
        }
        for (RouteType routeType : getRelatedRouteTypes()) {
            if (!getPrimitive().hasTag(routeType.toString(), "yes")) {
                tags.put(routeType.toString(), "yes");
                addTaggedRouteType(routeType);
            }
        }
        return tags;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

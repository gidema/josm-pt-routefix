package org.openstreetmap.josm.plugins.pt_routefix.issues;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.Route;
import org.openstreetmap.josm.plugins.pt_routefix.StopPosition;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class DuplicateStopPositionOnRouteIssue implements Issue {
    private Route route;
    private String name;
    private List<StopPosition> stopPositions;

    public DuplicateStopPositionOnRouteIssue(Route route, String name,
            List<StopPosition> stopPositions) {
        super();
        this.route = route;
        this.name = name;
        this.stopPositions = stopPositions;
    }

    public Route getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public List<StopPosition> getStopPositions() {
        return stopPositions;
    }
    
    @Override
    public TestError getError(Test tester) {
        List<OsmPrimitive> primitives = new LinkedList<>();
        primitives.add(route.getRelation());
        for (StopPosition stopPosition : stopPositions) {
            primitives.add(stopPosition.getPrimitive());
        }
        return new TestError(tester, Severity.WARNING,
            I18n.tr("PT Route contains more than 1 stop position with the same name"),
            PTRouteTest.DUPLICATE_STOP_POSITION, primitives);
    }

}

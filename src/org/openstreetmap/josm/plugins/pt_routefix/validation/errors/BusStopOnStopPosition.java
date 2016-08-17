package org.openstreetmap.josm.plugins.pt_routefix.validation.errors;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class BusStopOnStopPosition extends TestError {

    public BusStopOnStopPosition(Test tester, Node node) {
        super(tester, Severity.ERROR, I18n.tr("'highway=busstop can't be " +
            "combined with 'public_transport=stop_position'"),
            PTRouteTest.BUSSTOP_ON_STOP_POSITION, node);
    }
}

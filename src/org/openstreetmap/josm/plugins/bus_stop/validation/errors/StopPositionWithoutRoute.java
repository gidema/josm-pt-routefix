package org.openstreetmap.josm.plugins.bus_stop.validation.errors;

import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.bus_stop.StopPosition;
import org.openstreetmap.josm.plugins.bus_stop.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class StopPositionWithoutRoute extends TestError {

    public StopPositionWithoutRoute(PTRouteTest tester, StopPosition stopPosition) {
        super(tester, Severity.WARNING, I18n.tr("PT Stop position not used in any route"),
            PTRouteTest.STOP_POSITION_WITHOUT_ROUTE, stopPosition.getPrimitive());
    }

    @Override
    public boolean isFixable() {
        return false;
    }
}

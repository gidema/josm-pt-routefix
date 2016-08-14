package org.openstreetmap.josm.plugins.bus_stop.validation.errors;

import java.util.Arrays;

import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.bus_stop.Platform;
import org.openstreetmap.josm.plugins.bus_stop.Route;
import org.openstreetmap.josm.plugins.bus_stop.Stop;
import org.openstreetmap.josm.plugins.bus_stop.StopPosition;
import org.openstreetmap.josm.plugins.bus_stop.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class RouteWithMissingStopPosition extends TestError {
    private final Route route;
    private final Stop stop;

    public RouteWithMissingStopPosition(PTRouteTest tester, Route route, Stop stop) {
        super(tester, Severity.WARNING, I18n.tr("PT Route without stop_postion for platform."),
            PTRouteTest.PLATFORM_WITHOUT_STOP_POSITION, Arrays.asList(route.getRelation(),stop.getPlatform().getPrimitive()));
        this.route = route;
        this.stop = stop;
    }

    @Override
    public boolean isFixable() {
        return true;
    }

    @Override
    public Command getFix() {
        Platform platform = stop.getPlatform();
        StopPosition stopPosition = stop.getStopPosition();
        return addStopPositionToRoute(route, stopPosition, platform);
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
}

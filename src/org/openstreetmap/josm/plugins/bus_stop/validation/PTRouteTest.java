package org.openstreetmap.josm.plugins.bus_stop.validation;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.plugins.bus_stop.PTData;
import org.openstreetmap.josm.plugins.bus_stop.PTUtil;
import org.openstreetmap.josm.plugins.bus_stop.Platform;
import org.openstreetmap.josm.plugins.bus_stop.Route;
import org.openstreetmap.josm.plugins.bus_stop.RouteFactory;
import org.openstreetmap.josm.plugins.bus_stop.RouteMember;
import org.openstreetmap.josm.plugins.bus_stop.StopPosition;
import org.openstreetmap.josm.plugins.bus_stop.issues.Issue;
import org.openstreetmap.josm.plugins.bus_stop.validation.errors.MissingPlatformTags;
import org.openstreetmap.josm.plugins.bus_stop.validation.errors.MissingStopPositionTags;
import org.openstreetmap.josm.plugins.bus_stop.validation.errors.PlatformWithoutRoute;
import org.openstreetmap.josm.plugins.bus_stop.validation.errors.PlatformWithoutStopPosition;
import org.openstreetmap.josm.plugins.bus_stop.validation.errors.StopPositionWithoutRoute;

public class PTRouteTest extends Test {
    public static final int BUSSTOP_ON_NONE_NODE = 13651;
    public static final int STOPPOSITION_ON_NONE_NODE = 13652;
    public static final int MISSING_PLATFORM_TAGS = 13653;
    public static final int MISSING_STOP_POSITION = 13654;
    public static final int BUSSTOP_ON_STOP_POSITION = 13655;
    public static final int INVALID_MEMBER_ROLE = 13656;
    public static final int PLATFORM_WITH_INVALID_ROLE = 13657;
    public static final int PLATFORM_WITHOUT_STOP_POSITION = 13668;
    public static final int MISSING_STOP_POSITION_TAGS = 13669;
    public static final int PLATFORM_WITHOUT_ROUTE = 13670;
    public static final int STOP_POSITION_WITHOUT_ROUTE = 13671;
    public static final int DUPLICATE_PLATFORM = 13672;
    public static final int DUPLICATE_STOP_POSITION = 13673;

    private final PTData ptData;
    private final RouteFactory routeFactory;
    private final Collection<Relation> routeRelations = new LinkedList<>();

//    protected static class PlatformError extends TestError {
//
//        public PlatformError(PTRouteTest tester, int code, OsmPrimitive p, String message) {
//            this(tester, code, Collections.singleton(p), message);
//        }
//
//        public PlatformError(PTRouteTest tester, int code, Collection<OsmPrimitive> collection, String message) {
//            this(tester, code, collection, message, null, null);
//        }
//
//        public PlatformError(PTRouteTest tester, int code, Collection<OsmPrimitive> collection, String message,
//                String description, String englishDescription) {
//            this(tester, code, Severity.WARNING, collection, message, description, englishDescription);
//        }
//
//        public PlatformError(PTRouteTest tester, int code, Severity severity, Collection<OsmPrimitive> collection, String message,
//                String description, String englishDescription) {
//            super(tester, severity, message, description, englishDescription, code, collection);
//        }
//
//        @Override
//        public boolean isFixable() {
//            switch (getCode()) {
//            case MISSING_PLATFORM_TAGS:
//            case MISSING_STOP_POSITION:
//                return true;
//            }
//            return false;
//        }
//    }

    public PTRouteTest() {
        super(tr("Platform"), tr("Checks for errors in platforms."));
        ptData = new PTData();
        routeFactory = new RouteFactory(ptData);
    }

    
    public PTData getPtData() {
        return ptData;
    }

    @Override
    public void startTest(ProgressMonitor monitor) {
        super.startTest(monitor);
        routeRelations.clear();
        ptData.clear();
    }

    @Override
    public void visit(Node n) {
        if (PTUtil.isTaggedAsPlatform(n)) {
            Platform platform = new Platform(n);
            ptData.add(platform);
        }
        else if (PTUtil.isTaggedAsStopPosition(n)) {
            StopPosition stopPosition = new StopPosition(n);
            ptData.add(stopPosition);
        }
    }

    @Override
    public void visit(Way w) {
        checkStopPositionOnNoneNode(w);
        checkBusStopOnNoneNode(w);
    }

    @Override
    public void visit(Relation r) {
        checkBusStopOnNoneNode(r);
        checkStopPositionOnNoneNode(r);
        if (PTUtil.isTaggedAsPTRoute(r)) {
            routeRelations.add(r);
        }
    }

    @Override
    public void endTest() {
        for (Relation relation : routeRelations) {
            Route route = routeFactory.createRoute(relation);
            assert (route != null);
            ptData.add(route);
        }
        for (Platform platform : ptData.getPlatforms()) {
            checkMissingTags(platform);
            checkPlatformWithoutRoute(platform);
        }
        for (StopPosition stopPosition : ptData.getStopPositions()) {
            checkMissingTags(stopPosition);
            checkStopPositionWithoutRoute(stopPosition);
        }
        for (Route route : ptData.getRoutes()) {
            checkRoute(route);
        }
        for (Platform platform : ptData.getPlatforms()) {
            if (!platform.hasStops()) {
                TestError error = new PlatformWithoutStopPosition(this, platform);
                errors.add(error);
            }
            else {
                for (Route route : platform.getRelatedRoutes()) {
//                    if (!stop.getStopPosition().getRelatedRoutes().contains(route)) {
//                        TestError error = new RouteWithMissingStopPosition(this, route, stop);
//                        errors.add(error);
//                    }
                }
            }
        }
    }

    private void checkBusStopOnNoneNode(OsmPrimitive primitive) {
        if (primitive.hasTag("highway", "bus_stop")) {
            List<OsmPrimitive> errorList = Collections.singletonList(primitive);
            errors.add(new TestError(this, Severity.ERROR,
                tr("PT highway=busstop is only allowed on Nodes"), BUSSTOP_ON_NONE_NODE, errorList));
        }
    }

    private void checkStopPositionOnNoneNode(OsmPrimitive primitive) {
        if (primitive.hasTag("public_transport", "stop_position")) {
            List<OsmPrimitive> errorList = Collections.singletonList(primitive);
            errors.add(new TestError(this, Severity.ERROR,
                    tr("public_transport=stop_position is only allowed on Nodes"), STOPPOSITION_ON_NONE_NODE, errorList));
        }
    }

    private void checkRoute(Route route) {
        if (!route.isInComplete()) {
            for (RouteMember member : route.getMembers()) {
                TestError error = member.checkRole(this);
                if (error != null) {
                    errors.add(error);
                }
            }
        }
        for (Issue issue : route.getIssues()) {
            errors.add(issue.getError(this));
        }
    }


    private void checkMissingTags(Platform platform) {
        if (!platform.getMissingTags().isEmpty()) {
            errors.add(new MissingPlatformTags(this, platform));
        }
    }
    
    private void checkMissingTags(StopPosition stopPosition) {
        if (!stopPosition.getMissingTags().isEmpty()) {
            errors.add(new MissingStopPositionTags(this, stopPosition));
        }
    }
    
    private void checkPlatformWithoutRoute(Platform platform) {
        if (platform.getRelatedRoutes().isEmpty()) {
            errors.add(new PlatformWithoutRoute(this, platform));
        }
    }
    
    private void checkStopPositionWithoutRoute(StopPosition stopPosition) {
        if (stopPosition.getRelatedRoutes().isEmpty()) {
            errors.add(new StopPositionWithoutRoute(this, stopPosition));
        }
    }
}

package org.openstreetmap.josm.plugins.pt_routefix.issues;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.Platform;
import org.openstreetmap.josm.plugins.pt_routefix.Route;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;

public class DuplicatePlatformOnRouteIssue implements Issue {
    private Route route;
    private String name;
    private List<Platform> platforms;

    public DuplicatePlatformOnRouteIssue(Route route, String name,
            List<Platform> platforms) {
        super();
        this.route = route;
        this.name = name;
        this.platforms = platforms;
    }

    public Route getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }
    
    @Override
    public TestError getError(Test tester) {
        List<OsmPrimitive> primitives = new LinkedList<>();
        primitives.add(route.getRelation());
        for (Platform platform : platforms) {
            primitives.add(platform.getPrimitive());
        }
        return new TestError(tester, Severity.WARNING,
            I18n.tr("PT Route contains more than 1 platform with the same name"),
            PTRouteTest.DUPLICATE_PLATFORM, primitives);
    }
}

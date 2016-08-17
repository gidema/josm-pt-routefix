package org.openstreetmap.josm.plugins.pt_routefix.validation.errors;

import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.Platform;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class PlatformWithoutRoute extends TestError {
    public PlatformWithoutRoute(PTRouteTest tester, Platform platform) {
        super(tester, Severity.WARNING, I18n.tr("PT Platform not used in any route."),
            PTRouteTest.PLATFORM_WITHOUT_ROUTE, platform.getPrimitive());
    }
}

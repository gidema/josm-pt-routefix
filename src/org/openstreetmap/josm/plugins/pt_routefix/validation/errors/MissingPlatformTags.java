package org.openstreetmap.josm.plugins.pt_routefix.validation.errors;

import java.util.Collections;
import java.util.Map;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.Platform;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class MissingPlatformTags extends TestError {
    private Platform platform;
    
    public MissingPlatformTags(Test tester, Platform platform) {
        super(tester, Severity.WARNING, I18n.tr("PT Platform is missing tags."),
            PTRouteTest.MISSING_PLATFORM_TAGS, platform.getPrimitive());
        this.platform = platform;
    }

    @Override
    public boolean isFixable() {
        return true;
    }

    @Override
    public Command getFix() {
        Map<String, String> tags = platform.getMissingTags();
        OsmPrimitive primitive = platform.getPrimitive();
        return new ChangePropertyCommand(Collections.singleton(primitive), tags);
    }
}

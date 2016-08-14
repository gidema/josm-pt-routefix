package org.openstreetmap.josm.plugins.bus_stop.validation.errors;

import java.util.Collections;
import java.util.Map;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.bus_stop.StopPosition;
import org.openstreetmap.josm.plugins.bus_stop.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class MissingStopPositionTags extends TestError {
    private StopPosition stopPosition;
    
    public MissingStopPositionTags(Test tester, StopPosition stopPosition) {
        super(tester, Severity.WARNING, I18n.tr("PT Stop position is missing tags."),
            PTRouteTest.MISSING_STOP_POSITION_TAGS, stopPosition.getPrimitive());
        this.stopPosition = stopPosition;
    }

    @Override
    public boolean isFixable() {
        return true;
    }

    @Override
    public Command getFix() {
        Map<String, String> tags = stopPosition.getMissingTags();
        OsmPrimitive primitive = stopPosition.getPrimitive();
        return new ChangePropertyCommand(Collections.singleton(primitive), tags);
    }
}

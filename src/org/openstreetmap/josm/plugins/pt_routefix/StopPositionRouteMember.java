package org.openstreetmap.josm.plugins.pt_routefix;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.validation.errors.StopPositionWithInvalidRole;

public class StopPositionRouteMember extends RouteMember {
    private final static List<String> VALID_ROLES =
            Arrays.asList("stop", "stop_exit_only", "stop_entry_only");

    public StopPositionRouteMember(Route route, RelationMember member, StopPosition stopPosition) {
        super(route, member, stopPosition);
    }

    @Override
    public TestError checkRole(Test tester) {
        if (!VALID_ROLES.contains(getRole())) {
            return new StopPositionWithInvalidRole(tester, this);
        }
        return null;
    }
}

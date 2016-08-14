package org.openstreetmap.josm.plugins.bus_stop;

import java.util.Arrays;
import java.util.List;

import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.bus_stop.validation.errors.PlatformWithInvalidRole;

public class PlatformRouteMember extends RouteMember {
    private final static List<String> VALID_ROLES =
            Arrays.asList("platform", "platform_exit_only", "platform_entry_only");
    
    public PlatformRouteMember(Route route, RelationMember member, Platform platform) {
        super(route, member, platform);
    }

    @Override
    public TestError checkRole(Test tester) {
        if (!VALID_ROLES.contains(getRole())) {
            return new PlatformWithInvalidRole(tester, this);
        }
        return null;
    }
}

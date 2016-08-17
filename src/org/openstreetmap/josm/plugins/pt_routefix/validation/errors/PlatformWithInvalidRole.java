package org.openstreetmap.josm.plugins.pt_routefix.validation.errors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.ChangeRelationMemberRoleCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.RouteMember;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class PlatformWithInvalidRole extends TestError {
    private RouteMember member;
    
    public PlatformWithInvalidRole(Test tester, RouteMember member) {
        super(tester, Severity.ERROR, I18n.tr("PT: Platform has invalid role in route relation."),
            PTRouteTest.INVALID_MEMBER_ROLE, Arrays.asList(
                member.getRoute().getRelation(), member.getPrimitive()));
        this.member = member;
    }

    @Override
    public boolean isFixable() {
        return true;
    }

    @Override
    public Command getFix() {
        if (!member.isConnected()) {
            // Fix by setting the role to "platform"
            Relation relation = member.getRoute().getRelation();
            int position = relation.getMembers().indexOf(member.getRelationMember());
            return new ChangeRelationMemberRoleCommand(
                relation, position, "platform");
        }
        // Fix by changing this object into a stop_position
        Map<String, String> tags = new HashMap<>();
        tags.put("public_transport", "stop_position");
        tags.put("highway", null);
        return new ChangePropertyCommand(Collections.singleton(member.getPrimitive()),
                tags);
    }
}

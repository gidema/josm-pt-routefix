package org.openstreetmap.josm.plugins.pt_routefix.validation.errors;

import java.util.Arrays;

import org.openstreetmap.josm.command.ChangeRelationMemberRoleCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.pt_routefix.RouteMember;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class StopPositionWithInvalidRole extends TestError {
    private RouteMember member;
    
    public StopPositionWithInvalidRole(Test tester, RouteMember member) {
        super(tester, Severity.ERROR, I18n.tr("PT: Stop position has invalid role in route relation."),
            PTRouteTest.INVALID_MEMBER_ROLE, Arrays.asList(
                member.getRoute().getRelation(), member.getPrimitive()));
        this.member = member;
    }

    @Override
    public boolean isFixable() {
        return member.isConnected();
    }

    @Override
    public Command getFix() {
        if (member.isConnected()) {
            // Fix by setting the role to "stop"
            Relation relation = member.getRoute().getRelation();
            int position = relation.getMembers().indexOf(member.getRelationMember());
            return new ChangeRelationMemberRoleCommand(
                relation, position, "stop");
        }
        return null;
    }
}

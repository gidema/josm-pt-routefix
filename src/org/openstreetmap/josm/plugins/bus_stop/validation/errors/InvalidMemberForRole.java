package org.openstreetmap.josm.plugins.bus_stop.validation.errors;

import java.util.Arrays;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.validation.Severity;
import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;
import org.openstreetmap.josm.plugins.bus_stop.RouteMember;
import org.openstreetmap.josm.plugins.bus_stop.validation.PTRouteTest;
import org.openstreetmap.josm.tools.I18n;

public class InvalidMemberForRole extends TestError {
    private RouteMember member;
    
    public InvalidMemberForRole(Test tester, RouteMember member) {
        super(tester, Severity.ERROR, I18n.tr("The role for the member in the in route is not valid",
                member.getRole(), member.getName(), member.getRoute().getName()),
            PTRouteTest.INVALID_MEMBER_ROLE, Arrays.asList(
                member.getRoute().getRelation(), member.getPrimitive()));
        this.member = member;
    }

    @Override
    public boolean isFixable() {
        switch (member.getType()) {
        case Platform:
            if ("stop".equals(anObject)member.getRole()
        }
        // TODO Auto-generated method stub
        return getFix() != null;
    }

    @Override
    public Command getFix() {
        Command command = new 
        // TODO Auto-generated method stub
        return super.getFix();
    }
    
    
}

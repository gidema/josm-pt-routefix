package org.openstreetmap.josm.plugins.bus_stop;

import org.openstreetmap.josm.data.osm.RelationMember;

@Deprecated
public class RouteMemberFactory {
    private PTData ptData;
    
    public RouteMemberFactory(PTData ptData) {
        super();
        this.ptData = ptData;
    }

    public StopPositionRouteMember createStopPositionMember(Route route, RelationMember member) {
        StopPosition stopPositionm = ptData.getStopPosition(member.getMember());
        if (stopPositionm != null) {
            stopPositionm.addRelatedRoute(route);
//          Stop stop = stopPosition.getStop();
//          if (stop == null) {
//              stop = route.getStop(stopPosition.getName());
//              if (stop == null) {
//                  stop = new Stop(stopPosition.getName());
//              }
//              stopPosition.setStop(stop);
//          }
//          stop.addRouteObject(stopPosition);
            return new StopPositionRouteMember(route, member, stopPositionm);
        }
        return null;
    }

    
    public PlatformRouteMember createPlatformMember(Route route, RelationMember member) {
        Platform platform = ptData.getPlatform(member.getMember());
        if (platform != null) {
            platform.addRelatedRoute(route);
//          Stop stop = platform.getStop();
//          if (stop == null) {
//              stop = route.getStop(platform.getName());
//              if (stop == null) {
//                  stop = new Stop(platform.getName());
//              }
//              platform.setStop(stop);
//          }
//          stop.addRouteObject(platform);

            return new PlatformRouteMember(route, member, platform);
        }
        return null;
    }
}

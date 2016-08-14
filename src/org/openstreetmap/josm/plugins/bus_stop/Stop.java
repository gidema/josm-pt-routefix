package org.openstreetmap.josm.plugins.bus_stop;

import java.util.Objects;

public class Stop {
    private String name;
    private Platform platform;
    private StopPosition stopPosition;
//    private boolean valid = true;

    public Stop(String name) {
        super();
        this.name = name;
    }

    public Stop(Platform platform, StopPosition stopPosition) {
        assert platform != null && stopPosition != null;
        assert platform.getName().equals(stopPosition.getName());
        this.platform = platform;
        this.stopPosition = stopPosition;
        this.name = platform.getName();
    }

    public String getName() {
        return name;
    }
    
//    public void setPlatform(Platform platform) {
//        if (this.platform != null) {
//            JOptionPane.showMessageDialog(Main.panel, I18n.tr(
//                "This route has more than one platform with the name '{0}'.\n" +
//                "The stop with this name will be ignored.", name));
//            valid = false;
//        }
//        else {
//            this.platform = platform;
//        }
//    }
    
//    public void setStopPosition(StopPosition stopPosition) {
//        if (this.stopPosition != null) {
//            JOptionPane.showMessageDialog(Main.panel, I18n.tr(
//                "This route has more than one stop position with the name '{0}'.\n" +
//                "The stop with this name will be ignored.", name));
//            valid = false;
//        }
//        else {
//            this.stopPosition = stopPosition;
//        }
//    }
    
    public Platform getPlatform() {
        return platform;
    }

    public StopPosition getStopPosition() {
        return stopPosition;
    }
    
//    public boolean isValid() {
//        return valid;
//    }
    
//    public boolean isComplete() {
//        return platform != null && stopPosition != null;
//    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, stopPosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Stop)) return false;
        Stop otherStop = (Stop)obj;
        return Objects.equals(otherStop.platform, platform) &&
            Objects.equals(otherStop.stopPosition, stopPosition);
    }
}

package org.openstreetmap.josm.plugins.pt_routefix;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.validation.OsmValidator;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.pt_routefix.validation.PTRouteTest;

public class BusStopPlugin extends Plugin {
    private JMenu menu;

    public BusStopPlugin(PluginInformation info) {
        super(info);
        initializeMenu();
        OsmValidator.addTest(PTRouteTest.class);
    }

    private void initializeMenu() {
        if (menu == null) {
            menu = Main.main.menu.addMenu("Bus Stop", "Bus Stop", KeyEvent.VK_UNDEFINED,
                    4, ht("/Plugin/Bus_stop"));
            menu.add(new FixBusStopAction());
        }
    }
    
    public JMenu getMenu() {
        return menu;
    }
}

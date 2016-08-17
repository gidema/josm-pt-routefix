package org.openstreetmap.josm.plugins.pt_routefix;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.tools.I18n;

public class FixBusStopAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
//    private Map<String, StopArea> stopAreas = new HashMap<>();
     
    public FixBusStopAction() {
        super("Fix bus stops");
        super.putValue("description", "Adapt bus stops to new public transport standard.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Layer layer = Main.getLayerManager().getActiveLayer();
        if (!(layer instanceof OsmDataLayer)) {
            JOptionPane.showMessageDialog(Main.parent, I18n.tr(
                 "Please select an Osm data layer first"), "No osm layer selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DataSet data = ((OsmDataLayer) layer).data;
        boolean invalid = (data.getAllSelected().size() != 1);
        OsmPrimitive route = null;
        if (!invalid) {
            route = data.getAllSelected().iterator().next();
            invalid = !(OsmPrimitiveType.RELATION == route.getType()
                && "route".equals(route.get("type"))
                && "bus".equals(route.get("route")));
        }
        if (!invalid) {
            BusStopFixer busStopFixer = new BusStopFixer((Relation)route);
            busStopFixer.run();
        }
        else {
            JOptionPane.showMessageDialog(Main.panel, I18n.tr(
                    "Please select exactly 1 bus route relation"));
            return;
        }
    }
}
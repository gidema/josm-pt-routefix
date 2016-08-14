package org.openstreetmap.josm.plugins.bus_stop.issues;

import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;

public interface Issue {

    TestError getError(Test tester);

}

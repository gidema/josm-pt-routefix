package org.openstreetmap.josm.plugins.pt_routefix.issues;

import org.openstreetmap.josm.data.validation.Test;
import org.openstreetmap.josm.data.validation.TestError;

public interface Issue {

    TestError getError(Test tester);

}

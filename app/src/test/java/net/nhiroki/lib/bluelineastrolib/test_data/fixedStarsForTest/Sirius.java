package net.nhiroki.lib.bluelineastrolib.test_data.fixedStarsForTest;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.FixedStar;

public class Sirius extends FixedStar {
    public Sirius() {
        // https://ja.wikipedia.org/wiki/%E3%82%B7%E3%83%AA%E3%82%A6%E3%82%B9
        super(Math.toRadians((6.0 + 45.0 / 60.0 + 08.91728 / 3600.0) * 15.0),
                Math.toRadians(-(16.0 + 42.0 / 60.0 + 58.0171 / 3600.0)));
    }
}

package net.nhiroki.lib.bluelineastrolib.test_data.fixedStarsForTest;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.FixedStar;

public class Polaris extends FixedStar {
    public Polaris() {
        // https://ja.wikipedia.org/wiki/%E3%83%9D%E3%83%A9%E3%83%AA%E3%82%B9_(%E6%81%92%E6%98%9F)
        super(Math.toRadians((2.0 + 31.0 / 60.0 + 49.09456 / 4600.0) * 15.0),
                Math.toRadians(89.0 + 15.0 / 60.0 + 50.7923 / 3600.0));
    }
}

package net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.earth.Earth;
import net.nhiroki.lib.bluelineastrolib.earth.TimePointOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;

import java.time.Instant;


/**
 * The moon seen from the earth
 */
public class Moon implements AstronomicalObject {
    // https://en.wikipedia.org/wiki/Moon
    private static final double MEAN_RADIUS_KM = 1737.4;


    @Override
    public double calculateRightAscensionRad(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        double eclipticTilt = Earth.calculateEclipticTiltRad(t);

        double longitude = this.calculateEclipticLongitudeRad(t);
        double latitude = this.calculateEclipticLatitudeRad(t);

        double U = Math.cos(latitude) * Math.cos(longitude);
        double V = -Math.sin(latitude) * Math.sin(eclipticTilt) + Math.cos(latitude) * Math.sin(longitude) * Math.cos(eclipticTilt);

        if (Math.abs(U) < 1e-20) {
            if (U > 0.0) {
                if (V > 0.0) {
                    return Math.PI / 2.0;
                } else {
                    return - Math.PI / 2.0;
                }
            } else {
                if (V > 0.0) {
                    return - Math.PI / 2.0;
                } else {
                    return Math.PI / 2.0;
                }
            }
        }

        double ret = Math.atan(V / U);
        if (U < 0.0) {
            ret += Math.PI;
        }
        ret -= 2.0 * Math.PI * Math.floor(ret / (2.0 * Math.PI));
        return ret;
    }

    @Override
    public double calculateDeclinationRad(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        double eclipticTilt = Earth.calculateEclipticTiltRad(t);

        double longitude = this.calculateEclipticLongitudeRad(t);
        double latitude = this.calculateEclipticLatitudeRad(t);

        double U = Math.cos(latitude) * Math.cos(longitude);
        double V = -Math.sin(latitude) * Math.sin(eclipticTilt) + Math.cos(latitude) * Math.sin(longitude) * Math.cos(eclipticTilt);
        double W = Math.sin(latitude) * Math.cos(eclipticTilt) + Math.cos(latitude) * Math.sin(longitude) * Math.sin(eclipticTilt);

        return Math.atan(W / Math.sqrt(U * U + V * V));
    }

    @Override
    public double calculateEquatorialHorizontalParallaxRad(Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
        //   Trigonometric Series for the Coordinates of the Objects in the Solar System
        //   Yoshio Kubo
        //
        // And there is distribution table that compared in certain time points from 1972 to 1981, which only a little number of cases had difference of more than 0.2" compared to the Japanese Emphemeris.
        // The author expects this distribution to be retained throughout the 60 years centering at 2000.
        double retDeg = 0.0;
        retDeg += 0.000005 * Math.cos(Math.toRadians( 405201.0   * T + 140.0));
        retDeg += 0.000006 * Math.cos(Math.toRadians(  99863.0   * T + 212.0));
        retDeg += 0.000006 * Math.cos(Math.toRadians( 485333.0   * T + 276.0));
        retDeg += 0.000007 * Math.cos(Math.toRadians(1808933.0   * T + 148.0));
        retDeg += 0.000007 * Math.cos(Math.toRadians(2322131.0   * T + 281.0));
        retDeg += 0.000009 * Math.cos(Math.toRadians( 790672.0   * T + 204.0));
        retDeg += 0.000010 * Math.cos(Math.toRadians(1745069.0   * T + 114.0));
        retDeg += 0.000011 * Math.cos(Math.toRadians( 858602.0   * T + 219.0));
        retDeg += 0.000011 * Math.cos(Math.toRadians(1908795.0   * T + 180.0));
        retDeg += 0.000012 * Math.cos(Math.toRadians(2258267.0   * T + 246.0));
        retDeg += 0.000013 * Math.cos(Math.toRadians( 401329.0   * T +   4.0));
        retDeg += 0.000013 * Math.cos(Math.toRadians( 341337.0   * T + 106.0));
        retDeg += 0.000013 * Math.cos(Math.toRadians(1403732.0   * T + 188.0));
        retDeg += 0.000019 * Math.cos(Math.toRadians(1267871.0   * T + 339.0));
        retDeg += 0.000023 * Math.cos(Math.toRadians( 553069.0   * T + 266.0));
        retDeg += 0.000026 * Math.cos(Math.toRadians( 818536.0   * T + 241.0));
        retDeg += 0.000029 * Math.cos(Math.toRadians( 990397.0   * T +  87.0));
        retDeg += 0.000030 * Math.cos(Math.toRadians(  75870.0   * T + 131.0));
        retDeg += 0.000031 * Math.cos(Math.toRadians( 922466.0   * T + 253.0));
        retDeg += 0.000033 * Math.cos(Math.toRadians( 541062.0   * T + 349.0));
        retDeg += 0.000034 * Math.cos(Math.toRadians( 918399.0   * T + 272.0));
        retDeg += 0.000041 * Math.cos(Math.toRadians( 481266.0   * T + 295.0));
        retDeg += 0.000063 * Math.cos(Math.toRadians( 449334.0   * T + 278.0));
        retDeg += 0.000064 * Math.cos(Math.toRadians(1331734.0   * T +  13.0));
        retDeg += 0.000073 * Math.cos(Math.toRadians(1781068.0   * T + 111.0));
        retDeg += 0.000078 * Math.cos(Math.toRadians(1844932.0   * T + 146.0));
        retDeg += 0.000083 * Math.cos(Math.toRadians( 926533.0   * T +  53.0));
        retDeg += 0.000084 * Math.cos(Math.toRadians(  63864.0   * T + 214.0));
        retDeg += 0.000103 * Math.cos(Math.toRadians( 826671.0   * T + 201.0));
        retDeg += 0.000111 * Math.cos(Math.toRadians(  35999.0   * T + 178.0));
        retDeg += 0.000167 * Math.cos(Math.toRadians(1303870.0   * T + 336.0));
        retDeg += 0.000173 * Math.cos(Math.toRadians(1431597.0   * T +  45.0));
        retDeg += 0.000197 * Math.cos(Math.toRadians( 489205.0   * T + 232.0));
        retDeg += 0.000263 * Math.cos(Math.toRadians( 513198.0   * T + 312.0));
        retDeg += 0.000271 * Math.cos(Math.toRadians( 445267.0   * T + 118.0));
        retDeg += 0.000319 * Math.cos(Math.toRadians( 441199.8   * T + 137.4));
        retDeg += 0.000400 * Math.cos(Math.toRadians( 377336.3   * T + 103.2));
        retDeg += 0.000531 * Math.cos(Math.toRadians( 854535.2   * T + 238.2));
        retDeg += 0.000858 * Math.cos(Math.toRadians(1367733.1   * T +  10.7));
        retDeg += 0.002824 * Math.cos(Math.toRadians( 954397.74  * T + 269.93));
        retDeg += 0.007842 * Math.cos(Math.toRadians( 890534.22  * T + 235.70));
        retDeg += 0.009530 * Math.cos(Math.toRadians( 413335.35  * T + 100.74));
        retDeg += 0.051820 * Math.cos(Math.toRadians( 477198.868 * T + 134.963));
        retDeg += 0.950725;

        return Math.toRadians(retDeg);
    }

    @Override
    public double calculateApparentRadiusRad(Instant t) {
        return Math.atan(MEAN_RADIUS_KM / this.calculateDistanceFromTheEarthKM(t));
    }

    @Override
    public double estimatedIncrementOfRightAscensionRadPerDay(Instant t) throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        // Calculate directly
        double ret = this.calculateRightAscensionRad(t.plusSeconds(43200))
                - this.calculateRightAscensionRad(t.minusSeconds(43200));

        // The right ascension of the moon increases about 13 degrees per day,
        // so the change in a day does not exceed 180 degrees
        ret -= Math.floor((ret + Math.PI) / (2.0 * Math.PI)) * 2.0 * Math.PI;

        return ret;
    }

    public double calculateDistanceFromTheEarthAU(Instant t) {
        return Earth.calculateDistanceAUByEquatorialHorizontalParallaxRad(this.calculateEquatorialHorizontalParallaxRad(t));
    }

    public double calculateDistanceFromTheEarthKM(Instant t) {
        return this.calculateDistanceFromTheEarthAU(t) * Sun.AU_IN_KM;
    }

    private double calculateEclipticLongitudeRad (Instant t) {
        return Math.toRadians(this.calculateEclipticLongitudeDeg(t));
    }

    public double calculateEclipticLongitudeDeg (Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
        //   Trigonometric Series for the Coordinates of the Objects in the Solar System
        //   Yoshio Kubo
        //
        // Looks like the writer of this paper expects the precision about 0.1' for 1970-2030 for Moon
        // And there is distribution table that compared in certain time points from 1972 to 1981, which only a little number of cases had difference of more than 10" compared to the Japanese Emphemeris.
        // The author expects this distribution to be retained throughout the 60 years centering at 2000.
        double retDeg = 0.0;
        retDeg += 0.0003 * Math.cos(Math.toRadians(2322131.0 * T + 191.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians(   4067.0 * T +  70.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians( 549197.0 * T + 220.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians(1808933.0 * T +  58.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians( 349472.0 * T + 337.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians( 381404.0 * T + 354.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians( 958465.0 * T + 340.0));
        retDeg += 0.0004 * Math.cos(Math.toRadians(  12006.0 * T + 187.0));
        retDeg += 0.0004 * Math.cos(Math.toRadians(  39871.0 * T + 223.0));
        retDeg += 0.0005 * Math.cos(Math.toRadians( 509131.0 * T + 242.0));
        retDeg += 0.0005 * Math.cos(Math.toRadians(1745069.0 * T +  24.0));
        retDeg += 0.0005 * Math.cos(Math.toRadians(1908795.0 * T +  90.0));
        retDeg += 0.0006 * Math.cos(Math.toRadians(2258267.0 * T + 156.0));
        retDeg += 0.0006 * Math.cos(Math.toRadians( 111869.0 * T +  38.0));
        retDeg += 0.0007 * Math.cos(Math.toRadians(  27864.0 * T + 127.0));
        retDeg += 0.0007 * Math.cos(Math.toRadians( 485333.0 * T + 186.0));
        retDeg += 0.0007 * Math.cos(Math.toRadians( 405201.0 * T +  50.0));
        retDeg += 0.0007 * Math.cos(Math.toRadians( 790672.0 * T + 114.0));
        retDeg += 0.0008 * Math.cos(Math.toRadians(1403732.0 * T +  98.0));
        retDeg += 0.0009 * Math.cos(Math.toRadians( 858602.0 * T + 129.0));
        retDeg += 0.0011 * Math.cos(Math.toRadians(1920802.0 * T + 186.0));
        retDeg += 0.0012 * Math.cos(Math.toRadians(1267871.0 * T + 249.0));
        retDeg += 0.0016 * Math.cos(Math.toRadians(1856938.0 * T + 152.0));
        retDeg += 0.0018 * Math.cos(Math.toRadians( 401329.0 * T + 274.0));
        retDeg += 0.0021 * Math.cos(Math.toRadians( 341337.0 * T +  16.0));
        retDeg += 0.0021 * Math.cos(Math.toRadians(  71998.0 * T +  85.0));
        retDeg += 0.0021 * Math.cos(Math.toRadians( 990397.0 * T + 357.0));
        retDeg += 0.0022 * Math.cos(Math.toRadians( 818536.0 * T + 151.0));
        retDeg += 0.0023 * Math.cos(Math.toRadians( 922466.0 * T + 163.0));
        retDeg += 0.0024 * Math.cos(Math.toRadians(  99863.0 * T + 122.0));
        retDeg += 0.0026 * Math.cos(Math.toRadians(1379739.0 * T +  17.0));
        retDeg += 0.0027 * Math.cos(Math.toRadians( 918399.0 + T + 182.0));
        retDeg += 0.0028 * Math.cos(Math.toRadians(   1934.0 * T + 145.0));
        retDeg += 0.0037 * Math.cos(Math.toRadians( 541062.0 * T + 259.0));
        retDeg += 0.0038 * Math.cos(Math.toRadians(1781068.0 * T +  21.0));
        retDeg += 0.0040 * Math.cos(Math.toRadians(    133.0 * T +  29.0));
        retDeg += 0.0040 * Math.cos(Math.toRadians(1844932.0 * T +  56.0));
        retDeg += 0.0040 * Math.cos(Math.toRadians(1331734.0 * T + 283.0));
        retDeg += 0.0050 * Math.cos(Math.toRadians( 481266.0 * T + 205.0));
        retDeg += 0.0052 * Math.cos(Math.toRadians(  31932.0 * T + 107.0));
        retDeg += 0.0068 * Math.cos(Math.toRadians( 926533.0 * T + 323.0));
        retDeg += 0.0079 * Math.cos(Math.toRadians( 449334.0 * T + 188.0));
        retDeg += 0.0085 * Math.cos(Math.toRadians( 826671.0 * T + 111.0));
        retDeg += 0.0100 * Math.cos(Math.toRadians(1431597.0 * T + 315.0));
        retDeg += 0.0107 * Math.cos(Math.toRadians(1303870.0 * T + 246.0));
        retDeg += 0.0110 * Math.cos(Math.toRadians( 489205.0 * T + 142.0));
        retDeg += 0.0125 * Math.cos(Math.toRadians(1443603.0 * T +  52.0));
        retDeg += 0.0154 * Math.cos(Math.toRadians(  75870.0 * T +  41.0));
        retDeg += 0.0304 * Math.cos(Math.toRadians( 513197.9 * T + 222.5));
        retDeg += 0.0347 * Math.cos(Math.toRadians( 445267.1 * T +  27.9));
        retDeg += 0.0409 * Math.cos(Math.toRadians( 441199.8 * T +  47.4));
        retDeg += 0.0458 * Math.cos(Math.toRadians( 854535.2 * T + 148.2));
        retDeg += 0.0533 * Math.cos(Math.toRadians(1367733.1 * T + 280.7));
        retDeg += 0.0571 * Math.cos(Math.toRadians( 377336.3 * T +  13.2));
        retDeg += 0.0588 * Math.cos(Math.toRadians(  63863.5 * T + 124.2));
        retDeg += 0.1144 * Math.cos(Math.toRadians( 966404.0 * T + 276.5));
        retDeg += 0.1851 * Math.cos(Math.toRadians(  35999.05 * T +  87.53));
        retDeg += 0.2136 * Math.cos(Math.toRadians( 954397.74 * T + 179.93));
        retDeg += 0.6583 * Math.cos(Math.toRadians( 890534.22 * T + 145.70));
        retDeg += 1.2740 * Math.cos(Math.toRadians( 413335.35 * T +  10.74));
        retDeg += 6.2888 * Math.cos(Math.toRadians( 477198.868 * T +  44.963));
        retDeg += 218.3162 + 481267.8809 * T;

        retDeg -= Math.floor(retDeg / 360.0) * 360.0;

        return retDeg;
    }

    private double calculateEclipticLatitudeRad (Instant t) {
        return Math.toRadians(this.calculateEclipticLatitudeDeg(t));
    }

    public double calculateEclipticLatitudeDeg (Instant t) {
        double T = new TimePointOnTheEarth(t).julianYearFromJ2000_0() / 100.0;

        // https://www1.kaiho.mlit.go.jp/kenkyu/report/rhr15/rhr15-06.pdf
        //   Trigonometric Series for the Coordinates of the Objects in the Solar System
        //   Yoshio Kubo
        //
        // Looks like the writer of this paper expects the precision about 0.1' for 1970-2030 for Moon
        // And there is distribution table that compared in certain time points from 1972 to 1981, which only a little numbers of expections exceed 10 seconds.
        // The author expects this distribution to be retained throughout the 60 years centering at 2000.
        double retDeg = 0.0;
        retDeg += 0.0003 * Math.cos(Math.toRadians( 335334.0 * T +  57.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians(1814936.0 * T +  16.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians(2264270.0 * T + 115.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians(1409735.0 * T +  57.0));
        retDeg += 0.0003 * Math.cos(Math.toRadians( 932536.0 * T + 282.0));
        retDeg += 0.0004 * Math.cos(Math.toRadians(1024264.0 * T + 352.0));
        retDeg += 0.0004 * Math.cos(Math.toRadians(2328134.0 * T + 149.0));
        retDeg += 0.0005 * Math.cos(Math.toRadians( 948395.0 * T + 222.0));
        retDeg += 0.0005 * Math.cos(Math.toRadians( 419339.0 * T + 149.0));
        retDeg += 0.0005 * Math.cos(Math.toRadians( 848532.0 * T + 190.0));
        retDeg += 0.0006 * Math.cos(Math.toRadians(1361730.0 * T + 322.0));
        retDeg += 0.0006 * Math.cos(Math.toRadians( 559072.0 * T + 134.0));
        retDeg += 0.0007 * Math.cos(Math.toRadians(1309873.0 * T + 205.0));
        retDeg += 0.0008 * Math.cos(Math.toRadians( 972407.0 * T + 235.0));
        retDeg += 0.0009 * Math.cos(Math.toRadians(1787072.0 * T + 340.0));
        retDeg += 0.0010 * Math.cos(Math.toRadians(1297866.0 * T + 288.0));
        retDeg += 0.0011 * Math.cos(Math.toRadians(1914799.0 * T +  48.0));
        retDeg += 0.0013 * Math.cos(Math.toRadians(  37935.0 * T +  65.0));
        retDeg += 0.0013 * Math.cos(Math.toRadians( 447203.0 * T +   6.0));
        retDeg += 0.0014 * Math.cos(Math.toRadians(  29996.0 * T + 129.0));
        retDeg += 0.0015 * Math.cos(Math.toRadians( 996400.0 * T + 316.0));
        retDeg += 0.0015 * Math.cos(Math.toRadians( 928469.0 * T + 121.0));
        retDeg += 0.0015 * Math.cos(Math.toRadians(  42002.0 * T +  46.0));
        retDeg += 0.0018 * Math.cos(Math.toRadians(1449606.0 * T +  10.0));
        retDeg += 0.0018 * Math.cos(Math.toRadians( 519201.0 * T + 181.0));
        retDeg += 0.0018 * Math.cos(Math.toRadians( 820668.0 * T + 153.0));
        retDeg += 0.0019 * Math.cos(Math.toRadians( 924402.0 * T + 141.0));
        retDeg += 0.0021 * Math.cos(Math.toRadians( 105866.0 * T +  80.0));
        retDeg += 0.0022 * Math.cos(Math.toRadians(1337737.0 * T + 241.0));
        retDeg += 0.0022 * Math.cos(Math.toRadians( 481268.0 * T + 308.0));
        retDeg += 0.0025 * Math.cos(Math.toRadians( 860538.0 * T + 106.0));
        retDeg += 0.0034 * Math.cos(Math.toRadians( 443331.0 * T + 230.0));
        retDeg += 0.0042 * Math.cos(Math.toRadians(1850935.0 * T +  14.0));
        retDeg += 0.0043 * Math.cos(Math.toRadians( 547066.0 * T + 217.0));
        retDeg += 0.0082 * Math.cos(Math.toRadians( 371333.0 * T +  55.0));
        retDeg += 0.0088 * Math.cos(Math.toRadians( 471196.0 * T +  87.0));
        retDeg += 0.0093 * Math.cos(Math.toRadians( 884531.0 * T + 187.0));
        retDeg += 0.0172 * Math.cos(Math.toRadians(1437599.8 * T + 273.2));
        retDeg += 0.0326 * Math.cos(Math.toRadians(1373736.2 * T + 239.0));
        retDeg += 0.0463 * Math.cos(Math.toRadians(  69866.7 * T +  82.5));
        retDeg += 0.0554 * Math.cos(Math.toRadians( 896537.4 * T + 104.0));
        retDeg += 0.1733 * Math.cos(Math.toRadians( 407332.20 * T +  52.43));
        retDeg += 0.2777 * Math.cos(Math.toRadians(   6003.15 * T +  48.31));
        retDeg += 0.2806 * Math.cos(Math.toRadians( 960400.89 * T + 138.24));
        retDeg += 5.1281 * Math.cos(Math.toRadians( 483202.019 * T +   3.273));

        retDeg -= Math.floor(retDeg / 360.0) * 360.0;

        return retDeg;
    }
}

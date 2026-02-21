package net.nhiroki.lib.bluelineastrolib.logic;

import java.time.Duration;
import java.time.Instant;
import org.junit.Test;

import net.nhiroki.lib.bluelineastrolib.astronomical_objects.AstronomicalObject;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Moon;
import net.nhiroki.lib.bluelineastrolib.earth.Earth;
import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;
import net.nhiroki.lib.bluelineastrolib.exceptions.UnsupportedDateRangeException;
import net.nhiroki.lib.bluelineastrolib.test_data.AstroComputingTestDataList;
import net.nhiroki.lib.bluelineastrolib.test_data.fixedStarsForTest.Polaris;
import net.nhiroki.lib.bluelineastrolib.test_data.fixedStarsForTest.Sirius;
import net.nhiroki.lib.bluelineastrolib.location.LocationOnTheEarth;
import net.nhiroki.lib.bluelineastrolib.astronomical_objects.objects.Sun;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.NorthPoleE0Z;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.NullIsland;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.RioDeJaneiro;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.SouthPoleE0Z;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.TokyoNAO;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.TopOfMtFuji;
import net.nhiroki.lib.bluelineastrolib.test_data.locationsForTest.Tromsoe;

import static org.junit.Assert.*;


public class AstronomicalObjectCalculatorTest {
    @Test
    public void checkSunInfoCalculatorBasicFunctionality() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        // https://eco.mtk.nao.ac.jp/koyomi/dni/2024/s1302.html
        // In Tokyo (N35.6581deg, E139.7414deg), on 2024/02/29,
        // Sunrise: 06:12, Culmination 11:54, Sunset 17:35
        LocationOnTheEarth placeToTest = new TokyoNAO();
        Sun sun = new Sun();
        Instant testDay1 = Instant.parse("2024-02-28T15:00:00Z");

        Instant sunriseOnTestDay1 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertEquals(Instant.parse("2024-02-28T21:12:00Z").getEpochSecond(), sunriseOnTestDay1.getEpochSecond(), 30.0);
        Instant culminationOnTestDay1 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay1, placeToTest);
        assertEquals(Instant.parse("2024-02-29T02:54:00Z").getEpochSecond(), culminationOnTestDay1.getEpochSecond(), 30.0);
        Instant sunsetOnTestDay1 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertEquals(Instant.parse("2024-02-29T08:35:00Z").getEpochSecond(), sunsetOnTestDay1.getEpochSecond(), 30.0);
    }

    @Test
    public void checkSunInfoCalculatorBasicFunctionalityLinearSearch() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        // https://eco.mtk.nao.ac.jp/koyomi/dni/2024/s1302.html
        // In Tokyo (N35.6581deg, E139.7414deg), on 2024/02/29,
        // Sunrise: 06:12, Culmination 11:54, Sunset 17:35
        LocationOnTheEarth placeToTest = new TokyoNAO();
        Sun sun = new Sun();
        Instant testDay1 = Instant.parse("2024-02-28T15:00:00Z");

        Instant[] sunriseListOnTestDay1 = AstronomicalObjectCalculator.calculateAllEvents(sun, AstronomicalObjectCalculator.EventDirectionType.RISE,
                testDay1, testDay1.plusSeconds(86400), Duration.ofMinutes(1), Duration.ofMillis(200), placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP,
                true, -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC / 3600.0));
        assertEquals(1, sunriseListOnTestDay1.length);
        assertEquals(Instant.parse("2024-02-28T21:12:00Z").getEpochSecond(), sunriseListOnTestDay1[0].getEpochSecond(), 30.0);

        Instant[] culminationListOnTestDay1 = AstronomicalObjectCalculator.calculateAllEvents(sun, AstronomicalObjectCalculator.EventDirectionType.CULMINATION,
                testDay1, testDay1.plusSeconds(86400), Duration.ofMinutes(1), Duration.ofMillis(200), placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP,
                true, -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC / 3600.0));
        assertEquals(1, culminationListOnTestDay1.length);
        assertEquals(Instant.parse("2024-02-29T02:54:00Z").getEpochSecond(), culminationListOnTestDay1[0].getEpochSecond(), 30.0);

        Instant[] sunsetListOnTestDay1 = AstronomicalObjectCalculator.calculateAllEvents(sun, AstronomicalObjectCalculator.EventDirectionType.SET,
                testDay1, testDay1.plusSeconds(86400), Duration.ofMinutes(1), Duration.ofMillis(200), placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP,
                true, -Math.toRadians(Earth.ATMOSPHERIC_REFRACTION_AT_HORIZON_DEC_SEC / 3600.0));
        assertEquals(1, sunsetListOnTestDay1.length);
        assertEquals(Instant.parse("2024-02-29T08:35:00Z").getEpochSecond(), sunsetListOnTestDay1[0].getEpochSecond(), 30.0);
    }

    @Test
    public void checkSunInfoAvailableThroughoutYearInTokyoWithTimeMeasure() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        LocationOnTheEarth placeToTest = new TokyoNAO();
        Sun sun = new Sun();

        long calcStartTimeMs = System.currentTimeMillis();

        Instant testDay = Instant.parse("2025-12-31T15:00:00Z");

        int daysToCalc = 36525;
        for (int i = 0; i < daysToCalc; ++i) {
            Instant sunrise = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
            assertNotNull(sunrise);
            long sunriseSecFromDay = Duration.between(testDay, sunrise).toSeconds();
            // 04:20-07:00
            assertEquals(5 * 3600 + 40 * 60, sunriseSecFromDay, 80 * 60);

            Instant culmination = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay, placeToTest);
            assertNotNull(culmination);
            long culminationSecFromDay = Duration.between(testDay, culmination).toSeconds();
            assertEquals(11 * 3600 + 40 * 60, culminationSecFromDay, 20 * 60);

            Instant sunset = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
            assertNotNull(sunrise);
            long sunsetSecFromDay = Duration.between(testDay, sunset).toSeconds();
            // 16:15-19:05
            assertEquals(17 * 3600 + 40 * 60, sunsetSecFromDay, 85 * 60);

            testDay = testDay.plusSeconds(86400);
        }

        long calcEndTimeMs = System.currentTimeMillis();

        assertTrue(calcEndTimeMs - calcStartTimeMs < daysToCalc / 20);
    }

    @Test
    public void checkSunInfoCalculatorSouthFunctionality() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        LocationOnTheEarth placeToTest = new RioDeJaneiro();
        Sun sun = new Sun();
        Instant testDay1 = Instant.parse("2024-02-29T03:00:00Z");
        Instant sunriseOnTestDay1 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertEquals(Instant.parse("2024-02-29T09:00:00Z").getEpochSecond(), sunriseOnTestDay1.getEpochSecond(), 3600.0);
        Instant culminationOnTestDay1 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay1, placeToTest);
        assertEquals(Instant.parse("2024-02-29T15:00:00Z").getEpochSecond(), culminationOnTestDay1.getEpochSecond(), 3600.0);
        Instant sunsetOnTestDay1 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertEquals(Instant.parse("2024-02-29T21:00:00Z").getEpochSecond(), sunsetOnTestDay1.getEpochSecond(), 3600.0);
    }

    @Test
    public void checkFirstSunriseJapan_2011() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        LocationOnTheEarth placeToTest = new TopOfMtFuji();
        Sun sun = new Sun();

        Instant testDay1 = Instant.parse("2011-12-31T15:00:00Z");

        Instant sunriseOnTestDay1WithoutHeight = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        // This is from calculation and may have error
        assertEquals(Instant.parse("2011-12-31T21:55:13Z").getEpochSecond(), sunriseOnTestDay1WithoutHeight.getEpochSecond(), 120.0);

        // Referred to the following explanation for first sunrise in Japan in 2012
        // https://www.nao.ac.jp/faq/a0106.html
        // > それでは、島を除くとどうなるでしょう。北海道・本州・四国・九州でいちばん早く初日の出を見られるのは富士山の山頂で、日の出時刻は午前6時42分です。標高が高い場所では平地（標高0mの場所）より日の出が早くなりますので、もっと東にある標高の低い場所よりも、初日の出を先に見ることができるのです。
        Instant sunriseOnTestDay1 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay1, placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertEquals(Instant.parse("2011-12-31T21:42:00Z").getEpochSecond(), sunriseOnTestDay1.getEpochSecond(), 30.0);
    }

    @Test
    public void checkSunInfoCalculatorTromsoe() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        LocationOnTheEarth placeToTest = new Tromsoe();
        Sun sun = new Sun();

        assertNotNull(AstronomicalObjectCalculator.calculateRiseWithin24h(sun, Instant.parse("2026-03-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));
        assertNotNull(AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, Instant.parse("2026-03-20T00:00:00Z"), placeToTest));
        assertNotNull(AstronomicalObjectCalculator.calculateSetWithin24h(sun, Instant.parse("2026-03-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));

        assertNull(AstronomicalObjectCalculator.calculateRiseWithin24h(sun, Instant.parse("2026-06-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));
        assertNotNull(AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, Instant.parse("2026-06-20T00:00:00Z"), placeToTest));
        assertNull(AstronomicalObjectCalculator.calculateSetWithin24h(sun, Instant.parse("2026-06-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));

        assertNotNull(AstronomicalObjectCalculator.calculateRiseWithin24h(sun, Instant.parse("2026-09-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));
        assertNotNull(AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, Instant.parse("2026-09-20T00:00:00Z"), placeToTest));
        assertNotNull(AstronomicalObjectCalculator.calculateSetWithin24h(sun, Instant.parse("2026-09-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));

        assertNull(AstronomicalObjectCalculator.calculateRiseWithin24h(sun, Instant.parse("2026-12-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));
        assertNotNull(AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, Instant.parse("2026-12-20T00:00:00Z"), placeToTest));
        assertNull(AstronomicalObjectCalculator.calculateSetWithin24h(sun, Instant.parse("2026-12-20T00:00:00Z"), placeToTest, true, AstronomicalObjectCalculator.ReferencePoint.TOP));
    }

    @Test
    public void checkSunInfoCalculatorNorthPoleE0_2026() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        LocationOnTheEarth placeToTest = new NorthPoleE0Z();
        Sun sun = new Sun();

        Instant testDay1 = Instant.parse("2026-01-01T00:00:00Z");
        Instant sunriseOnTestDay1 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunriseOnTestDay1);
        // culmination should happen around 12:00 local time, even if sun is never seen
        Instant culminationOnTestDay1 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay1, placeToTest);
        assertEquals(Instant.parse("2026-01-01T12:00:00Z").getEpochSecond(), culminationOnTestDay1.getEpochSecond(), 1800.0);
        Instant sunsetOnTestDay1 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunsetOnTestDay1);

        Instant testDay2 = Instant.parse("2026-07-01T00:00:00Z");
        Instant sunriseOnTestDay2 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay2, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunriseOnTestDay2);
        // culmination should happen around 12:00 local time, even if sun never sets
        Instant culminationOnTestDay2 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay2, placeToTest);
        assertEquals(Instant.parse("2026-07-01T12:00:00Z").getEpochSecond(), culminationOnTestDay2.getEpochSecond(), 1800.0);
        Instant sunsetOnTestDay2 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunsetOnTestDay2);

        Instant testDay3 = Instant.parse("2026-03-18T00:00:00Z");
        Instant sunriseOnTestDay3 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay3, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        // Vernal equinox in 2026 is 2026/03/20 14:45 UTC
        // https://ja.wikipedia.org/wiki/%E6%98%A5%E5%88%86
        // Sunrise on North Pole in vernal equinox should be a bit earlier
        // Sunset on North pole in autumnal equinox should be a bit later
        // timeanddate.com reports that sunrise is at 2026/003/18 12:18
        // https://www.timeanddate.com/sun/@89.99999,0.00000?month=3&year=2026
        // However in this case the slightest difference of sun height which is regarded as "sunrise" changes time so much
        // Thus placing this test which allows 09:00-15:00
        assertEquals(Instant.parse("2026-03-18T12:00:00Z").getEpochSecond(), sunriseOnTestDay3.getEpochSecond(), 10800.0);
        // And placing test based on current implementation so that we can notice the change
        assertEquals(Instant.parse("2026-03-18T10:58:26Z").getEpochSecond(), sunriseOnTestDay3.getEpochSecond(), 5.0);
        // culmination should happen around 12:00 local time, regardless of sun visibility
        Instant culminationOnTestDay3 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay3, placeToTest);
        assertEquals(Instant.parse("2026-03-18T12:00:00Z").getEpochSecond(), culminationOnTestDay3.getEpochSecond(), 1800.0);
        Instant sunsetOnTestDay3 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay3, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunsetOnTestDay3);
        Instant testDay3_1 = Instant.parse("2026-03-17T00:00:00Z");
        Instant sunriseOnTestDay3_1 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay3_1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunriseOnTestDay3_1);
        Instant testDay3_2 = Instant.parse("2026-03-19T00:00:00Z");
        Instant sunriseOnTestDay3_2 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay3_2, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunriseOnTestDay3_2);

        Instant testDay4 = Instant.parse("2026-09-25T00:00:00Z");
        Instant sunriseOnTestDay4 = AstronomicalObjectCalculator.calculateRiseWithin24h(sun, testDay4, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunriseOnTestDay4);
        // culmination should happen around 12:00 local time, regardless of sun visibility
        Instant culminationOnTestDay4 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sun, testDay4, placeToTest);
        assertEquals(Instant.parse("2026-09-25T12:00:00Z").getEpochSecond(), culminationOnTestDay4.getEpochSecond(), 1800.0);
        Instant sunsetOnTestDay4 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay4, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        // Autumnal equinox in 2026 is 2026/09/23 00:05 UTC
        // https://ja.wikipedia.org/wiki/%E7%A7%8B%E5%88%86
        // Sunset on North pole in autumnal equinox should be a bit later
        // timeanddate.com reports that sunset is at 2026/09/25 03:14
        // https://www.timeanddate.com/sun/@89.99999,0.00000?month=9&year=2026
        // However in this case the slightest difference of sun height which is regarded as "sunset" changes time so much
        // Thus placing this test which allows 00:00-06:00
        assertEquals(Instant.parse("2026-09-25T03:00:00Z").getEpochSecond(), sunsetOnTestDay4.getEpochSecond(), 10800.0);
        // And placing test based on current implementation so that we can notice the change
        assertEquals(Instant.parse("2026-09-25T04:39:11Z").getEpochSecond(), sunsetOnTestDay4.getEpochSecond(), 5.0);
        Instant testDay4_1 = Instant.parse("2026-09-24T00:00:00Z");
        Instant sunsetOnTestDay4_1 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay4_1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunsetOnTestDay4_1);
        Instant testDay4_2 = Instant.parse("2026-09-26T00:00:00Z");
        Instant sunsetOnTestDay4_2 = AstronomicalObjectCalculator.calculateSetWithin24h(sun, testDay4_2, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.TOP);
        assertNull(sunsetOnTestDay4_2);
    }

    @Test
    public void checkMoonInfoCalculatorBasicFunctionality() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        // https://eco.mtk.nao.ac.jp/koyomi/dni/2026/m1301.html
        // In Tokyo (N35.6581 deg, E139.7414deg)
        LocationOnTheEarth placeToTest = new TokyoNAO();
        Moon moon = new Moon();

        // 2026/01/01: Moonrise: 14:04, Culmination 21:47, Moonset 04:21
        Instant testDay1 = Instant.parse("2025-12-31T15:00:00Z");
        Instant moonriseOnTestDay1 = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-01-01T05:04:00Z").getEpochSecond(), moonriseOnTestDay1.getEpochSecond(), 30.0);
        Instant culminationOnTestDay1 = AstronomicalObjectCalculator.calculateCulminationWithin24h(moon, testDay1, placeToTest);
        assertEquals(Instant.parse("2026-01-01T12:47:00Z").getEpochSecond(), culminationOnTestDay1.getEpochSecond(), 30.0);
        Instant moonsetOnTestDay1 = AstronomicalObjectCalculator.calculateSetWithin24h(moon, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2025-12-31T19:21:00Z").getEpochSecond(), moonsetOnTestDay1.getEpochSecond(), 30.0);

        // 2026/01/03: Moonrise: 16:06, Culmination --:--, Moonset 06:42
        Instant testDay2 = Instant.parse("2026-01-02T15:00:00Z");
        Instant moonriseOnTestDay2 = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, testDay2, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-01-03T07:16:00Z").getEpochSecond(), moonriseOnTestDay2.getEpochSecond(), 30.0);
        Instant culminationOnTestDay2 = AstronomicalObjectCalculator.calculateCulminationWithin24h(moon, testDay2, placeToTest);
        assertNull(culminationOnTestDay2);
        Instant moonsetOnTestDay2 = AstronomicalObjectCalculator.calculateSetWithin24h(moon, testDay2, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-01-02T21:42:00Z").getEpochSecond(), moonsetOnTestDay2.getEpochSecond(), 30.0);

        // 2026/01/10: Moonrise: --:--, Culmination 04:57, Moonset 10:43
        Instant testDay3 = Instant.parse("2026-01-09T15:00:00Z");
        Instant moonriseOnTestDay3 = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, testDay3, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertNull(moonriseOnTestDay3);
        Instant culminationOnTestDay3 = AstronomicalObjectCalculator.calculateCulminationWithin24h(moon, testDay3, placeToTest);
        assertEquals(Instant.parse("2026-01-09T19:57:00Z").getEpochSecond(), culminationOnTestDay3.getEpochSecond(), 30.0);
        Instant moonsetOnTestDay3 = AstronomicalObjectCalculator.calculateSetWithin24h(moon, testDay3, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-01-10T01:43:00Z").getEpochSecond(), moonsetOnTestDay3.getEpochSecond(), 30.0);

        // 2026/01/26: Moonrise: 10:33, Culmination 17:37, Moonset --:--
        Instant testDay4 = Instant.parse("2026-01-25T15:00:00Z");
        Instant moonriseOnTestDay4 = AstronomicalObjectCalculator.calculateRiseWithin24h(moon, testDay4, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-01-26T01:33:00Z").getEpochSecond(), moonriseOnTestDay4.getEpochSecond(), 30.0);
        Instant culminationOnTestDay4 = AstronomicalObjectCalculator.calculateCulminationWithin24h(moon, testDay4, placeToTest);
        assertEquals(Instant.parse("2026-01-26T08:37:00Z").getEpochSecond(), culminationOnTestDay4.getEpochSecond(), 30.0);
        Instant moonsetOnTestDay4 = AstronomicalObjectCalculator.calculateSetWithin24h(moon, testDay4, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertNull(moonsetOnTestDay4);
    }

    @Test
    public void checkSiriusBehavior() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        Sirius sirius = new Sirius();
        LocationOnTheEarth placeToTest = new TokyoNAO();

        // 2026/02/01: Rise 16:28, Pass: 21:40:24, Set: 02:57
        // With calculating in https://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/riseset_rhip.cgi
        // In Tokyo (N35.6581 deg, E139.7414deg)
        Instant testDay1 = Instant.parse("2026-01-31T15:00:00Z");
        Instant riseOnTestDay1 = AstronomicalObjectCalculator.calculateRiseWithin24h(sirius, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-02-01T07:28:00Z").getEpochSecond(), riseOnTestDay1.getEpochSecond(), 120.0);
        Instant culminationOnTestDay1 = AstronomicalObjectCalculator.calculateCulminationWithin24h(sirius, testDay1, placeToTest);
        assertEquals(Instant.parse("2026-02-01T12:40:24Z").getEpochSecond(), culminationOnTestDay1.getEpochSecond(), 120.0);
        Instant setOnTestDay1 = AstronomicalObjectCalculator.calculateSetWithin24h(sirius, testDay1, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER);
        assertEquals(Instant.parse("2026-01-31T17:57:00Z").getEpochSecond(), setOnTestDay1.getEpochSecond(), 120.0);
    }

    @Test
    public void checkPolarisBehavior() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        Polaris polaris = new Polaris();
        LocationOnTheEarth locations[] = new LocationOnTheEarth[] {
                new TokyoNAO(),
                new TopOfMtFuji(),
                new NorthPoleE0Z(),
                new SouthPoleE0Z(),
                new RioDeJaneiro(),
        };
        for (LocationOnTheEarth placeToTest: locations) {
            Instant t = Instant.parse("2026-01-01T00:00:00Z");
            for (int i = 0; i < 365; ++i) {
                assertNull(AstronomicalObjectCalculator.calculateRiseWithin24h(polaris, t, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER));
                assertNotNull(AstronomicalObjectCalculator.calculateCulminationWithin24h(polaris, t, placeToTest));
                assertNull(AstronomicalObjectCalculator.calculateSetWithin24h(polaris, t, placeToTest, false, AstronomicalObjectCalculator.ReferencePoint.CENTER));
                t = t.plusSeconds(86400);
            }
            assertEquals("2027-01-01T00:00:00Z", t.toString());
        }

        {
            // On the equator of the earth, polaris should move around the horizon, and rise and set every day.
            LocationOnTheEarth nullIsland = new NullIsland();
            Instant t = Instant.parse("2026-01-01T00:00:00Z");
            for (int i = 0; i < 365; ++i) {
                assertNotNull(AstronomicalObjectCalculator.calculateRiseWithin24h(polaris, t, nullIsland, false, AstronomicalObjectCalculator.ReferencePoint.CENTER));
                assertNotNull(AstronomicalObjectCalculator.calculateCulminationWithin24h(polaris, t, nullIsland));
                assertNotNull(AstronomicalObjectCalculator.calculateSetWithin24h(polaris, t, nullIsland, false, AstronomicalObjectCalculator.ReferencePoint.CENTER));
                t = t.plusSeconds(86400);
            }
            assertEquals("2027-01-01T00:00:00Z", t.toString());
        }
    }

    @Test
    public void checkNoCrashOrInfiniteLoopForBunchData() throws AstronomicalPhenomenonComputationException, UnsupportedDateRangeException {
        for (AstronomicalObject astronomicalObject: AstroComputingTestDataList.listAstronomicalObjectsForTest()) {
            for (LocationOnTheEarth locationOnTheEarth: AstroComputingTestDataList.listLocationsForTest()) {
                Instant t = Instant.parse("2026-01-01T00:00:00Z");
                for (int i = 0; i < 5 * 365 * 4 / 3; ++i) {
                    AstronomicalObjectCalculator.calculateRiseWithin24h(astronomicalObject, t, locationOnTheEarth,
                            true, AstronomicalObjectCalculator.ReferencePoint.TOP);
                    AstronomicalObjectCalculator.calculateCulminationWithin24h(astronomicalObject, t, locationOnTheEarth);
                    AstronomicalObjectCalculator.calculateSetWithin24h(astronomicalObject, t, locationOnTheEarth,
                            true, AstronomicalObjectCalculator.ReferencePoint.TOP);

                    t = t.plusSeconds(21600 * 3);
                }
                assertEquals("2030-12-30T18:00:00Z", t.toString());

                t = Instant.parse("-10000-01-01T00:00:00Z");
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 365 / 41 * 4; ++j) {
                        AstronomicalObjectCalculator.calculateRiseWithin24h(astronomicalObject, t, locationOnTheEarth,
                                true, AstronomicalObjectCalculator.ReferencePoint.TOP);
                        AstronomicalObjectCalculator.calculateCulminationWithin24h(astronomicalObject, t, locationOnTheEarth);
                        AstronomicalObjectCalculator.calculateSetWithin24h(astronomicalObject, t, locationOnTheEarth,
                                true, AstronomicalObjectCalculator.ReferencePoint.TOP);

                        t = t.plusSeconds(21600 + 86400 * 10);
                    }
                    t = t.plusSeconds( 86400l * 365l * 4000l);
                }
                assertEquals("+21985-12-09T00:00:00Z", t.toString());
            }
        }
    }
}

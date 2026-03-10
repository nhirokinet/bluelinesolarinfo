package net.nhiroki.lib.bluelineastrolib.tool;

import static org.junit.Assert.assertEquals;

import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;

import org.junit.Test;

import java.time.Instant;

public class SunToolTest {
    @Test
    public void calculateNextTimeOfEclipticLongitudeDegTest() throws AstronomicalPhenomenonComputationException {
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou262.html
        assertEquals(Instant.parse("2026-03-20T14:46:00Z").getEpochSecond(), SunTool.calculateNextTimeOfEclipticLongitudeDeg(Instant.parse("2026-01-01T00:00:00Z"), 0.0).getEpochSecond(), 900.0);
        assertEquals(Instant.parse("2026-06-21T08:25:00Z").getEpochSecond(), SunTool.calculateNextTimeOfEclipticLongitudeDeg(Instant.parse("2026-01-01T00:00:00Z"), 90.0).getEpochSecond(), 900.0);
        assertEquals(Instant.parse("2026-09-23T00:05:00Z").getEpochSecond(), SunTool.calculateNextTimeOfEclipticLongitudeDeg(Instant.parse("2026-01-01T00:00:00Z"), 180.0).getEpochSecond(), 900.0);
        assertEquals(Instant.parse("2026-12-21T20:50:00Z").getEpochSecond(), SunTool.calculateNextTimeOfEclipticLongitudeDeg(Instant.parse("2026-01-01T00:00:00Z"), 270.0).getEpochSecond(), 900.0);
    }
}

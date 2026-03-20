package net.nhiroki.lib.bluelineastrolib.tool;

import static org.junit.Assert.assertEquals;

import net.nhiroki.lib.bluelineastrolib.exceptions.AstronomicalPhenomenonComputationException;

import org.junit.Test;

import java.time.Instant;

public class MoonToolTest {
    @Test
    public void calculatePreviousTimeOfMoonPhaseTest() throws AstronomicalPhenomenonComputationException {
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou263.html
        assertEquals(Instant.parse("2026-01-18T19:52:00Z").getEpochSecond(), MoonTool.calculatePreviousTimeOfMoonPhase(Instant.parse("2026-02-01T09:00:00Z"),   0.0).getEpochSecond(), 60.0);
        assertEquals(Instant.parse("2026-01-26T04:47:00Z").getEpochSecond(), MoonTool.calculatePreviousTimeOfMoonPhase(Instant.parse("2026-02-01T09:00:00Z"),  90.0).getEpochSecond(), 60.0);
        assertEquals(Instant.parse("2026-01-03T10:03:00Z").getEpochSecond(), MoonTool.calculatePreviousTimeOfMoonPhase(Instant.parse("2026-02-01T09:00:00Z"), 180.0).getEpochSecond(), 60.0);
        assertEquals(Instant.parse("2026-01-10T15:48:00Z").getEpochSecond(), MoonTool.calculatePreviousTimeOfMoonPhase(Instant.parse("2026-02-01T09:00:00Z"), 270.0).getEpochSecond(), 60.0);
    }

    @Test
    public void calculateNextTimeOfMoonPhaseTest() throws AstronomicalPhenomenonComputationException {
        // https://eco.mtk.nao.ac.jp/koyomi/yoko/2026/rekiyou263.html
        assertEquals(Instant.parse("2026-01-18T19:52:00Z").getEpochSecond(), MoonTool.calculateNextTimeOfMoonPhase(Instant.parse("2026-01-01T09:00:00Z"),   0.0).getEpochSecond(), 60.0);
        assertEquals(Instant.parse("2026-01-26T04:47:00Z").getEpochSecond(), MoonTool.calculateNextTimeOfMoonPhase(Instant.parse("2026-01-01T09:00:00Z"),  90.0).getEpochSecond(), 60.0);
        assertEquals(Instant.parse("2026-01-03T10:03:00Z").getEpochSecond(), MoonTool.calculateNextTimeOfMoonPhase(Instant.parse("2026-01-01T09:00:00Z"), 180.0).getEpochSecond(), 60.0);
        assertEquals(Instant.parse("2026-01-10T15:48:00Z").getEpochSecond(), MoonTool.calculateNextTimeOfMoonPhase(Instant.parse("2026-01-01T09:00:00Z"), 270.0).getEpochSecond(), 60.0);
    }

    @Test
    public void checkNoCrashForBunchData() throws AstronomicalPhenomenonComputationException {
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        for (int i = 0; i < 20 * 365 * 4 / 3; ++i) {
            for (double phase = 0.0; phase < 360.0; phase += 46.0) {
                MoonTool.calculatePreviousTimeOfMoonPhase(t, phase);
            }
            t = t.plusSeconds(21600 * 3);
        }
        assertEquals("2045-12-26T18:00:00Z", t.toString());

        t = Instant.parse("-10000-01-01T00:00:00Z");
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 365 * 4 / 21; ++j) {
                for (double phase = 0.0; phase < 360.0; phase += 109.0) {
                    MoonTool.calculatePreviousTimeOfMoonPhase(t, phase);
                }
                t = t.plusSeconds(21600 + 86400 * 5);
            }
            t = t.plusSeconds( 86400l * 365l * 2000l);
        }
        assertEquals("+19994-12-16T18:00:00Z", t.toString());
    }
}

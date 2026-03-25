package net.nhiroki.androidlib.bluelineastroandroidlib.moonphase;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import net.nhiroki.bluelinesolarinfo.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RunWith(AndroidJUnit4.class)
public class MoonPhaseRendererTest {
    @Test
    public void generateBitmapOfSingleColorMoonPhaseAndSaveTest() throws IOException, InterruptedException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TypedValue typedValue = new TypedValue();
        Context themedContext = new ContextThemeWrapper(appContext, R.style.Theme_BlueLineSolarInfo);
        themedContext.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

        int primaryColor;
        if (typedValue.resourceId != 0) {
            primaryColor = themedContext.getResources().getColor(typedValue.resourceId, themedContext.getTheme());
        } else {
            primaryColor = typedValue.data;
        }
        int bmpSize = 32;
        double moonPhaseDeg = 50.0;
        Bitmap moonBitmap = MoonPhaseRenderer.generateBitmapOfSingleColorMoonPhase(bmpSize, primaryColor, moonPhaseDeg);

        /*
        // This is not clean test, but by enabling (removeing commenting out) this part, image is generated into /sdcard/Pictures or /sdcard/emulated/0/Pictures
        // You can view it on Device Explorer to use it for widget preview
        // In my understanding, this should need android.permission.WRITE_EXTERNAL_STORAGE permission added in app/src/androidTest/AndroidManifest.xml, but worked without it in my Android Studio environment.

        int currentNightMode = appContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        String nightModeStr = currentNightMode == Configuration.UI_MODE_NIGHT_YES ? "dark" : "light";

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        String title = "moon_phase_" + nightModeStr + bmpSize + "_px_" + moonPhaseDeg + "deg_" + dateStr + ".png";

        File saveTo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), title);
        OutputStream out = new FileOutputStream(saveTo);
        moonBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.close();
        */
    }
}

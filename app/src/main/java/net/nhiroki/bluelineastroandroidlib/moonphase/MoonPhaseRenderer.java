package net.nhiroki.androidlib.bluelineastroandroidlib.moonphase;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MoonPhaseRenderer {
    public static Bitmap generateBitmapOfSingleColorMoonPhase(int size, int color, double moonPhaseDeg) {
        Bitmap moonBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(moonBitmap);
        Paint moonPaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        moonPaint.setColor(color);
        moonPaint.setStyle(Paint.Style.FILL);
        Paint moonCirclePaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        moonCirclePaint.setColor(color);
        moonCirclePaint.setStyle(Paint.Style.FILL);
        moonCirclePaint.setAlpha(50);
        drawMoonPhaseOnCanvas(canvas, moonPhaseDeg, moonPaint, moonCirclePaint);

        return moonBitmap;
    }

    public static void drawMoonPhaseOnCanvas(@NonNull Canvas canvas, double moonPhaseDeg,
                                             @NonNull Paint moonPaint, @Nullable Paint circlePaint) {
        int drawSize = Math.min(canvas.getHeight(), canvas.getWidth());
        double radiusDouble = (double)drawSize / 2.0;
        double moonPhaseRad = Math.toRadians(moonPhaseDeg);

        if (circlePaint != null) {
            Path circlePath = new Path();

            circlePath.moveTo((float)(drawSize / 2), 0);
            for (int y = 1; y <= drawSize; ++y) {
                double yFromCenterRelative = ((double) y / radiusDouble) - 1.0;
                double x = Math.sqrt(1.0 - yFromCenterRelative * yFromCenterRelative);
                circlePath.lineTo((float) (x * radiusDouble + radiusDouble), (float)y);
            }

            for (int y = drawSize - 1; y >= 1; --y) {
                double yFromCenterRelative = ((double) y / radiusDouble) - 1.0;
                double x = - Math.sqrt(1.0 - yFromCenterRelative * yFromCenterRelative);
                circlePath.lineTo((float) (x * radiusDouble + radiusDouble), (float)y);
            }

            circlePath.close();

            canvas.drawPath(circlePath, circlePaint);
        }

        Path moonPath = new Path();

        moonPath.moveTo((float)(drawSize / 2), 0);

        double sinLeftSide = moonPhaseRad > Math.PI ? -1.0 : Math.sin(Math.PI / 2.0 - moonPhaseRad);
        for (int y = 1; y <= drawSize; ++y) {
            double yFromCenterRelative = ((double) y / radiusDouble) - 1.0;
            double x = sinLeftSide * Math.sqrt(1.0 - yFromCenterRelative * yFromCenterRelative);
            moonPath.lineTo((float) (x * radiusDouble + radiusDouble), (float)y);
        }

        double sinRightSide = moonPhaseRad < Math.PI ? 1.0 : Math.sin(Math.PI * 1.5 - moonPhaseRad);
        for (int y = drawSize - 1; y >= 1; --y) {
            double yFromCenterRelative = ((double) y / radiusDouble) - 1.0;
            double x = sinRightSide * Math.sqrt(1.0 - yFromCenterRelative * yFromCenterRelative);
            moonPath.lineTo((float) (x * radiusDouble + radiusDouble), (float)y);
        }
        moonPath.close();
        canvas.drawPath(moonPath, moonPaint);
    }

}

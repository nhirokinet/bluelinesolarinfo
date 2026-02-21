package net.nhiroki.androidlib.bluelineastroandroidlib.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import net.nhiroki.androidlib.bluelineastroandroidlib.moonphase.MoonPhaseRenderer;


public class MoonPhaseView extends View {
    private double moonPhaseDeg = 0.0;
    private boolean moonPhaseSet = false;

    private int circlePaintAlpha = 50;


    public MoonPhaseView(Context context) {
        super(context);
    }

    public MoonPhaseView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MoonPhaseView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    public MoonPhaseView(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
    }

    public void setMoonPhaseDeg(double moonPhaseDeg) {
        this.moonPhaseDeg = moonPhaseDeg;
        this.moonPhaseSet = true;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (! this.moonPhaseSet) {
            return;
        }

        int primaryColor = this.calculatePaintedColor();

        Paint paint = new Paint((Paint.ANTI_ALIAS_FLAG));
        paint.setColor(primaryColor);
        paint.setStyle(Paint.Style.FILL);
        Paint backgroundPaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        backgroundPaint.setColor(primaryColor);
        backgroundPaint.setAlpha(circlePaintAlpha);
        backgroundPaint.setStyle(Paint.Style.FILL);

        MoonPhaseRenderer.drawMoonPhaseOnCanvas(canvas, this.moonPhaseDeg, paint, backgroundPaint);
    }

    private int calculatePaintedColor() {
        TypedValue typedValue = new TypedValue();
        this.getContext().getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

        int primaryColor;
        if (typedValue.resourceId != 0) {
            primaryColor = getResources().getColor(typedValue.resourceId, this.getContext().getTheme());
        } else {
            primaryColor = typedValue.data;
        }

        return primaryColor;
    }
}

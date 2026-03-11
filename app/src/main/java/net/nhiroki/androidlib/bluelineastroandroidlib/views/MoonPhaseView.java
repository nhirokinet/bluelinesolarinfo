package net.nhiroki.androidlib.bluelineastroandroidlib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import net.nhiroki.androidlib.bluelineastroandroidlib.moonphase.MoonPhaseRenderer;
import net.nhiroki.bluelinesolarinfo.R;


// app/src/main/res/values/moonphaseview_attr.xml is required
public class MoonPhaseView extends View {
    private float moonPhaseDeg = -1.0f;

    private int circlePaintAlpha = 50;


    public MoonPhaseView(Context context) {
        super(context);
        init(context, null);
    }

    public MoonPhaseView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public MoonPhaseView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context, attributeSet);
    }

    public MoonPhaseView(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        init(context, attributeSet);
    }

    public void setMoonPhaseDeg(float moonPhaseDeg) {
        this.moonPhaseDeg = moonPhaseDeg;
        this.invalidate();
    }

    private void init(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray arr = context.obtainStyledAttributes(attributeSet, R.styleable.MoonPhaseView);
            moonPhaseDeg = arr.getFloat(R.styleable.MoonPhaseView_moonPhaseDeg, -1.0f);
            arr.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.moonPhaseDeg < -0.01) {
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

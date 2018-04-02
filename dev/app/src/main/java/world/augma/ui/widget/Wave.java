package world.augma.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import world.augma.R;

public class Wave extends View {

    private final float OFFSET_THRESHOLD = 3.4028235E38F;
    private final int WAVE_HEIGHT = 16;
    private final float WAVE_LENGTH = 1.5F;
    private final float WAVE_FREQUENCY = 0.05F;
    private final float MAX_X_SPACE_ALLOWED = 20.0F;

    private Path topWavePath;
    private Path bottomWavePath;
    private Paint topWavePaint;
    private Paint bottomWavePaint;
    private int topWaveColor;
    private int bottomWaveColor;
    private int waveLevel;
    private float waveLength;
    private int waveHeight;
    private float maxRight;
    private float waveFrequency;
    private float topOffset;
    private float bottomOffset;
    private Wave.Refresher refresher;
    private int left;
    private int right;
    private int bottom;
    private double omega;

    public Wave(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, com.john.waveview.R.attr.waveViewStyle);
    }

    public Wave(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        topWavePath = new Path();
        bottomWavePath = new Path();
        topWavePaint = new Paint();
        bottomWavePaint = new Paint();
        topWaveColor = Color.WHITE;
        bottomWaveColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null);

        //Initialize
        waveHeight = WAVE_HEIGHT;
        waveFrequency = WAVE_FREQUENCY;
        topOffset = 0.0F;
        bottomOffset = (float) waveHeight * 0.4F;
        setLayoutParams(new LinearLayout.LayoutParams(-1, waveHeight * 2));

        topWavePaint.setColor(topWaveColor);
        topWavePaint.setStyle(Paint.Style.FILL);
        topWavePaint.setAntiAlias(true);

        bottomWavePaint.setColor(bottomWaveColor);
        bottomWavePaint.setAlpha(0);
        bottomWavePaint.setStyle(Paint.Style.FILL);
        bottomWavePaint.setAntiAlias(true);
    }

    public Paint getTopWavePaint() {
        return topWavePaint;
    }

    public Paint getBottomWavePaint() {
        return bottomWavePaint;
    }

    /**
     * This function fluctuates/moves the wave via offsets by
     * frequency amount.
     */
    private void fluctuate() {
        if(bottomOffset > OFFSET_THRESHOLD) {
            bottomOffset = 0.0F;
        } else {
            bottomOffset += waveFrequency;
        }

        if(topOffset > OFFSET_THRESHOLD) {
            topOffset = 0.0F;
        } else {
            topOffset += waveFrequency;
        }
    }

    /**
     * Calculates the new path for the next iteration of refresher after invalidation.
     */
    private void regeneratePaths() {
        topWavePath.reset();
        bottomWavePath.reset();
        fluctuate();
        topWavePath.moveTo((float) left, (float) bottom);

        float y;
        float x;

        for(x = 0.0F; x <= maxRight; x += MAX_X_SPACE_ALLOWED){
            y = (float) ((double) waveHeight * Math.sin(omega * (double) x + (double) topOffset) + (double) waveHeight);
            topWavePath.lineTo(x, y);
        }

        topWavePath.lineTo((float) right, (float) bottom);
        bottomWavePath.lineTo((float) left, (float) bottom);

        for(x = 0.0F; x <= maxRight; x += MAX_X_SPACE_ALLOWED) {
            y = (float) ((double) waveHeight * Math.sin(omega * (double) x + (double) bottomOffset) + (double) waveHeight);
            bottomWavePath.lineTo(x, y);
        }

        bottomWavePath.lineTo((float) right, (float) bottom);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        if(visibility == GONE) {
            removeCallbacks(refresher);
        } else {
            removeCallbacks(refresher);
            refresher = new Wave.Refresher();
            post(refresher);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if(hasWindowFocus && waveLength == 0.0F) {
            startFluctuation();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(waveLength == 0.0F) {
            startFluctuation();
        }
    }

    private void startFluctuation() {
        if(getWidth() != 0) {
            int width = getWidth();
            waveLength = (float) width * WAVE_LENGTH;
            left = getLeft();
            right = getRight();
            bottom = getBottom() + 2;
            maxRight = (float) right + MAX_X_SPACE_ALLOWED;
            omega = (Math.PI * 2) / (double) waveLength;
        }
    }

    /**
     * On each invalidation and recreation cycle draw top and bottom waves. This will create
     * illusion of wave animation.
     *
     * @param canvas Canvas to print on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(bottomWavePath, bottomWavePaint);
        canvas.drawPath(topWavePath, topWavePaint);
    }

    private class Refresher implements Runnable {

        private Refresher() {}

        @Override
        public void run() {
            Wave var = Wave.this;
            synchronized (var) {
                long start = System.currentTimeMillis();
                var.regeneratePaths();
                var.invalidate();
                long gap = 16L - (System.currentTimeMillis() - start);
                var.postDelayed(this,gap < 0L ? 0L : gap);
            }
        }
    }

}

package world.augma.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class SolidWave extends View {

    private Paint topWavePaint;
    private Paint bottomWavePaint;

    public SolidWave(Context context, AttributeSet attributeSet, Paint topWavePaint, Paint bottomWavePaint) {
        this(context, attributeSet, 0 , topWavePaint, bottomWavePaint);
    }

    public SolidWave(Context context, AttributeSet attributeSet, int style, Paint topWavePaint, Paint bottomWavePaint) {
        super(context, attributeSet, style);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.weight = 1.0F;
        setLayoutParams(params);

        this.topWavePaint = topWavePaint;
        this.bottomWavePaint = bottomWavePaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect((float) getLeft(), 0.0F, (float) getRight(), (float) getBottom(), bottomWavePaint);
        canvas.drawRect((float) getLeft(), 0.0F, (float) getRight(), (float) getBottom(), topWavePaint);
    }
}

package world.augma.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;

public class DragAndScaleView extends LinearLayout {

    private static final int INVALID_POINTER_ID = -1;

    private float posY;
    private float lastY;
    private int activePointerID;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1f;

    public DragAndScaleView(Context context) {
        super(context);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public DragAndScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public DragAndScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public DragAndScaleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Inspect scale events
        scaleGestureDetector.onTouchEvent(event);

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:{
                final float y = event.getY();
                lastY = posY;
                activePointerID = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(activePointerID);
                final float y = event.getY(pointerIndex);

                if (!scaleGestureDetector.isInProgress()) {
                    final float dy = y - lastY;

                    posY += dy;
                    invalidate();
                }

                lastY = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                activePointerID = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                activePointerID = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == activePointerID) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;

                    lastY = event.getY(newPointerIndex);
                    activePointerID = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(0, posY);
        canvas.scale(scaleFactor, scaleFactor);
        //TODO olmazsa icine layout'u at
        canvas.restore();
    }

    private class ScaleListener extends  ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }
}

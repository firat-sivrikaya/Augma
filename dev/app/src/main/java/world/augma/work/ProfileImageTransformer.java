package world.augma.work;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Burak on 24-Mar-18.
 */

public class ProfileImageTransformer extends BitmapTransformation{

    public ProfileImageTransformer(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {

        //The code below transforms any shape of picture into a circular one
        if(toTransform == null) {
            return null;
        }

        int size = Math.min(toTransform.getWidth(), toTransform.getHeight());
        int x = (toTransform.getWidth() - size) / 2;
        int y = (toTransform.getHeight() - size) / 2;

        Bitmap sq = Bitmap.createBitmap(toTransform, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);

        if(result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(sq, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}

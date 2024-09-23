package com.demo.common.text.html.ctrl.img;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

public class HtmlImageGetter implements Html.ImageGetter {
    public static WeakHashMap<Bitmap, String> imgMap;

    private OnImageLoadComplete onImageLoadComplete;
    private int height;
    private int width;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BitmapDrawable drawable = (BitmapDrawable) msg.obj;
            if (onImageLoadComplete != null) onImageLoadComplete.onImageLoadComplete(drawable);
            drawable.invalidateSelf();
        }
    };

    public HtmlImageGetter() {

    }

    public Drawable getDrawable(String src,OnImageLoadComplete onImageLoadComplete, int width, int height){
        this.onImageLoadComplete = onImageLoadComplete;
        this.height = height;
        this.width = width;
        return getDrawable(src);
    }

    @Override
    public Drawable getDrawable(final String source) {

        if (TextUtils.isEmpty(source)) return new ColorDrawable(Color.TRANSPARENT);
        if (imgMap == null) {
            imgMap = new WeakHashMap<>();
        }
        Bitmap bitmap = getDrawableCache(source);
        if (bitmap != null) {
            Bitmap img = Bitmap.createBitmap(bitmap);
            BitmapDrawable drawable = new BitmapDrawable(img);
            setDrawableBound(drawable, width, height);
            return drawable;
        }

        final BitmapDrawable drawable = new BitmapDrawable();
        /*HuangYeService.getImageService().loadBitmap(source, new LoadImageCallback<Bitmap>() {
            @Override
            public void onSuccess(Uri uri, Bitmap bitmap1) {
                //bitmap即为下载所得图片
                if (drawable != null && bitmap1 != null) {
                    if (getDrawableCache(source) == null) {
                        imgMap.put(bitmap1, source);
                    }
                    bitmap1 = Bitmap.createBitmap(bitmap1);

                    drawable.setBounds(0, 0, bitmap1.getWidth(), bitmap1.getHeight());

                    try {
                        Method method = ReflectUtil.getDeclaredMethod(drawable, "setBitmap", Bitmap.class);
                        method.invoke(drawable, bitmap1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setDrawableBound(drawable, width, height);
                    Message message = mHandler.obtainMessage();
                    message.obj = drawable;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(Uri uri, Throwable throwable) {

            }

            @Override
            public void onCancel(Uri uri) {

            }
        });*/

        return drawable;

    }

    private Bitmap getDrawableCache(String source) {
        if (imgMap.containsValue(source)) {//有缓存图片
            for (Map.Entry entry : imgMap.entrySet()) {
                if (entry.getValue().equals(source)) {
                    Bitmap drawable = (Bitmap) entry.getKey();
                    if (drawable != null && drawable.isRecycled()) {
                        imgMap.remove(drawable);
                        continue;
                    }
                    return drawable;
                }
            }
        }
        return null;
    }

    private void setDrawableBound(BitmapDrawable drawable, int height) {
        setDrawableBound(drawable, 0, height);
    }

    private void setDrawableBound(BitmapDrawable drawable, int width, int height) {
        if (width <= 0) {
            if (drawable == null || drawable.getBitmap() == null || height == 0) return;
            float scr = drawable.getBitmap().getHeight() / (float) height;
            width = (int) (drawable.getBitmap().getWidth() / scr);
        }
        drawable.setBounds(0, 2, width, height);
    }

    public interface OnImageLoadComplete {
        void onImageLoadComplete(BitmapDrawable drawable);
    }
}

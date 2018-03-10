package com.smona.app.evaluationcar.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapUtil {

    public static final Paint HIGH_PAINT = new Paint(Paint.FILTER_BITMAP_FLAG);
    private static final Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int MAX_DIFFER_PX = 10;

    public static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        return bitmap;
    }

    public static void recycleBitmap(Bitmap bitmap) {

    }

    public static Bitmap getDesBitmap(String filePath, int desWidth, int desHeight) throws IOException {
        File imgFile = new File(filePath);
        if (!imgFile.exists()) {
            return null;
        }
        int[] imgWh = new int[2];
        getImgWH(filePath, imgWh);
        int zoomCount = 0;
        while (true) {
            int outW = imgWh[0] >> zoomCount;
            int outH = imgWh[1] >> zoomCount;
            boolean isNotNeedZoom = (desWidth <= 0 && desHeight <= 0) || (desWidth > 0 && outW < desWidth * 2)
                    || (desHeight > 0 && outH < desHeight * 2);
            if (isNotNeedZoom) {
                break;
            }
            zoomCount++;
        }
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFile));
        BitmapFactory.Options options = new BitmapFactory.Options();
        CarLog.d("zoom", "file path  = " + filePath + "  file size  = " + in.available());
        CarLog.d("zoom", "options.outWidth=" + imgWh[0] + " options.outHeight=" + imgWh[1]);
        options.inSampleSize = (int) Math.pow(2.0D, zoomCount);
        options.inJustDecodeBounds = false;
        CarLog.d("zoom", "options.inSampleSize  = " + options.inSampleSize);
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
        if (bitmap != null) {
            CarLog.d("zoom",
                    "bitmap.Width=" + bitmap.getWidth() + " bitmap.Height=" + bitmap.getHeight());
        }
        return bitmap;
    }

    public static void getImgWH(String filePath, int[] wh) throws FileNotFoundException {
        File imgFile = new File(filePath);
        if (!imgFile.exists()) {
            return;
        }
        if (null == wh || wh.length < 2) {
            return;
        }
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(imgFile));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        wh[0] = options.outWidth;
        wh[1] = options.outHeight;
    }

    public static Bitmap extractThumbnailIfNeed(Bitmap source, int desWidth, int desHeight) {
        if (needExtract(source, desWidth, desHeight)) {
            Bitmap thumbnail = extractThumbnail(source, desWidth, desHeight);
            if (thumbnail != null) {
                CarLog.d("zoom", "thumbnail.Width=" + thumbnail.getWidth() + " thumbnail.Height="
                        + thumbnail.getHeight());
            }
            return thumbnail;
        } else {
            return source;
        }
    }

    private static boolean needExtract(Bitmap source, int desWidth, int desHeight) {
        if (null == source) {
            return false;
        }
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if (desWidth > 0 && desWidth <= sourceWidth - MAX_DIFFER_PX) { // 原图太宽
            return true;
        }

        return desHeight > 0 && desHeight <= sourceHeight - MAX_DIFFER_PX;
    }

    private static Bitmap extractThumbnail(Bitmap source, int desWidth, int desHeight) {
        float sourceWidth = source.getWidth();
        float sourceHeight = source.getHeight();
        int outW = desWidth;
        int outH = desHeight;
        if (desHeight <= 0) {
            outW = desWidth;
            outH = (int) (sourceHeight / sourceWidth * outW);
        } else if (desWidth <= 0) {
            outH = desHeight;
            outW = (int) (sourceWidth / sourceHeight * outH);
        } else if (desWidth > sourceWidth) {
            outW = (int) sourceWidth;
            outH = (int) (desHeight / (float) desWidth * outW);
        } else if (desHeight > sourceHeight) {
            outH = (int) sourceHeight;
            outW = (int) (desWidth / (float) desHeight * outH);
        }
        float offsetX = (outW - sourceWidth) / 2f;
        float offsetY = (outH - sourceHeight) / 2f;
        float scaleWidth = outW / sourceWidth;
        float scaleHeight = outH / sourceHeight;
        float scale = Math.max(scaleWidth, scaleHeight);

        Bitmap retBitmap = BitmapUtil.createBitmap(outW, outH, DEFAULT_BITMAP_CONFIG);
        Matrix matrix = new Matrix();
        Canvas canvas = new Canvas(retBitmap);
        matrix.postTranslate(offsetX, offsetY);
        matrix.postScale(scale, scale, outW / 2f, outH / 2f);
        canvas.drawBitmap(source, matrix, HIGH_PAINT);
        BitmapUtil.recycleBitmap(source);
        return retBitmap;
    }
}

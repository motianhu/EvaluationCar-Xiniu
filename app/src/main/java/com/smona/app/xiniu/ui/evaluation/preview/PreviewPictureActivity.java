package com.smona.app.xiniu.ui.evaluation.preview;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.ui.common.activity.HeaderActivity;

/**
 * Created by Moth on 2017/3/9.
 */

public class PreviewPictureActivity extends HeaderActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZoomImageView mImageView = (ZoomImageView) findViewById(R.id.image);

        Drawable bitmap = ContextCompat.getDrawable(this, R.drawable.mask_dashboard);
        BitmapDrawable bd = (BitmapDrawable) bitmap;
        mImageView.setImageBitmap(bd.getBitmap());
        mImageView.setCanMove(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_picture;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.preview_title;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}

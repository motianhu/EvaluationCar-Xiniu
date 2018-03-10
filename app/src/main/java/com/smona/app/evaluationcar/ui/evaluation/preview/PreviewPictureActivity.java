package com.smona.app.evaluationcar.ui.evaluation.preview;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.framework.imageloader.ImageLoaderProxy;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;
import com.smona.app.evaluationcar.util.CacheContants;

/**
 * Created by Moth on 2017/3/9.
 */

public class PreviewPictureActivity extends HeaderActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String imageUrl = getIntent().getStringExtra(CacheContants.ATTACH_FILE_URL);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        ImageLoaderProxy.loadImage(imageUrl, imageView);
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

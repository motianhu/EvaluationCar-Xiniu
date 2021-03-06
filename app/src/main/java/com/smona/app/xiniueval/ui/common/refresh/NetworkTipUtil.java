package com.smona.app.xiniueval.ui.common.refresh;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.xiniueval.R;

/**
 * Created by huangwf on 16-7-29.
 */
public class NetworkTipUtil {
    private NetworkTipUtil() {

    }

    public static void showNetworkTip(View parent, View.OnClickListener l) {
        boolean hasNet = hasNetworkInfo(parent.getContext());
        TextView tip = (TextView) parent.findViewById(R.id.no_content_text);
        ImageView imageView = (ImageView) parent.findViewById(R.id.no_content_image);
        if (hasNet) {
            tip.setText(R.string.no_data_online);
            imageView.setImageResource(R.drawable.empty_default);
        } else {
            tip.setText(R.string.no_network);
            imageView.setImageResource(R.drawable.empty_default);

        }
        imageView.setOnClickListener(l);
    }

    public static void showNoDataTip(View parent, String tipText, View.OnClickListener l) {
        TextView tip = (TextView) parent.findViewById(R.id.no_content_text);
        ImageView imageView = (ImageView) parent.findViewById(R.id.no_content_image);
        tip.setText(tipText);
        imageView.setImageResource(R.drawable.empty_default);
        imageView.setOnClickListener(l);
    }

    public static boolean hasNetworkInfo(Context context) {
        return NetworkUtils.isNetWorkOk(context, false);
    }
}

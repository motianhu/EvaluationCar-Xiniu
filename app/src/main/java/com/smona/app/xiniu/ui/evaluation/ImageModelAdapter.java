package com.smona.app.xiniu.ui.evaluation;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smona.app.xiniu.R;
import com.smona.app.xiniu.data.bean.CarImageBean;
import com.smona.app.xiniu.framework.imageloader.ImageLoaderProxy;
import com.smona.app.xiniu.ui.evaluation.camera.CameraActivity;
import com.smona.app.xiniu.util.ActivityUtils;
import com.smona.app.xiniu.util.CarLog;
import com.smona.app.xiniu.util.ScreenInfo;
import com.smona.app.xiniu.util.StatusUtils;
import com.smona.app.xiniu.util.UrlConstants;
import com.smona.app.xiniu.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moth on 2017/3/16.
 */

public class ImageModelAdapter extends BaseAdapter {

    private static final String TAG = ImageModelAdapter.class.getSimpleName();

    private List<CarImageBean> mDatas = new ArrayList<CarImageBean>();
    private Context mContext;
    private int mImageWidth;

    private boolean mNeedReload = true;

    public ImageModelAdapter(Context context, int type) {
        mContext = context;
        int i = ScreenInfo.getInstance().getScreenWidth();
        int j = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        mImageWidth = ((i - j * 3) / 2);
    }

    public void update(List<CarImageBean> datas) {
        if(!isNeedReload()) {
            return;
        }
        if (datas == null) {
            setNeedReload(false);
            return;
        }
        setNeedReload(false);
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public boolean isNeedReload() {
        return mNeedReload;
    }

    public void setNeedReload(boolean needReload) {
        this.mNeedReload = needReload;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CarImageBean bean = mDatas.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = ViewUtil.inflater(mContext, R.layout.evaluation_image_item);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.centerImage = (ImageView) convertView.findViewById(R.id.iv_add_center);
            viewHolder.centerText = (TextView) convertView.findViewById(R.id.tv_part_center);
            viewHolder.leftText = (TextView) convertView.findViewById(R.id.tv_part_left);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CarLog.d(TAG, "bean: " + bean);
                setNeedReload(true);
                ActivityUtils.jumpCameraActivity(mContext, bean, CameraActivity.class);
            }
        });

        boolean hasPic = processImage(bean, viewHolder.image);
        String diplayName = TextUtils.isEmpty(bean.displayName) ? mContext.getString(R.string.add_picture) : bean.displayName;
        viewHolder.centerText.setText(diplayName);
        ViewUtil.setViewVisible(viewHolder.centerImage, true);
        ViewUtil.setViewVisible(viewHolder.centerText, true);
        ViewUtil.setViewVisible(viewHolder.leftText, false);

        if (position == (mDatas.size() - 1)) {
            viewHolder.centerImage.setImageResource(R.drawable.icon_add_photo);
            viewHolder.image.setImageBitmap(null);
            viewHolder.image.setBackgroundResource(R.drawable.round_grey_border);
        } else {
            if (hasPic) {
                ViewUtil.setViewVisible(viewHolder.centerImage, false);
                ViewUtil.setViewVisible(viewHolder.centerText, false);
                ViewUtil.setViewVisible(viewHolder.leftText, true);
                viewHolder.leftText.setText(diplayName);
            } else {
                viewHolder.centerImage.setImageResource(R.drawable.icon_camera);
            }
        }
        return convertView;
    }

    private boolean processImage(CarImageBean bean, ImageView image) {
        ViewGroup.LayoutParams localLayoutParams = image.getLayoutParams();
        localLayoutParams.width = mImageWidth;
        localLayoutParams.height = (3 * mImageWidth / 4);
        image.setLayoutParams(localLayoutParams);

        String picUrl = null;

        if (!TextUtils.isEmpty(bean.imageThumbPath) && bean.imageUpdate == StatusUtils.IMAGE_DEFAULT) {
            picUrl = UrlConstants.getProjectInterface() + bean.imageThumbPath;
        } else if (!TextUtils.isEmpty(bean.imageLocalUrl)) {
            picUrl = "file://" + bean.imageLocalUrl;
        }
        if (!TextUtils.isEmpty(picUrl)) {
            ImageLoaderProxy.loadCornerImage(picUrl, image);
        } else {
            image.setImageBitmap(null);
        }
        return !TextUtils.isEmpty(picUrl);
    }

    public CarImageBean checkPhoto() {
        for (int i = 0; i < mDatas.size() - 1; i++) {
            if (TextUtils.isEmpty(mDatas.get(i).imageLocalUrl) && TextUtils.isEmpty(mDatas.get(i).imagePath)) {
                return mDatas.get(i);
            }
        }
        return null;
    }

    private final class ViewHolder {
        ImageView image;
        ImageView centerImage;
        TextView centerText;
        TextView leftText;
    }
}

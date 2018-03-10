package com.smona.app.evaluationcar.ui.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Moth on 2017/2/16.
 */

public abstract class ContentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        init(view);
        return view;
    }

    protected abstract int getLayoutId();

    protected void init(View root) {

    }
}

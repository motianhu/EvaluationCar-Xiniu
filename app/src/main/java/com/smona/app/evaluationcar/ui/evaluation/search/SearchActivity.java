package com.smona.app.evaluationcar.ui.evaluation.search;

import android.os.Bundle;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;


/**
 * Created by Moth on 2017/3/13.
 */

public class SearchActivity extends HeaderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.search_title;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

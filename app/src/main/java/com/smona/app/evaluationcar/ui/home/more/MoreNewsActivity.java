package com.smona.app.evaluationcar.ui.home.more;

import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.common.activity.HeaderActivity;

/**
 * Created by motianhu on 5/3/17.
 */

public class MoreNewsActivity extends HeaderActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_more_news;
    }

    @Override
    protected boolean showDelete() {
        return false;
    }

    @Override
    protected int getHeaderTitle() {
        return R.string.more_news;
    }
}

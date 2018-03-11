package com.smona.app.evaluationcar.ui.home.fragment;

import android.view.View;
import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.status.notsubmit.LocalLayer;

/**
 * Created by Moth on 2015/8/28 0028.
 */
public class NoSubmitFragment extends ContentFragment {

    protected int getLayoutId() {
        return R.layout.status_local_layer;
    }

    protected void init(View root) {
        LocalLayer layer = (LocalLayer)root.findViewById(R.id.local_root);
    }
}

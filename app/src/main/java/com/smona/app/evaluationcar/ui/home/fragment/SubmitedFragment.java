package com.smona.app.evaluationcar.ui.home.fragment;

import android.view.View;
import com.smona.app.evaluationcar.R;
import com.smona.app.evaluationcar.ui.status.submited.SubmitedLayer;


/**
 * Created by Moth on 2015/8/28 0028.
 */
public class SubmitedFragment extends ContentFragment {

    protected int getLayoutId() {
        return R.layout.status_pass_layer;
    }


    protected void init(View root) {
        SubmitedLayer layer = (SubmitedLayer)root.findViewById(R.id.submitted_layer);
        layer.request1Page();

    }
}

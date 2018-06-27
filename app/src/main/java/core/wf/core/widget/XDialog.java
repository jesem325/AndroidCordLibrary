package core.wf.core.widget;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import core.wf.core.R;
import core.wf.core.utils.ScreenUtils;

/**
 * @author feng
 * @desc
 * @time 2018/6/27 23:18
 */
public class XDialog extends DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.XDialog);
        setCancelable(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.x_dialog, null);
        view.findViewById(R.id.tv_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        View llRoot = view.findViewById(R.id.ll_root);
        ViewGroup.LayoutParams lp = llRoot.getLayoutParams();
        lp.width = ScreenUtils.getScreenWidth(getActivity()) - ScreenUtils.dp2px(getActivity(), 100);
        llRoot.setLayoutParams(lp);
        return view;
    }
}

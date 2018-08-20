package core.wf.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import core.wf.core.Base.BaseActivity;
import core.wf.core.widget.XDialog;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
//        XDialog dialog = new XDialog();
//        dialog.show(getFragmentManager(), "dialog");

        startActivity(new Intent());
    }
}

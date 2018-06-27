package core.wf.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import core.wf.core.widget.XDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        XDialog dialog = new XDialog();
        dialog.show(getFragmentManager(), "dialog");
    }
}

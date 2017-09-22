package com.example.michal.testsqllite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CSQL sql_obj = null;
    private Button btn_close = null;

    private void init_component(){
        //init obj component
        sql_obj = new CSQL(getApplicationContext());
        btn_close = (Button) findViewById(R.id.btn_close);


        // add component to listener
        btn_close.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_login);

        init_component();
    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.btn_close:
                this.finishAndRemoveTask();
                finish();
                System.exit(0);
                break;



            default:
                break;
        }

    }

}

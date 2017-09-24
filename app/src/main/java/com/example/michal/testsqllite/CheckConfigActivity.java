package com.example.michal.testsqllite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckConfigActivity extends AppCompatActivity  implements View.OnClickListener{

    private CSQL sql_obj = null;
    private Button btn_close = null;
    private Button btn_clear = null;
    private Button btn_save = null;
    private Button btn_load = null;
    private EditText editTxt_input= null;
    private EditText editTxt_output = null;

    private void init_component(){
        //init obj component
        sql_obj = new CSQL(getApplicationContext());
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_load = (Button) findViewById(R.id.btn_load);
        editTxt_input = (EditText) findViewById(R.id.editTxt_input);
        editTxt_output = (EditText) findViewById(R.id.editTxt_output);

        // settings component
        editTxt_output.setEnabled(false);

        // add component to listener
        btn_close.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_load.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_config);

        init_component();
    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.btn_close:
                this.finishAndRemoveTask();
                finish();
                System.exit(0);
                break;

            case R.id.btn_save:
                String txt_input = editTxt_input.getText().toString();
                sql_obj.save_config("message",txt_input);
                if(sql_obj.check_sql_error()) {
                    editTxt_output.setText(sql_obj.get_sql_error());
                }else {
                    editTxt_output.setText(txt_input);
                    editTxt_input.setText("");
                }
                break;

            case R.id.btn_load:
                String str_load = sql_obj.load_config("message");
                if(sql_obj.check_sql_error()) {
                    editTxt_output.setText(sql_obj.get_sql_error());
                }else{
                    editTxt_output.setText(str_load);
                }


                editTxt_input.setText("");
                break;

            case R.id.btn_clear:
                editTxt_input.setText("");
                editTxt_output.setText("");
                break;

            default:
                break;
        }

    }

}

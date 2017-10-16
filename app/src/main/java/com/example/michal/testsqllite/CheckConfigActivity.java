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
    private Button btn_delete = null;
    private EditText editTxt_input= null;
    private EditText editTxt_output = null;
    private EditText editTxt_counter = null;

    private void init_component(){
        //init obj component
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_load = (Button) findViewById(R.id.btn_load);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        editTxt_input = (EditText) findViewById(R.id.editTxt_input);
        editTxt_output = (EditText) findViewById(R.id.editTxt_output);
        editTxt_counter = (EditText) findViewById(R.id.editTxt_counter);

        // settings component
        editTxt_output.setEnabled(false);

        // add component to listener
        btn_close.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        sql_obj = new CSQL(getApplicationContext());
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
                sql_obj.config_save("message",txt_input);
                if(sql_obj.check_sql_error()) {
                    editTxt_input.setText("");
                    editTxt_counter.setText("Error: " + sql_obj.get_sql_error());
                    editTxt_output.setText("");
                }else {
                    editTxt_input.setText("");
                    editTxt_output.setText(txt_input);
                }
                break;

            case R.id.btn_load:
                String str_load = sql_obj.config_load("message");
                if(sql_obj.check_sql_error()) {
                    editTxt_output.setText(str_load);
                    editTxt_counter.setText("Error: " + sql_obj.get_sql_error());
                }else{
                    editTxt_output.setText(str_load);
                    editTxt_counter.setText( "Count rows: " + Long.toString(sql_obj.get_count_row()) );
                }
                break;

            case R.id.btn_delete:
                sql_obj.config_remove("message");
                break;

            case R.id.btn_clear:
                editTxt_input.setText("");
                editTxt_output.setText("");
                editTxt_counter.setText("");
                break;

            default:
                break;
        }

    }

}

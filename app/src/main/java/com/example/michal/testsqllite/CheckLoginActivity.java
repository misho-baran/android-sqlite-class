package com.example.michal.testsqllite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private CSQL sql_obj = null;
    private Button btn_check = null;
    private Button btn_save = null;
    private Button btn_delete = null;
    private Button btn_close = null;
    private EditText editTxt_userName= null;
    private EditText editTxt_userPass= null;
    private EditText editTxt_status= null;

    private void init_component(){
        //init obj component
        sql_obj = new CSQL(getApplicationContext());
        btn_check = (Button) findViewById(R.id.btn_check);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_close = (Button) findViewById(R.id.btn_close);
        editTxt_userName = (EditText) findViewById(R.id.editTxt_userName);
        editTxt_userPass = (EditText) findViewById(R.id.editTxt_userPass);
        editTxt_status = (EditText) findViewById(R.id.editTxt_status);

        // settings component
        //editTxt_status.setEnabled(false);

        // add component to listener
        btn_check.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_close.setOnClickListener(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_login);

        init_component();
    }

    public void onClick(View v) {

        String usr_name = editTxt_userName.getText().toString();
        String usr_pass = editTxt_userPass.getText().toString();

        switch(v.getId()) {

            case R.id.btn_check:
                if(usr_name.isEmpty() || usr_pass.isEmpty()) break;
                editTxt_userName.setText("");
                editTxt_userPass.setText("");
                boolean check_exist_user = sql_obj.user_check_exist(usr_name);
                boolean check_passwd = sql_obj.user_check_passwd(usr_name, usr_pass);
                usr_name = "";
                usr_pass = "";

                if(sql_obj.check_sql_error()) {
                    editTxt_status.setText("Error: " + sql_obj.get_sql_error());
                }else{
                    if(check_exist_user) {

                        if(check_passwd){
                            editTxt_status.setText("User exist and password is correct");
                        }else{
                            editTxt_status.setText("User exist and password is not correct");
                        }
                    }else{
                        editTxt_status.setText("User not exist.");
                    }
                }
                break;

            case R.id.btn_save:
                if(usr_name.isEmpty() || usr_pass.isEmpty()) break;
                editTxt_userName.setText("");
                editTxt_userPass.setText("");
                sql_obj.user_add(usr_name, usr_pass);
                usr_name = "";
                usr_pass = "";

                if(sql_obj.check_sql_error()) {
                    editTxt_status.setText("Error: " + sql_obj.get_sql_error());
                }else{
                    editTxt_status.setText("");
                }

                editTxt_userName.setText("");
                editTxt_userPass.setText("");
                break;

            case R.id.btn_delete:
                if(usr_name.isEmpty()) break;
                sql_obj.user_remove(usr_name);
                editTxt_userName.setText("");
                editTxt_userPass.setText("");
                break;

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

package com.example.michal.testsqllite;

//import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private CSQL sql_obj = null;
    private Button btn_exit = null;
    private Button btn_config = null;
    private Button btn_login = null;
    private Button btn_createDB = null;
    private Button btn_removeDB = null;
    private EditText editTxt_info = null;

    /*protected Intent intent_checkConfigActivity = null;
    protected Intent intent_checkLoginActivity = null;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init obj component
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_config = (Button) findViewById(R.id.btn_config);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_createDB = (Button) findViewById(R.id.btn_createDB);
        btn_removeDB = (Button) findViewById(R.id.btn_removeDB);
        editTxt_info = (EditText) findViewById(R.id.editTxt_info);


        // settings component
        editTxt_info.setEnabled(false);

        PackageManager m = getPackageManager();
        String s = getPackageName();
        PackageInfo p = null;
        try {
            p = m.getPackageInfo(s, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        s = p.applicationInfo.dataDir;
        editTxt_info.setText(s);

        // add component to listener
        btn_config.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_createDB.setOnClickListener(this);
        btn_removeDB.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        sql_obj = new CSQL(getApplicationContext());
    }

    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.btn_config:
                Intent intent_checkConfigActivity = new Intent(v.getContext(), CheckConfigActivity.class);
                startActivity(intent_checkConfigActivity);
                break;

            case R.id.btn_login:
                Intent intent_checkLoginActivity = new Intent(v.getContext(), CheckLoginActivity.class);
                startActivity(intent_checkLoginActivity);
                break;

            case R.id.btn_createDB:
                sql_obj.create_db();
                break;

            case R.id.btn_removeDB:
                sql_obj.delete_db();
                break;

            case R.id.btn_exit:
                this.finishAndRemoveTask();
                finish();
                System.exit(0);
                break;


            default:
                break;
        }

    }


}

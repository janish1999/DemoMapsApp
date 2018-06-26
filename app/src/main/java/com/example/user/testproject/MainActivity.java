
package com.example.user.testproject;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private  static final int ERROR_REQUEST=9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isServiceOk()){
            init();
        }

    }
    public boolean isServiceOk(){
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available== ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"Sorry Nothing can be done",Toast.LENGTH_SHORT).show();
        }
        return false;

    }
    public void init(){
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }
}

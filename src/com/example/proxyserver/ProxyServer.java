package com.example.proxyserver;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;



public class ProxyServer extends Activity implements OnClickListener {
  private static final String TAG = "Streaming Proxy";
  Button buttonStart, buttonStop;
  //CellularSignal cellSig;
  //EditText urlText;

  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    buttonStart = (Button) findViewById(R.id.button1);
    buttonStop = (Button) findViewById(R.id.button2);
    //urlText = (EditText) findViewById(R.id.editTextSimple);
    
    buttonStart.setOnClickListener(this);
    buttonStop.setOnClickListener(this);
  }
  
  

  public void onClick(View src) 
  {
    switch (src.getId()) 
    {
    	case R.id.button1:
    	Log.d(TAG, "onClick: starting srvice");
    	startService(new Intent(this, ServiceControl.class));
    	//Intent intent = new Intent(this,com.example.policy.CellularSignal.class);
        //startActivity(intent);      
        //finish();
        
        //Intent batteryIntent = new Intent(this,com.example.policy.BatteryStatus.class);
        //startActivity(batteryIntent);      
        //finish();
    	break;
    	
    case R.id.button2:
        Log.d(TAG, "onClick: stopping srvice");
        stopService(new Intent(this, ServiceControl.class));
        break;
    }
    
  }
 }
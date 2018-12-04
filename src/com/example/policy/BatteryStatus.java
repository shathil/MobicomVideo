package com.example.policy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BatteryStatus extends Activity
{
	private String TAG="BatterManager";

    //private Intent ChannelChangeListenerIntent = null;
	public IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
		registerReceiver(batteryReceiver, filter);
	}	
	protected void onPause()
	{
		super.onPause();
		
		//unregisterReceiver(batteryReceiver);
		if (filter != null) {
		    unregisterReceiver(batteryReceiver);
		    filter = null;
		}
	
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		registerReceiver(batteryReceiver, filter);
	}
	
	
	
    	public final BroadcastReceiver batteryReceiver = new BroadcastReceiver() 
    	{
		        int scale = -1;
		        int level = -1;
		        int voltage = -1;
		        int temp = -1;
	        @Override
	        public void onReceive(Context context, Intent intent) 
	        {
	            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	            temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
	            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
	            Log.e("BatteryManager", "level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage);
	        }
	    };
	    
	    
	
}
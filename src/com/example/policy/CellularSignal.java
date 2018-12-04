package com.example.policy;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;


public class CellularSignal extends Activity
{
	private String TAG="Cellular Signal";
	
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
     
        startSignalLevelListener();
        //Log.d(TAG, "onCreate");
        //displayTelephonyInfo();
    }
	 @Override
		protected void onPause()
		{
			super.onPause();
			
			stopListening();
		}

		@Override
		protected void onResume()
		{
			super.onResume();
			
			startSignalLevelListener();
		}
		
		@Override
		protected void onDestroy()
		{
			stopListening();
			
			super.onDestroy();
		}
		
		
	  private void startSignalLevelListener() 
	  {
	  	TelephonyManager teleman = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	  	int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | 
	  				 PhoneStateListener.LISTEN_DATA_ACTIVITY | 
	  				 PhoneStateListener.LISTEN_CELL_LOCATION |
	  				 PhoneStateListener.LISTEN_CALL_STATE |
	  				 PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
	  				 PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
	  				 PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
	  				 PhoneStateListener.LISTEN_SERVICE_STATE;
	  	
	  	teleman.listen(phoneStateListener, events);
	  }
	
	public void stopListening(){
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
	 private int logString(String message) {
			return Log.i(TAG,message);
		}
		
		

	public void displayTelephonyInfo()
	{
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
		
		int cellid = loc.getCid();
		int lac = loc.getLac();
		
		String deviceid = tm.getDeviceId();
		String phonenumber = tm.getLine1Number();
		String softwareversion = tm.getDeviceSoftwareVersion();
		String operatorname = tm.getNetworkOperatorName();
		String simcountrycode = tm.getSimCountryIso();
		String simoperator = tm.getSimOperatorName();
		String simserialno = tm.getSimSerialNumber();
		String subscriberid = tm.getSubscriberId();
		
		
		logString("CellID: " + cellid);
		logString("LAC: " + lac);
		logString("Device ID: " + deviceid);
		logString("Phone Number: " + phonenumber);
		logString("Software Version: " + softwareversion);
		logString("Operator Name: " + operatorname);
		logString("SIM Country Code: " + simcountrycode);
		logString("SIM Operator: " + simoperator);
		logString("SIM Serial No.: " + simserialno);
		logString("Sibscriber ID: " + subscriberid);
		String neighborCellInfo = "";
		List<NeighboringCellInfo> cellinfo = tm.getNeighboringCellInfo();
		
		if(null != cellinfo){
			for(NeighboringCellInfo info: cellinfo)
			{
				neighborCellInfo+= ("\tCellID: " + info.getCid() + ", RSSI: " + info.getRssi() + "\n");
			}
		}
		
		logString("Neighbour ID: " + neighborCellInfo);
		//setTextViewText(info_ids[INFO_DEVICE_INFO_INDEX],deviceinfo);
	}



	private String getNetworkTypeString(int type){
		String typeString = "Unknown";
		
		switch(type)
		{
			case TelephonyManager.NETWORK_TYPE_CDMA:	typeString = "CDMA"; break;
			case TelephonyManager.NETWORK_TYPE_HSPA:	typeString = "HSPA"; break;
			case TelephonyManager.NETWORK_TYPE_LTE:		typeString = "LTE"; break;
			case TelephonyManager.NETWORK_TYPE_EDGE:	typeString = "EDGE"; break;
			case TelephonyManager.NETWORK_TYPE_GPRS:	typeString = "GPRS"; break;
			case TelephonyManager.NETWORK_TYPE_UMTS:	typeString = "UMTS"; break;
			default:									typeString = "UNKNOWN"; break;
		}
		
		return typeString;
	}
	
	private String getPhoneTypeString(int type){
		String typeString = "Unknown";
		
		switch(type)
		{
			case TelephonyManager.PHONE_TYPE_GSM:	typeString = "GSM"; break;
			case TelephonyManager.PHONE_TYPE_NONE:	typeString = "UNKNOWN"; break;
			case TelephonyManager.PHONE_TYPE_CDMA:  typeString ="CDMA"; break;
			default:								typeString = "UNKNOWN"; break;
		}
		
		return typeString;
	}
	
	private final PhoneStateListener phoneStateListener = new PhoneStateListener()
	{

		@Override
		public void onCallForwardingIndicatorChanged(boolean cfi)
		{
			Log.i(TAG, "onCallForwardingIndicatorChanged " + cfi);
			 
			super.onCallForwardingIndicatorChanged(cfi);
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			String callState = "UNKNOWN";
			
			switch(state)
			{
				case TelephonyManager.CALL_STATE_IDLE:		callState = "IDLE"; break;
				case TelephonyManager.CALL_STATE_RINGING: 	callState = "Ringing (" + incomingNumber + ")"; break;
				case TelephonyManager.CALL_STATE_OFFHOOK: 	callState = "Offhook"; break;
			}
			
			//setTextViewText(info_ids[INFO_CALL_STATE_INDEX],callState);
			
			Log.i(TAG, "onCallStateChanged " + callState);
			
			super.onCallStateChanged(state, incomingNumber);
		}

		@Override
		public void onCellLocationChanged(CellLocation location)
		{
			String locationString = location.toString();
			
			//setTextViewText(info_ids[INFO_CELL_LOCATION_INDEX],locationString);
			
			Log.i(TAG, "onCellLocationChanged " + locationString);
			
			super.onCellLocationChanged(location);
		}

		@Override
		public void onDataActivity(int direction)
		{
			String directionString = "none";
			
			switch(direction)
			{
				case TelephonyManager.DATA_ACTIVITY_IN: 	directionString = "IN"; break;
				case TelephonyManager.DATA_ACTIVITY_OUT: 	directionString = "OUT"; break;
				case TelephonyManager.DATA_ACTIVITY_INOUT: 	directionString = "INOUT"; break;
				case TelephonyManager.DATA_ACTIVITY_NONE: 	directionString = "NONE"; break;
				default:									directionString = "UNKNOWN: " + direction; break;
			}
			
			//setDataDirection(info_ids[INFO_DATA_DIRECTION_INDEX],direction);
			
			Log.i(TAG, "onDataActivity " + directionString);
			
			super.onDataActivity(direction);
		}

		@Override
		public void onDataConnectionStateChanged(int state)
		{
			String connectionState = "Unknown";
			
			switch(state)
			{
				case TelephonyManager.DATA_CONNECTED: 		connectionState = "Connected"; break;
				case TelephonyManager.DATA_CONNECTING: 		connectionState = "Connecting"; break;
				case TelephonyManager.DATA_DISCONNECTED: 	connectionState = "Disconnected"; break;
				case TelephonyManager.DATA_SUSPENDED: 		connectionState = "Suspended"; break;
				default: 									connectionState = "Unknown: " + state; break;
			}
			
			//setTextViewText(info_ids[INFO_CONNECTION_STATE_INDEX],connectionState);
			
			Log.i(TAG, "onDataConnectionStateChanged " + connectionState);
			
			super.onDataConnectionStateChanged(state);
		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi)
		{
			Log.i(TAG, "onMessageWaitingIndicatorChanged " + mwi);
			
			super.onMessageWaitingIndicatorChanged(mwi);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState)
		{
			String serviceStateString = "UNKNOWN";
			
			switch(serviceState.getState())
			{
				case ServiceState.STATE_IN_SERVICE: 		serviceStateString = "IN SERVICE"; break;
				case ServiceState.STATE_EMERGENCY_ONLY: 	serviceStateString = "EMERGENCY ONLY"; break;
				case ServiceState.STATE_OUT_OF_SERVICE: 	serviceStateString = "OUT OF SERVICE"; break;
				case ServiceState.STATE_POWER_OFF: 			serviceStateString = "POWER OFF"; break;
				default: 									serviceStateString = "UNKNOWN"; break;
			}
			
			//setTextViewText(info_ids[INFO_SERVICE_STATE_INDEX],serviceStateString);
			
			Log.i(TAG, "onServiceStateChanged " + serviceStateString);
			
			super.onServiceStateChanged(serviceState);
		}
		
		@Override
		public void onSignalStrengthsChanged(SignalStrength sigrth)
		{
			
			
			TelephonyManager Tel = ( TelephonyManager )getSystemService(TELEPHONY_SERVICE);
			GsmCellLocation loc = (GsmCellLocation) Tel.getCellLocation();
			
			String neighborCellInfo = "";
			List<NeighboringCellInfo> cellinfo = Tel.getNeighboringCellInfo();
			
			if(null != cellinfo){
				for(NeighboringCellInfo info: cellinfo)
				{
					neighborCellInfo+= ("\tCellID: " + info.getCid() + ", RSSI: " + info.getRssi() + "\n");
				}
			}
			
			logString("Neighbour ID: " + neighborCellInfo);
			
			Log.i(TAG, "onSignalStrengthChanged " + sigrth.toString());
			
			//setSignalLevel(info_ids[INFO_SIGNAL_LEVEL_INDEX],info_ids[INFO_SIGNAL_LEVEL_INFO_INDEX],asu);
			
			super.onSignalStrengthsChanged(sigrth);
		}
		
		
		};
}

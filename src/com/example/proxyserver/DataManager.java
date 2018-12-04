package com.example.proxyserver;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public final class DataManager 
{
	
    private TelephonyManager m_telManager = null;
    private ConnectivityManager m_conManager = null;
 	
    // ------------------------------------------------------
    // ------------------------------------------------------
    public DataManager(Context context)
    {
        try
        {
            // Get phone and connectivity services
            m_telManager = (TelephonyManager)context.getSystemService("phone");
            m_conManager = (ConnectivityManager)context.getSystemService("connectivity");
        }
        catch (Exception e)
        {
            m_telManager = null;
            m_conManager = null;
        }
    }

    // ------------------------------------------------------
    // ------------------------------------------------------
    boolean switchState(boolean enable) 
    {
    	boolean bRes = false;
    	
        // Data Connection mode (only if correctly initialized)
    	if (m_telManager != null)
        {
            try
            {
              // Will be used to invoke hidden methods with reflection
    	        Class<? extends TelephonyManager> cTelMan = null;
    	        Method getITelephony = null;
    	        Object oTelephony = null;
    	        Class<? extends Object> cTelephony = null;
    	        Method action = null;
        		
    	        // Get the current object implementing ITelephony interface
    	        cTelMan = m_telManager.getClass();
    	        getITelephony = cTelMan.getDeclaredMethod("getITelephony");
    	        getITelephony.setAccessible(true);
    	        oTelephony = getITelephony.invoke(m_telManager);
    	        
    	        // Call the enableDataConnectivity/disableDataConnectivity method
    	        // of Telephony object
    	        cTelephony = oTelephony.getClass();
    	        if (enable)
    	        {
                    action = cTelephony.getMethod("enableDataConnectivity");
                    Log.i("DataManaer","enabledataconnectivity");
    	        }
    	        else
    	        {
    	            action = cTelephony.getMethod("disableDataConnectivity");
    	            Log.i("DataManaer","disabledataconnectivity");
    	        }
    	        action.setAccessible(true);
    	        bRes = (Boolean)action.invoke(oTelephony);
            }
            catch (Exception e)
    	    {
                bRes = false;
            }
        }
        
    	return bRes;
    } 

    // ------------------------------------------------------
    // ------------------------------------------------------
    public boolean isEnabled()
    {
    	boolean bRes = false;
       
        // Data Connection mode (only if correctly initialized)
        if (m_conManager != null)
        {
            try       
            {
    	        // Get Connectivity Service state
    	        NetworkInfo netInfo = m_conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	        
    	        // Data is enabled if state is CONNECTED
    	        bRes = (netInfo.getState() == NetworkInfo.State.CONNECTED);
            }
            catch (Exception e)
            {
    	        bRes = false;
            }
        }     
        
    	return bRes;
    }

}

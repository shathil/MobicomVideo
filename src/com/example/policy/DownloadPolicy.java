package com.example.policy;

public class DownloadPolicy 
{
	private static final int WIFI_INTERFACE=1;
	private static final int HSPA_INTERFACE=2;
	private static final int LTE_INTERFACE=3;
	
	private static final int default_policy=1;
	private static final int aggressive_default_policy=2;
	
	
	private int policyId;
	private int burstInterval;
	private int burstBeginEarlier;
	private int radioInterface;
	
	public String getDefaultPolicy(int policyid)
	{
		String policy="";
		if(policyid==DownloadPolicy.default_policy)
		{
			policy+="Default-Policy: "+default_policy;
			
		}
		return policy;
	}
	
	
	public void generatePolicy()
	{
		burstInterval=40;
	}
	
	public void multipleDownloadTogether()
	
	{}
}

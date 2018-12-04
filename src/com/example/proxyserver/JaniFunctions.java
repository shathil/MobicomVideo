package com.example.proxyserver;

public class JaniFunctions 
{
	static
	{
		System.loadLibrary("ffmpegproxy");
	}

	//public static native int getport(int port);
	//public static native String getaddress(String address);
	public static native String decodevideoinfo(String info,String id);
	public static native String getvideoinfourls(String request,int len, String server, int port, String id);
	public static native String getvideo(String request,String OtherParams);
	public static native int getkeyframepos(int bytepos);
	
	public static native int getmutexlock();
	public static native int releasemutex();
	public static native int initializemutex();
}

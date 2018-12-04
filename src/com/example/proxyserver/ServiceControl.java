package com.example.proxyserver;


import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import scheduler.DownloadScheduler;

public class ServiceControl extends Service{

	private String TAG = "MyStreamingProxy";
	private static int dThreadCount=0;

	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;

	}
	@Override
	public void onCreate() 
	{
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		//cellSig.startSignalLevelListener();
	    //cellSig.displayTelephonyInfo();    
		
	}

	
	@Override
	public void onDestroy() 
	{
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
        super.onDestroy();
        this.stopSelf();
    }

	@Override
	
	
	public void onStart(Intent intent, int startid) 
	{
		
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
	
		/* Initialize mutex........*/
		Log.i(TAG,"Initializing Thread Mutex");
		JaniFunctions.initializemutex();
				
		/* Video Downloading Thread*/
		if(ServiceControl.dThreadCount==0)
		{
			ServiceControl.dThreadCount=1;
			(new Thread(new Runnable(){public void run() 
		    {try{downloadThread();}
		    catch(InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}
		    }})).start();
			  
			/* Video Playback Thread */
			(new Thread(new Runnable(){public void run() 
			{try{playbackThread();}catch(InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();}}})).start();
			
		}
	}
	
	public void playbackThread() throws InterruptedException
	{
		
	}
	
	/* Video Downloading */
	public void downloadThread() throws InterruptedException
	{
		Log.i(TAG,"Downloading Thread Started.");
    	String requestUrl="http://www.youtube.com/watch?v=93hWmfH2Wpg";
    	int videoQuality=18;
    	
    	GetVideoUrlServer videoUrlObject=new GetVideoUrlServer(requestUrl,videoQuality);
    	//String videoId=videoUrlObject.getVideoId();
    
    	
    	String default_aud="/mnt/sdcard/catagey_two.txt";
    	DownloadScheduler newS=new DownloadScheduler();
    	try {
			newS.readAudienceRetention(default_aud);
			newS.LoadAudRet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i(TAG, "duration");
    	downloadVideo(videoUrlObject);
    	
    	//downloadVideoEstreamer(videoUrlObject);
	}

	
	
	
	
	private void downloadVideo(GetVideoUrlServer videoUrlObject) throws InterruptedException
	{
		boolean downloadInComplete=true;
		int con=0,i,j=0;
		long byterange=0,contentlength=0,bandwidth=0;
		int videoQuality=videoUrlObject.getVideoQuality();
		int gotvideoUrls=videoUrlObject.receiveAndSetVideoUrl();
		String videoId=videoUrlObject.getVideoId();
		DataManager OnOff=new DataManager(this.getApplicationContext());
		int bitrate=0;
		
		
		
		
		
		int duration=videoUrlObject.getVideoDuration(videoId);
		Log.i(TAG, "duration"+duration);
    	ChunkScheduler newSchedule=new ChunkScheduler(duration);
    	newSchedule.setnewChunkSizes();
		String videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
				(videoQuality,byterange,contentlength);

		String fileName=videoUrlObject.getVideofileName(videoId,videoQuality);
		
		Log.i(TAG, fileName);
		
		if((gotvideoUrls==200)&&(videoRequest.contains("videoplayback"))) 
		{
			String params="";
			videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
					(videoQuality,byterange,contentlength);

			int tempind=0,totalind=0;
			while(totalind<duration)
			{
				Log.i(TAG,""+duration);
				long beginMili = System.currentTimeMillis();
				tempind=DownloadScheduler.getexpectedviewduration(1,399,2);
				totalind+=tempind;
				long endMilli=System.currentTimeMillis()-beginMili;
				Log.i(TAG,"New Schedule "+tempind+ "time required: "+endMilli);
				
			}

			while(downloadInComplete)
			{

				int indChunk=newSchedule.getChunkSize(j);
				bandwidth=0;
				int mTcp=newSchedule.getTcpM(j);
				Log.i(TAG,"Chunk Size: "+indChunk);
				Log.i(TAG,"Number of short chunks: "+mTcp);
				int shortChunk=0;
				long chunkStart=System.currentTimeMillis();
				for(i=0;i<mTcp;++i)
				{
					//shortChunk=40;
					
					if(i==mTcp-1)
						shortChunk=indChunk%(newSchedule.getFastStart());
					else
						shortChunk=newSchedule.getFastStart();
					
			    	params=
			    	 "Host: "+videoRequest.split("\r\n")[1].split("Host: ")[1]+"\r\n"
			    	+"File-Name: "+videoUrlObject.getVideofileName(videoId,videoQuality)+"\r\n"
			    	+"Bit-Rate: "+bitrate+"\r\n"
			    	+"Bandwidth: "+bandwidth+"\r\n"
			    	+"Burst-Duration: "+shortChunk+"\r\n"
			    	+"Video-Duration: "+duration+"\r\n";
			    	
			    	Log.i(TAG,"ShortChunk: "+shortChunk);
				    String headerEle=JaniFunctions.getvideo(videoRequest,params);
					if((headerEle.contains("HTTP/1.1 200 OK"))||(headerEle.contains("HTTP/1.1 206 Partial Content")))
					{
						
						if(con==0)
						{
							contentlength=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Content-Length: ", '\r'));
							byterange=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Downloaded-Bytes: ", '\r'));
							bitrate=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Bit-Rate: ", '\r'));
							videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
							(videoQuality,byterange,contentlength);
							Log.i(TAG,"Content-Length: "+contentlength);
		
						}
						else
						{
							byterange=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Downloaded-Bytes: ", '\r'));
							if(byterange>=contentlength)
							{
								downloadInComplete=false;
								Log.i(TAG,"Download Complete.");
								break;
								
							}
							else
								videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
								(videoQuality,byterange,contentlength);
												
						}
						
						con=con+1;
					}
					else if (headerEle.contains("HTTP/1.1 302 Found"))
					{
						videoUrlObject.updateIndivudualUrl(headerEle.split("Location: ")[1].split("\r\n")[0],videoQuality);
						videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest(videoQuality,byterange,contentlength);
						Log.i(TAG,"Video is redirected "+"\n"+videoRequest);
					}
					else
					{
						Log.i(TAG,"Other Response."+"\n"+headerEle);
					}
				
				
				}//end of inner for loop
				
				if(OnOff.isEnabled())
				{
					Log.i(TAG, "Data was enabled\n");
					if (OnOff.switchState(false))
						Log.i(TAG, "Data disabled\n");
					
				}
				long downloadElasped=System.currentTimeMillis()-chunkStart;
				long sleepDur=(indChunk*1000)-downloadElasped-2000;
				Thread.sleep(sleepDur);

				if(OnOff.isEnabled())
				{
					Log.i(TAG, "Data was disabled\n");
					if(OnOff.switchState(true))
						Log.i(TAG, "Data enabled\n");

					
				}
				++j;

			}//end of while
			
			//long difMilli=System.currentTimeMillis()-beginMili;
			//if((duration*1000)>difMilli)
			//	Thread.sleep((duration*1000)-difMilli);
			
			
			
		}
		else
		{
			Log.i(TAG,"Could not find the video Url of "+videoQuality+" "+gotvideoUrls+videoRequest);
		}
		
	}//end of Function.
	
	/*
	
	private void downloadVideoEstreamer(GetVideoUrlServer videoUrlObject) throws InterruptedException
	{
		boolean downloadInComplete=true;
		int con=0,i,j=0;
		long byterange=0,contentlength=0;
		int videoQuality=videoUrlObject.getVideoQuality();
		int gotvideoUrls=videoUrlObject.receiveAndSetVideoUrl();
		String videoId=videoUrlObject.getVideoId();
		DataManager OnOff=new DataManager(this.getApplicationContext());
		int bitrate=0;
		
		
		
		
		int duration=videoUrlObject.getVideoDuration(videoId);
    	ChunkScheduler newSchedule=new ChunkScheduler(duration);
    	newSchedule.setnewChunkSizes();
		String videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
				(videoQuality,byterange,contentlength);

		String fileName=videoUrlObject.getVideofileName(videoId,videoQuality);
		
		Log.i(TAG, fileName);
		
		if((gotvideoUrls==200)&&(videoRequest.contains("videoplayback"))) 
		{
			String params="";
			

			//for(j=0;j<1;++j)
			//{
				byterange=0;
				contentlength=0;
				con=0;
				bitrate=0;
				downloadInComplete=true;
				videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
						(videoQuality,byterange,contentlength);

				Log.i(TAG,"Schedule: "+j);
				while(downloadInComplete)
				{
					int shortChunk=0;
					long chunkStart=System.currentTimeMillis();
				int indChunk=newSchedule.getChunkSize(j);
				int mTcp=newSchedule.getTcpM(j);
				Log.i(TAG,"Chunk Size: "+indChunk);
				Log.i(TAG,"Number of short chunks: "+mTcp);
				for(i=0;i<mTcp;++i)
				{
					//shortChunk=40;
					
					if(i==mTcp-1)
						shortChunk=indChunk%(newSchedule.getFastStart());
					else
						shortChunk=newSchedule.getFastStart();

						//long beginMili = System.currentTimeMillis();

						//shortChunk=newSchedule.getChunkSize(i);
						
				    	params=
				    	 "Host: "+videoRequest.split("\r\n")[1].split("Host: ")[1]+"\r\n"
				    	+"File-Name: "+videoUrlObject.getVideofileName(videoId,videoQuality)+"\r\n"
				    	+"Bit-Rate: "+bitrate+"\r\n"
				    	+"Burst-Duration: "+shortChunk+"\r\n"
				    	+"Video-Duration: "+duration+"\r\n";
				    	
				    	Log.i(TAG,"ShortChunk: "+shortChunk);
					    String headerEle=JaniFunctions.getvideo(videoRequest,params);
						if((headerEle.contains("HTTP/1.1 200 OK"))||(headerEle.contains("HTTP/1.1 206 Partial Content")))
						{
							
							if(con==0)
							{
								contentlength=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Content-Length: ", '\r'));
								byterange=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Downloaded-Bytes: ", '\r'));
								bitrate=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Bit-Rate: ", '\r'));
								videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
								(videoQuality,byterange,contentlength);
								Log.i(TAG,"Content-Length: "+contentlength);
			
							}
							else
							{
								byterange=Integer.parseInt(StringUtils.getRangeOfBytes(headerEle, "Downloaded-Bytes: ", '\r'));
								if(byterange>=contentlength)
								{
									downloadInComplete=false;
									Log.i(TAG,"Download Complete.");
									break;
									
								}
								else
									videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest
									(videoQuality,byterange,contentlength);
													
							}
							
							con=con+1;
						}
						else if (headerEle.contains("HTTP/1.1 302 Found"))
						{
							videoUrlObject.updateIndivudualUrl(headerEle.split("Location: ")[1].split("\r\n")[0],videoQuality);
							videoRequest=videoUrlObject.getIndividualVideoUrlHttpRequest(videoQuality,byterange,contentlength);
							Log.i(TAG,"Video is redirected "+"\n"+videoRequest);
						}
						else
						{
							Log.i(TAG,"Other Response."+"\n"+headerEle);
						}
					
		
					
					}//end of inner for loop
					++j;
					long difMilli=System.currentTimeMillis()-chunkStart;
					Log.i(TAG, "Elapsed time: "+difMilli);
					long sleepDur=(indChunk*1000)-difMilli-2000;
					Thread.sleep(sleepDur);

				}//end of while
			
			//} //end of for loop
			
		}
		else
		{
			Log.i(TAG,"Could not find the video Url of "+videoQuality+" "+gotvideoUrls+videoRequest);
		}
		
	}//end of Function.
	
	public void playbackThread() throws InterruptedException
	{
		Log.i(TAG,"Playback Thread Started.");
		while(true)
		{
			
			Thread.sleep(3);
			//JaniFunctions.getmutexlock();
			
		}
	}*/
	
	
}

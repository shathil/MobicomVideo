package com.example.proxyserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.util.Log;

public class VideoInfo {
	public static int service=0;
	private static int maxVideos=20;
	private int availVideo=0;
	
	String TAG="VideoInfo";
	
	String [] videoURLS;
	int [] iTags;
	int [] iRes;
	String [] conTainer;
	String [] fallbackHost;
	boolean[] rateBypass;
	String videoinfo;
	String videoId;

	private int videoDuration=0;
	
	
	public VideoInfo()
	{
		this.videoURLS=new String[maxVideos];
		this.iTags=new int[maxVideos];
		this.iRes=new int[maxVideos];
		this.conTainer=new String[maxVideos];
		this.rateBypass=new boolean[maxVideos];
		this.fallbackHost=new String[maxVideos];

	}

	public void setVideoId(String  url)
	{
		this.videoId=url.split("/")[3].split("&")[0].split("=")[1];
		if(url.contains("youtube"))
			VideoInfo.service=1;
		else
			VideoInfo.service=2;
		//Log.i(TAG,this.videoId=url.split("/")[3]);
	}
	public String getVideoUrl(int quality)
	{
		int i=0;
		for(i=0;i<availVideo;++i)
		{
			if(iTags[i]==quality)
				break;
		}
		//Log.i(TAG,videoURLS[i]);
		return videoURLS[i];
	}
	
	public String getVideoId()
	{
		
		return this.videoId;
	}
	public int isUrlAvailable(int quality)
	{
		int i=0,j=-1;
		for(i=0;i<availVideo;++i)
		{
			if(iTags[i]==quality)
			{
				j=i;
				//Log.i(TAG,"VideoFound");
				break;
			}
		}
		return j;
	}

	private void setContainer(int itag, int index)
	{
		switch(itag)
		{
			case 5:
			case 34:
			case 35: conTainer[index]="flv"; break;
			case 36: conTainer[index]="3gpp"; break;
			
			case 17:
			case 18: 
			case 22: conTainer[index]="mp4"; break;
			
			
			case 43: 
			case 44:
			case 45: 
			case 46: conTainer[index]="webm"; break;		
		}

		
	}
	
	
	private void setResulation(int itag, int index)
	{
		switch(itag)
		{
			case 5: iRes[index]=240; break;
			case 34:iRes[index]=360; break;
			case 35:iRes[index]=480; break;
			case 36:iRes[index]=240; break;
			
			case 17:iRes[index]=144; break;
			case 18:iRes[index]=360; break;
			case 22:iRes[index]=720; break;

			case 43:iRes[index]=360; break; 
			case 44:iRes[index]=480; break;
			case 45:iRes[index]=720; break;
			case 46:iRes[index]=1020; break;
		}
	}
	
	public String[] getVideoDetail(String videoid, int Quality)
	{
		
		String [] elements=new String[4];
		int i=0;
		//Log.i(TAG,"Elements Index: "+i+availVideo+videoid+this.videoId);
		if(this.videoId.equals(videoid))
		{
			for(i=0;i<availVideo;++i)
			{
				
				if(iTags[i]==Quality)
					break;
			}
		}
		elements[0]=videoId;
		elements[1]=""+iRes[i];
		elements[2]=""+iTags[i];
		elements[3]=conTainer[i];
		return elements;
	}
	
	public int getVideoDuration(String videoId)
	{
		return this.videoDuration;
	}
	
	public void addBeginParam()
	{
		for(int i=0;i<this.availVideo;++i)
		{
			String tempurl="";
			String urlParams[]=videoURLS[i].split("&");
			for(int j=0;j<urlParams.length;++j)
			{
				
				
				if(urlParams[j].contains("itag"))
				{
					
					tempurl+="begin=20"+"&";
				}
				if(j==urlParams.length-1)
					tempurl+=urlParams[j];
				else
					tempurl+=urlParams[j]+"&";
			}
			videoURLS[i]=tempurl;
		}	
		
		
		
		
		
	}
	
	public void dumpVideoInfo()
	{
		for(int i=0;i<this.availVideo;++i)
			Log.i(TAG,""+iTags[i]+iRes[i]+conTainer[i]);
	}
	
	public void updateUrl(String url, int index)
	{
		
		this.videoURLS[index]=url;
		
	}
	
	public void setVideoInfoDetail(String file_name) throws IOException
	{
		FileInputStream file_stream = new FileInputStream(file_name);
		DataInputStream in = new DataInputStream(file_stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String temp,tempUrl="";
		int i=0;
		boolean key_url=false, id_added=false;
		while ((temp = br.readLine()) != null)   
		{
			if((temp.contains("url=http://"))&&(temp.contains("videoplayback?")))
			{
				tempUrl="";
				iTags[i]=0;
				key_url=true;
			}

			
			if(temp.contains("length_seconds=")){
				
				this.videoDuration=Integer.parseInt(StringUtils.getRangeOfBytes(temp,"length_seconds=",'&'));
			}
			
			if(temp.contains("id=")&&(key_url)&&(!id_added))
			{
				tempUrl+=temp+"&";
				id_added=true;
			}
			
			if(temp.contains("sig=")&&(key_url)&&(id_added))
			{
				tempUrl+="signature="+temp.split("sig=")[1];
				String temp_url=tempUrl.split("url=")[1];
				
			    
				if(temp_url.contains("ratebypass=true"))
					rateBypass[i]=true;
				else
					rateBypass[i]=false;
				
				if(temp_url.contains("itag="))
				{
					iTags[i]=Integer.parseInt(StringUtils.getRangeOfBytes(temp_url,"itag=",'&'));
					this.setResulation(iTags[i],i);
					this.setContainer(iTags[i],i);

				
				}
				
				this.updateUrl(temp_url,i);
				//Log.i(TAG,temp_url);
				++i;
				key_url=false;
				id_added=false;

			}
			
			
			if((key_url)&&(!id_added)&&temp.contains("="))
			{
				String newtemp=temp;
				tempUrl+=newtemp+"&";
				
			}
		}
		this.availVideo=i;
	}	
}

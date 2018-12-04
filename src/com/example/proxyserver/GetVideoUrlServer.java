package com.example.proxyserver;


import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class GetVideoUrlServer {
	private String TAG="GetVideoUrlServer";
	private String prequery="el=embedded&ps=default&eurl=1&gl=US&hl=en";
	private String initialReq="http://www.youtube.com/?gl=US&hl=en";
	private String serverHost="www.youtube.com";
	private int serverPort=80;
	private int quality=0;
	private String cookieValue="";
	private String videoId="";
	private VideoInfo YouTubeVideoInfo;
	private String sourceUrl="";

	
	
	
	public GetVideoUrlServer(String url, int quality)
	{
		this.YouTubeVideoInfo=new VideoInfo();
		this.sourceUrl=url;
		this.quality=quality;

	}
	
	public String getVideoId()
	{
		return this.YouTubeVideoInfo.getVideoId();
	}
	
	public int getVideoQuality()
	{
		return this.quality;
	}
	public int receiveAndSetVideoUrl()
	{
		
		int responseCode=0;
		/* Set video Id*/
		Log.i(TAG,this.sourceUrl);
		this.sourceUrl+="&gl=US&hl=en&has_varified=1";
		this.YouTubeVideoInfo.setVideoId(this.sourceUrl);
		this.videoId=this.YouTubeVideoInfo.getVideoId();
		
		
		// TODO Send Initial requests to get session ID 
		/*getIntialResponse();*/
		
		// TODO Send watch?v=video_id request 		
		watchVideo(sourceUrl);
		
		// TODO send get_video_info? request 		

		try {
			String infoUrl="/get_video_info?video_id="+
							this.videoId+"&"+prequery;
			
			Log.i(TAG,"here1");
			/*TODO calling function to get video download urls*/
			responseCode=getVideoInfoUrls(infoUrl);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG,"here2");

		return responseCode;
	}
	
	public String getIndividualVideoUrlHttpRequest(int quality, long byteRangeone,long contentLength)
	{
		//String desUrl="Not Found";
		if(this.YouTubeVideoInfo.isUrlAvailable(quality)>-1)
		{
			String desUrl=this.YouTubeVideoInfo.getVideoUrl(quality);		
			HttpGet newRequest = new HttpGet(desUrl);
			
			String host=desUrl.split("/")[2];
			String comHeader="";
			comHeader+="GET /"+newRequest.getURI().toString().split("/")[3]+" HTTP/1.1"+"\r\n";
			comHeader+="Host: "+host+"\r\n";
			comHeader+="Accept-Language: "+"en-us,en;q=0.5"+"\r\n";
			if((byteRangeone>0)&&(contentLength>0))
			{
				comHeader+="Accept-Encoding: "+"identity"+"\r\n";
				comHeader+="Range: bytes="+byteRangeone+"-\r\n";
				
			}
			else
				comHeader+="Accept-Encoding: "+"gzip, deflate"+"\r\n";
			
			comHeader+="Accept: "+"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"+"\r\n";
			comHeader+="User-Agent: "+ "Mozilla/5.0 (X11; Linux x86_64; rv:5.0.1) Gecko/20100101 Firefox/5.0.1"+"\r\n";
			comHeader+="Accept-Charset: "+"ISO-8859-1,utf-8;q=0.7,*;q=0.7"+"\r\n";
			comHeader+="Connection: "+"close"+"\r\n";
			comHeader+=this.cookieValue+"\r\n";
			comHeader+="\r\n";
			
			return comHeader;
		}
		else
			return "HTTP/1.1 404";
	}
	public void updateIndivudualUrl(String url,int quality)
	{
		int index=this.YouTubeVideoInfo.isUrlAvailable(quality);
		if(index>-1)
		{
			this.YouTubeVideoInfo.updateUrl(url, index);
		
		}
		
	}
	
	/*
	private int getIntialResponse()
	{		
		int returnValue=0;

		if(this.sendRequest(this.initialReq)==200)
    	{
			Log.i(TAG,"getInitialResponse");
			returnValue=1;
    	}
		
		return returnValue;
	}*/

	private int watchVideo(String url)
	{
		int returnValue=0;
    	if(this.sendRequest(url)==200)
    	{
			Log.i(TAG,"watchVideo");
    		returnValue=1;
    	}
		
		return returnValue;
	}
	
	private int getVideoInfoUrls(String url) throws IOException
	{
		
		Log.i(TAG,"getVideoUrl");
		int responseCode=0;
		String strHeader="";
		String tempCookie=this.cookieValue.substring(0, this.cookieValue.length()-2);
		this.cookieValue=tempCookie;
		
		
		HttpGet newRequest=getHttpGetrequest(url);
		Header[] header=newRequest.getAllHeaders();
		for(int i=0;i<header.length;++i)
		{
			if(i==0)
			{
				strHeader+="GET "+newRequest.getURI()+" HTTP/1.1"+"\r\n";
				strHeader+=header[i].toString()+"\r\n";
			}
			else
				strHeader+=header[i].toString()+"\r\n";
		}
		strHeader+="\r\n";
		
		String []back=JaniFunctions.getvideoinfourls(strHeader, strHeader.length(),serverHost,serverPort,this.videoId).split("\r\n\r\n");		
		if(back[0].contains("HTTP/1.1 200 OK"))
		{
			
			String fileLoc=back[1].trim();
			fileLoc=JaniFunctions.decodevideoinfo(fileLoc,this.videoId);
			YouTubeVideoInfo.setVideoInfoDetail(fileLoc);
			//YouTubeVideoInfo.addBeginParam();
			responseCode=200;
		}
		else
			responseCode=403;
		
		return responseCode;
	}

	public HttpGet getHttpGetrequest(String url)
	{
	    
		HttpGet myget = new HttpGet(url);
		myget.addHeader("Host", serverHost);
	    myget.addHeader("Accept-Language", "en-us,en;q=0.5");
	    myget.addHeader("Accept-Encoding", "gzip, deflate");
	    myget.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	    myget.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:5.0.1) Gecko/20100101 Firefox/5.0.1");
	    myget.addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
	    //myget.addHeader("Accept-Charset", "utf-8");
	    myget.addHeader("Connection", "close");
	    
	    if(this.cookieValue!="")
	    	myget.addHeader("Cookie", this.cookieValue);
	    
	    return myget;
	}


	private int sendRequest(String url)
	{
		String strcode="";
		HttpResponse newResponse = null;
		HttpClient httpclient=new DefaultHttpClient();
		HttpGet newRequest=getHttpGetrequest(url);
		try 
		{
			newResponse=httpclient.execute(newRequest);
		} catch (ClientProtocolException e1) 
		{
			e1.printStackTrace();
		} catch (IOException e1) 
		{
		
			e1.printStackTrace();
		}
		strcode= newResponse.getStatusLine().toString().split(" ")[1];
		
		String tempCookie="";
    	Header resHeader[]=newResponse.getAllHeaders();
    	for(int i=0;i<resHeader.length;++i)
	    {
	    	if(resHeader[i].getName().equals("Set-Cookie"))
	    	{
	    		String checkCookie=resHeader[i].getValue().toString().split(";")[0]+"; ";
	    		if(this.cookieValue.contains(checkCookie))
	    			continue;
	    		else
	    			tempCookie+=resHeader[i].getValue().toString().split(";")[0]+"; ";
	    	}
	    }
    	this.cookieValue+=tempCookie;
    	return Integer.parseInt(strcode);
    }
	
	public String getVideofileName(String videoId, int Quality)
	{
		String[] videoElements=YouTubeVideoInfo.getVideoDetail(videoId,Quality);
		String fileName="";
		Log.i(TAG,"Elements length "+videoElements.length);
		for(int i=0;i<videoElements.length;++i)
		{
			if((videoElements[i].contains("flv"))||(videoElements[i].contains("webm"))||
			(videoElements[i].contains("mp4"))||(videoElements[i].contains("3gpp")))
				fileName+="."+videoElements[i];
			else
				fileName+=videoElements[i];
		}
		
		
		return fileName;
	}
	
	public int getVideoDuration(String videoid)
	{
		return YouTubeVideoInfo.getVideoDuration(videoid);
	}

}

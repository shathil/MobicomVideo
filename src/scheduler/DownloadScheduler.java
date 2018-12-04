package scheduler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class DownloadScheduler 
{
	public static native void loadaudretention(String time, String prob, int size);
	public static native int getexpectedviewduration(int nwint,int duration, int scope);
	
	
	private String timeSeries;
	private String viewProbablity;
	
	
	public void readAudienceRetention(String fileName) throws IOException
	{
		FileInputStream file_stream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(file_stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		this.timeSeries="";
		this.viewProbablity="";
		String temp;
		while ((temp = br.readLine()) != null)   
		{
			if(temp!=" ")
			{
				String tempNew[]=temp.split("\t");
				//Log.i("Scheduler", tempNew[0]+" "+tempNew[1]);
				this.timeSeries+=tempNew[0]+",";
				this.viewProbablity+=tempNew[1]+",";
				//Log.i("Scheduler", tempNew[0]+" "+tempNew[1]);
				//Log.i("Scheduler", "View Probab "+this.viewProbablity);
			}
			
		}
	}
	
	public void LoadAudRet()
	{
		DownloadScheduler.loadaudretention(timeSeries, viewProbablity,this.timeSeries.length());
	}
	
	
	public String getTimeSeries()
	{
		return this.timeSeries;
	}
	
	public String getViewProbability()
	{
		return this.viewProbablity;
	}
	public int distributionSize()
	{
		return this.timeSeries.length();
	}
}

package com.example.proxyserver;

public class ChunkScheduler {
	
	private int maxChunks=0;
	private int totalChunks=20;
	private int chunkStep=0;
	int chunkSize[];
	
	public ChunkScheduler(int videoDuration)
	{
		this.maxChunks=videoDuration;
	}
	
	public void setTotalChunks(int totalChunks)
	{
		int i=0;
		this.totalChunks=totalChunks;
		this.chunkSize=new int[this.totalChunks];
		this.chunkStep=this.maxChunks/totalChunks;
		this.setChunkSizes();
	}
	
	public void setnewChunkSizes()
	{
		this.chunkSize=new int[this.totalChunks];
		chunkSize[0]=56;
		chunkSize[1]=140;
		//chunkSize[2]=29;
		//chunkSize[3]=25;
		//chunkSize[4]=27;
		//chunkSize[5]=35;
		//chunkSize[6]=38;
		//chunkSize[7]=31;
		//chunkSize[8]=29;
		//chunkSize[9]=29;
		//chunkSize[10]=28;
		//chunkSize[11]=31;
		//chunkSize[12]=25;
		//chunkSize[13]=25;

	}
	
	private void setChunkSizes()
	{
		int i=0,step=this.chunkStep;
		for(i=0;i<totalChunks;++i)
		{
			chunkSize[i]=step;
			step+=50;
		}
	}
	public int getChunkSize(int i)
	{
		return chunkSize[i];
		
	}

	public int getTcpM(int i)
	{
		int numBurst=0,defBurst=this.getFastStart();
		int duration=this.getChunkSize(i);
		
    	if(duration%defBurst!=0)
    		numBurst=duration/defBurst+1;
    	else
    		numBurst=duration/defBurst;
		return numBurst;		
	}
	
	public int getFastStart()
	{
		if (VideoInfo.service==1) /*YouTube*/
			return 40;
		else
			return 15;
	}
	
}

#include "interface.h"
#include "Scheduler.h"
const char* TAG="ffmpegproxy";



int open_remote_host(const char *host, int port)
{
    struct sockaddr_in rem_addr;
    int len, s, x;
    struct hostent *H;
    int on = 1;

    H = gethostbyname(host);
    if (!H)
	return (-2);

    len = sizeof(rem_addr);

    s = socket(AF_INET, SOCK_STREAM, 0);
    if (s < 0)
	return s;

    setsockopt(s, SOL_SOCKET, SO_REUSEADDR, &on, 4);

    len = sizeof(rem_addr);
    memset(&rem_addr, '\0', len);
    rem_addr.sin_family = AF_INET;
    memcpy(&rem_addr.sin_addr, H->h_addr, H->h_length);
    rem_addr.sin_port = htons(port);
    x = connect(s, (struct sockaddr *) &rem_addr, len);
    if (x < 0) {
	close(s);
	return x;
    }

    return s;
}

unsigned long hex2int(char *a, unsigned int len)
{
    int i;
    unsigned long val = 0;

    for(i=0;i<len;i++)
       if(a[i] <= 57)
        val += (a[i]-48)*(1<<(4*(len-1-i)));
       else
        val += (a[i]-55)*(1<<(4*(len-1-i)));
    return val;
}
/*
jstring Java_com_example_proxyserver_JaniFunctions_decodevideoinfo(JNIEnv * env, jobject this, jstring urls,jstring id)
{
	char* temp_path=(char*)(*env)->GetStringUTFChars(env,urls, 0);
	const char* vid=(const char *)(*env)->GetStringUTFChars(env,id, 0);

	FILE *temp_file=fopen(temp_path,"r");
	fseek(temp_file, 0L, SEEK_END);
	int file_size = ftell(temp_file);
	fseek(temp_file, 0L, SEEK_SET);
	char* temp_file_buf=(char*)malloc(file_size);
	if(fread(temp_file_buf,1,file_size,temp_file)<file_size)
		LOGI("%s:%s:%d",TAG,"Could not read the complete content from the file:",file_size);

	fclose(temp_file);

	int i=0;
	int ascii_value=0,index=0;
	char temp_hex[2];
	jstring response="";

	char *new_sbuf=(char*)malloc(1024);
	char *new_path=(char*)malloc(100);
	strcpy(new_path,"/mnt/sdcard/");
	strcat(new_path,vid);
	strcat(new_path,"get_video_info.txt");
	FILE *new_file=fopen(new_path,"w");


	while(i<file_size)
	{
		if(index==1024)
		{
			if(fwrite(new_sbuf,1,index,new_file)<0)
				LOGI("%s %s",TAG,"Could not write into file");

			memset(new_sbuf,'\0',1024);
			index=0;


		}

		if((temp_file_buf[i]=='%')&&(i+2<file_size))
		{
			memcpy(temp_hex,temp_file_buf+i+1,2);
			ascii_value=hex2int(temp_hex,2);
			i=i+3;
			if((char)ascii_value=='%')
			{

				memset(temp_hex,'\0',2);
				memcpy(temp_hex,temp_file_buf+i,2);
				ascii_value=hex2int(temp_hex,2);
				new_sbuf[index]=(char)ascii_value;
				++index;
				i=i+2;

			}
			else if((char)ascii_value=='&')
			{
				new_sbuf[index]='\n';
				++index;

			}
			else
			{
				new_sbuf[index]=(char)ascii_value;
				++index;

			}

		}
		else
		{
			new_sbuf[index]=temp_file_buf[i];
			++i;
			++index;

		}
	}

	response=(*env)->NewStringUTF(env,new_path);
	fclose(new_file);
	free(new_path);
	free(new_sbuf);
	free(temp_file_buf);
	return response;
}



void stream_metadata(const char* filename,double seek_time)
{

		AVFormatContext *p_format_ctx;
		AVPacket packet;

		AVMetadataTag * curr;
		AVMetadata * md1;

		int i=0,video_stream=-1;

		if(av_open_input_file(&p_format_ctx, filename, NULL, 0, NULL)!=0)
		{

			LOGI("%s %s",TAG,"Could not open file");
			//return pack_info;

		}
		//LOGI("%s 2 %s",TAG,filename);

		if(av_find_stream_info(p_format_ctx)<0)
			//return pack_info;
			LOGI("%s %s",TAG,"Could not find streaming info");

		//LOGI("%s 3 %s",TAG,filename);

		for(i=0; i<p_format_ctx->nb_streams; i++)
		{
			if(p_format_ctx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO)
			{
			  video_stream=i;
			  break;
			}
		}

		md1 = p_format_ctx->metadata;
		curr = av_metadata_get (md1, "title", NULL, 0);
		LOGI ("%s %s", TAG,curr->value);


	    //const char* artist = av_dict_get(metadata, "artist", NULL, 0);
		//const char* title = av_dict_get(metadata, "title", NULL, 0)->value;


}
*/



/*
struct AVPacket_info get_kframe_index(const char* filename,double seek_time,int flag)
{

	AVFormatContext *p_format_ctx;
	AVPacket packet;
	struct AVPacket_info pack_info;
	pack_info.duration=0;
	pack_info.key_dts=0;
	pack_info.key_pos=0;
	pack_info.key_pts=0;
	pack_info.key_size=0;
	int i=0,video_stream=-1;

	if(av_open_input_file(&p_format_ctx, filename, NULL, 0, NULL)!=0)
	{

		LOGI("%s %s",TAG,"Could not open file");
		return pack_info;

	}

	if(av_find_stream_info(p_format_ctx)<0)
		return pack_info;


	for(i=0; i<p_format_ctx->nb_streams; i++)
	{
		if(p_format_ctx->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO)
		{
		  video_stream=i;
		  break;
		}
	}


	if(video_stream==-1)
		return pack_info;



	av_init_packet(&packet);
	double r_time=seek_time;
	int64_t seek_target=0;
	while(1)
	{
		seek_target=(int64_t)(r_time*AV_TIME_BASE);
		seek_target=av_rescale_q(seek_target, AV_TIME_BASE_Q,p_format_ctx->streams[video_stream]->time_base);
		if(av_seek_frame(p_format_ctx,video_stream,seek_target, AVSEEK_FLAG_ANY) < 0)
			break;
		else
		{
			if(av_read_frame(p_format_ctx, &packet)>=0)
			{

				if(packet.flags)
				{
					pack_info.key_pos=packet.pos;
					pack_info.key_dts=packet.dts;
					pack_info.key_pts=packet.pts;
					pack_info.key_size=packet.size;
					pack_info.duration=packet.duration;
					if(flag)
					 break;

				}

			}
		}
		av_free_packet(&packet);
		r_time+=0.01;
	}
	av_close_input_file(p_format_ctx);
	return pack_info;
}

jint Java_com_example_proxyserver_JaniFunctions_getkeyframepos(JNIEnv * env, jobject this, jint pos)
{

	return pos;
}

*/



int get_content_length(const char* header,int header_length)
{
	int pos=0,index=0;
	char length[12];
	memset(length,'\0',12);
	if(strstr(header,"Content-Length: ")!=NULL)
	{
		pos=header_length-strlen(strstr(header,"Content-Length: "))+strlen("Content-Length: ");
		while(header[pos]!='\r')
		{
			length[index]=header[pos];
			++index;
			++pos;
		}
	}
	return atoi(length);
}


void write_video_to_file(FILE* filename, char *filepath,char* data, int length)
{

	//struct AVPacket_info pack;
	filename=fopen(filepath,"a+");
	if(fwrite(data,1,length,filename)<=0)
	{
		LOGE("%s: Unable to write to file:%s\n",TAG,filepath);
	}
	fclose(filename);
}

/* JNI functions defined below */

void Java_com_example_proxyserver_JaniFunctions_releasemutex()
{
	pthread_mutex_unlock(&file_access_mutex);
}


jint Java_com_example_proxyserver_JaniFunctions_initializemutex()
{
	int success=1;
	pthread_mutex_init(&file_access_mutex, NULL);
	//pthread_mutex_init(&global_burst_param_access, NULL);
	return success;
}
jint Java_com_example_proxyserver_JaniFunctions_getmutexlock()
{

	if(pthread_mutex_trylock(&file_access_mutex)==-EBUSY)
		return 0;
	else
		return 1;
}



jstring Java_com_example_proxyserver_JaniFunctions_getvideoinfourls(JNIEnv * env, jobject this, jstring request, int len, jstring server, jint port, jstring id)
{
	const char* s_request=(const char *)(*env)->GetStringUTFChars(env,request, 0);
	const char* host=(const char *)(*env)->GetStringUTFChars(env,server, 0);
	const char* vid=(const char *)(*env)->GetStringUTFChars(env,id, 0);
	int sport=(int)port;

	int http_header_check=0,http_header_length=0;
	int writebytes=0,readbytes=0,n=0,sbo=0;

	/* Allocating buffer to read from socket*/
	u_char *sbuf=(char*)malloc(BUF_SIZE);
	memset(sbuf,'\0',BUF_SIZE);
	jstring response="NULL";

	/*Construct filename*/
	char *filepath=(char*)malloc(100);
	strcpy(filepath,"/mnt/sdcard/");
	strcat(filepath,vid);
	strcat(filepath,"temp_get_video_info.txt");

	/* Create socket, connect to the server and read from the server*/
	int scock=open_remote_host(host,sport);
	writebytes=write(scock,s_request,len);
	if(writebytes<0)
	{
		LOGE("%s: Error Writing Bytes",TAG);
		response=(*env)->NewStringUTF(env,"Error Sending Request");

	}
	else
	{
		//long int start=get_micro_seconds();
		while(1)
		{
			n=read(scock,sbuf+sbo,BUF_SIZE);
			sbo+=n;


			if(n==0)
			{
				break;
			}
		}
		//long int diff=get_time_dif(start);

		/* Get First bandwidth here
		if(diff>0)
		{
			//global_bandwidth=sbo/diff;
			//LOGI("%s Global Bandwidth: %d Bytes/s",TAG, global_bandwidth);
		}*/

	}
	close(scock);


	http_header_length=strlen(sbuf)-strlen(strstr(sbuf,"\r\n\r\n"))+strlen("\r\n\r\n");
	n=http_header_length;
	char* chunk_size=(char*)malloc(5);
	int i=0,count=0;
	FILE *fp=fopen(filepath,"w");
	while(count<10)
	{
		i=0;
		memset(chunk_size,'\0',5);
		while(sbuf[n]!='\r')
		{
			chunk_size[i]=(char)sbuf[n];
			++i;
			++n;
		}

		n=n+2;
		i=0;


		int ch_size=strtol(chunk_size,0,16);
		if(ch_size==0)
			break;


		char *chunk=(char*) malloc(ch_size);
		memset(chunk,'\0',ch_size);
		while(sbuf[n]!='\r')
		{
			chunk[i]=(char)sbuf[n];
			++i;
			++n;
		}

		int wr_bytes=fwrite(chunk,1,i,fp);
		if(wr_bytes<0)
		{
			LOGI("%s:Could not write to file %d",TAG,i);
		}
		n=n+2;

		free(chunk);
		++count;

	}
	fclose(fp);

	char* http_header=(char*)malloc(http_header_length+strlen(filepath)+1);
	memset(http_header,'\0',http_header_length+strlen(filepath)+1);
	memcpy(http_header,sbuf,http_header_length);
	strcat(http_header,filepath);

	response=(*env)->NewStringUTF(env,http_header);
	free(filepath);
	free(sbuf);
	free(chunk_size);
	free(http_header);
	return response;
}




int get_file_size(char * filepath)
{

		FILE *temp_file=fopen(filepath,"r");
		fseek(temp_file, 0L, SEEK_END);
		int file_size = ftell(temp_file);
		fseek(temp_file, 0L, SEEK_SET);

		return file_size;
}

void get_params(const char* holder, const char* param, char* value)
{
	int i=0,index=0;
	if(strstr(holder,param)!=NULL)
	{
		i=strlen(holder)-strlen(strstr(holder,param))+strlen(param);
		while(holder[i]!='\r')
		{
			value[index]=holder[i];
			++i;
			++index;
		}

	}
}



/* jstring video_file_name, duration, burst_duration, bitrate,*/
jstring Java_com_example_proxyserver_JaniFunctions_getvideo(JNIEnv * env, jobject this,
		jstring request, jstring other_params)
{

	jstring response="";
	char* s_request=(char *)(*env)->GetStringUTFChars(env,request, 0);
	char* o_params=(char *)(*env)->GetStringUTFChars(env,other_params, 0);
	char *host=(char*)malloc(255);
	char *videoId=(char*)malloc(50);
	char* temp=(char*)malloc(255);
	char *filepath=(char*)malloc(100);
	char *sbuf=(char*)malloc(4096);
	memset(sbuf,'\0',4096);
	char* temp_response=(char*) malloc(2048);
	memset(temp_response,'\0',2048);


	long int v_bitrate=0;
	double v_bandwidth=0;
	int v_duration=0;
	int burst_duration=0;
	int sport=80,n=0,master_thread=0;
	long int sbo=0,content_len=0, start_time=0,end_time=0;

	strcpy(filepath,"/mnt/sdcard/");
	FILE *fp;


	if(strstr(o_params,"Host: ")!=NULL)
	{
		memset(temp,'\0',255);
		get_params(o_params,"Host: ",temp);
		memset(host,'\0',255);
		memcpy(host,temp,strlen(temp));
		memset(temp,'\0',255);
	}

	if(strstr(o_params,"File-Name: ")!=NULL)
	{

		memset(temp,'\0',255);
		get_params(o_params,"File-Name: ",temp);
		memset(videoId,'\0',50);
		memcpy(videoId,temp,strlen(temp));
		strcat(filepath,videoId);
	}

	if(strstr(o_params,"Bit-Rate: ")!=NULL)
	{

		memset(temp,'\0',255);
		get_params(o_params,"Bit-Rate: ",temp);
		v_bitrate=atoi(temp);
	}

	if(strstr(o_params,"Bandwidth: ")!=NULL)
	{

		memset(temp,'\0',255);
		get_params(o_params,"Bandwidth: ",temp);
		v_bandwidth=atoi(temp);
		LOGI("%s:Bandwidth: %ld",TAG,v_bandwidth);
	}


	if(strstr(o_params,"Burst-Duration: ")!=NULL)
	{

		memset(temp,'\0',255);
		get_params(o_params,"Burst-Duration: ",temp);
		burst_duration=atoi(temp);
	}


	if(strstr(o_params,"Video-Duration: ")!=NULL)
	{

		memset(temp,'\0',255);
		get_params(o_params,"Video-Duration: ",temp);
		v_duration=atoi(temp);
	}

	free(temp);
	free(videoId);






	long int burst_size=v_bitrate*burst_duration;


	if(v_bitrate==0)
	{

		LOGI("%s %s",TAG,"Creating a new file.");
		master_thread=1;
		fp=fopen(filepath,"w");
		fclose(fp);
	}
	else
		LOGI("%s:%s",TAG,"File already exists.");


	/* sending request to the youtube server*/
	int scock=open_remote_host(host,sport);
	if(write(scock,s_request,strlen(s_request))<0)
	{
		LOGE("%s: Error Writing Bytes",TAG);
		response=(*env)->NewStringUTF(env,"Error Sending Request");
	}
	else
	{

		//long int start=get_micro_seconds();
		int header_flag=0;
		while(1)
		{
			/*if(master_thread)
			{
				/*read the schedule from */
			//}
			memset(sbuf,'\0',4096);
			n=read(scock,sbuf,4096);
			if(n>0)
			{
				if((strstr(sbuf,"\r\n\r\n"))&&(!header_flag))
				{
					int http_header_length=strlen(sbuf)-strlen(strstr(sbuf,"\r\n\r\n"))+strlen("\r\n\r\n");
					memcpy(temp_response,sbuf,http_header_length);
					int data_length=n-http_header_length;
					int byte_range=0;
					if(strstr(sbuf,"HTTP/1.1 200 OK"))
					{
						content_len=get_content_length(sbuf,strlen(sbuf));
						if(v_bitrate==0)
						{

							v_bitrate=content_len/v_duration;
							burst_size=v_bitrate*burst_duration;
							start_time=get_micro_seconds();
						}
						if(master_thread)
						{
							/*update bandwidth sec*/

							LOGI("%s Length: %d Bitrate: %d Burst Size: %ld",TAG,content_len,v_bitrate,burst_size);
						}


						//Java_com_example_proxyserver_JaniFunctions_initializemutex();
					}
					if(strstr(sbuf,"HTTP/1.1 206 Partial Content"))
					{
						//LOGI("%s %s",TAG,temp_response);
						content_len=get_content_length(sbuf,strlen(sbuf));

						//LOGI("%s Partial Content %d",TAG,content_len,bitrate);

					}

					if (data_length>0)
					{


						//while(pthread_mutex_trylock(&file_access_mutex)==EBUSY) usleep(1000);
						write_video_to_file(fp,filepath,sbuf+http_header_length,data_length);
						//pthread_mutex_unlock(&file_access_mutex);
						sbo+=data_length;
					}

					header_flag=1;

				}

				else
				{
					sbo+=n;
					/*Calculate bandwidth*/


					if(sbo>burst_size)
					{
						int write_bytes=sbo-burst_size;
						LOGI("%s Burst Size:%ld Sbo %ld",TAG,burst_size,sbo);
						//while(pthread_mutex_trylock(&file_access_mutex)==EBUSY) usleep(1000);
						write_video_to_file(fp,filepath,sbuf,write_bytes);
						//pthread_mutex_unlock(&file_access_mutex);
						break;
					}

					if(sbo<=burst_size)
					{
						//while(pthread_mutex_trylock(&file_access_mutex)==EBUSY) usleep(1000);
						write_video_to_file(fp,filepath,sbuf,n);
						//pthread_mutex_unlock(&file_access_mutex);
						if(sbo==burst_size)
							break;
					}// end if (sbo<=burst_size)
				}//end else (if not header)
			}// if (n>0)
			if(n==0)
			{
				LOGI("%s: written bytes: %d",TAG,sbo);
				break;
			}
		}
	}

	close(scock);
	end_time=get_time_dif(start_time);

	fp=fopen(filepath,"r+");
	fseek(fp, 0L, SEEK_END);
	long int file_size = ftell(fp);
	if(v_bandwidth==0)
	{

		v_bandwidth=(file_size/end_time)*1000000;
	}

	LOGI("%s Burst Size:%ld File Size %ld Bandwidth:%f",TAG,burst_size,file_size,v_bandwidth);

	char *content_length=(char*)malloc(255);
	memset(content_length,'\0',30);
	sprintf(content_length,"Downloaded-Bytes: %ld\r\nBit-Rate: %ld\r\nBandwidth: %f\r\n",file_size,v_bitrate,v_bandwidth);
	strcat(temp_response,content_length);

	response=(*env)->NewStringUTF(env,temp_response);


	free(temp_response);
	free(sbuf);
	free(content_length);
	free(host);
	free(filepath);

	return response;
}



jint Java_scheduler_DownloadScheduler_getexpectedviewduration(JNIEnv * env, jobject this,
		jint wrint, jint dur, jint bitrate, jint bandwidth, jint scope)
{
	//LOGI("%s TAG: inside getexpected",TAG);
	short int nw=(short int)wrint;
	short int duration=(short int)dur;
	short int sscope=(short int)scope;
	if((int)bitrate==0)
		return MIN_INTERVAL;

	update_schedule_heuristic(nw,duration,sscope);
	LOGI("%s TAG: burst size: %d", TAG,global_result);
	return global_result-MIN_INTERVAL;
}


void Java_scheduler_DownloadScheduler_loadaudretention(JNIEnv * env, jobject this,
		jstring time_series, jstring probability, jint size)
{
	//LOGI("%s loaded aud retention",TAG);
	char* s_tseries=(char *)(*env)->GetStringUTFChars(env,time_series, 0);
	char* s_pseries=(char *)(*env)->GetStringUTFChars(env,probability, 0);
	char time[5];
	char prob[5];
	memset(time,'\0',5);
	memset(prob,'\0',5);
	//LOGI("%s loaded aud retention",TAG);

	int i=0,index=0,j=0;
	while (i<(int)size)
	{

		memset(time,'\0',5);
		while(s_tseries[i]!=',')
		{
			time[index]=s_tseries[i];
			++i;
			++index;

		}
		index=0;
		++i;
		audience_time_series[j]=atoi(time);
		//LOGI("%s loaded time: %d",TAG,audience_time_series[j]);

		++j;

	}
	audience_size=j;
	index=0,i=0,j=0;
	while (i<size)
	{

		memset(prob,'\0',5);
		while(s_pseries[i]!=',')
		{
			prob[index]=s_pseries[i];
			++i;
			++index;

		}

		index=0;
		++i;

		if(strstr(prob," ")==NULL)
		{
			audience_view_series[j]=atof(prob);
			//LOGI("%s loaded aud retention: %0.2f",TAG,audience_view_series[j]);
			++j;
			if(j==audience_size)
				break;

		}

		/*handling empty space at the end of string*/
	}

	for(j=0;j<audience_size;++j)
	{

		if(j>0)
			audience_intr_series[j]=audience_view_series[j]-audience_view_series[j+1];
		else
			audience_intr_series[0] = 1 - audience_view_series[0];


		//LOGI("%s loaded aud retention: %0.2f",TAG,audience_intr_series[j]);

	}


	LOGI("%s loaded aud retention size: %d",TAG,audience_size);
	//free(time);
	//free(prob);
}








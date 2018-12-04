/*
 * interface.h
 *
 *  Created on: Aug 20, 2011
 *      Author: mhoque
 */
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <resolv.h>
#include <pthread.h>
#include <netdb.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include <errno.h>
#include <time.h>






#define     FIXED_FRAME_DIF 130000
#define		BUF_SIZE 	20480
#define 	MAX_BUF 	20480
//#define     STREAM_BUF  10240000
#define 	DEFAULT_PORT 	80
#define 	SQUID_PORT 	3128
#define 	INBUF_SIZE 4096

#define  	LOCAL_ADDRESS "127.0.0.1"

#define 	FIXED_RESPONSE_SIZE 1448
#define 	HTTP_HEADER_SIZE 325 //325+1
#define 	FLV_METADATA_SIZE 315
#define 	OTHER_METADATA_SIZE 549



#define     CONTENT_LENGTH 16


#define 	FIXED_PLAY_PERIOD 37 /*sec*/
#define     MAX_PLAY_PERIOD 47000 /*msec*/
#define 	FIXED_PLAY_THER_PERIOD 10000
#define     FLV_METADATA_ELEMENTS     10
#define     FLV_DATA_TYPE_NUMBER      0x00
#define     FLV_DATA_TYPE_BOOL        0x01
#define     FLV_DATA_TYPE_STRING      0x02
#define     FLV_DATA_TYPE_OBJECT      0x03
#define     FLV_DATA_TYPE_NULL        0x05
#define     FLV_DATA_TYPE_UNDEFINED   0x06



#define     FLV_DATA_TYPE_REFERENCE    0x07
#define     FLV_DATA_TYPE_MIXEDARRAY   0x08
#define     FLV_DATA_TYPE_OBJECT_END   0x09
#define     FLV_DATA_TYPE_ARRAY        0x0a
#define     FLV_DATA_TYPE_DATE         0x0b
#define     FLV_DATA_TYPE_LONG_STRING  0x0c
#define     FLV_DATA_TYPE_UNSUPPORTED  0x0d


static pthread_mutex_t file_access_mutex;

#define LOG_TAG "libffmpegproxy"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)





#ifndef INTERFACE_H_
#define INTERFACE_H_



struct sockets
{
	int mobile_tcpproxy;
	int tcpproxy_squid;
	int begin_burst;
	int sleep_time;
	int youtube_request;
	char ip_address[64];
};

struct youtube_video_property
{
	u_char video_playback_url[10240];
	long int content_length;
	struct sockets* sock_pair;

};

struct AVPacket_info
{

	int64_t key_pos;
	int64_t key_dts;
	int64_t key_pts;
	int key_size;
	int duration;
};

typedef struct string {
 unsigned long length;
 unsigned *data;
} string;

#endif /* INTERFACE_H_ */

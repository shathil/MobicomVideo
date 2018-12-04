//#include <jni.h>
#include <android/log.h>
#include "interface.h"
#include "PowerMonitor.h"

#define min(x,y) ((x) < (y)? (x) :(y))
#define MAX_VIDEO_DUR 1000
#define MIN_INTERVAL 5 //sec
const char *STAG= "scheduler";

int global_schduler_interrup_index=0;
int global_schduler_view_index=0;
int global_schduler_bandwidth=0;
int global_schduler_bitrate=0;
short global_scheduler_burst_size=0;
static pthread_mutex_t global_burst_param_access;




short int audience_size=0;
float audience_time_series[MAX_VIDEO_DUR];
float audience_view_series[MAX_VIDEO_DUR];
float audience_intr_series[MAX_VIDEO_DUR];
//short int final_schedule_times[MAX_VIDEO_DUR];

//short int timeStamp[MAX_VIDEO_DUR];
short int global_result=0;
short int result_incr=0;



int get_burst_size()
{

	return global_scheduler_burst_size;
}

long int get_micro_seconds()
{

	struct timeval tv;
	long int total;

	gettimeofday(&tv, NULL);
	total = tv.tv_sec*1000000+tv.tv_usec;
	return total;
}


long int get_time_dif(long int old_time)
{
	struct timeval tv;
	long int dif,total;

	gettimeofday(&tv, NULL);
	total = tv.tv_sec*1000000+tv.tv_usec;
	dif=total-old_time;
	return dif;

}
void update_schedule_heuristic(int nw, int duration, int scope)
{
	int r_s=0,btc=0;
	while(pthread_mutex_trylock(&global_burst_param_access)==EBUSY) usleep(1000);
	r_s=global_schduler_bitrate;
	btc=global_schduler_bandwidth;
	pthread_mutex_unlock(&global_burst_param_access);

	short int i=0,j,p_int_len, new_index=0;
    short int newdur,interval2,interval3;
    float P_int[MAX_VIDEO_DUR];
    short int length=audience_size;

    short int residual=duration-result_incr;
    duration=residual;

    //LOGI("%s new residual: %d", TAG,residual);

    for(i=0;i<length;++i)
    {
    	if(audience_time_series[i]>result_incr)
    	{
    		new_index=i;
    		break;
    	}
    }
    	LOGI("%s new index: %d", STAG,new_index);


    if(scope == 2)
    {
        interval2 = duration/10;
        LOGI("%s interval2: %d", STAG,interval2);
    }

    if (residual > 0)
    {
        float E_dl_waste_chosen, E_tail_chosen;
        float heuristic, dur_chosen, p_here, B_wasted, E_dl_wasted;
        newdur = residual;
        E_dl_waste_chosen = 0;
        E_tail_chosen = 0;
        heuristic = 0;
        dur_chosen = 1;
        if(duration == residual)
        {
            p_here = 1;
            LOGI("%s duration==residual", STAG);
        }
        else
        {
            for(i =new_index; i < length; i++)
            {
                if(audience_time_series[i] > (duration - residual))
                {
                    p_here = audience_view_series[i];
                    break;
                }
            }

            //LOGI("%s p_hera: %0.2f", TAG,p_here);

        }
        while(newdur > 0)
        //while(newdur > 0)
        {
            //LOGI("%s newdur: %d", TAG,newdur);

            float E_tail;
            B_wasted = 0;
            p_int_len = 0;
            for(i = new_index; i < length; i++)
            {
                if(audience_time_series[i] >= (duration - residual))
                {
                    P_int[p_int_len++] = audience_intr_series[i] /p_here;
                }
            }
            for(i = new_index; i < newdur; i++)
            {
                if((newdur*r_s)/btc > i)
                {
                    B_wasted = B_wasted + P_int[min(i, p_int_len -1 )] * (btc*i - i*r_s);
                }
                else
                {
                    B_wasted = B_wasted + P_int[min(i, p_int_len -1 )] * (newdur*r_s - i*r_s);
                }
            }
            if (nw == 1)
            {
                E_dl_wasted = WIFI_RX_POWER* B_wasted / btc;
                PowerMonitor(1,0);

               // LOGI("%s PowerMonitor wifi; dur:%d, rs:%ld, btc:%ld", TAG, newdur, r_s,btc);
                E_tail = getTailEnergy(newdur,r_s, btc);
                //E_tail = tail_energy_wifi(newdur, 0.1,r_s,btc,0.435);
                //LOGI("%s tail energy :%0.3f", TAG,E_tail);
            }
            else if(nw == 2)
            {

                E_dl_wasted = DCH_POWER*B_wasted/btc;
                PowerMonitor(2,1);
                //LOGI("%s PowerMonitor wifi; dur:%d, rs:%ld, btc:%ld", TAG, newdur, r_s,btc);
                E_tail = getTailEnergy(newdur,r_s, btc);
                //E_tail = tail_energy_3g(newdur,8,3,0,r_s,btc,0.15*3.7,0.08*3.7);
                //LOGI("%s tail energy :%0.3f", TAG,E_tail);
            }
            else if(nw == 3)
            {
                E_dl_wasted = LTE_RX_POWER*B_wasted /btc;
                PowerMonitor(3,1);
                E_tail = getTailEnergy(newdur,r_s, btc);

                //E_tail = tail_energy_LTE(newdur,10,0,r_s,btc,1.3);
            }
              if(scope>=2)
            {	float E_dl_wasted2, E_tail2;
                float newdur2 = residual-newdur;
                if (newdur2<=0)
                {
                    if(( (E_dl_wasted+E_tail)/newdur < heuristic) || heuristic==0)
                    {
                        E_dl_waste_chosen = E_dl_wasted;
                        E_tail_chosen = E_tail;
                        dur_chosen = newdur;
                        heuristic = (E_tail_chosen+E_dl_waste_chosen)/dur_chosen;
                    }
                }
                while (newdur2>0)
                {
                	float B_wasted2 = 0;
                    for (i=new_index; i <newdur2; i++)
                    {
                        float p_int_sig = 0;
                        for(j = new_index; j < length; j++)
                        {
                            if(audience_time_series[j] >= (duration - residual + newdur + i ))
                            {
                                p_int_sig = audience_intr_series[j] /p_here;
                                break;
                            }
                        }


                        if (newdur2*r_s/btc > i)
                            B_wasted2 = B_wasted2 + p_int_sig*(btc*i - i*r_s);
                        else

                            B_wasted2 = B_wasted2 + p_int_sig*(newdur2*r_s - i*r_s);
                    }


                    if (nw==1)
                    {
                        E_dl_wasted2 = WIFI_RX_POWER*B_wasted2/btc;

                        PowerMonitor(1,0);
                        E_tail2 = getTailEnergy(newdur2,r_s, btc);
                        //LOGI("%s tail energy :%0.3f", TAG,E_tail);
                       // E_tail2 = tail_energy_wifi(newdur2,0.1,r_s,btc,0.435);
                    }

                    else if (nw==2)
                    {
                      	E_dl_wasted2 = DCH_POWER*B_wasted2/btc;
                        PowerMonitor(2,1);
                        E_tail2 = getTailEnergy(newdur2,r_s, btc);

                        //E_tail2 = tail_energy_3g(newdur2,8,3,0,r_s,btc,0.15*3.7,0.08*3.7);

                    }
                    else if (nw==3)
                    {
                    	E_dl_wasted2 = LTE_RX_POWER*B_wasted2/btc;
                        PowerMonitor(3,1);
                        E_tail2 = getTailEnergy(newdur2,r_s, btc);

                        //E_tail2 = tail_energy_LTE(newdur2,10,0,r_s,btc,1.3);
                    }

                    if(scope==3){}
                    else{


                        if ((E_dl_wasted+E_tail+E_dl_wasted2+E_tail2)/(newdur+newdur2) < heuristic || heuristic==0)
                        {
        					   E_dl_waste_chosen = E_dl_wasted;
        					   E_tail_chosen = E_tail;
        					   dur_chosen = newdur;
        					   heuristic = (E_dl_wasted+E_tail+E_dl_wasted2+E_tail2)/(newdur+newdur2);
                        }

                    }


                    newdur2 = newdur2 - interval2;
                }//end of scope 2 while




            }

            else
            {
                   if (((E_dl_wasted+E_tail)/newdur < heuristic) || heuristic==0)
                   {
                        E_dl_waste_chosen = E_dl_wasted;
                        E_tail_chosen = E_tail;
                        dur_chosen = newdur;
                        heuristic = (E_tail_chosen+E_dl_waste_chosen)/dur_chosen;
                   }
            }
            newdur=newdur-MIN_INTERVAL;

        }//end of inner while
        global_result=dur_chosen;

        result_incr+=global_result;
        //LOGI("%s result:%d", TAG,global_result);
        //residual=residual - dur_chosen;
    }
}




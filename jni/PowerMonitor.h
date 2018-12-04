
#define DCH_TIMER_T1 8
#define FACH_TIMER_T2 3
#define FD_TIMER 5
#define DCH_POWER 0.78//Watt
#define FACH_POWER 0.59//Watt

#define PSMA_TIMER 0.2//s
#define WIFI_IDLE_POWER 0.435//s
#define WIFI_RX_POWER 0.78//s


#define LTE_RRC_TIMER 10//s
#define LTE_DRX_TIMER 0.75//s
#define LTE_IDLE_INA_POWER 1.3//W
#define LTE_DRX_INA_POWER 1.3//W
#define LTE_RX_POWER 1.58//W

static int radioInterface=0;
static int timerFlag=0;

struct burstDetail{
	float tIdle;
	float tTail;//s
	float E_tail;//J
	float E_burst;
};

static struct burstDetail *new_burst;

void PowerMonitor(int radioInter, int Flag);
void WiFiTailPower(int T_b, int r_s, int btc);
void HSPATailPower(int T_b, int r_s, int btc);
void LTETailPower(int T_b, int r_s, int btc);
double getTailEnergy(int burstDuration, int bitRate,int bandWidth);




void PowerMonitor(int radioInter, int Flag)
{
	new_burst=(struct burstDetail*)malloc(sizeof(struct burstDetail));
	new_burst->tIdle=0.0;
	new_burst->tTail=0.0;
	new_burst->E_tail=0.0;
	new_burst->E_burst=0.0;
	radioInterface=radioInter;
	timerFlag=Flag;
}

void WiFiTailPower(int T_b, int r_s, int btc)
{

	new_burst->tIdle = T_b*(1-r_s/btc);
	if(new_burst->tIdle > PSMA_TIMER)
	{
		new_burst->E_tail = WIFI_IDLE_POWER*new_burst->tIdle;
		new_burst->tTail = new_burst->tIdle;
	}
	else
	{
		new_burst->E_tail =WIFI_IDLE_POWER*PSMA_TIMER;
		new_burst->tTail = PSMA_TIMER;
	}
	new_burst->E_burst = WIFI_RX_POWER*T_b*r_s/btc;
}



void HSPATailPower(int T_b, int r_s, int btc)
{
	new_burst->tIdle = T_b*(1-r_s/btc);
	if (timerFlag ==0) // Fast Dormany is not enabled
	{
		if (new_burst->tIdle <= DCH_TIMER_T1)
		{
			new_burst->E_tail = DCH_POWER*new_burst->tIdle;
			new_burst->tTail = new_burst->tIdle;

		}
		else if (new_burst->tIdle <= DCH_POWER+FACH_POWER)
		{
			new_burst->E_tail = DCH_POWER*DCH_TIMER_T1 -
						FACH_POWER*DCH_TIMER_T1 +
						FACH_POWER*new_burst->tIdle;
			new_burst->tTail = new_burst->tIdle;
		}
		else
		{
			new_burst->E_tail = DCH_POWER*DCH_TIMER_T1 +
						FACH_POWER*DCH_TIMER_T1;
			new_burst->tTail = DCH_TIMER_T1+FACH_TIMER_T2;
		}
	}
	else //Fast Dormancy is enabled.
	{
		if (new_burst->tIdle <= FD_TIMER)
		{
			new_burst->E_tail = DCH_POWER*new_burst->tIdle;
			new_burst->tTail = new_burst->tIdle;
		}
		else
		{
			new_burst->E_tail = DCH_POWER*new_burst->tIdle;
			new_burst->tTail = FD_TIMER;

		}
	}

	new_burst->E_burst = DCH_POWER*T_b*r_s/btc;

}

void LTETailPower(int T_b, int r_s, int btc)
{
	new_burst->tIdle = T_b*(1-r_s/btc);
	if (timerFlag ==0)//DRX is not enabled
	{
		if (new_burst->tIdle <=LTE_RRC_TIMER)
		{
			new_burst->E_tail = LTE_IDLE_INA_POWER*new_burst->tIdle;
			new_burst->tTail = new_burst->tIdle;
		}
		else
		{
			new_burst->E_tail = LTE_IDLE_INA_POWER*new_burst->tTail;
			new_burst->tTail= LTE_RRC_TIMER;
		}
	}
	else //DRX is enabled
	{
		if (new_burst->tIdle<=LTE_DRX_TIMER)
		{
			new_burst->E_tail = LTE_DRX_INA_POWER*new_burst->tIdle;
			new_burst->tTail = new_burst->tIdle;
		}
		else
		{
			new_burst->E_tail = LTE_DRX_INA_POWER*new_burst->tTail;
			new_burst->tTail =  LTE_DRX_TIMER;
		}
	}
	new_burst->E_burst = LTE_RX_POWER*T_b*r_s/btc;
}


double getTailEnergy(int burstDuration, int bitRate,int bandWidth)
{
	switch(radioInterface)
	{
		case 1://WiFi
			WiFiTailPower(burstDuration, bandWidth, bandWidth);
			break;
		case 2://HSPA
			HSPATailPower(burstDuration, bitRate, bandWidth);
			break;
		case 3://LTE
			LTETailPower(burstDuration, bitRate, bandWidth);
			break;
	}

	return new_burst->E_tail;
}

double getTTail()
{
	return new_burst->tTail;
}

double getBurstEnergy()
{
	return new_burst->E_burst;
}

double getTotalEnergyPerBurst()
{
	return new_burst->E_tail+new_burst->E_burst;
}


	void resetValues()
	{
		free(new_burst);
	}


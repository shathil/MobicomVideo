package scheduler;

public class PowerMonitor 
{
	
	private static double DCH_TIMER_T1=8;//s
	private static double FACH_TIMER_T2=3.5;//s
	private static double FD_TIMER=5;//s
	private static double DCH_POWER=1.4;//Watt
	private static double FACH_POWER=0.7;//Watt
	
	private static double PSMA_TIMER=0.2;//s
	private static double WIFI_IDLE_POWER=0.435;//s
	private static double WIFI_RX_POWER=1.021;//s
	
	
	private static double LTE_RRC_TIMER=10;//s
	private static double LTE_DRX_TIMER=0.75;//s
	private static double LTE_IDLE_INA_POWER=1.3;//W
	private static double LTE_DRX_INA_POWER=1.3;//W
	private static double LTE_RX_POWER=1.4;//W
	
	private int radioInterface=0;
	private int timerFlag=0;
	
	private double tIdle=0;//s 
	private double tTail=0;//s
	private double E_tail=0;//J
	private double E_burst=0;
	
	public void PowerMonitor(int radioInter, int timerFlag)
	{
		this.radioInterface=radioInter;
		this.timerFlag=timerFlag;
	}
	
	
	private void WiFiTailPower(int T_b, int r_s, int btc)
	{
		
		this.tIdle = T_b*(1-r_s/btc);
		if (this.tIdle <= PowerMonitor.PSMA_TIMER)
		{
		    this.E_tail = PowerMonitor.WIFI_IDLE_POWER*this.tIdle;
		    this.tTail = this.tIdle;
		}
		else{
		    this.E_tail = PowerMonitor.WIFI_IDLE_POWER*PowerMonitor.PSMA_TIMER;
		    this.tTail = PowerMonitor.PSMA_TIMER;
		}
		this.E_burst = PowerMonitor.WIFI_RX_POWER*T_b*r_s/btc;
	}

	

	private void HSPATailPower(int T_b, int r_s, int btc)
	{
		this.tIdle = T_b*(1-r_s/btc);
		if (this.timerFlag ==0) // Fast Dormany is not enabled
		{
		    if (this.tIdle <= PowerMonitor.DCH_TIMER_T1)
		    {
		        this.E_tail = PowerMonitor.DCH_POWER*this.tIdle;
		        this.tTail = this.tIdle;
		        
		    }
		    else if (tIdle <= PowerMonitor.DCH_POWER+PowerMonitor.FACH_POWER)
		    {
		            this.E_tail = PowerMonitor.DCH_POWER*PowerMonitor.DCH_TIMER_T1 - 
		            		PowerMonitor.FACH_POWER*PowerMonitor.DCH_TIMER_T1 + 
		            		PowerMonitor.FACH_POWER*this.tIdle;
		            this.tTail = this.tIdle;
		    }
		    else
		    {
		            this.E_tail = PowerMonitor.DCH_POWER*PowerMonitor.DCH_TIMER_T1 +
		            		PowerMonitor.FACH_POWER*PowerMonitor.DCH_TIMER_T1;
		            this.tTail = PowerMonitor.DCH_TIMER_T1+PowerMonitor.FACH_TIMER_T2;
		    }
		}
		else //Fast Dormancy is enabled.
		{
		    if (this.tIdle <= FD_TIMER)
		    {
		        this.E_tail = PowerMonitor.DCH_POWER*this.tIdle;
		        this.tTail = this.tIdle;
		    }
		    else
		    {
		        this.E_tail = PowerMonitor.DCH_POWER*this.tIdle;
		        this.tTail = PowerMonitor.FD_TIMER;
		        
		    }
		}
		
		this.E_burst = PowerMonitor.DCH_POWER*T_b*r_s/btc;
		
	}
	
	private void LTETailPower(int T_b, int r_s, int btc)
	{		
		this.tIdle = T_b*(1-r_s/btc);
		if (this.timerFlag ==0)//DRX is not enabled
		{
		    if (this.tIdle <=PowerMonitor.LTE_RRC_TIMER)
	    	{
		        this.E_tail = PowerMonitor.LTE_IDLE_INA_POWER*this.tIdle;
		        this.tTail = this.tIdle;
	        }
		    else
		    {
		        this.E_tail = PowerMonitor.LTE_IDLE_INA_POWER*this.tTail;
		        this.tTail= PowerMonitor.LTE_RRC_TIMER;
		    }
		}
		else //DRX is enabled
		{
		    if (this.tIdle<=PowerMonitor.LTE_DRX_TIMER)
		    {
		        this.E_tail = PowerMonitor.LTE_DRX_INA_POWER*this.tIdle;
		        this.tTail = this.tIdle;
		    }
		    else
		    {
		        E_tail = PowerMonitor.LTE_DRX_INA_POWER*PowerMonitor.LTE_DRX_TIMER;
		        this.tTail =  PowerMonitor.LTE_DRX_TIMER;
		    }
		}
		this.E_burst = PowerMonitor.LTE_RX_POWER*T_b*r_s/btc;
	}
	
	
	public double getTailEnergy(int burstDuration, int bitRate,int bandWidth)
	{
		switch(this.radioInterface)
		{
			case 1://WiFi
				this.WiFiTailPower(burstDuration, bandWidth, bandWidth);
				break;
			case 2://HSPA
				this.HSPATailPower(burstDuration, bitRate, bandWidth);
				break;
			case 3://LTE
				this.LTETailPower(burstDuration, bitRate, bandWidth);
				break;
		}
		
		return this.E_tail;
	}
	
	public double getTTail()
	{
		return this.tTail;
	}
	
	public double getBurstEnergy()
	{
		return this.E_burst;
	}
	
	public double getTotalEnergyPerBurst()
	{
		return this.E_tail+this.E_burst;
	}
	
	
	public void resetValues()
	{
		this.E_tail=0.0;
		this.tTail=0.0;
	}
}

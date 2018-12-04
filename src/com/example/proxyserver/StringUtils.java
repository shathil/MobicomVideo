package com.example.proxyserver;

public class StringUtils {
	
	public static String getRangeOfBytes(String holder, String value, char delim)
	{
		int pos=0,temp_pos=0;
		pos=holder.lastIndexOf(value)+value.length();
		temp_pos=pos;
		while(holder.charAt(pos)!=delim)
		{
			++pos;
		}
		
		return holder.substring(temp_pos,pos);
		
	}

}

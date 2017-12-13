package com.icox.homebabysetting;

import java.io.FileDescriptor;

import android.util.Log;

public class Lwx_JNI {
	private static final String TAG = "lwxjni"; 
	    static {
	        // The runtime will add "lib" on the front and ".so" on the end of the name supplied to loadLibrary.
	    	try
	    	{
	    		Log.v(TAG, "try to load LWX_JNI.so"); 
	    		System.loadLibrary("LWX_JNI_JLF_F2");
	    		Log.v(TAG, "try to load LWX_JNI.so over"); 
	    	}
	    	catch (Exception e) {
	    		// TODO: handle exception
	    		Log.v(TAG, "fail to load libmy-gpio-jni-test.so"); 
	    	}
	    }
	    

	    public native int openGpioDev();
	    public native int closeGpioDev();
	    public native int getGpio(int num);//don't use 
	    public native int releaseGpio(int num);//don't use 
	    /*
	     cmd:			state
		15				0			led off
		15				1			led on
	    */
	    public native int setGpioState(int num,int state);
	    /*
	     cmd:			state 	udata
	     16					x      x
	     return
	     char[0]       0,getGpioState success  !0 ,fail
	     char[1]       1, support light,0,don't 
	     char[2]		1,status(light on), 0,status (light off)
	    */	    
	    public native int[] getGpioState(int num,int state,int[] udata,int arrayLen);

}

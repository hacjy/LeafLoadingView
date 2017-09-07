package com.ha.cjy.leafloadingview.utils;

/**日志类 */
public class Log {
	private static boolean mDebug = true;

	/**
	 * 设置日志状态
	 * @param debug  当debug为ture时显示日志，false时不显示日志
	 */
	public static void setDebug(boolean debug){
		mDebug = debug;
	}

	/**
	 * Send a DEBUG log message.
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 */
	public static void d(String tag,String msg){
		if(mDebug)
			android.util.Log.d(tag, msg);
	}


	/**
	 * Send an ERROR log message.
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 */
	public static void e(String tag,String msg){
		if(mDebug)
			android.util.Log.e(tag, msg);
	}

	/**
	 * Send a ERROR log message and log the exception.
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 * @param tr  An exception to log
	 */
	public static void e(String tag, String msg, Throwable tr){
		if(mDebug)
			android.util.Log.e(tag, msg,tr);
	}

	/**
	 * Send a VERBOSE log message.

	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 */
	public static void v(String tag,String msg){
		if(mDebug)
			android.util.Log.v(tag, msg);
	}

	/**
	 * Send a WARN log message.
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 */
	public static void w(String tag,String msg){
		if(mDebug)
			android.util.Log.w(tag, msg);
	}

	/**
	 * Send a INFO log message.
	 * @param tag  Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
	 * @param msg  The message you would like logged.
	 */
	public static void i(String tag,String msg){
		if(mDebug)
			android.util.Log.i(tag, msg);
	}
}

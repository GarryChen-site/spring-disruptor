package com.garry.springlifecycle.async;

public interface EventResultHandler {

	/**
	 * setup time out(MILLISECONDS) value for get event Event Result
	 * 
	 * @param timeoutforeturnResult
	 *            MILLISECONDS
	 */
	void setWaitforTimeout(int timeoutforeturnResult);

	/**
	 * get event Event Result until time out value: setTimeoutforeturnResult(int
	 * timeoutforeturnResult)
	 * 
	 * @return
	 */
	Object get();

	/**
	 * Blocking until get event Event Result
	 * 
	 * @return
	 */
	Object getBlockedValue();

	void send(Object eventResult);

	void clear();

}

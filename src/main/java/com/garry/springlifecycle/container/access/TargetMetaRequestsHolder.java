package com.garry.springlifecycle.container.access;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class TargetMetaRequestsHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2060798067579323872L;
	private static ThreadLocal targetMetaRequests = new ThreadLocal();

	public TargetMetaRequest getTargetMetaRequest() {
		return (TargetMetaRequest) targetMetaRequests.get();
	}

	public void setTargetMetaRequest(TargetMetaRequest targetMetaRequest) {
		targetMetaRequests.set(targetMetaRequest);
	}

	public void clear() {
		targetMetaRequests.remove();
	}

}

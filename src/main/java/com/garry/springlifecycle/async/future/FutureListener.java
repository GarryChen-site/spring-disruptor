package com.garry.springlifecycle.async.future;


import com.garry.springlifecycle.domain.message.DomainMessage;

public interface FutureListener {

	void action(DomainMessage domainMessage);
}

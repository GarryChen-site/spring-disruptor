/*
 * 
 */
package com.garry.springlifecycle.test.cqrs.b;


import com.garry.springlifecycle.annotation.model.Receiver;
import com.garry.springlifecycle.annotation.model.Send;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.ParameterVO;

public interface AbEventToCommandIF {
	@Send("CommandToB")
	DomainMessage ma(@Receiver AggregateRootB bModel, ParameterVO parameterVO);

}
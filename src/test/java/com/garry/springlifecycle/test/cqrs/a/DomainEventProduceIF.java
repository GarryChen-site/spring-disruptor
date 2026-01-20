/*
 * 
 */
package com.garry.springlifecycle.test.cqrs.a;


import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.ParameterVO;

public interface DomainEventProduceIF {

	DomainMessage sendtoAnotherAggragate(ParameterVO parameterVO);

}
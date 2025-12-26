package com.garry.springlifecycle.test.cqrs.a;


import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.model.Send;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.ParameterVO;
import org.springframework.stereotype.Component;

@JDComponent
@Introduce(values = {"message"})
public class DomainEventProducer implements DomainEventProduceIF {

	@Send("toEventB")
	public DomainMessage sendtoAnotherAggragate(ParameterVO parameterVO) {
		return new DomainMessage(parameterVO);
	}

}

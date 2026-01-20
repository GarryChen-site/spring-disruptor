/*
 * 
 */
package com.garry.springlifecycle.test.cqrs.b;


import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.ParameterVO;
import com.garry.springlifecycle.test.cqrs.RepositoryIF;
import org.springframework.stereotype.Component;

@JDComponent()
@Introduce(values = {"componentmessage"})
public class ABEventToCommand implements AbEventToCommandIF {

	private RepositoryIF aBRepository;
	
	public ABEventToCommand(RepositoryIF aBRepository) {
		super();
		this.aBRepository = aBRepository;
	}

	public DomainMessage ma(AggregateRootB bModel, ParameterVO parameterVO) {
		return new DomainMessage(parameterVO);

	}
}

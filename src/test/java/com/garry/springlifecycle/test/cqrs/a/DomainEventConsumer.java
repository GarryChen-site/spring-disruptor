package com.garry.springlifecycle.test.cqrs.a;

/*
 * 
 */


import com.garry.springlifecycle.annotation.Consumer;
import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.ParameterVO;
import com.garry.springlifecycle.test.cqrs.RepositoryIF;
import com.garry.springlifecycle.test.cqrs.b.AbEventToCommandIF;
import com.garry.springlifecycle.test.cqrs.b.AggregateRootB;

/**
 * acccept Domain message from @Send("mychannel") of @Introduce("message")
 * 
 * this is event futureTask message Listener;
 * 
 * 
 */
@Consumer("toEventB")
public class DomainEventConsumer implements DomainEventHandler {

	private RepositoryIF aBRepository;
	private AbEventToCommandIF aBEventToCommand;

	public DomainEventConsumer(RepositoryIF aBRepository, AbEventToCommandIF aBEventToCommand) {
		super();
		this.aBRepository = aBRepository;
		this.aBEventToCommand = aBEventToCommand;
	}

	public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
		ParameterVO parameterVO = (ParameterVO) event.getDomainMessage().getEventSource();
		String aggregateRootBId = parameterVO.getNextId();
		AggregateRootB modelB = aBRepository.getB(aggregateRootBId);
		DomainMessage nextCommandResult = aBEventToCommand.ma(modelB, parameterVO);
		event.getDomainMessage().setEventResult(nextCommandResult.getBlockEventResult());

	}

}

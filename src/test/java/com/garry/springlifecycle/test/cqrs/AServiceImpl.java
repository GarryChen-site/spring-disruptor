/*
 * 
 */
package com.garry.springlifecycle.test.cqrs;


import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.JDService;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.a.AggregateRootA;
import com.garry.springlifecycle.test.cqrs.b.AggregateRootB;
import org.springframework.stereotype.Service;

@JDService("myaService")
@Introduce(values = {"componentmessage"})
public class AServiceImpl implements AService {

	private RepositoryIF aBRepository;
	
	public AServiceImpl(RepositoryIF aBRepository) {
		super();
		this.aBRepository = aBRepository;
	}

	public AggregateRootA getAggregateRootA(String id) {
		return aBRepository.getA(id);
	}
	
	public AggregateRootB getAggregateRootB(String id) {
		return aBRepository.getB(id);
	}


	public DomainMessage commandAandB(String rootId, AggregateRootA model, int state) {
		System.out.print("\n send to AggregateRootA =" + model.getId());
		return new DomainMessage(new ParameterVO(aBRepository.loadSequencId(), state, "22"), 60000);
	}

}

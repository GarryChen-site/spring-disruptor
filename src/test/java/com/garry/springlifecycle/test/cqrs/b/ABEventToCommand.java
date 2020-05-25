/*
 * Copyright 2003-2009 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain event copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

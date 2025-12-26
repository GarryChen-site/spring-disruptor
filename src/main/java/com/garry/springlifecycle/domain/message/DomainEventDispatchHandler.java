package com.garry.springlifecycle.domain.message;



import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.garry.springlifecycle.domain.message.consumer.ConsumerMethodHolder;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * action by annotation "@onEvent("topicName")"
 * 
 * 
 * @author banq
 * 
 */
public class DomainEventDispatchHandler implements DomainEventHandler<EventDisruptor> {
	public final static String module = DomainEventDispatchHandler.class.getName();

	private ConsumerMethodHolder consumerMethodHolder;

	private ApplicationContext applicationContext;

	public DomainEventDispatchHandler(ConsumerMethodHolder consumerMethodHolder, ApplicationContext applicationContext) {
		super();
		this.consumerMethodHolder = consumerMethodHolder;
		this.applicationContext = applicationContext;
	}

	public String getSortName() {
		Object o = applicationContext.getBean(consumerMethodHolder.getClassName());
		return o.getClass().getName();
	}

	// 回调消息
	@Override
	public void onEvent(EventDisruptor event, final boolean endOfBatch) throws Exception {
		try {
			Method method = consumerMethodHolder.getMethod();
			Class[] pTypes = method.getParameterTypes();
			if (pTypes.length == 0) {
				Object o = applicationContext.getBean(consumerMethodHolder.getClassName());
				method.invoke(o, new Object[] {});
			}
			Object parameter = event.getDomainMessage().getEventSource();
			if (parameter == null) {
				Debug.logError("[Jdonframework]DomainMessage's EventSource is null, need " + pTypes[0].getName(), module);
				return;
			}

			Object[] parameters = new Object[pTypes.length];
			int i = 0;
			for (Class pType : pTypes) {
				if (pType.isAssignableFrom(parameter.getClass())) {
					parameters[i] = parameter;
				} else {
					// init other parameter to event instance;
					if (!pType.isPrimitive())
						parameters[i] = pType.newInstance();
					else
						parameters[i] = defaultValues.get(pType);
				}
				i++;
			}
			Object o = applicationContext.getBean(consumerMethodHolder.getClassName());
			Object eventResult = method.invoke(o, parameters);
			event.getDomainMessage().setEventResult(eventResult);
		} catch (Exception e) {
			Debug.logError("[Jdonframework]" + consumerMethodHolder.getClassName() + " method with @onEvent error: " + e, module);
		}

	}

	private final static Map<Class<?>, Object> defaultValues = new HashMap<Class<?>, Object>();
	static {
		defaultValues.put(String.class, "");
		defaultValues.put(Integer.class, 0);
		defaultValues.put(int.class, 0);
		defaultValues.put(Long.class, 0L);
		defaultValues.put(long.class, 0L);
		defaultValues.put(Character.class, '\0');
		defaultValues.put(char.class, '\0');
		// etc
	}

}

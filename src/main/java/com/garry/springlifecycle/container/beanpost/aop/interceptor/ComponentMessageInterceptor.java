package com.garry.springlifecycle.container.beanpost.aop.interceptor;

import com.garry.springlifecycle.annotation.model.Send;
import com.garry.springlifecycle.async.EventMessageFire;
import com.garry.springlifecycle.async.future.FutureListener;
import com.garry.springlifecycle.domain.message.DomainMessage;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ComponentMessageInterceptor implements MethodInterceptor {

    private ApplicationContext applicationContext;

    private EventMessageFire eventMessageFire;

    public ComponentMessageInterceptor(ApplicationContext applicationContext, EventMessageFire eventMessageFire) {
        this.applicationContext = applicationContext;
        this.eventMessageFire = eventMessageFire;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!invocation.getMethod().isAnnotationPresent(Send.class)){
            return invocation.proceed();
        }

        final Send send = invocation.getMethod().getAnnotation(Send.class);
        String channel = send.value();
        Object result = null;
        try {
            result = invocation.proceed();

            DomainMessage domainMessage = null;
            if (DomainMessage.class.isAssignableFrom(result.getClass())) {
                domainMessage = (DomainMessage) result;
            } else {
                domainMessage = new DomainMessage(result);
            }
            eventMessageFire.fire(domainMessage, send);

            final Object listener = applicationContext.getBean(channel);
            if (listener != null && listener instanceof FutureListener) {
                eventMessageFire.fire(domainMessage, send, (FutureListener) listener);
            } else {
                eventMessageFire.fireToModel(domainMessage, send, invocation);
            }
        }catch (Exception e){

        }
        return result;
    }
}

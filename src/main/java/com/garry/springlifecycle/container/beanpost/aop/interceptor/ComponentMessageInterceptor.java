package com.garry.springlifecycle.container.beanpost.aop.interceptor;

import com.garry.springlifecycle.annotation.model.Send;
import com.garry.springlifecycle.async.EventMessageFire;
import com.garry.springlifecycle.async.future.FutureListener;
import com.garry.springlifecycle.domain.message.DomainMessage;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("componentmessage")
public class ComponentMessageInterceptor implements MethodInterceptor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private EventMessageFire eventMessageFire;

    public ComponentMessageInterceptor(EventMessageFire eventMessageFire) {
        this.eventMessageFire = eventMessageFire;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!invocation.getMethod().isAnnotationPresent(Send.class)) {
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

            final boolean isExist = applicationContext.containsBean(channel);
            if (!isExist) {
                eventMessageFire.fireToModel(domainMessage, send, invocation);
            }
            final Object listener = applicationContext.getBean(channel);
            if (listener != null && listener instanceof FutureListener) {
                eventMessageFire.fire(domainMessage, send, (FutureListener) listener);
            } else {
                eventMessageFire.fireToModel(domainMessage, send, invocation);
            }
        } catch (Exception e) {

        }
        return result;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

/*
 *
 */
package com.garry.springlifecycle.domain.message;


import com.garry.springlifecycle.annotation.Interceptor;
import com.garry.springlifecycle.annotation.model.Send;
import com.garry.springlifecycle.async.EventMessageFire;
import com.garry.springlifecycle.async.future.FutureListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * this is for domain model, there is another for components/services
 * com.jdon.aop.interceptor.ComponentMessageInterceptor
 * <p>
 * useage see:com.jdon.sample.test.domain
 * <p>
 * 1. create dynamic proxy for event Model in DomainCacheInterceptor.
 * <p>
 * <p>
 * 2. intercepte the method with @send
 * <p>
 * 3. @Channel will accept the message;
 *
 * 不能是@Interceptor 因为这算一个组件
 */


@Component("message")
public class MessageInterceptor implements MethodInterceptor {
    public final static String module = MessageInterceptor.class.getName();

    private ApplicationContext applicationContext;
    protected EventMessageFire eventMessageFirer;

    public MessageInterceptor(ApplicationContext applicationContext, EventMessageFire eventMessageFirer) {
        super();
        this.applicationContext = applicationContext;
        this.eventMessageFirer = eventMessageFirer;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!invocation.getMethod().isAnnotationPresent(Send.class))
            return invocation.proceed();

        Send send = invocation.getMethod().getAnnotation(Send.class);
        String channel = send.value();
        Object result = null;
        try {

            result = invocation.proceed();

            DomainMessage message = null;
            if (DomainMessage.class.isAssignableFrom(result.getClass())) {
                message = (DomainMessage) result;
            } else {
                message = new DomainMessage(result);
            }
            eventMessageFirer.fire(message, send);

            // older queue @Send(myChannl) ==> @Component(myChannl)
            Object listener = applicationContext.getBean(channel);
            if (listener != null && listener instanceof FutureListener)
                eventMessageFirer.fire(message, send, (FutureListener) listener);

            eventMessageFirer.fireToModel(message, send, invocation);

        } catch (Exception e) {
        }
        return result;
    }

}

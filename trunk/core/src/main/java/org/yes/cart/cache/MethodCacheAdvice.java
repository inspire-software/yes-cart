package org.yes.cart.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.yes.cart.domain.misc.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Serves Cacheable and CacheFlush annotations.
 * Ogirinal from ru.dev2dev.cache. Added small fixes for
 * cache name determination and initialization. Added  cache for
 * annotated method, because reflection operations to get
 * annotations on method are very expensive.
 *
 * Well know issue - not work with overloaded methods.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Component
@Aspect
public class MethodCacheAdvice implements ApplicationContextAware {

    private Map<String, Cache> cacheMap;

    private ApplicationContext applicationContext;

    //Class, mehtod name -  annotation
    private final Map<Pair<Class, String>, Annotation> classMethodAnnotationMap =
            new HashMap<Pair<Class, String>, Annotation>();


    /**
     * Caches method result if method is marked by ru.dev2dev.mzungu.cache.Cacheable annotation
     *
     * @param pjp ProceedingJoinPoint
     * @return method execution result
     * @throws Throwable in case of error
     */
    @Around("@annotation(Cacheable)")
    public Object doCache(final ProceedingJoinPoint pjp) throws Throwable {

        Cache cache = getCache(getAnnotation(pjp, Cacheable.class).value());

        String key = getCacheKey(pjp.getSignature().toLongString(), pjp.getArgs());
        Element element = cache.get(key);
        if (element == null) {
            element = new Element(key, pjp.proceed());
            cache.put(element);
        }

        return element.getValue();
    }

    /**
     * Flush cache AFTER method execution if method is marked CacheFlush annotation
     *
     * @param jp JoinPoint
     * @throws Throwable in case of error
     */
    @After("@annotation(CacheFlush)")
    public void doCacheFlush(JoinPoint jp) throws Throwable {
        final Cache cache = getCache(getAnnotation(jp, CacheFlush.class).value());
        cache.flush();
    }

    private Cache getCache(final String cacheName) throws NoSuchMethodException {
        final Cache cache = getCacheMap().get(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache bean with name '" + cacheName + "' not found");
        }
        return cache;
    }


    private <T extends Annotation> T getAnnotation(final JoinPoint pjp, final Class<T> annotation)
            throws NoSuchMethodException {
        /*
        Get the arguments as signature
        final Class[] argsClasses = new Class[pjp.getArgs().length];
        for (int i = 0; i < pjp.getArgs().length; i++) {
            Object arg = pjp.getArgs()[i];
            argsClasses[i] = arg.getClass();
        } */


        final Class targetClass = pjp.getTarget().getClass();
        final String methodName = pjp.getSignature().getName();
        final Pair<Class, String> key = new Pair(targetClass, methodName);
        if (classMethodAnnotationMap.containsKey(key)) {
            return (T) classMethodAnnotationMap.get(key);
        }

        final Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (methodName.equalsIgnoreCase(method.getName())) {
                T methodAnnotation = method.getAnnotation(annotation);
                classMethodAnnotationMap.put(key, methodAnnotation);
                return methodAnnotation;
            }
        }
        return null;
    }

    /**
     * Get the cache key.
     * @param signature method signature.
     * @param arguments value of arguments
     * @return key for cache.
     */
    private String getCacheKey(final String signature, final Object[] arguments) {
        StringBuilder sb = new StringBuilder(signature);
        if (arguments != null) {
            for (Object argument : arguments) {
                if (argument instanceof Iterable) {
                    for (Object argInList : (Iterable) argument) {
                        sb.append(":").append(argInList);
                    }
                } else {
                    sb.append(".").append(argument);
                }

            }
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * Get the all caches.
     * @return mas name - cache
     */
    private synchronized Map<String, Cache> getCacheMap() {
        if (cacheMap == null) {
            cacheMap = applicationContext.getBeansOfType(Cache.class);
        }
        return cacheMap;
    }

}

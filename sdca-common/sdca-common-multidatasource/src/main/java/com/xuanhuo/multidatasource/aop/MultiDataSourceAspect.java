package com.xuanhuo.multidatasource.aop;

import com.xuanhuo.multidatasource.annotation.MultiDataSource;
import com.xuanhuo.multidatasource.context.MultiDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class MultiDataSourceAspect {

    @Pointcut("@annotation(com.xuanhuo.multidatasource.annotation.MultiDataSource)")
    public void multiDataSourcePointCut(){}

    @Around("multiDataSourcePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("*******************进入多数据源AOP逻辑！！！！！");
        String dsKey = getAnnotation(joinPoint).value();
        System.out.println("*******************dsKey:" + dsKey);
        MultiDataSourceContextHolder.setContextKey(dsKey);
        try{
            return joinPoint.proceed();
        }finally {
            MultiDataSourceContextHolder.removeContextKey();
        }
    }

    private MultiDataSource getAnnotation(ProceedingJoinPoint joinPoint){
        Class<?> targetClass = joinPoint.getTarget().getClass();
        MultiDataSource dsAnnotation = targetClass.getAnnotation(MultiDataSource.class);
        if(Objects.nonNull(dsAnnotation)){
            return dsAnnotation;
        }else{
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            return methodSignature.getMethod().getAnnotation(MultiDataSource.class);
        }
    }
}

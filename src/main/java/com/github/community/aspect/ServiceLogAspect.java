package com.github.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * 第一个 * 代表接受任何返回值
     * com.github.community.service 包下的 .* 代表所有类，再跟上 .* 代表所有方法 ,(..) 代表方法接受任何参数
     */
    @Pointcut("execution(* com.github.community.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户 [1.2.3.4]，在[xxx],访问了 [com.github.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // joinPoint.getSignature().getDeclaringTypeName() -> com.github.community.service
        // joinPoint.getSignature().getName() -> methodName
        String declaringTypeAndMethodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s]", ip, date, declaringTypeAndMethodName));
    }

}

package com.mysql.namedlock.common.distributelock.aop;

import com.mysql.namedlock.common.distributelock.NamedLock;
import com.mysql.namedlock.common.distributelock.annotation.DistributeLock;
import com.mysql.namedlock.common.distributelock.dao.NamedLockDao;
import com.mysql.namedlock.common.distributelock.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributeLockAop {
  private final DistributeLockTransaction transaction;
  private final NamedLockDao namedLockDao;

  // DistributeLock 어노테이션에 패키지 경로
  @Around("@annotation(com.mysql.namedlock.common.distributelock.annotation.DistributeLock)")
  public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

    String key = (String) getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributeLock.key());
    String useType = (String) getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributeLock.useType());

    LockService lockService = NamedLock.getLockService(useType);
    List<String> locks = lockService.lock(key, useType);

    try {
      for (String lock : locks) {
        Integer result = namedLockDao.getLock(lock);
        boolean isLocked = (result != null && result == 1);
        log.info("lock key => {}, isLock {}", lock, isLocked);

        if (!isLocked) {
          String message = "";
          if (NamedLock.UseType.MENU.name().equalsIgnoreCase(useType)) {
            message = "지금은 주문량이 많아 잠시 후에 다시 시도해주시기 바랍니다.";
          }

          throw new Exception(message);
        }
      }

      // 동시성 처리를 하기 위해서는 락 획득 이후 트랜잭션이 시작되어야 하고 트랜잭션이 커밋되고 난 이후 락이 해제되어야
      // 하기때문에 AOP로 한번 감싸준다.
      return transaction.proceed(joinPoint);
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    } finally {
      for (String lock : locks) {
        namedLockDao.releaseLock(lock);
        log.info("unlock key => {}", lock);
      }

      locks.clear();
    }
  }

  // 메서드에 파라미터로 넘어온 값을 추출한다.
  private Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
    ExpressionParser parser = new SpelExpressionParser();
    StandardEvaluationContext context = new StandardEvaluationContext();

    for (int i = 0; i < parameterNames.length; i++) {
      context.setVariable(parameterNames[i], args[i]);
    }

    if (key != null && !key.startsWith("#")) {
      return key;
    }

    return parser.parseExpression(key).getValue(context, Object.class);
  }
}
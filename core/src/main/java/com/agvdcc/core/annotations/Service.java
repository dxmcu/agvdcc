package com.agvdcc.core.annotations;

import java.lang.annotation.*;

/**
 * Service类注解
 * 所有的Service都要注明该注解，用于系统启动时扫描注册
 *
 * @author Laotang
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}

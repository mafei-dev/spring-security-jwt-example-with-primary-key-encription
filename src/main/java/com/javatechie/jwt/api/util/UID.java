package com.javatechie.jwt.api.util;

import javax.validation.Payload;
import java.lang.annotation.*;

/*
  @Author kalhara@bowsin
  @Created 9/30/2020 9:58 PM  
*/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UID {
    boolean encrypt();

    boolean decrypt();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

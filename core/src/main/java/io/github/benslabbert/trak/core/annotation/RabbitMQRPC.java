package io.github.benslabbert.trak.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RabbitMQRPC {

    String message() default "RabbitMQ RPC method";
}

package com.seongbindb.javatest;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //메소드에서 사용할 것이다,
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 이 애노테이션을 유지
@Test
@Tag("slow")
public @interface SlowTest {

}

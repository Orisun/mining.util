package com.orisun.mining.util.dao;

import java.lang.annotation.*;
  
//用于描述类、接口(包括注解类型) 或enum声明
@Target(ElementType.TYPE)
//可以通过反射读取注解
@Retention(RetentionPolicy.RUNTIME)
//可被文档化
@Documented
public @interface Table {
	public String name();
}

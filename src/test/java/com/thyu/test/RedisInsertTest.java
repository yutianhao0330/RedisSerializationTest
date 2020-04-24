package com.thyu.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.ss.formula.ptg.StringPtg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thyu.common.utils.DateUtil;
import com.thyu.common.utils.RandomUtil;
import com.thyu.common.utils.StringUtil;
import com.thyu.domain.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:redis.xml")
public class RedisInsertTest {
	
	@Resource
	private RedisTemplate redisTemplate;
	
	private User generateUser() {
		User user = new User();
		String[] emailsuffixs = {"@qq.com","@163.com","@sian.com","@gmail.com","@sohu.com","@hotmail.com"};
		//姓名随机三个汉字
		user.setName(StringUtil.randomChineseString(3));
		//随机性别
		user.setGender(RandomUtil.random(0, 1)==1?"男":"女");
		//随机手机号
		String phone = "13";
		for(int i=0;i<9;i++) {
			phone+=RandomUtil.random(0, 9);
		}
		user.setPhone(phone);
		//随机邮箱
		user.setEmail(StringUtil.randomEnglishString(RandomUtil.random(3, 20))+emailsuffixs[RandomUtil.random(0, 5)]);
		//随机生日
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(1949, 3, 25);
		end.set(2002, 3, 24);
		user.setBirthday(DateUtil.randomDate(start.getTime(), end.getTime()));
		return user;
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testInsertByJDK() {
		List<User> userList = new ArrayList<User>();
		for(int i=1;i<=100000;i++) {
			User user = generateUser();
			//设置id
			user.setId(i);
			userList.add(user);
		}
		Object[] users = userList.toArray();
		System.out.println("存储开始.....");
		long start = System.currentTimeMillis();
		redisTemplate.opsForList().leftPushAll("users", users);
		long end = System.currentTimeMillis();
		System.out.println("存储完成.....");
		System.out.println("采用了JDK序列化的方式存储了"+userList.size()+"条数据一共耗时："+(end - start)+"ms");
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testInsertByJSON() {
		List<User> userList = new ArrayList<User>();
		for(int i=1;i<=100000;i++) {
			User user = generateUser();
			//设置id
			user.setId(i);
			userList.add(user);
		}
		Object[] users = userList.toArray();
		System.out.println("存储开始.....");
		long start = System.currentTimeMillis();
		redisTemplate.opsForList().leftPushAll("users", users);
		long end = System.currentTimeMillis();
		System.out.println("存储完成.....");
		System.out.println("采用了JSON序列化的方式存储了"+userList.size()+"条数据一共耗时："+(end - start)+"ms");
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testInsertByHash() {
		Map<String, User> map = new HashMap<String, User>();
		for(int i=1;i<=100000;i++) {
			User user = generateUser();
			//设置id
			user.setId(i);
			map.put(i+"", user);
		}
		System.out.println("存储开始.....");
		long start = System.currentTimeMillis();
		redisTemplate.opsForHash().putAll("users", map);
		long end = System.currentTimeMillis();
		System.out.println("存储完成.....");
		System.out.println("采用了HASH,JDK序列化的方式存储了"+map.size()+"条数据一共耗时："+(end - start)+"ms");
	}
}

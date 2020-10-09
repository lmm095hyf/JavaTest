package com.cn.util;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.cn.config.Config;
import com.cn.javabean.Student;

/**
 * 对于数据库连接做一个工具类
 * 1.数据库的连接
 * 2.关闭资源
 * 3.查询的封装
 * 4.增删改的封装
 * @author fangjun
 *
 */
public class DButil {

	//需要拿properties对象 声明properties对象
	private static Properties pop;

	/**
	 * 增删改查
	 * 增删改----都没有结果集   对应的sql不同
	 * 查 ------ 有结果  需要去封装
	 * 1.select count(*) from studentmsg where sid =?;
	 */

	public static int selectCount(String sql,String... args){
		//声明连接
		Connection conn = null;
		//获取连接
		conn = getConn();
		//加载sql
		PreparedStatement pst = null;
		//使用一个结果接受
		int countNum = 0;
		//声明结果集
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			//给sql传递参数
			for(int i = 0;i<args.length;i++){
				pst.setString(i+1, args[i]);
			}
			//拿结果
			rs = pst.executeQuery();
			while(rs.next()){
				//不为空
				countNum = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			free(conn, pst, rs);
		}

		return countNum;
	}

	//2.select * from studentmsg where sid = 1;
	public static <T> T selectToBean(String sql,Class<T> cla,String... args){
		//获取连接
		Connection conn = getConn();
		//预处理对象
		PreparedStatement pst = null;
		//结果集
		ResultSet rs = null;

		//声明实体
		T t = null;
		try {
			//加载sql
			pst = conn.prepareStatement(sql);
			//传递参数
			for(int i = 0;i<args.length;i++){
				//给参数赋值
				pst.setString(i+1, args[i]);
			}
			//获取结果
			rs = pst.executeQuery();
			//对结果进行处理

			//表里面的列对象
			ResultSetMetaData rsmd = rs.getMetaData();
			//获取列的列数
			int colunmCount = rsmd.getColumnCount();

			while(rs.next()){
				//如果能进入这个循环
				//派生除实体对象
				t = cla.newInstance();
				//拿所有的属性
				//通过反射拿对象的所有属性
				Field[] fs = cla.getDeclaredFields();
				//创建map结合
				HashMap<String,String> map = new HashMap<String,String>();
				/*
				 * map.put(key,value) key---属性名的全部小写  value对应的是真实的属性名
				 */
				//遍历所有的属性
				for (Field field : fs) {
					String key = field.getName().toLowerCase();
					String value = field.getName();
					map.put(key,value);
				}

				//遍历所有的列
				for(int i = 1;i<=colunmCount;i++){
					//获取两个东西  列名   列所对应的值
					String colunmName = rsmd.getColumnName(i);
					//拿值
					Object colunmValue = null;
					//获取类型
					int typeName = rsmd.getColumnType(i);
					System.out.println(typeName);
					if(typeName == 2){
						//number 类型
						BigDecimal tmpValue = (BigDecimal) rs.getObject(i);
						colunmValue = tmpValue.intValue();
					}else{
						colunmValue = rs.getObject(i);
					}
					System.out.println(typeName);
					//System.out.println(colunmValue);
					//拿真实的属性名
					String realName = map.get(colunmName.toLowerCase());
					//判断
					if(realName != null && realName != ""){
						//说明匹配成功
						//拿每一个属性对象   并给他赋值
						Field f = cla.getDeclaredField(realName);
						//打开访问权限 暴力反射
						f.setAccessible(true);
						//赋值
						f.set(t, colunmValue);
					}
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			free(conn, pst, rs);
		}

		return t;
	}

	//将查询到的数据封装到一个集合中
	/**
	 * 查询所有的数据封装到集合中
	 * @param sql
	 * @param cla
	 * @param args
	 * @return
	 */
	public static <T> List<T> selectToBeanList(String sql,Class<T> cla,String... args){
		//获取连接
		Connection conn = getConn();
		//预处理对象
		PreparedStatement pst = null;
		//结果集
		ResultSet rs = null;

		//声明一个集合来存放数据
		List<T> list = new ArrayList<T>();

		try {
			//加载sql
			pst = conn.prepareStatement(sql);
			//传递参数
			for(int i = 0;i<args.length;i++){
				//给参数赋值
				pst.setString(i+1, args[i]);
			}
			//获取结果
			rs = pst.executeQuery();
			//对结果进行处理

			//表里面的列对象
			ResultSetMetaData rsmd = rs.getMetaData();
			//获取列的列数
			int colunmCount = rsmd.getColumnCount();

			while(rs.next()){
				//声明实体
				T t = null;
				//如果能进入这个循环
				//派生除实体对象
				t = cla.newInstance();
				//拿所有的属性
				//通过反射拿对象的所有属性
				Field[] fs = cla.getDeclaredFields();
				//创建map结合
				HashMap<String,String> map = new HashMap<String,String>();
				/*
				 * map.put(key,value) key---属性名的全部小写  value对应的是真实的属性名
				 */
				//遍历所有的属性
				for (Field field : fs) {
					String key = field.getName().toLowerCase();
					String value = field.getName();
					map.put(key,value);
				}

				//遍历所有的列
				for(int i = 1;i<=colunmCount;i++){
					//获取两个东西  列名   列所对应的值
					String colunmName = rsmd.getColumnName(i);
					//拿值
					Object colunmValue = null;
					//获取类型
					int typeName = rsmd.getColumnType(i);
					System.out.println(typeName);
					if(typeName == 2){
						//number 类型
						BigDecimal tmpValue = (BigDecimal) rs.getObject(i);
						colunmValue = tmpValue.intValue();
					}else{
						colunmValue = rs.getObject(i);
					}
					System.out.println(typeName);
					//System.out.println(colunmValue);
					//拿真实的属性名
					String realName = map.get(colunmName.toLowerCase());
					//判断
					if(realName != null && realName != ""){
						//说明匹配成功
						//拿每一个属性对象   并给他赋值
						Field f = cla.getDeclaredField(realName);
						//打开访问权限 暴力反射
						f.setAccessible(true);
						//赋值
						f.set(t, colunmValue);
					}
				}
				list.add(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			free(conn, pst, rs);
		}

		return list;
	}

	//增删改
	/**
	 * 新增的方法
	 * @param sql
	 * @param args
	 * @return
	 */
	public static int insert(String sql,String... args){
		//获取连接
		Connection conn = getConn();
		//获取预处理对象
		PreparedStatement pst = null;
		//声明一个参数记载执行的数量
		int num = 0;
		//加载sql
		try {
			pst = conn.prepareStatement(sql);
			//传参
			for(int i = 0;i<args.length;i++){
				pst.setString(i+1, args[i]);
			}
			//执行sql
			num = pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return num;
	}

	//修改
	public static int update(String sql,String... args){
		return insert(sql, args);
	}

	//删除
	public static int delete(String sql,String... args){
		return insert(sql, args);
	}

	/**
	 * 释放两个资源的
	 * @param conn 连接对象
	 * @param pst	预处理对象
	 */
	public static void free(Connection conn,PreparedStatement pst){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(pst != null){
			try {
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 三个资源的释放  方法重载
	 * @param conn
	 * @param pst
	 * @param rs
	 */
	public static void free(Connection conn,PreparedStatement pst,ResultSet rs){
		free(conn, pst);
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取数据库连接的方法
	 * @return
	 */
	public static Connection getConn(){
		//先拿properties
		pop = Config.getPop();
		//声明Connection
		Connection conn = null;
		try {
			//记载驱动
			Class.forName(pop.getProperty("driver"));
			//获取连接
			conn = DriverManager.getConnection(pop.getProperty("url"),pop.getProperty("name"),pop.getProperty("pass"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
}
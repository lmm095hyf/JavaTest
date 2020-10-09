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
 * �������ݿ�������һ��������
 * 1.���ݿ������
 * 2.�ر���Դ
 * 3.��ѯ�ķ�װ
 * 4.��ɾ�ĵķ�װ
 * @author fangjun
 *
 */
public class DButil {

	//��Ҫ��properties���� ����properties����
	private static Properties pop;

	/**
	 * ��ɾ�Ĳ�
	 * ��ɾ��----��û�н����   ��Ӧ��sql��ͬ
	 * �� ------ �н��  ��Ҫȥ��װ
	 * 1.select count(*) from studentmsg where sid =?;
	 */

	public static int selectCount(String sql,String... args){
		//��������
		Connection conn = null;
		//��ȡ����
		conn = getConn();
		//����sql
		PreparedStatement pst = null;
		//ʹ��һ���������
		int countNum = 0;
		//���������
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			//��sql���ݲ���
			for(int i = 0;i<args.length;i++){
				pst.setString(i+1, args[i]);
			}
			//�ý��
			rs = pst.executeQuery();
			while(rs.next()){
				//��Ϊ��
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
		//��ȡ����
		Connection conn = getConn();
		//Ԥ�������
		PreparedStatement pst = null;
		//�����
		ResultSet rs = null;

		//����ʵ��
		T t = null;
		try {
			//����sql
			pst = conn.prepareStatement(sql);
			//���ݲ���
			for(int i = 0;i<args.length;i++){
				//��������ֵ
				pst.setString(i+1, args[i]);
			}
			//��ȡ���
			rs = pst.executeQuery();
			//�Խ�����д���

			//��������ж���
			ResultSetMetaData rsmd = rs.getMetaData();
			//��ȡ�е�����
			int colunmCount = rsmd.getColumnCount();

			while(rs.next()){
				//����ܽ������ѭ��
				//������ʵ�����
				t = cla.newInstance();
				//�����е�����
				//ͨ�������ö������������
				Field[] fs = cla.getDeclaredFields();
				//����map���
				HashMap<String,String> map = new HashMap<String,String>();
				/*
				 * map.put(key,value) key---��������ȫ��Сд  value��Ӧ������ʵ��������
				 */
				//�������е�����
				for (Field field : fs) {
					String key = field.getName().toLowerCase();
					String value = field.getName();
					map.put(key,value);
				}

				//�������е���
				for(int i = 1;i<=colunmCount;i++){
					//��ȡ��������  ����   ������Ӧ��ֵ
					String colunmName = rsmd.getColumnName(i);
					//��ֵ
					Object colunmValue = null;
					//��ȡ����
					int typeName = rsmd.getColumnType(i);
					System.out.println(typeName);
					if(typeName == 2){
						//number ����
						BigDecimal tmpValue = (BigDecimal) rs.getObject(i);
						colunmValue = tmpValue.intValue();
					}else{
						colunmValue = rs.getObject(i);
					}
					System.out.println(typeName);
					//System.out.println(colunmValue);
					//����ʵ��������
					String realName = map.get(colunmName.toLowerCase());
					//�ж�
					if(realName != null && realName != ""){
						//˵��ƥ��ɹ�
						//��ÿһ�����Զ���   ��������ֵ
						Field f = cla.getDeclaredField(realName);
						//�򿪷���Ȩ�� ��������
						f.setAccessible(true);
						//��ֵ
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

	//����ѯ�������ݷ�װ��һ��������
	/**
	 * ��ѯ���е����ݷ�װ��������
	 * @param sql
	 * @param cla
	 * @param args
	 * @return
	 */
	public static <T> List<T> selectToBeanList(String sql,Class<T> cla,String... args){
		//��ȡ����
		Connection conn = getConn();
		//Ԥ�������
		PreparedStatement pst = null;
		//�����
		ResultSet rs = null;

		//����һ���������������
		List<T> list = new ArrayList<T>();

		try {
			//����sql
			pst = conn.prepareStatement(sql);
			//���ݲ���
			for(int i = 0;i<args.length;i++){
				//��������ֵ
				pst.setString(i+1, args[i]);
			}
			//��ȡ���
			rs = pst.executeQuery();
			//�Խ�����д���

			//��������ж���
			ResultSetMetaData rsmd = rs.getMetaData();
			//��ȡ�е�����
			int colunmCount = rsmd.getColumnCount();

			while(rs.next()){
				//����ʵ��
				T t = null;
				//����ܽ������ѭ��
				//������ʵ�����
				t = cla.newInstance();
				//�����е�����
				//ͨ�������ö������������
				Field[] fs = cla.getDeclaredFields();
				//����map���
				HashMap<String,String> map = new HashMap<String,String>();
				/*
				 * map.put(key,value) key---��������ȫ��Сд  value��Ӧ������ʵ��������
				 */
				//�������е�����
				for (Field field : fs) {
					String key = field.getName().toLowerCase();
					String value = field.getName();
					map.put(key,value);
				}

				//�������е���
				for(int i = 1;i<=colunmCount;i++){
					//��ȡ��������  ����   ������Ӧ��ֵ
					String colunmName = rsmd.getColumnName(i);
					//��ֵ
					Object colunmValue = null;
					//��ȡ����
					int typeName = rsmd.getColumnType(i);
					System.out.println(typeName);
					if(typeName == 2){
						//number ����
						BigDecimal tmpValue = (BigDecimal) rs.getObject(i);
						colunmValue = tmpValue.intValue();
					}else{
						colunmValue = rs.getObject(i);
					}
					System.out.println(typeName);
					//System.out.println(colunmValue);
					//����ʵ��������
					String realName = map.get(colunmName.toLowerCase());
					//�ж�
					if(realName != null && realName != ""){
						//˵��ƥ��ɹ�
						//��ÿһ�����Զ���   ��������ֵ
						Field f = cla.getDeclaredField(realName);
						//�򿪷���Ȩ�� ��������
						f.setAccessible(true);
						//��ֵ
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

	//��ɾ��
	/**
	 * �����ķ���
	 * @param sql
	 * @param args
	 * @return
	 */
	public static int insert(String sql,String... args){
		//��ȡ����
		Connection conn = getConn();
		//��ȡԤ�������
		PreparedStatement pst = null;
		//����һ����������ִ�е�����
		int num = 0;
		//����sql
		try {
			pst = conn.prepareStatement(sql);
			//����
			for(int i = 0;i<args.length;i++){
				pst.setString(i+1, args[i]);
			}
			//ִ��sql
			num = pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return num;
	}

	//�޸�
	public static int update(String sql,String... args){
		return insert(sql, args);
	}

	//ɾ��
	public static int delete(String sql,String... args){
		return insert(sql, args);
	}

	/**
	 * �ͷ�������Դ��
	 * @param conn ���Ӷ���
	 * @param pst	Ԥ�������
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
	 * ������Դ���ͷ�  ��������
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
	 * ��ȡ���ݿ����ӵķ���
	 * @return
	 */
	public static Connection getConn(){
		//����properties
		pop = Config.getPop();
		//����Connection
		Connection conn = null;
		try {
			//��������
			Class.forName(pop.getProperty("driver"));
			//��ȡ����
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
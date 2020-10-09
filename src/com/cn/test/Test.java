package com.cn.test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import com.cn.config.Config;
import com.cn.javabean.Student;
import com.cn.util.DButil;


public class Test {
	@org.junit.Test
	public void test1(){
		//getConn();
		//getTest();
		test3("123","xiaoming");
	}

	public static Connection getConn(){
		/*
		 * driver = com.mysql.jdbc.Driver
			#url = jdbc:oracle:thin:@localhost:1521:XE
			url=jdbc:mysql://localhost:3306/mgr
			name = root
			password =root
		 */
		//ʹ��jdbc���������ݿ� driver url name password
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:orcl";
		String name = "scott";
		String pass = "tiger";

		//jdbcʹ��
		/*
		 * 1.��������
		 * 2.��ȡ����
		 * 3.����sql ---����
		 * 4.ִ��sql
		 * 5.��ȡ���
		 * 6.������
		 * 7.��װ���
		 * 8.�ر���Դ
		 */
		Connection conn = null;
		try {
			//��������
			Class.forName(driver);
			//��ȡ����
			conn = DriverManager.getConnection(url,name,pass);
			System.out.println(conn);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return conn;
	}

	public static void getTest(){
		//12��  ��ȡ����
		Connection conn = getConn();
		//sql ������ڣ���ʾռλ��
		String sql = "select * from studentmsg where sname = ? and ssex = ?";
		//Ԥ�������
		PreparedStatement pst = null;
		//���������
		ResultSet rs = null;
		try {
			//����sql
			pst = conn.prepareStatement(sql);
			//setString(index,value)   index��ָ��һ��λ�õĲ���   valueָ����ֵ
			pst.setString(1, "С��");
			pst.setString(2, "��");
			//pst.executeQuery() ��ȡ�����
			rs = pst.executeQuery();
			//������ rs.getInt(���±�/������)
			Student s = null;
			while(rs.next()){
				int sid = rs.getInt(1);
				String sname = rs.getString("sname");
				String spassword = rs.getString(3);
				String ssex = rs.getString(4);
				String saddress = rs.getString(5);
				s = new Student(sid,sname,spassword,ssex,saddress);
			}
			//��װ���

			System.out.println(s);

//			rs.close();
//			pst.close();
//			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//�ر���Դ
			if(rs != null){
				try {
					rs.close();
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
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@org.junit.Test
	public void test2(){
		Properties pop = Config.getPop();
		String name = (String)pop.get("name");
		System.out.println(name);
	}


	public void test3(String... args){
		for(int i = 0;i<args.length;i++){
			System.out.println(args[i]);
		}
	}

	@org.junit.Test
	public void test4(){
		String sql = "select * from studentmsg where ssex = ?";
		String str = "��";
		int num = DButil.selectCount(sql,str);
		System.out.println(num);
	}

	@org.junit.Test
	public void test5(){
		String sql = "select * from studentmsg where sid = 1";
		//String sid = "1";
		Student s = DButil.selectToBean(sql, Student.class);
		System.out.println(s);
	}

	@org.junit.Test
	public void test6(){
		String sql = "select * from studentmsg where ssex like ?";
		List<Student> list = DButil.selectToBeanList(sql, Student.class,"%��%");
		for (Student student : list) {
			System.out.println(student);
		}
	}

	@org.junit.Test
	public void test7(){
		String sql = "insert into studentmsg values (5,'С��','456789','��','������')";
		int num = DButil.insert(sql);
		System.out.println(num);
	}

}

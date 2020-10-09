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
		//使用jdbc来连接数据库 driver url name password
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@localhost:1521:orcl";
		String name = "scott";
		String pass = "tiger";

		//jdbc使用
		/*
		 * 1.加载驱动
		 * 2.获取连接
		 * 3.加载sql ---传参
		 * 4.执行sql
		 * 5.获取结果
		 * 6.处理结果
		 * 7.封装结果
		 * 8.关闭资源
		 */
		Connection conn = null;
		try {
			//加载驱动
			Class.forName(driver);
			//获取连接
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
		//12步  获取连接
		Connection conn = getConn();
		//sql 里面存在？表示占位符
		String sql = "select * from studentmsg where sname = ? and ssex = ?";
		//预处理对象
		PreparedStatement pst = null;
		//声明结果集
		ResultSet rs = null;
		try {
			//加载sql
			pst = conn.prepareStatement(sql);
			//setString(index,value)   index是指哪一个位置的参数   value指的是值
			pst.setString(1, "小明");
			pst.setString(2, "男");
			//pst.executeQuery() 获取到结果
			rs = pst.executeQuery();
			//处理结果 rs.getInt(传下标/传列明)
			Student s = null;
			while(rs.next()){
				int sid = rs.getInt(1);
				String sname = rs.getString("sname");
				String spassword = rs.getString(3);
				String ssex = rs.getString(4);
				String saddress = rs.getString(5);
				s = new Student(sid,sname,spassword,ssex,saddress);
			}
			//封装结果

			System.out.println(s);

//			rs.close();
//			pst.close();
//			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//关闭资源
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
		String str = "男";
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
		List<Student> list = DButil.selectToBeanList(sql, Student.class,"%男%");
		for (Student student : list) {
			System.out.println(student);
		}
	}

	@org.junit.Test
	public void test7(){
		String sql = "insert into studentmsg values (5,'小黑','456789','男','哈尔滨')";
		int num = DButil.insert(sql);
		System.out.println(num);
	}

}

package com.cn.javabean;

public class Student {
	private int sid;
	private String sname;
	private String spassword;
	private String ssex;
	private String saddress;
	
	
	
	
	public Student() {
		super();
	}
	
	public Student(int sid, String sname, String spassword, String ssex, String saddress) {
		super();
		this.sid = sid;
		this.sname = sname;
		this.spassword = spassword;
		this.ssex = ssex;
		this.saddress = saddress;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public String getSpassword() {
		return spassword;
	}
	public void setSpassword(String spassword) {
		this.spassword = spassword;
	}
	public String getSsex() {
		return ssex;
	}
	public void setSsex(String ssex) {
		this.ssex = ssex;
	}
	public String getSaddress() {
		return saddress;
	}
	public void setSaddress(String saddress) {
		this.saddress = saddress;
	}

	@Override
	public String toString() {
		return "student [sid=" + sid + ", sname=" + sname + ", spassword=" + spassword + ", ssex=" + ssex
				+ ", saddress=" + saddress + "]";
	}
	
	
}

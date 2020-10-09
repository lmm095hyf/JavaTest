package com.cn.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static Properties pop;
	
	static{
		pop=new Properties();
		try {
			FileInputStream fis=new FileInputStream("db.properties");
			pop.load(fis);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Properties getPop(){
		return pop;
	}
	
	
}

package com.metyouat.playground;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	private Connection c;

	public Database(String url) throws SQLException{
		c = DriverManager.getConnection(url);
	}
	
	public void met(long meeter, Long... mets) throws SQLException{
		CallableStatement s = c.prepareCall("{ call met( ?, ? ) }");
		try{
			s.setLong(1, meeter);
			s.setArray(2, c.createArrayOf("long", mets));
			s.execute();
		}finally{
			s.close();
		}
	}
}

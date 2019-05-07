package com.kitri.guestbook;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.PreparedStatement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/gblist")
public class GuestBookList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		super.init(config);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("로그인하러 왔다......");

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

//		1. data get (id, pass)
		request.setCharacterEncoding("UTF-8");


		String name = request.getParameter("name");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		

//		2. logic 1의 data를 기준으로 select 이름
//		select name 
//		from member 
//		where id = ? and pass = ?; 단 하나의 값만 찾으면 된다.
//		rs->이름
		

		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.14.52:1521:orcl", "kitri", "kitri");
			StringBuffer sql = new StringBuffer();
			sql.append("select seq, name, subject, content, logtime  \n");
			sql.append("from guestbook \n");

			pstmt = conn.prepareStatement(sql.toString());
			
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				name = rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

//		3. 2번의 결과에 따라
//			name == null : 아이디 또는 비밀번호를 확인하세요. 등록되지 않은 아이디거나, 아이디 또는 비밀번호를 잘못 입력하셨습니다. >>> 로그인(/user/login.html)
//			name != null : 홍길동님 안녕하세요.	
//	

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("	<body>");
		if (name != null) {
			out.println("<strong>" + name + " </strong> 님 안녕하세요. <br>");
		} else {
			out.println("<font size = \"20\" color = \"red\">");
			out.println("아이디 또는 비밀번호를 확인하세요.<br>");
			out.println("등록되지 않은 아이디거나, 아이디 또는 비밀번호를 잘못 입력하셨습니다.");
			out.println("	</font>");
		}
		out.println("	</body>");
		out.println("</html>");
	}

}

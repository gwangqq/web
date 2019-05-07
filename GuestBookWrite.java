package com.kitri.guestbook;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/gbwrite")
public class GuestBookWrite extends HttpServlet {
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
		System.out.println("회원가입하러 왔다......");
//	1. data get(이름, 아이디, 비번, 이메일1, 이메일2, 전번1, 전번2, 전번3, 우편번호, 주소, 상세주소)
		Connection conn = null;
		PreparedStatement pstmt = null;

		request.setCharacterEncoding("UTF-8");
//		2. Logic : 1의 data를 insert

//		insert all - 한번에 여러개 넣을 때 사용
//		insert all 
//			into member (id, name, pass, email, emaildomain, joindate) 
//				values (?, ?, ?, ?, sysdate) 
//			into member_detail (id, zipcode, address, address_detail, tel1, tel2, tel3)
//				values (?,?,?,?,?,?,?)
//		select * from dual; --> 이걸 반드시 해야함!!!

		String name = request.getParameter("name");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		int cnt = 0;
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "kitri", "kitri");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into guestbook \n");
			sql.append("	values(GUESTBOOK_SEQ.nextval, ?, ?, ?, sysdate)");
			
			pstmt = conn.prepareStatement(sql.toString());
			
			int idx = 0;
			pstmt.setString(++idx, name);
			pstmt.setString(++idx, subject);
			pstmt.setString(++idx, content);
			
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (pstmt != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


//	3. response page : 2의 결과에 따라.
//	3-1. 실패시 :0, 성공시 :2(insert를 두번 하니깐)
//	!0: 홍길동님 회원가입을 환영합니다.
//	3-2. 0 : 서버 문제로 회원 가입이 실패하였습니다. 다음에 다시 시도하세요.
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("	<body>");
		if(cnt != 0) {
			out.println(name + "님 글작성이 완료되었습니다.");
			out.println("<a href = \"index.html\">돌아가기</a>");
		} else {
			out.println("<font color = \"red\" size = \"20\">");
			out.println("서버 문제로 글작성에 실패했습니다.");
			out.println("다음에 시도해주세요");
			out.println("</font>");
		}
		out.println("	</body>");
		out.println("</html>");
	}

}

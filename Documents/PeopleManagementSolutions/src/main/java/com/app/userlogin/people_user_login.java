package com.app.userlogin;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
@WebServlet(name="people_user_login", urlPatterns="/servlet/people_user_login")
public class people_user_login extends HttpServlet{
	String lDBUser  = "";
	String lDBPswd  = "";
	String lDBUrl   = "";
    /**Initialize global variables*/

        @SuppressWarnings("unused")
		@Override
	public void init(ServletConfig config) throws ServletException {
	    System.out.println("initializing controller servlet.");
		ServletContext context = config.getServletContext();
		lDBUser = "root";
		lDBPswd = "root";
		lDBUrl  = "jdbc:mysql://localhost:3306/management";
		super.init(config);
	}
        
	/**Process the HTTP Get request*/
        @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
		doPost(request, response);
	}
        
	/**Process the HTTP Post request*/
        @SuppressWarnings("unused")
		@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException{
	    response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		session.setAttribute("lErrorMsg",null);
		String target = "/people_user_login.jsp";
		String action = request.getParameter("action");
		String action_submit = request.getParameter("action_submit");
		String action_chngpswd = request.getParameter("action_chngpswd");
		System.out.println("action_submit=="+action_submit);
		if ( action_submit != null || action_chngpswd != null ){
			if ( request.getParameter("submit").equals("Submit") ){
				System.out.println("in the  Submit");
				if ( action_submit.equals("people_user_login_submit") ){
					System.out.println("in the people_user_login_submit ");
					action = "people_user_login_submit";
				}
				else
					if (action_submit.equals("login_pswd_change_submit")){
						action = "login_pswd_change_submit";
					}
			}
			else 
				if ( request.getParameter("submit").equals("Change Password") ){
					if ( action_chngpswd.equals("people_change_pswd_submit") )
					action = "people_change_pswd_submit";
				}  
		}
		if (action!=null) {
			System.out.println("in the  "+action);
			if (action.equals("people_user_login_submit")){
				String lUserId = "";
				String lUserName = "";
				String lUserPswd = "";
				lUserId = (String)request.getParameter("user_id");
				lUserName = (String)request.getParameter("user_name");
				lUserPswd = (String)request.getParameter("user_pswd");
				UserLoginDBObj userLoginDBObj = new UserLoginDBObj();
				UserLoginDBMethods userLoginDBMethods = new UserLoginDBMethods(lDBUser,lDBPswd,lDBUrl);
				userLoginDBObj = (UserLoginDBObj)userLoginDBMethods.getRecordByPrimaryKey(lUserId,lUserName,lUserPswd);
				if ( userLoginDBObj != null && ( userLoginDBObj.user_id != null && (userLoginDBObj.user_id).length() > 0) ){
					target = "/people_default.jsp";
				}
				else{
					String lErrorMsg = "User Does Not Exist!!"; 
					session.setAttribute("lErrorMsg",lErrorMsg);
					target = "/people_user_login.jsp";
				}	
			}
			else
				if (action.equals("people_change_pswd_submit")){
					target = "/people_user_login_pswd_change.jsp";
				}
				else
				if (action.equals("login_pswd_change_submit")){ 
					UserLoginDBObj popUserLoginDBObj = new UserLoginDBObj();
					UserLoginDBMethods userLoginDBMethods = new UserLoginDBMethods(lDBUser,lDBPswd,lDBUrl);
					String lUserId = "";
					String lUserName = "";
					String lCurPswd = "";
					String lNewPswd = "";
					String lRetypePswd = "";
					popUserLoginDBObj = (UserLoginDBObj)userLoginDBMethods.populateUserLoginDBObjFromReq(request);  
					lRetypePswd = (String)request.getParameter("retype_pswd");
					if ( (popUserLoginDBObj.new_pswd).equals(lRetypePswd) ){
						UserLoginDBObj userLoginDBObj = new UserLoginDBObj();
						userLoginDBObj = (UserLoginDBObj)userLoginDBMethods.getRecordByPrimaryKey(popUserLoginDBObj.user_id,popUserLoginDBObj.user_name,popUserLoginDBObj.old_pswd);
						if ( userLoginDBObj != null && ( userLoginDBObj.user_id != null && (userLoginDBObj.user_id).length() > 0) ){
							int rval = userLoginDBMethods.updateUserLoginByPrimaryKey(popUserLoginDBObj);
							if ( rval > 0 ){
								target = "/people_user_login.jsp";
							}
							else{
								target = "/people_user_login_pswd_change.jsp";
							}
						}
						else{
							String lErrorMsg = "User Does Not Exist!!"; 
							session.setAttribute("lErrorMsg",lErrorMsg);
							target = "/people_user_login_pswd_change.jsp";
						}
					} 
					else{
						String lErrorMsg = "Retype Correct Password!!"; 
						session.setAttribute("lErrorMsg",lErrorMsg);
						target = "/people_user_login_pswd_change.jsp";
					}
				}
			} // (action== null )
			/* forwarding the request/response to the targeted view */
			RequestDispatcher requestDispatcher = getServletContext().getRequestDispatcher(target);
			requestDispatcher.forward(request, response);
		} // doPost closed
		/* write the methods that are used in class  */
        
}// class closed

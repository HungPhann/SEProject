package Controller.myprofile;

import Helper.JWTHandler;
import Helper.Validation;
import Model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "change-pass-word")
public class ChangePassword extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String token = request.getParameter("token");
        String currentPassword = request.getParameter("current_password");
        String newPassword = request.getParameter("new_password");
        String confirmPassword = request.getParameter("confirm_password");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode1 = mapper.createObjectNode();                 //return data
        int userID = JWTHandler.verifyToken(token);

        if (userID < 0) {
            if(userID == -1 || userID == -2) {                              //verifying token fails
                objectNode1.put("verify_token", false);
            }
            else {                                                          //internal error from server
                objectNode1.put("verify_token", true);
                objectNode1.put("success", false);
            }
        }
        else {
            objectNode1.put("verify_token", true);
            objectNode1.put("success", true);
            ObjectNode result = Validation.changePasswordValidation(userID, currentPassword, newPassword, confirmPassword);
            if(!result.get("valid").asBoolean()){
                objectNode1.put("valid", false);
                objectNode1.put("error_message", result.get("error_message"));
            }
            else {
                if (!User.changePassword(userID, newPassword))
                    objectNode1.put("success", false);              //internal error from server
                else
                    objectNode1.put("valid", true);
            }
        }

        PrintWriter wr = response.getWriter();
        wr.write(objectNode1.toString());
        wr.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}

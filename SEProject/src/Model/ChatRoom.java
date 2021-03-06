package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class ChatRoom {
    public static ObjectNode getChatRoomList(int id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnJSON = mapper.createObjectNode();
        ArrayNode roomArrayJSON = mapper.createArrayNode();

        Connection conn = null;


        try {
            conn = DatabaseConnection.getConnection();
            Statement st1 = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            String sql = "select CR.roomID, unread_message, message, sending_time " +
                    "from chat_room as CR LEFT JOIN message ON CR.roomID = message.roomID " +
                    "where CR.userId = " + id +
                    " and sending_time >= ALL (SELECT sending_time from message as MS where CR.roomID = MS.roomID) " +
                    "ORDER BY sending_time DESC";
            ResultSet rs1 = st1.executeQuery(sql);
            Statement st2 = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            ResultSet rs2 = null;
            while (rs1.next()){
                ObjectNode roomNodeJSON = mapper.createObjectNode();
                ArrayNode userArrayJSON = mapper.createArrayNode();

                sql = "Select chat_room.userID, user_name from chat_room, user_info" +
                        " Where chat_room.userID != " + id +
                        " and chat_room.userID = user_info.userID " +
                        "and roomID = " +  rs1.getInt("roomID");
                rs2 = st2.executeQuery(sql);
                while(rs2.next()) {
                    ObjectNode userNodeJSON = mapper.createObjectNode();
                    userNodeJSON.put("userID", rs2.getInt("userID"));
                    userNodeJSON.put("user_name", rs2.getString("user_name"));
                    userArrayJSON.add(userNodeJSON);
                }

                roomNodeJSON.put("roomID", rs1.getInt("roomID"));
                roomNodeJSON.put("user_list", userArrayJSON);
                roomNodeJSON.put("last_message", rs1.getString("message"));
                roomNodeJSON.put("sending_time", rs1.getString("sending_time"));
                roomNodeJSON.put("unread_message", rs1.getInt("unread_message"));
                roomArrayJSON.add(roomNodeJSON);
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return null;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        returnJSON.put("room_list", roomArrayJSON);
        return returnJSON;
    }

    public static ObjectNode searchChatRoom(int id, String searchKey) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnJSON = mapper.createObjectNode();
        ArrayNode roomArrayJSON = mapper.createArrayNode();

        Connection conn = null;


        try {
            conn = DatabaseConnection.getConnection();
            Statement st1 = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            String sql = "select CR.roomID, unread_message, message, sending_time " +
                    "from chat_room as CR LEFT JOIN message ON CR.roomID = message.roomID " +
                    "where CR.userId = " + id +
                    " and exists (SELECT userID from user_info " +
                    "WHERE userID != "+ id +
                    " AND user_name LIKE \"%" + searchKey + "%\"" +
                    " AND userID IN (SELECT userID from chat_room as CR1 where CR1.roomID = CR.roomID)) " +
                    "and sending_time >= ALL (SELECT sending_time from message as MS where CR.roomID = MS.roomID)" +
                    "ORDER BY sending_time DESC";
            ResultSet rs1 = st1.executeQuery(sql);
            Statement st2 = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            ResultSet rs2 = null;
            while (rs1.next()){
                ObjectNode roomNodeJSON = mapper.createObjectNode();
                ArrayNode userArrayJSON = mapper.createArrayNode();

                sql = "Select chat_room.userID, user_name from chat_room, user_info" +
                        " Where chat_room.userID != " + id +
                        " and chat_room.userID = user_info.userID " +
                        "and roomID = " +  rs1.getInt("roomID");
                rs2 = st2.executeQuery(sql);
                while(rs2.next()) {
                    ObjectNode userNodeJSON = mapper.createObjectNode();
                    userNodeJSON.put("userID", rs2.getInt("userID"));
                    userNodeJSON.put("user_name", rs2.getString("user_name"));
                    userArrayJSON.add(userNodeJSON);
                }

                roomNodeJSON.put("roomID", rs1.getInt("roomID"));
                roomNodeJSON.put("user_list", userArrayJSON);
                roomNodeJSON.put("last_message", rs1.getString("message"));
                roomNodeJSON.put("sending_time", rs1.getString("sending_time"));
                roomNodeJSON.put("unread_message", rs1.getInt("unread_message"));
                roomArrayJSON.add(roomNodeJSON);
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return null;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        returnJSON.put("room_list", roomArrayJSON);
        return returnJSON;
    }

    public static ObjectNode loadMessage(int id, int roomID, int requestNumberOfMessages) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnJSON = mapper.createObjectNode();                 //return data
        ArrayNode messageListJSON = mapper.createArrayNode();
        ArrayNode userInfoListJSON = mapper.createArrayNode();
        int numberOfMessages = 0;


        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            //get message list
            String sql = "SELECT message, from_userID, sending_time from chat_room, message " +
                    "WHERE chat_room.roomID = ? " +
                    "AND chat_room.userID = ? " +
                    "AND chat_room.roomID = message.roomID ORDER BY sending_time " +
                    "LIMIT ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomID);
            pstmt.setInt(2, id);
            pstmt.setInt(3, requestNumberOfMessages);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                numberOfMessages++;
                Message message = new Message(rs.getString("message"), rs.getInt("from_userID"), rs.getTimestamp("sending_time"));
                messageListJSON.add(message.toJSON());
            }

            //get user info list
            sql = "SELECT chat_room.userId, user_name, profile_picture from chat_room, user_info " +
                    "WHERE chat_room.roomID = ? " +
                    "AND chat_room.userID = user_info.userID ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomID);
            rs = pstmt.executeQuery();
            while (rs.next()){
                ObjectNode userInfoJSON = mapper.createObjectNode();
                userInfoJSON.put("userID", rs.getInt("userID"));
                userInfoJSON.put("user_name", rs.getString("user_name"));
                userInfoJSON.put("profile_picture", rs.getString("profile_picture"));
                userInfoListJSON.add(userInfoJSON);
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return null;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return  null;
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try

        returnJSON.put("roomID", roomID);
        returnJSON.put("number_of_messages", numberOfMessages);
        returnJSON.put("userInfo_list", userInfoListJSON);
        returnJSON.put("message_list", messageListJSON);
        return returnJSON;
    }

    public static ArrayNode getChatRoomLIDist(int id){
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode roomArrayJSON = mapper.createArrayNode();

        Connection conn = null;


        try {
            conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            String sql = "select roomID " +
                    "from chat_room " +
                    "where userId = " + id;
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()){
                roomArrayJSON.add(rs.getInt("roomID"));
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return null;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return roomArrayJSON;
    }

    public static int createChatRoom(int id, int[] userIDList){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int roomID = -1;
        boolean flag = true;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            if(userIDList.length == 1) {
                String sql = "SELECT CR.roomID from chat_room AS CR WHERE CR.roomID IN " +
                                      "(SELECT CR1.roomID " +
                                      "FROM chat_room  as CR1, chat_room as CR2 " +
                                        "WHERE CR1.userID = " + id +
                                        " AND CR2.userID = " + userIDList[0] +
                                        " AND CR1.roomID = CR2.roomID) " +
                            "GROUP BY (roomID) HAVING count(roomID) = 2";
                rs = stmt.executeQuery(sql);
                if (rs.next()){
                    roomID =  rs.getInt("roomID");
                    flag = false;
                }
            }

            if(flag) {
                String sql = "SELECT roomID from chat_room  ORDER BY roomID DESC limit 1";
                rs = stmt.executeQuery(sql);
                if (rs.next())
                    roomID = rs.getInt("roomID") + 1;

                //prepare sql value
                String sqlTmpParam = "(" + roomID + "," + id + "),";
                for (int i = 0; i < userIDList.length; i++)
                    sqlTmpParam += "(" + roomID + "," + userIDList[i] + "),";
                String sqlParam = sqlTmpParam.substring(0, sqlTmpParam.length() - 1);         //remove last character ','

                sql = "INSERT INTO chat_room (roomID, userID) VALUES " + sqlParam;
                stmt.executeUpdate(sql);
            }
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return -1;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return -1;
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return roomID;
    }

    public static boolean addMessage(int roomID, Message message){
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE chat_room " +
                    "SET unread_message = (unread_message + 1) " +
                    "WHERE roomID = ? AND userID != ?" ;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomID);
            pstmt.setInt(2, message.getFromUserID());
            pstmt.executeUpdate();
            sql = "INSERT INTO message VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, roomID);
            pstmt.setString(2, message.getMessage());
            pstmt.setInt(3, message.getFromUserID());
            pstmt.setString(4, message.getSending_time().toString());
            pstmt.executeUpdate();
            conn.commit();
        } catch (SQLException se) {
            //Handle errors for JDBC
            try{
                conn.rollback();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return false;
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return true;
    }

    public static boolean seenMessage(int id, int roomID){
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            String sql  = "UPDATE chat_room SET unread_message = 0 " +
                    "WHERE roomID =" + roomID +
                    " AND userID = " + id;

            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            return false;
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            return false;
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return true;
    }


}

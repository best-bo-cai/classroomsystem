package com.room;

import com.dao.RoomDelete;
import com.dao.RoomInsert;
import com.dao.RoomSelect;
import com.model.Room;
import com.util.IsInteger;
import com.util.StringUtil;

//import javax.imageio.plugins.tiff.FaxTIFFTagSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@WebServlet("/apply")
public class UserApply extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset = utf-8");
        req.setCharacterEncoding("utf-8");

        String roomName = req.getParameter("roomName");

        if(StringUtil.isNotEmpty(roomName) && !new RoomSelect().selectRoomName(roomName).isEmpty()) {
            String year = req.getParameter("year");
            String month = req.getParameter("month");
            String day = req.getParameter("day");
            String startHour = req.getParameter("startHour");
            String startMinute = req.getParameter("startMinute");
            String endHour = req.getParameter("endHour");
            String endMinute = req.getParameter("endMinute");
            String message = req.getParameter("message");

            if(IsInteger.isInteger(year) && IsInteger.isInteger(month) && IsInteger.isInteger(day) && IsInteger.isInteger(startHour) &&
                    IsInteger.isInteger(startMinute) && IsInteger.isInteger(endHour) && IsInteger.isInteger(endMinute) && new StringUtil().isNotEmpty(message)) {
                String startTime = year+"-"+month+"-"+day+"-"+startHour+"-"+startMinute;
                String endTime = year+"-"+month+"-"+day+"-"+endHour+"-"+endMinute;

                Calendar startCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                try {
                    startCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd-HH-mm").parse(startTime));
                    endCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd-HH-mm").parse(endTime));
                    List<Room> list = new RoomSelect().selectRoomName(roomName);
                    if(startCalendar.getTimeInMillis() >= endCalendar.getTimeInMillis())  {
                        resp.sendRedirect(""+req.getContextPath()+"/errorPage.jsp");
                    } else {
                        boolean flag = true;
                        for (int i = 0; i < list.size(); i ++) {
                            if(list.get(i).getRoomStart() != 0) {
                                if(startCalendar.getTimeInMillis() <= list.get(i).getRoomEnd() &&
                                        endCalendar.getTimeInMillis() >= list.get(i).getRoomStart()) {
                                    flag = false;
                                    resp.sendRedirect(""+req.getContextPath()+"/errorPage.jsp");
                                    break;
                                }
                            }
                        }
                        if(flag) {
                            new RoomDelete().deleteNull(roomName);
                            new RoomInsert().insert(roomName,message,startCalendar.getTimeInMillis(),endCalendar.getTimeInMillis());
                            resp.sendRedirect(""+req.getContextPath()+"/successPage.jsp");
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                resp.sendRedirect(""+req.getContextPath()+"/errorPage.jsp");
            }

        } else {
            resp.sendRedirect(""+req.getContextPath()+"/errorPage.jsp");
        }
    }
}

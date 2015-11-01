/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.domainlib;

import com.google.gson.Gson;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * new
 * @author hexvaara
 */
public class DateTimeToken
{
    String year, month, day, hour, minute, second, token;
    
    public DateTimeToken()
    {
        
    }
    
    public DateTimeToken
        (
            String year,
            String month,
            String day,
            String hour,
            String minute,
            String second,
            String token
        )
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.token = token;
    }
        
    public String year() { return year; }
    public String month() { return month; }
    public String day() { return day; }
    public String hour() { return hour; }
    public String minute() { return minute; }
    public String second() { return second; }
    public String token() { return token; }
    
    
    public boolean isExpired()
    {
        Date now = new Date();
        Date auth = new Date();
        
        now.setYear(now.getYear());
        now.setMonth(now.getMonth());
        now.setDate(now.getDate());
        now.setHours(now.getHours());
        now.setMinutes(now.getMinutes());
        now.setSeconds(now.getSeconds());
        
        auth.setYear(Integer.parseInt(year)-1900);
        auth.setMonth(Integer.parseInt(month)-1);
        auth.setDate(Integer.parseInt(day));
        auth.setHours(Integer.parseInt(hour));
        auth.setMinutes(Integer.parseInt(minute));
        auth.setSeconds(Integer.parseInt(second));
        
        System.out.println("now: "+now.toString());
        System.out.println("auth: "+auth.toString());
        System.out.println("auth.after(now): "+auth.after(now));
        
        return auth.before(now);
    }
    
    @Override
    public String toString()
    {
        Gson gson = new Gson();
        
        String asJson = gson.toJson(this);
        
        return asJson;
    }
    
    public static DateTimeToken generate(int plusSeconds)
    {
        
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, plusSeconds);
        
        DateFormat year = new SimpleDateFormat("yyyy");
        DateFormat month = new SimpleDateFormat("MM");
        DateFormat day = new SimpleDateFormat("dd");
        DateFormat hour = new SimpleDateFormat("HH");
        DateFormat minute = new SimpleDateFormat("mm");
        DateFormat second = new SimpleDateFormat("ss");
        
        String yyyy = year.format(cal.getTime());
        String MM = month.format(cal.getTime());
        String dd = day.format(cal.getTime());
        String hh = hour.format(cal.getTime());
        String mm = minute.format(cal.getTime());
        String ss = second.format(cal.getTime());
        
        System.out.println(yyyy+" "+MM+" "+dd+" "+hh+" "+mm+" "+ss);
        
        return new DateTimeToken(yyyy, MM, dd, hh, mm, ss, UUID.randomUUID().toString());
    }
}

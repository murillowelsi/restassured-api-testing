package br.murillowelsi.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {

    public static String getFutureDate(Integer qtDays) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, qtDays);
        //format date to string
        return getFormatDate(cal.getTime());
    }

    public static String getFormatDate(Date date) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }

}

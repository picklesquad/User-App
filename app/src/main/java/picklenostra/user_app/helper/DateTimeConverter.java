package picklenostra.user_app.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Syukri Mullia Adil P on 5/14/2016.
 */
public class DateTimeConverter {

    public static long dateTimeToMillis(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = sdf.parse(time);
        return date.getTime();
    }

    public static String[] generateTanggalWaktu(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(date).split(" ");
    }
}

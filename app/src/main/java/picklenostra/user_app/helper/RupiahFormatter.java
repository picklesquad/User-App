package picklenostra.user_app.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by Syukri Mullia Adil P on 5/16/2016.
 */
public class RupiahFormatter {

    public static String format(int harga){
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String hsl = "Rp " + df.format(harga);
        return hsl;
    }

    public static String format(double harga){
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String hsl = "Rp " + df.format(harga);
        return hsl;
    }
}

package picklenostra.user_app.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by Syukri Mullia Adil P on 5/16/2016.
 */
public class PickleFormatter {

    public static String formatHarga(int harga){
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String hsl = "Rp " + df.format(harga);
        return hsl;
    }

    public static String formatHarga(double harga){
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String hsl = "Rp " + df.format(harga);
        return hsl;
    }

    public static String formatHarga(String harga){
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String hsl = "Rp " + df.format(Double.parseDouble(harga));
        return hsl;
    }

    public static String formatTextLength(String text, int maxLength) {
        if (text.length() > maxLength) text = text.substring(0, maxLength - 2) + "...";
        return text;
    }
}

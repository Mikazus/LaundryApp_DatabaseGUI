package util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LaundryHelper {

    private LaundryHelper() {}

    public static double getMinimumBerat(String namaLayanan) {
        if (namaLayanan.contains("Jam")) return 2.0;
        return 4.0;
    }

    public static Date calculateTanggalAmbil(Date tanggalTerima, String namaLayanan) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(tanggalTerima);

        if (namaLayanan.contains("1 Hari")) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } else if (namaLayanan.contains("2 Hari")) {
            cal.add(Calendar.DAY_OF_MONTH, 2);
        } else if (namaLayanan.contains("3 Hari")) {
            cal.add(Calendar.DAY_OF_MONTH, 3);
        } else if (namaLayanan.contains("3 Jam")) {
            cal.add(Calendar.HOUR_OF_DAY, 3);
        } else if (namaLayanan.contains("6 Jam")) {
            cal.add(Calendar.HOUR_OF_DAY, 6);
        }

        return cal.getTime();
    }

    public static String generateKodeStruk() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        int rand = (int)(Math.random() * 900) + 100;
        return "STR" + sdf.format(new Date()) + rand;
    }

    public static String formatRupiah(int amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp " + nf.format(amount);
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
}
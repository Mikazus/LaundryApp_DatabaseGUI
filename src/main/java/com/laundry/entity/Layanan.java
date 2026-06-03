package entity;

import java.text.NumberFormat;
import java.util.Locale;

public class Layanan {
    private String namaLayanan;
    private int hargaPerKg;

    public Layanan() {
    }

    public Layanan(String namaLayanan, int hargaPerKg) {
        this.namaLayanan = namaLayanan;
        this.hargaPerKg = hargaPerKg;
    }

    public String getNamaLayanan() {
        return namaLayanan;
    }

    public void setNamaLayanan(String n) {
        this.namaLayanan = n;
    }

    public int getHargaPerKg() {
        return hargaPerKg;
    }

    public void setHargaPerKg(int h) {
        this.hargaPerKg = h;
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return namaLayanan + " (Rp " + nf.format(hargaPerKg) + "/Kg)";
    }
}
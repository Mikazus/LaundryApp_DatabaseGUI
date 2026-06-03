package entity;

public class DetailTransaksi {
    private String kodeStruk;
    private String namaLayanan;
    private double berat;

    public DetailTransaksi() {
    }

    public DetailTransaksi(String kodeStruk, String namaLayanan, double berat) {
        this.kodeStruk = kodeStruk;
        this.namaLayanan = namaLayanan;
        this.berat = berat;
    }

    public String getKodeStruk() {
        return kodeStruk;
    }

    public void setKodeStruk(String k) {
        this.kodeStruk = k;
    }

    public String getNamaLayanan() {
        return namaLayanan;
    }

    public void setNamaLayanan(String n) {
        this.namaLayanan = n;
    }

    public double getBerat() {
        return berat;
    }

    public void setBerat(double b) {
        this.berat = b;
    }
}
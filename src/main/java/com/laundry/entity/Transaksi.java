package entity;

import java.util.Date;

public class Transaksi {
    private String kodeStruk;
    private Date tanggalTerima;
    private Date tanggalAmbil;
    private int customerId;
    private int total;
    private String pembayaran;
    private String statusBayar;
    private String namaKasir;

    public Transaksi() {
    }

    public Transaksi(String kodeStruk, Date tanggalTerima, Date tanggalAmbil, int customerId, int total, String pembayaran, String statusBayar, String namaKasir) {
        this.kodeStruk = kodeStruk;
        this.tanggalTerima = tanggalTerima;
        this.tanggalAmbil = tanggalAmbil;
        this.customerId = customerId;
        this.total = total;
        this.pembayaran = pembayaran;
        this.statusBayar = statusBayar;
        this.namaKasir = namaKasir;
    }

    public String getKodeStruk() {
        return kodeStruk;
    }

    public void setKodeStruk(String k) {
        this.kodeStruk = k;
    }

    public Date getTanggalTerima() {
        return tanggalTerima;
    }

    public void setTanggalTerima(Date d) {
        this.tanggalTerima = d;
    }

    public Date getTanggalAmbil() {
        return tanggalAmbil;
    }

    public void setTanggalAmbil(Date d) {
        this.tanggalAmbil = d;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int id) {
        this.customerId = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int t) {
        this.total = t;
    }

    public String getPembayaran() {
        return pembayaran;
    }

    public void setPembayaran(String p) {
        this.pembayaran = p;
    }

    public String getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(String s) {
        this.statusBayar = s;
    }

    public String getNamaKasir() {
        return namaKasir;
    }

    public void setNamaKasir(String n) {
        this.namaKasir = n;
    }
}
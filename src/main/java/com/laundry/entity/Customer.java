package entity;

public class Customer {
    private int customerId;
    private String namaCustomer;
    private String alamat;

    public Customer() {
    }

    public Customer(String namaCustomer, String alamat) {
        this.namaCustomer = namaCustomer;
        this.alamat = alamat;
    }

    public Customer(int customerId, String namaCustomer, String alamat) {
        this.customerId = customerId;
        this.namaCustomer = namaCustomer;
        this.alamat = alamat;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int id) {
        this.customerId = id;
    }

    public String getNamaCustomer() {
        return namaCustomer;
    }

    public void setNamaCustomer(String n) {
        this.namaCustomer = n;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String a) {
        this.alamat = a;
    }

    @Override
    public String toString() {
        return namaCustomer;
    }
}
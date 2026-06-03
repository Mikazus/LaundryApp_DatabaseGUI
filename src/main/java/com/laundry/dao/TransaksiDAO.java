package dao;

import config.DBConnection;
import entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public void insert(Customer customer, Transaksi transaksi, DetailTransaksi detail)
            throws SQLException {

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            int customerId;
            String sqlCust = "INSERT INTO Customer (namaCustomer, alamat) VALUES (?, ?)";
            try (PreparedStatement ps =
                         conn.prepareStatement(sqlCust, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customer.getNamaCustomer());
                ps.setString(2, customer.getAlamat());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Failed to retrieve generated customerId.");
                    customerId = rs.getInt(1);
                }
            }

            String sqlTrx =
                "INSERT INTO Transaksi " +
                "(kodeStruk, tanggalTerima, tanggalAmbil, customerId, total, pembayaran, statusBayar, namaKasir) " +
                "VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx)) {
                ps.setString(1, transaksi.getKodeStruk());
                ps.setTimestamp(2, new Timestamp(transaksi.getTanggalTerima().getTime()));
                ps.setTimestamp(3, new Timestamp(transaksi.getTanggalAmbil().getTime()));
                ps.setInt(4, customerId);
                ps.setInt(5, transaksi.getTotal());
                ps.setString(6, transaksi.getPembayaran());
                ps.setString(7, transaksi.getStatusBayar());
                ps.setString(8, transaksi.getNamaKasir());
                ps.executeUpdate();
            }

            String sqlDet =
                "INSERT INTO detailTransaksi (kodeStruk, namaLayanan, berat) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDet)) {
                ps.setString(1, transaksi.getKodeStruk());
                ps.setString(2, detail.getNamaLayanan());
                ps.setDouble(3, detail.getBerat());
                ps.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public List<Object[]> getAll() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT t.kodeStruk, c.namaCustomer, dt.namaLayanan, dt.berat, " +
            "       t.total, t.statusBayar, t.pembayaran, t.namaKasir, " +
            "       t.tanggalTerima, t.tanggalAmbil, c.customerId, c.alamat " +
            "FROM Transaksi t " +
            "JOIN Customer c        ON t.customerId  = c.customerId " +
            "JOIN detailTransaksi dt ON t.kodeStruk  = dt.kodeStruk " +
            "ORDER BY t.tanggalTerima DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("kodeStruk"),
                    rs.getString("namaCustomer"),
                    rs.getString("namaLayanan"),
                    rs.getDouble("berat"),
                    rs.getInt("total"),
                    rs.getString("statusBayar"),
                    rs.getString("pembayaran"),
                    rs.getString("namaKasir"),
                    rs.getTimestamp("tanggalTerima"),
                    rs.getTimestamp("tanggalAmbil"),
                    rs.getInt("customerId"),
                    rs.getString("alamat")
                });
            }
        }
        return list;
    }

    public void update(String kodeStruk, int customerId,
                       String namaCustomer, String alamat,
                       String statusBayar, String pembayaran, String namaKasir)
            throws SQLException {

        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String sqlCust = "UPDATE Customer SET namaCustomer = ?, alamat = ? WHERE customerId = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCust)) {
                ps.setString(1, namaCustomer);
                ps.setString(2, alamat);
                ps.setInt(3, customerId);
                ps.executeUpdate();
            }

            String sqlTrx =
                "UPDATE Transaksi SET statusBayar = ?, pembayaran = ?, namaKasir = ? " +
                "WHERE kodeStruk = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx)) {
                ps.setString(1, statusBayar);
                ps.setString(2, pembayaran);
                ps.setString(3, namaKasir);
                ps.setString(4, kodeStruk);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public void delete(String kodeStruk) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            String sqlDet = "DELETE FROM detailTransaksi WHERE kodeStruk = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDet)) {
                ps.setString(1, kodeStruk);
                ps.executeUpdate();
            }

            String sqlTrx = "DELETE FROM Transaksi WHERE kodeStruk = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx)) {
                ps.setString(1, kodeStruk);
                ps.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}

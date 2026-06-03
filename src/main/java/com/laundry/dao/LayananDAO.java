package dao;

import config.DBConnection;
import entity.Layanan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LayananDAO {

    public List<Layanan> getAll() throws SQLException {
        List<Layanan> list = new ArrayList<>();
        String sql = "SELECT namaLayanan, hargaPerKg FROM Layanan ORDER BY namaLayanan";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Layanan(
                    rs.getString("namaLayanan"),
                    rs.getInt("hargaPerKg")
                ));
            }
        }
        return list;
    }
}

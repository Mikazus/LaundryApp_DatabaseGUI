package ui;

import dao.LayananDAO;
import dao.TransaksiDAO;
import entity.*;
import util.LaundryHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AddTransactionPanel extends JPanel {

    private JTextField txtNama, txtAlamat, txtBerat;
    private JComboBox<Layanan> cmbLayanan;
    private JLabel lblHarga, lblTglTerima, lblTglAmbil;

    private JTextField txtKasir, txtTotal;
    private JComboBox<String> cmbPembayaran, cmbStatusBayar;

    private final LayananDAO layananDAO = new LayananDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    public AddTransactionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        add(buildTitle("➕  TAMBAH TRANSAKSI BARU", new Color(30, 58, 138)), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 14, 0));
        center.add(buildLeftPanel());
        center.add(buildRightPanel());
        add(center, BorderLayout.CENTER);

        add(buildButtonRow(), BorderLayout.SOUTH);

        loadLayanan();
    }

    private JPanel buildLeftPanel() {
        JPanel p = titledPanel("Data Customer & Layanan", new Color(30, 58, 138));
        GridBagConstraints g = defaultGbc();

        txtNama = new JTextField();
        txtAlamat = new JTextField();
        cmbLayanan = new JComboBox<>();
        cmbLayanan.setMaximumRowCount(10);
        lblHarga = new JLabel("—");
        lblHarga.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHarga.setForeground(new Color(39, 174, 96));
        txtBerat = new JTextField();

        lblTglTerima = new JLabel(LaundryHelper.DATE_FORMAT.format(new Date()));
        lblTglTerima.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTglAmbil = new JLabel("—");
        lblTglAmbil.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        cmbLayanan.addActionListener(e -> onLayananChanged());

        int row = 0;
        addRow(p, g, row++, "Nama Customer *", txtNama);
        addRow(p, g, row++, "Alamat *", txtAlamat);
        addRow(p, g, row++, "Layanan *", cmbLayanan);
        addRow(p, g, row++, "Harga /Kg", lblHarga);
        addRow(p, g, row++, "Berat (Kg) *", txtBerat);
        addRow(p, g, row++, "Tgl Terima", lblTglTerima);
        addRow(p, g, row, "Est. Tgl Ambil", lblTglAmbil);

        g.gridx = 0;
        g.gridy = ++row;
        g.gridwidth = 2;
        JLabel hint = new JLabel("<html><i style='color:gray'>* 1/2/3 Hari: min 4 Kg &nbsp;&nbsp; * 3/6 Jam: min 2 Kg</i></html>");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        p.add(hint, g);

        return p;
    }

    private JPanel buildRightPanel() {
        JPanel p = titledPanel("Data Transaksi", new Color(30, 58, 138));
        GridBagConstraints g = defaultGbc();

        txtKasir = new JTextField();
        cmbPembayaran = new JComboBox<>(new String[]{"Tunai", "Transfer", "QRIS"});
        cmbStatusBayar = new JComboBox<>(new String[]{"Lunas", "Cicilan"});
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(255, 255, 200));
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);

        int row = 0;
        addRow(p, g, row++, "Nama Kasir *", txtKasir);
        addRow(p, g, row++, "Pembayaran *", cmbPembayaran);
        addRow(p, g, row++, "Status Bayar", cmbStatusBayar);
        addRow(p, g, row, "Total", txtTotal);

        return p;
    }

    private JPanel buildButtonRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));

        JButton btnHitung = styledButton("🔢  Hitung Total", new Color(52, 152, 219));
        JButton btnSimpan = styledButton("💾  Simpan Transaksi", new Color(39, 174, 96));
        JButton btnReset = styledButton("🔄  Reset Form", new Color(127, 140, 141));

        btnHitung.addActionListener(e -> hitungTotal());
        btnSimpan.addActionListener(e -> simpanTransaksi());
        btnReset.addActionListener(e -> resetForm());

        p.add(btnHitung);
        p.add(btnSimpan);
        p.add(btnReset);
        return p;
    }

    private void loadLayanan() {
        try {
            List<Layanan> items = layananDAO.getAll();
            cmbLayanan.removeAllItems();
            items.forEach(cmbLayanan::addItem);
        } catch (SQLException ex) {
            showError("Gagal memuat daftar layanan: " + ex.getMessage());
        }
    }

    private void onLayananChanged() {
        Layanan sel = (Layanan) cmbLayanan.getSelectedItem();
        if (sel == null) return;
        
        lblHarga.setText(LaundryHelper.formatRupiah(sel.getHargaPerKg()) + " / Kg");
        Date now = new Date();
        Date ambil = LaundryHelper.calculateTanggalAmbil(now, sel.getNamaLayanan());
        lblTglTerima.setText(LaundryHelper.DATE_FORMAT.format(now));
        lblTglAmbil.setText(LaundryHelper.DATE_FORMAT.format(ambil));
        txtTotal.setText("");
    }

    private void hitungTotal() {
        Layanan sel = (Layanan) cmbLayanan.getSelectedItem();
        if (sel == null) {
            showWarn("Pilih layanan terlebih dahulu!");
            return;
        }

        double berat;
        try {
            berat = Double.parseDouble(txtBerat.getText().trim());
        } catch (NumberFormatException ex) {
            showWarn("Berat harus berupa angka (contoh: 3.5)");
            return;
        }

        double minBerat = LaundryHelper.getMinimumBerat(sel.getNamaLayanan());
        if (berat < minBerat) {
            showWarn("Berat minimum untuk layanan ini adalah " + (int) minBerat + " Kg!\nBerat yang dimasukkan: " + berat + " Kg");
            return;
        }

        int total = (int) (sel.getHargaPerKg() * berat);
        txtTotal.setText(LaundryHelper.formatRupiah(total));
    }

    private void simpanTransaksi() {
        if (isEmpty(txtNama) || isEmpty(txtAlamat) || isEmpty(txtBerat) || isEmpty(txtKasir)) {
            showWarn("Semua field bertanda * wajib diisi.");
            return;
        }
        if (txtTotal.getText().isBlank()) {
            showWarn("Klik 'Hitung Total' sebelum menyimpan.");
            return;
        }

        Layanan sel = (Layanan) cmbLayanan.getSelectedItem();
        if (sel == null) {
            showWarn("Pilih layanan!");
            return;
        }

        double berat;
        try {
            berat = Double.parseDouble(txtBerat.getText().trim());
        } catch (NumberFormatException ex) {
            showWarn("Berat harus berupa angka.");
            return;
        }

        double minBerat = LaundryHelper.getMinimumBerat(sel.getNamaLayanan());
        if (berat < minBerat) {
            showWarn("Berat minimum untuk layanan ini adalah " + (int) minBerat + " Kg!");
            return;
        }

        Date now = new Date();
        Date tanggalAmbil = LaundryHelper.calculateTanggalAmbil(now, sel.getNamaLayanan());
        String kode = LaundryHelper.generateKodeStruk();
        int total = (int) (sel.getHargaPerKg() * berat);

        Customer customer = new Customer(txtNama.getText().trim(), txtAlamat.getText().trim());
        Transaksi transaksi = new Transaksi(
            kode, now, tanggalAmbil, 0, total,
            (String) cmbPembayaran.getSelectedItem(),
            (String) cmbStatusBayar.getSelectedItem(),
            txtKasir.getText().trim()
        );
        DetailTransaksi detail = new DetailTransaksi(kode, sel.getNamaLayanan(), berat);

        try {
            transaksiDAO.insert(customer, transaksi, detail);
            JOptionPane.showMessageDialog(this,
                "✅  Transaksi berhasil disimpan!\n\nKode Struk : " + kode +
                "\nCustomer   : " + customer.getNamaCustomer() +
                "\nLayanan    : " + sel.getNamaLayanan() +
                "\nTotal      : " + LaundryHelper.formatRupiah(total),
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } catch (SQLException ex) {
            showError("Gagal menyimpan transaksi:\n" + ex.getMessage());
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtBerat.setText("");
        txtKasir.setText("");
        txtTotal.setText("");
        lblTglAmbil.setText("—");
        lblHarga.setText("—");
        cmbPembayaran.setSelectedIndex(0);
        cmbStatusBayar.setSelectedIndex(0);
        if (cmbLayanan.getItemCount() > 0) {
            cmbLayanan.setSelectedIndex(0);
        }
        lblTglTerima.setText(LaundryHelper.DATE_FORMAT.format(new Date()));
    }

    private static JLabel buildTitle(String text, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private static JPanel titledPanel(String title, Color borderColor) {
        JPanel p = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            title,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), borderColor);
        p.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        return p;
    }

    private static GridBagConstraints defaultGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private static void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 1;
        g.weightx = 0;
        p.add(new JLabel(label + ":"), g);
        g.gridx = 1;
        g.weightx = 1.0;
        p.add(field, g);
    }

    private static JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private boolean isEmpty(JTextField f) {
        return f.getText().trim().isEmpty();
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
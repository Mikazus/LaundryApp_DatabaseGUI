package ui;

import dao.TransaksiDAO;
import util.LaundryHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateTransactionPanel extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private DefaultTableModel tableModel;

    private JTextField txtKodeStruk, txtNamaCustomer, txtAlamat, txtNamaKasir;
    private JComboBox<String> cmbPembayaran, cmbStatusBayar;

    private List<Object[]> cachedData = new ArrayList<>();
    private int selectedCustomerId = -1;

    private static final String[] TABLE_COLS = {
        "Kode Struk", "Nama Customer", "Layanan", "Total", "Status Bayar"
    };
    
    private static final int[] TABLE_WIDTHS = {
        160, 140, 170, 110, 95
    };

    public UpdateTransactionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel("✏️  EDIT TRANSAKSI", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(30, 58, 138));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildTablePanel(), buildFormPanel());
        split.setDividerLocation(220);
        split.setResizeWeight(0.4);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(titledBorder("Pilih transaksi yang ingin diedit (klik pada baris)", new Color(30, 58, 138)));

        tableModel = new DefaultTableModel(TABLE_COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.getTableHeader().setReorderingAllowed(false);
        
        // --- ADD THIS CUSTOM HEADER RENDERER ---
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(30, 58, 138));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        for (int i = 0; i < TABLE_WIDTHS.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(TABLE_WIDTHS[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected(table.getSelectedRow());
            }
        });

        JButton btnRefresh = styledButton("🔄  Refresh", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadData());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        topBar.add(btnRefresh);

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(topBar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(titledBorder("Form Edit Data", new Color(30, 58, 138)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        GridBagConstraints g = defaultGbc();

        txtKodeStruk = readOnlyField();
        txtNamaCustomer = new JTextField();
        txtAlamat = new JTextField();
        txtNamaKasir = new JTextField();
        cmbPembayaran = new JComboBox<>(new String[]{"Tunai", "Transfer", "QRIS"});
        cmbStatusBayar = new JComboBox<>(new String[]{"Lunas", "Cicilan"});

        int row = 0;
        addFormRow(form, g, row++, "Kode Struk", txtKodeStruk);
        addFormRow(form, g, row++, "Nama Customer", txtNamaCustomer);
        addFormRow(form, g, row++, "Alamat", txtAlamat);
        addFormRow(form, g, row++, "Nama Kasir", txtNamaKasir);
        addFormRow(form, g, row++, "Pembayaran", cmbPembayaran);
        addFormRow(form, g, row, "Status Bayar", cmbStatusBayar);

        JButton btnSave = styledButton("💾  Simpan Perubahan", new Color(39, 174, 96));
        btnSave.addActionListener(e -> saveChanges());

        JButton btnClear = styledButton("✖  Batal", new Color(127, 140, 141));
        btnClear.addActionListener(e -> clearForm());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        btnRow.add(btnSave);
        btnRow.add(btnClear);

        outer.add(form, BorderLayout.CENTER);
        outer.add(btnRow, BorderLayout.SOUTH);
        return outer;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        cachedData.clear();
        clearForm();

        try {
            cachedData = transaksiDAO.getAll();
            for (Object[] row : cachedData) {
                tableModel.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    LaundryHelper.formatRupiah((Integer) row[4]),
                    row[5]
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(int tableRow) {
        if (tableRow < 0 || tableRow >= cachedData.size()) return;
        Object[] row = cachedData.get(tableRow);

        txtKodeStruk.setText((String) row[0]);
        txtNamaCustomer.setText((String) row[1]);
        txtNamaKasir.setText((String) row[7]);
        txtAlamat.setText((String) row[11]);
        cmbPembayaran.setSelectedItem(row[6]);
        cmbStatusBayar.setSelectedItem(row[5]);
        selectedCustomerId = (Integer) row[10];
    }

    private void saveChanges() {
        if (txtKodeStruk.getText().isBlank() || selectedCustomerId < 0) {
            warn("Pilih transaksi dari tabel terlebih dahulu!");
            return;
        }
        
        if (txtNamaCustomer.getText().isBlank() || txtAlamat.getText().isBlank() || txtNamaKasir.getText().isBlank()) {
            warn("Nama Customer, Alamat, dan Nama Kasir tidak boleh kosong.");
            return;
        }

        try {
            transaksiDAO.update(
                txtKodeStruk.getText(),
                selectedCustomerId,
                txtNamaCustomer.getText().trim(),
                txtAlamat.getText().trim(),
                (String) cmbStatusBayar.getSelectedItem(),
                (String) cmbPembayaran.getSelectedItem(),
                txtNamaKasir.getText().trim()
            );
            JOptionPane.showMessageDialog(this,
                "✅  Data transaksi berhasil diperbarui!", "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memperbarui data:\n" + ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtKodeStruk.setText("");
        txtNamaCustomer.setText("");
        txtAlamat.setText("");
        txtNamaKasir.setText("");
        cmbPembayaran.setSelectedIndex(0);
        cmbStatusBayar.setSelectedIndex(0);
        selectedCustomerId = -1;
    }

    private static TitledBorder titledBorder(String title, Color color) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 1), title,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), color);
    }

    private static GridBagConstraints defaultGbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private static void addFormRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 1;
        g.weightx = 0;
        p.add(new JLabel(label + ":"), g);
        g.gridx = 1;
        g.weightx = 1.0;
        p.add(field, g);
    }

    private static JTextField readOnlyField() {
        JTextField f = new JTextField();
        f.setEditable(false);
        f.setBackground(new Color(235, 235, 235));
        return f;
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

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
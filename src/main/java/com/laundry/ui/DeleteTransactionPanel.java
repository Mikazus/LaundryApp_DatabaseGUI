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

public class DeleteTransactionPanel extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();

    private DefaultTableModel tableModel;

    private JTextField txKode, txCustomer, txLayanan, txBerat, txTotal, txStatus;

    private List<Object[]> cachedData = new ArrayList<>();
    private String selectedKode = null;

    private static final String[] TABLE_COLS = {
        "Kode Struk", "Nama Customer", "Layanan", "Berat (Kg)", "Total", "Status Bayar"
    };
    
    private static final int[] TABLE_WIDTHS = {
        160, 140, 170, 75, 110, 95
    };

    public DeleteTransactionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel("🗑️  HAPUS TRANSAKSI", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(185, 28, 28));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildTablePanel(), buildPreviewPanel());
        split.setDividerLocation(240);
        split.setResizeWeight(0.5);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBorder(titledBorder("Pilih transaksi yang ingin dihapus (klik pada baris)", new Color(185, 28, 28)));

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
        table.setSelectionBackground(new Color(255, 200, 200));
        table.getTableHeader().setReorderingAllowed(false);
        
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

    private JPanel buildPreviewPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(titledBorder("Detail Transaksi Terpilih", new Color(185, 28, 28)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        GridBagConstraints g = defaultGbc();

        txKode = roField(new Color(235, 235, 235));
        txCustomer = roField(new Color(235, 235, 235));
        txLayanan = roField(new Color(235, 235, 235));
        txBerat = roField(new Color(235, 235, 235));
        txTotal = roField(new Color(255, 255, 200));
        txStatus = roField(new Color(235, 235, 235));
        txTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));

        int row = 0;
        addFormRow(form, g, row++, "Kode Struk", txKode);
        addFormRow(form, g, row++, "Nama Customer", txCustomer);
        addFormRow(form, g, row++, "Layanan", txLayanan);
        addFormRow(form, g, row++, "Berat (Kg)", txBerat);
        addFormRow(form, g, row++, "Total", txTotal);
        addFormRow(form, g, row, "Status Bayar", txStatus);

        JLabel warn = new JLabel(
            "<html><b style='color:#b91c1c'>⚠ Perhatian:</b> " +
            "Data detail transaksi juga akan ikut dihapus. Tindakan ini tidak dapat dibatalkan.</html>");
        warn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JButton btnHapus = styledButton("🗑️  Hapus Transaksi Ini", new Color(185, 28, 28));
        btnHapus.addActionListener(e -> deleteTransaction());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        btnRow.add(btnHapus);

        JPanel south = new JPanel(new BorderLayout());
        south.add(warn, BorderLayout.NORTH);
        south.add(btnRow, BorderLayout.CENTER);

        outer.add(form, BorderLayout.CENTER);
        outer.add(south, BorderLayout.SOUTH);
        return outer;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        cachedData.clear();
        clearPreview();

        try {
            cachedData = transaksiDAO.getAll();
            for (Object[] row : cachedData) {
                tableModel.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    String.format("%.2f", (Double) row[3]),
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

        selectedKode = (String) row[0];
        txKode.setText(selectedKode);
        txCustomer.setText((String) row[1]);
        txLayanan.setText((String) row[2]);
        txBerat.setText(String.format("%.2f", (Double) row[3]) + " Kg");
        txTotal.setText(LaundryHelper.formatRupiah((Integer) row[4]));
        txStatus.setText((String) row[5]);
    }

    private void deleteTransaction() {
        if (selectedKode == null || selectedKode.isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Pilih transaksi dari tabel terlebih dahulu!", "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Anda yakin ingin menghapus transaksi berikut?<br><br>" +
            "<b>Kode  :</b> " + txKode.getText() + "<br>" +
            "<b>Customer :</b> " + txCustomer.getText() + "<br>" +
            "<b>Layanan  :</b> " + txLayanan.getText() + "<br>" +
            "<b>Total    :</b> " + txTotal.getText() + "<br><br>" +
            "<font color='red'>Tindakan ini tidak dapat dibatalkan!</font></html>",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            transaksiDAO.delete(selectedKode);
            JOptionPane.showMessageDialog(this,
                "✅  Transaksi " + selectedKode + " berhasil dihapus.", "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal menghapus transaksi:\n" + ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearPreview() {
        selectedKode = null;
        txKode.setText("");
        txCustomer.setText("");
        txLayanan.setText("");
        txBerat.setText("");
        txTotal.setText("");
        txStatus.setText("");
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

    private static JTextField roField(Color bg) {
        JTextField f = new JTextField();
        f.setEditable(false);
        f.setBackground(bg);
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
}
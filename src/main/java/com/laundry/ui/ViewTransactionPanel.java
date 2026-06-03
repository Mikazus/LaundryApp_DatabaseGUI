package ui;

import dao.TransaksiDAO;
import util.LaundryHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ViewTransactionPanel extends JPanel {

    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private DefaultTableModel tableModel;
    private JLabel lblRowCount;

    private static final String[] COLUMNS = {
        "Kode Struk", "Nama Customer", "Layanan",
        "Berat (Kg)", "Total", "Status Bayar",
        "Pembayaran", "Kasir", "Tgl Terima", "Tgl Ambil"
    };

    private static final int[] COL_WIDTHS = {
        155, 130, 165, 70, 105, 95, 80, 100, 120, 120
    };

    public ViewTransactionPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel title = new JLabel("📋  DAFTAR TRANSAKSI", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(30, 58, 138));

        JButton btnRefresh = styledButton("🔄  Refresh", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadData());

        p.add(title, BorderLayout.WEST);
        p.add(btnRefresh, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(173, 216, 230));

        table.getTableHeader().setReorderingAllowed(false);
        
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(30, 58, 138));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        for (int i = 0; i < COL_WIDTHS.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(COL_WIDTHS[i]);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBayarRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        return new JScrollPane(table);
    }

    private JPanel buildBottomBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblRowCount = new JLabel("Total: 0 transaksi");
        lblRowCount.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRowCount.setForeground(Color.GRAY);
        p.add(lblRowCount);
        return p;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = transaksiDAO.getAll();
            for (Object[] row : rows) {
                tableModel.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    String.format("%.2f", (Double) row[3]),
                    LaundryHelper.formatRupiah((Integer) row[4]),
                    row[5],
                    row[6],
                    row[7],
                    LaundryHelper.DATE_FORMAT.format(row[8]),
                    LaundryHelper.DATE_FORMAT.format(row[9])
                });
            }
            lblRowCount.setText("Total: " + rows.size() + " transaksi");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class StatusBayarRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));

            if (!isSelected) {
                String status = value == null ? "" : value.toString();
                if (status.equalsIgnoreCase("Lunas")) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(new Color(255, 229, 180));
                    setForeground(new Color(133, 77, 14));
                }
            }
            return this;
        }
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
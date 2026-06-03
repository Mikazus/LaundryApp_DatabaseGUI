package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final ViewTransactionPanel viewPanel;
    private final UpdateTransactionPanel updatePanel;
    private final DeleteTransactionPanel deletePanel;

    public MainFrame() {
        setTitle("PAIS LAUNDRY — Management System");
        setSize(960, 680);
        setMinimumSize(new Dimension(860, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);

        AddTransactionPanel addPanel = new AddTransactionPanel();
        viewPanel = new ViewTransactionPanel();
        updatePanel = new UpdateTransactionPanel();
        deletePanel = new DeleteTransactionPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("➕  Tambah Transaksi", addPanel);
        tabs.addTab("📋  Lihat Transaksi", viewPanel);
        tabs.addTab("✏️  Edit Transaksi", updatePanel);
        tabs.addTab("🗑️  Hapus Transaksi", deletePanel);

        tabs.addChangeListener(e -> {
            switch (tabs.getSelectedIndex()) {
                case 1 -> viewPanel.loadData();
                case 2 -> updatePanel.loadData();
                case 3 -> deletePanel.loadData();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        header.setBackground(new Color(30, 58, 138));

        JLabel icon = new JLabel("🧺");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel title = new JLabel("PAIS LAUNDRY  ·  Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        header.add(icon);
        header.add(title);
        return header;
    }
}
package gui.robotstate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class RobotStateFrame extends JInternalFrame {

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JButton addBotButton;
    private final JButton removeBotButton;

    public RobotStateFrame() {
        super("Состояние роботов", true, true, true, true);
        setLayout(new BorderLayout(0, 4));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "X", "Y", "Цель X", "Цель Y"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);

        addBotButton = new JButton("Добавить бота");
        removeBotButton = new JButton("Удалить выбранного");

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        toolbar.add(addBotButton);
        toolbar.add(removeBotButton);

        add(toolbar, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(500, 260));
        add(scroll, BorderLayout.CENTER);

        setMinimumSize(new Dimension(360, 200));
        pack();
    }

    public void wireActions(Runnable onAddBot, Runnable onRemoveSelected) {
        addBotButton.addActionListener(e -> onAddBot.run());
        removeBotButton.addActionListener(e -> onRemoveSelected.run());
    }

    public int getSelectedRobotId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return -1;
        }
        Object v = tableModel.getValueAt(row, 0);
        return v instanceof Integer id ? id : -1;
    }

    public void syncRows(int[][] rows) {
        int keepSelectionId = getSelectedRobotId();

        Set<Integer> want = new HashSet<>(rows.length * 2);
        for (int[] r : rows) {
            want.add(r[0]);
        }

        for (int mr = tableModel.getRowCount() - 1; mr >= 0; mr--) {
            Object v = tableModel.getValueAt(mr, 0);
            if (!(v instanceof Integer id) || !want.contains(id)) {
                tableModel.removeRow(mr);
            }
        }

        for (int[] row : rows) {
            int mr = findModelRowById(row[0]);
            if (mr >= 0) {
                for (int c = 1; c < 5; c++) {
                    if (!Objects.equals(tableModel.getValueAt(mr, c), row[c])) {
                        tableModel.setValueAt(row[c], mr, c);
                    }
                }
            } else {
                tableModel.addRow(new Object[]{row[0], row[1], row[2], row[3], row[4]});
            }
        }

        if (keepSelectionId >= 0) {
            int mr = findModelRowById(keepSelectionId);
            if (mr >= 0) {
                table.setRowSelectionInterval(mr, mr);
            }
        }
    }

    private int findModelRowById(int id) {
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            Object v = tableModel.getValueAt(r, 0);
            if (v instanceof Integer i && i == id) {
                return r;
            }
        }
        return -1;
    }
}

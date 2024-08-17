package so_proyecto_grupo4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmuladorAdministradorTareas extends JFrame {

    private static final String DB_URL = "jdbc:sqlite:C:\\Proyecto\\SO_Proyecto_Grupo4\\SO_Proyecto.db";
    private static final String QUERY_BASE = "SELECT nombre, prioridad, tiempoCPU FROM procesos";
    
    private JTable table;
    private JProgressBar globalCpuUsageBar;
    private JPanel processPanel;
    private List<Proceso> procesos = new ArrayList<>();
    private List<JProgressBar> processProgressBars = new ArrayList<>();
    private Timer timer;
    private JComboBox<String> sortComboBox;

    public EmuladorAdministradorTareas() {
        setTitle("Emulador del Administrador de Tareas");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadDataFromDatabase("prioridad"); // Cargar datos por defecto ordenados por prioridad
        startUpdatingProgressBars();
    }

    private void initComponents() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        table = new JTable(new DefaultTableModel(new Object[]{"Proceso"}, 0));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        String[] sortOptions = {"Prioridad", "Nombre"}; // Opciones de ordenación
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) sortComboBox.getSelectedItem();
                String sortBy = selectedOption.equals("Prioridad") ? "prioridad" : "nombre";
                loadDataFromDatabase(sortBy);
            }
        });
        controlPanel.add(new JLabel("Ordenar por:"));
        controlPanel.add(sortComboBox);

        JPanel progressPanel = new JPanel(new BorderLayout());
        globalCpuUsageBar = new JProgressBar(0, 100);
        globalCpuUsageBar.setStringPainted(true);
        progressPanel.add(new JLabel("Uso Total de CPU"), BorderLayout.NORTH);
        progressPanel.add(globalCpuUsageBar, BorderLayout.CENTER);

        processPanel = new JPanel();
        processPanel.setLayout(new GridLayout(0, 1));
        JScrollPane processScrollPane = new JScrollPane(processPanel);

        setLayout(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
        add(progressPanel, BorderLayout.SOUTH);
        add(processScrollPane, BorderLayout.EAST);
    }

    private void loadDataFromDatabase(String sortBy) {
        procesos.clear();
        processPanel.removeAll();

        String query = QUERY_BASE + " ORDER BY " + sortBy + " ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String nombreProceso = rs.getString("nombre");
                int prioridad = rs.getInt("prioridad");
                int tiempoCPU = rs.getInt("tiempoCPU");
                Proceso proceso = new Proceso(nombreProceso, tiempoCPU, prioridad);
                procesos.add(proceso);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateTable();
    }

    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Limpiar tabla existente

        processProgressBars.clear(); // Limpiar barras de progreso

        for (Proceso proceso : procesos) {
            Object[] row = new Object[]{proceso.getNombre()};
            model.addRow(row);
            addProcessCpuUsageBar(proceso);
        }

        updateGlobalCpuUsageBar();
    }

    private void addProcessCpuUsageBar(Proceso proceso) {
        JPanel panel = new JPanel(new BorderLayout());
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString(proceso.getNombre() + " (0%)");
        processProgressBars.add(progressBar);
        panel.add(new JLabel(proceso.getNombre()), BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);
        processPanel.add(panel);
    }

    private void startUpdatingProgressBars() {
        timer = new Timer(1000, e -> updateProgressBars());
        timer.start();
    }

    private void updateProgressBars() {
        for (int i = 0; i < processProgressBars.size(); i++) {
            JProgressBar progressBar = processProgressBars.get(i);
            Proceso proceso = procesos.get(i);
            int usagePercentage = calculateCpuUsagePercentage(proceso);
            progressBar.setValue(usagePercentage);
            progressBar.setString(proceso.getNombre() + " (" + usagePercentage + "%)");
        }

        updateGlobalCpuUsageBar();
    }

    private int calculateCpuUsagePercentage(Proceso proceso) {
        int maxTimeCpu = proceso.getTiempoCPU();
        int simulatedUsage = (int) (Math.random() * maxTimeCpu);
        return (int) ((simulatedUsage * 100.0) / maxTimeCpu);
    }

    private void updateGlobalCpuUsageBar() {
        int totalCpuUsage = processProgressBars.stream().mapToInt(JProgressBar::getValue).sum();
        globalCpuUsageBar.setValue(processProgressBars.size() > 0 ? totalCpuUsage / processProgressBars.size() : 0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmuladorAdministradorTareas().setVisible(true));
    }
}

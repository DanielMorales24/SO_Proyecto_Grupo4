/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so_proyecto_grupo4;

/**
 *
 * @author henri
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class EmuladorSorteo extends JFrame {
    private JTextArea procesosArea;
    private JButton sortearButton;
    private JLabel numeroSorteoLabel;
    private JTextArea ganadoresArea;
    private Map<String, Set<Integer>> procesosConBoletos;
    private Set<Integer> boletosDisponibles;
    private Random random;
    private static final int MAX_BOLETOS_POR_PROCESO = 10; // Máximo número de boletos por proceso

    public EmuladorSorteo() {
        setTitle("Emulador de Sorteo y Procesos");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializar el mapa de procesos y boletos
        procesosConBoletos = new HashMap<>();
        boletosDisponibles = new HashSet<>();
        random = new Random();

        // Inicializar los boletos disponibles del 1 al 100
        for (int i = 1; i <= 100; i++) {
            boletosDisponibles.add(i);
        }

        // Panel superior para mostrar los procesos y boletos
        procesosArea = new JTextArea(15, 50);
        procesosArea.setEditable(false);
        JScrollPane scrollPaneProcesos = new JScrollPane(procesosArea);
        add(scrollPaneProcesos, BorderLayout.NORTH);

        // Panel central para mostrar el sorteo
        JPanel sorteoPanel = new JPanel();
        sorteoPanel.setLayout(new BorderLayout());

        numeroSorteoLabel = new JLabel("Número sorteado: ");
        numeroSorteoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        sortearButton = new JButton("Sortear");
        ganadoresArea = new JTextArea(15, 50);
        ganadoresArea.setEditable(false);
        JScrollPane scrollPaneGanadores = new JScrollPane(ganadoresArea);

        sorteoPanel.add(numeroSorteoLabel, BorderLayout.NORTH);
        sorteoPanel.add(sortearButton, BorderLayout.CENTER);
        sorteoPanel.add(scrollPaneGanadores, BorderLayout.SOUTH);

        add(sorteoPanel, BorderLayout.CENTER);

        // Cargar los procesos desde la base de datos y mostrarlos
        cargarProcesosDesdeDB();

        // Configurar el botón de sorteo
        sortearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarSorteo();  // Realizar el sorteo cuando se presione el botón
            }
        });
    }

    private void cargarProcesosDesdeDB() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Conectar a la base de datos
            conn = DriverManager.getConnection("jdbc:sqlite:C:/proyecto/SO_Proyecto.db");
            stmt = conn.createStatement();
            String query = "SELECT nombre FROM procesos";
            rs = stmt.executeQuery(query);

            // Verificar si la consulta retorna resultados
            if (!rs.isBeforeFirst()) {
                procesosArea.setText("No se encontraron procesos en la base de datos.");
                return;  // No hay procesos, salir del método
            }

            // Construir el texto de los procesos y asignar boletos únicos
            StringBuilder procesosTexto = new StringBuilder();
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                Set<Integer> boletos = obtenerBoletosUnicos(); // Obtener boletos únicos
                procesosConBoletos.put(nombre, boletos);
                procesosTexto.append("Proceso: ").append(nombre).append(" - Boletos: ").append(boletos).append("\n");
            }

            procesosArea.setText(procesosTexto.toString());  // Mostrar los procesos y boletos en el área de texto

        } catch (Exception e) {
            e.printStackTrace();
            procesosArea.setText("Error al cargar los procesos desde la base de datos.");
        } finally {
            // Cerrar los recursos
            try { if (rs != null) rs.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (Exception e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private Set<Integer> obtenerBoletosUnicos() {
        int cantidadBoletos = random.nextInt(MAX_BOLETOS_POR_PROCESO) + 1; // Entre 1 y MAX_BOLETOS_POR_PROCESO boletos
        Set<Integer> boletos = new HashSet<>();
        while (boletos.size() < cantidadBoletos && !boletosDisponibles.isEmpty()) {
            int index = random.nextInt(boletosDisponibles.size());
            int boleto = (Integer) boletosDisponibles.toArray()[index];
            boletosDisponibles.remove(boleto); // Eliminar boleto para que no se repita
            boletos.add(boleto);
        }
        return boletos;
    }

    private void realizarSorteo() {
        // Generar un número aleatorio y mostrarlo
        int numeroSorteado = random.nextInt(100) + 1; // Genera un número entre 1 y 100
        numeroSorteoLabel.setText("Número sorteado: " + numeroSorteado);

        // Verificar si el número sorteado coincide con algún boleto
        StringBuilder ganadoresTexto = new StringBuilder();
        for (Map.Entry<String, Set<Integer>> entry : procesosConBoletos.entrySet()) {
            String nombre = entry.getKey();
            Set<Integer> boletos = entry.getValue();
            if (boletos.contains(numeroSorteado)) {
                ganadoresTexto.append("Ganador: ").append(nombre).append(" - Sorteo: ").append(boletos).append("\n");
            }
        }

        // Mostrar los ganadores en el área de texto
        if (ganadoresTexto.length() == 0) {
            ganadoresArea.setText("No hay ganadores con el número sorteado.");
        } else {
            ganadoresArea.setText(ganadoresTexto.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmuladorSorteo().setVisible(true);
            }
        });
    }
}

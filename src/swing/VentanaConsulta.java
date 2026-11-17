package swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class VentanaConsulta extends JFrame {
    private static final String URL = "http://localhost:8080/socios";

    private JButton btnConsultar = new JButton("Consultar socios");
    private JLabel lblEstado = new JLabel("Listo para consultar");
    private JTextArea areaDatos = new JTextArea();
    private JProgressBar barra = new JProgressBar();   // Ejercicio 4

    public VentanaConsulta() {
        super("Consulta al servidor");
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        arriba.add(btnConsultar);
        arriba.add(lblEstado);
        areaDatos.setEditable(false);
        areaDatos.setFont(new Font("Monospaced", Font.PLAIN, 13));
        barra.setIndeterminate(false);
        barra.setVisible(false);
        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(areaDatos), BorderLayout.CENTER);
        add(barra, BorderLayout.SOUTH);
        btnConsultar.addActionListener(e -> consultar());
        setSize(560, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void consultar() {
        btnConsultar.setEnabled(false);
        lblEstado.setText("Consultando...");
        areaDatos.setText("");
        barra.setVisible(true);
        barra.setIndeterminate(true);
        SwingWorker<String, Void> trabajador = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                HttpClient cliente = HttpClient.newHttpClient();
                HttpRequest peticion = HttpRequest.newBuilder(URI.create(URL)).build();
                HttpResponse<String> respuesta =
                    cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
                return respuesta.body();
            }

            @Override
            protected void done() {
                try {
                    String json = get();
                    areaDatos.setText(json.replace("}, {", "}\n{"));
                    lblEstado.setText("Datos recibidos");
                } catch (InterruptedException | ExecutionException ex) {
                    lblEstado.setText("Error");
                    JOptionPane.showMessageDialog(VentanaConsulta.this,
                        "No se pudo conectar con el servidor",
                        "Error de conexion", JOptionPane.ERROR_MESSAGE);
                } finally {
                    barra.setIndeterminate(false);
                    barra.setVisible(false);
                    btnConsultar.setEnabled(true);
                }
            }
        };
        trabajador.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaConsulta().setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class ConversorGUI extends JFrame {
    private JComboBox<String> opcionesCombo;
    private JTextField cantidadField;
    private JLabel resultadoLabel;
    private JButton convertirButton;
    private JButton cerrarButton;
    private DecimalFormat formatoDecimal = new DecimalFormat("#.##");

    // Definimos algunos colores
    private static final Color COLOR_FONDO = new Color(240, 248, 255); // Azul claro
    private static final Color COLOR_PANEL = new Color(173, 216, 230); // Azul cielo claro
    private static final Color COLOR_BOTON = new Color(70, 130, 180);  // Azul acero

    public ConversorGUI() {
        setTitle("Conversor de Moneda -Tito Real-");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_PANEL);

        String[] opciones = { "Dolares (USD) a Euros (EUR)", "Dolares (USD) a Pesos Colombianos (COP)", 
                              "Dolares (USD) a Libras Esterlinas (GBP)", "Dolares (USD) a Yen (JPY)",
                              "Dolares (USD) a Won (KRW)" };
        opcionesCombo = new JComboBox<>(opciones);
        opcionesCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, opcionesCombo.getPreferredSize().height));

        cantidadField = new JTextField(10);
        cantidadField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cantidadField.getPreferredSize().height));

        convertirButton = new JButton("Convertir");
        convertirButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        estilizarBoton(convertirButton);

        cerrarButton = new JButton("Cerrar Aplicación");
        cerrarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        estilizarBoton(cerrarButton);

        resultadoLabel = new JLabel("Resultado: ");
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(new JLabel("Seleccione la conversión:"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(opcionesCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(new JLabel("Ingrese la cantidad en Dólares:"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(cantidadField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(convertirButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(resultadoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(cerrarButton);

        convertirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertir();
            }
        });

        cerrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcion = JOptionPane.showConfirmDialog(ConversorGUI.this,
                    "¿Está seguro que desea cerrar la aplicación?",
                    "Confirmar Cierre",
                    JOptionPane.YES_NO_OPTION);
                
                if (opcion == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        add(panel);
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(COLOR_BOTON);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
    }

    private void convertir() {
        try {
            double dolares = Double.parseDouble(cantidadField.getText());
            String eleccion = (String) opcionesCombo.getSelectedItem();
            String monedaDestino = obtenerCodigoMoneda(eleccion);
            double tasaDeCambio = obtenerTasaDeCambio(monedaDestino);

            if (tasaDeCambio == 0.0) {
                resultadoLabel.setText("Error al obtener la tasa de cambio.");
                return;
            }

            double resultado = convertirMoneda(dolares, tasaDeCambio);
            resultadoLabel.setText(dolares + " USD = " + formatoDecimal.format(resultado) + " " + obtenerNombreMoneda(monedaDestino));
        } catch (NumberFormatException ex) {
            resultadoLabel.setText("Por favor, ingrese un valor numérico válido.");
        }
    }
    
    public static String obtenerCodigoMoneda(String eleccion) {
        if (eleccion.contains("Euros")) return "EUR";
        if (eleccion.contains("Pesos")) return "COP";
        if (eleccion.contains("Libras")) return "GBP";
        if (eleccion.contains("Yen")) return "JPY";
        if (eleccion.contains("Won")) return "KRW";
        return "";
    }
    
    public static String obtenerNombreMoneda(String codigo) {
        switch (codigo) {
            case "EUR": return "Euros";
            case "COP": return "Pesos Colombianos";
            case "GBP": return "Libras Esterlinas";
            case "JPY": return "Yenes";
            case "KRW": return "Wones";
            default: return "";
        }
    }
    
    public static double obtenerTasaDeCambio(String monedaDestino) {
        try {
            String apiKey = "264de6227c33159605c3776f"; // clave API
            URL url = new URL("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/USD");
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }
            scanner.close();
            JSONObject json = new JSONObject(sb.toString());
            
            return json.getJSONObject("conversion_rates").getDouble(monedaDestino);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener la tasa de cambio.");
            return 0.0;
        }
    }
    
    public static double convertirMoneda(double cantidad, double tasaDeCambio) {
        return cantidad * tasaDeCambio;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConversorGUI().setVisible(true);
            }
        });
    }
}
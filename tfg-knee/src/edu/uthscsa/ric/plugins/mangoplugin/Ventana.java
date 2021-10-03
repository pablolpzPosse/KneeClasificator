package edu.uthscsa.ric.plugins.mangoplugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class Ventana extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel texto;           // etiqueta o texto no editable
    private JTextField caja;        // caja de texto, para insertar datos
    private JButton boton;          // boton con una determinada accion
    private static String Ubicacion = "";


	public void setUbicacion(String ubicacion) {
		Ubicacion = ubicacion;
	}

	public Ventana(String texto) {
        super();                    // usamos el contructor de la clase padre JFrame
        configurarVentana();        // configuramos la ventana
        inicializarComponentes(texto);   // inicializamos los atributos o componentes
    }

    private void configurarVentana() {
        this.setTitle("Guardar");
        this.setSize(310, 210);
        this.setLocationRelativeTo(null);  // centramos la ventana en la pantalla
        this.setLayout(null);             
        this.setResizable(false);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void inicializarComponentes(String text) {
        texto = new JLabel();
        caja = new JTextField();
        boton = new JButton();
        texto.setText(text);    // colocamos un texto a la etiqueta
        texto.setBounds(50, 50, 100, 25);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        caja.setBounds(150, 50, 100, 25);   // colocamos posicion y tamanio a la caja (x, y, ancho, alto)
        boton.setText("Aceptar");   // colocamos un texto al boton
        boton.setBounds(50, 100, 100, 30);  // colocamos posicion y tamanio al boton (x, y, ancho, alto)
        boton.addActionListener(this);
        
        this.add(texto);
        this.add(caja);
        this.add(boton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       setUbicacion(caja.getText());// obtenemos el contenido de la caja de texto
        JOptionPane.showMessageDialog(this, "Hola " + Ventana.Ubicacion + ".");    // mostramos un mensaje (frame, mensaje)
    }

    public static void main(String[] args) {
        Ventana V = new Ventana("");      // creamos una ventana
        V.setVisible(true);             // hacemos visible la ventana creada
    }
}

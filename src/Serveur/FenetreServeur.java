/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Serveur;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Benoit
 */
public class FenetreServeur extends JFrame {
    protected String nomFichier;
    private JLabel lbNomFichier;
    private JTextField tfNomFichier;
    private JButton val;
    private JButton cancel;
    private boolean stop=false;
    
    protected JTextArea ta;
    private JScrollPane sp;
    FenetreServeur() {
        this.setTitle("Serveur Jeu Anagramme");
        this.setMinimumSize(new Dimension(400, 200));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);
        if(dialogStart()){
            System.exit(0);
        }
        installMainWindow();
    }
    public boolean dialogStart(){
        final JDialog dial = new JDialog();
        dial.setTitle("Veuillez rentrer le nom du fichier log :");
        dial.setModal(true);
        dial.setLayout(new BorderLayout());
        dial.setMinimumSize(new Dimension(300,100));
        dial.setResizable(false);
        dial.setLocationRelativeTo(null);
        
        lbNomFichier = new JLabel ("Nom du fichier log: ");
        tfNomFichier = new JTextField ("fichier_log");
        
        JPanel form = new JPanel(new FlowLayout());
        form.add(lbNomFichier);
        form.add(tfNomFichier);
        tfNomFichier.setPreferredSize(new Dimension(100,20));
        
        val = new JButton("Valider");
        cancel = new JButton("Annuler");
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(val);
        buttons.add(cancel);
        
        dial.add(form, BorderLayout.NORTH);
        dial.add(buttons, BorderLayout.SOUTH);   
        
        val.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nomFichier = tfNomFichier.getText();
                dial.dispose();
            }
        });
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(dial, "Voulez-vous quitter ? ", "Quitter ?", JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION) {
                    dial.dispose();
                    stop = true;
                }
            }
        });
        dial.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {}
            @Override
            public void windowClosing(WindowEvent we) {
                dial.dispose();
                stop = true;
            }
            @Override
            public void windowClosed(WindowEvent we) {}
            @Override
            public void windowIconified(WindowEvent we) {}
            @Override
            public void windowDeiconified(WindowEvent we) {}
            @Override
            public void windowActivated(WindowEvent we) {}
            @Override
            public void windowDeactivated(WindowEvent we) {}
        });
        dial.pack(); 
        dial.setVisible(true);
        return stop;
    }
    public void installMainWindow(){
        ta = new JTextArea();
        ta.setEditable(false);
        sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(400, 200));
        this.add(sp);        
    }/*
    public static void main(String[] args) {
        FenetreServeur fen = new FenetreServeur();
        fen.pack(); // calcul récursif des positions et des tailles
        fen.setVisible(true);  // fait apparaître la fenêtre 
    }*/
}

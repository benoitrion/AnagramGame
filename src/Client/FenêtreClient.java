/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

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
public class FenêtreClient extends JFrame {
    //JDialog de début 
    private JLabel lab;
    private JLabel lbNbErr;
    private JTextField tfNbErr;
    private JLabel lbHost;
    private JTextField tfHost;
    private JLabel lbPseudo;
    private JTextField tfPseudo;
    private JButton val;
    private JButton cancel;
    
    protected int limite;
    protected String host;
    protected String pseudo;
    private boolean stop=false;
    
    //Main Window
    private JMenuBar mbJeu;
    private JMenu mJeu;
    private JMenuItem mOptions;
    private JMenuItem mQuitter;
    protected JTextArea ta;
    private JScrollPane sp;
    protected JTextField tf;
    protected JButton butEnvoi;
    
    public FenêtreClient () {
        this.setTitle("Jeu Anagramme");
        this.setMinimumSize(new Dimension(500, 300));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        if(dialogStart()){
            System.exit(0);
        }
        installMainWindow();
        installEvents();
        
    }    
    public boolean dialogStart(){
        final JDialog dial = new JDialog();
        dial.setTitle("Veuillez complèter vos infos :");
        dial.setModal(true);
        dial.setLayout(new BorderLayout());
        dial.setMinimumSize(new Dimension(300,120));
        dial.setResizable(false);
        dial.setLocationRelativeTo(null);
        
        lbNbErr = new JLabel ("Nombre d'erreur : ");
        tfNbErr = new JTextField ("3");
        lbHost = new JLabel("Adresse Ip :");
        tfHost = new JTextField("localhost");
        lbPseudo = new JLabel("Pseudo :");
        tfPseudo = new JTextField("Benoit");
        
        JPanel form = new JPanel(new GridLayout(3,3));
        
        form.add(lbNbErr);
        form.add(tfNbErr);
        form.add(lbHost);
        form.add(tfHost);
        form.add(lbPseudo);
        form.add(tfPseudo);
        
        val = new JButton("Valider");
        cancel = new JButton("Annuler");
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(val);
        buttons.add(cancel);
        
        dial.add(form, BorderLayout.NORTH);
        dial.add(buttons, BorderLayout.SOUTH);   
        
        val.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limite = Integer.parseInt(tfNbErr.getText());
                host = tfHost.getText();
                pseudo = tfPseudo.getText();
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
        mbJeu = new JMenuBar();
        this.setJMenuBar(mbJeu);
        mJeu = new JMenu("Jeu");
        mbJeu.add(mJeu);
        mOptions = new JMenuItem("Options");
        mQuitter = new JMenuItem("Quitter");
        mJeu.add(mOptions);
        mJeu.add(mQuitter);
        tf = new JTextField();
        ta = new JTextArea();
        ta.setEditable(false);
        sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(300, 200));
        butEnvoi = new JButton("Envoi");
        this.setLayout(new BorderLayout());
        JPanel pan = new JPanel(new FlowLayout());
        pan.add(tf);
        tf.setPreferredSize(new Dimension(150, 20));
        pan.add(butEnvoi);
        this.add(sp,BorderLayout.NORTH);
        this.add(pan, BorderLayout.SOUTH);
        
    }
    public void installEvents(){
        mOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        mQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}

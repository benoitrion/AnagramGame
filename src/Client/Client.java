/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import AnagrammeDivers.AnagramMsg;
import static AnagrammeDivers.AnagramMsg.PORT_NUM;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Benoit
 */
public class Client extends FenêtreClient {

    private Socket serveur;
    int nbEssai = 0;
    ObjectInputStream fluxEntrant;
    ObjectOutputStream fluxSortant;
    Scanner sc = new Scanner(System.in);
    private boolean connected = true;
    private String proposition;

    public Client() {
        super();
        this.pack(); // calcul récursif des positions et des tailles
        this.setVisible(true);  // fait apparaître la fenêtre 
        try {
            serveur = new Socket(host, PORT_NUM);
            fluxSortant = new ObjectOutputStream(serveur.getOutputStream());
            fluxEntrant = new ObjectInputStream(serveur.getInputStream());
        } catch (IOException ex) {
            ta.append("\nFatal error : \n"
                    + "Please check server is running and "
                    + "check the port you want to listen.\n"
                    + "And check your IP address.");
        }
        butEnvoi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proposition = tf.getText();
                tf.setText("");
                try {
                    fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_PROPOSE, proposition, null, null));
                    fluxSortant.flush();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER && tf.getText().compareTo("") != 0) {
                    butEnvoi.doClick();
                }
            }
        });
    }

    public void tourner() throws IOException, ClassNotFoundException {

        AnagramMsg msgReceived;
        AnagramMsg msgToSend;
        try {

            ta.append("You are connected...\n");
            fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_LOGIN, null, null, null));
            fluxSortant.flush();

            ta.append("The game starts...\n");
            fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_NEW, null, null, null));
            fluxSortant.flush();
        } catch (IOException ex) {
            ta.append(ex.getMessage() + "\n");
        }
        while (connected && (msgReceived = (AnagramMsg) (fluxEntrant.readObject())) != null) {
            switch (msgReceived.getType()) {
                case SRV_ANAGRAM:
                    ta.append("Find the anagram : " + msgReceived.getAnagram() + "\n");
                    choisir();
                    break;
                case SRV_ERROR:
                    JOptionPane.showMessageDialog(this, "'" + proposition + "' is not the right solution.\nThe proposition contains an error", "Warning !", WIDTH, null);

                    ta.append("'" + proposition + "' is not the right solution.\nThe proposition contains an error\n");
                    if (limite - nbEssai > 0) {
                        choisir();
                    } else {
                        fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_STOP, null, null, null));
                        fluxSortant.flush();
                        ta.append("It remains " + (limite - nbEssai) + " plays...\n");
                        ta.append("You loosed the game...\n");
                        connected = false;
                    }
                    break;
                case SRV_WORD:
                    ta.append("The right word was : " + msgReceived.getWord() + "\n");
                    break;
                case SRV_WORD_FOUND:
                    nbEssai = 0;
                    JOptionPane.showMessageDialog(this, "Congratulation, you ve found the right word", "You succeeded !", WIDTH, null);
                    ta.append("Congratulation, you ve found the right word !\n");
                    ta.append("The game restart...\n");
                    fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_NEW, null, null, null));
                    fluxSortant.flush();
                    break;
                case SRV_WORD_NOT_FOUND:
                    JOptionPane.showMessageDialog(this, "'" + proposition + "' is not the right solution.\nWrong proposition!!!", "Warning !", WIDTH, null);
                    ta.append("'" + proposition + "' is not the right solution.\nWrong proposition!!!\n");
                    if (limite - nbEssai > 0) {
                        choisir();
                    } else {
                        fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_STOP, null, null, null));
                        fluxSortant.flush();
                        JOptionPane.showMessageDialog(this,
                                "It remains " + (limite - nbEssai) + " plays...\n"
                                + "The game is over...\n",
                                "You loosed !", WIDTH, null);
                        ta.append("It remains " + (limite - nbEssai) + " plays...\n");
                        ta.append("The game is over...\n");
                        connected = false;
                    }
                    break;
            }
        }
        ta.append("You leaved the game !\n");
        serveur.close();
    }

    public void choisir() {
        try {
            String[] obj = {"Proposition", "Log out"};
            int choice = JOptionPane.showOptionDialog(null,
                    "It remains (" + (limite - nbEssai) + "/" + limite + ") plays.\n"
                    + "Proposition or log out ? Let choose one !!!",
                    "Choose an option !",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    obj,
                    "Proposition");
            if (choice == 0) {
                nbEssai++;
                ta.append("Please propose a word :\n");

            } else {
                fluxSortant.writeObject((AnagramMsg) new AnagramMsg(AnagramMsg.Type.CLT_LOGOUT, null, null, null));
                fluxSortant.flush();
                connected = false;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.tourner();
    }
}

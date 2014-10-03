/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serveur;

import AnagrammeDivers.AnagramMsg;
import static AnagrammeDivers.AnagramMsg.PORT_NUM;
import be.esi.alg3ir.anagram.business.AnagramException;
import be.esi.alg3ir.anagram.business.AnagramGame;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Benoit
 */
public class Serveur extends FenetreServeur {

    PrintWriter log;
    private ServerSocket ss;
    private ArrayList<ObjectOutputStream> fluxDeMesClients;
    Scanner sc = new Scanner(System.in);

    public Serveur() throws IOException {
        super();
        this.pack(); // calcul récursif des positions et des tailles
        this.setVisible(true);  // fait apparaître la fenêtre 
        ss = new ServerSocket(PORT_NUM);
        fluxDeMesClients = new ArrayList<>();
        log = new PrintWriter(new FileOutputStream(nomFichier + ".txt", true));
        JOptionPane.showMessageDialog(this, "A log file has been created");
    }

    public void tourner() throws IOException {
        ta.append("Server is running...\n");
        Socket leClient;
        while ((leClient = ss.accept()) != null) {
            ta.append("A client is connecting : "
                    + leClient.getInetAddress().toString()
                    + "\n");
            log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " : Log in.");
            log.write("\r\n");
            (new ThreadClient(leClient)).start();
        }
        ss.close();
    }

    public class ThreadClient extends Thread {

        Socket leClient;
        InetAddress ia;
        ObjectInputStream fluxEntrant;
        ObjectOutputStream fluxSortant;
        String[] words = {"travelling",
            "germany",
            "oktoberfest",
            "discovering",
            "highway"};
        String motActuel;
        AnagramGame jeu = new AnagramGame(words);
        private boolean connected = true;

        public ThreadClient(Socket sock) throws IOException {
            leClient = sock;
            ia = sock.getInetAddress();
            fluxEntrant = new ObjectInputStream(leClient.getInputStream());
            fluxSortant = new ObjectOutputStream(leClient.getOutputStream());
        }

        @Override
        public void run() {
            AnagramMsg msgReceived;
            AnagramMsg msgToSend;
            try {
                while (connected && (msgReceived = (AnagramMsg) (fluxEntrant.readObject())) != null) {
                    switch (msgReceived.getType()) {
                        case CLT_LOGIN:
                            fluxDeMesClients.add(fluxSortant);
                            break;
                        case CLT_LOGOUT:
                            fluxDeMesClients.remove(fluxSortant);
                            connected = false;
                            break;
                        case CLT_NEW:
                            if (fluxDeMesClients.indexOf(fluxSortant) != -1) {
                                motActuel = jeu.newAnagramGame();
                                msgToSend = new AnagramMsg(AnagramMsg.Type.SRV_ANAGRAM, null, motActuel, null);
                                fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).writeObject((AnagramMsg) msgToSend);
                                fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).flush();
                                log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " : Nouvelle partie, l'anagrame a trouver est " + motActuel);
                                log.write("\r\n");
                            }
                            ;
                            break;
                        case CLT_PROPOSE:
                            try {
                                if (jeu.propose(msgReceived.getWord())) {
                                    msgToSend = new AnagramMsg(AnagramMsg.Type.SRV_WORD_FOUND, null, motActuel, null);
                                    fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).writeObject((AnagramMsg) msgToSend);
                                    fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).flush();
                                    log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " : Le client a trouvé la solution avec le mot '" + msgReceived.getWord() + "'");
                                    log.write("\r\n");
                                } else {
                                    msgToSend = new AnagramMsg(AnagramMsg.Type.SRV_WORD_NOT_FOUND, null, motActuel, null);
                                    fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).writeObject((AnagramMsg) msgToSend);
                                    fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).flush();
                                    log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " :  '" + msgReceived.getWord() + "' n'est pas la solution");
                                    log.write("\r\n");
                                }
                            } catch (AnagramException | IllegalArgumentException e) {
                                msgToSend = new AnagramMsg(AnagramMsg.Type.SRV_ERROR, null, null, e.getMessage());
                                fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).writeObject((AnagramMsg) msgToSend);
                                fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).flush();
                                log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " :  '" + msgReceived.getWord() + "' n'est pas la solution");
                                log.write("\r\n");
                            }
                            break;
                        case CLT_STOP:
                            msgToSend = new AnagramMsg(AnagramMsg.Type.SRV_WORD, jeu.stopGame(), null, null);
                            fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).writeObject((AnagramMsg) msgToSend);
                            fluxDeMesClients.get(fluxDeMesClients.indexOf(fluxSortant)).flush();
                            log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " :  La partie est perdue");
                            log.write("\r\n");
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                ta.append(ex.getMessage()+"\n");
            }
            try {
                if (fluxEntrant != null) {
                    fluxEntrant.close();
                }
                if (fluxSortant != null) {
                    fluxSortant.flush();
                    fluxSortant.close();
                }
                if (leClient != null) {
                    log.write(Calendar.getInstance().getTime().toString() + " - " + leClient.getInetAddress().toString() + " : Log out.");
                    log.write("\r\n\r\n");
                    log.close();
                    ta.append("Un client is disconnecting : "
                            + leClient.getInetAddress().toString()
                            + "\n");
                    leClient.close();
                }
            } catch (IOException ioe) {
                ta.append("Problème lors de la fermeture du client " + leClient.getInetAddress().toString()+"\n");
            }

        }
    }

    public static void main(String[] args) {
        try {
            Serveur serv = new Serveur();
            serv.tourner();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

/* -----------------------------------------------------------------------------
 *       UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED
 *                 (C) Copyright 2018, USAREUR G3 MCSD
 *                         ALL RIGHTS RESERVED
 *                 THIS NOTICE DOES NOT IMPLY PUBLICATION
 * -------------------------------------------------------------------------- */
package mil.army.usareur.g3.mcsd.CoTUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin.c.dudel.civ@mail.mil
 */
public class CoTtcpListener extends Thread {

    private ServerSocket cotSocket;
    private int cotPort = 9998;
    private boolean debug = false;
    private volatile boolean runFlag = true;
    private CoTparser cotParser;
    private boolean customParserSet = false;

    /**
     * Create a CoT TCP socket listener on the default port of 9998
     */
    public CoTtcpListener() {
        try {
            cotSocket = new ServerSocket(cotPort);
        } catch (IOException ex) {
            Logger.getLogger(CoTtcpListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//CotTcpListener

    /**
     * Set a customized CoT parser to be used for parsing the CoT events. This
     * customized parser contains the developers specific implementation and
     * handling of CoT events.
     *
     * @param cp an implementation of the CoTparser
     */
    public void setCoTparser(CoTparser cp) {
        this.cotParser = cp;
        customParserSet = true;
    }

    /**
     * Create a CoT TCP socket listener on the desired port.
     *
     * @param port an integer port number
     */
    public CoTtcpListener(int port) {
        cotPort = port;
        try {
            cotSocket = new ServerSocket(cotPort);
        } catch (IOException ex) {
            Logger.getLogger(CoTtcpListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//CotTcpListener(int port)

    /**
     * Create a CoT TCP listener on the desired port and set the debug statement
     * option. Debug statements are not printed by default.
     *
     * @param port an integer port number
     * @param debug a boolean indicating whether to print out debug statements
     */
    public CoTtcpListener(int port, boolean debug) {
        cotPort = port;
        this.debug = debug;
        debugToConsole("CoT Listener port: " + cotPort);
        try {
            cotSocket = new ServerSocket(cotPort);
        } catch (IOException ex) {
            Logger.getLogger(CoTtcpListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//CotTcpListener(int port)

    private void debugToConsole(String msg) {
        if (this.debug) {
            System.out.println(msg);
        }
    }//debugOut

    /**
     * Stops the listener. Note that when listener threads are stopped they can
     * not be restarted. The listener object must be reconstructed.
     */
    public void stopThread() {
        debugToConsole("CoT Listener received stop request, port:" + cotPort);
        this.runFlag = false;
        try {
            cotSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(CoTtcpListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//stopThread

    /**
     *
     * @return an integer representing the port the socket listener uses
     */
    public int getPort() {
        return this.cotPort;
    }//getPort

    @Override
    public void run() {
        debugToConsole("CoT Listener thread started, port:" + cotPort);
        Socket accept = null;
        if (!this.customParserSet) {
            cotParser = new CoTparser();  // Create a default Cursor on Target parser
        }
        String cotLine;
        StringBuilder cotMessage = new StringBuilder();
        while (this.runFlag) {
            try {
                accept = cotSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));

                while ((cotLine = reader.readLine()) != null) {
                    cotMessage.append(cotLine);
                }
                debugToConsole("TCP ========== Begin message:\n" + cotMessage.toString() + "\nTCP ========== End message");
                if (this.debug) {
                    cotParser.dumpCoTevent(cotMessage.toString());
                }
                // The CoT message is handled here
                cotParser.coTeventHandler(cotMessage.toString());
                // Reset the StringBuilder to hold the next TCP CoT message
                cotMessage = new StringBuilder();
            } catch (Exception e) {
                this.runFlag = false;
                //throw new RuntimeException(e);
                System.out.println(e.getMessage() + "\n" + e.toString());
                System.out.println("CoT Listener thread stopped, port:" + cotPort);
            }
        }//while
    }//run    
}// class CoTtcpListener

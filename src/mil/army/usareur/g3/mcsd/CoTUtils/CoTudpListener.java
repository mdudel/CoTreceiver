/* -----------------------------------------------------------------------------
 *       UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED
 *                 (C) Copyright 2018, USAREUR G3 MCSD
 *                         ALL RIGHTS RESERVED
 *                 THIS NOTICE DOES NOT IMPLY PUBLICATION
 * -------------------------------------------------------------------------- */
package mil.army.usareur.g3.mcsd.CoTUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin.c.dudel.civ@mail.mil
 */
public class CoTudpListener extends Thread {

    private int cotUdpPort = 9999;
    private boolean debug = false;
    private int packetSize = 1024;
    private volatile boolean runFlag = true;
    private DatagramSocket serverSocket;
    private CoTparser cotParser;
    private boolean customParserSet = false;

    /**
     * Create a CoT UDP listener on the default port of 9999
     */
    public CoTudpListener() {
        // Constructor using default port of 9999
        cotUdpPort = 9999;
    }//CotUdpListener

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
     * Set the maximum packet size that the CoT UDP parse will handle. Bytes
     * that exceed this threshold will be truncated. The maximum expected UDP
     * packet size is 64 x 1024.
     *
     * The default packet size is 1024 bytes.
     *
     * @param packetSize an integer representing the maximum packet size
     */
    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }//setPacketSize

    /**
     * Create a CoT UDP listener on the desired port. The default packet size is
     * 1024 bytes.
     *
     * @param port an integer port number
     */
    public CoTudpListener(int port) {
        // Constructor that sets a custom UDP listening port
        cotUdpPort = port;
    }//CotUdpListener(int port)

    /**
     * Create a CoT UDP listener on the desired port and a maximum packet size.
     *
     * @param port an integer port number
     * @param packetSize an integer representing the maximum packet size
     */
    public CoTudpListener(int port, int packetSize) {
        // Constructor that sets a custom UDP listening port and packet size
        cotUdpPort = port;
        this.packetSize = packetSize;
    }//CotUdpListener(int port, int packetSize)    

    /**
     * Create a CoT UDP listener on the desired port and set the debug statement
     * option. Debug statements are not printed by default.
     *
     * @param port an integer port number
     * @param debug a boolean indicating whether to print out debug statements
     */
    public CoTudpListener(int port, boolean debug) {
        // Constructor that sets a custom UDP listening port
        cotUdpPort = port;
        this.debug = debug;
        debugToConsole("CoT UDP Listener port: " + cotUdpPort);
    }//CotUdpListener(int port, boolean debug)

    /**
     * Create a CoT UDP listener on the desired port with a maximum packet size
     * and set the debug statement option. Debug statements are not printed by
     * default.
     *
     * @param port an integer port number
     * @param packetSize an integer representing the maximum packet size
     * @param debug a boolean indicating whether to print out debug statements
     */
    public CoTudpListener(int port, int packetSize, boolean debug) {
        // Constructor that sets a custom UDP listening port and packet size
        cotUdpPort = port;
        this.packetSize = packetSize;
        this.debug = debug;
        debugToConsole("CoT UDP Listener port: " + cotUdpPort);
        debugToConsole("Maximum CoT UDP packet size: " + this.packetSize);
    }//CotUdpListener(int port, int packetSize, boolean debug)  

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
        debugToConsole("CoT UDP Listener received stop request, port:" + cotUdpPort);
        this.runFlag = false;
        serverSocket.close();
    }//stopThread

    /**
     *
     * @return an integer representing the port the listener uses
     */
    public int getPort() {
        return this.cotUdpPort;
    }//getPort

    @Override
    public void run() {
        byte[] rcvData = new byte[packetSize];
        if (!this.customParserSet) {
            cotParser = new CoTparser();  // Create a default Cursor on Target parser
        }
        try {
            serverSocket = new DatagramSocket(cotUdpPort);
            debugToConsole("CoT UDP Listener thread started, port:" + cotUdpPort);
            while (this.runFlag) {
                DatagramPacket rcvPacket = new DatagramPacket(rcvData, rcvData.length);
                serverSocket.receive(rcvPacket);
                String cotXml = new String(rcvPacket.getData());
                debugToConsole("UDP ========== Begin message:\n" + cotXml.trim() + "\nUDP ========== End message");
                if (this.debug) {
                    cotParser.dumpCoTevent(cotXml);
                }
                // The CoT message is handled here                
                cotParser.coTeventHandler(cotXml);
                // reset the byte array for the mext UDP packet
                rcvData = new byte[packetSize];
            }//while
        } catch (SocketException ex) {
            if (this.runFlag) {
                Logger.getLogger(CoTudpListener.class.getName()).log(Level.SEVERE, null, ex);
            } else {
                serverSocket.close();
                debugToConsole("CoT UDP Listener socket closed, port:" + cotUdpPort);
            }
        } catch (IOException ex) {
            Logger.getLogger(CoTudpListener.class.getName()).log(Level.SEVERE, null, ex);
        }//try catch
        debugToConsole("CoT UDP Listener thread closed, port:" + cotUdpPort + " socket closed:" + serverSocket.isClosed());
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }// ensure socket is closed
    }//run
}// class CoTudpListener

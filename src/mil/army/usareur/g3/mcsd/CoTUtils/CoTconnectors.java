/* -----------------------------------------------------------------------------
 *       UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED
 *                 (C) Copyright 2018, USAREUR G3 MCSD
 *                         ALL RIGHTS RESERVED
 *                 THIS NOTICE DOES NOT IMPLY PUBLICATION
 * -------------------------------------------------------------------------- */
package mil.army.usareur.g3.mcsd.CoTUtils;

import java.lang.Thread.State;
import java.util.HashMap;

/**
 *
 * @author martin.c.dudel.civ@mail.mil
 */
public class CoTconnectors {

    private final HashMap<Integer, Object> cotListeners = new HashMap<>();
    private CoTparser cotParser;
    private boolean cotParserSet = false;

    /**
     * Set a customized CoT parser to be used for parsing the CoT events. This
     * customized parser contains the developers specific implementation and
     * handling of CoT events.
     *
     * @param cotParser an implementation of the CoTparser
     */
    public void setCoTparser(CoTparser cotParser) {
        this.cotParser = cotParser;
        cotParserSet = true;
    }

    /**
     * Add a UDP listener on a desired port. The listener still needs to be
     * started.
     *
     * @param port an integer port number
     */
    public void addUdpListener(int port) {
        if (!cotListeners.containsKey(port)) {
            CoTudpListener cotUdpListener = new CoTudpListener(port);
            if (this.cotParserSet) {
                cotUdpListener.setCoTparser(cotParser);
            }
            cotListeners.put(port, cotUdpListener);
            System.out.println("CoT UDP Listener created, port: " + cotUdpListener.getPort());
        } else {
            // Todo: handle existing listener
            System.out.println(port + " already exists, finish this method");
        }
    }//addUdpListener

    /**
     * Add a TCP listener on a desired port. The listener still needs to be
     * started.
     *
     * @param port an integer port number
     */
    public void addTcpListener(int port) {
        if (!cotListeners.containsKey(port)) {
            CoTtcpListener cotTcpListener = new CoTtcpListener(port);
            if (this.cotParserSet) {
                cotTcpListener.setCoTparser(cotParser);
            }
            cotListeners.put(port, cotTcpListener);
            System.out.println("CoT TCP Listener created, port: " + cotTcpListener.getPort());
        } else {
            // Todo: handle existing listener
            System.out.println(port + " already exists, finish this method");
        }
    }//addTcpListener

    /**
     * Add a UDP listener on a desired port and set the debug statement option.
     * Debug statements are not printed by default. The listener still needs to
     * be started.
     *
     * @param port an integer port number
     * @param debug a boolean indicating whether to print out debug statements
     */
    public void addUdpListener(int port, boolean debug) {
        if (!cotListeners.containsKey(port)) {
            CoTudpListener cotUdpListener = new CoTudpListener(port, debug);
            if (this.cotParserSet) {
                cotUdpListener.setCoTparser(cotParser);
            }
            cotListeners.put(port, cotUdpListener);
            System.out.println("CoT UDP Listener created, port: " + cotUdpListener.getPort());
        } else {
            // Todo: handle existing listener
            System.out.println(port + " already exists, finish this method");
        }
    }//addUdpListener

    /**
     * Add a TCP listener on a desired port and set the debug statement option.
     * Debug statements are not printed by default. The listener still needs to
     * be started.
     *
     * @param port an integer port number
     * @param debug a boolean indicating whether to print out debug statements
     */
    public void addTcpListener(int port, boolean debug) {
        if (!cotListeners.containsKey(port)) {
            CoTtcpListener cotTcpListener = new CoTtcpListener(port, debug);
            if (this.cotParserSet) {
                cotTcpListener.setCoTparser(cotParser);
            }
            cotListeners.put(port, cotTcpListener);
            System.out.println("CoT TCP Listener created, port: " + cotTcpListener.getPort());
        } else {
            // Todo: handle existing listener
            System.out.println(port + " already exists, finish this method");
        }
    }//addTcpListener

    /**
     * Add a UDP listener on the desired port with a maximum packet size.
     *
     * @param port an integer port number
     * @param packetSize an integer representing the maximum packet size
     */
    public void addUdpListener(int port, int packetSize) {
        if (!cotListeners.containsKey(port)) {
            CoTudpListener cotUdpListener = new CoTudpListener(port, packetSize);
            if (this.cotParserSet) {
                cotUdpListener.setCoTparser(cotParser);
            }
            cotListeners.put(port, cotUdpListener);
            System.out.println("CoT UDP Listener created, port: " + cotUdpListener.getPort());
        } else {
            // Todo: handle existing listener
            System.out.println(port + " already exists, finish this method");
        }
    }//addUdpListener

    /**
     * Add a UDP listener on the desired port with a maximum packet size and set
     * the debug statement option. Debug statements are not printed by default.
     *
     * @param port an integer port number
     * @param packetSize an integer representing the maximum packet size
     * @param debug a boolean indicating whether to print out debug statements
     */
    public void addUdpListener(int port, int packetSize, boolean debug) {
        if (!cotListeners.containsKey(port)) {
            CoTudpListener cotUdpListener = new CoTudpListener(port, packetSize, debug);
            if (this.cotParserSet) {
                cotUdpListener.setCoTparser(cotParser);
            }
            cotListeners.put(port, cotUdpListener);
            System.out.println("CoT UDP Listener created, port: " + cotUdpListener.getPort());
        } else {
            // Todo: handle existing listener
            System.out.println(port + " already exists, finish this method");
        }
    }//addUdpListener

    /**
     * Returns the listener type of the port
     *
     * @param port an integer port number
     * @return a String representation of the listener type ("udp","tcp")
     */
    public String getPortType(int port) {
        if (cotListeners.containsKey(port)) {
            Object listener = cotListeners.get(port);
            if (listener instanceof CoTudpListener) {
                return "udp";
            } else if (listener instanceof CoTtcpListener) {
                return "tcp";
            }
        }
        return "null";
    }//getPortType

    /**
     * A list of all the ports set to be listeners. Note that this does not
     * indicate if the ports have been started.
     *
     * @return an integer array of ports
     */
    public Integer[] getPorts() {
        Integer[] ports = new Integer[cotListeners.size()];
        int i = 0;
        for (Integer port : cotListeners.keySet()) {
            ports[i] = port;
            i++;
        }
        return ports;
    }//getPorts

    /**
     * Start a listener on a port. Note that the listener must have already been
     * added using an addUdpListener or addTcpListener method.
     *
     * @param port an integer representing a listener port
     */
    public void startListener(int port) {
        if (cotListeners.containsKey(port)) {
            Object listener = cotListeners.get(port);
            if (listener instanceof CoTudpListener) {
                System.out.println("Starting UDP listener, port: " + port);
                CoTudpListener udp = (CoTudpListener) listener;
                udp.start();
            } else if (listener instanceof CoTtcpListener) {
                System.out.println("Starting TCP listener, port: " + port);
                CoTtcpListener tcp = (CoTtcpListener) listener;
                tcp.start();
            }
        } else {
            System.out.println("Listener on port " + port + " does not exist");
        }
    }//startListener

    /**
     * Get the state of the listener of the specified port
     *
     * @param port n integer representing a listener port
     * @return the State of the listener thread
     */
    public State getState(int port) {
        if (cotListeners.containsKey(port)) {
            Object listener = cotListeners.get(port);
            if (listener instanceof CoTudpListener) {
                CoTudpListener udp = (CoTudpListener) listener;
                return udp.getState();
            } else if (listener instanceof CoTtcpListener) {
                CoTtcpListener tcp = (CoTtcpListener) listener;
                return tcp.getState();
            }
        } else {
            System.out.println("Port " + port + " does not exist, unable to getState");

        }
        return null;
    }//getState

    /**
     * Stop a listener on a port. The port is then removed from the collection.
     * If the listener is to be restarted then it must be re-added using the
     * addUdpListener or addTcpListener method
     *
     * @param port an integer representing a listener port
     */
    public void stopListener(int port) {
        if (cotListeners.containsKey(port)) {
            Object listener = cotListeners.get(port);
            if (listener instanceof CoTudpListener) {
                System.out.println("Stopping UDP listener, port: " + port);
                CoTudpListener udp = (CoTudpListener) listener;
                udp.stopThread();
                cotListeners.remove(port);
            } else if (listener instanceof CoTtcpListener) {
                System.out.println("Stopping TCP listener, port: " + port);
                CoTtcpListener tcp = (CoTtcpListener) listener;
                tcp.stopThread();
                cotListeners.remove(port);
            }
        } else {
            System.out.println("Listener on port " + port + " does not exist");
        }
    }//stopListener

}//CoTconnectors

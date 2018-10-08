# CoTreceiver
A Cursor on Target listener and message parser

  CoT messages are XML messages that are sent either UDP or TCP. Once
  the CoT XML is received it can be parsed and the data used to perform
  some function. 
 
  UDP and TCP listeners are provided, as well as a helper class that 
  allows multiple listeners to be managed from a single class.
  - CoTudpListener.java
  - CoTtcpListener.java
  - CoTconnectors.java
 
  The parsing of the CoT XML occurs in the CoTparser class. This class 
  should be extended and the coTeventHandler method overridden for
  custom CoT data handling.
  - CoTparser.coTeventHandler()
 
  The process to receive and parse CoT messages passed as UDP or TCP is
  generally the following:
  
  I.)  Create a custom implementation of the CoTparser class and override
       the coTeventHandler() method. This method receives the CoT message
       (or event) as an XML string. See the CoTparser.dumpCoTevent() method
       for an example of how to extract nearly all of the CoT message data.
 
  II.) Create a UDP or TCP listener. This can be done by invoking the
       listeners directly (CoTudpListener, CoTtcpListener) or using the 
       connectors class (CoTconnectors) which allows the management of 
       multiple listeners.

  Example I:
  - Create a custom CoTparser class. See CustomCoTparser.java.
  - Override the coTeventHandler() to add your custom CoT data handling.
  - Invoke the customized parser:
  
        CustomCoTparser cp = new CustomCoTparser();
 
  Example II.a: Invoke a TCP listener
  - Create a TCP listener directly

    CoTtcpListener cotTcpListener = new CoTtcpListener(cotTcpPort); <-- DEBUG is false by default

    CoTtcpListener cotTcpListener = new CoTtcpListener(cotTcpPort, debug); <-- DEBUG is set by developer
    
        CoTtcpListener cotTcpListener = new CoTtcpListener(9998, true);
  - Assign the custom parser to the listener
  
        cotTcpListener.setCoTparser(cp);
  - start the TCP listener
  
        cotTcpListener.start();
  - Stop the TCP listener
 
        cotTcpListener.stopThread();
  Note that threads cannot be restarted. If you desire to restart the 
  listener you will have to re-invoke and set the custom parser:
  
      cotTcpListener = new CoTtcpListener(9998, true);
      cotTcpListener.setCoTparser(cp);
 
 
  Example II.b: Invoke a UDP listener
  - Create a UDP listener direct
    
    CoTudpListener cotUdpListener = new CoTudpListener(cotUdpPort); <-- DEBUG is false by default

    CoTudpListener cotUdpListener = new CoTudpListener(cotUdpPort, debug); <-- DEBUG is set by developer

    CoTudpListener cotUdpListener = new CoTudpListener(cotUdpPort, packetSize); <-- Set the UDP packet size, default = 1024 bytes
 
    CoTudpListener cotUdpListener = new CoTudpListener(cotUdpPort, packetSize, debug);
    
        CoTudpListener cotUdpListener = new CoTudpListener(9999, 2048, true);
  - Assign the custom parser to the listener
  
        cotUdpListener.setCoTparser(cp);
  - start the UDP listener
  
        cotUdpListener.start();
  - Stop the UDP listener
 
        cotUdpListener.stopThread();
  Note that threads cannot be restarted. If you desire to restart the 
  listener you will have to re-invoke and set the custom parser:
  
      cotUdpListener = new CoTudpListener(9999, 2048, true);
      cotUdpListener.setCoTparser(cp);
 
  Example II.c: Manage several UDP and TCP listeners
  - Create a CoT connector
  
        CoTconnectors connectors = new CoTconnectors();
  - Assign the custom to be used by the listeners. Note that the CoT
    custom parser must be assigned before the listeners are started.
    
        connectors.setCoTparser(cp);
  - Add UPD and TCP listeners to the connectors object
    The UDP and TCP listeners are add with methods that take the same
    parameters as used when invoking the listeners directly.

    connectors.addTcpListener(cotTcpPort);

    connectors.addTcpListener(cotTcpPort, debug);

    connectors.addUdpListener(cotUdpPort);

    connectors.addUdpListener(cotUdpPort, debug);

    connectors.addUdpListener(cotUdpPort, packetSize);

    connectors.addUdpListener(cotUdpPort, packetSize, debug);
    
        connectors.addTcpListener(10000);
        connectors.addUdpListener(10001);
        connectors.addTcpListener(9998, true);
        connectors.addUdpListener(9999, 2048, true);
 
  - List all the ports
  
        Integer[] ports = connectors.getPorts();
        for (int i = 0; i < ports.length; i++) {
            int p = ports[i];
            System.out.println("Listener port: " + p + " " + connectors.getPortType(p));
        }
        int p = 666;
        System.out.println("Test invalid port: " + p + " " + connectors.getPortType(p));
 
  - Get the state of all the listeners (using the returned ports array)
  
        for (int i = 0; i < ports.length; i++) {
            p = ports[i];
            System.out.println("Listener state port: " + p + " " + connectors.getState(p));
        }
  - Start all the listeners by port
    Note: the custom CoT parser must be set before calling the start methods
    
        connectors.startListener(10000);
        connectors.startListener(10001);
        connectors.startListener(9998);
        connectors.startListener(9999);
 
  - Get the state of the ports (explicitly as opposed to the ports array
    used in the example above)
 
        System.out.println("Listener state port: 10000 " + connectors.getState(10000));
        System.out.println("Listener state port: 10001 " + connectors.getState(10001));
        System.out.println("Listener state port: 9998 " + connectors.getState(9998));
        System.out.println("Listener state port: 9999 " + connectors.getState(9999));
  
  - Stop all the listeners explicitly
  
        connectors.stopListener(10000);
        connectors.stopListener(10001);
        connectors.stopListener(9998);
        connectors.stopListener(9999);
 
 - Common usage example: 
 
        System.out.println("\n\nStarting listener pool\n");
        connectors.setCoTparser(cp);
        connectors.addTcpListener(9998, true);
        connectors.addUdpListener(9999, 1024, false);
        connectors.startListener(9998);
        connectors.startListener(9999);

        ports = connectors.getPorts();
        for (int i = 0; i < ports.length; i++) {
            p = ports[i];
            System.out.println("port: " + p + " " + connectors.getPortType(p)+" "+connectors.getState(p));
        }

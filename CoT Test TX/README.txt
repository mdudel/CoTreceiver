======== CoT PLI Tool v.0.0.2 ========
Usage:
java -jar -Djava.net.preferIPv4Stack=true CoTPLITool.jar --performanceTestSendCoT <CoT PLI Destination IP Address> <CoT PLI Destination Port> <Number Unique Entities> <Number PLI Per Second>

CoT messages will be generated at a random location with a two minute stale time. The number PLI per second argument is the total number of PLI messages that will be sent per second, it is not a per entity update rate.





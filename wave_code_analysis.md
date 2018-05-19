# my scenario

1. static nodes
  * I need to give the exact positions of the nodes. 
2. 


# WAVE Examples

This example program allows one to run vehicular ad hoc network (VANET) simulation scenarios in ns-3 to assess performance by evaluating different 802.11p MAC/PHY characteristics, propagation loss models (e.g. Friss, Two-Ray Ground, or ITU R-P.1411), and application traffic (e.g. Basic Safety Message) and/or routing traffic (e.g. DSDV, AODV, OLSR, or DSR) under either a synthetic highway scenario (i.e. a random waypoint mobility model) or by playing back mobility trace files (i.e. ns-2 movement files).

The script draws from several ns-3 examples, including: 
/examples/routing/manet-routing-compare.cc
/src/propagation/model/itu-r-1411-los-propagation-loss-model.cc
/src/mobility/examples/ns2-mobility-trace.cc
/src/wave/examples/wave-simple-80211p.cc
 
The script allows many parameters to be modified and includes two predefined scenarios. 

By default **scenario=1** runs for 10 simulated seconds with 40 nodes (i.e. vehicles) moving according to RandomWaypointMobilityModel with a speed of 20 m/s and no pause time within a 300x1500 m region. The WiFi is 802.11p with continuous access to a 10 MHz Control Channel (CH) for all traffic.  All nodes transmit a 200-byte safety message 10 times per second at 6 Mbps. Additionally, all nodes (optionally) attempt to continuously route 64-byte packets at an application rate of 2.048 Kbps to one of 10 other nodes, selected as sink nodes. The default routing protocol is AODV and the Two-Ray Ground loss model is used. The transmit power is set to 20 dBm and the transmission range for safety message packet delivery is 145 m.


**Scenario 2** plays back vehicular trace files in ns-2 movement format, and are taken from: http://www.lst.inf.ethz.ch/research/ad-hoc/car-traces/. This scenario is 300 simulation seconds of 99 vehicles respectively within the Unterstrass section of Zurich Switzerland that travel based on models derived from real traffic data.  Note that these scenarios can require a lot of clock time to complete.

All parameters can be changed from their defaults (see --help) and changing simulation parameters can have dramatic impact on network performance.

Several items can be output:
* a CSV file of data reception statistics, output once per second
* final statistics, in a CSV file
* dump of routing tables at 5 seconds into the simulation
* ASCII trace file
* PCAP trace files for each node

Simulation scenarios can be defined and configuration settings can be saved using config-store (raw text) which can they be replayed again. This is an easy way to define and save the settings for a scenario, and then re-execute the same scenario exactly, or to set up several different simulation scenarios. For example, to set up a scenario and save the configuration as "scenario1.txt":

```sh
./waf --run "vanet-routing-compare --scenario=1 --saveconfig=scenario1.txt"
```

Then, to re-play the scenario using the save configuration settings:
```sh
./waf --run "vanet-routing-compare --loadconfig=scenario1.txt"
```

## Class Diagram:

main()
    +--uses-- VanetRoutingExperiment
        +--is_a--- WifiApp
        +--uses--- ConfigStoreHelper
        +--has_a-- WaveBsmHelper
        |            +--has_a-- WaveBsmStats
        +--has_a-- RoutingHelper
        |            +--has_a--RoutingStats
        +--has_a-- WifiPhyStats

## class : WifiApp

This function defines the main methods that should be present or redined in `VanetRoutingExperiment`. All the methods are empty except the method `Simulate` in order to run the simulation. The main methods that should be declared are:

```cpp
class WifiApp
{
public:
  WifiApp (); // Constructor
  virtual ~WifiApp (); //Destructor
  /**
   * \brief Enacts simulation of an ns-3 wifi application
   * \param argc program arguments count
   * \param argv program arguments
   * \return none
   */
  void Simulate (int argc, char **argv);

protected:
  /**
   * \brief Sets default attribute values
   * \return none
   */
  virtual void SetDefaultAttributeValues ();
  /**
   * \brief Process command line arguments
   * \param argc program arguments count
   * \param argv program arguments
   * \return none
   */
  virtual void ParseCommandLineArguments (int argc, char **argv);
  /**
   * \brief Configure nodes
   * \return none
   */
  virtual void ConfigureNodes ();
  /**
   * \brief Configure channels
   * \return none
   */
  virtual void ConfigureChannels ();
  /**
   * \brief Configure devices
   * \return none
   */
  virtual void ConfigureDevices ();
  /**
   * \brief Configure mobility
   * \return none
   */
  virtual void ConfigureMobility ();
  /**
   * \brief Configure applications
   * \return none
   */
  virtual void ConfigureApplications ();
  /**
   * \brief Configure tracing
   * \return none
   */
  virtual void ConfigureTracing ();
  /**
   * \brief Run the simulation
   * \return none
   */
  virtual void RunSimulation ();
  /**
   * \brief Process outputs
   * \return none
   */
  virtual void ProcessOutputs ();
};
```

As introduced earlier all the methods are empty except the method `Simulate()`. The main idea is to describe in what order the simulation should be run. As following:

```cpp
void WifiApp::Simulate (int argc, char **argv)
{
  SetDefaultAttributeValues ();
  ParseCommandLineArguments (argc, argv);
  /*
   * Create the nodes
   */
  ConfigureNodes (); 
  /*
   * Call the SetupAdhocDevices and 
   * intilize the lossModel (m_lossModel)
   * and the MAC layer.
   * In fact all the configuration of the 
   * devices is done within this method
   */
  ConfigureChannels (); 
  /*
   * Add only callbacks for tracing. 
   * Not all callbakcs are working.
   * For example the TxDrop, RxDrop 
   * are not working yet.
   */
  ConfigureDevices ();
  /*
   * Call the method SetupAdhocMobilityNodes.
   * 
   */
  ConfigureMobility ();
  /*
   * Set SetupRoutingMessages and SetupWaveMessages 
   */
  ConfigureApplications ();
  ConfigureTracing ();
  RunSimulation ();
  ProcessOutputs ();
}
```



## class : VanetRoutingExperiment

This class implements a wifi app `WifiApp` that allows VANET routing experiments to be simulated. The main functions are:

* **Question** Why declare again the methods inherited from WifiApp? 

```cpp
class VanetRoutingExperiment : public WifiApp
{
public:
  /**
   * \brief Sets default attribute values
   * \return none
   * In fact this function do nothing since all defaults attributes values are hanfled in the constructor...
   */
  virtual void SetDefaultAttributeValues (); 
  /**
   * \brief Process command line arguments
   * \param argc program arguments count
   * \param argv program arguments
   * \return none
   * This function call the function CommandSetup and SetupScenario in order to get all the command line arguments...
   */
  virtual void ParseCommandLineArguments (int argc, char **argv);
  /**
   * \brief Configure nodes
   * \return none
   */
  virtual void ConfigureNodes ();
  /**
   * \brief Configure channels
   * \return none
   */
  virtual void ConfigureChannels ();
  /**
   * \brief Configure devices
   * \return none
   */
  virtual void ConfigureDevices ();
  /**
   * \brief Configure mobility
   * \return none
   */
  virtual void ConfigureMobility ();
  /**
   * \brief Configure applications
   * \return none
   */
  virtual void ConfigureApplications ();
  /**
   * \brief Configure tracing
   * \return none
   */
  virtual void ConfigureTracing ();
  /**
   * \brief Run the simulation
   * \return none
   */
  virtual void RunSimulation (); 
  /**
   * \brief Process outputs
   * \return none
   */
  virtual void ProcessOutputs (); // ===> All the methods declared until now are inherited from WifiApp... 
  /**
   * \brief Run the simulation
   * \return none
   */
  void Run (); // What is the difference between RunSimulation() and Run() ???
  /**
   * \brief Run the simulation
   * \param argc command line argument count
   * \param argv command line parameters
   * \return none
   */
  void CommandSetup (int argc, char **argv);
  /**
   * \brief Checks the throughput and outputs summary to CSV file1.
   * This is scheduled and called once per second
   * \return none
   */
  void CheckThroughput ();

  /**
   * \brief Set up log file
   * \return none
   */
  void SetupLogFile ();

  /**
   * \brief Set up logging
   * \return none
   */
  void SetupLogging ();

  /**
   * \brief Configure default attributes
   * \return none
   */
  void ConfigureDefaults ();

  /**
   * \brief Set up the adhoc mobility nodes
   * \return none
   */
  void SetupAdhocMobilityNodes ();

  /**
   * \brief Set up the adhoc devices
   * \return none
   */
  void SetupAdhocDevices ();

  /**
   * \brief Set up generation of IEEE 1609 WAVE messages,
   * as a Basic Safety Message (BSM).  The BSM is typically
   * a ~200-byte packets broadcast by all vehicles at a nominal
   * rate of 10 Hz
   * \return none
   */
  void SetupWaveMessages ();

  /**
   * \brief Set up generation of packets to be routed
   * through the vehicular network
   * \return none
   */
  void SetupRoutingMessages ();

  /**
   * \brief Set up a prescribed scenario
   * \return none
   */
  void SetupScenario ();

  /**
   * \brief Write the header line to the CSV file1
   * \return none
   */
  void WriteCsvHeader ();

  /**
   * \brief Set up configuration parameter from the global variables
   * \return none
   */
  void SetConfigFromGlobals ();

  /**
   * \brief Set up the global variables from the configuration parameters
   * \return none
   */
  void SetGlobalsFromConfig ();

  /**
   * Course change function
   * \param os the output stream
   * \param context trace source context (unused)
   * \param mobility the mobility model
   */
  static void
  CourseChange (std::ostream *os, std::string context, Ptr<const MobilityModel> mobility);

  uint32_t m_port; ///< port
  std::string m_CSVfileName; ///< CSV file name
  std::string m_CSVfileName2; ///< CSV file name
  uint32_t m_nSinks; ///< number of sinks
  std::string m_protocolName; ///< protocol name
  double m_txp; ///< distance
  bool m_traceMobility; ///< trace mobility
  uint32_t m_protocol; ///< protocol

  uint32_t m_lossModel; ///< loss model
  uint32_t m_fading; ///< fading
  std::string m_lossModelName; ///< loss model name

  std::string m_phyMode; ///< phy mode
  uint32_t m_80211mode; ///< 80211 mode

  std::string m_traceFile; ///< trace file 
  std::string m_logFile; ///< log file
  uint32_t m_mobility; ///< mobility
  uint32_t m_nNodes; ///< number of nodes
  double m_TotalSimTime; ///< total sim time
  std::string m_rate; ///< rate
  std::string m_phyModeB; ///< phy mode
  std::string m_trName; ///< trace file name
  int m_nodeSpeed; ///< in m/s
  int m_nodePause; ///< in s
  uint32_t m_wavePacketSize; ///< bytes
  double m_waveInterval; ///< seconds
  int m_verbose; ///< verbose
  std::ofstream m_os; ///< output stream
  NetDeviceContainer m_adhocTxDevices; ///< adhoc transmit devices
  Ipv4InterfaceContainer m_adhocTxInterfaces; ///< adhoc transmit interfaces
  uint32_t m_scenario; ///< scenario
  double m_gpsAccuracyNs; ///< GPS accuracy
  double m_txMaxDelayMs; ///< transmit maximum delay
  int m_routingTables; ///< routing tables
  int m_asciiTrace; ///< ascii trace
  int m_pcap; ///< PCAP
  std::string m_loadConfigFilename; ///< load config file name
  std::string m_saveConfigFilename; ///< save configi file name

  WaveBsmHelper m_waveBsmHelper; ///< helper
  Ptr<RoutingHelper> m_routingHelper; ///< routing helper
  Ptr<WifiPhyStats> m_wifiPhyStats; ///< wifi phy statistics
  int m_log; ///< log
  /// used to get consistent random numbers across scenarios
  int64_t m_streamIndex;
  NodeContainer m_adhocTxNodes; ///< adhoc transmit nodes --> Nodes
  double m_txSafetyRange1; ///< range 1
  double m_txSafetyRange2; ///< range 2
  double m_txSafetyRange3; ///< range 3
  double m_txSafetyRange4; ///< range 4
  double m_txSafetyRange5; ///< range 5
  double m_txSafetyRange6; ///< range 6
  double m_txSafetyRange7; ///< range 7
  double m_txSafetyRange8; ///< range 8
  double m_txSafetyRange9; ///< range 9
  double m_txSafetyRange10; ///< range 10
  std::vector <double> m_txSafetyRanges; ///< list of ranges
  std::string m_exp; ///< exp
  int m_cumulativeBsmCaptureStart; ///< capture start
};

```


## class: RoutingHelper

The RoutingHelper class generates routing data between nodes (vehicles) and uses the RoutingStats class to collect statistics on routing data (application-data packet and byte counts). A routing protocol is configured, and all nodes attempt to send (i.e. route) small packets to another node, which acts as data sinks.  Not all nodes act as data sinks. for the vehicular network Main methods are list bellow.

### Variables

```cpp
  double m_TotalSimTime;        ///< seconds
  uint32_t m_protocol;          ///< routing protocol; 0=NONE, 1=OLSR, 2=AODV, 3=DSDV, 4=DSR
  uint32_t m_port;              ///< port
  uint32_t m_nSinks;            ///< number of sink nodes (< all nodes)
  int m_routingTables;          ///< dump routing table (at t=5 sec).  0=No, 1=Yes
  RoutingStats routingStats;    ///< routing statistics
  std::string m_protocolName;   ///< protocol name
  int m_log;                    ///< log
```


### Install method:
```cpp
  /**
   * \brief Installs routing funcationality on nodes and their
   * devices and interfaces.
   * \param c node container
   * \param d net device container
   * \param i IPv4 interface container
   * \param totalTime the total time that nodes should attempt to
   * route data ==> After this time the node drop the packet ???
   * \param protocol the routing protocol (1=OLSR;2=AODV;3=DSDV;4=DSR)
   * \param nSinks the number of nodes which will act as data sinks
   * \param routingTables dump routing tables at t=5 seconds (0=no;1=yes)
   * \return none
   */
    void Install (NodeContainer & c,
                NetDeviceContainer & d,
                Ipv4InterfaceContainer & i,
                double totalTime,
                int protocol,
                uint32_t nSinks,
                int routingTables);
    
    ...
    ...

    void
    RoutingHelper::Install (NodeContainer & c,
                            NetDeviceContainer & d,
                            Ipv4InterfaceContainer & i,
                            double totalTime,
                            int protocol,
                            uint32_t nSinks,
                            int routingTables)
    {
      // init some local RoutingHelper class variables
      // used in other methods
      m_TotalSimTime = totalTime;
      m_protocol = protocol;
      m_nSinks = nSinks;
      m_routingTables = routingTables;

      // protocol
      SetupRoutingProtocol (c);
      // address
      AssignIpAddresses (d, i);
      // ???
      SetupRoutingMessages (c, i);
    }
```

  /**
   * \brief Trace the receipt of an on-off-application generated packet
   * \param context this object
   * \param packet a received packet
   * \return none
   */
  void OnOffTrace (std::string context, Ptr<const Packet> packet);

  /**
   * \brief Returns the RoutingStats instance
   * \return the RoutingStats instance
   */
  RoutingStats & GetRoutingStats ();

  /**
   * \brief Enable/disable logging
   * \param log non-zero to enable logging
   * \return none
   */
  void SetLogging (int log);

private:
  /**
   * \brief Sets up the protocol protocol on the nodes
   * \param c node container
   * \return none
   */
  void SetupRoutingProtocol (NodeContainer & c);

  /**
   * \brief Assigns IPv4 addresses to net devices and their interfaces
   * \param d net device container
   * \param adhocTxInterfaces IPv4 interface container
   * \return none
   */
  void AssignIpAddresses (NetDeviceContainer & d,
                          Ipv4InterfaceContainer & adhocTxInterfaces);

  /**
   * \brief Sets up routing messages on the nodes and their interfaces
   * \param c node container
   * \param adhocTxInterfaces IPv4 interface container
   * \return none
   */
  void SetupRoutingMessages (NodeContainer & c,
                             Ipv4InterfaceContainer & adhocTxInterfaces);

  /**
   * \brief Sets up a routing packet for tranmission
   * \param addr destination address
   * \param node source node
   * \return Socket to be used for sending/receiving a routed data packet
   */
  Ptr<Socket> SetupRoutingPacketReceive (Ipv4Address addr, Ptr<Node> node);

  /**
   * \brief Process a received routing packet
   * \param socket the receiving socket
   * \return none
   */
  void ReceiveRoutingPacket (Ptr<Socket> socket);

  double m_TotalSimTime;        ///< seconds
  uint32_t m_protocol;       ///< routing protocol; 0=NONE, 1=OLSR, 2=AODV, 3=DSDV, 4=DSR
  uint32_t m_port;           ///< port
  uint32_t m_nSinks;              ///< number of sink nodes (< all nodes)
  int m_routingTables;      ///< dump routing table (at t=5 sec).  0=No, 1=Yes
  RoutingStats routingStats; ///< routing statistics
  std::string m_protocolName; ///< protocol name
  int m_log; ///< log
};
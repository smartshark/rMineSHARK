package de.ugoe.cs.smartshark.rMineSHARK.util;

import org.apache.commons.cli.*;

/**
 * This class provides parameter to the entire application. It reads the command line
 * arguments and assigns the options to appropriate parameter. To avoid multiple instances
 * the singleton pattern is used.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class Parameter {
  private static Parameter instance;
  final private String version = "0.10";

  private String project;

  // boolean parameter
  private boolean showVersion;
  private boolean showHelp;
  private boolean ssl;

  // database parameter
  private String dbName;
  private String dbUser;
  private String dbPassword;
  private String dbHostname;
  private int dbPort;
  private String dbAuthentication;

  // debug parameter
  private String debugLevel;

  private boolean initialized = false;
  private final OptionHandler optionsHandler;

  private Parameter() {
    optionsHandler = new OptionHandler();
  }

  public static synchronized Parameter getInstance() {
    if (instance == null) {
      instance = new Parameter();
    }
    return instance;
  }

  public void init(String[] args) {
    CommandLine cmd = parseCommandLineArguments(args);

    project = cmd.getOptionValue("n");
    showHelp = cmd.hasOption("h");
    ssl = cmd.hasOption("ssl");
    dbName = cmd.getOptionValue("DB", "smartshark");
    dbUser = cmd.getOptionValue("U", "");
    dbPassword = cmd.getOptionValue("P", "");
    dbHostname = cmd.getOptionValue("H", "localhost");
    dbPort = Integer.parseInt(cmd.getOptionValue("p", "27017"));
    dbAuthentication = cmd.getOptionValue("a", "");
    debugLevel = cmd.getOptionValue("d", "ERROR");

    initialized = true;
  }

  public String getProjectName() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return project;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public String getDbName() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return dbName;
  }

  public boolean isShowHelp() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return showHelp;
  }

  public boolean isSsl() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return ssl;
  }

  public String getDbUser() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return dbUser;
  }

  public String getDbPassword() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return dbPassword;
  }

  public String getDbHostname() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return dbHostname;
  }

  public int getDbPort() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return dbPort;
  }

  public String getDbAuthentication() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return dbAuthentication;
  }

  public String getDebugLevel() {
    if (!isInitialized()) {
      System.out.println("The current parameter instance is not initialized!");
    }
    return debugLevel;
  }

  private CommandLine parseCommandLineArguments(String[] args) {
    CommandLineParser parser =  new DefaultParser();
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(optionsHandler.getOptions(), args);
      if (commandLine.hasOption("h") ) {
        printHelp();
        System.exit(0);
      } else if (commandLine.hasOption("v")) {
        printVersion();
        System.exit(0);
      }
    } catch (ParseException e) {
      System.err.println("ERROR: " + e.getMessage());
      printHelp();
      System.exit(1);
    }
    return commandLine;
  }

  public void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "refSHARK", optionsHandler.getOptions() );
  }

  public void printVersion() {
    System.out.println("This is refSHARK version " + version);
  }

}

package de.ugoe.cs.smartshark.rMineSHARK.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * In this class options representing all possible program arguments are defined.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class OptionHandler {
  private final Options options;

  public OptionHandler() {
    options = new Options();
    initOptions();
  }

  public Options getOptions() {
    return options;
  }

  private void initOptions() {
    Option option;

    option = new Option("n", "Name of the project. Required.");
    option.setRequired(false);
    option.setLongOpt("project");
    option.setArgs(1);
    option.setArgName("project");
    options.addOption(option);

    option = new Option("h", "Shows the help page for this command.");
    option.setRequired(false);
    option.setLongOpt("help");
    option.setArgs(0);
    options.addOption(option);

    option = new Option("U", "The mongodb user name. Default: None.");
    option.setRequired(false);
    option.setLongOpt("db-user");
    option.setArgs(1);
    option.setArgName("db_user");
    options.addOption(option);

    option = new Option("P", "The mongodb user password. Default: None.");
    option.setRequired(false);
    option.setLongOpt("db-password");
    option.setArgs(1);
    option.setArgName("db_password");
    options.addOption(option);

    option = new Option("DB", "The database name (e.g., name of the mongodb database that should be used). Default name: 'smartshark'.");
    option.setRequired(false);
    option.setLongOpt("db-database");
    option.setArgs(1);
    option.setArgName("db_name");
    options.addOption(option);

    option = new Option("H", "The hostname, where the datastore runs on. Default: 'localhost'.");
    option.setRequired(false);
    option.setLongOpt("db-hostname");
    option.setArgs(1);
    option.setArgName("hostname");
    options.addOption(option);

    option = new Option("p", "The port, where the datastore runs on. Default: 27017.");
    option.setRequired(false);
    option.setLongOpt("db-port");
    option.setArgs(1);
    option.setArgName("port");
    options.addOption(option);

    option = new Option("a", "The name of the authentication database. Default: None.");
    option.setRequired(false);
    option.setLongOpt("db-authentication");
    option.setArgs(1);
    option.setArgName("db_authentication");
    options.addOption(option);

    option = new Option("ssl", "Enables ssl for the connection to the mongodb. Default: False.");
    option.setRequired(false);
    options.addOption(option);

  }
}

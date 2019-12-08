package de.ugoe.cs.smartshark.rMineSHARK;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.ugoe.cs.smartshark.model.Project;
import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.SmartSharkRefactorDetection;
import de.ugoe.cs.smartshark.rMineSHARK.util.Parameter;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.List;

/**
*
 */
public class rMineShark {

	private static MongoClient client;

	public static void main(String[] args) throws Exception {
		Parameter param = Parameter.getInstance();
		param.init(args);

		final Datastore datastore = createDatastore();

		setLogLevel();

		Query<Project> projects = datastore.createQuery(Project.class);
		projects.and(
		projects.criteria("name").equal(param.getProjectName())
		);

		for (Project project : projects) {
			System.out.println(project.getName());
			SmartSharkRefactorDetection detection = new SmartSharkRefactorDetection(project, datastore, client);
			detection.execute();
		}
	}

	private static Datastore createDatastore() {
		Morphia morphia = new Morphia();
		morphia.mapPackage("de.ugoe.cs.smartshark.refshark.model");
		Datastore datastore = null;

		try {
			if (Parameter.getInstance().getDbPassword().isEmpty()) {
				datastore = morphia.createDatastore(
						new MongoClient(Parameter.getInstance().getDbHostname(), Parameter.getInstance().getDbPort()),
						Parameter.getInstance().getDbName());
			} else {
				ServerAddress addr = new ServerAddress(Parameter.getInstance().getDbHostname(),
						Parameter.getInstance().getDbPort());
				List<MongoCredential> credentialsList = Lists.newArrayList();
				MongoCredential credential = MongoCredential.createCredential(Parameter.getInstance().getDbUser(),
						Parameter.getInstance().getDbAuthentication(),
						Parameter.getInstance().getDbPassword().toCharArray());
				credentialsList.add(credential);
				client = new MongoClient(addr, credentialsList);
				datastore = morphia.createDatastore(client, Parameter.getInstance().getDbName());
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}

		return datastore;
	}

	private static void setLogLevel() {
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		String level = Parameter.getInstance().getDebugLevel();

		switch (level) {
		case "INFO":
			root.setLevel(Level.INFO);
			break;
		case "DEBUG":
			root.setLevel(Level.DEBUG);
			break;
		case "WARNING":
			root.setLevel(Level.WARN);
			break;
		case "ERROR":
			root.setLevel(Level.ERROR);
			break;
		default:
			root.setLevel(Level.DEBUG);
			break;
		}
	}

}

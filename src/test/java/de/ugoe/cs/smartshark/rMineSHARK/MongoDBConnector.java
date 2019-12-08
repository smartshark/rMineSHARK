package de.ugoe.cs.smartshark.rMineSHARK;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.ugoe.cs.smartshark.model.VCSSystem;
import de.ugoe.cs.smartshark.rMineSHARK.local.LocalSmartSHARKRefactorDetection;
import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.SmartSharkRefactorDetection;
import de.ugoe.cs.smartshark.rMineSHARK.util.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import de.ugoe.cs.smartshark.model.Project;

public class MongoDBConnector {

    //   private static String[] projectToExecute = {"ant-ivy", "archiva", "calcite", "cayenne", "commons-bcel", "commons-beanutils", "commons-codec", "commons-collections", "commons-compress", "commons-configuration", "commons-dbcp", "commons-digester", "commons-io", "commons-jcs", "commons-jexl", "commons-lang", "commons-math", "commons-net", "commons-scxml", "commons-validator", "commons-vfs", "deltaspike", "eagle", "giraph", "gora", "jspwiki", "knox", "kylin", "lens", "mahout", "manifoldcf", "nutch", "opennlp", "parquet-mr", "santuario-java", "systemml", "tika", "wss4j"};

    private static String[] projectToExecute = {"santuario-java"};

    public static void main(String args[]) throws Exception {
        // Telling morphia where to find the models
        final Morphia morphia = new Morphia();
        morphia.mapPackage("de.ugoe.cs.smartshark.model");

        // Creating a connection to the database
        //
        MongoClientURI uri = new MongoClientURI("mongodb://userser@mondfo/?authSource=smartshark");
        MongoClient mongoClient = new MongoClient(uri);
        final Datastore datastore = morphia.createDatastore(mongoClient, "smartshark");


        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        for (String project : projectToExecute) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Query<Project> projects = datastore.createQuery(Project.class);
                        projects.and(
                                projects.criteria("name").equal(project) // oisafe ist ein gutes Testprojekt oder gora
                        );

                        // Project 1:n -> VCSSystem 1:n -> 1:n Commit 1:n -> FileAction 1:n -> Hunks
                        // Commits über revision hash erkennen?
                        // Plan iteriere mit dem Tool einmal durch das Project -> wenn Commit erkannt, dann suche passenden Commit
                        for (Project project2 : projects) {
                            System.out.println(project2.getName());
                            SmartSharkRefactorDetection detection = new LocalSmartSHARKRefactorDetection(project2, datastore, mongoClient);
                            detection.execute();

                            // lineCountForProject();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        System.out.println(project + " failed");
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);


    }


    private static void lineCountForProject(VCSSystem system, Datastore datastore, Project project) {
        LineCounter lineCounter = new LineCounter(system, datastore, project.getName());
        int count = lineCounter.getLinesOfAllCommits();
        Logger.log("All lines " + count);
    }
}

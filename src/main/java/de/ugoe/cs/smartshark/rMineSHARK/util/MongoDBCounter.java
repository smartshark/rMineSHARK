package de.ugoe.cs.smartshark.rMineSHARK.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import de.ugoe.cs.smartshark.model.Project;
import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.SmartSharkRefactorDetection;

public class MongoDBCounter {

	public static void main(String args[]) throws Exception
	{		
		// Telling morphia where to find the models
		final Morphia morphia = new Morphia();
		morphia.mapPackage("de.ugoe.cs.smartshark.model");

		// Creating a connection to the database
		MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/");
		MongoClient mongoClient = new MongoClient(uri);
		final Datastore datastore = morphia.createDatastore(mongoClient, "smartshark_backup");
		
		Query<Project> projects = datastore.createQuery(Project.class);
		projects.and(
		projects.criteria("name").equal("gora") // oisafe ist ein gutes Testprojekt oder gora
		);
	
		// oisafe 10,9%
		// gora 8,38%
		
		// Project 1:n -> VCSSystem 1:n -> 1:n Commit 1:n -> FileAction 1:n -> Hunks
		// Commits über revision hash erkennen?
		// Plan iteriere mit dem Tool einmal durch das Project -> wenn Commit erkannt, dann suche passenden Commit
		for (Project project : projects) {
			System.out.println(project.getName());
			Query<Project> counter = datastore.createQuery(Project.class);
			counter.and(
					counter.criteria("name").equal("gora") // oisafe ist ein gutes Testprojekt oder gora
			);
		}
		
	}
}

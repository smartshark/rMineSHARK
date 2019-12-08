package de.ugoe.cs.smartshark.rMineSHARK;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.ugoe.cs.smartshark.rMineSHARK.util.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import de.ugoe.cs.smartshark.model.Commit;
import de.ugoe.cs.smartshark.model.File;
import de.ugoe.cs.smartshark.model.FileAction;
import de.ugoe.cs.smartshark.model.Hunk;
import de.ugoe.cs.smartshark.model.VCSSystem;

public class LineCounter {

	private final Datastore datastore;
	private VCSSystem system;
	private String project;

	private	String[] HEADERS = { "project", "count_hunks", "count_files", "count_lines"};
	private Map<String, Integer> mapper = new TreeMap();

	public LineCounter(VCSSystem system, Datastore datastore, String project) {
		this.datastore = datastore;
		this.system = system;
		this.project = project;
	}

	public int getLinesOfAllCommits()
	{
		int countLines = 0;
		int countFiles = 0;
		int countHunks = 0;
		List<Commit> commits = findAllCommits();
		for (Commit commit : commits) {
			for (String key: commit.getLabels().keySet()) {
				if("true".equals(commit.getLabels().get(key)))
				{
					if(mapper.containsKey(key))
					{
						mapper.put(key,mapper.get(key)+ 1);
					} else {
						mapper.put(key, 1);
					}
				}
			}
			List<FileAction> fileActions = findAllFileAction(commit);
			for (FileAction fileAction : fileActions) {
				File file = findFile(fileAction);
				if(file.getPath().trim().endsWith(".java")) {
					countFiles++;
				List<Hunk> hunks = findAllHunks(fileAction);
				for (Hunk hunk : hunks) {
					countHunks++;
					countLines += hunk.getNewLines();
					countLines += hunk.getOldLines();
				}
				}
			}
		}

		try {
			String[] headersJoined = (String[]) ArrayUtils.addAll(HEADERS, mapper.keySet().toArray());
			FileWriter in = new FileWriter("results_counter.csv", true);
			CSVPrinter printer = CSVFormat.DEFAULT.print(in);
			List<String> toPrint = new ArrayList<>();
			toPrint.add(project);
			toPrint.add(String.valueOf(countHunks));
			toPrint.add(String.valueOf(countFiles));
			toPrint.add(String.valueOf(countLines));

			for (String key : mapper.keySet()) {
			//	toPrint.add(String.valueOf(mapper.get(key)));
			}

			printer.printRecord(toPrint);
			in.close();
		} catch ( Exception e)
		{

		}

		return countLines;
	}

	private List<Hunk> findAllHunks(FileAction fileAction) {
		Query<Hunk> hunks = datastore.createQuery(Hunk.class);
		hunks.and(hunks.criteria("fileActionId").equal(fileAction.getId()));
		return hunks.asList();
	}

	private File findFile(FileAction fileAction) {
		Query<File> file = datastore.createQuery(File.class);
		file.and(file.criteria("id").equal(fileAction.getFileId()));
		return file.get();
	}

	private List<FileAction> findAllFileAction(Commit commit) {
		Query<FileAction> fileActions = datastore.createQuery(FileAction.class);
		fileActions.and(fileActions.criteria("commitId").equal(commit.getId()));
		return fileActions.asList();
	}

	private List<Commit> findAllCommits() {
		Query<Commit> commit = datastore.createQuery(Commit.class);
		commit.and(commit.criteria("vcSystemId").equal(this.system.getId()));
		return commit.asList();
	}
}

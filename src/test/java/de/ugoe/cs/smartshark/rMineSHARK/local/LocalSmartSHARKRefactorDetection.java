package de.ugoe.cs.smartshark.rMineSHARK.local;

import com.mongodb.MongoClient;
import de.ugoe.cs.smartshark.model.Commit;
import de.ugoe.cs.smartshark.model.Project;
import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.SmartSharkRefactorDetection;
import de.ugoe.cs.smartshark.rMineSHARK.util.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.mongodb.morphia.Datastore;
import org.refactoringminer.api.Refactoring;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LocalSmartSHARKRefactorDetection extends SmartSharkRefactorDetection {

    private String[] HEADERS = {"project", "hunks_removed", "files_removed", "lines_removed"};
    private Map<String, Integer> mapper = new TreeMap();

    public LocalSmartSHARKRefactorDetection(Project project, Datastore datastore, MongoClient mongoClient) {
        super(project, datastore, mongoClient);
    }


    @Override
    protected int handleCommitAndRefactoring(Commit commit, List<Refactoring> refactorings) {
        Logger.log("Commit found!");
        for (String key : commit.getLabels().keySet()) {
            if ("true".equals(commit.getLabels().get(key))) {
                if (mapper.containsKey(key)) {
                    mapper.put(key, mapper.get(key) + 1);
                } else {
                    mapper.put(key, 1);
                }
            }
        }
        return super.handleCommitAndRefactoring(commit, refactorings);
    }

    protected void shutdownHook() throws Exception {
        Logger.log("Print label stats");
        for (String key : mapper.keySet()) {
            System.out.println(key + ": " + mapper.get(key));
        }
        Logger.log("Hunks removed " + getHunksRemoved());
        Logger.log("Complete file actions as refacorting removed " + getCompleteFile());
        Logger.log("All lines remove: " + getCompleteLines());

        String[] headersJoined = (String[]) ArrayUtils.addAll(HEADERS, mapper.keySet().toArray());
        FileWriter in = new FileWriter("results.csv", true);
        CSVPrinter printer = CSVFormat.DEFAULT.print(in);
        List<String> toPrint = new ArrayList<>();
        toPrint.add(getP().getName());
        toPrint.add(String.valueOf(getHunksRemoved()));
        toPrint.add(String.valueOf(getCompleteFile()));
        toPrint.add(String.valueOf(getCompleteLines()));

        printer.printRecord(toPrint);
        in.close();
    }
}

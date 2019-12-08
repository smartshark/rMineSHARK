package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK;

import java.io.FileWriter;
import java.util.*;

import com.mongodb.MongoClient;
import de.ugoe.cs.smartshark.rMineSHARK.util.Common;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jgit.lib.Repository;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import de.ugoe.cs.smartshark.model.Commit;
import de.ugoe.cs.smartshark.model.File;
import de.ugoe.cs.smartshark.model.FileAction;
import de.ugoe.cs.smartshark.model.Hunk;
import de.ugoe.cs.smartshark.model.Project;
import de.ugoe.cs.smartshark.model.VCSSystem;
import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.internal.RefactoringTypeMatcher;
import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model.RefactoringHunk;
import de.ugoe.cs.smartshark.rMineSHARK.util.Logger;
import gr.uom.java.xmi.LocationInfo;

public class SmartSharkRefactorDetection {

    // Refactorings for oisafe
    // 3fe952c0c6ccd6371cbf727982a4e7f09e705707 ->
    // https://github.com/openintents/safe/commit/ed0c657a64f7c528722528320aa39b44d2addb79
    // ed0c657a64f7c528722528320aa39b44d2addb79
    // ef4a92c27d01a81534ce7f5368e864d0d0f25909

    private static final String TOOL_NAME = "rMiner";
    private final MongoClient mongoClient;

    private int hunksRemoved = 0;
    private int completeFile = 0;
    private int completeLines = 0;

    private Project p;
    private final Datastore datastore;
    private List<de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model.Refactoring> refactoringsToStore;

    public SmartSharkRefactorDetection(Project project, Datastore datastore, MongoClient mongoClient) {
        this.p = project;
        this.datastore = datastore;
        refactoringsToStore = new ArrayList<>();
        this.mongoClient = mongoClient;
    }

    public void execute() throws Exception {
        Query<VCSSystem> systems = datastore.createQuery(VCSSystem.class);
        systems.and(systems.criteria("projectId").equal(p.getId()));

        Common.loadRepoFromMongoDB(p.getName(), mongoClient);

        for (VCSSystem vcsSystem : systems) {
            performForVCSSystem(vcsSystem);
        }

        store();
    }


    private void performForVCSSystem(VCSSystem system) throws Exception {
        Logger.log("Analyze " + system.getUrl());
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = gitService.openRepository("tmp/" + p.getName());

        // Commit based refactoring

        // miner.detectAtCommit(repo, system.getUrl() + ".git",
        // "ed0c657a64f7c528722528320aa39b44d2addb79",
        // new RefactoringHandler() {
        // @Override
        // public void handle(RevCommit commitData, List<Refactoring>
        // refactorings) {
        // log("Refactorings at " + commitData.getId().getName());
        // // no refactoring -> return
        // if (refactorings.size() == 0)
        // return;
        // // find commit in mongo db
        // Commit commit = findCommitForId(commitData.getId().getName());
        // if (commit != null) {
        // log("Commit found!");
        // handleCommitAndRefactoring(commit, refactorings);
        // }
        //
        // }
        //
        // });

        // Null to detect for *all* branches
        miner.detectAll(repo, (String) null, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                Logger.log("Refactorings at " + commitId);
                // no refactoring -> return
                if (refactorings.size() == 0)
                    return;
                // find commit in mongo db
                Commit commit = findCommitForId(commitId);
                if (commit != null) {
                    handleCommitAndRefactoring(commit, refactorings);
                }
            }

            public void handleException(String commitId, Exception e) {
                e.printStackTrace();

                Logger.log("Exeception for commit " + commitId);
            }

        });
        shutdownHook();
    }

    protected void shutdownHook() throws Exception {
        // empty to override in if needed
    }

    private Commit findCommitForId(String name) {
        Query<Commit> commit = datastore.createQuery(Commit.class);
        commit.and(commit.criteria("revisionHash").equal(name));
        return commit.get();
    }

    private List<FileActionFileHunksContainer> findFileActionFileHunksContainerForCommit(Commit commit) {
        List<FileActionFileHunksContainer> fileActionFileHunksContainers = new ArrayList<>();
        Query<FileAction> fileAction = datastore.createQuery(FileAction.class);
        fileAction.and(fileAction.criteria("commitId").equal(commit.getId()));
        for (FileAction fileAction2 : fileAction) {

            Query<File> file = datastore.createQuery(File.class);
            file.and(file.criteria("id").equal(fileAction2.getFileId()));

            Query<Hunk> hunks = datastore.createQuery(Hunk.class);
            hunks.and(hunks.criteria("file_action_id").equal(fileAction2.getId()));

            fileActionFileHunksContainers
                    .add(new FileActionFileHunksContainer(fileAction2, file.get(), hunks.asList()));
        }
        return fileActionFileHunksContainers;
    }


    // HookMethod
    protected int handleCommitAndRefactoring(Commit commit, List<Refactoring> refactorings) {

        List<FileActionFileHunksContainer> fileActions = findFileActionFileHunksContainerForCommit(commit);

        // 1. Remove refactorings from hunks

        for (Refactoring ref : refactorings) {
            System.out.println(ref.getInvolvedClassesAfterRefactoring());
            List<LocationInfo> infos = RefactoringTypeMatcher.getLocationInfoRefactoringSpecific(ref);
            if (infos != null && infos.size() > 0) {
                de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model.Refactoring refactoring = new de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model.Refactoring();
                refactoring.setCommitId(commit.getId());
                refactoring.setDetectionTool(SmartSharkRefactorDetection.TOOL_NAME);
                refactoring.setType(ref.getName());
                refactoring.setDescription(ref.toString());
                refactoring.setHunks(new ArrayList<>());

                for (LocationInfo locationInfo : infos) {
                    // Sucht den passenden Container, der alle Hunks für die Location (gemachted wird über die Datei) enthält
                    FileActionFileHunksContainer matchedContainer = getContainerForLocation(locationInfo, fileActions);
                    if (matchedContainer == null) {
                        Logger.log("No Match");
                        continue;
                    }
                    Logger.log("Match! " + ref.getRefactoringType());

                    // create hunk objects
                    int linesBefore = matchedContainer.getLines();
                    List<Hunk> affectedHunks = matchedContainer.getAffectedHunksBasedOnLocation(locationInfo);
                    for (Hunk hunk : affectedHunks) {
                        RefactoringHunk refactoringHunk = new RefactoringHunk();
                        refactoringHunk.setHunkId(hunk.getId());

                        refactoringHunk.setStartColumn(locationInfo.getStartColumn());
                        refactoringHunk.setStartLine(locationInfo.getStartLine());
                        refactoringHunk.setStartOffset(locationInfo.getStartOffset());

                        refactoringHunk.setEndColumn(locationInfo.getEndColumn());
                        refactoringHunk.setEndLine(locationInfo.getEndLine());
                        refactoringHunk.setEndOffset(locationInfo.getEndOffset());

                        refactoringHunk.setLength(locationInfo.getLength());
                        // add
                        refactoring.getHunks().add(refactoringHunk);
                    }
                    //
                    //matchedContainer.printAreas();
                    Logger.log("LocationInfo: " + locationInfo.getFilePath());
                    Logger.log(locationInfo.getStartLine() + "----" + locationInfo.getEndLine());
                    Logger.log("");
                    hunksRemoved += matchedContainer.removeHunksBasedOnLocationInfo(locationInfo);
                    int linesAfter = matchedContainer.getLines();
                    completeLines += Math.max(0, linesBefore - linesAfter);
                }
                refactoringsToStore.add(refactoring);
            }
        }

        // 2. Remove whitespaces from hunks

        for (FileActionFileHunksContainer fileActionFileHunksContainer : fileActions) {
            if (fileActionFileHunksContainer.getFile().getPath().endsWith(".java")) {
                fileActionFileHunksContainer.cleanWhitespace();
                fileActionFileHunksContainer.cleanComments();
                fileActionFileHunksContainer.cleanAnnotation();
            }
        }

        // 3. Statistics
        int countHunksWithOutRefacorting = 0;
        for (FileActionFileHunksContainer fileActionFileHunksContainer : fileActions) {
            if (fileActionFileHunksContainer.getFile().getPath().endsWith(".java")) {
                if (fileActionFileHunksContainer.getHunks().size() == 0) {
                    completeFile += 1;
                    Logger.log(
                            "complete file as refactoring: " + fileActionFileHunksContainer.getFile().getPath());
                }
                for (HunkMapperObject hunk : fileActionFileHunksContainer.getHunks()) {
                    countHunksWithOutRefacorting += hunk.getNewLines();
                }
            }
        }
        return hunksRemoved;
    }

    public Project getP() {
        return p;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    private FileActionFileHunksContainer getContainerForLocation(LocationInfo locationInfo,
                                                                 List<FileActionFileHunksContainer> fileActions) {
        Logger.log("Search for:" + locationInfo.getFilePath());
        for (FileActionFileHunksContainer fileActionFileHunksContainer : fileActions) {
            if (fileActionFileHunksContainer.getFile().getPath().equals(locationInfo.getFilePath())) {
                return fileActionFileHunksContainer;
            }
        }
        return null;
    }

    private void store() {
        Logger.log("Should store " + refactoringsToStore.size() + " objects");
        datastore.save(refactoringsToStore);
        Logger.log("Ojects saved");
    }

    public int getHunksRemoved() {
        return hunksRemoved;
    }

    public int getCompleteFile() {
        return completeFile;
    }

    public int getCompleteLines() {
        return completeLines;
    }
}

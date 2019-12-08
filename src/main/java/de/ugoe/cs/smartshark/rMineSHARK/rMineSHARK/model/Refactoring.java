package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

@Entity(noClassnameStored = true, value = "refactoring")
public class Refactoring {

	@Id
	@Property("_id")
	private ObjectId id;

	@Property("commit_id")
	private ObjectId commitId;

	@Property("detection_tool")
	private String detectionTool;

	@Property("type")
	private String type;

	@Property("description")
	private String description;

	@Embedded("hunks")
	private List<RefactoringHunk> hunks;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getCommitId() {
		return commitId;
	}

	public void setCommitId(ObjectId commitId) {
		this.commitId = commitId;
	}

	public String getDetectionTool() {
		return detectionTool;
	}

	public void setDetectionTool(String detectionTool) {
		this.detectionTool = detectionTool;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<RefactoringHunk> getHunks() {
		return hunks;
	}

	public void setHunks(List<RefactoringHunk> hunks) {
		this.hunks = hunks;
	}

}

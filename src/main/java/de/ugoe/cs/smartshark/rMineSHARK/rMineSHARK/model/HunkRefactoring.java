package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Property;

/**
 * A hunk can have multiple HunkRefactorings.
 * Uses the dectetion method of ..
 * 
 * @author blede
 *
 */
public class HunkRefactoring {

	@Property("_id")
	private ObjectId id;
	  
	@Property("hunk_id")
    private ObjectId hunkId;
	
	// Based on the new commit 
	@Property("start")
	private Integer start;

	@Property("end")
	private Integer end;
	
	private String type;

	public ObjectId getHunkId() {
		return hunkId;
	}

	public void setHunkId(ObjectId hunkId) {
		this.hunkId = hunkId;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}

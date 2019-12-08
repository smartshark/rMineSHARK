package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import de.ugoe.cs.smartshark.model.Hunk;

public class RefactoringHunk {

	@Property("hunk_id")
	private ObjectId hunkId;

	@Property("start_offset")	
	private int startOffset;

	@Property("end_offset")	
	private int endOffset;
	
	@Property("length")	
	private int length;

	@Property("start_line")	
	private int startLine;	

	@Property("start_column")	
	private int startColumn;
	
	@Property("end_line")	
	private int endLine;

	@Property("end_column")
	private int endColumn;	

	public ObjectId getHunkId() {
		return hunkId;
	}

	public void setHunkId(ObjectId hunkId) {
		this.hunkId = hunkId;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}

}

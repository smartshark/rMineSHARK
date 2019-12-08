package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ugoe.cs.smartshark.model.File;
import de.ugoe.cs.smartshark.model.FileAction;
import de.ugoe.cs.smartshark.model.Hunk;
import de.ugoe.cs.smartshark.rMineSHARK.util.Logger;
import gr.uom.java.xmi.LocationInfo;

public class FileActionFileHunksContainer {

	private FileAction action;

	private File file;

	private List<HunkMapperObject> hunks;

	public FileAction getAction() {
		return action;
	}

	public void setAction(FileAction action) {
		this.action = action;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<HunkMapperObject> getHunks() {
		return hunks;
	}

	public void setHunks(List<HunkMapperObject> hunks) {
		this.hunks = hunks;
	}

	public FileActionFileHunksContainer(FileAction action, File file, List<Hunk> hunks) {
		super();
		this.action = action;
		this.file = file;
		this.hunks = new ArrayList<>();
		for (Hunk hunk : hunks) {
			HunkMapperObject mapper = new HunkMapperObject(hunk);
			this.hunks.add(mapper);
		}
	}

	public void printAreas() {
		Logger.log("File: " + file.getPath());
		for (HunkMapperObject hunk : hunks) {
			Logger.log(hunk.getNewStart() + "----" + (hunk.getNewStart() + hunk.getNewLines()));
			Logger.log("   ");
		}
		Logger.log("");
	}

	/**
	 * Gibt zurück, wie viele Hunks entfernt worden sind
	 * @param locationInfo
	 * @return
	 */
	public int removeHunksBasedOnLocationInfo(LocationInfo locationInfo) {
		int removed = 0;
		int startLocation = locationInfo.getStartLine();
		int endLocation = locationInfo.getEndLine();
		List<HunkMapperObject> toAdd = new ArrayList<>();
		List<HunkMapperObject> toRemove = new ArrayList<>();
		for (Iterator<HunkMapperObject> hunkIt=hunks.iterator(); hunkIt.hasNext();) {
			HunkMapperObject hunk = hunkIt.next();
			int start = hunk.getNewStart();
			int end = (hunk.getNewStart() + hunk.getNewLines());

			// Falls die LocationInfo den Hunk überspannt, kann er entfernt werden
			// startLocation --- start --- end -- endLocation
			if(startLocation <= start && end <= endLocation)
			{
				removed++;
				toRemove.add(hunk);
			} else
			// startLocation --- start -- endLocation --- end
			if(startLocation <= start && start <= endLocation && endLocation <= end )
			{
				hunk.removeFromStartTo(endLocation);
				if(hunk.getLines().size() == 0)
				{
					removed++;
					toRemove.add(hunk);
				}
			} else
			// start --- startLocation -- end --- endLocation
			if(start <= startLocation && startLocation <= end && end <= endLocation )
			{
				hunk.removeFromToEnd(startLocation);
				if(hunk.getLines().size() == 0)
				{
					removed++;
					toRemove.add(hunk);
				}
			} else
			// start --- startLocation -- endLocation --- end
			if(start <= startLocation && endLocation <= end )
			{
				toRemove.add(hunk);

			    toAdd.addAll(hunk.split(startLocation,endLocation));
//
//				Hunk h1 = new Hunk();
//				h1.setNewStart(start);
//				h1.setNewLines(startLocation - start);
//				toAdd.add(h1);
//
//				Hunk h2 = new Hunk();
//				h2.setNewStart(endLocation);
//				h2.setNewLines(end - endLocation);
//				toAdd.add(h2);
			}
		}
		hunks.removeAll(toRemove);
		hunks.addAll(toAdd);
		return removed;
	}

	public List<Hunk> getAffectedHunksBasedOnLocation(LocationInfo locationInfo) {
		List<Hunk> affected = new ArrayList<>();
		int startLocation = locationInfo.getStartLine();
		int endLocation = locationInfo.getEndLine();
		for (Iterator<HunkMapperObject> hunkIt = hunks.iterator(); hunkIt.hasNext();) {
			HunkMapperObject hunk = hunkIt.next();
			int start = hunk.getNewStart();
			int end = (hunk.getNewStart() + hunk.getNewLines());

			// Falls die LocationInfo den Hunk überspannt, kann er entfernt
			// werden
			// startLocation --- start --- end -- endLocation
			if (startLocation <= start && end <= endLocation) {
				affected.add(hunk.getHunk());
			} else
			// startLocation --- start -- endLocation --- end
			if (startLocation <= start && start <= endLocation && endLocation <= end) {
				affected.add(hunk.getHunk());

			} else
			// start --- startLocation -- end --- endLocation
			if (start <= startLocation && startLocation <= end && end <= endLocation) {
				affected.add(hunk.getHunk());
			} else
			// start --- startLocation -- endLocation --- end
			if (start <= startLocation && endLocation <= end) {
				affected.add(hunk.getHunk());
			}
		}
		return affected;
	}

	public void cleanWhitespace() {
		for (Iterator<HunkMapperObject> hunkIt=hunks.iterator(); hunkIt.hasNext();) {
			HunkMapperObject object = hunkIt.next();
			object.cleanWhitespace();
			if(object.getNewLines() == 0)
			{
				hunkIt.remove();
			}
		}
	}

	public void cleanComments() {
		for (Iterator<HunkMapperObject> hunkIt=hunks.iterator(); hunkIt.hasNext();) {
			HunkMapperObject object = hunkIt.next();
			object.cleanComments();
			if(object.getNewLines() == 0)
			{
				hunkIt.remove();
			}
		}
	}

	public void cleanAnnotation() {
		for (Iterator<HunkMapperObject> hunkIt=hunks.iterator(); hunkIt.hasNext();) {
			HunkMapperObject object = hunkIt.next();
			object.cleanAnnotation();
			if(object.getNewLines() == 0)
			{
				hunkIt.remove();
			}
		}
	}

	public int getLines() {
		int count = 0;
		for (Iterator<HunkMapperObject> hunkIt=hunks.iterator(); hunkIt.hasNext();) {
			count += hunkIt.next().getLines().size();
		}
		return count;
	}
}

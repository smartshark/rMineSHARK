package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.ugoe.cs.smartshark.model.Hunk;

public class HunkMapperObject {

	private Hunk hunk;
	private List<String> lines;

	public HunkMapperObject(Hunk hunk) {
		this.hunk = hunk;
		this.lines = new ArrayList<String>();
		String[] linesArray = hunk.getContent().split("\n");
		for (String string : linesArray) {
			if(string.trim().startsWith("-"))
			{
				// ignore
			} else if(string.trim().startsWith("+"))
			{
			  String line =	string.replaceFirst("\\+","");
			  line = line.trim();
			  lines.add(line);
			} else {
				String line = string.trim();
				lines.add(line);
			}
		}
	}

	public int getNewStart() {
		return hunk.getNewStart();
	}

	public int getNewLines() {
		return lines.size();
	}

	public int getEndLine()
	{
		return (hunk.getNewStart() + getNewLines());
	}

	public List<String> getLines() {
		return lines;
	}

	public void removeFromStartTo(int endLocation) {
		int z = endLocation - hunk.getNewStart();
		if(z == 0)
			return;
		System.out.println("Remove from 0 to " + z);
		for(int i = 0; i < z; i++)
		{
			lines.remove(0);
		}
		int oldEnd = getEndLine();
		hunk.setNewStart(endLocation);
		hunk.setNewLines(oldEnd - endLocation);
	}

	public void removeFromToEnd(int startLocation) {
		int start = startLocation-hunk.getNewStart();
		if(start == getNewLines())
			return;
		System.out.println("Remove from " + start + " to " +  getNewLines());
		int oldLength = getNewLines();
		for(int i = start; i < oldLength; i++)
		{
			lines.remove(start);
		}
	}

	public List<HunkMapperObject> split(int startLocation, int endLocation) {
		List<HunkMapperObject> objects = new ArrayList<>();

		Hunk h1 = new Hunk();
		h1.setContent(hunk.getContent());
		h1.setNewStart(hunk.getNewStart());
		h1.setNewLines(hunk.getNewLines());

		HunkMapperObject map1 = new HunkMapperObject(h1);
		map1.getLines().clear();
		map1.getLines().addAll(new ArrayList<>(getLines()));
		map1.removeFromToEnd(startLocation);
		objects.add(map1);


		Hunk h2 = new Hunk();
		h2.setContent(hunk.getContent());
		h2.setNewStart(hunk.getNewStart());
		h2.setNewLines(hunk.getNewLines());

		HunkMapperObject map2 = new HunkMapperObject(h2);
		map2.getLines().clear();
		map2.getLines().addAll(new ArrayList<>(getLines()));
		map2.removeFromStartTo(endLocation);
		objects.add(map2);

		return objects;
	}

	public void cleanWhitespace() {
		for (Iterator<String> iterator=lines.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			if(string.replaceAll("\\s+", "").length() == 0)
			{
				//System.out.println("only whitespaces!");
				iterator.remove();
			}
		}

	}

	public void cleanComments() {
		for (Iterator<String> iterator=lines.iterator(); iterator.hasNext();) {
			String string = iterator.next().trim();
			if(string.startsWith("*") || string.startsWith("/*") || string.startsWith("//"))
			{
				//System.out.println("only commt line!");
				iterator.remove();
			}
		}
	}

	public void cleanAnnotation() {
		for (Iterator<String> iterator=lines.iterator(); iterator.hasNext();) {
			String string = iterator.next().trim();
			if(string.contains("@"))
			{
				//System.out.println("contains annotation line!");
				iterator.remove();
			}
		}
	}

	public Hunk getHunk() {
		return hunk;
	}

}

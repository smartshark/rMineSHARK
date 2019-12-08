package de.ugoe.cs.smartshark.rMineSHARK;

import static org.junit.Assert.*;

import java.util.List;

import de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.HunkMapperObject;
import org.junit.Test;

import de.ugoe.cs.smartshark.model.Hunk;

public class HunkMapperObjectTest {

	@Test
	public void test() {
		Hunk hunk = new Hunk();
		hunk.setNewStart(120);
		hunk.setNewLines(1);
		hunk.setContent("- testetsetset \n + testetset");
		HunkMapperObject o = new HunkMapperObject(hunk);

		assertEquals(1, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(121, o.getEndLine());
		assertEquals("testetset", o.getLines().get(0));
	}

	@Test
	public void testRemoveFromStart() {
		Hunk hunk = new Hunk();
		hunk.setNewStart(120);
		hunk.setNewLines(5);
		hunk.setContent("- testetsetset \n + testetset \n + te \n + te \n + tes \n + test");
		HunkMapperObject o = new HunkMapperObject(hunk);

		assertEquals(5, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(125, o.getEndLine());
		assertEquals("testetset", o.getLines().get(0));

		o.removeFromStartTo(122);
		assertEquals(3, o.getNewLines());
		assertEquals(122, o.getNewStart());
		assertEquals(125, o.getEndLine());
		assertEquals("te", o.getLines().get(0));
	}

	@Test
	public void testRemoveFromStartAll() {
		Hunk hunk = new Hunk();
		hunk.setNewStart(120);
		hunk.setNewLines(5);
		hunk.setContent("- testetsetset \n + testetset \n + te \n + te \n + tes \n + test");
		HunkMapperObject o = new HunkMapperObject(hunk);

		assertEquals(5, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(125, o.getEndLine());
		assertEquals("testetset", o.getLines().get(0));

		o.removeFromStartTo(125);
		assertEquals(0, o.getNewLines());
		assertEquals(125, o.getNewStart());
		assertEquals(125, o.getEndLine());
	}

	@Test
	public void testRemoveFromToEnd() {
		Hunk hunk = new Hunk();
		hunk.setNewStart(120);
		hunk.setNewLines(5);
		hunk.setContent("- testetsetset \n + testetset \n + te \n + te \n + tes \n + test");
		HunkMapperObject o = new HunkMapperObject(hunk);

		assertEquals(5, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(125, o.getEndLine());
		assertEquals("testetset", o.getLines().get(0));

		o.removeFromToEnd(122);
		assertEquals(2, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(122, o.getEndLine());
		assertEquals("te", o.getLines().get(1));
	}

	@Test
	public void testRemoveFromToEndAll() {
		Hunk hunk = new Hunk();
		hunk.setNewStart(120);
		hunk.setNewLines(5);
		hunk.setContent("- testetsetset \n + testetset \n + te \n + te \n + tes \n + test");
		HunkMapperObject o = new HunkMapperObject(hunk);

		assertEquals(5, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(125, o.getEndLine());
		assertEquals("testetset", o.getLines().get(0));

		o.removeFromToEnd(120);
		assertEquals(0, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(120, o.getEndLine());
	}

	@Test
	public void testSplit() {
		Hunk hunk = new Hunk();
		hunk.setNewStart(120);
		hunk.setNewLines(5);
		hunk.setContent("- testetsetset \n + testetset \n + te \n + te \n + tes \n + test");
		HunkMapperObject o = new HunkMapperObject(hunk);

		assertEquals(5, o.getNewLines());
		assertEquals(120, o.getNewStart());
		assertEquals(125, o.getEndLine());
		assertEquals("testetset", o.getLines().get(0));

		List<HunkMapperObject> spiltedObjects = o.split(121,123);

		assertEquals(2, spiltedObjects.size());

		assertEquals(1, spiltedObjects.get(0).getNewLines());
		assertEquals(120, spiltedObjects.get(0).getNewStart());
		assertEquals(121,  spiltedObjects.get(0).getEndLine());
		assertEquals("testetset", spiltedObjects.get(0).getLines().get(0));

		assertEquals(2, spiltedObjects.get(1).getNewLines());
		assertEquals(123, spiltedObjects.get(1).getNewStart());
		assertEquals(125,  spiltedObjects.get(1).getEndLine());
		assertEquals("tes", spiltedObjects.get(1).getLines().get(0));
	}
}

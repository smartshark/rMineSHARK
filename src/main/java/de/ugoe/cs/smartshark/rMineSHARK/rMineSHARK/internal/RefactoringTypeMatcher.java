package de.ugoe.cs.smartshark.rMineSHARK.rMineSHARK.internal;

import java.util.ArrayList;
import java.util.List;

import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.decomposition.AbstractCodeMapping;
import gr.uom.java.xmi.decomposition.VariableDeclaration;
import gr.uom.java.xmi.diff.*;
import org.mongodb.morphia.mapping.lazy.proxy.AbstractReference;
import org.refactoringminer.api.Refactoring;

import de.ugoe.cs.smartshark.rMineSHARK.util.Logger;
import gr.uom.java.xmi.LocationInfo;

public class RefactoringTypeMatcher {

	public static List<LocationInfo> getLocationInfoRefactoringSpecific(Refactoring ref) {
		List<LocationInfo> info = new ArrayList<>();
		if (ref instanceof RenameAttributeRefactoring) {
			RenameAttributeRefactoring refImpl = (RenameAttributeRefactoring) ref;
			for (CandidateAttributeRefactoring attribute : refImpl.getAttributeRenames()) {
				info.add(attribute.getOperationAfter().getLocationInfo());
			}
		} else if (ref instanceof MoveOperationRefactoring) {
			MoveOperationRefactoring refImpl = (MoveOperationRefactoring) ref;
			info.add(refImpl.getMovedOperation().getLocationInfo());
		} else if (ref instanceof MoveAndRenameClassRefactoring) {
			MoveAndRenameClassRefactoring refImpl = (MoveAndRenameClassRefactoring) ref;
			info.add(refImpl.getRenamedClass().getLocationInfo());
		} else if (ref instanceof RenameClassRefactoring) {
			RenameClassRefactoring refImpl = (RenameClassRefactoring) ref;
			info.add(refImpl.getRenamedClass().getLocationInfo());
		} else if (ref instanceof ExtractOperationRefactoring) {
			ExtractOperationRefactoring refImpl = (ExtractOperationRefactoring) ref;
			info.add(refImpl.getSourceOperationAfterExtraction().getLocationInfo());
		} else if (ref instanceof MoveAttributeRefactoring) {
			MoveAttributeRefactoring refImpl = (MoveAttributeRefactoring) ref;
			info.add(refImpl.getMovedAttribute().getLocationInfo());
		} else if (ref instanceof ExtractClassRefactoring) {
			ExtractClassRefactoring refImpl = (ExtractClassRefactoring) ref;
			info.add(refImpl.getExtractedClass().getLocationInfo());
		} else if (ref instanceof RenameOperationRefactoring) {
			RenameOperationRefactoring refImpl = (RenameOperationRefactoring) ref;
			info.add(refImpl.getRenamedOperation().getLocationInfo());
		} else if (ref instanceof RenameVariableRefactoring) {
			RenameVariableRefactoring refImpl = (RenameVariableRefactoring) ref;
			info.add(refImpl.getOperationAfter().getLocationInfo());
		} else if (ref instanceof MoveClassRefactoring) {
			MoveClassRefactoring refImpl = (MoveClassRefactoring) ref;
			info.add(refImpl.getMovedClass().getLocationInfo());
		} else if (ref instanceof InlineOperationRefactoring) {
			InlineOperationRefactoring refImpl = (InlineOperationRefactoring) ref;
			info.add(refImpl.getInlinedOperation().getLocationInfo());
		} else if (ref instanceof InlineVariableRefactoring) {
			InlineVariableRefactoring refImpl = (InlineVariableRefactoring) ref;
			info.add(refImpl.getOperation().getLocationInfo());
			for (AbstractCodeMapping classRefactoring:refImpl.getReferences()) {
				info.add(classRefactoring.getOperation2().getLocationInfo());
			}
		} else if (ref instanceof MergeVariableRefactoring) {
			MergeVariableRefactoring refImpl = (MergeVariableRefactoring) ref;
			info.add(refImpl.getOperationAfter().getLocationInfo());
		} else if (ref instanceof ConvertAnonymousClassToTypeRefactoring) {
			ConvertAnonymousClassToTypeRefactoring refImpl = (ConvertAnonymousClassToTypeRefactoring) ref;
			info.add(refImpl.getAddedClass().getLocationInfo());
		} else if (ref instanceof RenamePackageRefactoring) {
			RenamePackageRefactoring refImpl = (RenamePackageRefactoring) ref;
			for (MoveClassRefactoring classRefactoring:refImpl.getMoveClassRefactorings()) {
				info.add(classRefactoring.getMovedClass().getLocationInfo());
			}
		} else if (ref instanceof ChangeAttributeTypeRefactoring) {
			ChangeAttributeTypeRefactoring refImpl = (ChangeAttributeTypeRefactoring) ref;
			info.add(refImpl.getChangedTypeAttribute().getLocationInfo());
		} else if (ref instanceof ChangeVariableTypeRefactoring) {
			ChangeVariableTypeRefactoring refImpl = (ChangeVariableTypeRefactoring) ref;
			info.add(refImpl.getChangedTypeVariable().getLocationInfo());
		} else if (ref instanceof SplitVariableRefactoring) {
			SplitVariableRefactoring refImpl = (SplitVariableRefactoring) ref;
			info.add(refImpl.getOperationAfter().getLocationInfo());
		} else if (ref instanceof ExtractVariableRefactoring) {
			ExtractVariableRefactoring refImpl = (ExtractVariableRefactoring) ref;
			info.add(refImpl.getVariableDeclaration().getLocationInfo());
			info.add(refImpl.getOperation().getLocationInfo());
			for (AbstractCodeMapping classRefactoring:refImpl.getReferences()) {
				info.add(classRefactoring.getOperation2().getLocationInfo());
			}
		} else if (ref instanceof MergeAttributeRefactoring) {
			MergeAttributeRefactoring refImpl = (MergeAttributeRefactoring) ref;
			info.add(refImpl.getNewAttribute().getLocationInfo());
			for (VariableDeclaration classRefactoring:refImpl.getMergedAttributes()) {
				info.add(classRefactoring.getLocationInfo());
			}
		} else if (ref instanceof MoveSourceFolderRefactoring) {
			MoveSourceFolderRefactoring refImpl = (MoveSourceFolderRefactoring) ref;
			for (MovedClassToAnotherSourceFolder classRefactoring:refImpl.getMovedClassesToAnotherSourceFolder()) {
				info.add(classRefactoring.getMovedClass().getLocationInfo());
			}
		} else if (ref instanceof ExtractAttributeRefactoring) {
			ExtractAttributeRefactoring refImpl = (ExtractAttributeRefactoring) ref;
			info.add(refImpl.getVariableDeclaration().getLocationInfo());
		} else if (ref instanceof SplitAttributeRefactoring) {
			SplitAttributeRefactoring refImpl = (SplitAttributeRefactoring) ref;
			for (CandidateSplitVariableRefactoring classRefactoring:refImpl.getAttributeSplits()) {
				info.add(classRefactoring.getOperationAfter().getLocationInfo());
			}
		} else if (ref instanceof ChangeReturnTypeRefactoring) {
			ChangeReturnTypeRefactoring refImpl = (ChangeReturnTypeRefactoring) ref;
			info.add(refImpl.getOperationAfter().getLocationInfo());
			for (AbstractCodeMapping classRefactoring:refImpl.getReturnReferences()) {
				info.add(classRefactoring.getOperation2().getLocationInfo());
			}
		}  else if (ref instanceof ExtractSuperclassRefactoring) {
			ExtractSuperclassRefactoring refImpl = (ExtractSuperclassRefactoring) ref;
			info.add(refImpl.getExtractedClass().getLocationInfo());
			for (UMLClass classRefactoring:refImpl.getUMLSubclassSet()) {
				info.add(classRefactoring.getLocationInfo());
			}
		} else {
			Logger.log("WARN not implemented refacorting " + ref.getRefactoringType());
		}

		return info;
	}
}

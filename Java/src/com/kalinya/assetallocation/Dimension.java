package com.kalinya.assetallocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.Assertions;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.ToStringBuilder;

public final class Dimension implements Comparable<Dimension> {
	private Dimension parentDimension;
	private List<Dimension> childDimensions;
	private String name;
	
	private Dimension() {
		childDimensions = new ArrayList<>();
	}
	
	private Dimension(String name) {
		this();
		this.name = name;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("Name", name)
				.build();
	}
	
	public String toVerboseString() {
		return new ToStringBuilder(this)
				.append("Name", name)
				.append("ParentDimension", parentDimension)
				.append("ChildDimensions", getDimensionNames(childDimensions))
				.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<Dimension>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(name.toUpperCase())
				.build();
	}
	
	@Override
	public int compareTo(Dimension that) {
		return new CompareToBuilder()
				.append(name.toUpperCase(), that.name.toUpperCase())
				.build();
	}
	
	public static Dimension create(String name) {
		return new Dimension(name);
	}
	
	public void setParentDimension(Dimension parentDimension) {
		Assertions.isNotEqual("AssetAllocationDimension", this, parentDimension);
		this.parentDimension = parentDimension;
		if(!parentDimension.getChildDimensions().contains(this)) {
			parentDimension.addChildDimension(this);
		}
	}
	
	public Dimension getParentDimension() {
		return parentDimension;
	}
	
	public void addChildDimension(Dimension childDimension) {
		Assertions.isNotEqual("AssetAllocationDimension", this, childDimension);
		childDimensions.add(childDimension);
		if(!this.equals(childDimension.getParentDimension())) {
			childDimension.setParentDimension(this);
		}
	}
	
	public List<Dimension> getChildDimensions() {
		return childDimensions;
	}
	
	public static String getDimensionNames(List<Dimension> dimensions) {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(Dimension dimension: dimensions) {
			sb.append(concatenator);
			sb.append(dimension);
			concatenator = ", ";
		}
		return sb.toString();
	}

	public static List<Dimension> getDimensionsAsList(Dimension... dimensionsArray) {
		List<Dimension> dimensionsList = new ArrayList<>();
		for(Dimension dimension: dimensionsArray) {
			dimensionsList.add(dimension);
		}
		return dimensionsList;
	}
	
	public static List<Dimension> getDimensionsWithInheritanceAsList(Dimension... dimensionsArray) {
		List<Dimension> dimensionsList = new ArrayList<>();
		for(Dimension dimension: dimensionsArray) {
			dimensionsList.add(dimension);
			dimensionsList.addAll(dimension.getDescendants());
			dimensionsList.addAll(dimension.getAntecedents());
		}
		return dimensionsList;
	}

	public Set<Dimension> getAntecedents() {
		Set<Dimension> antecedents = new LinkedHashSet<>();
		Dimension antecedent = getParentDimension();
		while(antecedent != null) {
			antecedents.add(antecedent);
			antecedent = antecedent.getParentDimension();
		}
		return antecedents;
	}

	public Set<Dimension> getDescendants() {
		Set<Dimension> descendants = new LinkedHashSet<>();
		for(Dimension descendant: getChildDimensions()) {
			descendants.add(descendant);
			descendants.addAll(descendant.getDescendants());
		}
		return descendants;
	}
	
	public int getChildGenerationCount() {
		Set<Dimension> descendants = getDescendants();
		if(name.equalsIgnoreCase("Core")) {
			System.out.println("Core");
		}
		if(descendants.size() == 0) {
			return 0;
		}
		int maximumChildGenerationCount = 1;
		for(Dimension descendant: descendants) {
			maximumChildGenerationCount = Math.max(maximumChildGenerationCount, descendant.getChildGenerationCount()+1);
		}
		return maximumChildGenerationCount;
	}
	
	public static Map<Integer, Set<Dimension>> getDimensionFamilyTree(List<Dimension> dimensions) {
		Map<Integer, Set<Dimension>> familyTree = new TreeMap<Integer, Set<Dimension>>(Collections.reverseOrder());
		for(Dimension dimension: dimensions) {
			int childGenerationCount = dimension.getChildGenerationCount();
			Set<Dimension> generation = familyTree.get(childGenerationCount);
			if(generation == null) {
				familyTree.put(childGenerationCount, new TreeSet<Dimension>());
			}
			familyTree.get(childGenerationCount).add(dimension);
		}
		return familyTree; 
	}

	public static String getDimensionFamilyTreeAsString(List<Dimension> dimensions) {
		Map<Integer, Set<Dimension>> familyTree = getDimensionFamilyTree(dimensions);
		Set<Dimension> printed = new HashSet<Dimension>();
		StringBuilder sb = new StringBuilder();
		String newLineBreak = "";
		String tab = "";
		String bullet = "-";
		for(int i: familyTree.keySet()) {
			Set<Dimension> dimensionsInGeneration = familyTree.get(i);
			for(Dimension dimension: dimensionsInGeneration) {
				if(printed.contains(dimension)) {
					continue;
				}
				sb.append(newLineBreak + tab + bullet);
				sb.append(dimension.name);
				printed.add(dimension);
				newLineBreak = "\n";
			}
			tab += " ";
			bullet += "-";
		}
		return sb.toString();
	}
}

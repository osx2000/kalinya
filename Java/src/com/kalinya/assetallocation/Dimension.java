package com.kalinya.assetallocation;

import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.Assertions;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.ToStringBuilder;

public final class Dimension implements Comparable<Dimension> {
	public static final Dimension CASH = new Dimension("Cash");
	private Dimension parentDimension;
	private Dimensions childDimensions;
	private String name;
	
	private Dimension() {
		childDimensions = Dimensions.create();
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
				.append("ChildDimensions", childDimensions.toMinimalString())
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
	
	public String getName() {
		return name;
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
	
	public Dimensions getChildDimensions() {
		return childDimensions;
	}
	
	public Dimensions getRelatedDimensions() {
		Dimensions dimensions = Dimensions.create();
		dimensions.add(this);
		dimensions.addAll(getDescendants());
		dimensions.addAll(getAntecedents());
		return dimensions;
	}

	public Dimensions getAntecedents() {
		Dimensions antecedents = Dimensions.create();
		Dimension antecedent = getParentDimension();
		while(antecedent != null) {
			antecedents.add(antecedent);
			antecedent = antecedent.getParentDimension();
		}
		return antecedents;
	}

	public Dimensions getDescendants() {
		Dimensions descendants = Dimensions.create();
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
	
	/*public static Map<Integer, Set<Dimension>> getDimensionsHierarchy(List<Dimension> dimensions) {
		Map<Integer, Set<Dimension>> dimensionsHierarchy = new TreeMap<Integer, Set<Dimension>>(Collections.reverseOrder());
		for(Dimension dimension: dimensions) {
			int childGenerationCount = dimension.getChildGenerationCount();
			Set<Dimension> generation = dimensionsHierarchy.get(childGenerationCount);
			if(generation == null) {
				dimensionsHierarchy.put(childGenerationCount, new TreeSet<Dimension>());
			}
			dimensionsHierarchy.get(childGenerationCount).add(dimension);
		}
		return dimensionsHierarchy; 
	}*/

}

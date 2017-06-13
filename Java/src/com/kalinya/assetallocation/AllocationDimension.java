package com.kalinya.assetallocation;

import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.Assertions;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.ToStringBuilder;

public final class AllocationDimension implements Comparable<AllocationDimension> {
	public static final AllocationDimension CASH = new AllocationDimension("Cash");
	public static final AllocationDimension UNKNOWN = new AllocationDimension("Unknown");
	private AllocationDimension parentDimension;
	private AllocationDimensions childDimensions;
	private String name;
	
	private AllocationDimension() {
		childDimensions = AllocationDimensions.create();
	}
	
	private AllocationDimension(String name) {
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
		return new ComparableEqualsBuilder<AllocationDimension>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(name.toUpperCase())
				.build();
	}
	
	@Override
	public int compareTo(AllocationDimension that) {
		return new CompareToBuilder()
				.append(name.toUpperCase(), that.name.toUpperCase())
				.build();
	}
	
	public static AllocationDimension create(String name) {
		return new AllocationDimension(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setParentDimension(AllocationDimension parentDimension) {
		Assertions.isNotEqual("AssetAllocationDimension", this, parentDimension);
		this.parentDimension = parentDimension;
		if(!parentDimension.getChildDimensions().contains(this)) {
			parentDimension.addChildDimension(this);
		}
	}
	
	public AllocationDimension getParentDimension() {
		return parentDimension;
	}
	
	public void addChildDimension(AllocationDimension childDimension) {
		Assertions.isNotEqual("AssetAllocationDimension", this, childDimension);
		childDimensions.add(childDimension);
		if(!this.equals(childDimension.getParentDimension())) {
			childDimension.setParentDimension(this);
		}
	}
	
	public AllocationDimensions getChildDimensions() {
		return childDimensions;
	}
	
	public AllocationDimensions getRelatedDimensions() {
		AllocationDimensions dimensions = AllocationDimensions.create();
		dimensions.add(this);
		dimensions.addAll(getDescendants());
		dimensions.addAll(getAntecedents());
		return dimensions;
	}

	public AllocationDimensions getAntecedents() {
		AllocationDimensions antecedents = AllocationDimensions.create();
		AllocationDimension antecedent = getParentDimension();
		while(antecedent != null) {
			antecedents.add(antecedent);
			antecedent = antecedent.getParentDimension();
		}
		return antecedents;
	}

	public AllocationDimensions getDescendants() {
		AllocationDimensions descendants = AllocationDimensions.create();
		for(AllocationDimension descendant: getChildDimensions()) {
			descendants.add(descendant);
			descendants.addAll(descendant.getDescendants());
		}
		return descendants;
	}
	
	public int getChildGenerationCount() {
		Set<AllocationDimension> descendants = getDescendants();
		if(name.equalsIgnoreCase("Core")) {
			System.out.println("Core");
		}
		if(descendants.size() == 0) {
			return 0;
		}
		int maximumChildGenerationCount = 1;
		for(AllocationDimension descendant: descendants) {
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

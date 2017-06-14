package com.kalinya.assetallocation;

import java.util.Set;

import com.kalinya.instrument.InstrumentData;
import com.kalinya.util.Assertions;
import com.kalinya.util.ToStringBuilder;

public final class AllocationDimension extends InstrumentData<AllocationDimension> {
	private static final long serialVersionUID = -1561648899512026567L;
	public static final AllocationDimension CASH = AllocationDimension.create("Cash");
	public static final AllocationDimension UNKNOWN = AllocationDimension.create("Unknown");
	private AllocationDimension parentDimension;
	private AllocationDimensions<AllocationDimension> childDimensions;
	
	private AllocationDimension(String name) {
		super(name);
		childDimensions = AllocationDimensions.create();
	}
	
	public String toVerboseString() {
		return new ToStringBuilder(this)
				.append("Name", name)
				.append("ParentDimension", parentDimension)
				.append("ChildDimensions", childDimensions.toMinimalString())
				.build();
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
	
	public AllocationDimensions<? extends AllocationDimension> getChildDimensions() {
		return childDimensions;
	}
	
	public AllocationDimensions<AllocationDimension> getRelatedDimensions() {
		AllocationDimensions<AllocationDimension> dimensions = new AllocationDimensions<AllocationDimension>();
		dimensions.add(this);
		dimensions.addAll(getDescendants());
		dimensions.addAll(getAntecedents());
		return dimensions;
	}

	public AllocationDimensions<AllocationDimension> getAntecedents() {
		AllocationDimensions<AllocationDimension> antecedents = new AllocationDimensions<>();
		AllocationDimension antecedent = getParentDimension();
		while(antecedent != null) {
			antecedents.add(antecedent);
			antecedent = antecedent.getParentDimension();
		}
		return antecedents;
	}

	public AllocationDimensions<AllocationDimension> getDescendants() {
		AllocationDimensions<AllocationDimension> descendants = new AllocationDimensions<>();
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

	public static AllocationDimension create(String name) {
		return new AllocationDimension(name);
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

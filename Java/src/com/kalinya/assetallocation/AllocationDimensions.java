package com.kalinya.assetallocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.util.BaseSet;

public final class AllocationDimensions extends BaseSet<AllocationDimension> {
	private static final long serialVersionUID = -1720424418344669033L;
	public static final AllocationDimensions EMPTY = new AllocationDimensions();

	public AllocationDimensions() {
		super();
	}
	
	@Override
	protected Set<AllocationDimension> createSet() {
		return new LinkedHashSet<AllocationDimension>();
	}

	public static AllocationDimensions create() {
		return new AllocationDimensions();
	}
	
	@Override
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(AllocationDimension dimension: getSet()) {
			sb.append(concatenator);
			sb.append(dimension);
			concatenator = ", ";
		}
		return sb.toString();
	}

	public Map<Integer, AllocationDimensions> getHierarchy() {
		Map<Integer, AllocationDimensions> dimensionsHierarchy = new TreeMap<Integer, AllocationDimensions>(Collections.reverseOrder());
		for(AllocationDimension dimension: getSet()) {
			int childGenerationCount = dimension.getChildGenerationCount();
			Set<AllocationDimension> generation = dimensionsHierarchy.get(childGenerationCount);
			if(generation == null) {
				dimensionsHierarchy.put(childGenerationCount, AllocationDimensions.create());
			}
			dimensionsHierarchy.get(childGenerationCount).add(dimension);
		}
		return dimensionsHierarchy; 
	}

	public AllocationDimension get(String name) {
		for(AllocationDimension dimension: getSet()) {
			if(dimension.getName().equalsIgnoreCase(name)) {
				return dimension;
			}
		}
		throw new IllegalArgumentException(String.format("Dimension [%s] is not one of the dimension in the collection %s", name, toMinimalString()));
	}
	
	public String getDimensionFamilyTreeAsString() {
		/*
		 * -Core
		 *  --Active
		 *  --Cash1
		 *  --Passive
		 *  --Satellite
		 *   ---BmkBills
		 *   ---BmkBonds
		 *   ---Cash
		 *   ---Corp
		 *   ---Country
		 *   ---Duration
		 *   ---Govt
		 *   ---SemiGovt
		 *   
		 *  //TODO: what I'd like to have: 
		 * -Core
		 *  --Active
		 *   ---Govt
		 *   ---SemiGovt
		 *   ---Corp
		 *  --Passive
		 *   ---BmkBonds
		 *   ---BmkBills
		 * -Satellite
		 *  --Country
		 *  --Duration
		 * -Cash1
		 *  --Cash
		 */
		
		Map<Integer, AllocationDimensions> familyTree = getHierarchy();
		Set<AllocationDimension> printed = new HashSet<AllocationDimension>();
		StringBuilder sb = new StringBuilder();
		String newLineBreak = "";
		String tab = "";
		String bullet = "-";
		for(int i: familyTree.keySet()) {
			Set<AllocationDimension> dimensionsInGeneration = familyTree.get(i);
			for(AllocationDimension dimension: dimensionsInGeneration) {
				if(printed.contains(dimension)) {
					continue;
				}
				sb.append(newLineBreak + tab + bullet);
				sb.append(dimension.getName());
				printed.add(dimension);
				newLineBreak = "\n";
			}
			tab += " ";
			bullet += "-";
		}
		return sb.toString();
	}
}


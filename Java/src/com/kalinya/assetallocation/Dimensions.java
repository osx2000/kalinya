package com.kalinya.assetallocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.util.BaseSet;

public final class Dimensions extends BaseSet<Dimension> {
	private static final long serialVersionUID = -1720424418344669033L;
	public static final Dimensions EMPTY = new Dimensions();

	public Dimensions() {
		super();
	}
	
	@Override
	protected Set<Dimension> createSet() {
		return new LinkedHashSet<Dimension>();
	}

	public static Dimensions create() {
		return new Dimensions();
	}
	
	@Override
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(Dimension dimension: getSet()) {
			sb.append(concatenator);
			sb.append(dimension);
			concatenator = ", ";
		}
		return sb.toString();
	}

	public Map<Integer, Dimensions> getHierarchy() {
		Map<Integer, Dimensions> dimensionsHierarchy = new TreeMap<Integer, Dimensions>(Collections.reverseOrder());
		for(Dimension dimension: getSet()) {
			int childGenerationCount = dimension.getChildGenerationCount();
			Set<Dimension> generation = dimensionsHierarchy.get(childGenerationCount);
			if(generation == null) {
				dimensionsHierarchy.put(childGenerationCount, Dimensions.create());
			}
			dimensionsHierarchy.get(childGenerationCount).add(dimension);
		}
		return dimensionsHierarchy; 
	}

	public Dimension get(String name) {
		for(Dimension dimension: getSet()) {
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
		
		Map<Integer, Dimensions> familyTree = getHierarchy();
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


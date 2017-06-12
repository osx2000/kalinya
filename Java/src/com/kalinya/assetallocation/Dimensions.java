package com.kalinya.assetallocation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class Dimensions implements Set<Dimension>, Serializable {
	private static final long serialVersionUID = -1720424418344669033L;
	public static final Dimensions EMPTY = new Dimensions();
	private Set<Dimension> set;

	public Dimensions() {
		super();
	}
	
	protected Set<Dimension> createSet() {
		return new LinkedHashSet<Dimension>();
	}

	public static Dimensions create() {
		return new Dimensions();
	}
	
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
	
	public final Set<Dimension> getSet() {
		return set;
	}

	@Override
	public boolean add(Dimension arg0) {
		return getSet().add(arg0);
	}

	@Override
	public boolean addAll(Collection<? extends Dimension> arg0) {
		return getSet().addAll(arg0);
	}
	
	@SafeVarargs
	public final void add(Dimension... elements) {
		for(Dimension element: elements) {
			add(element);
		}
	}
	
	@Override
	public void clear() {
		getSet().clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return getSet().contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return getSet().containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return getSet().isEmpty();
	}

	@Override
	public Iterator<Dimension> iterator() {
		return getSet().iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		return getSet().remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return getSet().removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return getSet().retainAll(arg0);
	}

	@Override
	public int size() {
		return getSet().size();
	}

	@Override
	public Object[] toArray() {
		return getSet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		//TODO: test this
		return getSet().toArray(arg0);
	}

	public int getCount() {
		return getSet().size();
	}
}


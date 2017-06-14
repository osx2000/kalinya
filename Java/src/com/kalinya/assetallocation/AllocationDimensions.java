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

public final class AllocationDimensions<E extends Comparable<AllocationDimension>> implements Set<E>, Serializable {
	private static final long serialVersionUID = -1720424418344669033L;
	public static final AllocationDimensions<AllocationDimension> EMPTY = new AllocationDimensions<>();
	private Set<E> set;

	public AllocationDimensions() {
		set = createSet();
	}
	
	protected Set<E> createSet() {
		return new LinkedHashSet<E>();
	}

	@Override 
	public String toString() {
		return toVerboseString();
	}
	
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(E dimension: getSet()) {
			sb.append(concatenator);
			sb.append(dimension);
			concatenator = ", ";
		}
		return sb.toString();
	}
	
	public String toVerboseString() {
		StringBuilder sb = new StringBuilder("<" + getClass().getSimpleName() + ">");
		sb.append(" Size [" + size() + "]");
		sb.append(getSetStringWithLineBreaks());
		return sb.toString();
	}
	
	protected String getSetStringWithLineBreaks() {
		StringBuilder sb = new StringBuilder();
		for(E e: getSet()) {
			sb.append("\n" + e.toString());
		}
		return sb.toString();
	}

	public Map<Integer, AllocationDimensions<AllocationDimension>> getHierarchy() {
		Map<Integer, AllocationDimensions<AllocationDimension>> dimensionsHierarchy = new TreeMap<Integer, AllocationDimensions<AllocationDimension>>(Collections.reverseOrder());
		for(E dimension: getSet()) {
			int childGenerationCount = ((AllocationDimension) dimension).getChildGenerationCount();
			Set<AllocationDimension> generation = dimensionsHierarchy.get(childGenerationCount);
			if(generation == null) {
				dimensionsHierarchy.put(childGenerationCount, new AllocationDimensions<>());
			}
			dimensionsHierarchy.get(childGenerationCount).add((AllocationDimension) dimension);
		}
		return dimensionsHierarchy; 
	}

	public AllocationDimension get(String name) {
		for(E dimension: getSet()) {
			if(((AllocationDimension) dimension).getName().equalsIgnoreCase(name)) {
				return (AllocationDimension) dimension;
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
		
		Map<Integer, AllocationDimensions<AllocationDimension>> familyTree = getHierarchy();
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
	
	public final Set<E> getSet() {
		return set;
	}
	
	@Override
	public boolean add(E arg0) {
		return getSet().add(arg0);
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		return getSet().addAll(arg0);
	}
	
	@SafeVarargs
	public final void add(E... elements) {
		for(E element: elements) {
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
	public Iterator<E> iterator() {
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

	public static AllocationDimensions<AllocationDimension> create() {
		return new AllocationDimensions<AllocationDimension>();
	}
}


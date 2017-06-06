package com.kalinya.assetallocation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kalinya.optimization.Instrument;
import com.kalinya.util.ToStringBuilder;

public class Strategy {
	
	private String name;
	private Map<Integer, Set<Dimension>> allocationHierarchy;
	private Map<Dimension, BigDecimal> specifiedTargetAllocations;
	private Map<Dimension, BigDecimal> aggregatedTargetAllocations;
	private Map<Instrument, BigDecimal> portfolio;
	
	private Strategy() {
	}
	
	private Strategy(String name) {
		this();
		this.name = name;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("Name", name)
				.append("TargetAllocations", aggregatedTargetAllocations)
				.build();
	}
	
	public static Strategy create(String name) {
		return new Strategy(name);
	}
	
	public void setDimensions(List<Dimension> dimensions) {
		allocationHierarchy = Dimension.getDimensionsHierarchy(dimensions);
		aggregatedTargetAllocations = new HashMap<Dimension, BigDecimal>();
		for(Dimension dimension: dimensions) {
			aggregatedTargetAllocations.put(dimension, BigDecimal.ZERO);
		}
	}
	
	public List<Dimension> getDimensions() {
		Collection<Set<Dimension>> dimensions = allocationHierarchy.values();
		List<Dimension> distinctDimensions = new LinkedList<>();
		for(Set<Dimension> set: dimensions) {
			distinctDimensions.addAll(set);
		}
		return distinctDimensions;
	}
	
	public void setTargetAllocation(Dimension dimension, BigDecimal target) {
		if(specifiedTargetAllocations == null) {
			specifiedTargetAllocations = new HashMap<Dimension, BigDecimal>();
		}
		if(aggregatedTargetAllocations == null) {
			aggregatedTargetAllocations = new HashMap<Dimension, BigDecimal>();
		}
		specifiedTargetAllocations.put(dimension, target);
		aggregateTargetAllocations();
	}

	private void aggregateTargetAllocations() {
		//TODO: this is tricky
		int firstGenerationId = allocationHierarchy.keySet().iterator().next();
		int generationId = 0;
		while(generationId <= firstGenerationId) {
			Set<Dimension> generationDimensions = allocationHierarchy.get(generationId);
			for(Dimension dimension: generationDimensions) {
				BigDecimal target = BigDecimal.ZERO;
				if(generationId == 0) {
					target = specifiedTargetAllocations.get(dimension);
					if(target == null) {
						target = BigDecimal.ZERO;
					}
				} else {
					for(Dimension childDimension: dimension.getDescendants()) {
						if(specifiedTargetAllocations.keySet().contains(childDimension)) {
							target = target.add(aggregatedTargetAllocations.get(childDimension));
						}
					}
				}
				aggregatedTargetAllocations.put(dimension, target);
			}
			generationId++;
		}
		/*for(Integer generationCount: allocationHierarchy.keySet()) {
			for(Dimension dimension: allocationHierarchy.get(generationCount)) {
				List<Dimension> childDimensions = dimension.getChildDimensions();
				BigDecimal aggregateTarget = BigDecimal.ZERO;
				for(Dimension childDimension: childDimensions) {
					BigDecimal specifiedTarget = aggregatedTargetAllocations.get(childDimension);
					aggregateTarget.add(specifiedTarget);
				}
			}
		}*/
		
	}

	public Map<Dimension, BigDecimal> getSpecifiedTargetAllocations() {
		Set<Dimension> specifiedDimensions = allocationHierarchy.get(0);
		Map<Dimension, BigDecimal> specifiedTargetAllocations = new HashMap<Dimension, BigDecimal>();
		for(Dimension dimension: specifiedDimensions) {
			BigDecimal target = this.aggregatedTargetAllocations.get(dimension);
			specifiedTargetAllocations.put(dimension, target);
		}
		return specifiedTargetAllocations;
	}
	
	public Map<Dimension, BigDecimal> getAggregatedTargetAllocations() {
		return aggregatedTargetAllocations;
	}
	
	public String getTargetAllocationsAsString() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setActualAllocation(Map<Instrument, BigDecimal> portfolio) {
		this.portfolio = portfolio;
	}
	
	public Map<Instrument, BigDecimal> getActualAllocation() {
		return portfolio;
	}
	
	
}

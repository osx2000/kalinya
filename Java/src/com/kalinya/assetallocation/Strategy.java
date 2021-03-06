package com.kalinya.assetallocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kalinya.performance.Instrument;
import com.kalinya.util.Assertions;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

public class Strategy {
	
	private String name;
	private Map<Integer, AllocationDimensions<AllocationDimension>> allocationHierarchy;
	private Map<AllocationDimension, BigDecimal> specifiedTargetAllocationsByDimension;
	private Map<AllocationDimension, BigDecimal> aggregatedTargetAllocationsByDimension;
	private Map<Instrument, BigDecimal> portfolio;
	private BigDecimal portfolioSize;
	private BigDecimal minimumOrderSize;
	private List<String> orderDetails;
	private Map<Instrument, BigDecimal> orders;
	
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
				.append("TargetAllocations", aggregatedTargetAllocationsByDimension)
				.build();
	}
	
	public static Strategy create(String name) {
		return new Strategy(name);
	}
	
	public void setDimensions(AllocationDimensions<AllocationDimension> dimensions) {
		allocationHierarchy = dimensions.getHierarchy();
		aggregatedTargetAllocationsByDimension = new HashMap<AllocationDimension, BigDecimal>();
		for(AllocationDimension dimension: dimensions) {
			aggregatedTargetAllocationsByDimension.put(dimension, BigDecimal.ZERO);
		}
	}
	
	public List<AllocationDimension> getDimensions() {
		Collection<AllocationDimensions<AllocationDimension>> dimensions = allocationHierarchy.values();
		List<AllocationDimension> distinctDimensions = new LinkedList<>();
		for(Set<AllocationDimension> set: dimensions) {
			distinctDimensions.addAll(set);
		}
		return distinctDimensions;
	}
	
	private void aggregateTargetAllocations() {
		//TODO: this is tricky
		int firstGenerationId = allocationHierarchy.keySet().iterator().next();
		int generationId = 0;
		while(generationId <= firstGenerationId) {
			Set<AllocationDimension> generationDimensions = allocationHierarchy.get(generationId);
			for(AllocationDimension dimension: generationDimensions) {
				BigDecimal target = BigDecimal.ZERO;
				if(generationId == 0) {
					target = specifiedTargetAllocationsByDimension.get(dimension);
					if(target == null) {
						target = BigDecimal.ZERO;
					}
				} else {
					for(AllocationDimension childDimension: dimension.getDescendants()) {
						if(specifiedTargetAllocationsByDimension.keySet().contains(childDimension)) {
							target = target.add(aggregatedTargetAllocationsByDimension.get(childDimension));
						}
					}
				}
				aggregatedTargetAllocationsByDimension.put(dimension, target);
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

	public void setTargetAllocation(AllocationDimension dimension, BigDecimal target) {
		if(specifiedTargetAllocationsByDimension == null) {
			specifiedTargetAllocationsByDimension = new HashMap<AllocationDimension, BigDecimal>();
		}
		if(aggregatedTargetAllocationsByDimension == null) {
			aggregatedTargetAllocationsByDimension = new HashMap<AllocationDimension, BigDecimal>();
		}
		specifiedTargetAllocationsByDimension.put(dimension, target);
		aggregateTargetAllocations();
	}
	
	public Map<AllocationDimension, BigDecimal> getSpecifiedTargetAllocations() {
		Set<AllocationDimension> specifiedDimensions = allocationHierarchy.get(0);
		Map<AllocationDimension, BigDecimal> specifiedTargetAllocations = new HashMap<AllocationDimension, BigDecimal>();
		for(AllocationDimension dimension: specifiedDimensions) {
			BigDecimal target = this.aggregatedTargetAllocationsByDimension.get(dimension);
			specifiedTargetAllocations.put(dimension, target);
		}
		return specifiedTargetAllocations;
	}
	
	public Map<AllocationDimension, BigDecimal> getAggregatedTargetAllocations() {
		return aggregatedTargetAllocationsByDimension;
	}
	
	public String getTargetAllocationsAsString() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setActualAllocation(Map<com.kalinya.performance.Instrument, BigDecimal> portfolio) {
		this.portfolio = portfolio;
	}
	
	public Map<Instrument, BigDecimal> getActualAllocation() {
		return portfolio;
	}

	public BigDecimal getPortfolioSize() {
		if(portfolioSize == null) {
			calculatePortfolioSize();
		}
		return portfolioSize;
	}
	
	public void calculatePortfolioSize() {
		BigDecimal portfolioSize = BigDecimal.ZERO;
		for(BigDecimal instrumentValue: getActualAllocation().values()) {
			portfolioSize = portfolioSize.add(instrumentValue);
		}
		this.portfolioSize = portfolioSize;
	}

	public void setMinimumOrderSize(BigDecimal minimumOrderSize) {
		this.minimumOrderSize = minimumOrderSize;
	}
	
	public BigDecimal getMinimumOrderSize() {
		return minimumOrderSize;
	}
	
	public Map<AllocationDimension, Set<Instrument>> getInstrumentsByDimension() {
		Assertions.notNullOrEmpty("Portfolio", portfolio);
		Map<AllocationDimension, Set<Instrument>> instrumentsByDimension = new LinkedHashMap<AllocationDimension, Set<Instrument>>();
		for(Instrument instrument: portfolio.keySet()) {
			AllocationDimension dimension = instrument.getAllocationDimension();
			Set<Instrument> instruments = instrumentsByDimension.get(dimension);
			if(instruments == null) {
				instrumentsByDimension.put(dimension, new LinkedHashSet<Instrument>());
			}
			instrumentsByDimension.get(dimension).add(instrument);
		}
		return instrumentsByDimension;
	}
	
	public Map<AllocationDimension, BigDecimal> getPortfolioSizeByDimension() {
		Assertions.notNullOrEmpty("Portfolio", portfolio);
		Map<AllocationDimension, BigDecimal> portfolioSizeByDimension = new LinkedHashMap<AllocationDimension, BigDecimal>();
		for(Instrument instrument: portfolio.keySet()) {
			BigDecimal value = portfolio.get(instrument);
			AllocationDimension dimension = instrument.getAllocationDimension();
			BigDecimal dimensionSize = portfolioSizeByDimension.get(dimension);
			if(dimensionSize != null) {
				value = dimensionSize.add(value);
			}
			portfolioSizeByDimension.put(dimension, value);
		}
		return portfolioSizeByDimension;
	}

	public Map<Instrument, BigDecimal> getOrders() {
		Assertions.notNullOrEmpty("Portfolio", portfolio);
		Assertions.notZero("PortfolioSize", getPortfolioSize());
		Map<Instrument, BigDecimal> weightsInDimension = getWeightsInDimension();
		Map<Instrument, BigDecimal> targetAllocationsByInstrument = new LinkedHashMap<Instrument, BigDecimal>();
		Map<Instrument, BigDecimal> allocationDifferenceByInstrument = new LinkedHashMap<Instrument, BigDecimal>();
		orders = new LinkedHashMap<Instrument, BigDecimal>();
		orderDetails = new ArrayList<>();
		for(Instrument instrument: portfolio.keySet()) {
			BigDecimal weightInDimension = weightsInDimension.get(instrument);
			AllocationDimension dimension = instrument.getAllocationDimension();
			BigDecimal dimensionTargetAllocation = specifiedTargetAllocationsByDimension.get(dimension);
			BigDecimal instrumentTargetAllocation = weightInDimension.multiply(dimensionTargetAllocation);
			targetAllocationsByInstrument.put(instrument, instrumentTargetAllocation);
			BigDecimal actualAllocation = portfolio.get(instrument).divide(getPortfolioSize(), NumberUtil.MATH_CONTEXT);
			BigDecimal allocationDifference = instrumentTargetAllocation.subtract(actualAllocation);
			allocationDifferenceByInstrument.put(instrument, allocationDifference);
			BigDecimal orderValue = allocationDifference.multiply(getPortfolioSize());
			orders.put(instrument, orderValue.setScale(2, NumberUtil.ROUNDING_MODE));
			String orderDetail = String.format("Instrument [%s] Dimension [%s] ActualAllocation [%s%%] InstrumentTargetAllocation [%s%%] Difference [%s%%] Order [%s]",
					instrument.getInstrumentId(), dimension.getName(), 
					StringUtil.formatDouble(actualAllocation.multiply(NumberUtil.ONE_HUNDRED)),
					StringUtil.formatDouble(instrumentTargetAllocation.multiply(NumberUtil.ONE_HUNDRED)),
					StringUtil.formatDouble(allocationDifference.multiply(NumberUtil.ONE_HUNDRED)),
					StringUtil.formatDouble(orderValue));
			orderDetails.add((orderDetails.size() > 0 ? "\n" : "") + orderDetail);
		}
		return orders;
	}
	
	public List<String> getOrderDetails() {
		if(orders == null) {
			getOrders();
		}
		return orderDetails;
	}
	
	private Map<Instrument, BigDecimal> getWeightsInDimension() {
		Assertions.notNullOrEmpty("Portfolio", portfolio);
		Map<Instrument, BigDecimal> weightsInDimension = new LinkedHashMap<Instrument, BigDecimal>();
		Map<AllocationDimension, Set<Instrument>> instrumentsByDimension = getInstrumentsByDimension();
		Map<AllocationDimension, BigDecimal> portfolioSizeByDimension = getPortfolioSizeByDimension();
		
		for(AllocationDimension dimension: instrumentsByDimension.keySet()) {
			Set<Instrument> instrumentsInDimension = instrumentsByDimension.get(dimension);
			for(Instrument instrument: instrumentsInDimension) {
				BigDecimal instrumentValue = portfolio.get(instrument);
				BigDecimal dimensionValue = portfolioSizeByDimension.get(dimension);
				BigDecimal weightInDimension = instrumentValue.divide(dimensionValue, NumberUtil.MATH_CONTEXT);
				weightsInDimension.put(instrument, weightInDimension);
			}
		}
		return weightsInDimension;
	}

	
}

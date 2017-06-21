package com.kalinya.harness;

import java.util.Date;

import com.kalinya.assetallocation.AllocationDimension;
import com.kalinya.assetallocation.AllocationDimensions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.StringUtil;

public class TestHarness {

	public static void main(String[] args) {
		/*InstrumentType cash = new InstrumentType("cAsh");
		InstrumentType swap = new InstrumentType("SWAP");
		InstrumentType swap2 = new InstrumentType("SWAp");
		InstrumentType bond = new InstrumentType("generic option");
		Set<InstrumentType> instrumentTypesAsTreeSet = new TreeSet<>();
		instrumentTypesAsTreeSet.add(cash);
		instrumentTypesAsTreeSet.add(swap);
		instrumentTypesAsTreeSet.add(swap2);
		instrumentTypesAsTreeSet.add(bond);
		Set<InstrumentType> instrumentTypesAsHashSet = new HashSet<>();
		instrumentTypesAsHashSet.add(cash);
		instrumentTypesAsHashSet.add(swap2);
		instrumentTypesAsHashSet.add(swap);
		instrumentTypesAsHashSet.add(bond);
		System.out.println(String.format("InstrumentTypesAsHashSet: %s", instrumentTypesAsHashSet.toString()));
		System.out.println(String.format("InstrumentTypesAsTreeSet: %s", instrumentTypesAsTreeSet.toString()));
		System.out.println(String.format("Swap v Swap2 CompareTo: %s", swap.compareTo(swap2)));
		System.out.println(String.format("Swap2 v Swap CompareTo: %s", swap2.compareTo(swap)));
		System.out.println(String.format("Swap v Swap2 Equals: %s", swap.equals(swap2)));
		System.out.println(String.format("Bond v Swap CompareTo: %s", bond.compareTo(swap)));
		System.out.println(String.format("Swap v Bond CompareTo: %s", swap.compareTo(bond)));
		System.out.println(String.format("Bond v Swap Equals: %s", swap.equals(bond)));*/
		
		AllocationDimensions<AllocationDimension> dims = AllocationDimensions.create();
		AllocationDimension cashAllocation = AllocationDimension.create("cAsh");
		AllocationDimension cashAllocation2 = AllocationDimension.create("CASH");
		AllocationDimension govtBondAllocation = AllocationDimension.create("GovT BOND");
		AllocationDimension corpBondAllocation = AllocationDimension.create("CoRP BoNd");
		dims.add(cashAllocation);
		dims.add(cashAllocation2);
		dims.add(govtBondAllocation);
		dims.add(corpBondAllocation);
		System.out.println(String.format("AllocationDimensions: %s", dims.toVerboseString()));
		System.out.println(String.format("Contains(cashAllocation): %s", dims.contains(cashAllocation)));
		System.out.println(String.format("Contains(cashAllocation2): %s", dims.contains(cashAllocation2)));
		dims.remove(cashAllocation2);
		System.out.println(String.format("Contains(cashAllocation): %s", dims.contains(cashAllocation)));
		System.out.println(String.format("Contains(cashAllocation2): %s", dims.contains(cashAllocation2)));
		
		
	}

	@SuppressWarnings("unused")
	private static void printDate(String s) {
		Date date = DateUtil.parseDate(s);
		System.out.println("Input [" + s + "] Ouput [" + StringUtil.formatDate(date) + "]");
	}

}

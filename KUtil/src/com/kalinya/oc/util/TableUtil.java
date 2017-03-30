package com.kalinya.oc.util;

import com.olf.openjvs.OException;
import com.olf.openjvs.enums.OLF_RETURN_CODE;

public class TableUtil {
	/**
	 * Returns true if the jvs table is not null and is valid
	 * 
	 * @param jvsTable
	 * @return
	 * @throws OException
	 */
	public static boolean isValidTable(com.olf.openjvs.Table jvsTable) {
		try {
			return jvsTable != null
					&& com.olf.openjvs.Table.isTableValid(jvsTable) == OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt();
		} catch (OException e) {
			throw new RuntimeException(e);
		}
	}
}

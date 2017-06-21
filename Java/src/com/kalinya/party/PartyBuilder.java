package com.kalinya.party;

import com.kalinya.oc.util.MessageLog;
import com.kalinya.util.PluginUtil;
import com.olf.openjvs.OException;
import com.olf.openjvs.Ref;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.staticdata.EnumBusinessUnitFieldId;
import com.olf.openrisk.staticdata.EnumLegalEntityFieldId;
import com.olf.openrisk.staticdata.EnumPartyGroupFieldId;
import com.olf.openrisk.staticdata.EnumPartyStatus;
import com.olf.openrisk.staticdata.EnumReferenceObject;
import com.olf.openrisk.staticdata.EnumReferenceTable;
import com.olf.openrisk.staticdata.PartyFunction;
import com.olf.openrisk.staticdata.ReferenceChoice;
import com.olf.openrisk.staticdata.ReferenceChoices;
import com.olf.openrisk.staticdata.StaticDataFactory;
import com.olf.openrisk.staticdata.WritableBusinessUnit;
import com.olf.openrisk.staticdata.WritableLegalEntity;
import com.olf.openrisk.staticdata.WritablePartyGroup;
import com.olf.openrisk.staticdata.WritableReferenceObject;

public class PartyBuilder {
	private MessageLog messageLog;
	private Session session;
	private final static String BUSINESS_UNIT_SUFFIX = "";
	private final static String LEGAL_ENTITY_SUFFIX = " - LEGAL";

	public PartyBuilder(MessageLog messageLog) {
		setMessageLog(messageLog);
		setSession(messageLog.getSession());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		return sb.toString();
	}
	
	private void setMessageLog(MessageLog messageLog) {
		this.messageLog = messageLog;
	}
	
	public MessageLog getMessageLog() {
		return messageLog;
	}
	
	private void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}

	public void createIssuer(String partyGroupName) {
		createParty(partyGroupName, getPartyFunctionsForIssuers());
	}
	
	public void createIssuer(String businesUnitName, String legalEntityName) {
		String partyGroupName = businesUnitName.replace(" - BU", "");
		partyGroupName = businesUnitName.replace("-BU", "");
		createParty(partyGroupName, businesUnitName, legalEntityName, getPartyFunctionsForIssuers());
	}
	
	private void createParty(String partyGroupName, ReferenceChoices partyFunctions) {
		String businessUnitName = partyGroupName + BUSINESS_UNIT_SUFFIX;
		String legalEntityName = partyGroupName + LEGAL_ENTITY_SUFFIX;
		createParty(partyGroupName, businessUnitName, legalEntityName, getPartyFunctionsForIssuers());
	}
	
	private void createParty(String partyGroupName, String businessUnitName, String legalEntityName, ReferenceChoices partyFunctions) {
		if(partyExists(businessUnitName) && partyExists(legalEntityName)) {
			return;
		}
		
		if(partyExists(businessUnitName) && !partyExists(legalEntityName)) {
			throw new UnsupportedOperationException("UnsupportedOperation: The BusinessUnit [" + businessUnitName + "] exists but the LegalEntity [" + legalEntityName + "] does not exist");
		}
		
		if(!partyExists(businessUnitName) && partyExists(legalEntityName)) {
			throw new UnsupportedOperationException("UnsupportedOperation: The LegalEntity [" + legalEntityName + "] exists but the BusinessUnit [" + businessUnitName + "] does not exist");
		}

		getMessageLog().info("Creating Structure: Group [" + partyGroupName + "] BUnit [" + businessUnitName + "] LEntity [" + legalEntityName + "]");
		boolean success = false;
		WritablePartyGroup partyGroup = null;
		WritableBusinessUnit businessUnit = null;
		WritableLegalEntity legalEntity = null;
		try {
			if(!partyGroupExists(partyGroupName)) {
				partyGroup = (WritablePartyGroup) session.getStaticDataFactory().createWritableReferenceObject(EnumReferenceObject.PartyGroup, partyGroupName);
				partyGroup.getField(EnumPartyGroupFieldId.IsExternal).setValue(true);
				partyGroup.save();
			} else {
				partyGroup = (WritablePartyGroup) session.getStaticDataFactory().getWritableReferenceObject(EnumReferenceObject.PartyGroup, partyGroupName);
			}

			if(!partyExists(businessUnitName)) {
				businessUnit = session.getStaticDataFactory().createWritableBusinessUnit(businessUnitName.toUpperCase(), partyGroup);
				businessUnit.getField(EnumBusinessUnitFieldId.Status).setValue(EnumPartyStatus.Authorized.getName());
				businessUnit.getField(EnumBusinessUnitFieldId.LongName).setValue(businessUnitName);
				businessUnit.getField(EnumBusinessUnitFieldId.PartyFunction).setSelection(partyFunctions);
				businessUnit.save();
			}
			PluginUtil.dispose(businessUnit);
			businessUnit = (WritableBusinessUnit) session.getStaticDataFactory().getWritableReferenceObject(EnumReferenceObject.BusinessUnit, businessUnitName.toUpperCase());
			doubleCheckBusinessUnitFunctions(businessUnit, partyFunctions);

			if(!partyExists(legalEntityName)) {
				legalEntity = session.getStaticDataFactory().createWritableLegalEntity(legalEntityName.toUpperCase(), partyGroup);
				legalEntity.getField(EnumLegalEntityFieldId.Status).setValue(EnumPartyStatus.Authorized.getName());
				legalEntity.getField(EnumLegalEntityFieldId.LongName).setValue(legalEntityName);
				legalEntity.save();
			} else {
				legalEntity = (WritableLegalEntity) session.getStaticDataFactory().getWritableReferenceObject(EnumReferenceObject.LegalEntity, legalEntityName.toUpperCase());
			}

			setDefaultLegalEntity(businessUnit, legalEntity);
			success = true;
		} finally {
			PluginUtil.dispose(partyGroup);
			PluginUtil.dispose(businessUnit);
			PluginUtil.dispose(legalEntity);
			if(success) {
				getMessageLog().info("Successfully created Group [" + partyGroupName + "]");
			}
		}
	}

	/**
	 * Bug: sometimes only the first BU function is written to the db. Try
	 * again.
	 * 
	 * @param businessUnit
	 * @param partyFunctions
	 */
	private void doubleCheckBusinessUnitFunctions(WritableBusinessUnit businessUnit, ReferenceChoices partyFunctions) {
		PartyFunction[] assignedPartyFunctions = businessUnit.getFunctions();
		int originalFunctionCount = assignedPartyFunctions.length;
		businessUnit.getField(EnumBusinessUnitFieldId.PartyFunction).setSelection(partyFunctions);
		int newFunctionCount = businessUnit.getFunctions().length;
		if(newFunctionCount > originalFunctionCount) {
			businessUnit.save();
		}
	}

	/**
	 * OC is missing a facility to link a BU and its LE as a default
	 * 
	 * @param businessUnit
	 * @param legalEntity
	 */
	private void setDefaultLegalEntity(WritableBusinessUnit businessUnit, WritableLegalEntity legalEntity) {
		com.olf.openjvs.Table partiesToUpdate = null;
		com.olf.openjvs.Table partyDetails = null;
		try {
			partyDetails = com.olf.openjvs.Table.tableNew();
			
			//Update BusinessUnit
			partiesToUpdate = getPartiesTable(businessUnit);
			Ref.exportParties(partiesToUpdate, partyDetails);
			boolean updateRequired = false;
			if(partyDetails.getInt("def_legal_flag", 1) != 1) {
				updateRequired = true;
				partyDetails.setInt("def_legal_flag", 1, 1);
			}
			if(!legalEntity.getName().equalsIgnoreCase(partyDetails.getString("def_legal_name", 1))) {
				updateRequired = true;
				partyDetails.setString("def_legal_name", 1, legalEntity.getName());
			}
			if(updateRequired) {
				Ref.importPartyTable(partyDetails);
			}
			
			//Update LegalEntity
			partiesToUpdate = getPartiesTable(legalEntity);
			Ref.exportParties(partiesToUpdate, partyDetails);
			updateRequired = false;
			if(partyDetails.getInt("def_bunit_flag", 1) != 1) {
				updateRequired = true;
				partyDetails.setInt("def_bunit_flag", 1, 1);
			}
			if(!businessUnit.getName().equalsIgnoreCase(partyDetails.getString("def_bunit_name", 1))) {
				updateRequired = true;
				partyDetails.setString("def_bunit_name", 1, businessUnit.getName());
			}
			if(updateRequired) {
				Ref.importPartyTable(partyDetails);
			}
		} catch (Exception e) {
			throw new OpenRiskException(e);
		} finally {
			PluginUtil.destroy(partiesToUpdate);
		}
	}

	private Table getPartiesTable(WritableReferenceObject referenceObject) throws OException {
		com.olf.openjvs.Table parties = com.olf.openjvs.Table.tableNew();
		parties.addCol("party_name", COL_TYPE_ENUM.COL_STRING);
		parties.addCol("party_class", COL_TYPE_ENUM.COL_INT);
		parties.addCol("party_id", COL_TYPE_ENUM.COL_INT);
		
		int rowNum = parties.addRow();
		parties.setString("party_name", rowNum, referenceObject.getName());

		return parties;
	}

	public boolean partyExists(String partyName) {
		return partyExists(EnumReferenceTable.Party, partyName);
	}
	
	public boolean partyGroupExists(String partyName) {
		return partyExists(EnumReferenceTable.PartyGroup, partyName);
	}
	
	private boolean partyExists(EnumReferenceTable referenceTable, String partyName) {
		ReferenceChoices referenceChoices = null;
		try {
			referenceChoices = getSession().getStaticDataFactory().getReferenceChoices(referenceTable);
			ReferenceChoice referenceChoice = referenceChoices.findChoice(partyName);
			return referenceChoice != null;
		} catch (Exception e) {
			//continue;
			return false;
		} finally {
			PluginUtil.dispose(referenceChoices);
		}
	}

	public ReferenceChoices getPartyFunctionsForIssuers() {
		StaticDataFactory sdf = getSession().getStaticDataFactory();
		ReferenceChoices partyFunctionsForIssuers = sdf.createReferenceChoices();
		ReferenceChoices allPartyFunctions = sdf.getReferenceChoices(EnumReferenceTable.FunctionType);
		
		partyFunctionsForIssuers.add(allPartyFunctions.findChoice("Issuer"));
		partyFunctionsForIssuers.add(allPartyFunctions.findChoice("Office"));
		partyFunctionsForIssuers.add(allPartyFunctions.findChoice("Reference Entity"));
		partyFunctionsForIssuers.add(allPartyFunctions.findChoice("Trading"));
		
		return partyFunctionsForIssuers;
	}
}

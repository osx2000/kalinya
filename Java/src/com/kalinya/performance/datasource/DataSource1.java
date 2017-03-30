package com.kalinya.performance.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.kalinya.oc.util.MessageLog;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.enums.DataSourceType;
import com.kalinya.util.StringUtil;

public final class DataSource1 implements Serializable {

	public enum Predefined {
		Csv(DataSource1.CSV, "CSV"),
		FindurUserTable(DataSource1.FINDUR_USER_TABLE, "Findur User Table"),
		FindurPmm(DataSource1.FINDUR_PMM, "Findur Performance Measurement Module"),
		FindurAcsDesktop(DataSource1.FINDUR_ACS_DESKTOP, "Findur ACS Desktop"),
		;

		private final DataSource1 dataSource;
		private final String name;

		Predefined(final DataSource1 source, String name) {
			this.dataSource = source;
			this.name = name;
		}

		/**
		 * Gets the data source.
		 *
		 * @return the format.
		 */
		public DataSource1 getDataSource() {
			return dataSource;
		}

		public String getName() {
			return name;
		}

		public static Predefined fromName(String name) {
			for(Predefined predefined: values()) 
				if(predefined.getName().equalsIgnoreCase(name)) {
					return predefined;
				}
			throw new IllegalArgumentException(String.format("Unknown name [%s]", name));
		}
	}

	/**
	 * Default format with no preset parameters
	 */
	public static final DataSource1 DEFAULT = new DataSource1(null, false, null,
			null, new ArrayList<String>(), null, null, null, null, null, null, null);

	/**
	 * Comma separated format
	 *
	 * <p>
	 * Settings are:
	 * </p>
	 * <ul>
	 * <li>tbd</li>
	 * </ul>
	 *
	 * @see #DEFAULT
	 */
	public static final DataSource1 CSV = DEFAULT
			.withDataSourceType(DataSourceType.CSV)
			.withPositionsFilePath(Configurator.POSITIONS_FILE_PATH);
	public static final DataSource1 FINDUR_USER_TABLE = DEFAULT.withDataSourceType(DataSourceType.FINDUR_USER_TABLE)
			.withRequiresFindurSession()
			.withDataSourceTableName("USER_performance_results");
	public static final DataSource1 FINDUR_PMM = DEFAULT
			.withDataSourceType(DataSourceType.FINDUR_PMM)
			.withRequiresFindurSession();
	public static final DataSource1 FINDUR_ACS_DESKTOP = DEFAULT
			.withDataSourceType(DataSourceType.FINDUR_ACS_DESKTOP)
			.withRequiresFindurSession()
			.withMarketValueAcsRuleName(Configurator.ACS_RULE_NAME_MARKET_VALUE);

	private static final long serialVersionUID = 1L;

	/**
	 * Gets one of the predefined formats from {@link DataSource1.Predefined}.
	 *
	 * @param source
	 *            name
	 * @return one of the predefined sources
	 */
	public static DataSource1 valueOf(final String source) {
		return DataSource1.Predefined.valueOf(source).getDataSource();
	}

	private final DataSourceType dataSourceType;
	private final Date startDate;
	private final Date endDate;
	private Collection<String> portfolios;
	private final String positionsFilePath;
	private String securityMasterFilePath;
	private final String pricesFilePath;
	private String resultsExportFilePath;
	private final String dataSourceTableName;
	private String acsRuleNameMarketValue;
	private String exportUserTableName;
	private boolean requiresFindurSession;

	private DataSource1(final DataSourceType dataSourceType,
			final boolean requiresFindurSession, final Date startDate,
			final Date endDate, final Collection<String> portfolios,
			final String positionsfilePath,
			final String securityMasterFilePath, final String pricesFilePath,
			final String resultsExportFilePath, final String dataSourceTableName,
			String acsRuleNameMarketValue, String exportUserTableName) {
		this.dataSourceType = dataSourceType;
		this.requiresFindurSession = requiresFindurSession;
		this.startDate = startDate;
		this.endDate = endDate;
		this.portfolios = portfolios;
		this.positionsFilePath = positionsfilePath;
		this.securityMasterFilePath = securityMasterFilePath;
		this.pricesFilePath = pricesFilePath;
		this.resultsExportFilePath = resultsExportFilePath;
		this.dataSourceTableName = dataSourceTableName;
		this.acsRuleNameMarketValue = acsRuleNameMarketValue;
		this.exportUserTableName = exportUserTableName;
		validate();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final DataSource1 other = (DataSource1) obj;
		if (positionsFilePath != other.positionsFilePath) {
			return false;
		}
		//TODO: finish this.  use equalsBuilder
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((positionsFilePath == null) ? 0 : positionsFilePath.hashCode());
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
		if(getDataSourceType() != null) {
			sb.append(String.format("\nDataSourceType [%s]", getDataSourceType()));
		}
		sb.append(String.format("\nRequiresFindurSession [%s]", requiresFindurSession()));
		if(getStartDate() != null) {
			sb.append(String.format("\nStartDate [%s]", StringUtil.formatDate(getStartDate())));
		}
		if(getEndDate() != null) {
			sb.append(String.format("\nEndDate [%s]", StringUtil.formatDate(getEndDate())));
		}
		if(getPositionsFilePath() != null) {
			sb.append(String.format("\nPositionsFilePath [%s]", getPositionsFilePath()));
		}
		if(getPricesFilePath() != null) {
			sb.append(String.format("\nPricesFilePath [%s]", getPricesFilePath()));
		}
		if(getTableName() != null) {
			sb.append(String.format("\nTableName [%s]", getTableName()));
		}
		if(getAcsRuleNameMarketValue() != null) {
			sb.append(String.format("\nAcsRuleNameMarketValue [%s]", getAcsRuleNameMarketValue()));
		}
		if(getResultsExportFilePath() != null) {
			sb.append(String.format("\nResultsExportFilePath [%s]", getResultsExportFilePath()));
		}
		if(getExportUserTableName() != null) {
			sb.append(String.format("\nUserTableName [%s]", getExportUserTableName()));
		}
		return sb.toString();
	}

	/**
	 * Verifies the consistency of the parameters and throws an
	 * IllegalArgumentException if necessary.
	 * 
	 * @throws IllegalArgumentException
	 */
	private void validate() throws IllegalArgumentException {
	}

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public DataSource1 withDataSourceType(DataSourceType dataSourceType) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withRequiresFindurSession() {
		return withRequiresFindurSession(true);
	}

	public DataSource1 withRequiresFindurSession(boolean b) {
		return new DataSource1(dataSourceType, b, startDate, endDate,
				portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withPositionsFilePath(final String positionsFilePath) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withSecurityMasterFilePath(String securityMasterFilePath) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withDataSourceTableName(String tableName) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, tableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withMarketValueAcsRuleName(String acsRuleNameMarketValue) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withStartDate(Date startDate) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withEndDate(Date endDate) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public DataSource1 withPortfolios(Collection<String> portfolios) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}
	
	public DataSource1 withResultsExtractFilePath(String resultsExportFilePath) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}
	
	public DataSource1 withExportToUserTable(String exportUserTableName) {
		return new DataSource1(dataSourceType, requiresFindurSession, startDate,
				endDate, portfolios, positionsFilePath, securityMasterFilePath,
				pricesFilePath, resultsExportFilePath, dataSourceTableName, acsRuleNameMarketValue, exportUserTableName);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getPositionsFilePath() {
		return positionsFilePath;
	}
	
	public Collection<String> getPortfolios() {
		return portfolios;
	}
	
	public String getPortfoliosAsString() {
		if(getPortfolios().size() > 0) {
			String[] portfoliosArray = getPortfolios().toArray(new String[getPortfolios().size()]);
			return StringUtil.join(portfoliosArray, ",", "'");
		}
		return "";
	}

	public String getSecurityMasterFilePath() {
		return securityMasterFilePath;
	}

	public String getPricesFilePath() {
		return pricesFilePath;
	}
	
	public String getResultsExportFilePath() {
		return resultsExportFilePath;
	}
	
	public String getExportUserTableName() {
		return exportUserTableName;
	}

	public String getTableName() {
		return dataSourceTableName;
	}

	public String getAcsRuleNameMarketValue() {
		return acsRuleNameMarketValue;
	}

	public boolean requiresFindurSession() {
		return requiresFindurSession;
	}
}

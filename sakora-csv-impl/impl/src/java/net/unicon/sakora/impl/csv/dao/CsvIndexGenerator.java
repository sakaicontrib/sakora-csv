package net.unicon.sakora.impl.csv.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.db.api.SqlService;

/**
 * For some reason Hibernate &lt;database-object&gt; config doesn't seem
 * to be picked up during auto-ddl, so define indices here. Effectively
 * the same thing you find in the CM impl (<code>IndexGenerator</code> there).
 * 
 *
 */
public class CsvIndexGenerator {

	private static final Log log = LogFactory.getLog(CsvIndexGenerator.class);
	private SqlService sqlService;
	private ServerConfigurationService serverConfigurationService;
	
	public void init() {
		if(log.isInfoEnabled()) log.info("init()");
		if(serverConfigurationService.getBoolean("auto.ddl", true)) {
			sqlService.ddl(this.getClass().getClassLoader(), "sakora_csv_indices");
		}
	}
	
	public void destroy() {
		if(log.isInfoEnabled()) log.info("destroy()");
	}
	
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}

	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}
	
}

package org.ekstep.graph.service.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ekstep.graph.service.common.DACConfigurationConstants;
import org.ekstep.graph.service.common.DACErrorCodeConstants;
import org.ekstep.graph.service.common.DACErrorMessageConstants;
import org.ekstep.graph.service.common.GraphOperation;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.exceptions.ClientException;

import com.ilimi.common.logger.PlatformLogger;

public class DriverUtil {

	
	private static Map<String, Driver> driverMap = new HashMap<String, Driver>();
	
	public static Driver getDriver(String graphId, GraphOperation graphOperation) {
		PlatformLogger.log("Get Driver for Graph Id: ", graphId);
		String driverKey = graphId + DACConfigurationConstants.DEFAULT_GRAPH_ID_AND_GRAPH_OPERATION_SEPARATOR + StringUtils.lowerCase(graphOperation.name());
		PlatformLogger.log("Driver Configuration Key: " + driverKey);
		
		Driver driver = driverMap.get(driverKey);
		if (null == driver) {
			driver = loadDriver(graphId, graphOperation);
			driverMap.put(driverKey, driver);
		}
		return driver;
	}

	public static Driver loadDriver(String graphId, GraphOperation graphOperation) {
		PlatformLogger.log("Loading driver for Graph Id: ", graphId);
		String driverType = DACConfigurationConstants.NEO4J_SERVER_DRIVER_TYPE;
		if (StringUtils.isBlank(driverType))
			throw new ClientException(DACErrorCodeConstants.INVALID_DRIVER.name(),
					DACErrorMessageConstants.INVALID_DRIVER_TYPE + " | [Driver Initialization Failed.]");
		PlatformLogger.log("Driver Type: " + driverType);
		
		Driver driver = null;
		switch (driverType) {
		case "simple":
		case "SIMPLE":
			PlatformLogger.log("Reading Simple Driver. | [Driver Initialization.]");
			driver = GraphDatabase.driver(RoutingUtil.getRoute(graphId, graphOperation), ConfigUtil.getConfig());
			break;
			
		case "medium":
		case "MEDIUM":
			PlatformLogger.log("Reading Medium Driver. | [Driver Initialization.]");
			driver = GraphDatabase.driver(RoutingUtil.getRoute(graphId, graphOperation), AuthTokenUtil.getAuthToken(), ConfigUtil.getConfig());
			break;
			
		case "complex":
		case "COMPLEX":
			PlatformLogger.log("Reading Complex Driver. | [Driver Initialization.]");
			driver = GraphDatabase.driver(RoutingUtil.getRoute(graphId, graphOperation), AuthTokenUtil.getAuthToken(),
					ConfigUtil.getConfig());
			break;

		default:
			PlatformLogger.log("Invalid Database (Bolt) Driver Type: " + driverType + " | [Default Driver Type is ]");
			driver = GraphDatabase.driver(RoutingUtil.getRoute(graphId, null));
			break;
		}
		if (null != driver)
			registerShutdownHook(driver);
		return driver;
	}
	
	private static void registerShutdownHook(Driver driver) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Closing Neo4j Graph Driver...");
                if (null != driver)
                	driver.close();
            }
        });
    }

}

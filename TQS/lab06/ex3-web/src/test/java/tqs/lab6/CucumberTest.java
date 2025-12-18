package tqs.lab6;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * Test runner for Web automation Cucumber features
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tqs/lab6")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "tqs.lab6")
public class CucumberTest {
}

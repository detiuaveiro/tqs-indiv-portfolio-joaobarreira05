package pt.zeromonos.garbagecollection;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // Diz onde estão os ficheiros .feature
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "pt.zeromonos.garbagecollection.steps") // Diz onde estão as Step Definitions
public class CucumberTestRunner {
    // Esta classe fica vazia. Serve apenas como ponto de entrada para o JUnit.
}

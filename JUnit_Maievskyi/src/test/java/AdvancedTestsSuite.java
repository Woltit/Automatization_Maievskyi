import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(ItemRentalCalculatorTest.class)
@IncludeTags({"slow", "dynamic"})
public class AdvancedTestsSuite {
}
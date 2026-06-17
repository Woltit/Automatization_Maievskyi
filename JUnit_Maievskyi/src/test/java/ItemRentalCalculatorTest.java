import code.ItemRentalCalculator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class ItemRentalCalculatorTest {

    private final ItemRentalCalculator calculator = new ItemRentalCalculator();

    @Test
    @Tag("fast")
    @DisplayName("Простий тест: базова вартість оренди")
    void testBasicCalculation() {

        assumeTrue(calculator.isPromotionSystemActive(),
                "Тест перервано: система розрахунку знижок наразі недоступна");

        assertEquals(100.0, calculator.calculateRent(50.0, 2), "Ціна за 2 дні по 50 має бути 100");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7})
    @Tag("fast")
    @DisplayName("Параметризований (1 параметр): оренда без знижки")
    void testNoDiscountForShortRentals(int days) {
        double pricePerDay = 10.0;
        assertEquals(pricePerDay * days, calculator.calculateRent(pricePerDay, days));
    }

    @ParameterizedTest
    @MethodSource("provideDiscountData")
    @Tag("slow")
    @DisplayName("Параметризований (набір): перевірка 15% знижки")
    void testDiscountCalculation(double price, int days, double expectedTotal) {
        assertEquals(expectedTotal, calculator.calculateRent(price, days), 0.01);
    }

    private static Stream<Arguments> provideDiscountData() {
        return Stream.of(
                Arguments.of(10.0, 8, 68.0),
                Arguments.of(20.0, 10, 170.0),
                Arguments.of(50.0, 14, 595.0)
        );
    }

    @TestFactory
    @Tag("dynamic")
    @DisplayName("Динамічні тести: некоректна кількість днів")
    Stream<DynamicTest> testInvalidInputsDynamically() {
        int[] invalidDays = {0, -1, -5, -100};

        return java.util.Arrays.stream(invalidDays)
                .mapToObj(days -> dynamicTest("Перевірка для кількості днів: " + days, () -> {
                    Exception exception = assertThrows(IllegalArgumentException.class,
                            () -> calculator.calculateRent(10.0, days));
                    assertEquals("Кількість днів має бути більшою за 0", exception.getMessage());
                }));
    }
}
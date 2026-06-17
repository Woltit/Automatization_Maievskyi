package code;

public class ItemRentalCalculator {
    public double calculateRent(double pricePerDay, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Кількість днів має бути більшою за 0");
        }
        double total = pricePerDay * days;
        if (days > 7) {
            total *= 0.85;
        }
        return total;
    }

    public boolean isPromotionSystemActive() {
        return true;
    }
}
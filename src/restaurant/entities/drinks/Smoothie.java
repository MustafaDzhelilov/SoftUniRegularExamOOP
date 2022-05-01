package restaurant.entities.drinks;

public class Smoothie extends BaseBeverage{

    private static final double SMOOTH_PRICE = 4.50;

    public Smoothie(String name, int counter, String brand) {
        super(name, counter, SMOOTH_PRICE, brand);
    }
}

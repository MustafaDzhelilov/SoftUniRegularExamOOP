package restaurant.core;

import restaurant.common.ExceptionMessages;
import restaurant.common.OutputMessages;
import restaurant.core.interfaces.Controller;
import restaurant.entities.drinks.Fresh;
import restaurant.entities.drinks.Smoothie;
import restaurant.entities.healthyFoods.Salad;
import restaurant.entities.healthyFoods.VeganBiscuits;
import restaurant.entities.healthyFoods.interfaces.HealthyFood;
import restaurant.entities.drinks.interfaces.Beverages;
import restaurant.entities.tables.InGarden;
import restaurant.entities.tables.Indoors;
import restaurant.entities.tables.interfaces.Table;
import restaurant.repositories.BeverageRepositoryImpl;
import restaurant.repositories.HealthFoodRepositoryImpl;
import restaurant.repositories.TableRepositoryImpl;
import restaurant.repositories.interfaces.*;

public class ControllerImpl implements Controller {

    private HealthFoodRepository<HealthyFood> healthFoodRepository;
    private BeverageRepository<Beverages> beverageRepository;
    private TableRepository<Table> tableRepository;
    double totalPrice;


    public ControllerImpl(HealthFoodRepository<HealthyFood> healthFoodRepository, BeverageRepository<Beverages> beverageRepository, TableRepository<Table> tableRepository) {
        this.healthFoodRepository = healthFoodRepository;
        this.beverageRepository = beverageRepository;
        this.tableRepository = tableRepository;
    }

    @Override
    public String addHealthyFood(String type, double price, String name) {
        HealthyFood healthyFood = this.healthFoodRepository.foodByName(name);
        if(price <= 0){
            throw new IllegalArgumentException(ExceptionMessages.INVALID_PRICE);
        }
        if(healthyFood != null){
            throw new IllegalArgumentException(String.format(ExceptionMessages.FOOD_EXIST,name));
        }

        if(type.equals("Salad")){
            healthyFood = new Salad(name, price);
        } else if(type.equals("VeganBiscuits")){
            healthyFood = new VeganBiscuits(name, price);
        }
        this.healthFoodRepository.add(healthyFood);
        return String.format(OutputMessages.FOOD_ADDED,name);
    }

    @Override
    public String addBeverage(String type, int counter, String brand, String name){
       Beverages beverages = this.beverageRepository.beverageByName(name,brand);
        if(counter <= 0){
            throw new IllegalArgumentException(ExceptionMessages.INVALID_COUNTER);
        }
       if(beverages != null){
           throw new IllegalArgumentException(String.format(ExceptionMessages.BEVERAGE_EXIST,name));
       }
       if(type.equals("Fresh")){
           beverages = new Fresh(name, counter, brand);
       } else if(type.equals("Smoothie")){
           beverages = new Smoothie(name, counter, brand);
       }
       this.beverageRepository.add(beverages);
        return String.format(OutputMessages.BEVERAGE_ADDED,type,brand);
    }

    @Override
    public String addTable(String type, int tableNumber, int capacity) {
        Table table = this.tableRepository.byNumber(tableNumber);
        if(capacity <= 0){
            throw new IllegalArgumentException(ExceptionMessages.INVALID_TABLE_SIZE);
        }
        if(table != null){
            throw new IllegalArgumentException(String.format(ExceptionMessages.TABLE_IS_ALREADY_ADDED,tableNumber));
        }
        if(type.equals("Indoors")){
            table = new Indoors(tableNumber,capacity);
        } else if(type.equals("InGarden")){
            table = new InGarden(tableNumber, capacity);
        }
        this.tableRepository.add(table);
        return String.format(OutputMessages.TABLE_ADDED,tableNumber);
    }

    @Override
    public String reserve(int numberOfPeople) {
        for (Table table : this.tableRepository.getAllEntities()) {
            if(!table.isReservedTable() && table.getSize() >= numberOfPeople){
                table.reserve(numberOfPeople);
                return String.format(OutputMessages.TABLE_RESERVED,table.getTableNumber(),numberOfPeople);
            }
        }
        return String.format(OutputMessages.RESERVATION_NOT_POSSIBLE,numberOfPeople);
    }

    @Override
    public String orderHealthyFood(int tableNumber, String healthyFoodName) {
       Table table = this.tableRepository.byNumber(tableNumber);
       if(table == null){
           return String.format(OutputMessages.WRONG_TABLE_NUMBER,tableNumber);
       }
       HealthyFood healthyFood = this.healthFoodRepository.foodByName(healthyFoodName);
       if(healthyFood == null){
           return  String.format(OutputMessages.NONE_EXISTENT_FOOD,healthyFoodName);
       }
       table.orderHealthy(healthyFood);
        return String.format(OutputMessages.FOOD_ORDER_SUCCESSFUL,healthyFoodName,tableNumber);
    }

    @Override
    public String orderBeverage(int tableNumber, String name, String brand) {
        Table table = this.tableRepository.byNumber(tableNumber);
        if(table == null){
            return String.format(OutputMessages.WRONG_TABLE_NUMBER,tableNumber);
        }
        Beverages beverages = this.beverageRepository.beverageByName(name, brand);
        if(beverages == null){
            return String.format(OutputMessages.NON_EXISTENT_DRINK,name, brand);
        }
        table.orderBeverages(beverages);
        return String.format(OutputMessages.BEVERAGE_ORDER_SUCCESSFUL,name, tableNumber);
    }

    @Override
    public String closedBill(int tableNumber) {
        Table table = this.tableRepository.byNumber(tableNumber);
        double billFromCurrentTable = table.bill();
        this.totalPrice += billFromCurrentTable;
        table.clear();

        return String.format(OutputMessages.BILL,tableNumber,billFromCurrentTable);
    }


    @Override
    public String totalMoney() {
        return String.format(OutputMessages.TOTAL_MONEY,this.totalPrice);
    }
}

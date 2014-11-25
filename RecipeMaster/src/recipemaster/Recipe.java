package recipemaster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 
 * @author Ryan Burns
 */
public class Recipe {
    protected String name;
    protected String meal;
    protected String type;
    protected ArrayList<String> ingredients;
    protected ArrayList<Double> quantities;
    protected Calendar lastMadeOn;
    protected Calendar createdOn;
    protected double rating;
    protected String notes;

    public Recipe(String name, String meal, String type, ArrayList<String> ingredients,
                    ArrayList<Double> quantities, GregorianCalendar lastMadeOn, GregorianCalendar createdOn, Double rating, String notes) {
        this.name = name;
        this.meal = meal;
        this.type = type;
        this.ingredients = ingredients;
        this.quantities = quantities;
        this.lastMadeOn = lastMadeOn;
        this.createdOn = createdOn;
        this.rating = rating;
        this.notes = notes;
    }

    public Recipe(String name, String meal, String type, ArrayList<String> ingredients,
            ArrayList<Double> quantities, GregorianCalendar lastMadeOn, Double rating) {
        this.name = name;
        this.meal = meal;
        this.type = type;
        this.ingredients = ingredients;
        this.quantities = quantities;
        this.lastMadeOn = lastMadeOn;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String menuPrint() {
        return "Item: " + name;
    }

    @Override
    public String toString() {
        StringBuilder ingrStr = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            ingrStr.append(ingredients.get(i))
                    .append(" x ")
                    .append(quantities.get(i));
            if (i != ingredients.size() - 1) {
                ingrStr.append(", ");
            }
        }
        StringBuilder dateStr = new StringBuilder();
        dateStr.append(Integer.toString(lastMadeOn.get(Calendar.MONTH)));
        dateStr.append(" ");
        dateStr.append(Integer.toString(lastMadeOn.get(Calendar.DAY_OF_MONTH)));
        dateStr.append(" ");
        dateStr.append(Integer.toString(lastMadeOn.get(Calendar.YEAR)));

        StringBuilder finalStr = new StringBuilder();
        finalStr.append("*\r\nItem:\r\n");
        finalStr.append(name);
        finalStr.append("\r\n\r\nMeal:\r\n");
        finalStr.append(meal);
        finalStr.append("\r\n\r\nType:\r\n");
        finalStr.append(type);
        finalStr.append("\r\n\r\nIngredients:\r\n");
        finalStr.append(ingrStr);
        finalStr.append("\r\n\r\nLastMade On:\r\n");
        finalStr.append(dateStr);
        finalStr.append("\r\n\r\nRating:\r\n");
        finalStr.append(rating);
        finalStr.append("\r\n\r\n");
        return finalStr.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Recipe)) {
            return false;
        } else {
            Recipe that = (Recipe) other;
            return that.getName().equals(name);
        }
    }
}
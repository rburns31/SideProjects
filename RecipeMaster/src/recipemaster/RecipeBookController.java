package recipemaster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class for RecipeBook
 * @author Ryan Burns
 */
public class RecipeBookController implements Initializable, ControlledScreen {
    @FXML private Button viewCartButton;
    @FXML private Button addToCartButton;
    @FXML private Button editCartButton;
    @FXML private Button checkOutButton;
    @FXML private Button addRecipeButton;
    @FXML private Button viewRecipeButton;
    @FXML private Button deleteRecipeButton;
    @FXML private Button sortButton;
    @FXML private ScrollPane listScrollPane;
    @FXML private VBox scrollVBox;
    @FXML private GridPane grid;

    private ArrayList<Recipe> recipes = new ArrayList<>();
    private ScreensController parentController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            readFromFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Recipe testRecipe = new Recipe("Lasagna", "Dinner", "Pasta", new ArrayList<>(Arrays.asList("Noodles", "Tomatoe Sauce", "Meatballs")),
                new ArrayList<>(Arrays.asList(1.0, 2.0, 5.0)), new GregorianCalendar(2014, 10, 5), 4.2);
        addToRecipes(testRecipe);
        System.out.print(toStringList(recipes));
        try {
            writeToFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        mainItemDisplay();
    }

    @Override
    public void setScreenParent(ScreensController parentController) {
        this.parentController = parentController;
    }

    private boolean addToRecipes(Recipe recipe) {
        boolean added = false;
        if (!recipes.contains(recipe)) {
            recipes.add(recipe);
            added = true;
        }
        return added;
    }

    private void readFromFile() throws IOException {
        try (BufferedReader input = new BufferedReader(new FileReader("recipes.txt"))) {
            int counter = 0;
            int curStart = 0;
            String line = input.readLine();
            while (line != null) {
                if (line.startsWith("*")) {
                    curStart = counter;
                    recipes.add(new Recipe("", "", "", new ArrayList<>(), new ArrayList<>(), null, 0.0));
                } else if (curStart + 2 == counter) {
                    recipes.get(recipes.size() - 1).name = line;
                } else if (curStart + 5 == counter) {
                    recipes.get(recipes.size() - 1).meal = line;
                } else if (curStart + 8 == counter) {
                    recipes.get(recipes.size() - 1).type = line;
                } else if (curStart + 11 == counter) {
                    String[] prelimSplit = line.split(", ");
                    for (String prelimSplit1: prelimSplit) {
                        String[] secondSplit = prelimSplit1.split(" x ");
                        recipes.get(recipes.size() - 1).ingredients.add(secondSplit[0]);
                        recipes.get(recipes.size() - 1).quantities.add(Double.parseDouble(secondSplit[1]));
                    }
                } else if (curStart + 14 == counter) {
                    String[] timeStuff = line.split(" ");
                    Calendar cal = new GregorianCalendar(Integer.parseInt(timeStuff[2]), Integer.parseInt((timeStuff[0])), Integer.parseInt(timeStuff[1]));
                    recipes.get(recipes.size() - 1).lastMadeOn = cal;
                } else if (curStart + 17 == counter) {
                    recipes.get(recipes.size() - 1).rating = Double.parseDouble(line);
                }
                line = input.readLine();
                counter++;
            }
        }
    }

    private void writeToFile() throws IOException {
        try (PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("recipes.txt")))) {
            output.print(toStringList(recipes));
        }
    }

    private String toStringList(ArrayList<Recipe> list) {
        StringBuilder finalStr = new StringBuilder();
        finalStr.append("Total Recipes: ");
        finalStr.append(recipes.size());
        finalStr.append("\r\n\r\n");
        for (Recipe r: list) {
            finalStr.append(r.toString());
        }
        return finalStr.toString();
    }

    private void oldCrap(Stage stage) {
        stage.setTitle("RecipeMaster");
        stage.setResizable(false);
        ArrayList<CheckBox> boxes = new ArrayList<>(Arrays.asList(new CheckBox("Alphabetic"),
                    new CheckBox("Least Recently Made"), new CheckBox("Recently Made"), new CheckBox("Highest Rating"),
                    new CheckBox("Lowest Rating"), new CheckBox("Recently Added"), new CheckBox("Containing: ")));
        for (CheckBox box: boxes) {
            box.setOnAction((ActionEvent event) -> {
                for (CheckBox thisBox: boxes) {
                    if (!thisBox.equals(box)) {
                        thisBox.setSelected(false);
                    }
                }
            });
        }
        TextField ingredientField = new TextField();
        ChoiceBox<String> mealList = new ChoiceBox<>(FXCollections.observableArrayList("Meal Type", "Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "Appetizer"));
        mealList.setValue("Meal Type");
    }

    public void mainItemDisplay() {
        for (int i = 1; i <= recipes.size(); i++) {
            grid.getRowConstraints().add(i, new RowConstraints(40));
            CheckBox check = new CheckBox();
            check.setPadding(new Insets(0, 0, 0, 2));
            grid.add(check, 0, i);
            Label label = new Label(recipes.get(i - 1).name);
            label.setPadding(new Insets(0, 0, 0, 10));
            grid.add(label, 1, i);
        }
    }

    @FXML
    private void viewCartButtonAction(ActionEvent event) {
        System.out.println("Viewing cart!");
    }
    @FXML
    private void addToCartButtonAction(ActionEvent event) {
        System.out.println("Adding to cart!");
    }
    @FXML
    private void editCartButtonAction(ActionEvent event) {
        System.out.println("Editing cart!");
    }
    @FXML
    private void checkOutButtonAction(ActionEvent event) {
        System.out.println("Checking out!");
    }
    @FXML
    private void addRecipeButtonAction(ActionEvent event) {
        parentController.setScreen("Recipe");
    }
    @FXML
    private void viewRecipeButtonAction(ActionEvent event) {
        System.out.println("Viewing recipe!");
    }
    @FXML
    private void deleteRecipeButtonAction(ActionEvent event) {
        System.out.println("Deleting recipe!");
    }
    @FXML
    private void sortButtonAction(ActionEvent event) {
        System.out.println("Sorting!");
    }
}
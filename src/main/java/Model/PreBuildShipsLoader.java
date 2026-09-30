package Model;

import Controller.Enums.MatchLevel;
import Model.Enums.Direction;
import Model.Factories.ComponentFactory;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreBuildShipsLoader {

    private static String SHIPS_PATH = "src/main/resources/ships.json";

    public static List<ShipBoard> loadPreBuiltShips(MatchLevel level) {


        try (FileReader reader = new FileReader(SHIPS_PATH)) {

            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            List<ShipBoard> ships = new ArrayList<>();

            for (JsonElement el : array) {

                JsonObject ship = el.getAsJsonObject();

                if (ship.has("ID")) {
                    if(ship.has("level")){

                        if(ship.get("level").getAsString().equals(level.toString())){

                            ShipBoard shipBoard = new ShipBoard();



                            if(ship.has("components")){

                                //carica tutti i componenti della nave
                                List<SpaceshipComponent> allTiles = ComponentLoader.loadComponents(false);

                                JsonArray components = ship.get("components").getAsJsonArray();
                                for (JsonElement componentEl : components) {
                                    JsonObject componentObj = componentEl.getAsJsonObject();

                                    if(componentObj.has("componentId")){
                                        SpaceshipComponent activeTile = allTiles.get(componentObj.get("componentId").getAsInt()-1);
                                        if(componentObj.has("direction")){
                                            Direction targetDirection = Direction.valueOf(componentObj.get("direction").getAsString());
                                            while(activeTile.getOrientation() != targetDirection){
                                                activeTile.rotate();
                                            }

                                            // Piazza il componente nella nave, se le coordinate esistono nel JSON, settando ShipBoard sul componente e facendo added
                                            if(componentObj.has("row")&&componentObj.has("col")){
                                                int i = componentObj.get("row").getAsInt();
                                                int j = componentObj.get("col").getAsInt();
                                                shipBoard.addComponent(activeTile, new Coordinates(i,j));
                                                activeTile.setShipBoard(shipBoard);
                                                activeTile.added();

                                            }else{
                                                throw new RuntimeException("Component JSON object missing 'row' or 'col' field: at Ship ID " + ship.get("ID").getAsString());
                                            }




                                        }else{
                                            throw new RuntimeException("Component JSON object missing 'direction' field: at Ship ID " + ship.get("ID").getAsString());
                                        }

                                    }else{
                                        throw new RuntimeException("Component JSON object missing 'ID' field: at Ship ID " + ship.get("ID").getAsString());
                                    }
                                }

                                ships.add(shipBoard);

                            }
                        }


                    }else{
                        throw new RuntimeException("Ship JSON object missing 'level' field:  at ID " + ship.get("ID").getAsString());
                    }
                }else{
                    throw new RuntimeException("JSON object missing 'ID' field");
                }



            }

            return ships;



        } catch (FileNotFoundException e){
            throw new RuntimeException("Ships JSON file not found at: " + SHIPS_PATH, e);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load spaceship components from JSON", e);
        }
    }

}

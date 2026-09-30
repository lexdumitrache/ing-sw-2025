package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.CardLevel;
import Model.Exceptions.InvalidMethodParameters;

import java.io.Serializable;

import com.google.gson.JsonObject;

// Classe per la gestione delle carte avventura
public abstract class AdventureCardFilip implements Serializable, Cloneable {
    private final int id;
    private final CardLevel level;
    private String imagePath;
    private String backCardImagePath;

    public AdventureCardFilip(int id, CardLevel level) {
        this.id = id;
        this.level = level;
        this.imagePath="";
        this.backCardImagePath="";
    }

    public AdventureCardFilip(JsonObject json) {
        if(json.has("id")){
            this.id= json.get("id").getAsInt();
        }else{
            throw new RuntimeException("No ID provided");
        }
        if(!json.has("level")){
            throw new RuntimeException("No level provided for card with ID: " + id);
        }


        this.level = CardLevel.valueOf(json.get("level").getAsString().toUpperCase());
        //this.level      = CardLevelMapper.mapJsonLevelToCardLevel(json.get("level").getAsString());


        if (json.has("imagePath")){
            this.imagePath  = json.get("imagePath").getAsString();
        }else{
            throw new RuntimeException("No image path provided");
        }
        if(level==CardLevel.LEVEL_THREE){
            throw new RuntimeException("AdventureCardFilip level three is not supported");
        }
        if(level==CardLevel.LEVEL_TWO){
            backCardImagePath= "src/main/resources/pics/cards/GT-cards_II_IT_0121.jpg";
        }else{
            backCardImagePath= "src/main/resources/pics/cards/GT-cards_I_IT_0121.jpg";
        }


    }

    public void visualize(){
        System.out.println("==========================");
        System.out.println("ID: " + this.id);
        System.out.println("Nome: " + this.getName());
        System.out.println("Livello: " + this.level);
        System.out.println("Immagine: " + this.imagePath);
    }

    public String[] visualizeString() {
        return null;
    }


    public String getImagePath() {
        return imagePath;
    }

    public String getBackCardImagePath() {
        return backCardImagePath;
    }

    public final int getId() {
        return id;
    }

    public final CardLevel getLevel() {
        return level;
    }

    public abstract String getName();

    public abstract String getDescription();

    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) throws InvalidMethodParameters, InvalidParameters {

    }

    @Override
    public AdventureCardFilip clone() {
        return this;
    }
}

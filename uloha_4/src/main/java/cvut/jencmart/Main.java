package cvut.jencmart;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        FileParser fp = new FileParser();
	    KnowledgeBase kb = fp.generateKnowledgeBaseFromFile ();
	    UserInterface ui = new UserInterface();

	    InferenceEngine ie = new InferenceEngine(kb, ui);

        try {
            ie.startInference();
        } catch (ActionException e) {
            System.out.println("Ending now...");
        }
    }
}

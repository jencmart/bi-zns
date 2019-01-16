package cvut.jencmart;

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

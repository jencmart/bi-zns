package cvut.jencmart;

public class Main {

    public static void main(String[] args) {
	KnowledgeBase kb = new KnowledgeBase();
	UserInterface ui = new UserInterface();

	InferenceEngine ie = new InferenceEngine(kb, ui);

	ie.startInference();
    }
}

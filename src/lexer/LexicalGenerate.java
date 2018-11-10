package lexer;

import java.io.File;

public class LexicalGenerate {

    public static void main(String[] args) {
        String path = "D:\\SourceCode\\AutomataProject\\LexicalAnalyzerGUI\\src\\lexer\\Lexer.flex";
        generarLexer(path);

    }

    public static void generarLexer(String path) {
        File file = new File(path);
        jflex.Main.generate(file);
    }
}

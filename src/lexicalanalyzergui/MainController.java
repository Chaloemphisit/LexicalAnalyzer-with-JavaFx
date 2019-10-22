/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexicalanalyzergui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.awt.Paint;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import lexer.*;

/**
 *
 * @author chaloemphisit
 */
public class MainController implements Initializable {

    @FXML
    private JFXButton btnClose;
    @FXML
    private JFXButton btnChooseFile;
    @FXML
    private JFXButton btnAnalyze;
    @FXML
    private JFXButton btnClear;
    @FXML
    private Label lblFilePath;
    @FXML
    private TextArea txtEditor;
    @FXML
    private JFXListView<TextFlow> listResult;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == btnClose) {
            System.exit(0);
        } else if (event.getSource() == btnChooseFile) {
            openDialog();
        } else if (event.getSource() == btnClear) {
            clearForm();
        } else if (event.getSource() == btnAnalyze) {
            probarLexerFile();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listResult.setExpanded(true);
        listResult.setVerticalGap(20.0);
        listResult.depthProperty().set(1);
    }

    private void addList(String header, String desc, boolean isError) {

        TextFlow tf = new TextFlow();
        Text t1 = new Text(header + "\n");
        t1.setStyle("-fx-font-weight: bold;");
        Text t2 = new Text("" + desc);
        if (isError) {
            t1.setFill(Color.RED);
            t2.setFill(Color.RED);
        }

        tf.getChildren().addAll(t1, t2);

        listResult.getItems().add(tf);
    }

    private void clearForm() throws IOException {
        lblFilePath.setText("Lexical Analyzer");
        txtEditor.setText("");
        listResult.getItems().clear();
    }

    private void openDialog() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF files");
//        fileChooser.setInitialDirectory(new File("D:\\SourceCode\\AutomataProject\\LexicalAnalyzer"));
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("All Files", "*"));

        File selectedFiles = fileChooser.showOpenDialog(stage);

        if (selectedFiles != null) {
            lblFilePath.setText("Lexical Analyzer [" + selectedFiles.getPath() + "]");
            txtEditor.setText(null);
            listResult.getItems().clear();

            BufferedReader br = new BufferedReader(new FileReader(selectedFiles.getPath()));

            String line, st = "";
            while ((line = br.readLine()) != null) {
                st = st + line + "\n";
            }
            txtEditor.setText(st);
            probarLexerFile();
        } else {
            lblFilePath.setText("Lexical Analyzer");
        }
    }
    List<String> tokenslist;

    public int probarLexerFile() throws IOException {

        if (txtEditor.getText() != null || !txtEditor.getText().trim().isEmpty()) //Create a temo file
        {
            Path temp = Files.createTempFile("inputTemp", ".tmp");

            // Write data into temporary file
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp.toFile()));
            bw.write(txtEditor.getText());
            bw.close();

            tokenslist = new LinkedList<>();
            Reader reader = new BufferedReader(new FileReader(temp.toFile()));
            Lexer lexer = new Lexer(reader);
            listResult.getItems().clear();
            while (true) {
                Token token = lexer.yylex();
                if (token == null) {
                    reader.close();
                    Files.delete(temp);
                    return 1;
                }
                switch (token) {
                    case OPERATOR:
                        addList("Operator", lexer.lexeme, false);
                        break;
                    case PUNCTUATION:
                        addList("Punctuation", lexer.lexeme, false);
                        break;
                    case KEYWORD:
                        addList("Keyword", lexer.lexeme, false);
                        break;
                    case INTEGER:
                        addList("Integer", lexer.lexeme, false);
                        break;
                    case IDENTIFIER: {
                        if (tokenslist.indexOf(lexer.lexeme) != -1) {
                            addList("Identifier", "\"" + lexer.lexeme + "\" already in symbol table", true);
                        } else {
                            addList("New identifier", lexer.lexeme, false);
                        }
                        tokenslist.add(lexer.lexeme);
                        break;
                    }
                    case STRINGS:
                        addList("String", lexer.lexeme, false);
                        break;
                    case ERROR:
                        addList("Error", "Unrecognized symbol", true);
                        break;
                    default:
                        addList("Nothing", "", false);
                }
            }
        }
        return 0;
    }

}

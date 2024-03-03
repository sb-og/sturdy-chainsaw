package testerArtifact;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.gson.*; // Załóżmy, że mamy bibliotekę Gson

public class BrowserDetectorApp extends JFrame {
    private JComboBox<String> browsersComboBox;

    public BrowserDetectorApp() {
        setTitle("Browser Detector");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        browsersComboBox = new JComboBox<>();
        add(browsersComboBox);

        fillComboBoxWithBrowsers();
    }

    private void fillComboBoxWithBrowsers() {
        try {
            // Tworzenie i konfiguracja ProcessBuilder'a
            String workingDir = System.getProperty("user.dir");
            ProcessBuilder builder = new ProcessBuilder(
                    "powershell.exe",
                    "-ExecutionPolicy", "Bypass",
                    "-File", workingDir + "\\detect_browsers.ps1");
            builder.redirectErrorStream(true);

            // Uruchomienie procesu
            Process process = builder.start();

            // Czytanie wyników wyjścia procesu
            StringBuilder jsonOutput = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonOutput.append(line);
            }

            // Oczekiwanie na zakończenie procesu i sprawdzenie kodu wyjścia
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Parsowanie danych JSON i aktualizacja JComboBox
                JsonArray browsersArray = JsonParser.parseString(jsonOutput.toString()).getAsJsonArray();
                for (JsonElement browserElement : browsersArray) {
                    JsonObject browserObject = browserElement.getAsJsonObject();
                    String browserInfo = browserObject.get("Name").getAsString() + " - " +
                                         browserObject.get("Version").getAsString();
                    browsersComboBox.addItem(browserInfo);
                }
            } else {
                System.out.println("Skrypt nie został wykonany poprawnie.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BrowserDetectorApp app = new BrowserDetectorApp();
            app.setVisible(true);
        });
    }
}

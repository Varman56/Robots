package backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import backend.WindowStatus;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SaveManager {
    private final String filePath;
    private final Gson gson;
    private Map<String, WindowStatus> windowsStatus;

    public SaveManager(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.windowsStatus = load();
    }

    public void saveWindow(Component window, String windowName) {
        windowsStatus.put(windowName, new WindowStatus(window));
        saveToFile();
    }

    public boolean loadWindow(Component window, String windowName) {
        WindowStatus windowSettings = windowsStatus.get(windowName);
        if (windowSettings != null) {
            windowSettings.apply(window);
            return true;
        }
        return false;
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(windowsStatus, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, WindowStatus> load() {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, WindowStatus>>() {
            }.getType();
            Map<String, WindowStatus> loaded = gson.fromJson(reader, type);
            return loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
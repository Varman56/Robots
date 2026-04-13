package backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

public class SaveManager {
    private final String FILEPATH = "WindowsPosition.json";
    private final Gson gson;
    private Map<WindowId, WindowStatus> windowsStatus;

    public SaveManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.windowsStatus = load();
    }

    public void saveWindow(Component window, WindowId windowId) {
        windowsStatus.put(windowId, new WindowStatus(window));
        saveToFile();
    }

    public void loadWindow(Component window, WindowId windowId) {
        WindowStatus status = windowsStatus.getOrDefault(windowId, new WindowStatus(windowId.getDefaults()));
        status.apply(window);
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILEPATH)) {
            gson.toJson(windowsStatus, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<WindowId, WindowStatus> load() {
        File file = new File(FILEPATH);
        if (!file.exists()) return new EnumMap<>(WindowId.class);

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<WindowId, WindowStatus>>() {}.getType();
            Map<WindowId, WindowStatus> loaded = gson.fromJson(reader, type);
            return loaded != null ? new EnumMap<>(loaded) : new EnumMap<>(WindowId.class);
        } catch (IOException e) {
            return new EnumMap<>(WindowId.class);
        }
    }
}
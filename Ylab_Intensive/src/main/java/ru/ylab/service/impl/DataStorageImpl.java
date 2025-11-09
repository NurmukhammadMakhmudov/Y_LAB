package main.java.ru.ylab.service.impl;

import main.java.ru.ylab.model.AppData;
import main.java.ru.ylab.service.DataStorage;

import java.io.*;


public class DataStorageImpl implements DataStorage {
    private static final String DATA_FILE = "marketplace_data.ser";
    private static final String BACKUP_FILE = "marketplace_data_backup.ser";


    @Override
    public void save(AppData data) {
        try {
            // Создаём бэкап предыдущей версии (на случай ошибки)
            createBackup();

            // Сохраняем новые данные
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(DATA_FILE))) {
                oos.writeObject(data);
                System.out.println("Данные сохранены: " + data);
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public AppData load() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            System.out.println("Файл данных не найден. Создаём новую базу данных.");
            return new AppData();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            AppData data = (AppData) ois.readObject();
            System.out.println("Данные загружены: " + data);
            return data;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());

            // Пытаемся загрузить из бэкапа
            return loadBackup();
        }
    }


    private void createBackup() {
        File current = new File(DATA_FILE);
        File backup = new File(BACKUP_FILE);

        if (current.exists()) {
            try {
                copyFile(current, backup);
            } catch (IOException e) {
                System.err.println("Не удалось создать бэкап: " + e.getMessage());
            }
        }
    }


    private AppData loadBackup() {
        File backup = new File(BACKUP_FILE);

        if (!backup.exists()) {
            System.err.println("Резервная копия не найдена. Создаём новую базу.");
            return new AppData();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(BACKUP_FILE))) {
            AppData data = (AppData) ois.readObject();
            System.out.println("Данные восстановлены из резервной копии: " + data);
            return data;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Не удалось загрузить бэкап. Создаём новую базу.");
            return new AppData();
        }
    }

    private void copyFile(File source, File dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
    }


    @Override
    public boolean dataExists() {
        return new File(DATA_FILE).exists();
    }


    @Override
    public void deleteAll() {
        new File(DATA_FILE).delete();
        new File(BACKUP_FILE).delete();
        System.out.println("Все файлы данных удалены");
    }
}

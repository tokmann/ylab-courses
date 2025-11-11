package ru.ylab.tasks.task1.service.persistence;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserFileService {

    private final String fileName;

    public UserFileService(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Загружает пользователей из файла.
     * Формат строки: login|password|role
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;
                users.add(new User(parts[0], parts[1], Role.valueOf(parts[2])));
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки пользователей: " + e.getMessage());
        }
        return users;
    }

    /**
     * Сохраняет всех пользователей в файл.
     */
    public void saveUsers(Collection<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (User u : users) {
                writer.write(u.getLogin() + "|" + u.getPassword() + "|" + u.getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения пользователей: " + e.getMessage());
        }
    }
}

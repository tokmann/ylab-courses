package ru.ylab.tasks.task1.repository;

import ru.ylab.tasks.task1.constant.Role;
import ru.ylab.tasks.task1.model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserRepository {

    private final Map<String, User> users = new HashMap<>();
    private final String FILE_NAME = "users.txt";

    public void save(User user) {
        users.put(user.getLogin(), user);
    }

    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(users.get(login));
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public boolean existsByLogin(String login) {
        return users.containsKey(login);
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;
                save(new User(parts[0], parts[1], Role.valueOf(parts[2])));
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки пользователей: " + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (User u : users.values()) {
                writer.write(u.getLogin() + "|" + u.getPassword() + "|" + u.getRole());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка сохранения пользователей: " + e.getMessage());
        }
    }
}

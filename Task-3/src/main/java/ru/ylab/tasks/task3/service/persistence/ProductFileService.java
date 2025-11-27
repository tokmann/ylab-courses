package ru.ylab.tasks.task3.service.persistence;

import ru.ylab.tasks.task3.model.Product;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.ylab.tasks.task3.constant.FileConstants.*;

/**
 * Сервис для работы с файлами продуктов.
 * Обеспечивает загрузку и сохранение списка {@link Product} в указанный файл.
 */
public class ProductFileService {

    private final String fileName;

    public ProductFileService(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Загружает продукты из файла.
     * Формат строки: id|name|category|brand|price|description
     * @return список продуктов, загруженных из файла (может быть пустым)
     */
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return products;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SPLIT_REGEX);
                if (parts.length < 6) {
                    System.out.println("Строка " + line + " пропущена: некорректный формат");
                    continue;
                }
                Product p = new Product(
                        Long.parseLong(parts[0]),
                        parts[1],
                        parts[2],
                        parts[3],
                        new BigDecimal(parts[4]),
                        parts[5]
                );
                products.add(p);
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки продуктов: " + e.getMessage());
        }
        return products;
    }

    /**
     * Сохраняет все продукты в файл.
     */
    public void saveProducts(Collection<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Product p : products) {
                writer.write(p.getId() + DELIMITER +
                        p.getName() + DELIMITER +
                        p.getCategory() + DELIMITER +
                        p.getBrand() + DELIMITER +
                        p.getPrice() + DELIMITER +
                        p.getDescription().replace("\n", " "));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка сохранения продуктов: " + e.getMessage());
        }
    }
}

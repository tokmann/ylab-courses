package ru.ylab.tasks.task1.repository.jdbc;

import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.ylab.tasks.task1.constant.SqlConstants.*;

/**
 * Реализация репозитория продуктов на основе JDBC.
 * Обеспечивает сохранение, поиск, обновление и удаление продуктов в базе данных.
 */
public class JdbcProductRepository implements ProductRepository {

    private final Connection connection;

    public JdbcProductRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохраняет продукт в базе данных.
     * Если продукт новый (id = null), выполняет вставку, иначе - обновление.
     * @param product продукт для сохранения
     */
    @Override
    public void save(Product product) {
        try {
            if (product.getId() == null) {
                try (PreparedStatement ps = connection.prepareStatement(INSERT_PRODUCT)) {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getCategory());
                    ps.setString(3, product.getBrand());
                    ps.setBigDecimal(4, product.getPrice());
                    ps.setString(5, product.getDescription());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        product.setId(rs.getLong("id"));
                    }
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(UPDATE_PRODUCT)) {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getCategory());
                    ps.setString(3, product.getBrand());
                    ps.setBigDecimal(4, product.getPrice());
                    ps.setString(5, product.getDescription());
                    ps.setLong(6, product.getId());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения продукта: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает все продукты из базы данных.
     * @return коллекция всех продуктов
     */
    @Override
    public Collection<Product> findAll() {
        List<Product> list = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_PRODUCTS)) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Находит продукт по его идентификатору.
     * @param id идентификатор продукта
     * @return Optional с продуктом, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Product> findById(Long id) {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapProduct(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет продукт по идентификатору.
     * @param id идентификатор продукта для удаления
     */
    @Override
    public void deleteById(Long id) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_PRODUCT_BY_ID)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Находит все продукты указанной категории.
     * @param category категория для поиска
     * @return коллекция продуктов указанной категории
     */
    @Override
    public Collection<Product> findByCategory(String category) {
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Находит все продукты указанного бренда.
     * @param brand бренд для поиска
     * @return коллекция продуктов указанного бренда
     */
    @Override
    public Collection<Product> findByBrand(String brand) {
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_PRODUCTS_BY_BRAND)) {
            ps.setString(1, brand);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Находит все продукты в указанном диапазоне цен.
     * @param min минимальная цена (включительно)
     * @param max максимальная цена (включительно)
     * @return коллекция продуктов в указанном ценовом диапазоне
     */
    @Override
    public Collection<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_PRODUCTS_BY_PRICE_RANGE)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Возвращает минимальную цену среди всех продуктов.
     * @return Optional с минимальной ценой, или empty если продуктов нет
     */
    @Override
    public Optional<BigDecimal> getMinPrice() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(SELECT_MIN_PRICE)) {
            if (rs.next()) return Optional.ofNullable(rs.getBigDecimal("min_price"));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Возвращает максимальную цену среди всех продуктов.
     * @return Optional с максимальной ценой, или empty если продуктов нет
     */
    @Override
    public Optional<BigDecimal> getMaxPrice() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(SELECT_MAX_PRICE)) {
            if (rs.next()) return Optional.ofNullable(rs.getBigDecimal("max_price"));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Преобразует ResultSet в объект Product.
     * @param rs ResultSet с данными продукта
     * @return объект Product
     */
    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("brand"),
                rs.getBigDecimal("price"),
                rs.getString("description")
        );
    }
}

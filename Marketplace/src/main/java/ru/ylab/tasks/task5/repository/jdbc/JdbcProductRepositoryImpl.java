package ru.ylab.tasks.task5.repository.jdbc;

import org.springframework.stereotype.Repository;
import ru.ylab.tasks.task5.exception.DatabaseException;
import ru.ylab.tasks.task5.model.Product;
import ru.ylab.tasks.task5.repository.ProductRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.ylab.tasks.task5.constant.SqlConstants.*;

/**
 * Реализация репозитория продуктов на основе JDBC.
 * Обеспечивает сохранение, поиск, обновление и удаление продуктов в базе данных.
 */
public class JdbcProductRepositoryImpl implements ProductRepository {

    private final DataSource dataSource;

    public JdbcProductRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет продукт в базе данных.
     * Если продукт новый (id = null), выполняет вставку, иначе - обновление.
     * @param product продукт для сохранения
     */
    @Override
    public void save(Product product) {
        try (Connection conn = dataSource.getConnection()) {
            if (product.getId() == null) {
                try (PreparedStatement ps = conn.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getCategory());
                    ps.setString(3, product.getBrand());
                    ps.setBigDecimal(4, product.getPrice());
                    ps.setString(5, product.getDescription());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            product.setId(rs.getLong(1));
                        }
                    }
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(UPDATE_PRODUCT)) {
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
            throw new DatabaseException("Ошибка сохранения продукта: ", e);
        }
    }

    /**
     * Возвращает все продукты из базы данных.
     * @return коллекция всех продуктов
     */
    @Override
    public Collection<Product> findAll() {
        List<Product> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_ALL_PRODUCTS)) {
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка получения всех продуктов: ", e);
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapProduct(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска продукта по id: ", e);
        }
    }

    /**
     * Удаляет продукт по идентификатору.
     * @param id идентификатор продукта для удаления
     */
    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_PRODUCT_BY_ID)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка удаления продукта: ", e);
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PRODUCTS_BY_CATEGORY)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска продуктов по категории: ", e);
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PRODUCTS_BY_BRAND)) {
            ps.setString(1, brand);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска продуктов по бренду: ", e);
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_PRODUCTS_BY_PRICE_RANGE)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска продуктов по диапазону цен: ", e);
        }
        return list;
    }

    /**
     * Возвращает минимальную цену среди всех продуктов.
     * @return Optional с минимальной ценой, или empty если продуктов нет
     */
    @Override
    public Optional<BigDecimal> getMinPrice() {
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_MIN_PRICE)) {
            if (rs.next()) return Optional.ofNullable(rs.getBigDecimal("min_price"));
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка получения минимальной цены: ", e);
        }
    }

    /**
     * Возвращает максимальную цену среди всех продуктов.
     * @return Optional с максимальной ценой, или empty если продуктов нет
     */
    @Override
    public Optional<BigDecimal> getMaxPrice() {
        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(SELECT_MAX_PRICE)) {
            if (rs.next()) return Optional.ofNullable(rs.getBigDecimal("max_price"));
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка получения максимальной цены: ", e);
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

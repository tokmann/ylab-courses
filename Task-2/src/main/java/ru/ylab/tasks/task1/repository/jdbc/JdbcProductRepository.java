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


public class JdbcProductRepository implements ProductRepository {

    private final Connection connection;

    public JdbcProductRepository(Connection connection) {
        this.connection = connection;
    }

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

    @Override
    public void deleteById(Long id) {
        try (PreparedStatement ps = connection.prepareStatement(DELETE_PRODUCT_BY_ID)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

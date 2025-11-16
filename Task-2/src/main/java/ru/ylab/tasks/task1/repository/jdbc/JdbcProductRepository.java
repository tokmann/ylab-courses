package ru.ylab.tasks.task1.repository.jdbc;

import ru.ylab.tasks.task1.model.Product;
import ru.ylab.tasks.task1.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JdbcProductRepository implements ProductRepository {

    private final Connection connection;

    public JdbcProductRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Product product) {
        try {
            if (product.getId() == null) {
                String sql = "INSERT INTO marketplace.products (name, category, brand, price, description) " +
                        "VALUES (?, ?, ?, ?, ?) RETURNING id";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getCategory());
                    ps.setString(3, product.getBrand());
                    ps.setBigDecimal(4, product.getPrice());
                    ps.setString(5, product.getDescription());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.next()) {
                            product.setId(rs.getLong("id"));
                        }
                    }
                }
            } else {
                String sql = "UPDATE marketplace.products SET name=?, category=?, brand=?, price=?, description=? WHERE id=?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "SELECT id, name, category, brand, price, description FROM marketplace.products";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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
        String sql = "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "DELETE FROM marketplace.products WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Product> findByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE category=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE brand=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "SELECT id, name, category, brand, price, description FROM marketplace.products WHERE price BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "SELECT MIN(price) AS min_price FROM marketplace.products";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return Optional.ofNullable(rs.getBigDecimal("min_price"));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BigDecimal> getMaxPrice() {
        String sql = "SELECT MAX(price) AS max_price FROM marketplace.products";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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

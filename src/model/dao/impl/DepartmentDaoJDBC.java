package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {
    private Connection conn;

    public DepartmentDaoJDBC (Connection conn) { this.conn = conn; }

    @Override
    public void insert(Department department) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(
                    "INSERT INTO department " +
                            "(Name)" +
                            "VALUES (?)", Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, department.getName());

            int rowsAffected = statement.executeUpdate();

            if(rowsAffected > 0) {
                ResultSet resultSet = statement.getGeneratedKeys();

                if(resultSet.next()) {
                    department.setId(resultSet.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(
              "UPDATE department SET Name = ? WHERE Id = ?"
            );

            statement.setString(1, department.getName());
            statement.setInt(2, department.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(
              "DELETE FROM department WHERE Id = ?"
            );

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = conn.prepareStatement(
                    "SELECT department.* from department"
            );

            resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Department department = instantiateDepartment(resultSet);
                return department;
            }

            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(statement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = conn.prepareStatement("SELECT * FROM department ORDER BY Name");
            resultSet = statement.executeQuery();

            List<Department> list = new ArrayList<>();

            while(resultSet.next()) {
                Department dep = instantiateDepartment(resultSet);
                list.add(dep);
            }

            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException{
        Department department = new Department();
        department.setName(resultSet.getString("Name"));
        department.setId(resultSet.getInt("Id"));

        return department;
    }
}

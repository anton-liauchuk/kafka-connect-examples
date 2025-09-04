package com.uuidable.container;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerService {

	private final DBConnectionProvider connectionProvider;

	public CustomerService(DBConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		createCustomersTableIfNotExists();
	}

	public void createCustomer(Customer customer) {
		try (var conn = this.connectionProvider.getConnection();
				var pstmt = conn.prepareStatement(
						"insert into customers(id,name) values(?,?)"
				)) {
			pstmt.setLong(1, customer.id());
			pstmt.setString(2, customer.name());
			pstmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void createCustomersTableIfNotExists() {
		try (Connection conn = this.connectionProvider.getConnection();
				var pstmt = conn.prepareStatement(
						"""
								create table if not exists customers (
									id bigint not null,
									name varchar not null,
									primary key (id)
								)
								"""
				)) {
			pstmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}

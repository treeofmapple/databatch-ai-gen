package com.tom.service.datagen.model;

import java.time.Duration;

import com.tom.service.datagen.model.product.CategoryProduct;
import com.tom.service.datagen.model.product.Product;
import com.tom.service.datagen.model.product.PurchaseHistory;
import com.tom.service.datagen.model.product.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor	
public class ProductRecomendation {

	private Long id;
	private Product product;
	private User user;
	private CategoryProduct category;
	private PurchaseHistory history;
	private Duration timeSpent;
	private int clicks;
}

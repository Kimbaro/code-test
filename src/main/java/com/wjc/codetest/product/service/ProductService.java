package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/*
문제: 해당 클래스 구조로 인해 상용 운영에서 기능 확장/분기 추가 시 변경 영향 범위가 커질 수 있습니다 (설계)
원인:
- 현재 구조는 클래스 1개로 비지니스로직을 구현할 계획으로 보입니다.
- 기능이 늘어난다는 것은 코드가 그만큼 늘어나고 추후 로직이 너무 복잡해질 수 있습니다.
- 고객사/도메인별 분기가 필요해질 경우 Context 주입/구현체 분리가 필요해질 수 있습니다.
개선안:
- 대안:
  - ProductService를 Interface로 두고, 구현은 Impl로 분리하여 확장 지점을 명확히 함
- 선택 근거:
  - 상용 시스템에서는 기능이 추가되고 코드가 늘어나는 등 확장성을 고려해야 합니다.
  - Product 중심으로 예측하지 못한 새로운 기능이 제시될 수 있고 반대로 제거될 수 있습니다.
  - 이때, Interface 중심으로 다양한 비지니스 로직을 분리한다면 변경 영향 범위를 줄이고 테스트/운영 대응을 쉽게 할 수 있습니다.
- 트레이드오프:
  - 초기 구조가 늘어날 수 있지만, 확장 가능성이 높은 도메인에서는 장기 비용을 절감할 수 있습니다.
*/

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    public Product update(UpdateProductRequest dto) {
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    public Page<Product> getListByCategory(GetProductListRequest dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}
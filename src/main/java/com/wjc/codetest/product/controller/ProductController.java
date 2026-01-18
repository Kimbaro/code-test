package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 문제: HTTP 표준 미준수 및 계층 간 객체 간섭 (설계)
 * 원인:
 * - 수정/삭제에 POST를 사용하여 HTTP Method가 불일치합니다.
 * - URI 경로에 'get', 'create' 등 동사를 포함하여 리소스 중심의 REST 규칙을 위반합니다.
 * - DB 엔티티를 API 응답으로 직접 반환하여 내부 스키마가 외부에 노출됩니다.
 * - 컨트롤러에서 응답 객체를 직접 생성하여 프리젠테이션 계층에서 관리 포인트가 증가합니다.
 * 개선안:
 * - URI는 명사로 구성하고, 행위는 Method(GET, POST, PATCH, DELETE)로 구분합니다.
 *      (ex> GET /station/{id}, POST /station, PATCH /station/{id} ...)
 * - 응답 전용 DTO를 사용하여 엔티티 정보를 은닉하고 API 규격과 분리합니다.
 * - DTO 변환 로직은 DTO 내부의 정적 메서드를 활용하여 컨트롤러 코드를 간소화합니다.
 * - CRUD 함수를 직접 호출 하지말고 비지니스기능이 명시된 함수로 감싸서 호출합니다.
 * - 선택 근거:
 *   - 표준 규격 준수를 통한 유지보수 용이성을 고려하고, 스키마를 숨기면서 보안성을 강화합니다.
 *   - 엔티티와 API 스펙을 분리하여 DB 리팩토링 시 사이드 이펙트를 최소화합니다.
 * 검증:
 * - HTTP 상태 코드 및 Method 매핑 정상 동작을 확인합니다.
 * - 응답 JSON 내 불필요한 엔티티 필드 포함 여부를 검토합니다.
 */

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/create/product")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest dto){
        Product product = productService.create(dto);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequest dto){
        Product product = productService.update(dto);
        return ResponseEntity.ok(product);
    }

    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(@RequestBody GetProductListRequest dto){
        Page<Product> productList = productService.getListByCategory(dto);
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}
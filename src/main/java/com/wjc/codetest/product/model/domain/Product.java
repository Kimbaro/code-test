package com.wjc.codetest.product.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/*
 * 문제: Entity와 DTO 역할 미분리 및 명명 규칙 부재 (설계/가독성)
 * 원인:
 * - 클래스명이 'Product'로 Entity인지 DTO인지 역할이 불명확합니다.
 * - Lombok @Getter 사용 중인데 수동으로 getter 메서드를 재정의하여 코드 중복이 발생합니다.
 * - @Setter 사용으로 Entity의 모든 필드를 다른 모듈에서 수정가능합니다(DB 데이터는 절대 수정토록 하면 안 됩니다!).
 * - Entity와 DTO가 분리되지 않아 계층 간 분리가 모호합니다.
 * - GenerationType.AUTO 사용으로 DB별 ID 생성 전략이 불명확합니다.
 * 개선안:
 * - 대안:
 *   - Entity는 'ProductEntity'로 명명하고 불변성을 유지합니다.
 *   - DTO는 별도 클래스로 분리하여 'ProductDto', 'ProductResponse' 등으로 명명합니다.
 *   - DTO에서 @Data 사용, Entity에서는 @Getter만 사용하고 비즈니스 메서드를 제공합니다.
 * - 선택 근거:
 *   - 명확한 클래스 생성 규칙으로 코드 가독성을 향상하고, Entity와 DTO 분리로 계층 간 책임을 명확히 합니다.
 *   - Entity 불변성 확보로 예측 가능한 동작을 보장하고, DTO는 데이터 전송 목적으로만 사용합니다.
 *   - (참고: Entity vs DTO 차이, Layered Architecture, 명명 규칙)
 * - 트레이드오프:
 *   - DTO 클래스 추가로 코드량이 증가하나 계층 간 책임이 명확해지고 유지보수성이 대폭 향상됩니다.
 * 검증:
 * - Entity와 DTO 분리 후 계층 간 데이터 전달과 CRUD 기능 동작을 확인합니다.
 * - Entity 불변성 확보 여부를 확인합니다 (비즈니스 메서드로만 상태 변경).
 * - 클래스명으로 역할 구분 가능 여부를 확인합니다.
 *
 * */


@Entity
@Getter
@Setter
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}

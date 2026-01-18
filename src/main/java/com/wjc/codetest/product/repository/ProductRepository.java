package com.wjc.codetest.product.repository;

import com.wjc.codetest.product.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
문제: Repository 메서드와 파라미터가 모호해 유지보수 시 실수할 수 있습니다 (가독성/설계)
원인:
- `findAllByCategory(String name, Pageable pageable)`에서 파라미터명이 `name`이라, 엔티티의 `name` 필드와 혼동될 수 있습니다.
  (메서드명은 category인데 변수명은 name이라 읽는 사람이 아차 하고 넘어갈 수 있습니다.)
- `findDistinctCategories()`는 DISTINCT로 중복 제거는 되지만 정렬 기준이 명시되어 있지 않아 호출 결과 순서가 고정되지 않을 수 있습니다.
- 추후 정렬 기준이 다양해지는 경우(가나다순/빈도순/최근순 등), 단순 ORDER BY 추가로는 대응이 어렵고 로직이 점점 복잡해질 수 있습니다.
개선안:
- 대안:
  - `findAllByCategory`의 파라미터명을 `category`로 맞춰 의도를 코드에 고정합니다.
  - `findDistinctCategories`는 정렬 기준을 문자열로 바로 받기 보다는, 허용된 정렬 옵션을 정의(Enum/화이트리스트)하고 옵션에 따라 정렬/쿼리를 분기할 수 있도록 확장 지점을 명확히 합니다.
    (예: CATEGORY_ASC, CATEGORY_DESC, FREQUENCY_DESC 등)
- 선택 근거:
  - 상용 시스템에서는 요구사항이 계속 추가될 수 있고, 정렬 기준도 예측하지 못한 형태로 늘어날 수 있습니다.
  - 파라미터명/정렬옵션을 명확히 해두면 주니어가 다른 길로 빠지지 않고 “여기를 고치면 된다”는 포인트를 쉽게 찾을 수 있습니다.
  - 결과 순서가 고정되면 UI/테스트/캐시에서도 흔들림이 줄어듭니다.
- 트레이드오프:
  - 초기에는 코드/구조가 늘어날 수 있지만, 확장 가능성이 높은 도메인에서는 장기적으로 운영/유지보수 비용을 줄일 수 있습니다.
검증(생략 가능):
- `findAllByCategory` 파라미터명 변경 후 호출부 컴파일/테스트로 영향 범위를 확인합니다.
- `findDistinctCategories`는 동일 데이터 기준으로 동일 옵션을 주면 항상 동일한 순서가 나오는지 확인합니다.
- 옵션을 잘못 주면 4xx로 떨어지는지(허용 옵션 검증) 확인합니다.

참고용 샘플코드:
- 변경 전 : Page<Product> findAllByCategory(String name, Pageable pageable);
- 변경 후 : Page<Product> findAllByCategory(String category, Pageable pageable);

- 정렬 옵션 예시: CATEGORY_ASC / CATEGORY_DESC / FREQUENCY_DESC
- (빈도순은 DISTINCT만으로는 어렵고 GROUP BY + COUNT 같은 형태를 고려해야 합니다.)
* */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCategory(String name, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}

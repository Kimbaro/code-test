문제: 패키지 구조가 역할을 충분히 드러내지 못해 DTO/Entity 경계가 모호합니다 (가독성/설계)
원인:
- 현재 구조가 `model/domain`, `model/request`, `model/response`로 나뉘어 있으나, `model`은 의미 범위가 넓어 Entity/VO/DTO 등 판단 기준이 모호합니다.
- request/response는 사실상 Controller 경계에서 사용하는 DTO인데, 패키지명만으로 DTO라는 의도가 고정되지 않습니다.
  
개선안:
- 대안:
  - 전송 객체는 `dto/request`, `dto/response`로 명확히 분리합니다.
  - 도메인객체와 DTO 객체는 명칭 마지막에 Entity, DTO를 기재하여 가독성을 높입니다.
    예) `product/domain/ProductEntity`
    `product/dto/request/CreateProductRequestDTO`
    `product/dto/response/ProductListResponseDTO`
- 선택 근거:
  - 패키지명이 클래스 유지보수 시, Controller 경계(dto)와 핵심 도메인(domain/entity)의 책임을 자연스럽게 구분하도록 유도할 수 있습니다.
- 트레이드오프:
  - 패키지 깊이가 증가하고, 최초 1회 리팩토링(클래스 이동/임포트 수정) 비용이 발생합니다.
검증(생략 가능):
- Controller가 Entity를 직접 노출하지 않고 DTO로 응답하는지(코드 리뷰 체크리스트로 확인합니다).

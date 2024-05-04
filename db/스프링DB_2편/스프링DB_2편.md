# 데이터 접근 기술 - 시작

## 데이터 접근 기술 진행 방식 소개

앞으로 실무에서 주로 사용하는 다음과 같은 다양한 데이터 접근 기술들을 학습한다.

**적용 데이터 접근 기술**

- JdbcTemplate
- MyBatis
- JPA, Hibernate
- 스프링 데이터 JPA
- Querydsl

여기에는 크게 2가지 분류가 있다.

**SQLMapper**

- JdbcTemplate
- MyBatis

**ORM 관련 기술**

- JPA, Hibernate
- 스프링 데이터 JPA
- Querydsl

**ORM 주요 기능**

- JdbcTemplate이나 MyBatis같은 SQL 매퍼 기술은 SQL을 개발자가 직접 작성해야 하지만, JPA를 사용하면 기본적으로 SQL은 JPA가 대신 작성하고 처리해준다. 개발자는 저장하고 싶은 객체를 마치 자바 컬렉션에 저장하고 조회하듯이 사용하면 ORM기술이 데이터베이스에 해당 객체를 저장하고 조회해준다.
- JPA는 자바 진영의 ORM 표준이고, HIbernate(하이버네이트)는 JPA에서 가장 많이 사용하는 구현체이다. 자바에서 ORM을 사용할 때는 JPA 인터페이스를 사용하고, 그 구현체로 하이버네이트를 사용한다고 생각하면 된다.
- 스프링 데이터 JPA, Quertdsl은 JPA를 더 편리하게 사용할 수 있게 도와주는 프로젝트이다. 실무에서는 JPA를 사용하면 이 프로젝트도 꼭! 함께 사용하는 것이 좋다.

### 학습 목표

데이터 저장 기술들은 하나하나 별도의 책이나 강의로 다루어야 할 정도로 내용이 방대하다. 특히 JPA의 경우 스프링과 학습 분량이 비슷할 정도로 공부해야 할 내용이 많다. 그래서 세세한 기능을 설명하기 보다는 주로 해당 기술이 왜 필요한지, 각 기술의 장단점은 무엇인지 설명하는데 초점을 맞춘다.

- 데이터 접근 기술에 대한 기본 예외와 전체 큰 그림을 그린다.
- 각 기술들의 핵심 기능 위주로 학습한다.
- 각 기술들을 점진적으로 도입하는 과정을 통해서 각 기술의 특징과 장단점을 자연스럽게 이해할 수 있다.

먼저 메모리 가반으로 완성되어 있는 프로젝트를 확인하고, 이 프로젝트에 데이터 접근 기술을 하나씩 추가해보자.

## 프로젝트 설정과 메모리 저장소

스프링 MVC 1에서 마지막에 완성한 상품 관리 프로젝트를 떠올려 보자. 해당 프로젝트는 단순히 메모리에 상품 데이터를 저장하도록 되어 있었다. 여기셍 메모리가 아닌 실제 데이터 접근 기술들을 하나씩 적용해가면서, 각각의 데이터 접근 기술들을 어떻게 사용하는지, 장단점은 무엇인지 코드로 이해하고 학습해보자.

MVC1편에서 개발한 상품 관리 프로젝트를 황용하자

## 프로젝트 구조 설명1 - 기본

### 도메인 분석

**item**

```java
@Data
public class Item {

    private Long id;

    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

`Item`은 상품 자체를 나타내는 객체이다. 이름, 가격, 수량을 속성으로 가지고 있다.

### 리포지토리 분석

**itemRepository 인터페이스**

```java
public interface ItemRepository {

    Item save(Item item);

    void update(Long itemId, ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond cond);
}
```

- 메모리 구현체에서 향후 다양한 데이터 접근 기술 구현체로 손쉽게 변경하기 위해 리포지토리에 인터페이스를 도입했다.
- 각각의 기느은 메서드 이름으로 충분히 이해가 될 것이다.
- findAll의 경우 itemSearchCond이 넘어간다. (상품명, 가격제한 검색조건으로 상품 목록을 조회해야 하기 때문이다)

**ItemSearchCond**

```java
@Data
public class ItemSearchCond {

    private String itemName;
    private Integer maxPrice;

    public ItemSearchCond() {
    }

    public ItemSearchCond(String itemName, Integer maxPrice) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
    }
}
```

- 검색 조건으로 사용된다. 검색 조건에는 상품명, 최대 가격이 있다. 참고로 상품명의 일부만 포함되어도 검색이 가능해야 한다. (`like` 검색)
- (참고) `cond` -> `condition`을 줄여서 사용했다. (이 프로젝트에서 검색 조건은 뒤에 `Cond`를 붙이도록 규칙을 정했다.)

**ItemUpdateDto**

```java
@Data
public class ItemUpdateDto {
    private String itemName;
    private Integer price;
    private Integer quantity;

    public ItemUpdateDto() {
    }

    public ItemUpdateDto(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

- 상품을 수정할 때 사용하는 객체이다
- 단순히 데이터를 전달하는 용도로 사용되므로 DTO를 뒤에 붙였다. (DTO에 대한 설명은 아래에 참고)

**MemortyItemRepository

```java
package hello.itemservice.repository.memory;

@Repository
public class MemoryItemRepository implements ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); //static
    private static  long sequence = 0L; //static

    @Override
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();
        return store.values().stream()
                .filter(item -> {
                    if (ObjectUtils.isEmpty(itemName)) {
                        return true;
                    }
                    return item.getItemName().contains(itemName);
                }).filter(item -> {
                    if (maxPrice == null) {
                        return true;
                    }
                    return item.getPrice() <= maxPrice;
                }).collect(Collectors.toList());
    }

    public void clearStore() {
        store.clear();
    }
}
```

- `ItemRepository`인터페이스를 구현한 메모리 저장소이다.
- 메모리이기 때문에 자바를 다시 실행하면 기존에 저장된 데이터가 모두 사라진다
- `save`, `update`, `findById`는 쉽게 이해할 수 있을 것이다. 참고로 `findById`는 `Optional`을 반환해야 하기 때문에 `Optional.ofNullable`을 사용했다.
- `findAll`은 `ItemSearchCond` 이라는 검색 조건을 받아서 내부에서 데이터를 검색하는 기능을 한다. 데이터베이스를 보면 `where` 구문을 사용해서 필요한 데이터를 필터링하는 과정을 거치는 것이다.
  - 여기서 자바 스트림을 사용한다.
  - `itemName`이나, `maxPrice`가 `null`이거나 비었으면 해당 조건을 무시한다
  - `itemName`이나, `manPrice`에 값이 있을 때만 해당 조건으로 필터링 기능을 수행한다
- `clearStroe()`는 메모리에 저장된 `Item`을 모두 삭제해서 초기화한다. 테스트 용도로만 사용한다.

### [참고] DTO (data transfer object)

- 데이터 전송 객체
- DTO는 기능은 없고 주로 데이터 잔달만 하는 용도로 사용되는 객체를 뜻한다
  - 참고로 DTO에 기능이 있으면 안되는 가? 그것은 아니다. 객체의 주 목적이 데이터를 전송하는 것이라면 DTO라 할 수 있다.
- 객체 이름에 DTO를 꼭 붙여야 하는 것은 아니다. 대신 붙여두면 용도를 명확히 알 수 있다는 장점이 있다
- 이전에 설명한 `ItemSearchCond`도 DTO역할을 하지만, 이프로젝트에서 `Cond`는 검색 조건으로 사용한다는 규칙을 정했다. 따라서 DTO를 붙이지 않아도 된다. `ItemSearchComdDto`이렇게 하면 너무 복잡해진다. 그리고 Cond라는 것만 봐도 용도를 알 수 있다.
- 참고로 이런 규칙은 정해진 것 없기 때문에 해당 프로젝트 안에서 일관성 있게 규칙을 정하면 된다.(이 프로젝트에서는 Cond는 DTO라는 범위까지 포함한다고 규칙을 정한 것.)

### 서비스 분석

**itemService 인터페이스**

```java
package hello.itemservice.service;

public interface ItemService {

    Item save(Item item);

    void update(Long itemId, ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findItems(ItemSearchCond itemSearch);
}
```

- 서비스의 구현체를 쉽게 설명하기 위해 인터페이스를 사용했다
- 참고로 서비스는 구현체를 변경할 일이 많지는 않기 때문에 사실 서비스에 인터페이스를 잘 도입하지는 않는다
  - 여기서는 예제 설명 과정에서 구현체를 변경할 예정이어서 인터페이스를 도입했다.

**itemServiceV1**

```java
package hello.itemservice.service;

@Service
@RequiredArgsConstructor
public class ItemServiceV1 implements ItemService{

    private final ItemRepository itemRepository;

    @Override
    public Item save(Item item) {
        return null;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {

    }

    @Override
    public Optional<Item> findById(Long id) {
        return null;
    }

    @Override
    public List<Item> findItems(ItemSearchCond itemSearch) {
        return null;
    }
}
```

- `ItemServiceV1`서비스 구현체는 대부분의 기능을 단순히 리포지토리에 위임한다.

## 프로젝트 구조 설명2 - 설정

## 프로젝트 구조 설명3 - 테스트

## 데이터베이스 테이블 생성
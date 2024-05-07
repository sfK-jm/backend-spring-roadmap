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
        return itemRepository.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        itemRepository.update(itemId, updateParam);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> findItems(ItemSearchCond cond) {
        return itemRepository.findAll(cond);
    }
}

```

- `ItemServiceV1`서비스 구현체는 대부분의 기능을 단순히 리포지토리에 위임한다.

### 컨트롤러 분석

**HomeController**

```java
package hello.itemservice.web;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "redirect:/items";
    }
}
```

단순히 홈으로 요청이 왔을 때 `items`로 이동하는 컨트롤러이다.

**ItemController**

```java
package hello.itemservice.web;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public String items(@ModelAttribute("itemSearch") ItemSearchCond itemSearch, Model model) {
        List<Item> items = itemService.findItems(itemSearch);
        model.addAttribute("items", items);
        return "items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemService.findById(itemId).get();
        model.addAttribute("item", item);
        return "item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "addForm";
    }


    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemService.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemService.findById(itemId).get();
        model.addAttribute("item", item);
        return "editForm";
    }

    @PostMapping("{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute ItemUpdateDto updateParam) {
        itemService.update(itemId, updateParam);
        return "redirect:/items/{itemId}";
    }
}
```

- 상품을 CRUD하는 컨트롤러이다.

## 프로젝트 구조 설명2 - 설정

**스프링 부트 설정 분석**

### MemoryConfig - 빈 수동 등록

```java
package hello.itemservice.config;

@Configuration
public class MemoryConfig {

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new MemoryItemRepository();
    }
}
```

- `ItemServiceV1`, `MemoryItemRepository`를 스프링 빈으로 등록하고 생성자를 통해 의존관계를 주입한다.
- 참고로 여기서는 서비스와 리포지토리는 구현체를 편리하게 변경하기 위해, 이렇게 수동으로 빈을 등록했다.
- 컨트롤러는 컴포넌트 스캔을 사용한다.

### TestDataInit

```java
package hello.itemservice;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;

    /**
     * 확인용 초기 데이터 추가
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("test data init");
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
```

- 애플리케이션을 실행할 때 초기 데이터를 저장한다
- 리스트에서 데이터가 잘 나오는지 편리하게 확인할 용도로 사용한다.
  - 이 기능이 없으면 서버를 실행할 때 마다 데이터를 입력해야 리스트에 나타난다.(메모리여서 서버를 내리면 데이터가 제거된다)
- `@EventListener(ApplicationReadyEvent.class)`: 스프링 컨테이너가 완전히 초기화를 다 끝내고, 실행 준비가 되었을 때 발생하는 이벤트이다. 스프링이 이 시점에서 해당 애노테이션이 붙은 `initData()`메서드를 호출해준다. (참고로 스프링에서 발생하는 이벤트이기 때문에 TestDataInit가 스프링 빈으로 등록되어 있어야 initData가 호출된다.)
  - 참고로 이 기능 대신 `@PostConstruct`를 사용할 경우 AOP 같은 부분이 아직 다 처리되지 않은 시점에 호출될 수 있기 때문에, 간혹 문제가 발생할 수 있다. 예를 들어서 `@Transactional`과 관련된 AOP가 적용되지 않은 상태로 호출될 수 있다.
  - `@EventListener(ApplicationReadyEvent.class)`는 AOP를 포함한 스프링 컨테이너가 완전히 초기화 된 이후에 호출되기 때문에 이런 문제가 발생하지 않는다.

### ItemServiceApplication

```java
package hello.itemservice;

@Import(MemoryConfig.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}
}
```

- `@Import(MemoryConfig.class)`: 앞서 설정한 `MemoryConfig`를 설정 파일로 사용한다.
- `scanBasePackages = "hello.itemservice.web"`: 여기서는 컨트롤러만 컴포넌트 스캔을 사용하고, 나머지는 직접 수동 등록한다. 그래서 컴포넌트 스캔 경로를 `hello.itemservice.web`하위로 지정했다.
- `@Profile("local")`: 특정 프로필의 경우에만 해당 스프링 빈을 등록한다. 여기서는 `local`이라는 이름의 프로필이 사용되는 경우에만 `testDataInit`이라는 스프링 빈을 등록한다. 이 빈은 앞서 본 것인데, 편의상 초기 데이터를 만들어서 저장하는 빈이다.

### 프로필

스프링은 로딩 시점에 `application.properties`의 `spring.profiles.active`속성을 읽어서 프로필로 사용한다.<br>
이 프로필은 로컬(나의 PC), 운영환경, 테스트 실행 등등 다양한 환경에 따라서 다른 설정을 할 때 사용하는 정보이다.

예를 들어서 로컬PC에서는 로컬 PC에 설치된 데이터베이스에 접근해야 하고, 운영 환경에서는 운영 데이터베이스에 접근해야 한다면 서로 설정 정보가 달라야 한다. 심지어 환경에 따라서 다른 스프링 빈을 등록해야 할 수 도 있다. 프로필을 사용하면 이런 문제를 깔끔하게 해결할 수 있다.

#### main 프로필

`application.properties`<br>
```properties
spring.profiles.active=local
```

`application.properties`는 `/src/main`하위의 자바 객체를 실행할 때 (주로 `main()`)동작하는 스프링 설정이다. `spring.profiles.active=local`이라고 하면 스프링은 `local`이라는 프로필로 동작한다. 따라서 직전에 설정한 `	@Profile("local")`가 동작하고, `testDataInit`가 스프링 빈으로 등록된다.

#### test 프로필

```properties
spring.profiles.active=test
```

- 주로 테스트 케이스를 실행할 때 동작한다.
- `spring.profiles.active=test`로 설정하면 `test`라는 프로필로 동작한다. 이 경우 직전에 설명한 `@Profile("local")`는 프로필 정보가 맞지 않아서 동작하지 않는다. 따라서 `testDataInit`이라는 스프링 빈도 등록되지 않고, 초기 데이터도 추가하지 않는다.

프로필 기능을 사용해서 스프링으로 웹 애플리케이션을 로컬(`local`)에서 직접 실행할 때는 `testDataInit`이 스프링 빈으로 등록된다. 따라서 등록한 초기화 데이터를 편리하게 확인할 수 있다. 초기화 데이터 덕분에 편리한 점도 있지만, 테스트 케이스를 실행할 때는 문제가 될 수 있다. 테스트에서 이런 데이터가 들어있다면 오류가 발생할 수 있다. 예를 들어서 데이터를 하나 저장하고 전체 카운트를 확인하는데 1이 아니라 `testDataInit`때문에 데이터가 2건 추가되어서 3이 되는 것이다. 프로필 덕분에 테스트 케이스에서는 `test`프로필이 실행된다. 따라서 `TestDataInit`는 스프링 빈으로 추가되지 않고, 따라서 초기 데이터도 추가되지 않는다.

> [!TIP]
> 프로필에 대한 스프링 부트 공식 메뉴얼은 다음을 참고하자. (https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)

## 프로젝트 구조 설명3 - 테스트

### ItemRepositoryTest

```java
package hello.itemservice.repository;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @AfterEach
    void afterEach() {
        //MemoryItemRepository의 경우 제한적으로 사용
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }
    }

    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        // when
        Item savedItem = itemRepository.save(item);

        // then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long iteId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(iteId, updateParam);

        //then
        Item findItem = itemRepository.findById(iteId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        // 둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    void test(String itemName, Integer maxPrice, Item... items) {
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));
        assertThat(result).containsExactly(items);
    }
}
```

- `afterEach`: 테스트는 서로 영향을 주면 안된다. 따라서 각각의 테스트가 끝나고 나면 저장한 데이터를 제거해야 한다. `@AfterEach`는 각각의 테스트의 실행이 끝나는 시점에 호출된다. 여기서는 메모리 저장소를 완전히 삭제해서 다음 테스트에 영향을 주지 않도록 초기화 한다.
- 인터페이스에는 `clearStore()`가 없기 때문에 `MemoryItemRepository`인 경우에만 다운 케스팅을 해서 데이터를 초기화한다. 뒤에서 학습하겠지만, 실제 DB를 사용하는 경우에만 테스트가 끝난 후에 트랜잭션을 롤백해서 데이터를 초기화 할 수 있다.
- `save()`: 상품을 하나 저장하고 잘 저장되었는지 검증한다
- `updateItem()`: 상품을 하나 수정하고 잘 수정되었는지 검증한다.
- `findItems()`: 상품을 찾는 테스트이다.
  - 상품명과 상품 가격 조건을 다양하게 비교하는 것을 확인할 수 있다.
  - 문자의 경우 `null`조건도 있지만, 빈 문자(`""`)의 경우에도 잘 동작하는지 검증한다.

> [!NOTE]
> 인터페이스를 테스트하자. 여기서는 `MemoryItemRepository`구현체를 테스트 하는 것이 아니라 `ItemRepository`인터페이스를 테스트하는 것을 확인할 수 있다. 인터페이스를 대상으로 테스트하면 향후 다른 구현체로 변경되었을 때 해당 구현체가 잘 동작하는지 같은 테스트로 편리하게 검증할 수 있다.

## 데이터베이스 테이블 생성

이제부터 다양한 데이터 접근 기술들을 활용해서 메모리가 아닌 데이터베이스에 데이터를 보관하는 방밥을 알아보자

먼저 H2데이터베이스에 접근해서 item 테이블을 생성하자.

```sql
drop table if exists item CASCADE;
create table item
{
    id          brigint generated by default as identity,
    item_name   varchar(10),
    price       integer,
    quantity    integer,
    primary key (id)
};
```

- `generated by default as identity`
  - `identity`전략이라고 하는데, 기본 키 생성을 데이터베이스에 위임하는 방법이다. MySQL의 Auto increment와 같은 방법이다.
  - 여기서 PK로 사용되는 id는 개발자가 직접 지정하는 것이 아니라 비워두고 저장하면 된다. 그러면 데이터베이스가 순서대로 증가하는 값을 사용해서 넣어준다.

테이블을 생성했으면 잘 동작하는지 다음 SQL을 실행해보자

```sql
insert into item(item_name, price, quantity) values ('itemTest', 10000, 10);
select * from item;
```

### 참고 - 권장하는 식벽자 선택 전략

- 데이터베이스 기본 키는 다음 3가지 조건을 모두 만족해야 한다.
  - `null`값은 허용하지 않는다.
  - 유일해야 한다.
  - 변해선 안 된다.
  
- 테이블의 기본 키를 선택하는 전략은 크게 2가지가 있다
  - 자연 키 (natural key)
    - 비즈니스에 의미가 있는 키
    - 예: 주민등록번호, 이메일, 전화번호
  - 대리 키(surrogate key)
    - 비즈니스와 관련 없는 임의로 만들어진 키, 대체 키로도 불린다,
    - 예: 오라클 시퀀스, auto_increment, identity, 키생성 테이블 사용

**자연 키 보다는 대리 키를 권장한다**

자연키와 대리키는 일장일단이 있지만 될 수 이씅면 대리 키의 사용을 권장한다. 예를 들어 자연 키의 전화번호를 기본 키로 선택한다면 그 번호가 유일할 수는 있지만, 전화번호가 없을 수도 있고 전화번호가 변경될 수도 있다. 따라서 기본 키로 적당하지 않다. 문제는 주민등록번호처럼 그럴듯하게 보이는 값이다. 이 값은 `null`이 아니고 유일하며 변하지 않는다는 3가지 조건을 모두 만족하는 것 같다. 하지만 현실과 비즈니스 규칙은 생각보다 쉽게 변한다. 주민등록번호 조차도 여러가지 이유로 변경 될 수 있다.

**비즈니스 환경은 언젠가 변한다**

기본 키의 조건을 현재는 물론이고 미래까지 충족하는 자연 키를 찾기는 쉽지 않다. 대리 키는 비즈니스와 무관한 임의의 값이므롬 요구사항이 변경되어도 기본 키가 변경되는 일은 드물다. 대리 키를 기본 키로 사용하되 주민등록번호나 이메일처럼 자연 키의 후보가 되는 컬럼들은 필요에 따라 유니크 인덱스를 설정해서 사용하는 것을 권장한다.<br>참고로 JPA는 모든 엔티티에 일관된 방식으로 대리 키 사용을 권장한다. 비즈니스 요구사항은 계속해서 변하는데 테이블은 한 번 정의하면 변경하기 어렵다. 그러면에서 외부 풍파에 쉽게 흔들리지 않는 대리 키가 일반적으로 좋은 선택이라 생각한다.

# 데이터 접근 기술 - 스프링 JdbcTemplate

## JdbcTemplate 소개와 설정

SQL을 직접 사용하는 경우에 스프링이 제공하는 JdbcTemplate은 아주 좋은 선택지다.<br>JdbcTemplate은 JDBC를 매우 편리하게 사용할 수 있게 도와준다.

**장점**

- 설정의 편리함
  - JdbcTemplate은 `spring-jdbc`라이브러리에 포함되어 있는데, 이 라이브러리는 스프링으로 JDBC를 사용할 때 기본으로 사용하는 라이브러리이다. 그리고 별도의 복잡한 설정 없이 바로 사용할 수 있다
- 반복 문제 해결
  - JdbcTemplate은 템플릿 콜백 패턴을 사용해서, JDBC를 직접 사용할 때 발생하는 대부분의 반복 잡업을 대신 처리해준다
  - 개발자는 SQL을 작성하고, 전달할 파라미터를 정의하고, 응답 값을 매핑하기만 하면 된다.
  - 우리가 생각할 수 있는 대부분의 반복 작업을 대신 처리해준다.
    - 커넥션 획득
    - `statement`를 준비하고 실행
    - 결과를 반복해도록 루프를 실행
    - 커넥션 종료, `statement`, `resultset`종료
    - 트랜잭션 다루기 위한 커넥션 동기화
    - 예외 발생시 스프링 예외 변환기 실행

**단점**

- 동적 SQL을 해결하기 어렵다.

직접 JdbcTemplate을 설정하고 적용하면서 이해해보자.

### JdbcTemplate 설정

`build.gradle`추가

```gradle
//JdbcTemplate추가
implementation 'org.springframework.boot:spring-boot-starter-jdbc'
//H2 데이터베이스 추가
runtimeOnly 'com.h2database:h2'
```

- `org.springframework.boot:spring-boot-starter-jdbc`를 추가하면 JdbcTemplate이 들어있는 `spring-jdbc`가 라이브러리에 포함된다. (= JdbcTemplate은 `spring-jdbc`라이브러리만 추가하면 된다. 별도의 추가 설정 과정은 없다.)
- 여기서는 H2 데이터베이스에 접속해야 하기 때문에 H2 데이터베이스의 클라이언트 라이브러리(Jdbc Driver)도 추가하자. (`runtimeOnly 'com.h2database:h2'`)

## JdbcTemplate 적용1 - 기본

이제부터 본격적으로 JdbcTemplate을 사용해서 메모리에 저장하던 데이터를 데이터베이스에 저장해보자.

`ItemRepository` 인터페이스가 있으니 이 인터페이스를 기반으로 JdbcTemplate을 사용하는 새로운 구현체를 개발하자.

### JdbcTemplateItemRepositoryV1

```java
package hello.itemservice.repository;

/**
 * JdbcTemplate
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1  implements ItemRepository{

    private final JdbcTemplate template;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            //자동 증가 키
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }


    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?, price=?, quantity=? where id = ?";
        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId
        );
    }


    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = ?";
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";
        //동적 쿼리

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += "price <= ?";
            param.add(maxPrice);
        }

        log.info("sql={}", sql);
        return template.query(sql, itemRowMapper(), param.toArray());
    }

    private RowMapper<Item> itemRowMapper() {
        return ((rs, rownNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });
    }
}
```

### 코드분석

- **기본**
  - `JdbcTemplateItemRepositoryV1`은 `ItemRepository`인터페이스를 구현했다.
  - `this.template = new JdbcTemplate(dataSource)`
    - `JdbcTemplate`은 데이터소스(`dataSource`)가 필요하다.
    - `JdbcTemplateItemRepositoryV1()`생성자를 보면 `dataSource`를 의존 관계 주입 받고 생성자 내부에서 `JdbcTemplate`을 생성한다. 스프링에서는 `JdbcTemplate`을 사용할 때 관례상 이 방법을 많이 사용한다.
    - 물론 `JdbcTemplate`을 스프링 빈으로 직접 등록하고 주입받아도 된다.
- **save()**
  - 데이터를 저장한다
  - `template.update()`: 데이터를 변경할 때는 `update()`를 사용하면 된다. (여기서 말하는 변경이라는 것은 insert, update, delete를 모두 포함한다.)
    - `INSERT`, `UPDATE`, `DELETE` SQL에 사용한다.
    - `template.update()`의 반환 값은 `int`인데, 영향받은 로우 수를 반환한다.
  - 데이터를 저장할 때 PK생성에 `identity` (auto increment)방식을 사용하기 때문에, PK인 ID값을 개발자가 직접 지정하는 것이 아니라 비워두고 저장해야 한다. 그러면 데이터베이스가 PK인 ID를 대신 생성해준다.
  - 문제는 이렇게 데이터베이스가 대신 생성해주는 PK ID값은 데이터베이스가 생성하기 때문에, 데이터베이스에 INSERT가 완료되어야 생성된 PK ID값을 확인할 수 있다.
  - `KeyHolder`와 `connection.prepareStatement(sql, new String[]{"id"})`를 사용해서 `id`를 지정해주면 `INSERT`쿼리 실행 이후에 데이터베이스에 생성된 ID값을 조회할 수 있다.
  - 물론 데이터베이스에서 생성된 ID값을 조회하는 것은 순수 JDBC로도 가능하지만, 코드가 훨씬 더 복잡하다
  - 참고로 뒤에서 설명하겠지만 JdbcTemplate이 제공하는 `SimpleJdbcInsert`라는 훨씬 편리한 기능이 있으므로 대략 이렇게 사용한다 정도로만 알아두면 된다.
- **update()**
  - 데이터를 업데이트 한다.
  - `template.update()`: 데이터를 변경할 때는 `update()`를 사용하면 된다.
    - 쿼리 `?`에 바인딩할 파라미터를 순서대로 전달하면 된다.
    - 반환 값은 해당 쿼리의 영향을 받은 로우 수이다. 여기서는 `where id = ?`를 지정했기 때문에 영향 받은 로우수는 최대 1개이다.
- **findById()**
  - 데이터를 하나 조회한다
  - `template.quertyForObject()`
    - 결과 로우가 하나일 때 사용한다
    - `RowMapper`는 데이터베이스의 반환 결과인 `ResultSet`을 객체로 변환한다
    - 결과가 없으면 `EmptyResultDataAccessException`예외가 발생한다.
    - 결과가 둘 이상이면 `IncorrectResultSizeDataAccessException`예외가 발생한다.
  - `ItemRepository.findById()`인터페이스는 결과가 없을 때 `Optional`을 반환해야 한다. 따라서 결과가 없으면 예외를 잡아서 `Optional.empty`를 대신 반환하면 된다.
- **findAll()**
  - 데이터를 리스트로 조회한다. 그리고 검색 조건으로 적절한 데이터를 찾는다.
  - `template.query()`
    - 결과가 하나 이상일 때 사용한다.
    - `RowMapper`는 데이터베이스의 반환 결과인 `ResultSet`을 객체로 변환한다.
    - 결과가 없으면 빈 컬렉션을 반환한다.
    - 동적 쿼리에 대한 부분은 바로 다음에 다룬다
- **itemRowMapper()**
  - 데이터베이스의 조회 결과를 객체로 변환할 때 사용한다.
  - JDBC를 직접 사용할 때 `ResultSet`을 사용했던 부분을 떠올리면 된다. 차이가 있다면, JdbcTemplate이 다음과 같은 루프를 대신 돌려주고, 개발자는 `RowMapper`를 구현해서 그 내부 코드만 채운다고 이해하면 된다.

## JdbcTemplate 적용2 - 동적 쿼리 문제

결과를 검색하는 `findAll()`을 보자.<br>여기서 어려운 부분은 사용자가 검색하는 값에 따라서 실행하는 SQL이 동적으로 달라져야 한다는 점이다.

예를 들어서 다음과 같다

- 검색 조건이 없는 경우
  - `select id, item_name, price, quantity from item`
- 상품명(`itemName`)으로 검색한 경우
  - `select id, item_name, price, quantity from item where item_name like concat ('%',?,'%')`
- 최대 가격(`maxPrice`)으로 검색한 경우
  - `select id, item_name, price, quantity from item where price <= ?`
- 상품명 (`itemName`), 최대 가격(`maxPrice`)둘 다 검색한 경우
  - `select id, item_name, price, quantity from item where item_name like concat('%',?,'%') and price <= ?`

결과적으로 4가지 상황에 따른 SQL을 동적으로 생성해야 한다.<br>동적 쿼리가 언듯 보면 쉬워 보이지만, 막상 개발해보면 생각보다 다양한 상황을 고민해야 한다. 예를 들어서 어떤 경우에는 `where`를 앞에 넣고 어떤 경우에는 `and`를 넣어야 하는지 등을 모두 계산해야 한다. 그리고 각 상황에 맞추어 파라미터도 생성해댜 한다. 물론 실무에서는 이보다 훨씬 더 복잡한 동적 쿼리들이 사용된다.

참고로 이후에 설명할 MyBatis의 가장 큰 장점은 SQL을 직접 작성할 때 동적 쿼리를 쉽게 작성할 수 있다는 점이다.

## JdbcTemplate 적용3 - 구성과 실행

실제 코드가 동작하도록 구성하고 실행해보자

### JdbcTemplateV1Config 생성

```java
package hello.itemservice.config;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV1Config {

    private final DataSource dataSource;

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV1(dataSource);
    }
}
```

- `ItemRepository`구현체로 `JdbcTemplateItemRepositoryV1`이 사용되도록 했다. 이제 메모리 저장소가 아니라 실제 DB에 연결하는 JdbcTemplate이 사용된다.

### ItemServiceApplication - 변경

```java
package hello.itemservice;

//@Import(MemoryConfig.class)
@Import(JdbcTemplateV1Config.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}
}
```

### 데이터베이스 접근 설정


```properties
spring.profiles.active=local
spring.datasource.url=jdbc:h2:tcp://localhost/~/test
spring.datasource.username=sa
spring.datasource.password=
```

- 이렇게 설정만 하면 스프링 부트가 해당 설정을 사용해서 커넥션 풀과 `DataSource`, 트랜잭션 매니저를 스프링 빈으로 자동 등록한다.

### 실행

- 실행해보면 잘 동작한는 것을 확인할 수 있다. 그리고 DB에 실제 데이터가 저장되는 것도 확인할 수 있다
- 참고로 서버를 다시 시작할 때 마다 `TestDataInit`이 실행되기 때문에 `itemA`, `itemB`도 데이터베이스에 계속 추가된다. 메모리와 다르게 서버가 내려가도 데이터베이스는 유지되기 때문이다

### 로그추가

JdbcTemplate이 실행하는 SQL로그를 확인하려면 `application.properties`에 다음을 추가하면 된다. `main`, `test`설정이 분리되어 있기 때문에 둘다 확인하려면 두 곳에 모두 추가해야 한다

```properties
#jdbcTemplate sql log
logging.level.org.springframework.jdbc=debug
```

## JdbcTemplate - 이름 지정 파라미터1

### 순서대로 바인딩

JdbcTemplate을 기본으로 사용하면 파라미터를 순서대로 바인딩 한다.<br>예를 들어서 다음 코드를 보자

```java
@Override
public void update(Long itemId, ItemUpdateDto updateParam) {
    String sql = "update item set item_name=?, price=?, quantity=? where id = ?";
    template.update(sql,
            updateParam.getItemName(),
            updateParam.getPrice(),
            updateParam.getQuantity(),
            itemId
    );
}
```

여기서는 `itemName`, `price`, `quantity`가 SQL에 있는 `?`에 순서대로 바인딩 된다. 따라서 순서만 잘 지키면 문제가 될 것은 없다. 그런데 문제는 변경시점에 발생한다.

누군가 다음과 같이 SQL 코드의 순서를 변경했다고 가정해보자. (`price`와 `quantity`의 순서를 변경했다.)

```java
@Override
public void update(Long itemId, ItemUpdateDto updateParam) {
    String sql = "update item set item_name=?, quantity=?, price=? where id = ?";
    template.update(sql,
            updateParam.getItemName(),
            updateParam.getPrice(),
            updateParam.getQuantity(),
            itemId
    );
}
```

이렇게 되면 다음과 같은 순서로 데이터가 바인딩 된다.<br>`item_name=itemName, quantity=price, price=quantity`

결과적으로 `price`와 `quantity`가 바뀌는 매우 심각한 문제가 발생한다. 이럴일이 없을 것 같지만, 실무에서는 파라미터가 10~20개가 넘어가는 일도 아주 많다. 그래서 미래에 필드를 추가하거나, 수정하면서 이런 문제가 충분히 발생할 수 있다.

버그 중에서 가장 고치기 힘든 버그는 데이터베이스에 데이터가 잘못 들어가는 버그다. 이것은 코드만 고치는 수준이 아니라 데이터베이스의 데이터를 복구해야 하기 때문에 버그를 해결하는데 들어가는 리소스가 어마어마하다.

**개발을 할 때는 코드를 몇줄 줄이는 편리함도 중요하지만, 모호함도 제거해서 코드를 명확하게 만드는 것이 유지보수 관점에서 매우 중요하다.**

이처럼 파라미터를 순서대로 바인딩하는 것은 편리하기는 하지만, 순서가 맞지 않아서 버그가 발생할 수도 있으므로 주의해서 사용해야 한다.

### 이름 지정 바인딩

JdbcTemplate는 이런 문제를 보완하기 위해 `NamedParameterJdbcTemplate`라는 (순서가 이닌) 이름을 지정해서 파라미터를 바인딩하는 기능을 제공한다.

지금부터 코드로 알아보자.

#### JdbcTemplateItemRepositoryV2 생성

```java
package hello.itemservice.repository;

/**
 * NamedParameterJdbcTemplate
 */
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository{

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) " +
                "values(:itemName, :price, :quantity)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }


    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item " +
                "set item_name=:itemName, price=:price, quantity=:quantity " +
                "where id =:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        template.update(sql, param);
    }


    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Object> param = Map.of("id", id);
            Item item = template.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";

        //동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;

        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += "price <= :maxPrice";
        }

        log.info("sql={}", sql);
        return template.query(sql, param, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);
    }
}
```

#### 코드 분석

- **기본**
  - `JdbcTemplateItemRepositoryV2`는 `ItemRepository`인터페이스를 구현했다.
  - `this.template = new NamedParameterJdbcTemplate(dataSource)`
    - `NamedParameterJdbcTemplate`도 내부에 `dataSource`가 필요하다.
    - `JdbcTemplateItemRepositoryV2` 생성자를 보면 의존관계 주입은 `dataSource`를 받고 내부에서 `NamedParameterJdbcTemplate`을 생성해서 가지고 있다. 스프링에서는 `JdbcTemplate`관련 기능을 사용할때 관례상 이 방법을 많이 사용한다.
  - 물론 `NamedParameterJdbcTemplate`을 스프링 빈으로 직접 등록하고 주입받아도 된다.
- **save()**
  - SQL에서 `?`대신에 `:파라미터이름`을 받는 것을 확인할 수 있다.
  - 추가로 `NamedParameterJdbcTemplate`은 데이터베이스가 생성해주는 키를 매우 쉽게 조회하는 기능도 제공해준다.

## JdbcTemplate - 이름 지정 파라미터2

## JdbcTemplate - 이름 지정 파라미터3

## JdbcTemplate - SimpleJdbcInsert

## JdbcTemplate 기능 정리
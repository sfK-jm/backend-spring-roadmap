# 스프링MVC-2편

## 기본 기능

### 프로젝트 생성

- 프로젝트 선택
  - Project: Gradle - Groovy Project
  - Language: Java
  - Spring Boot: 2.5.0
  - Project Metadata
    - Group: hello
    - Artifact: thymeleaf-basic
    - Name: thymeleaf-basic
    - Package name: hello.thymeleaf
    - Packaging: Jar
    - Java: 11
  - Dependencies
    - Spring Web
    - Lombok
    - Thymeleaf

**홈 화면 추가**
홈 화면을 추가하자<br>
( 참고: `/resources/static/index.html` : 스프링 부트가 해당 디렉토리 위치에 index.html이 있으면 자동으로 Welcome Page로 인식한다.)

```html
<html lang="kr">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <ul>
      <li>
        텍스트
        <ul>
          <li><a href="/basic/text-basic">텍스트 출력 기본</a></li>
          <li><a href="/basic/text-unescaped">텍스트 text, utext</a></li>
        </ul>
      </li>
      <li>
        표준 표현식 구문
        <ul>
          <li><a href="/basic/variable">변수 - SpringEL</a></li>
          <li><a href="/basic/basic-objects?paramData=HelloParam">기본 객체들</a></li>
          <li><a href="/basic/date">유틸리티 객체와 날짜</a></li>
          <li><a href="/basic/link">링크 URL</a></li>
          <li><a href="/basic/literal">리터럴</a></li>
          <li><a href="/basic/operation">연산</a></li>
        </ul>
      </li>
      <li>
        속성 값 설정
        <ul>
          <li><a href="/basic/attribute">속성 값 설정</a></li>
        </ul>
      </li>
      <li>
        반복
        <ul>
          <li><a href="/basic/each">반복</a></li>
        </ul>
      </li>
      <li>
        조건부 평가
        <ul>
          <li><a href="/basic/condition">조건부 평가</a></li>
        </ul>
      </li>
      <li>
        주석 및 블록
        <ul>
          <li><a href="/basic/comments">주석</a></li>
          <li><a href="/basic/block">블록</a></li>
        </ul>
      </li>
      <li>
        자바스크립트 인라인
        <ul>
          <li><a href="/basic/javascript">자바스크립트 인라인</a></li>
        </ul>
      </li>
      <li>
        템플릿 레이아웃
        <ul>
          <li><a href="/template/fragment">템플릿 조각</a></li>
          <li><a href="/template/layout">유연한 레이아웃</a></li>
          <li><a href="/template/layoutExtend">레이아웃 상속</a></li>
        </ul>
      </li>
    </ul>
  </body>
</html>
```

### 타임리프 소개

**타임리프 특징**

- 서버 사이드 HTML 렌더링(SSR)
- 네츄럴 템플릿
- 스프링 통합 지원

**서버 사이드 HTML 렌더링 (SSR)**

- 타임리프는 백엔드 서버에서 HTML을 동적으로 렌더링하는 용도로 사용된다.

**내츄럴 템플릿**

- 타임리프는 순수 HTML을 최대한 유지하는 특징이 있다. 타임리프로 작성한 파일은 HTML을 유지하기 때문에 웹 브라우저에서 파일을 열어서 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있다.
- JSP를 포함한 다른 뷰 템플릿들은 해당 파일을 열면, 예를 들어서 JPS파일 자체를 그대로 웹 브라우저에서 열어보면 JSP소스코드와 HTML이 뒤죽박죽 섞여서 웹 브라우저에서 정상적인 HTML결과를 확인할 수 없다. 오직 서버를 통해서 JSP가 렌더링 되고 HTML응답 결과를 받아야 화면을 확인할 수 있다.
- 반면에 티임리프로 작성된 파일은 해당 파일을 그대로 웹 브라우저에서 열어도 정상적인 HTML결과를 확인할 수 있다. 물론 이 경우 동적으로 결과가 렌더링 되지는 않는다. 하지만 HTLM마크업 결과가 어떻게 되는지 파일만 열어도 바로 확인할 수 있다. 이렇게 **순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 네츄럴 템플릿** (nutural templates)이라 한다.

**스프링 통합 지원**

- 타임리프는 스프링과 자연스럽게 통합되고, 스프링의 다양한 기능을 편리하게 사용할 수 있게 지원한다. 이 부분은 다음 섹션인 [스프링 통합과 폼]에서 자세히 알아보겠다.

**타임리프 기본 기능**

- **타입리프 사용 선언**: 타임리프를 사용하려면 다음 선언을 하면 된다.
  - `<html xmlns:th="http://www.thymeleaf.org">`
- **기본 표현식**: 타임리프는 다음과 같은 기본 표현식들을 제공한다. 앞으로 하나씩 알아보자
  - 간단한 표현
    - 변수 표현식" `${...}`
    - 선택 변수 표현식: `*{...}`
    - 메시지 표현식: `#{...}`
    - 링크 URL 표현식: `@{...}`
    - 조각 표현식: `~{...}`
    - (참고) 선택 변수 표현식과 메시지 표현식은 이번 섹션에서는 다루지 않는다. 뒤에서 알아볼 것.
  - 리터럴
    - 텍스트: `'one text' 'Another one!', ...`
    - 숫자: `0, 34, 3.0, 12.3, ...`
    - 불린: `true, false`
    - 널: `null`
    - 리터럴 토큰: `one, sonetext, main, ...`
  - 문자 연산
    - 문자 합치기: `+`
    - 리터럴 대체: `|The name is ${name}|`
  - 산술 연산
    - Binary operators: `+, -, *, /, %`
    - Minus sign(unary operator): `!, NOT`
  - 비교와 동등
    - 비교: `>, <, >=, <= (gt, lt, ge, le)`
    - 동등 연산: `==, != (eq, ne)`
  - 조건 연산
    - If-then: `(if) ? (then)`
    - if-then-else: `(if) ? (then) : (else)`
    - Default: `(value) ?: (defaultvalue)`
  - 특별한 토큰:
    - No-Operation: `_`

### 텍스트 - text, utext

타임리프의 가장 기본 기능인 텍스트를 출력하는 기능 먼저 알아보자.

타임리프의 가장 기본 기능인 텍스트를 출력하는 기능 먼저 알아보자

- 타임리프는 기본적으로 HTML 태그의 속성에 기능을 정의해서 동작한다. HTML의 콘텐츠(content)에 데이터를 출력할 때는 다음과 같이 `th:text`를 사용하면 된다.
  - ex. `<span th:text="${data}">`
- HTML 태그의 속성이 아니라 HTML 콘텐츠 영역안에서 직접 데이터를 출력하고 싶으면 다음과 같이 `[[...]]`를 사요하면 된다.
  - 컨텐츠 안에서 직접 호출하기 = `[[${data}]]`

예제로 확인해보자

- `BasicController`: src > main > java > hello > thymeleaf > basic 패키지를 생성하고, 내부에 BasicController 클래스를 생성하자.

  ```java
  @Controller
  @RequestMapping("/basic")
  public class BasicController {

    @GetMapping("text-basic")
    public String textBasic(Model model) {
        model.addAttribute("data", "Hello Spring!");
        return "basic/text-basic";
    }
  }
  ```

- /resources/templates/basic/text-basic.html`
  ```html
  <!DOCTYPE html>
  <html xmlns:th="http://www.thymeleaf.org">
    <head>
      <meta charset="UTF-8" />
      <title>Title</title>
    </head>
    <body>
      <h1>컨텐츠에 데이터 출력하기</h1>
      <ul>
        <li>th:text 사용 <span th:text="${data}"></span></li>
        <li>컨텐츠 안에서 직접 출력하기 = [[${data}]]</li>
      </ul>
    </body>
  </html>
  ```

**Escape**

HTML문서는 `<, >`같은 특수 문자를 기반으로 정의된다. 따라서 뷰 템플릿으로 HTML화면을 생성할 때는 출력하는 데이터에 이러한 특수 문자가 있는 것을 주의해서 사용해야 한다.<br>
앞에서 만든 예제의 데이터를 다음과 같이 변경해서 실행해보자

변경점<br>
`"Hello Spring!"`

변경후<br>
`"Hello <b>Spring!</b>"`

`<b>`태그를 사용해서 **Spring!**이라는 단어가 진하게 나오도록 해보자

웹 브라우저에서 실행결과를 보자

- 웹 브라우저: `Hello <b>Spring!</b>`
- 소스보기: `Hello &lt;b&gt;Spring!&lt;/b&gt;`

개발자가 의도한 것은 `<b>`가 있으면 해당 부분을 강조하는 것이 목적이었다. 그런데 `<b>`태그가 그대로 나온다.<br>
소스보기를 하면 `<`부분이 `&lt;`로 변경된 것을 확인할 수 있다.

**HTML 엔티티**
웹 브라우저는 `<` HTML 태그의 시작으로 인식한다. 따라서 `<` 를 테그의 시작이 아니라 문자로 표현할 수 있는 방법 이 필요한데, 이것을 HTML 엔티티라 한다. 그리고 이렇게 HTML에서 사용하는 특수 문자를 HTML 엔티티로 변경하는 것을 이스케이프(escape)라 한다. 그리고 타임리프가 제공하는 `th:text`, `[[...]]` 는 **기본적으로 이스케이프 (escape)를 제공**한다.

- `<` `&lt;`
- `>` `&gt;`
- 기타 수 많은 HTML 엔티티가 있다.

**Unescape**
이스케이프 기능을 사용하지 않으러면 어떻게 해야할까?

타임리프는 다음 두 기능을 제공한다

- `th:text` -> `th:utext`
- `[[...]]` -> `[(...)]`

**BasicController에 추가**

```java
 @GetMapping("/text-unescaped")
 public String textUnescaped(Model model) {
     model.addAttribute("data", "Hello <b>Spring!</b>");
     return "basic/text-unescaped";
 }
```

`/resources/templates/basic/text-unescape.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>text vs utext</h1>
    <ul>
      <li>th:text = <span th:text="${data}"></span></li>
      <li>th:utext = <span th:utext="${data}"></span></li>
    </ul>
    <h1><span th:inline="none">[[...]] vs [(...)]</span></h1>
    <ul>
      <li><span th:inline="none">[[...]] = </span>[[${data}]]</li>
      <li><span th:inline="none">[(...)] = </span>[(${data})]</li>
    </ul>
  </body>
</html>
```

- `th:inline="none`: 타임리프는 `[[...]]`를 해석하기 때문에, 화면에 `[[...]]`글자를 보여줄 수 없다.<br>
  이 태그 안에서는 타임리프가 해석하지 말라는 옵션이다.

**실행**

- `http://localhost:8080/basic/text-unescaped`

실행해보면 다음과 같이 정상 수행되는 것을 확인할 수 있다.

- 웹 브라우저: Hello **Spring!**
- 소스보기: `Hello <b>Spring!<b>`

> [!WARNING]
> 실제 서비스를 개발하다 보면 escape를 사용하지 않아서 HTML이 정상 렌더링 되지 않는 수 많은 문제가 발생한다. escape를 기본으로 하고, 꼭 필요할 때만 unescape르 사용하자.

### 변수 - SpringEL

타임리프에서 변수를 사용할 때는 변수 표현식을 사용한다.

**변수 표현식**: `${...}`

그리고 이 변수 표현식에는 스프링 EL이라는 스프링이 제공하는 표현식을 사용할 수 있다.

`BasicController 추가`

```java
@GetMapping("/variable")
 public String variable(Model model) {
     User userA = new User("userA", 10);
     User userB = new User("userB", 20);
     List<User> list = new ArrayList<>();
     list.add(userA);
     list.add(userB);
     Map<String, User> map = new HashMap<>();
     map.put("userA", userA);
     map.put("userB", userB);
     model.addAttribute("user", userA);
     model.addAttribute("users", list);
     model.addAttribute("userMap", map);
     return "basic/variable";
 }
 @Data
 static class User {
    private String username;
    private int age;

    public User(String username, int age) {
        this.username = username;
        this.age = age;
  }
}
```

`/resources/templates/basic/variable.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>SpringEL 표현식</h1>
    <ul>
      Object
      <li>${user.username} = <span th:text="${user,username}"></span></li>
      <li>${user['username']} = <span th:text="${user['username']}"></span></li>
      <li>${user.getUsername()} = <span th:text="${user.getUsername()}"></span></li>
    </ul>
    <ul>
      List
      <li>${users[0].username} = <span th:text="${users[0].username}"></span></li>
      <li>${users[0]['username']} = <span th:text="${users[0]['username']}"></span></li>
      <li>${users[0].getUsername()} = <span th:text="${users[0].getUsername()}"></span></li>
    </ul>
    <ul>
      Map
      <li>${userMap['userA'].username} = <span th:text="${userMap['userA'].username}"></span></li>
      <li>${userMap['userA']['username']} = <span th:text="${userMap['userA']['username']}"></span></li>
      <li>${userMap['userA'].getUsername()} = <span th:text="${userMap['userA'].getUsername()}"></span></li>
    </ul>
  </body>
</html>
```

**SpringEl 다양한 표현식 사용**

**Object**

- `user.username`: user의 username을 프로퍼티 접근 -> `user.getUsername()`
- `user['username']`: 위와 같음 -> `user.getUsername()`
- `user.getUsername()`: user의 `getUsername()`을 직접 호출

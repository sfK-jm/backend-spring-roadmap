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

#### SpringEl 다양한 표현식 사용

**Object**

- `user.username`: user의 username을 프로퍼티 접근 -> `user.getUsername()`
- `user['username']`: 위와 같음 -> `user.getUsername()`
- `user.getUsername()`: user의 `getUsername()`을 직접 호출

**List**

- `users[0].username`: List에서 첫 번째 회원을 찾고 usernaeme 프로퍼티 접근 -> `list.get(0).getUsername()`
- `users[0]['username']`: 위와 같음
- `users[0].getusername()`: List에서 첫 번째 회원을 찾고 메서드 직접 호출

**Map**

- `userMap['userA'].username` : Map에서 userA를 찾고, username 프로퍼티 접근 -> `map.get("userA").getUsername()`
- `userMap['userA']['username']` : 위와 같음
- `userMap['userA'].getUsername()` : Map에서 userA를 찾고 메서드 직접 호출

**지역 변수 선언**

`th:with`를 사용하면 지역 변수를 선언해서 사용할 수 있다. 지역 변수는 선언한 태그 안에서만 사용할 수 있다.

- `/resources/templates/basic/variable.html` 추가
  ```html
  <h1>지역 변수 - (th:with)</h1>
  <div th:with="first=${users[0]}">
    <p>처음 사람의 이름은 <span th:text="${first.username}"></span></p>
  </div>
  ```

### 기본 객체들

타임리프는 기본 객체들을 제공한다

- `${#request}` - 스프링 부트 3.0부터 제공하지 않는다.
- `${#response}` - 스프링 부트 3.0부터 제공하지 않는다.
- `${#session}` - 스프링 부트 3.0부터 제공하지 않는다.
- `${#servletContext}` - 스프링 부트 3.0부터 제공하지 않는다.
- `${#locale}`

> [!WARNING]
> 스프링 부트 3.0부터는 `${#request}` , `${#response}`,`${#session}` , `${#servletContext}` 를 지원하지 않는다
> 스프링 부트 3.0이라면 직접 `model`에 해당 객체를 추가해서 사용해야 한다.

그런데 `#request`는 `HttpServletRequest`객체가 그대로 제공되기 때문에 데이터를 조회하려면 `request.getParameter("data")`처럼 불편하게 접근해야 한다.

이런 점을 해결하기 위해 편의 객체도 제공한다

- HTTP 요청 파라미터 접근: `param`
  - 예) `${param.paramData}`
- HTTP 세션 접근: `session`
  - 예) `${session.sessionData}`
- 스프링 빈 접근: `@`
  - 예) `${@helloBean.hello('Spring!')}`

스프링 3.0 이상

BasicController 추가

```java
@GetMapping("/basic-objects")
public String basicObject(Model model, HttpServletRequest request,
                          HttpServletResponse response, HttpSession session) {

    session.setAttribute("sessionData", "Hello Session");
    model.addAttribute("request", request);
    model.addAttribute("response", response);
    model.addAttribute("servletContext", request.getServletContext());

    return "basic/basic-objects";
}

@Component("helloBean")
static class HelloBean {
    public String hello(String data) {
        return "Hello " + data;
    }
}
```

`/resources/templates/basic/basic-objects.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>식 기본 객체 (Expression Basic Objects)</h1>
    <ul>
      <li>request = <span th:text="${request}"></span></li>
      <li>session = <span th:text="${session.sessionData}"></span></li>
      <li>servletContext = <span th:text="${servletContext}"></span></li>
      <li>locale = <span th:text="${#locale}"></span></li>
    </ul>

    <h1>편의 객체</h1>
    <ul>
      <li>Request Parameter = <span th:text="${param.paramData}"></span></li>
      <li>session = <span th:text="${session.sessionData}"></span></li>
      <li>spring bean = <span th:text="${@helloBean.hello('Spring!')}"></span></li>
    </ul>
  </body>
</html>
```

### 유틸리티 객체와 날짜

타임리프는 문자, 숫자, 날짜, URL등을 편리하게 다루는 다양한 유틸리티 객체들을 제공한다.

#### 타임리프 유틸리티 객체들

- `#message` : 메시지, 국제화 처리
- `#uris` : URI 이스케이프 지원
- `#dates` : `java.util.Date` 서식 지원
- `#calendars` : `java.util.Calendar` 서식 지원
- `#temporals` : 자바8 날짜 서식 지원
- `#numbers` : 숫자 서식 지원
- `#strings` : 문자 관련 편의 기능
- `#objects` : 객체 관련 기능 제공
- `#bools` : boolean 관련 기능 제공
- `#arrays` : 배열 관련 기능 제공
- `#lists` , `#sets` , `#maps` : 컬렉션 관련 기능 제공
- `#ids` : 아이디 처리 관련 기능 제공, 뒤에서 설명

#### 자바8 날짜

타임리프에서 자바8 날짜인 `LocalData`, `LocalDataTime`, `Instant`를 사용하려면 추가 라이브러리가 필요하다. 스프링 부트 타임리프를 사용하면 해당 라이브러리가 자동으로 추가되고 통합된다.

**타임리프 자바8 날짜 지원 라이브러리**
`thymeleaf-extras-java8time`

자바8 날짜용 유틸리티 객체
`#temporals`

**사용 예시**

```html
<span th:text="${#temporals.format(localDataTime, 'yyyy-MM-dd HH:mm:ss')}"></span>
```

BasicController 추가

```java
@GetMapping("/date")
public String data(Model model) {
    model.addAttribute("localDateTime", LocalDateTime.now());
    return "basic/date";
}
```

`/resources/templates/basic/date.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<h1>LocalDateTime</h1>
<ul>
    <li>default = <span th:text="${localDateTime}"></span> </li>
    <li>yyyy-MM-dd HH:mm:ss = <span th:text="${#temporals.format(localDateTime, 'yyyy-MM-dd HH:mm:ss')}"></span> </li>
</ul>

<h1>LocalDateTime - Utils</h1>
<ul>
    <li>${#temporals.day(localDateTime)} = <span th:text="${#temporals.day(localDateTime)}"></span> </li>
    <li>${#temporals.month(localDateTime)} = <span th:text="${#temporals.month(localDateTime)}"></span></li>
    <li>${#temporals.monthName(localDateTime)} = <span th:text="${#temporals.monthName(localDateTime)}"></span></li>
    <li>${#temporals.monthNameShort(localDateTime)} = <span th:text="${#temporals.monthNameShort(localDateTime)}"></span></li>
    <li>${#temporals.year(localDateTime)} = <span th:text="${#temporals.year(localDateTime)}"></span></li>
    <li>${#temporals.dayOfWeek(localDateTime)} = <span th:text="${#temporals.dayOfWeek(localDateTime)}"></span></li>
    <li>${#temporals.dayOfWeekName(localDateTime)} = <span th:text="${#temporals.dayOfWeekName(localDateTime)}"></span></li>
    <li>${#temporals.dayOfWeekNameShort(localDateTime)} = <span th:text="${#temporals.dayOfWeekNameShort(localDateTime)}"></span></li>
    <li>${#temporals.hour(localDateTime)} = <span th:text="${#temporals.hour(localDateTime)}"></span></li>
    <li>${#temporals.minute(localDateTime)} = <span th:text="${#temporals.minute(localDateTime)}"></span></li>
    <li>${#temporals.second(localDateTime)} = <span th:text="${#temporals.second(localDateTime)}"></span></li>
    <li>${#temporals.nanosecond(localDateTime)} = <span th:text="${#temporals.nanosecond(localDateTime)}"></span></li>
</ul>
</ul>
</body>
</html>
```

### URL 링크

타임리프에서 URL을 생성할 때는 `@{...}` 문법을 사용하면 된다.

BasicController 추가

```java
@GetMapping("/link")
public String link(Model model) {
    model.addAttribute("param1", "data1");
    model.addAttribute("param2", "data2");
    return "basic/link";
}
```

`/resources/templates/basic/link.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>URL 링크</h1>
    <ul>
      <li><a th:href="@{/hello}">basic url</a></li>
      <li><a th:href="@{/hello(param1=${param1}, param2=${param2})}">hello query param</a></li>
      <li><a th:href="@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}">path variable</a></li>
      <li><a th:href="@{/hello/{param1}(param1=${param1}, param2=${param2})}">path variable + query parameter</a></li>
    </ul>
  </body>
</html>
```

**단순한 URL**

- `@{/hello}` -> `/hell`

**쿼리 파라미터**

- `@{/hello(param1=${param1}, param2=${param2})}`
  - `/hello?param1=data1&param2=data2`
  - `()`에 있는 부분은 쿼리 파라미터로 처리된다

**경로 변수**

- `@{/hello/{param1}/{param2}(param1=${param1}, param2=${param2})}`
  - `/hello/data1/data2`
  - URL 경로상에 변수가 있다면 `()`부분은 경로 변수로 처리된다.

**경로 변수 + 쿼리 파라미터**

- `@{/hello/{param1}(param1=${param1}, param2=${param2})}`
  - `/hello/data1?param2=data2`
  - 경로 변수와 쿼리 파라미터를 함께 사용할 수 있다.

상대경로, 절대경로, 프로토콜 기준을 표현할 수도 있다.

- `/hello`: 절대 경로
- `hello`: 상대 경로

### 리터럴

**Literals**

리터럴은 소스 코드상에 고정된 값을 말하는 용어이다.<br>
예를 들어서 다음 코드에서 `"Hello"`는 문자 리터럴, `10`, `20`는 숫자 리터럴이다.

```java
String a = "Hello"
int a = 10 * 20
```

> [!TIP]
> 이 내용은 쉬워 보이지만 처음 타임리프를 사용하면 많이 실수하니 잘 보아두자

타임리프는 다음과 같은 리터럴이 있다.

- 문자: `hello`
- 숫자: `10`
- 불린: `true`, `false`
- null: `null`

타임리프에서 문자 리터럴은 항상 `'`(작은 따옴표)로 감싸야한다.
`<span th:text="'hello'">`

그런데 문자를 항상 `'`로 감싸는 것은 너무 귀찮은 일이다. 공백 없이 쭉 이어진다면 하나의 의미있는 토큰으로 인지해서 다음과 같이 작은 따옴표를 생략할 수 있다.<br>
룰: `A-Z` , `a-z` , `0-9` , `[]` , `.` , `-` , `_`

`<span th:text="hello">`

**오류**

`<span th:text="hello world!">`<br>
문자 리터럴은 원칙상 `'`로 감싸야 한다. 중간에 공백이 있어서 하나의 의미있는 토큰으로도 인식되지 않는다.

**수정**

`<span th:text="'hello world!'">` <br>
이렇게 `'`로 감싸면 정상 동작한다.

BasicController 추가

```java
 @GetMapping("/literal")
 public String literal(Model model) {
     model.addAttribute("data", "Spring!");
     return "basic/literal";
 }
```

`/resources/templates/basic/literal.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>리터럴</h1>
    <ul>
      <!--틀린 코드 -->
      <!-- <li>"hello world!"= <span th:text="hello world!"></span> </li> -->
      <!--        -->

      <li>'hello' + ' world! = <span th:text="'hello' + ' world!'"></span></li>
      <li>'hello world!' = <span th:text="'hello world!'"></span></li>
      <li>'hello ' + ${data} = <span th:text="'hello ' + ${data}"></span></li>
      <li>리터럴 대체 |hello ${data}| = <span th:text="|hello ${data}|"></span></li>
    </ul>
  </body>
</html>
```

**리터럴 대체(Literal substitutions)**

`<span th:text="|hello ${data}|">`<br>
마지막의 리터럴 대체 문법을 사용하면 마치 템플릿을 사용하는 것 처럼 편리하다.

### 연산

타임리프 연산을 자바와 크게 다르지 않다. HTML안에서 사용하기 때문에 HTML엔티티를 사용하는 부분만 주의하자.

BasicController 추가

```java
@GetMapping("/operation")
 public String operation(Model model) {
     model.addAttribute("nullData", null);
     model.addAttribute("data", "Spring!");
     return "basic/operation";
}
```

`/resources/templates/basic/operation.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <ul>
      <li>
        산술 연산
        <ul>
          <li>10 + 2 = <span th:text="10 + 2"></span></li>
          <li>10 % 2 == 0 = <span th:text="10 % 2 == 0"></span></li>
        </ul>
      </li>

      <li>
        비교 연산
        <ul>
          <li>1 > 10 = <span th:text="1 &gt; 10"></span></li>
          <li>1 gt 10 = <span th:text="1 gt 10"></span></li>
          <li>1 >= 10 = <span th:text="1 >= 10"></span></li>
          <li>1 ge 10 = <span th:text="1 ge 10"></span></li>
          <li>1 == 10 = <span th:text="1 == 10"></span></li>
          <li>1 != 10 = <span th:text="1 != 10"></span></li>
        </ul>
      </li>

      <li>
        조건식
        <ul>
          <li>(10 % 2 == 0)? '짝수':'홀수' = <span th:text="(10 % 2 == 0)? '짝수':'홀수'"></span></li>
        </ul>
      </li>

      <li>
        Elvis 연산자
        <ul>
          <li>${data}? '데이터가 없습니다.' = <span th:text="${data}?: '데이터가 없습니다.'"></span></li>
          <li>${nullData}? '데이터가 없습니다.' = <span th:text="${nullData}?: '데이터가 없습니다.'"></span></li>
        </ul>
      </li>

      <li>
        No-Operation
        <ul>
          <li>${data}?: _ = <span th:text="${data}?: _">데이터가 없습니다.</span></li>
          <li>${nullData}?: _ = <span th:text="${nullData}?: _">데이터가 없습니다.</span></li>
        </ul>
      </li>
    </ul>
  </body>
</html>
```

- **비교연산**: HTML 엔티티를 사용해야 하는 부분을 주의하자
  - `>`(gt), `<`(lt), `>=`(ge), `<=`(le), `!`(not), `==`(eq), `!=` (neq, ne)
- **조건식**: 자바의 조건식과 유사하다
- **Elvis 연산자**: 조건식의 편의 버전
- **No-Operation:**: `_`인 경우 마치 타임리프가 실행되지 않는 것 처럼 동작한다. 이것을 잘 사용하면 HTML의 내용 그대로 활용할 수 있다. 마지막 예를 보면 데이터가 없습니다. 부분이 그대로 출력된다.

### 속성 값 설정

**타임리프 태그 속성(Attribute)**

타임리프는 주로 HTML태그에 `th:*` 속성을 지정하는 방식으로 동작한다. `th:*`로 속성을 적용하면 기존 속성을 대체한다. 기존 속성이 없으면 새로 만든다.

BasicController 추가

```java
@GetMapping("/attribute")
public String attribute() {
    return "basic/attribute";
}
```

`/resources/templates/basic/attribute.html`

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>속성 설정</h1>
    <input type="text" name="mock" th:name="userA" />

    <h1>속성 추가</h1>
    - th:attrappend = <input type="text" class="text" th:attrappend="class='large'" /><br />
    - th:attrprepend = <input type="text" class="text" th:attrprepend="class='large'" /><br />
    - th:classappend = <input type="text" class="text" th:classappend="large" /><br />

    <h1>checked 처리</h1>
    - checked o <input type="checkbox" name="active" th:checked="true" /><br />
    - checked x <input type="checkbox" name="active" th:checked="false" /><br />
    - checked=false <input type="checkbox" name="active" checked="false" /><br />
  </body>
</html>
```

**속성 설정**

`th:*`속성을 지정하면 타임리프는 기존 속성을 `th:*`로 지정한 속성으로 대체한다. 기존 속성이 없다면 새로 만든다.<br>`input type="text" name="mock" th:name="userA" />`<br>-> 타임리프 랜더링 후 `<input type="text" name="userA" />`

**속성 추가**
`th:attrappend`: 속성 값의 뒤에 값을 추가한다
`th:attrprepend`: 속성 값의 앞에 값을 추가한다
`th:classappend`: class속성에 자연스럽게 추가한다.

**checked 처리**
HTML에서는 `<input type="checkbox" name="active" checked="false" />`<br>
-> 이 경우에도 checked속성이 있기 때문에 checked처리가 되어버린다.

HTML에서 `checked`속성은 `checked`속성의 값과 상관없이 `checked`라는 속성만 있어도 체크가 된다. 이런 부분이 `true`, `false`값을 주로 사용하는 개발자 입장에서는 불편하다.

타임리프의 `th:checked`는 값이 `false`인 경우 `checked`속성 자체를 제거한다.<br>
`<input type="checkbox" name="active" th:checked="false" />`<br>
-> 타임리프 렌더링 후: `<input type="checkbox" name="active" />`

### 반복

타임리프에서 반복은 `th:each`를 사용한다. 추가로 반복에서 사용할 수 있는 여러 상태 값을 지원한다.

BasicController 추가

```java
@GetMapping("/each")
 public String each(Model model) {
     addUsers(model);
     return "basic/each";
 }
 private void addUsers(Model model) {
     List<User> list = new ArrayList<>();
     list.add(new User("userA", 10));
     list.add(new User("userB", 20));
     list.add(new User("userC", 30));
     model.addAttribute("users", list);
 }
```

`/resources/templates/basic/each.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>기본 테이블</h1>
    <table border="1">
      <tr>
        <th>username</th>
        <th>age</th>
      </tr>
      <tr th:each="user : ${users}">
        <td th:text="${user.username}">username</td>
        <td th:text="${user.age}">0</td>
      </tr>
    </table>

    <h1>반복 상태 유지</h1>

    <table border="1">
      <tr>
        <th>count</th>
        <th>username</th>
        <th>age</th>
        <th>etc</th>
      </tr>
      <tr th:each="user, userStat : ${users}">
        <td th:text="${userStat.count}">username</td>
        <td th:text="${user.username}">username</td>
        <td th:text="${user.age}">0</td>
        <td>
          index = <span th:text="${userStat.index}"></span> count = <span th:text="${userStat.count}"></span> size =
          <span th:text="${userStat.size}"></span> even? = <span th:text="${userStat.even}"></span> odd? =
          <span th:text="${userStat.odd}"></span> first? = <span th:text="${userStat.first}"></span> last? =
          <span th:text="${userStat.last}"></span> current = <span th:text="${userStat.current}"></span>
        </td>
      </tr>
    </table>
  </body>
</html>
```

**반복 기능**

`<tr th:each="user : ${users}>`

- 밙복시 오른쪽 컬렉션(`${users}`)의 값을 하나씩 꺼내서 왼쪽 변수(`user`)에 담아서 태그를 반복 실행합니다.
- `th:each`는 `list`뿐만 아니라 배열, `java.util.Iterable`, `java.util.Enumeration`을 구현한 모든 객체를 반복에 사용할 수 있습니다. `Map`도 사용할 수 있는데 이 경우 변수에 담기는 값은 `Map.Entry`입니다.

**반복 상태 유지**

`<tr th:each="user, userStat : ${users}">`<br>
반복의 두번째 파라미터를 설정해서 반복의 상태를 확인 할 수 있다.<br>
두번째 파라미터는 생략 가능한데, 생락하면 지정한 변수(`user`) + `Stat`가 된다.<br>
여기서는 `user` + `Stat` = `userStat`이므로 생략 가능하다.

**반복 상태 유지 기능**

- `index`: 0부터 시작하는 값
- `count`: 1부터 시작하는 값
- `size`: 전체 사이즈
- `even`, `odd`: 홀수, 짝수 여부(`boolean`)
- `first`, `last`: 처음, 마지막 여부(`boolean`)
- `current`: 현재 객체

### 조건부 평가

타임리프 조건식

`if`, `unless`(`if`의 반대)

BasicController 추가

```java
@GetMapping("/condition")
public String condition(Model model) {
    addUsers(model);
    return "basic/condition";
}
```

`/resources/templates/basic/condition.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>if, unless</h1>
    <table border="1">
      <tr>
        <th>count</th>
        <th>username</th>
        <th>age</th>
      </tr>

      <tr th:each="user, userStat : ${users}">
        <td th:text="${userStat.count}">1</td>
        <td th:text="${user.username}">username</td>
        <td>
          <span th:text="${user.age}">0</span>
          <span th:text="'미성년자'" th:if="${user.age lt 20}"></span>
          <span th:text="'미성년자'" th:unless="${user.age ge 20}"></span>
        </td>
      </tr>
    </table>

    <h1>switch</h1>
    <table border="1">
      <tr>
        <th>count</th>
        <th>username</th>
        <th>age</th>
      </tr>
      <tr th:each="user, userStat : ${users}">
        <td th:text="${userStat.count}">1</td>
        <td th:text="${user.username}">username</td>
        <td th:switch="${user.age}">
          <span th:case="10">10살</span>
          <span th:case="20">20살</span>
          <span th:case="*">기타</span>
        </td>
      </tr>
    </table>
  </body>
</html>
```

**if, unless**

타임리프는 해당 조건이 맞지 않으면 태그 자체를 렌더링하지 않는다.<br>
만약 다음 조건이 `false`인 경우 `<span>...<span>`부분 자체가 렌더링 되지 않고 사라진다.<br>
`<span th:text="'미성년자'" th:if="${user.age lt 20}">`

**switch**

`*`은 만족하는 조건이 없을 때 사용하는 디폴트이다.

### 주석

BasicController 추가

```java
@GetMapping("/comments")
public String comments(Model model) {
    model.addAttribute("data", "Spring!");
    return "basic/comments";
}
```

`/resources/templates/basic/comments.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <h1>예시</h1>

    <span th:text="${data}">html data</span>

    <h1>1. 표준 HTML 주석</h1>
    <!--
<span th:text="${data}">html data</span>
-->

    <h1>2. 타임리프 파서 주석</h1>
    <!--/* [[${data}]] */-->

    <!--/*-->
    <span th:text="${data}">html data</span>
    <!--*/-->

    <h1>3. 타임리프 프로토타입 주석</h1>
    <!--/*/
    <span th:text="${data}">html data</span>
    /*/-->
  </body>
</html>
```

**결과**

```html
<h1>예시</h1>
<span>Spring!</span>
<h1>1. 표준 HTML 주석</h1>
<!--
<span th:text="${data}">html data</span> -->
<h1>2. 타임리프 파서 주석</h1>
<h1>3. 타임리프 프로토타입 주석</h1>
<span>Spring!</span>
```

**표준 HTML 주석**<br>
자바스크립트의 표준 HTML주석은 타임리프가 렌더링 하지 않고, 그래도 남겨둔다.

**타임리프 파서 주석**<br>
타임리프 파서 주석은 타임리프의 진짜 주석이다. 렌더링에서 주석 부분을 제거한다.

**타임리프 프로토타입 주석**<br>
타임리프 프로토타입은 약간 특이한데, HTML주석에 약간의 구문을 더했다.<br>
**HTML 파일**을 웹 브라우저에서 그대로 열어보면 HTML주석이기 때문에 이 부분이 웹 브라우저가 렌더링하지 않는다.<br>
**타임리프 렌더링**을 거치면 이 부분이 정상 렌더링 된다.<br>
쉽게 이야기해서 HTML 파일을 그대로 열어보면 주석처리가 되지만, 타임리프를 렌더링 한 경우에만 보이는 기능이다.

### 블록

`<th:block>`은 HTML태그가 아닌 타임리프의 유일한 자체 태그다.

BasicController 추가

```java
@GetMapping("/block")
public String block(Model model) {
    addUsers(model);
    return "basic/block";
}
```

`/resources/templates/basic/block.html`

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head>
    <meta charset="UTF-8" />
    <title>Title</title>
  </head>
  <body>
    <th:block th:each="user : ${users}">
      <div>
        사용자 이름1 <span th:text="${user.username}"></span>
        사용자 나이1 <span th:text="${user.age}"></span>
      </div>
      <div>
        요약 <span th:text="${user.username} + ' / ' + ${user.age}"></span>
      </div>
    </th:block>
  </body>
</html>
```

**실행 결과**<br>

```html
<div>사용자 이름1 <span>userA</span> 사용자 나이1 <span>10</span></div>
<div>요약 <span>userA / 10</span></div>
<div>사용자 이름1 <span>userB</span> 사용자 나이1 <span>20</span></div>
<div>요약 <span>userB / 20</span></div>
<div>사용자 이름1 <span>userC</span> 사용자 나이1 <span>30</span></div>
<div>요약 <span>userC / 30</span></div>
```

타임리프의 특성상 HTML태그안에 속성으로 기능을 정의해서 사용하는데, 위 예처럼 이렇게 사용하기 애매한 경우에 사용하면 된다. `<th:block>`은 렌더링시 제거된다.

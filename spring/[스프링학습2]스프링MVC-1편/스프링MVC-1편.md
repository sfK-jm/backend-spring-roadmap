# 스프링 MVC1편 - 백엔드 웹 개발 핵심 기술

## 소개

백엔드 웹 개발 핵심 기술

- 여기서는 웹 애플리케이션이라는게 뭔지 이해하기 위한 학습을 진행한다
- 생짜 서블릿코드, JSP, MVC패턴도 적용해 볼 것이다. 그리고 MVC 프레임워크를 직접 만들어 볼 것이다.
- 그리고 그 위에서 스프링 MVC의 핵심 구조를 깊이 있게 파악해 볼 것이고, 그 기반을 토대로 스프링 MVC의 기본 기능들을 하나씩 학습해 볼 것이다.
- 마지막으로 실제 웹 페이지와 웹 애플리케이션을 만들어서 웹 개발을 해볼것이다.

## 웹 서버, 웹 애플리케이션 서버

- 웹 - HTTP 기반

  - 웹은 HTTP를 기반으로 통신한다
  - 모든 것이 HTTP - HTTP 메시지에 모든 것을 전송
    - HTML, TEXT
    - IMAGE, 음성, 영상, 파일
    - JSON, XML (API)
    - 거의 모든 형태의 데이터 전송 가능
    - 서버간에 데이터를 주고 받을 때도 대부분 HTTP 사용

- 웹 서버

  - HTTP 기반으로 동작
  - 정적 리소스 제공, 기타 부가기능
  - 정적(파일) HTML, CSS, JS, 이미지, 영상
  - 예) NGINX, APACHE

- 웹 애플리케이션 서버 (WAS-Web Application Server)

  - HTTP 기반으로 동작
  - 웹 서버 기능 포함+ (정적 리소스 제공 가능)
  - 프로그램 코드를 실행해서 애플리케이션 로직 수행
    - 동적 HTML, HTTP API(JSON)
    - 서블릿, JSP, 스프링 MVC
  - 예) 톰캣(Tomcat), Jetty, Undertow

- 웹 서버, 웹 애플리케이션 서버(WAS)차이

  - 웹 서버는 정적 리소스(파일), WAS는 애플리케이션 로직
  - 그런데 사실은 둘의 용어도 경계도 모호함
    - 웹 서버도 프로그램을 실행하는 기능을 포함하기도 함
    - 웹 애플리케이션 서버도 웹 서버의 기능을 제공함
  - 자바는 서블릿 컨테이너 기능을 제공하면 WAS
    - 서블릿 없이 자바코드를 실행하는 서버 프레임워크도 있음
  - 그냥 이렇게 정리하자
    - WAS는 애플리케이션 코드를 실행하는데 더 특화되어있다.<br>(크게는, 웹 서버는 정적 리소스(파일)를 제공하는 서버, WAS는 애플리케이션 로직을 실행하는 서버다라고 생각하자.)

## 서블릿

이번에는 서블릿에 대해서 알아보자

다음과 같이 HTML Form양식을 작성하고 전송버튼을 클릭했다고 가정해보자
<img src="./images/서블릿1.png">

그리고 웹 애플리케이션 서버를 우리가 직접 구현해야 한다고 가정해보자. 서버에서 처리해야 하는 업무는 다음과 같다
<img src="./images/서블릿2.png">

그러면 우리는 위와 같은 과정의 로직을 다 작성해야 할 것이다. 그런데 사실상 의미있는 비즈니스 로직은 username과 age를 가지고 데이터베이스에 저장 요청하는게 끝이다. 그런데 전/후 단계가 너무 많다. 모두가 다 똑같이 이것을 개발하고 있기에는 너무 효율적이지 않아서 **서블릿**이라는게 등장한다.

### 서블릿을 지원하는 WAS사용

<img src="./images/서블릿3.png">
서블릿은 위 이미지에서, 의미있는 비즈니스 로직 영역을 제외한 전/후 모든 작업을 모두 지원해준다

<br/>**서블릿**

- 특징
  ```java
  @WebServlet(name = "helloServlet", urlPatterns = "/hello")
  public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response){
      // 애플리케이션 로직
    }
  }
  ```
  - urlPatterns(/hello)의 URL이 호출되면 서블릿 코드가 실행
  - HTTP 요청 정보를 편리하게 사용할 수 있는 HttpServletRequest
  - HTTP 응답 정보를 편리하게 제공할 수 있는 HttpServletResponse.
  - 개발자는 HTTP 스펙을 매우 편리하게 사용
- HTTP 요청, 응답 흐름
  <img src="./images/서블릿4.png">
  (HTTP요청시)
  - WAS는 Request, Response객체를 새로 만들어서 서블릿 객체 호출.
  - 개발자는 Request 객체에서 HTTP 요청 정보를 편리하게 꺼내서 사용.
  - 개발자는 Response 객체에 HTTP 응답 정보를 편리하게 입력.
  - WAS는 Response 객체에 담겨있는 내용으로 HTTP응답 정보를 생성
- **서블릿 컨테이너**
  <img src="./images/서블릿5.png">
  - 톰캣처럼 서블릿을 지원하는 WAS를 서블릿 컨테이너라고 함
  - 서블릿 컨테이너는 서블릿 객체 생성, 초기화, 호출, 종료하는 생명주기 관리
  - 서블릿 객체는 **싱글톤**으로 관리
    - 고객의 요청이 올 때 마다 계속 객체를 생성하는 것은 비효율
      - request, response 객체는 요청이 올 때 마다 항상 객체가 새로 생성되어야 한다. 그런데 helloServlet 이라는 것은 굳이 항상 생성할 필요가 있을까? 객체를 매번 생성할 필요가 없다. (다 같이 재사용한다.)
    - 최초 로딩 시점에 서블릿 객체를 미리 만들어두고 재활용
    - 모든 고객 요청은 동일한 서블릿 객체 인스턴스에 접근
    - **공유 변수 사용 주의**
    - 서블릿 컨테이너 종료시 함께 종료
  - JSP도 서블릿으로 변환 되어서 사용
  - 동시 요청을 위한 멀티 쓰레드 처리 지원

### 동시 요청 - 멀티 쓰레드

이번에는 동시 요청 - 멀티 쓰레드에 대해서 알아보자

클라이언트에서 서버로 요청을 하면 서버는 응답을 한다
<img src="./images/멀티쓰레드1.png">

클라이언트가 요청을 하면 TCP/IP 커넥션 연결이 되고, servlet을 호출한다
<img src="./images/멀티쓰레드2.png">

그런데 서블릿을 누가 호출하는걸까? 바로 "쓰레드"라는게 호출한다.
<img src="./images/멀티쓰레드3.png">

**쓰레드**

- 애플리케이션 코드를 하나하나 순차적으로 실행하는 것은 쓰레드
- 자바 메인 메서드를 처음 실행하면 main이라는 이름의 쓰레드가 실행.
- 쓰레드가 없다면 자바 애플리케이션 실행이 불가능
- 쓰레드는 한번에 하나의 코드 라인만 수행
- 그래서 동시 처리가 필요하면 쓰레드를 추가로 생성해줘야 한다.

**단일 요청-쓰레드 하나 사용**

- 동시처리가 필요하기 이전에, 쓰레드 하나가 있다고 가정해보자
  <img src="./images/단일요청1.png">
- 이제 클라이언트 요청이 왔다고 가정해보자. 그러면 쓰레드가 할당된다. 그리고 이 쓰레드를 가지고 서블릿이 호출된다
  <img src="./images/단일요청2.png">
- 그리고 나서 쓰레드를 가지고 응답까지 다 되고 나면, 쓰레드는 휴식한다.
  <img src="./images/단일요청3.png">
  <img src="./images/단일요청4.png">

**다중 요청-쓰레드 하나 사용**

- 쓰레드는 하나인데 다중 요청이 들어왔다고 가정해보자.<br>먼저 요청1이 들어왔다. 그래서 쓰레드를 할당해서 요청을 처리하다가 여러 이유로 인해 처리가 지연되고 있다.
  <img src="./images/다중요청1.png">
- 그때 요청2가 들어온다. 그런데 쓰레드는 현재 하나밖에 없다. 그러면 요청2는 해당 쓰레드를 기다려야한다.
  <img src="./images/다중요청2.png">
- 결국 1번도 처리중이고, 2번도 1번이 쓰레드를 다 사용하고 줘야하는데 1번이 계속 쓰레드를 점유하고 있으니, 수행 자체를 할 수 없다.(이렇게 되면 기다리다가 나중에 둘 다 타임아웃나거나 오류가 발생할 수 있다.)
  <img src="./images/다중요청3.png">
- 이것을 해결하려면, 아주 단순하게 매 요청마다 신규 쓰레드를 생성하면 된다.(요청이 올 때 마다 무조건 쓰레드는 새로 만드는 것.)
  <img src="./images/다중요청4.png">

<br>

**요청하면 쓰레드 생성(하도록 WAS를 설계하면...!)**

- 장점
  - 동시 요청을 처리할 수 있다
  - 리소스(CPU, 메모리)가 허용할 때 까지 처리 가능
  - 하나의 쓰레드가 지연되어도, 나머지 쓰레드는 정상 동작한다.
- 단점
  - 쓰레드는 생성 비용은 매우 비싸다
    - 고객의 요청이 올 때 마다 쓰레드를 생성하면, 응답 속도가 늦어진다
  - 쓰레드는 컨텍스트 스위칭 비용이 발생한다
  - 쓰레드 생성에 제한이 없다 - 고객의 요청이 너무 많이 오면, CPU, 메모리 임계점을 넘어서 서버가 죽을 수 있다.
    이것을 해결하기 위해서, 보통 WAS들은 다 아래와 같은 식으로 구현이 되어있다.(내부에 쓰레드 풀이라는 것을 사용한다.)

## 쓰레드풀

- 참고(1)
  <img src="./images/쓰레드풀-참고1.png">
  - 1번과 2번이 요청이 온다
  - 그러면 쓰레드 풀에게 놀고있는 쓰레드를 달라고 요청한다.(참고, 풀 안에 쓰레드를 미리 만들어둔다. 여기서는 200개 라고 가정)
  - 그렇게 되면 1번과 2번에게 할당했으니, 쓰레드 풀에는 198개가 된다
  - 1번과 2번 요청이 완료되면 해당 쓰레드를 죽이지 않고, 쓰레드 풀에 다시 반납한다.(그러면 다시 쓰레드 풀에는 쓰레드가 200개가 된다.)
- 참고(2)
  <img src="./images/쓰레드풀-참고2.png">
  - 이제 200개의 쓰레드가 이미 실행중이라고 가정해보자
  - 이후에 요청1과 요청2가 들어왔다. 그러면 이 요청들은 쓰레드 풀에 쓰레드가 없기 때문에, (설정에 따라)대기하거나 거절될 수 있다.(거절은 요청을 처리할 수 없으므로 요청을 뱉는다. 대기는 어느정도까지 대기를 받을지 설정할 수 있다.)

<br>

**쓰레드 풀 - 요청마다 쓰레드 생성의 단점 보안**

- 특징
  - 필요한 쓰레드를 쓰레드 풀에 보관하고 관리한다
  - 쓰레드 풀에 생성가능한 쓰레드의 최대치를 관리한다.<br>톰캣은 최대 200개 기본 설정(변경가능)
- 사용
  - 쓰레드가 필요하면 이미 생성되어 있는 쓰레드를 쓰레드 풀에서 꺼내서 사용한다
  - 사용을 종료하면 쓰레드 풀에 해당 쓰레드를 반납한다
  - 최대 쓰레드가 모두 사용중이어서 쓰레드 풀에 쓰레드가 없으면?
    - 기다리는 요청은 거절하거나 특정 숫자만큼만 대기하도록 설정할 수 있다.
- 장점
  - 쓰레드가 미리 생성되어 있으므로, 쓰레드를 생성하고 종료하는 비용(CPU)이 절약되고, 응답 시간이 빠르다
  - 생성 가능한 쓰레드의 최대치가 있으므로 너무 많은 요청이 들어와도 기존 요청은 안전하게 처리할 수 있다.

**쓰레드 풀 - 실무 팁**

- WAS의 주요 튜닝 포인트는 최대 쓰레드(max thread) 수이다.
- 이 값을 너무 낮게 설정하면?
  - 동시 요청이 많으면, 서버 리소스는 여유롭지만, 클라이언트는 금방 응답 지연
- 이 값을 너무 높게 설정하면?
  - 동시 요청이 많으면, CPU, 메모리 리소스 임계점 초과로 서버 다운
- 장애 발생시?
  - 클라우드면 일단 서버부터 늘리고, 이후에 튜닝
  - 클라우드가 아니면 열심히 튜닝

**쓰레드 풀 - 쓰레드 풀의 적정 숫자**

- 적정 숫자는 어떻게 찾나요?
- 애플리케이션 로직의 복잡도, CPU, 메모리, IO 리소스 상황에 따라 모두 댜름
- 성능 테스트를 꼭 해봐야 한다
  - 최대한 실제 서비스와 유사하게 성능 테스트 시도
  - 툴: 아파치 ab, 제이미터, nGrinder

**핵심은 WAS가 멀티 쓰레드를 지원한다는 것.**

- 멀티 쓰레드에 대한 부분은 WAS가 처리
- 개발자가 멀티 쓰레드 관련 코드를 신경쓰지 않아도 됨
- 개발자는 마치 싱글 쓰레드 프로그래밍을 하듯이 편리하게 소스 코드를 개발
- 멀티 쓰레드 환경이므로 싱글톤 객체(서블릿, 스프링 빈)는 주의해서 사용(공유 변수 등)

## HTML, HTTP API, CSR, SSR

이번에는 [HTML, HTTP API, CSR, SSR]에 대해 백엔드 개발자가 숙지해야할 부분을 알아보자.

**백엔드 개발자가 HTTP를 통해서 데이터를 제공할 때 고민해야할 포인트 3가지**

- 정적 리소스를 어떻게 제공할 것인가?
- 동적으로 제공되는 HTML 페이지를 어떻게 제공할 것인가?
- HTTP API를 어떻게 제공할 것인가?

- 정적리소스
  - 고정된 HTML, CSS, JS, 이미지, 영상 등을 제공
  - 주로 웹 브라우저
    - 클라이언트에서 요청시, 웹 서버는 이미 생성된 파일을 제공한다
- HTML 페이지
  - 동적으로 필요한 HTML파일을 생성해서 전달(ex. 주문내역)
  - 웹 브라우저: HTML 해석
    - 웹 브라우저에서 동적 HTML 요청시, WAS에서 주문내역 정보 조회 후, 프로그래밍을 통해 HTML을 동적으로 생성한다. 생성된 HTML을 웹 브라우저에 내려운다.
- HTTP API
  - HTML이 아니라 데이터를 전달
  - 주로 JSON 형식 사용
  - 다양한 시스템에서 호출
  - 데이터만 주고 받음, UI화면이 필요하면, 클라이언트가 별도 처리
  - 웹, 웹 클라이언트, 서버 to 서버

HTTP API - 다양한 시스템 연동

- 주로 JSON 형태로 데이터 통신
- UI클라이언트 접점
  - 앱 클라이언트(아이폰, 안드로이드, PC 앱)
  - 웹 브라우저에서 자바스크립트를 통해 HTTP API호출
  - React, Vue.j같은 웹 클라이언트
- 서버 to 서버
  - 주문 서버 -> 결제 서버
  - 기업간 데이터 통신

**서버사이드 렌더링(SSR), 클라이언트 사이드 렌더링(CSR)**

- SSR - 서버 사이드 렌더링
  - HTML 최종 결과를 서버에서 만들어서 웹 브라우저에 전달
  - 주로 정적인 화면에 사용
  - 관련기술: JSP, 타임리프 -> **백엔드 개발자**
- CSR - 클라이언트 사이드 렌더링
  - HTML결과를 자바스크립트를 사용해 웹 브라우저에서 동적으로 생성해서 적용
  - 주로 동적인 화면에 사용, 웹 환경을 마치 앱처럼 필요한 부분부분 변경할 수 있음
  - 예) 구글 지도, Gmail, 구글 캘린더
  - 관련기술: React, Vue.js -> 웹 프론트엔드 개발자
- 참고
  - React, Vue.js를 CSR + SSR 동시에 지원하는 웹 프레임워크도 있음
  - SSR을 사용하더라도, 자바스크립트를 사용해서 화면 일부를 동적으로 변경 가능

어디까지 알아야 하나요? (벡엔드 개발자 입장에서 UI기술)

- 백엔드 - 서버 사이드 렌더링 기술
  - JSP, 타임리프
  - 화면이 정적이고, 복잡하지 않을 때 사용
  - 백엔드 개발자는 서버 사이드 렌더링 기술 학슬 필수
- 웹 프론트엔드 - 클라이언트 사이드 렌더링 기술
  - React, Vue.js
  - 복잡하고 동적인 UI 사용
  - 웹 프론트엔드 개발자의 전문 분야
- 선택과 집중
  - 백엔드 개발자의 웹 프론트엔드 개술 학습은 옵션
  - 백엔드 개발자는 서버, DB, 인프라 등등 수 많은 백엔드 기술을 공부해야 된다
  - 웹 프론트엔드도 깊이있게 잘 하려면 숙련에 오랜 시간이 필요

## 자바 백엔드 웹 기술 역사

이번에는 자바 백엔드 웹 기술 역사에 대해서 알아보자

자바 웹 기술 역사 - 과거 기술

- 서블릿 - 1997
  - HTML 생성이 어려움
- JSP - 1999
  - HTML생성은 편리하지만, 비즈니스 로직까지 너무 많은 역할 담당
- 서블릿, JSP 조합 MVC패턴 사용
  - 모델, 뷰, 컨트롤러로 역할을 나누어 개발
- MVC프레임워크
  - MVC 패턴 자동화, 복잡한 웹 기술을 편리하게 사용할 수 있는 다양한 기능 지원
  - 스트럿츠, 웹워크, 스프링 MVC(과거 버전)

자바 웹 기술 역사 - 현재 사용 기술

- 애노테이션 기반의 스프링 MVC등장
  - @Controller
  - MVC프레임워크의 춘추 전국 시대 마무리
- 스프링 부트의 등장
  - 스프링 부트는 서버를 내장
  - 과거에는 서버에 WAS를 직접 설치하고, 소스는 War파일을 만들어서 설치한 WAS에 배포
  - 스프링 부트는 빌드 결과(Jar)에 WAS서버 포함 -> 빌드 배포 단순화

자바 웹 기술 역사 - 최신 기술(스프링 웹 기술 분화)

- Web Servlet - Spring MVC
- Web Reactive - Spring WebFlux

자바 웹 기술 역사 - 최신 기술(스프링 웹 플럭스(WebFlux))

- 특징
  - 비동기 넌 블러킹 처리
  - 최소 쓰레드로 최대 성능 - 쓰레드 컨텍스트 스위칭 비용 효율화
  - 함수형 스타일로 개발 - 동시처리 코드 효율화
  - 서블릿 기술 사용X
- 그런데
  - 웹 플럭스는 기술적 난이도 매우 높음
  - 아직은 RDB지원 부족
  - 일반 MVC의 쓰레드 모델도 충분히 빠르다
  - 실무에서 아직 많이 사용하지는 않음

자바 뷰 템플릿 역사 - HTML을 편리하게 생성하는 뷰 기능

- JSP
  - 속도 느림, 기능 부족
- 프리마커(Freemarker), Velocity(벨로시티)
  - 속도 문제 해결, 다양한 기능
- 타임리프(Thymeleaf)
  - 내추럴 템플릿: HTML의 모양은 유지하면서 뷰 템플릿 적용 가능
  - 스프링 MVC와 강력한 기능 통합
  - 최선의 선택, 단 성능은 프리마커, 벨로시티가 더 빠름

## servlet 프로젝트 생성

ServletApplication에 `@ServletComponentScan` 에노테이션을 추가
`@ServletComponentScan`은 서블릿 자동 등록된다.

- 그러면 이제 서블릿 만들어보자.(서블릿 등록)

  - src > main > java > hello > servlet > basic 패키지를 생성하고, 내부에 HelloServlet 클래스를 생성

    ```java
    package hello.servlet.basic;


    @WebServlet(name = "helloServlet", urlPatterns = "/hello")
    public class HelloServlet extends HttpServlet {

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            System.out.println("HelloServlet.service");
        }
    }
    ```

    - 서블릿은 `HttpServlet`이라는 것을 상속받아야 한다.
    - 그리고 `@WebServlet`애노테이션을 넣고, name(서블릿 이름): helloServlet, urlPatterns(URL 매핑): /hello를 입력한다. (HTTP 요청을 통해 매핑된 URL이 호출되면, 서블릿 컨테이너는 해당 서블릿의 다음 메서드를 실행한다. `protected void service(HttpServletRequest request, HttpServletResponse response)`)
    - service 메서드를 재정의한다.
    - 웹 브라우저에서 localhost:8080/hello를 호출해보면 빈 화면이 노출(아무것도 응답하지 않았기 때문에 빈 화면이 노출됨.)되고, 콘솔에는 정상적으로 "HelloServlet.service"이 노출됨을 확인할 수 있다.

- 이번에는 다음과 같이 작성후 실행 해보자

  ```java
  package hello.servlet.basic;

  @WebServlet(name = "helloServlet", urlPatterns = "/hello")
  public class HelloServlet extends HttpServlet {

      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          System.out.println("HelloServlet.service");

          System.out.println("request = " + request);
          System.out.println("response = " + response);

          String username = request.getParameter("username");
          System.out.println("username = " + username);

          response.setContentType("text/plain");
          response.setCharacterEncoding("utf-8");
          response.getWriter().write("hello " + username);

          // localhost:8080/hello?username=sung 으로 접속시
          // HelloServlet.service
          // request = org.apache.catalina.connector.RequestFacade@4b6b5279
          // response = org.apache.catalina.connector.ResponseFacade@391193c7
          // username = sung

          // 웹 페이지에서는
          // hello sung으로 나옴
      }
  }

  ```

  - `request.getParameter`를 사용해서 쿼리 파라미터(쿼리 스트링)정보를 조회할 수 있다.
  - `response`를 사용해서 응답 정보를 입력할 수 있다. (setContentType, setCharacterEncoding를 통해서 헤더 정보를 입력해준다. 그리고 write를 통해 HTTP 응답메시지 바디에 들어갈 정보를 입력한다.)
  - 실행해보면 웹 브라우저에 데이터가 정상적으로 노출되면 콘솔에서도 정상적으로 확인할 수 있다.

> [!TIP] HTTP 요청 메시지 로그로 확인하기
>
> - application.properties파일에 다음과 같이 수정
>
>   ```txt
>   logging.level.org.apache.coyote.http11=debug
>   ```
>
>   - 수정 후 다시 재구동하면 서버가 받은 HTTP요청 메시지를 출력하는 것을 확인할 수 있다.
>   - (참고) 운영서버에 이렇게 모든 요청 정보를 다 남기면 성능저하가 발생할 수 있다. 개발 단계에서만 적용하자

지금까지의 내용을 그림으로 동작 방식을 알아보자

- **서블릿 컨테이너 동작 방식 설명**
  - 내장 톰캣 서버 생성
    <img src="./images/서블릿컨테이너동작방식설명1.png">
    - 스프링 부트 프로젝트를 생성하여 실행해보았다. 그러면 스프링 부트가 실행하면서 내장 톰캣 서버를 띄워준다. (톰캣 서버는 내부에 서블릿 컨테이너 기능을 가지고 있다.)
    - 그러면서 서블릿 컨테이너를 통해서 서블릿을 다 생성해준다. (서블릿 컨테이너 안에 helloServlet이 생성됨)
  - HTTP요청, HTTP응답 메시지
    <img src="./images/서블릿컨테이너동작방식설명2.png">
    - 웹 브라우저를 통해 /hello?username=world를 입력하여 요청하면, 웹 브라우저는 이미지 왼쪽과 같이 HTTP 요청 메시지를 만들어서 서버에 던져준다.
  - 웹 애플리케이션 서버의 요청 응답 구조
    <img src="./images/서블릿컨테이너동작방식설명3.png">
    - 그러면 서버는 request, response 객체를 만들어서 (싱글톤으로 등록된) helloServlet의 service 메서드를 호출한다. 그러면서 request, response를 넘겨준다.
    - service 메서드내에서 필요한 로직들을 작성한다. (강의에서는 response에 헤더 정보, 메시지 정보 입력하였다.)
    - 이후 해당 메서드가 종료되고 나가면서 WAS 서버가 response 정보를 가지고 HTTP 응답 메시지 정보를 만들어서 반환한다.
    - (참고) HTTP 응답에서 Content-Length와 같은 부가적인 정보들은 웹 애플리케이션 서버가 자동으로 생성해준다.

### HttpServletRequest - 개요

HttpServletRequest - 개요

- **HttpServletRequest 역할**
  - HTTP 요청 메시지를 개발자가 직접 파싱해서 사용해도 되지만, 매우 불편할 것이다. 서블릿은 개발자가 HTTP요청 메시지를 편리하게 사용할 수 있도록 개발자 대신에 HTTP요청 메시지를 파싱한다. 그리고 그 결과를 `HttpServletRequest`객체에 담아서 제공한다. (그래서 우리가 이를 편리하게 사용할 수 있는 것)
  - HttpServletRequest를 사용하면 다음과 같은 Http 요청 메시지를 편리하게 조회할 수 있다.
    - **HTTP 요청 메시지**
      <img src="./images/HttpServletRequest-개요.png">
      - START LINE
        - HTTP 메소드
        - URL
        - 쿼리 스트링
        - 스키마, 프로토콜
      - 헤더
        - 헤더 조회
      - 바디
        - form 파라미터 형식 조회
        - message body 데이터 직접 조회
  - HttpServletRequest 객체는 추가로 여러가지 부가기능도 함께 제공한다. - **임시 저장소 기능** - 해당 HTTP 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 - 저장: `request.setAttribute(name, value)` - 조회: `request.getAttribute(name)` - 세션 관리 기능 - `request.getSession(create: true)`
    > [!IMPORTANT]
    > HttpServletRequest, HttpServletResponse를 사용할 때 **가장 중요한 점은 이 객체들이 HTTP 요청 메시지, HTTP 응답 메시지를 편리하게 사용하도록 도와주는 객체**라는 점이다. 따라서 이 기능에 대해서 깊이있는 이해를 하려면 HTTP 스펙이 제공하는 요청, 응답메시지 자체를 이해햐야 한다.

### HttpServletRequest - 기본 사용법

HttpServletRequest가 제공하는 기본 기능들을 알아보자.

- src > main > java > hello > servlet > basic > request 패키지 생성 후, RequestHeaderServlet 클래스를 생성

  ```java
  package hello.servlet.basic.request;

  import jakarta.servlet.ServletException;
  import jakarta.servlet.annotation.WebServlet;
  import jakarta.servlet.http.Cookie;
  import jakarta.servlet.http.HttpServlet;
  import jakarta.servlet.http.HttpServletRequest;
  import jakarta.servlet.http.HttpServletResponse;

  import java.io.IOException;
  import java.util.Enumeration;

  @WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
  public class RequestHeaderServlet extends HttpServlet { // 헤더 정보 출력을 위한 서블릿

      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          printStar(request);
          printHeader(request);
          printHeaderUtils(request);
          printEtc(request);
      }

      private void printStar(HttpServletRequest request) {
          System.out.println("--- REQUEST-LINE - start ---");
          System.out.println("request.getMethod() = " + request.getMethod()); // GET
          System.out.println("request.getProtocol() = " + request.getProtocol()); // HTTP/1.1
          System.out.println("request.getScheme() = " + request.getScheme()); // http
          // 전체 URL: https://localhost:8080/request-header
          System.out.println("request.getRequestURL() = " + request.getRequestURL());
          // 경로: /request-header
          System.out.println("request.getRequestURI() = " + request.getRequestURI());
          // 쿼리 스트링: username=h1
          System.out.println("request.getQueryString() = " + request.getQueryString());
          // https 사용 유무
          System.out.println("request.isSecure() = " + request.isSecure());
          System.out.println("--- REQUEST-LINE - end ---");
          System.out.println();
      }

      private void printHeader(HttpServletRequest request) {
          System.out.println("--- Headers - start ---");

          Enumeration<String> headerNames = request.getHeaderNames();
          while (headerNames.hasMoreElements()) {
              String headerName = headerNames.nextElement();
              System.out.println(headerName + ": " + headerName);
          }

          System.out.println("--- Headers - end ---");
          System.out.println();
      }

      private void printHeaderUtils(HttpServletRequest request) {
          System.out.println("--- Header 편의 조회 start ---");
          System.out.println("[Host 편의 조회]");
          System.out.println("request.getServerName() = " + request.getServerName());
          System.out.println("request.getServerPort() = " + request.getServerPort());
          System.out.println();

          System.out.println("[Accept-Language 편의 조회]");
          request.getLocales().asIterator()
                  .forEachRemaining(locale -> System.out.println("locale = " + locale));
          System.out.println("request.get = " + request.getLocale());
          System.out.println();

          System.out.println("[cookie 편의 조회]");
          if (request.getCookies() != null) {
              for (Cookie cookie : request.getCookies()) {
                  System.out.println(cookie.getName() + ": " + cookie.getValue());
              }
          }
          System.out.println();

          System.out.println("[Content 편의 조회]");
          System.out.println("request.getContentType() = " + request.getContentType());
          System.out.println("request.getContentLength = " + request.getContentLength());
          System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());

          System.out.println("--- Header 편의 조회 end ---");
          System.out.println();
      }

      private void printEtc(HttpServletRequest request) {
          System.out.println("--- 기타 조회 start ---");
          System.out.println("[Remote 정보");
          System.out.println("request.getRemoteHost() = " + request.getRemoteHost());
          System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr());
          System.out.println("request.getRemotePort() = " + request.getRemotePort());
          System.out.println();
          System.out.println("[Local 정보]");
          System.out.println("request.getLocalName() = " + request.getLocalName());
          System.out.println("request.getLocalAddr() = " + request.getLocalAddr());
          System.out.println("request.getLocalPort() = " + request.getLocalPort());
          System.out.println("--- 기타 조회 end---");

      }
  }

  ```

  ### HTTP 요청 데이터 - 개요

  이번에는 HTTP 요청 데이터에 대해서 알아보자
  (HTTP 요청 메시지를 통해 클라이언트에서 서버로 데이터를 전달하는 방법을 알아보자)

  **주로 다음 3가지 방법을 사용한다**

  - GET - 쿼리 파라미어
    - /url\*?username=hello&age=20
    - 메시지 바디 없이, URL의 쿼리 파라미터에 데이터를 포함해서 전달
    - 예) 검색, 필터 페이징등에서 많이 사용하는 방식
  - POST - HHTML Form
    - content-type: application/x-www-form-urlencoded
    - 메시지 바디에 쿼리 파라미터 형식으로 전달 (username=hello&age=20)
    - 예) 회원 가입, 상품 주문, HTML Form 사용
  - HTTP message body에 데이터를 직접 담아서 요청
    - HTTP API에서 주로 사용 JSON, XML, TEXT
    - 데이터 형식은 주로 JSON 사용
    - POST, PUT, PATCH

### HTTP 요청 데이터 - GET 쿼리 파라미터

이번에는 HTTP요청 데이터 중에, 가장 먼저 GET방식의 쿼리 파라미터로 전송되는 데이터를 어떻게 사용할 수 있는지 알아보자

다음 데이터를 클라이언트에서 서버로 전송해보자

**전달데이터**

- username=hello
- age=20

메시지 바디 없이, URL의 쿼리 파라미터를 사용해서 데이터를 전달하자
예) 검색, 필터, 페이징등에서 많이 사용하는 방식.

쿼리 파라미터는 URL에 다음과 같이 ? 를 시작으로 보낼 수 있다. 추가 파라미터는 &로 구분하면 된다
예) `http://localhost:8080/request-param?username=hello&age=20`

이렇게 전달된 데이터를 서버에서는 HttpServletRequest가 제공하는 다음 메서드를 통해 쿼리 파라미터를 편리하게 조회할 수 있다.

**쿼리 파라밈터 조회 메서드**

- 단일 파라미터 조회
  - `String username = request.getParameter("username");`
- 파라미터 이름을 모두 조회
  - `Enumeration parameterNames = request.getParameterNames();`
- 파라미터를 Map으로 조회
  - `Map parameterMap = request.getParameterMap();`
- 복수 파라미터 조회
  - `String[] usernames = request.getParameterValues("username");`

예시를 확인해보자

- src > main > java > hello > servlet > basic > request 패키지 아래 RequestParamServlet 클래스를 생성

  ```java
  package hello.servlet.basic.request;

  @WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
  public class RequestParamServlet extends HttpServlet {

      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

          System.out.println("[전체 파라미터 조회] - start");

          /*
          Enumeration<String> parameterNames = request.getParameterNames();
          while(parameterNames.hasMoreElements()) {
              String paramName = parameterNames.nextElement();
              System.out.println(paramName + "=" + request.getParameter(paramName));
          }
          */

          request.getParameterNames().asIterator()
                  .forEachRemaining(paramName ->
                          System.out.println(paramName + " = " +
                                  request.getParameter(paramName)));

          System.out.println("[전체 파라미터 조회] - end");

          System.out.println("[단일 파라미터 조회] - start");
          String username = request.getParameter("username");
          String age = request.getParameter("age");
          System.out.println("username = " + username);
          System.out.println("age = " + age);
          System.out.println("[단일 파라미터 조회] - end");


          // 파라미터 이름이 중복해서 넘어갈 수 있다. 이런 경우 내부 우선순위에 의해서 먼저 잡히는것이 나오게 된다.
          System.out.println("[이름이 같은 복수 파라미터 조회] - start");
          String[] usernames = request.getParameterValues("username");
          for(String name : usernames) System.out.println("username = " + name);
          System.out.println("[이름이 같은 복수 파라미터 조회] - end");
          response.getWriter().write("ok");
      }
  }
  ```

> [!TIP] 복수 파라미터에서 단일 파라미터 조회
> `username=hello&username=kim` 과 같이 파라미터 이름은 하나인테, 값이 중복되면 어떻게 될까? `request.getParameter()`는 하나의 파라미터 이름에 대해서 단 하나의 값만 있을 때 사용해야 한다. <br>지금처럼 중복일 때는 `request.getParameterValuest()`를 사용해야 한다.<br>참고로 이렇게 중복일 때 `request.getParameter()`를 사용하면 `request.getParameterValues()`의 첫번째 값을 반환한다.

### HTTP 요청 데이터 - POST HTML Form

이번에는 HTML의 Form을 사용해서 클라이언트에서 서버로 데이터를 전송해보자
(주로 회원 가입, 상품 주문 등에서 사용하는 방식이다.)

**특징**

- content-type: `application/x-www-form-urlencoded`
- 메시지 바디에 쿼리 파라미터 형식으로 데이터를 전달한다.

예제로 확인하자

- src > main > webapp > basic 폴더 생성 후 내부에 hello-form.html 을 생성

  ```html
  <!DOCTYPE html>
  <html>
    <head>
      <meta charset="UTF-8" />
      <title>Title</title>
    </head>
    <body>
      <form action="/request-param" method="post">
        username: <input type="text" name="username" /> age: <input type="text" name="age" />
        <button type="submit">전송</button>
      </form>
    </body>
  </html>
  ```

> [!NOTE] 정리
>
> - POSt의 HTML Form을 전송하면 웹 브라우저는 다음 형식으로 HTTP 메시지를 만든다
>   - 요청 URL: `http://localhost:8080/request-param`
>   - content-type: `application/x-www-form-urlencoded`
>   - message body: `username=hello&age=20`
> - application/x-www-form-urlencoded 형식은 앞서 GET에서 살펴본 쿼리 파라미터 형식과 같다. 따라서 서버에서 요청 데이터 조회시 쿼리 파라미터 조회 메서드를 그대로 사용하면 된다. (클라이언트(웹 브라우저) 입장에서는 두 방식에 차이가 있지만, 서버 입장에서는 둘의 형식이 동일하므로, request.getParameter() 로 편리하게 구분없이 조회할 수 있다.) **정리하면 request.getParameter() 는 GET URL 쿼리 파라미터 형식도 지원하고, POST HTML Form 형식도 둘 다 지원한다.**

> [!TIP] 참고
>
> - **content-type은 HTTP메시지 바디의 데이터 형식으로 지정한다**
> - **GET URL 쿼리 파라미터 형식**으로 클라이언트에서 서버로 데이터를 전달할 때는 HTTP 메시지 바디를 사용하지 않기 때문에 **content-type**이 없다.
> - **POST HTML Form 형식**으로 데이터 전달하면 HTTP 메시지 바디에 해당 데이터를 포함해서 보내기 때문에 바디에 포함된 데이터가 어떤 형식인지 content-type을 꼭 지정해야 한다. 이렇게 폼으로 데이터를 전송하는 형식을 application/x-www.form-urlencoded라 한다.

postman 테스트

- x-www-form-urlencoded
  - Key -> username
  - Value -> kim

### HTTP요청 데이터 - API 메시지 바디 - 단순 테스트

- HTTP message body에 데이터를 직접 담아서 서버에 요청
  - HTTP API에서 주로 사용, JSON ,XML, TEXT
  - 데이터 형식은 주로 JSON사용
  - POST, PUT, PATCH

먼저 가장 단순한 텍스트 메시지를 HTTP 메시지 바디에 담아서 전송하고, 읽어보자.<br>(참고. HTTP 메시지 바디의 데이터를 InputStream을 사용해서 직접 읽을 수 있다.)

- src > main > java > hello > servlet > basic > request > RequestBodyStringServlet 클래스를 생성하고 postman으로 실행해보자.

  ```java
  package hello.servlet.basic.request;

  @WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
  public class RequestBodyStringServlet extends HttpServlet {

      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          ServletInputStream inputStream = request.getInputStream();
          String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

          System.out.println("messageBody = " + messageBody);

          response.getWriter().write("ok");

          // messageBody = hello!
      }
  }
  ```

- postman에서 `http://localhost:8080/request-body-string`으로 post로 raw text `hello!`전송
  - HTTP 메시지 바디에 넣은 데이터가 그대로 노출됨을 확인할 수 있다
  - (참고) 문자 전송
  - POST `http://localhost:8080/request-body-string`
  - content-type: text/plain
  - message body: hello!
  - 결과: messageBody = hello!

> [!TIP] 참고
> inputStream은 byte코드를 반환한다. byte코드를 우리가 읽을 수 있는 문자(String)로 보려면 문자표 (Charset)를 지정해주어야 한다. 여기서는 UTF_8 Charset을 지정해줌

### HTTP 요청 데이터 - API 메시지 바디 - JSON

이번에는 HTTP API에서 주로 사용하는 JSON형식으로 데이터를 전달해보자

JSON형식 전송

- POST `http://localhost:8080/request-body-json`
- content-type: application/json
- message body: `{"username": "hello", "age": 20}`
- 결과: `messageBody = {"username": "hello", "age": 20}`

JSON 형식 파싱 추가<br>
(보통 JSON을 그대로 쓰지는 않는다. 객체로 바꿔서 사용한다. 그래서 JSON 형식으로 파싱할 수 있게 객체를 하나 생성하자.)

- src > main > java > hello > servlet > basic 디렉토리 아래 HelloData 클래스를 생성하자.

  ```java
  package hello.servlet.basic;

  import lombok.Getter;
  import lombok.Setter;

  @Getter
  @Setter
  public class HelloData {
      private String username;
      private  int age;
  }

  ```

  - src > main > java > hello > servlet > basic > request 내부에 RequestBodyJsonServlet 클래스를 생성

  ```java
  package hello.servlet.basic.request;

  @WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
  public class RequestBodyJsonServlet extends HttpServlet {

      private ObjectMapper objectMapper = new ObjectMapper();

      @Override
      protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          ServletInputStream inputStream = request.getInputStream();
          String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
          System.out.println("messageBody = " + messageBody);

          HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
          System.out.println("helloData.getUsername() = " + helloData.getUsername());
          System.out.println("helloData.getAge() = " + helloData.getAge());

          response.getWriter().write("ok");
      }
  }

  ```

> [!TIP] 참고
>
> - JSON 결과를 파싱해서 사용할 수 있는 자바 객체로 변화하려면 Jackson, Gson 같은 JSON변환 라이브러리를 추가해서 사용해야한다. 스프링 부트로 spring MVC를 선택하면 기본으로 Jackson 라이브러리(`ObjectMapper`)를 함께 제공한다.
> - HTML form 데이터도 메시지 바디를 통해 전송되므로 inputStream을 통해 직접 읽을 수 있다. 하지만 편리한 파리미터 조회 기능( `request.getParameter(...)` )을 이미 제공하기 때문에 파라미터 조회 기능을 사용하면 된다.

### HttpServletResponse - 기본 사용법

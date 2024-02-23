# 스프링MVC-2편

## 프로젝트 생성

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

**타임리프 특징**

- 서버 사이드 HTML 렌더링(SSR)
- 네츄럴 템플릿
- 스프링 통합 지원

**서버 사이드 HTML 렌더링 (SSR)**

- 타임리프는 백엔드 서버에서 HTML을 동적으로 렌더링하는 용도로 사용된다.

**내츄럴 템플릿**

- 타임리프는 순수 HTML을 최대한 유지하는 특징이 있다. 타임리프로 작성한 파일은 HTML을 유지하기 때문에 웹 브라우저에서 파일을 열어서 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면 동적으로 변경된 결과를 확인할 수 있다.
- JSP를 포함한 다른 뷰 템플릿들은 해당 파일을 열면, 예를 들어서 JPS파일 자체를 그대로 웹 브라우저에서 열어보면 JSP소스코드와 HTML이 뒤죽박죽 섞여서 웹 브라우저에서 정상적인 HTML결과를 확인할 수 없다. 오직 서버를 통해서 JSP가 렌더링 되고 HTML응답 결과를 받아야 화면을 확인할 수 있다.
